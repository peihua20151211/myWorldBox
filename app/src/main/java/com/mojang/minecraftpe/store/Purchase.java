package com.mojang.minecraftpe.store;

public class Purchase {
    public String mProductId;
    public boolean mPurchaseActive;
    public String mReceipt;

    public Purchase(String paramString) {
        this.mProductId = paramString;
    }
}
