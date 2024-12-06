package com.scumm.pancake;

import com.alibaba.fastjson.JSONObject;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.DynamicArray;
import org.web3j.abi.datatypes.DynamicBytes;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint160;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.abi.datatypes.generated.Uint32;
import org.web3j.crypto.Credentials;
import org.web3j.model.*;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.RemoteFunctionCall;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthCall;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.gas.StaticGasProvider;
import org.web3j.utils.Numeric;

import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * 1.pancake v3每个币对可能有多个池子，对应几个fee {100, 500, 3000, 10000}，提高了资金利用率
 * 2.调用router.multicall： 1.兑换代币 2.将WETH或者WBNB转换成主网币
 */

public class MainV3Entry {


    //零地址：主网币
    private static String ZERO_ADDRESS;
    //WBNB：主网币兑换时会替换成WBNB的地址
    private static String WBNB_ADDRESS;

    //BSC网络地址
    private static String BSC_NET;

    private static String PANCAKE_ROUTER;
    private static String PANCAKE_FACTORY;
    private static String PANCAKE_QUOTER;

    //获取最优路径的URI（swap wallet的GO服务api），上线后使用真实的域名
    private static String GET_BEST_PATH_URL;

    private static okhttp3.OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .connectTimeout(120, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(120, TimeUnit.SECONDS)
            .build();

    //uniswapv3每个币对最多有4个池子，每个池子对应的手续费不用
    private static final int[] fees = new int[]{100, 500, 3000, 10000};


    static {
        try {
            File file = new File("src/main/resources/main.properties");
            InputStream in = new FileInputStream(file);
            Properties props = new Properties();
            InputStreamReader inputStreamReader = new InputStreamReader(in, "UTF-8");
            props.load(inputStreamReader);

            ZERO_ADDRESS = props.getProperty("ZERO_ADDRESS");
            WBNB_ADDRESS = props.getProperty("WBNB_ADDRESS");

            BSC_NET = props.getProperty("BSC_NET");

            PANCAKE_ROUTER = props.getProperty("PANCAKE_ROUTER_V3");
            PANCAKE_FACTORY = props.getProperty("PANCAKE_FACTORY_V3");
            PANCAKE_QUOTER = props.getProperty("PANCAKE_QUOTER_V3");

            GET_BEST_PATH_URL = props.getProperty("GET_BEST_PATH_URL");

        } catch (Exception e) {
            System.out.println("init error");
        }


    }

    public static void main(String[] args) throws Exception {
        String fromToken = args[0];
        String toToken = args[1];
        BigDecimal amountInDec = new BigDecimal(args[2]);
        BigInteger impact = new BigInteger(args[3]); //滑点，10000基数， 1%滑点参数传 100
        String privateKey = args[4];

        if (fromToken.equalsIgnoreCase(toToken)) {
            throw new Exception("fromToken can't equals toToken");
        }

        Credentials credentials = Credentials.create(privateKey);
        String own = credentials.getAddress();
        System.out.println("address=" + own);

        Web3j web3j = Web3j.build(new org.web3j.protocol.http.HttpService(BSC_NET, new OkHttpClient.Builder().build(), false));


        //链上获取推荐的gas price
        BigInteger gasPrice = getGasPrice(web3j);

        ERC20Token erc20Token = ERC20Token.load(
                ZERO_ADDRESS.equalsIgnoreCase(fromToken) ? WBNB_ADDRESS : fromToken,
                web3j,
                credentials,
                new StaticGasProvider(gasPrice, BigInteger.valueOf(500000L)));
        BigInteger decimal = erc20Token.decimals().send();
        BigDecimal maxDecimal = new BigDecimal("10").pow(decimal.intValue());
        BigDecimal amountInDecimal = amountInDec.multiply(maxDecimal);
        BigInteger amountIn = amountInDecimal.toBigInteger();

        //PancakeSwapFactory（生成池子的类，可以查询是否存在某个币对）
        UniswapV3Factory factory = UniswapV3Factory.load(PANCAKE_FACTORY, web3j, credentials, new StaticGasProvider(gasPrice, BigInteger.valueOf(500000L)));

        //PancakeRouter swap入口
        UniswapV3Router router = UniswapV3Router.load(PANCAKE_ROUTER, web3j, credentials, new StaticGasProvider(gasPrice, BigInteger.valueOf(500000L)));

        //一个币对可能有多个池子（fee不同），选择最优兑换池子
        int bestFee = fees[0];
        BigInteger amountOut = BigInteger.ZERO;
        String token0 = ZERO_ADDRESS.equalsIgnoreCase(fromToken) ? WBNB_ADDRESS : fromToken;
        String token1 = ZERO_ADDRESS.equalsIgnoreCase(toToken) ? WBNB_ADDRESS : toToken;
        for (int fee : fees) {
            String pool = factory.getPool(token0, token1, BigInteger.valueOf(fee)).send();
            if (StringUtils.isNotEmpty(pool) && !ZERO_ADDRESS.equalsIgnoreCase(pool)) {
//                BigInteger out = staticCallQuoteExactInputSingle(web3j, PANCAKE_QUOTER, token0, token1, BigInteger.valueOf(100), amountIn);
                byte[] path = encodePath(token0, Long.valueOf(fee), token1);
                BigInteger out = staticCallQuoteExactInput(web3j, PANCAKE_QUOTER, path, amountIn);
                if (out == null) {
                    continue;
                }
                if (amountOut.compareTo(out) < 0) {
                    amountOut = out;
                    bestFee = fee;
                }
            }
        }

        //减去滑点可兑换的量
        BigInteger amountOutMin = amountOut.subtract(amountOut.multiply(impact).divide(BigInteger.valueOf(10000L)));

        BigInteger weiAmount = BigInteger.ZERO;
        if (!ZERO_ADDRESS.equalsIgnoreCase(fromToken)) {
            approve(erc20Token, own, PANCAKE_ROUTER); //非主网币兑换主网币，先检查授权
        } else {
            weiAmount = amountIn; //主网币兑换，payable付款金额
        }

        TransactionReceipt tr;
        if (ZERO_ADDRESS.equalsIgnoreCase(toToken)) {
            // encode exactInput
            RemoteFunctionCall<TransactionReceipt> exactInputCall = router.exactInput(
                    new UniswapV3Router.ExactInputParams(
                            encodePath(token0, Long.valueOf(bestFee), token1),
                            "0x0000000000000000000000000000000000000002",
                            amountIn,
                            amountOutMin),
                    weiAmount);
            byte[] encodedInputCall = Numeric.hexStringToByteArray(exactInputCall.encodeFunctionCall()); // 获取编码后的字节数据

            //encode unwrapWETH
            RemoteFunctionCall<TransactionReceipt> unwrapWETH = router.unwrapWETH9(amountOutMin, own, weiAmount);
            byte[] encodedUnwrapWETH = Numeric.hexStringToByteArray(unwrapWETH.encodeFunctionCall());
            // 先兑换成WETH，然后转换成ETH
            tr = router.multicall(Arrays.asList(encodedInputCall, encodedUnwrapWETH), BigInteger.ZERO).send();
        } else { // 其他
            // encode exactInput
            RemoteFunctionCall<TransactionReceipt> exactInputCall = router.exactInput(
                    new UniswapV3Router.ExactInputParams(
                            encodePath(token0, Long.valueOf(bestFee), token1),
                            own,
                            amountIn,
                            amountOutMin),
                    weiAmount);
            byte[] encodedInputCall = Numeric.hexStringToByteArray(exactInputCall.encodeFunctionCall()); // 获取编码后的字节数据
            tr = router.multicall(Arrays.asList(encodedInputCall), weiAmount).send();
        }

        System.out.println("tr hash=" + tr.getTransactionHash());

        System.out.println("END");
    }

    public static BigInteger staticCallQuoteExactInput(Web3j web3j, String contractAddress,
                                                       byte[] path, BigInteger amountIn) throws IOException {
        System.out.println("staticCallQuoteExactInput contractAddress=" + contractAddress + ", path="
                + Numeric.toHexString(path) + ", amountIn=" + amountIn);

        String funcName = "quoteExactInput";

        Function function = new Function(
                funcName,
                Arrays.asList(
                        new DynamicBytes(path),  // path as bytes
                        new Uint256(amountIn)     // amountIn
                ),
                Arrays.asList(
                        new org.web3j.abi.TypeReference<Uint256>() {
                        },   // amountOut
                        new org.web3j.abi.TypeReference<DynamicArray<Uint160>>() {
                        },  // sqrtPriceX96AfterList
                        new org.web3j.abi.TypeReference<DynamicArray<Uint32>>() {
                        },    // initializedTicksCrossedList
                        new org.web3j.abi.TypeReference<Uint256>() {
                        }   // gasEstimate
                )
        );

        // Encode function data
        String data = FunctionEncoder.encode(function);
        Transaction transaction = Transaction.createEthCallTransaction(null, contractAddress, data);

        // Perform the ethCall (static call)
        EthCall ethCall = web3j.ethCall(transaction, org.web3j.protocol.core.DefaultBlockParameterName.LATEST).send();

        if (ethCall.hasError()) {
            System.err.println("Error in EthCall: " + ethCall.getError().getMessage());
            return null;
        }

        // Decode the result
        List<org.web3j.abi.datatypes.Type> results = FunctionReturnDecoder.decode(ethCall.getValue(), function.getOutputParameters());

        // Parse the results
        return (BigInteger) results.get(0).getValue();


    }

    // 编码 path 数据，类似于 Solidity 中的 abi.encodePacked()
    public static byte[] encodePath(Object... path) {
        List<byte[]> packedData = new ArrayList<>();

        for (Object p : path) {
            if (p instanceof String) {
                // 地址类型 (String), 转换为 20 字节的字节数组
                String address = (String) p;
                packedData.add(Numeric.hexStringToByteArray(address));
            } else if (p instanceof Long) {
                // uint64 类型，转换为 8 字节数组
                packedData.add(toUint24Bytes((Long) p));
            } else if (p instanceof byte[]) {
                // 如果是字节数组，直接添加
                packedData.add((byte[]) p);
            } else {
                // 处理不支持的类型
                throw new IllegalArgumentException("Unsupported type: " + p.getClass());
            }
        }

        // 合并所有字节数组
        int totalLength = packedData.stream().mapToInt(arr -> arr.length).sum();
        byte[] result = new byte[totalLength];
        int index = 0;

        for (byte[] data : packedData) {
            System.arraycopy(data, 0, result, index, data.length);
            index += data.length;
        }

        return result;
    }

    // 将 uint64 转换为 3 字节（Uint24），这与 Go 中的 PutUint24 类似
    private static byte[] toUint24Bytes(long value) {
        if (value < 0 || value > 0xFFFFFF) {
            throw new IllegalArgumentException("Value out of bounds for uint24");
        }
        return new byte[]{
                (byte) (value >> 16),
                (byte) (value >> 8),
                (byte) value
        };
    }

    private static BigInteger staticCallQuoteExactInputSingle(Web3j web3j, String contractAddress, String token0, String token1, BigInteger fee, BigInteger amountIn) {
        System.out.println("staticCallQuoteExactInputSingle contractAddress=" + contractAddress + ",token0=" + token0 + ",token1=" + token1 + ",fee=" + fee + ",amountIn=" + amountIn);
        String funcName = "quoteExactInputSingle";
        BigInteger out = BigInteger.ZERO;

        // 创建合约函数对象，设置 sqrtPriceLimitX96 为有效值
        Function function = new Function(
                funcName,
                Arrays.asList(
                        new org.web3j.abi.datatypes.Address(token0),
                        new org.web3j.abi.datatypes.Address(token1),
                        new org.web3j.abi.datatypes.generated.Uint24(fee),
                        new org.web3j.abi.datatypes.generated.Uint256(amountIn),
                        new org.web3j.abi.datatypes.generated.Uint160(new BigInteger("0"))  // 设置 sqrtPriceLimitX96 为合理的默认值
                ),
                Arrays.asList(new TypeReference<Uint256>() {
                })
        );

        // 使用 Web3j 进行静态调用
        String data = FunctionEncoder.encode(function);
        Transaction transaction = Transaction.createEthCallTransaction("0x130870b453fa8feb171eba766a4c5b4092c9fe91", contractAddress, data);

        EthCall ethCall;
        try {
            ethCall = web3j.ethCall(transaction, DefaultBlockParameterName.LATEST).sendAsync().get();
            System.out.println("ethCall:" + JSONObject.toJSON(ethCall));
            List<Type> results = FunctionReturnDecoder.decode(ethCall.getValue(), function.getOutputParameters());
            out = (BigInteger) results.get(0).getValue();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return out;
    }


    private static boolean approve(ERC20Token erc20Token, String own, String spender) throws Exception {
        BigInteger allowance = erc20Token.allowance(own, spender).send();
        if (allowance.compareTo(BigInteger.ZERO) > 0) {
            return true;
        } else {
            erc20Token.approve(spender, new BigInteger("2").pow(255)).send(); //2的256次方
            return true;
        }
    }


    //链上推荐的gas price，快速成交可以乘2
    public static BigInteger getGasPrice(Web3j web3j) throws IOException {
        return web3j.ethGasPrice().send().getGasPrice();
    }
}
