package com.example.celocoffeeapp;

import android.os.Parcel;
import android.os.Parcelable;

public class BeneficiaryModel implements Parcelable {
    private int imgId;
    private String name;
    private String description;
    private String walletAddress;

    public BeneficiaryModel(int imgId, String name, String description,
                             String walletAddress) {
        this.imgId = imgId;
        this.name = name;
        this.description = description;
        this.walletAddress = walletAddress;
    }

    protected BeneficiaryModel(Parcel in) {
        imgId = in.readInt();
        name = in.readString();
        description = in.readString();
        walletAddress = in.readString();
    }

    public static final Creator<BeneficiaryModel> CREATOR = new Creator<BeneficiaryModel>() {
        @Override
        public BeneficiaryModel createFromParcel(Parcel in) {
            return new BeneficiaryModel(in);
        }

        @Override
        public BeneficiaryModel[] newArray(int size) {
            return new BeneficiaryModel[size];
        }
    };

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public int getImgId() {
        return imgId;
    }
    public void setImgId(int imgId) {
        this.imgId = imgId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWalletAddress() {
        return walletAddress;
    }

    public void setWalletAddress(String walletAddress) {
        this.walletAddress = walletAddress;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(imgId);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeString(walletAddress);
    }
}
