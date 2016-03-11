package com.teioh.m_feed.Utils.MAL;


import android.util.Base64;

import com.squareup.okhttp.OkHttpClient;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.converter.SimpleXMLConverter;

//public class MALApi {
//
//    private static MALService REST_CLIENT;
//    private static String ROOT = "http://myanimelist.net/api";
//
//    static {
//        setupRestClient();
//    }
//
//    private MALApi() {
//    }
//
//    public static MALService get() {
//        return REST_CLIENT;
//    }
//
//    private static void setupRestClient() {
//        RestAdapter restAdapter = new RestAdapter.Builder()
//                .setEndpoint(ROOT)
//                .setClient(new OkClient(new OkHttpClient()))
//                .build();
//
//        REST_CLIENT = restAdapter.create(MALService.class);
//    }
//}

public class MALApi {

    public static final String API_BASE_URL = "http://myanimelist.net/api";

    private static RestAdapter.Builder builder = new RestAdapter.Builder()
            .setEndpoint(API_BASE_URL)
            .setConverter(new SimpleXMLConverter())
            .setClient(new OkClient(new OkHttpClient()));

    public static MALService createService() {
        return createService(null, null);
    }

    public static MALService createService(String username, String password) {
        if (username != null && password != null) {
            // concatenate username and password with colon for authentication
            String credentials = username + ":" + password;
            // create Base64 encodet string
            final String basic = "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);

            builder.setRequestInterceptor(new RequestInterceptor() {
                @Override
                public void intercept(RequestInterceptor.RequestFacade request) {
                    request.addHeader("Authorization", basic);
                    request.addHeader("Accept", "application/xml");
                }
            });
        }

        RestAdapter adapter = builder.build();
        return adapter.create(MALService.class);
    }
}
