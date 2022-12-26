package com.example.celocoffeeapp;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.celocoffeeapp.databinding.FragmentSecondBinding;

import java.util.Objects;

public class SecondFragment extends Fragment {

    private FragmentSecondBinding binding;
    private BeneficiaryModel model;
    String inputAmount;
    String inputComment;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentSecondBinding.inflate(inflater, container, false);

        assert getArguments() != null;
        model = getArguments().getParcelable("data");
        String s = "Buy %s a coffee";
        binding.textviewName.setText(String.format(s, model.getName()));
        binding.textviewDescription.setText(model.getDescription());
        inputAmount = Objects.requireNonNull(binding.amountInput.getText()).toString().trim();
        inputComment = Objects.requireNonNull(binding.commentInput.getText()).toString().trim();

        binding.amountInput.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                // you can call or do what you want with your EditText here
                // yourEditText...
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                System.out.println("text" + s);
                binding.buttonSecond.setText(new StringBuilder().append("Support ").append("( cUSD").append(s).toString() + " )");
                inputAmount = s.toString();
            }
        });


        binding.buttonSecond.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                NavHostFragment.findNavController(SecondFragment.this)
//                        .navigate(R.id.action_SecondFragment_to_FirstFragment);
                if (inputAmount.isEmpty()) {
                    binding.amountLayout.setError("Field required!");
                    return;
                } else {
                    makePaymentWithCeloWallet();
                }
            }
        });
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void makePaymentWithCeloWallet() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String walletAddress = model.getWalletAddress();
//                    String url = "celo://wallet/pay?address=0x4b371df8d05abd2954564b54faf10b8c8f1bc3a2&displayName=Example%20name&amount=9.50&comment=Burger%20with%20fries&token=cUSD&currencyCode=USD";
                    String url = "celo://wallet/pay?address=" + walletAddress + "&displayName=Buy%20Me%20A%20Coffee&amount=" + inputAmount + "&comment=" + inputComment + "&token=cUSD&currencyCode=USD";
                    final Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(url));
                    startActivity(intent);
                } catch (Exception e) {
                    Log.i(TAG, "run: " + e);

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getContext(), "Valora app not installed, please install...", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
        thread.start();
    }
}