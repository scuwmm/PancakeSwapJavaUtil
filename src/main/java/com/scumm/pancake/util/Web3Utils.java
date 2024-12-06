package com.scumm.pancake.util;

import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.*;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.ens.NameHash;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthCall;
import org.web3j.utils.Numeric;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * web3j.ethCall 调用合约
 */
public class Web3Utils {

    public static String ownerOf(Web3j web3j, String from, String contractAddress) {
        if (web3j == null) return null;
        String methodName = "ownerOf";
        String totalSupply = "";

        //NameHash.nameHash("aaaaaa.sns") = 0xaea9b540c80b53001a47da5d9f40d0035870b4cee711702942c2632bccd6f69c
        List<Type> inputParameters = Arrays.asList(new Uint256(Numeric.toBigInt(NameHash.nameHash("aaaaaa.sns"))));
        List<TypeReference<?>> outputParameters = Arrays.asList(new TypeReference<Address>() {
        });


        Function function = new Function(methodName, inputParameters, outputParameters);

        String data = FunctionEncoder.encode(function);
        Transaction transaction = Transaction.createEthCallTransaction(from, contractAddress, data);

        EthCall ethCall;
        try {
            ethCall = web3j.ethCall(transaction, DefaultBlockParameterName.LATEST).sendAsync().get();
            List<Type> results = FunctionReturnDecoder.decode(ethCall.getValue(), function.getOutputParameters());
            totalSupply = (String) results.get(0).getValue();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return totalSupply;
    }


    public static Boolean available(Web3j web3j, String from, String contractAddress) {
        if (web3j == null) return null;
        String methodName = "available";
        Boolean totalSupply = false;
        List<Type> inputParameters = Arrays.asList(new Utf8String("bbbbbb"));
        List<TypeReference<?>> outputParameters = Arrays.asList(new TypeReference<Bool>() {
        });


        Function function = new Function(methodName, inputParameters, outputParameters);

        String data = FunctionEncoder.encode(function);
        Transaction transaction = Transaction.createEthCallTransaction("0xBE60f9Bfe340885511c0dED5f994677d55c8F5fD", contractAddress, data);

        EthCall ethCall;
        try {
            ethCall = web3j.ethCall(transaction, DefaultBlockParameterName.LATEST).sendAsync().get();
            List<Type> results = FunctionReturnDecoder.decode(ethCall.getValue(), function.getOutputParameters());
            totalSupply = (Boolean) results.get(0).getValue();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return totalSupply;
    }

}



