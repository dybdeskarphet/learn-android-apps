package com.ahmetardakavakci.coinlister.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.view.View;

import com.ahmetardakavakci.coinlister.adapter.CoinAdapter;
import com.ahmetardakavakci.coinlister.databinding.ActivityMainBinding;
import com.ahmetardakavakci.coinlister.model.Coin;
import com.ahmetardakavakci.coinlister.service.CryptoAPI;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    final private String BASE_URL = "https://gist.githubusercontent.com/";
    Retrofit retrofit;
    CoinAdapter coinAdapter;
    ArrayList<Coin> coinModels;

    CompositeDisposable compositeDisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        Gson gson = new GsonBuilder().setLenient().create();

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        loadData();

        /*
         currencies json
         https://gist.githubusercontent.com/dybdeskarphet/3ef094a6c1adc9f5fbd3acc002ebf31f/raw/00bce6439636d103f88a4373c83edfc2c92bf946/currencies.json
        */
    }

    private void loadData() {

        final CryptoAPI cryptoAPI = retrofit.create(CryptoAPI.class);
        compositeDisposable = new CompositeDisposable();
        compositeDisposable.add(cryptoAPI.getData()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(this::handleResponse)
        );
    }

    private void handleResponse(List<Coin> coinList){
        coinModels = new ArrayList<>(coinList);
        coinAdapter = new CoinAdapter(coinModels);
        binding.recyclerView.setAdapter(coinAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }
}