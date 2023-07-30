package com.napoleao.alphabeto.api;

import com.napoleao.alphabeto.api.response.ContextPageResponse;
import com.napoleao.alphabeto.model.Tema;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Interface that represents the Context API.
 */
public interface ContextService {

    @GET("v1/api/contexts/{idContext}")
    Call<Tema> getContextById(@Path("idContext") Long idContext);

    @GET("v1/api/contexts")
    Call<ContextPageResponse> getContextsByUser(@Query("email") String email);
}
