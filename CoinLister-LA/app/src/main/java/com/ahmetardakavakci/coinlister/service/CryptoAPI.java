package com.ahmetardakavakci.coinlister.service;

import com.ahmetardakavakci.coinlister.model.Coin;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.GET;

public interface CryptoAPI {

    // https://gist.githubusercontent.com/dybdeskarphet/3ef094a6c1adc9f5fbd3acc002ebf31f/raw/00bce6439636d103f88a4373c83edfc2c92bf946/currencies.json
    @GET("dybdeskarphet/3ef094a6c1adc9f5fbd3acc002ebf31f/raw/00bce6439636d103f88a4373c83edfc2c92bf946/currencies.json")
    Observable<List<Coin>> getData();

}
