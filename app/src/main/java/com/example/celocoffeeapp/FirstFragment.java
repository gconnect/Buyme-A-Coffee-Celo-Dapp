package com.example.celocoffeeapp;

import static org.celo.contractkit.protocol.CeloGasProvider.GAS_LIMIT;
import static org.celo.contractkit.protocol.CeloGasProvider.GAS_PRICE;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.celocoffeeapp.databinding.FragmentFirstBinding;
import com.google.gson.Gson;

import org.celo.contractkit.AccountBalance;
import org.celo.contractkit.CeloContract;
import org.celo.contractkit.ContractKit;
import org.celo.contractkit.ContractKitOptions;
import org.celo.contractkit.contract.GoldToken;
import org.celo.contractkit.protocol.CeloRawTransaction;
import org.celo.contractkit.wrapper.ExchangeWrapper;
import org.celo.contractkit.wrapper.GoldTokenWrapper;
import org.celo.contractkit.wrapper.StableTokenWrapper;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Convert;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Objects;

public class FirstFragment extends Fragment implements BeneficiaryAdapter.ItemClickListener{
    private FragmentFirstBinding binding;
    private static final String TAG = "FirstFragment";
    Gson gson = new Gson();

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentFirstBinding.inflate(inflater, container, false);

        // This section test some of the classes and methods of the Java SDK contract kit
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ContractKit contractKit = ContractKit.build(new HttpService("https://alfajores-forno.celo-testnet.org"));
                    Web3j web3j = Web3j.build(new HttpService(ContractKit.ALFAJORES_TESTNET));

                    ContractKitOptions config = new ContractKitOptions.Builder()
                            .setFeeCurrency(CeloContract.StableToken)
                            .setGasPrice(BigInteger.valueOf(21_000))
                            .build();
                    contractKit = new ContractKit(web3j, config);

                    // Multiple accounts can be added to the kit wallet. The first added account will be used by default.
                    contractKit.addAccount(Constant.somePrivateKey);

                    // To change default account to sign transactions
                    contractKit.setDefaultAccount(Constant.publicKey);

                    // Getting the Total Balance
                    try {
                        AccountBalance balance = contractKit.getTotalBalance(Constant.myAddress);
                        String acct = gson.toJson(balance, AccountBalance.class);
                        System.out.println( "acct details" + acct);
                        System.out.println("cUSD balance " + balance.cUSD);
                        System.out.println("Celo balance " + balance.CELO);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    // deploy contract
                    GoldToken deployedGoldenToken = null;
                    try {
                        deployedGoldenToken = contractKit.contracts.getGoldToken().deploy().send();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
//
//                    // Get transaction receipt
                    TransactionReceipt receipt = null;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N)
                        receipt = Objects.requireNonNull(deployedGoldenToken).getTransactionReceipt().get();
                    if (receipt != null) {
                        String hash = receipt.getTransactionHash();
                        System.out.println("hash " + hash);
                    }

                    //Buying celo with cusd
                    ExchangeWrapper exchange = contractKit.contracts.getExchange();
                    StableTokenWrapper stableToken = contractKit.contracts.getStableToken();
                    GoldTokenWrapper goldToken = contractKit.contracts.getGoldToken();
                    System.out.println( "gold token" + goldToken);

                    BigInteger cUsdBalance = stableToken.balanceOf(Constant.myAddress).send();
                    System.out.println( "cUsdBalance " + cUsdBalance);

                    TransactionReceipt approveTx = stableToken.approve(exchange.getContractAddress(), cUsdBalance).send();
                    String approveTxHash = approveTx.getTransactionHash();
                    System.out.println( "Approve stabletoken Hash" + approveTxHash);

                    BigInteger goldAmount = exchange.quoteUsdSell(cUsdBalance).send();
                    System.out.println("goldAmount" + goldAmount);
//                    TransactionReceipt sellTx = exchange.sellDollar(cUsdBalance, goldAmount).send();
//                    String sellTxHash = sellTx.getTransactionHash();
//                    System.out.println( "Exchange Hash" + sellTxHash);

//                    Interacting with CELO & cUSD
                    GoldTokenWrapper goldtoken = contractKit.contracts.getGoldToken();
                    BigInteger goldBalance = goldtoken.balanceOf(Constant.myAddress);
                    System.out.println( "goldBalance " + Convert.fromWei(goldBalance.toString(), Convert.Unit.ETHER));

//                    Send GoldToken fund
                    BigInteger oneGold = Convert.toWei(BigDecimal.ONE, Convert.Unit.ETHER).toBigInteger();
                    TransactionReceipt tx = goldtoken.transfer(Constant.someAddress, oneGold).send();
                    String hash = tx.getTransactionHash();
                    System.out.println( "trans hash " + hash);

                    // Contract Addresses
                    String goldTokenAddress = contractKit.contracts.addressFor(CeloContract.GoldToken);
                    System.out.println("goldAddress " + goldTokenAddress);


                    CeloRawTransaction tx1 = CeloRawTransaction.createCeloTransaction(
                            BigInteger.ZERO,
                            GAS_PRICE,
                            GAS_LIMIT,
                            Constant.someAddress,
                            BigInteger.ONE,
                            contractKit.contracts.addressFor(CeloContract.StableToken),
                            null,
                            null
                    );
                    EthSendTransaction receipt1 = contractKit.sendTransaction(tx1, Constant.myAddress);
                    System.out.print( "reeipt1 " + (receipt1 != null ? receipt1.getTransactionHash() : null));
                    Log.i(TAG, "receipt1 " + gson.toJson(receipt1, EthSendTransaction.class ));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();

        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
       BeneficiaryModel[] myListData =  DataSource.myListData;

        BeneficiaryAdapter adapter = new BeneficiaryAdapter(this,getContext(), myListData);
        binding.recyclerview.setHasFixedSize(true);
        binding.recyclerview.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerview.setAdapter(adapter);


        binding.swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                binding.recyclerview.setHasFixedSize(true);
                binding.recyclerview.setLayoutManager(new LinearLayoutManager(getContext()));
                binding.recyclerview.setAdapter(adapter);
                binding.swipe.setRefreshing(false);

            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


    @Override
    public void onItemClicked(BeneficiaryModel listData) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("data", listData);
        System.out.println(listData);
        NavHostFragment.findNavController(FirstFragment.this)
                .navigate(R.id.action_FirstFragment_to_SecondFragment, bundle );
    }
}