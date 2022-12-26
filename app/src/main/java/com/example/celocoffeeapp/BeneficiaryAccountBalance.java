package com.example.celocoffeeapp;

import org.celo.contractkit.CeloContract;
import org.celo.contractkit.ContractKit;
import org.celo.contractkit.ContractKitOptions;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Convert;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class BeneficiaryAccountBalance {
    public static BigDecimal balance(int position, BeneficiaryModel[] listdata) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        final BigInteger[] donationReceived = {BigInteger.valueOf(0)};

        Future<Long> result = executor.submit(new Callable<Long>() {
            @Override
            public Long call() throws Exception {
                // getting the balance of each of the address

                String listAddress = listdata[position].getWalletAddress();
                ContractKit contractKit = ContractKit.build(new HttpService("https://alfajores-forno.celo-testnet.org"));
                Web3j web3j = Web3j.build(new HttpService(ContractKit.ALFAJORES_TESTNET));
                ContractKitOptions config = new ContractKitOptions.Builder()
                        .setFeeCurrency(CeloContract.GoldToken)
                        .setGasPrice(BigInteger.valueOf(21_000))
                        .build();
                contractKit = new ContractKit(web3j, config);
                org.celo.contractkit.AccountBalance balance = null;
                try {
                    balance = contractKit.getTotalBalance(listAddress);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                System.out.println("cUSD balance " + balance.cUSD);
                donationReceived[0] = balance.cUSD;
//                System.out.println("TodonationReceived " + Convert.fromWei(donationReceived[0].toString(), Convert.Unit.ETHER));
                return donationReceived[0].longValue();
            }
        });
        while (!result.isDone()) {
            try {
//                System.out.println("Waiting for the Future to complete ...");
                Long returnValue = result.get();
//                System.out.println("returnvalue" + returnValue);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }

        return Convert.fromWei(donationReceived[0].toString(), Convert.Unit.ETHER);
    }
}

