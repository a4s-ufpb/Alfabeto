package com.napoleao.alphabeto.api;

import com.napoleao.alphabeto.model.Tema;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ContextService {

    @GET("contexts/{id}")
    Call<Tema> getContextById(@Path("id") Long id);

    @GET("contexts")
    Call<List<Tema>> getContextsByUser(@Query("user") Long id);
}
