package com.napoleao.alphabeto.helper;

import com.napoleao.alphabeto.api.ContextService;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Class to retrieve settings banked up from Github repository, to be used across App's
 */
public class RetrofitConfig {

    public static Retrofit getRetrofit() {
        return new Retrofit.Builder()
                .baseUrl(ApiConfig.getInstance(null).getDomain())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public ContextService contextService() {
        return getRetrofit().create(ContextService.class);
    }
}
