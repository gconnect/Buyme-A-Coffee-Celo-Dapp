package com.example.celocoffeeapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;


public class BeneficiaryAdapter extends RecyclerView.Adapter<BeneficiaryAdapter.ViewHolder>{
    private BeneficiaryModel[] listdata;
    private Context context;
    private ItemClickListener itemClickListener;

    public BeneficiaryAdapter(ItemClickListener clicklistener, Context context, BeneficiaryModel[] listdata) {
        this.listdata = listdata;
        this.context = context;
        this.itemClickListener = clicklistener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.beneficiary_list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        final BeneficiaryModel myListData = listdata[position];
        holder.imageView.setImageResource(listdata[position].getImgId());
        holder.name.setText(listdata[position].getName());
        holder.description.setText(listdata[position].getDescription());
        holder.donation_received.setText(String.valueOf(String.format("Donations Received: %s", BeneficiaryAccountBalance.balance(position, listdata))));
        holder.button.setText(String.format(listdata[position].getWalletAddress().substring(0, 5) + "..."  + "%s" , "Donate"));
        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(), "click on item: " + myListData.getName(), Toast.LENGTH_LONG).show();
                itemClickListener.onItemClicked(myListData);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listdata.length;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView name, description, donation_received;
        public Button button;
        public ConstraintLayout constraintLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            this.imageView = (ImageView) itemView.findViewById(R.id.profile_image);
            this.name = (TextView) itemView.findViewById(R.id.name);
            this.description = (TextView) itemView.findViewById(R.id.description);
            this.donation_received = (TextView) itemView.findViewById(R.id.donations_received);
            this.button = (Button) itemView.findViewById(R.id.donateBtn);
            constraintLayout = (ConstraintLayout) itemView.findViewById(R.id.constraint);

        }
    }

    public interface ItemClickListener {
        public void onItemClicked(BeneficiaryModel listData);
    }
}