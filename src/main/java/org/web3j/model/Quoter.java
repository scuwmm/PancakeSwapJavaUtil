package org.web3j.model;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.RemoteFunctionCall;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;

/**
 * <p>Auto generated code.
 * <p><strong>Do not modify!</strong>
 * <p>Please use the <a href="https://docs.web3j.io/command_line.html">web3j command line tools</a>,
 * or the org.web3j.codegen.SolidityFunctionWrapperGenerator in the 
 * <a href="https://github.com/web3j/web3j/tree/master/codegen">codegen module</a> to update.
 *
 * <p>Generated with web3j version 4.9.4.
 */
@SuppressWarnings("rawtypes")
public class Quoter extends Contract {
    public static final String BINARY = "Bin file was not provided";

    public static final String FUNC_WETH9 = "WETH9";

    public static final String FUNC_FACTORY = "factory";

    public static final String FUNC_QUOTEEXACTINPUT = "quoteExactInput";

    public static final String FUNC_QUOTEEXACTINPUTSINGLE = "quoteExactInputSingle";

    public static final String FUNC_QUOTEEXACTOUTPUT = "quoteExactOutput";

    public static final String FUNC_QUOTEEXACTOUTPUTSINGLE = "quoteExactOutputSingle";

    public static final String FUNC_UNISWAPV3SWAPCALLBACK = "uniswapV3SwapCallback";

    @Deprecated
    protected Quoter(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected Quoter(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected Quoter(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected Quoter(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public RemoteFunctionCall<String> WETH9() {
        final Function function = new Function(FUNC_WETH9, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<String> factory() {
        final Function function = new Function(FUNC_FACTORY, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<TransactionReceipt> quoteExactInput(byte[] path, BigInteger amountIn) {
        final Function function = new Function(
                FUNC_QUOTEEXACTINPUT, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.DynamicBytes(path), 
                new org.web3j.abi.datatypes.generated.Uint256(amountIn)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> quoteExactInputSingle(String tokenIn, String tokenOut, BigInteger fee, BigInteger amountIn, BigInteger sqrtPriceLimitX96) {
        final Function function = new Function(
                FUNC_QUOTEEXACTINPUTSINGLE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, tokenIn), 
                new org.web3j.abi.datatypes.Address(160, tokenOut), 
                new org.web3j.abi.datatypes.generated.Uint24(fee), 
                new org.web3j.abi.datatypes.generated.Uint256(amountIn), 
                new org.web3j.abi.datatypes.generated.Uint160(sqrtPriceLimitX96)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> quoteExactOutput(byte[] path, BigInteger amountOut) {
        final Function function = new Function(
                FUNC_QUOTEEXACTOUTPUT, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.DynamicBytes(path), 
                new org.web3j.abi.datatypes.generated.Uint256(amountOut)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> quoteExactOutputSingle(String tokenIn, String tokenOut, BigInteger fee, BigInteger amountOut, BigInteger sqrtPriceLimitX96) {
        final Function function = new Function(
                FUNC_QUOTEEXACTOUTPUTSINGLE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, tokenIn), 
                new org.web3j.abi.datatypes.Address(160, tokenOut), 
                new org.web3j.abi.datatypes.generated.Uint24(fee), 
                new org.web3j.abi.datatypes.generated.Uint256(amountOut), 
                new org.web3j.abi.datatypes.generated.Uint160(sqrtPriceLimitX96)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    @Deprecated
    public static Quoter load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new Quoter(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static Quoter load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new Quoter(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static Quoter load(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return new Quoter(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static Quoter load(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new Quoter(contractAddress, web3j, transactionManager, contractGasProvider);
    }
}
