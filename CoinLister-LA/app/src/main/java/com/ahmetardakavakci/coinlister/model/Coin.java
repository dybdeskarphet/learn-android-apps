package com.ahmetardakavakci.coinlister.model;

import com.google.gson.annotations.SerializedName;

public class Coin {

    @SerializedName("currency")
    public String currency;

    @SerializedName("price")
    public String price;

    public Coin(String currency, String price) {
        this.currency = currency;
        this.price = price;
    }
}
