package com.napoleao.alphabeto.api;

import com.napoleao.alphabeto.model.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Interface that represents the User API.
 */
public interface UserService {

    @GET("users")
    Call<List<User>> allUsers();

    @GET("users/{id}")
    Call<User> getUserById(@Path("id") Long id);
}
