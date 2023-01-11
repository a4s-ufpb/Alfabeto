package com.napoleao.alphabeto.helper;

import com.napoleao.alphabeto.api.ContextService;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitConfig {

    public static Retrofit getRetrofit(){
        return new Retrofit.Builder()
                .baseUrl(ApiConfig.getInstance(null).getDominio())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public ContextService contextService(){
        return getRetrofit().create(ContextService.class);
    }
}
