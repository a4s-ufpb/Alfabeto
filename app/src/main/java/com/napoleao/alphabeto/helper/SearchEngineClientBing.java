package com.napoleao.alphabeto.helper;


import android.content.Context;
import android.net.Uri;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.napoleao.alphabeto.R;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SearchEngineClientBing {

    private static String API_KEY = null;

    public SearchEngineClientBing (Context context){
        API_KEY = context.getResources().getString(R.string.bing_key);
    }

    public interface SearchEngineClientBingCompletionHandler
    {
        public void success(List<HashMap<String, Object>> resultado);
        public void failed(String reason);
    }

    public void search(final String query, SearchEngineClientBingCompletionHandler completionHandler) {
        SearchExecutorBing executor = new SearchExecutorBing(query, API_KEY, completionHandler);
        executor.start();
    }

    class SearchExecutorBing extends Thread {

        private String search;
        private String API_KEY = null;
        private List<HashMap<String, Object>> results = new ArrayList<>();
        private SearchEngineClientBingCompletionHandler completionHandler;

        SearchExecutorBing(String search, String key, SearchEngineClientBingCompletionHandler handler) {
            this.API_KEY = key;
            this.completionHandler = handler;
            this.search = search;
        }

        @Override
        public void run() {
            try {

                Uri.Builder builder = new Uri.Builder();
                builder.scheme("https")
                        .authority("api.bing.microsoft.com")
                        .appendPath("v7.0")
                        .appendPath("images")
                        .appendPath("search")
                        .appendQueryParameter("q", search)
                        .appendQueryParameter("imageType", "photo")
                        .appendQueryParameter("count", "150")
                        .appendQueryParameter("offset", "0")
                        .appendQueryParameter("safeSearch", "Strict");

                URL url = new URL(builder.build().toString());

                URLConnection connection = url.openConnection();
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);
                connection.setRequestProperty("Ocp-Apim-Subscription-Key", API_KEY);
                HttpURLConnection connectionResp = ((HttpURLConnection)connection);
                String strCurrentLine = "";
                if (connectionResp.getResponseCode() == 200) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(connectionResp.getInputStream()));
                    String resp;
                    while ((resp = br.readLine()) != null) {
                        strCurrentLine += resp;
                    }
                    ObjectMapper mapper = new ObjectMapper();
                    Map mapRequest = mapper.readValue(strCurrentLine, Map.class);
                    results = (List<HashMap<String, Object>>)mapRequest.get("value");

                    completionHandler.success(results);
                }

            } catch (Exception e) {
                completionHandler.failed("Erro ao carregar pesquisa por imagem.");
            }
        }
    }
}
