package org.web3j.model;

import io.reactivex.Flowable;
import io.reactivex.functions.Function;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Int24;
import org.web3j.abi.datatypes.generated.Uint24;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.RemoteFunctionCall;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.BaseEventResponse;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tuples.generated.Tuple5;
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
public class UniswapV3Factory extends Contract {
    public static final String BINARY = "Bin file was not provided";

    public static final String FUNC_CREATEPOOL = "createPool";

    public static final String FUNC_ENABLEFEEAMOUNT = "enableFeeAmount";

    public static final String FUNC_FEEAMOUNTTICKSPACING = "feeAmountTickSpacing";

    public static final String FUNC_GETPOOL = "getPool";

    public static final String FUNC_OWNER = "owner";

    public static final String FUNC_PARAMETERS = "parameters";

    public static final String FUNC_SETOWNER = "setOwner";

    public static final Event FEEAMOUNTENABLED_EVENT = new Event("FeeAmountEnabled", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint24>(true) {}, new TypeReference<Int24>(true) {}));
    ;

    public static final Event OWNERCHANGED_EVENT = new Event("OwnerChanged", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}, new TypeReference<Address>(true) {}));
    ;

    public static final Event POOLCREATED_EVENT = new Event("PoolCreated", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}, new TypeReference<Address>(true) {}, new TypeReference<Uint24>(true) {}, new TypeReference<Int24>() {}, new TypeReference<Address>() {}));
    ;

    @Deprecated
    protected UniswapV3Factory(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected UniswapV3Factory(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected UniswapV3Factory(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected UniswapV3Factory(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static List<FeeAmountEnabledEventResponse> getFeeAmountEnabledEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = staticExtractEventParametersWithLog(FEEAMOUNTENABLED_EVENT, transactionReceipt);
        ArrayList<FeeAmountEnabledEventResponse> responses = new ArrayList<FeeAmountEnabledEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            FeeAmountEnabledEventResponse typedResponse = new FeeAmountEnabledEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.fee = (BigInteger) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.tickSpacing = (BigInteger) eventValues.getIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<FeeAmountEnabledEventResponse> feeAmountEnabledEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, FeeAmountEnabledEventResponse>() {
            @Override
            public FeeAmountEnabledEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(FEEAMOUNTENABLED_EVENT, log);
                FeeAmountEnabledEventResponse typedResponse = new FeeAmountEnabledEventResponse();
                typedResponse.log = log;
                typedResponse.fee = (BigInteger) eventValues.getIndexedValues().get(0).getValue();
                typedResponse.tickSpacing = (BigInteger) eventValues.getIndexedValues().get(1).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<FeeAmountEnabledEventResponse> feeAmountEnabledEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(FEEAMOUNTENABLED_EVENT));
        return feeAmountEnabledEventFlowable(filter);
    }

    public static List<OwnerChangedEventResponse> getOwnerChangedEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = staticExtractEventParametersWithLog(OWNERCHANGED_EVENT, transactionReceipt);
        ArrayList<OwnerChangedEventResponse> responses = new ArrayList<OwnerChangedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            OwnerChangedEventResponse typedResponse = new OwnerChangedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.oldOwner = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.newOwner = (String) eventValues.getIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<OwnerChangedEventResponse> ownerChangedEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, OwnerChangedEventResponse>() {
            @Override
            public OwnerChangedEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(OWNERCHANGED_EVENT, log);
                OwnerChangedEventResponse typedResponse = new OwnerChangedEventResponse();
                typedResponse.log = log;
                typedResponse.oldOwner = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse.newOwner = (String) eventValues.getIndexedValues().get(1).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<OwnerChangedEventResponse> ownerChangedEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(OWNERCHANGED_EVENT));
        return ownerChangedEventFlowable(filter);
    }

    public static List<PoolCreatedEventResponse> getPoolCreatedEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = staticExtractEventParametersWithLog(POOLCREATED_EVENT, transactionReceipt);
        ArrayList<PoolCreatedEventResponse> responses = new ArrayList<PoolCreatedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            PoolCreatedEventResponse typedResponse = new PoolCreatedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.token0 = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.token1 = (String) eventValues.getIndexedValues().get(1).getValue();
            typedResponse.fee = (BigInteger) eventValues.getIndexedValues().get(2).getValue();
            typedResponse.tickSpacing = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.pool = (String) eventValues.getNonIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<PoolCreatedEventResponse> poolCreatedEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, PoolCreatedEventResponse>() {
            @Override
            public PoolCreatedEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(POOLCREATED_EVENT, log);
                PoolCreatedEventResponse typedResponse = new PoolCreatedEventResponse();
                typedResponse.log = log;
                typedResponse.token0 = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse.token1 = (String) eventValues.getIndexedValues().get(1).getValue();
                typedResponse.fee = (BigInteger) eventValues.getIndexedValues().get(2).getValue();
                typedResponse.tickSpacing = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.pool = (String) eventValues.getNonIndexedValues().get(1).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<PoolCreatedEventResponse> poolCreatedEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(POOLCREATED_EVENT));
        return poolCreatedEventFlowable(filter);
    }

    public RemoteFunctionCall<TransactionReceipt> createPool(String tokenA, String tokenB, BigInteger fee) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_CREATEPOOL, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, tokenA), 
                new org.web3j.abi.datatypes.Address(160, tokenB), 
                new org.web3j.abi.datatypes.generated.Uint24(fee)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> enableFeeAmount(BigInteger fee, BigInteger tickSpacing) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_ENABLEFEEAMOUNT, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint24(fee), 
                new org.web3j.abi.datatypes.generated.Int24(tickSpacing)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<BigInteger> feeAmountTickSpacing(BigInteger param0) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_FEEAMOUNTTICKSPACING, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint24(param0)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Int24>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<String> getPool(String param0, String param1, BigInteger param2) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_GETPOOL, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, param0), 
                new org.web3j.abi.datatypes.Address(160, param1), 
                new org.web3j.abi.datatypes.generated.Uint24(param2)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<String> owner() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_OWNER, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<Tuple5<String, String, String, BigInteger, BigInteger>> parameters() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_PARAMETERS, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Address>() {}, new TypeReference<Address>() {}, new TypeReference<Uint24>() {}, new TypeReference<Int24>() {}));
        return new RemoteFunctionCall<Tuple5<String, String, String, BigInteger, BigInteger>>(function,
                new Callable<Tuple5<String, String, String, BigInteger, BigInteger>>() {
                    @Override
                    public Tuple5<String, String, String, BigInteger, BigInteger> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple5<String, String, String, BigInteger, BigInteger>(
                                (String) results.get(0).getValue(), 
                                (String) results.get(1).getValue(), 
                                (String) results.get(2).getValue(), 
                                (BigInteger) results.get(3).getValue(), 
                                (BigInteger) results.get(4).getValue());
                    }
                });
    }

    public RemoteFunctionCall<TransactionReceipt> setOwner(String _owner) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_SETOWNER, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, _owner)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    @Deprecated
    public static UniswapV3Factory load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new UniswapV3Factory(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static UniswapV3Factory load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new UniswapV3Factory(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static UniswapV3Factory load(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return new UniswapV3Factory(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static UniswapV3Factory load(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new UniswapV3Factory(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static class FeeAmountEnabledEventResponse extends BaseEventResponse {
        public BigInteger fee;

        public BigInteger tickSpacing;
    }

    public static class OwnerChangedEventResponse extends BaseEventResponse {
        public String oldOwner;

        public String newOwner;
    }

    public static class PoolCreatedEventResponse extends BaseEventResponse {
        public String token0;

        public String token1;

        public BigInteger fee;

        public BigInteger tickSpacing;

        public String pool;
    }
}
