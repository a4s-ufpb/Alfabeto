package com.napoleao.alphabeto.helper;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.Map;
import java.util.concurrent.TimeUnit;


import com.napoleao.alphabeto.BuildConfig;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiConfig
{
    public static final String GITHUB_USER = "a4s-ufpb";

    private static ApiConfig instance;
    public SharedPreferences sPreferences = null;

    private String dominio;
    private long updateInterval;

    public static ApiConfig getInstance(Context appContext)
    {
        if(instance == null){
            instance = new ApiConfig(appContext);
        }
        return instance;
    }

    private ApiConfig(Context appContext)
    {
        try {
            if(sPreferences == null) {
                sPreferences = appContext.getSharedPreferences("apiConfig", MODE_PRIVATE);
            }
            dominio = sPreferences.getString("dominio", "https://api.apps4society.dcx.ufpb.br/educapi/");
            updateInterval = sPreferences.getLong("updateInterval", 5*60);
            updateApiConfigBackground();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateApiConfigBackground()
    {
        long lastTime = sPreferences.getLong("time", System.currentTimeMillis());
        if((System.currentTimeMillis()-lastTime) > updateInterval) {
            Log.i("ApiConfig", "Check Bypassed, Checked less than minimum interval "+updateInterval+" secs.");
            return;
        }
        Retrofit retrofit = new Retrofit.Builder().baseUrl("https://raw.githubusercontent.com/"+ GITHUB_USER +"/EducAPI-Config/main/").addConverterFactory(GsonConverterFactory.create()).client(getOkHttpClient()).build();
        ApiConfigRetrofit apiConfigRetrofit = retrofit.create(ApiConfigRetrofit.class);
        Call<Map> call = apiConfigRetrofit.getAPIConfig();
        call.enqueue(new Callback<Map>() {
            @Override
            public void onResponse(Call<Map> call, Response<Map> response) {
                if (response.isSuccessful()) {
                    Map apiConfigDic = response.body();
                    if(apiConfigDic!=null && apiConfigDic.containsKey("sucess") && new Boolean((Boolean) apiConfigDic.get("sucess")).booleanValue()==true) {

                        // use specific config by application id, if available
                        Map configForApp = (Map)apiConfigDic.get(BuildConfig.APPLICATION_ID);
                        if(configForApp != null) {
                            apiConfigDic = configForApp;
                        }

                        // update to settings
                        Object dominioVal = apiConfigDic.get("dominio");
                        if(dominioVal!=null) {
                            dominio = (String)dominioVal;
                        }
                        Object updateIntervalVal = apiConfigDic.get("updateInterval");
                        if(updateIntervalVal!=null) {
                            updateInterval = (long)updateIntervalVal;
                        }
                        saveAllChanges();
                    }
                }
            }

            @Override
            public void onFailure(Call<Map> call, Throwable t) {

            }
        });
    }

    private static OkHttpClient getOkHttpClient()
    {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        try {
            builder.connectTimeout(30, TimeUnit.MINUTES);
            builder.readTimeout(30, TimeUnit.MINUTES);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return builder.build();
    }

    private void saveAllChanges()
    {
        try {
            SharedPreferences.Editor edit = sPreferences.edit();
            edit.putString("dominio", dominio);
            edit.putLong("updateInterval", updateInterval);
            edit.putLong("time", System.currentTimeMillis());
            edit.apply();
            Log.i("ApiConfig", "All changes have been saved");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getDominio() {
        if(BuildConfig.EDUC_API_URL != null) {
            return BuildConfig.EDUC_API_URL;
        }
        return dominio;
    }
}
