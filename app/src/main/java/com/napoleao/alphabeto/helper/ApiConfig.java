package com.napoleao.alphabeto.helper;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.security.cert.CertificateException;
import java.util.Map;
import java.util.concurrent.TimeUnit;


import com.napoleao.alphabeto.BuildConfig;
import com.napoleao.alphabeto.R;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Class to retrieve settings banked up from Github repository, to be used across App's
 */
public class ApiConfig {
    /**
     * Setup Github user
     */
    public static final String GITHUB_USER = "a4s-ufpb";
    private static final boolean updateIntervalEnabled = false;

    private static ApiConfig instance;
    public SharedPreferences sPreferences = null;

    private String domain;
    private long updateInterval;

    /**
     * Get shared instance
     *
     * @param appContext
     * @return
     */
    public static ApiConfig getInstance(Context appContext) {
        if (instance == null) {
            instance = new ApiConfig(appContext);
        }
        return instance;
    }

    /**
     * Alloc instance and load variables from saved settings
     *
     * @param appContext
     */
    private ApiConfig(Context appContext) {
        try {
            if (sPreferences == null) {
                sPreferences = appContext.getSharedPreferences("apiConfig", MODE_PRIVATE);
            }

            // alloc saved API domain with default
            domain = sPreferences.getString("domain", appContext.getResources().getString(R.string.domain));

            // setup default update interval, between last retrieved settings and new request to update
            updateInterval = sPreferences.getLong("updateInterval", 5 * 60); // default 5min

            updateApiConfigBackground();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Run request App configuration in background
     */
    private void updateApiConfigBackground() {

        // check last saved settings time, to avoid spamming request to github server
        long lastTime = sPreferences.getLong("time", System.currentTimeMillis());
        if (updateIntervalEnabled && ((System.currentTimeMillis() - lastTime) > updateInterval)) {
            //Log.i("ApiConfig", "Check Bypassed, Checked less than minimum interval " + updateInterval + " secs.");
            return;
        }

        Retrofit retrofit = new Retrofit.Builder().baseUrl("https://raw.githubusercontent.com/" + GITHUB_USER + "/EducAPI-Config/main/").addConverterFactory(GsonConverterFactory.create()).client(getOkHttpClient()).build();
        ApiConfigRetrofit apiConfigRetrofit = retrofit.create(ApiConfigRetrofit.class);
        Call<Map> call = apiConfigRetrofit.getAPIConfig();
        call.enqueue(new Callback<Map>() {
            @Override
            public void onResponse(Call<Map> call, Response<Map> response) {
                if (response.isSuccessful()) {
                    Map apiConfigDic = response.body();
                    if (apiConfigDic != null && apiConfigDic.containsKey("sucess")) {

                        // use specific config by application id, if available
                        Map configForApp = (Map) apiConfigDic.get(BuildConfig.APPLICATION_ID);
                        if (configForApp != null) {
                            apiConfigDic = configForApp;
                        }

                        // update to settings
                        Object domainVal = apiConfigDic.get("domain");
                        if (domainVal != null) {
                            domain = String.valueOf(domainVal);
                        }
                        Object updateIntervalVal = apiConfigDic.get("updateInterval");
                        if (updateIntervalVal != null) {
                            updateInterval = Long.getLong(String.valueOf(updateIntervalVal));
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

    /**
     * Configure request timeout in API calls
     *
     * @return
     */
    private static OkHttpClient getOkHttpClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        try {
            builder.connectTimeout(30, TimeUnit.MINUTES);
            builder.readTimeout(30, TimeUnit.MINUTES);

            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[] {
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }
                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }
                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };
            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
            X509TrustManager trustManager = (X509TrustManager) trustAllCerts[0];
            builder.sslSocketFactory(sslSocketFactory, trustManager);
            builder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return builder.build();
    }

    /**
     * Save current variables to settings
     */
    private void saveAllChanges() {
        try {
            SharedPreferences.Editor edit = sPreferences.edit();
            edit.putString("domain", domain);
            edit.putLong("updateInterval", updateInterval);

            // store the current time of saved settings
            edit.putLong("time", System.currentTimeMillis());

            edit.apply();
            //Log.i("ApiConfig", "All changes have been saved");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Return API domain used in App
     *
     * @return
     */
    public String getDomain() {
        if (BuildConfig.DEBUG && !(BuildConfig.EDUC_API_URL == null || !BuildConfig.EDUC_API_URL.startsWith("http"))) {
            return BuildConfig.EDUC_API_URL;
        }
        return domain;
    }
}
