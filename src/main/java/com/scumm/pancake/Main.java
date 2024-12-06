package com.scumm.pancake;

import com.alibaba.fastjson.JSONObject;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.web3j.crypto.Credentials;
import org.web3j.model.ERC20Token;
import org.web3j.model.PancakeRouter;
import org.web3j.model.PancakeSwapFactory;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.gas.StaticGasProvider;

import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

public class Main {

    //零地址：主网币
    private static String ZERO_ADDRESS;
    //WBNB：主网币兑换时会替换成WBNB的地址
    private static String WBNB_ADDRESS;

    //BSC网络地址
    private static String BSC_NET;

    private static String PANCAKE_ROUTER;
    private static String PANCAKE_FACTORY;

    //获取最优路径的URI（swap wallet的GO服务api），上线后使用真实的域名
    private static String GET_BEST_PATH_URL;

    private static okhttp3.OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .connectTimeout(120, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(120, TimeUnit.SECONDS)
            .build();

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

            PANCAKE_ROUTER = props.getProperty("PANCAKE_ROUTER");
            PANCAKE_FACTORY = props.getProperty("PANCAKE_FACTORY");

            GET_BEST_PATH_URL = props.getProperty("GET_BEST_PATH_URL");

        } catch (Exception e) {
            System.out.println("init error");
        }


    }


    public static void main(String[] args) throws Exception {

        String fromToken = args[0];
        String toToken = args[1];
//        BigInteger amountIn = new BigInteger(args[2]);
        BigDecimal amountInDec = new BigDecimal(args[2]);
        BigInteger impact = new BigInteger(args[3]); //滑点，10000基数， 1%滑点参数传 100
        String privateKey = args[4];


        if (fromToken.equalsIgnoreCase(toToken)) {
            throw new Exception("fromToken can't equals toToken");
        }

        Web3j web3j = Web3j.build(new org.web3j.protocol.http.HttpService(BSC_NET, new OkHttpClient.Builder().build(), false));

        Credentials credentials = Credentials.create(privateKey);
        String own = credentials.getAddress();

        //链上获取推荐的gas price
        BigInteger gasPrice = getGasPrice(web3j);

        //from token 对应的ERC20，获取精度、approve等（这里使用FLOKI的合约，只要是ERC20的就行）
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
        PancakeSwapFactory factory = PancakeSwapFactory.load(
                PANCAKE_FACTORY,
                web3j,
                credentials,
                new StaticGasProvider(gasPrice, BigInteger.valueOf(500000L)));

        //如果是主网币兑换，需要转成对应的 WBNB 地址
        List<String> path = getBestPath(
                ZERO_ADDRESS.equalsIgnoreCase(fromToken) ? WBNB_ADDRESS : fromToken,
                ZERO_ADDRESS.equalsIgnoreCase(toToken) ? WBNB_ADDRESS : toToken,
                amountIn.toString(),
                factory);


        //PancakeRouter swap入口
        PancakeRouter router = PancakeRouter.load(
                PANCAKE_ROUTER,
                web3j,
                credentials,
                new StaticGasProvider(gasPrice, BigInteger.valueOf(500000L))
        );

        //正常可以兑换的量
        BigInteger amountOut = getMaxOut(amountIn, path, router);
        //减去滑点可兑换的量
        BigInteger amountOutMin = amountOut.subtract(amountOut.multiply(impact).divide(BigInteger.valueOf(10000L)));

        //超过1H的交易会自动失败
        BigInteger deadline = BigInteger.valueOf(System.currentTimeMillis() / 1000 + 3600L);

        //ERC20的代币合约，在用非主网币兑换时需要先进行合约授权
        //非主网币调用PancakeRouter时，需要允许PancakeRouter操作 fromToken 合约的资产

        TransactionReceipt tr;
        if (ZERO_ADDRESS.equalsIgnoreCase(fromToken)) {
            tr = swapExactETHForTokens(amountOutMin, path, own, deadline, amountIn, router);
        } else if (ZERO_ADDRESS.equalsIgnoreCase(toToken)) {
            approve(erc20Token, own, PANCAKE_ROUTER);
            tr = swapExactTokensForETH(amountIn, amountOutMin, path, own, deadline, router);
        } else {
            //尽量避免使用 非主网token 兑换 非主网token
            approve(erc20Token, own, PANCAKE_ROUTER);
            tr = swapExactTokensForTokens(amountIn, amountOutMin, path, own, deadline, router);
        }

        System.out.println("tr hash=" + tr.getTransactionHash());

        System.out.println("END");
    }

    //主网币兑换其他token
    private static TransactionReceipt swapExactETHForTokens(BigInteger amountOutMin, List<String> path, String to, BigInteger deadline, BigInteger amountIn, PancakeRouter router) throws Exception {
        return router.swapExactETHForTokens(amountOutMin, path, to, deadline, amountIn).send();
    }

    //其他token兑换主网币
    private static TransactionReceipt swapExactTokensForETH(BigInteger amountIn, BigInteger amountOutMin, List<String> path, String to, BigInteger deadline, PancakeRouter router) throws Exception {
        return router.swapExactTokensForETH(amountIn, amountOutMin, path, to, deadline).send();
    }

    //非主网币兑换
    private static TransactionReceipt swapExactTokensForTokens(BigInteger amountIn, BigInteger amountOutMin, List<String> path, String to, BigInteger deadline, PancakeRouter router) throws Exception {
        return router.swapExactTokensForTokens(amountIn, amountOutMin, path, to, deadline).send();
    }

    //已经设置过直接返回，否则需要调用合约
    private static boolean approve(ERC20Token erc20Token, String own, String spender) throws Exception {
        BigInteger allowance = erc20Token.allowance(own, spender).send();
        if (allowance.compareTo(BigInteger.ZERO) > 0) {
            return true;
        } else {
            erc20Token.approve(spender, new BigInteger("2").pow(255)).send(); //2的256次方
            return true;
        }
    }


    private static BigInteger getMaxOut(BigInteger amountIn, List<String> path, PancakeRouter router) throws Exception {
        List<BigInteger> amountOuts = router.getAmountsOut(amountIn, path).send();
        if (!CollectionUtils.isEmpty(amountOuts)) {
            return amountOuts.get(amountOuts.size() - 1);
        } else {
            throw new Exception("get max out error.");
        }
    }

    private static List<String> getBestPath(String fromToken, String toToken, String amountIn, PancakeSwapFactory factory) throws Exception {
        String pair = factory.getPair(fromToken, toToken).send();
        if (StringUtils.isNotBlank(pair)) { //
            return Arrays.asList(fromToken, toToken);
        } else {
//            String url = String.format(GET_BEST_PATH_URL, fromToken, toToken, amountIn);
//            Request req = new Request.Builder().url(url).build();
//            Response rsp = okHttpClient.newCall(req).execute();
//            String body = rsp.body().toString();
//            JSONObject jo = JSONObject.parseObject(body);
//            String data = jo.getString("Data");
//            return JSONObject.parseArray(data, String.class);
            throw new Exception("没有池子！！");
        }

    }

    //链上推荐的gas price，快速成交可以乘2
    public static BigInteger getGasPrice(Web3j web3j) throws IOException {
        return web3j.ethGasPrice().send().getGasPrice();
    }


}
