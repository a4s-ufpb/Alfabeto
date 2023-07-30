package com.napoleao.alphabeto.helper;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Class to retrieve settings banked up from Github repository, to be used across App's
 */
public interface ApiConfigRetrofit {
    @GET("config.json")
    Call<Map> getAPIConfig();
}
