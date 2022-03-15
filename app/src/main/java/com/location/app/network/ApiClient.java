package com.location.app.network ;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;


import com.location.app.utils.Constants;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.prefs.Preferences;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;

import retrofit2.converter.gson.GsonConverterFactory;

import retrofit2.converter.scalars.ScalarsConverterFactory;

public class ApiClient {


    private static Retrofit retrofit = null;
    public static Retrofit getRetrofitInstance(final Context mContext) {


        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.connectTimeout(30, TimeUnit.SECONDS);
        httpClient.readTimeout(30, TimeUnit.SECONDS);
        httpClient.writeTimeout(30, TimeUnit.SECONDS);
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        httpClient.interceptors().add(interceptor);
        httpClient.addInterceptor(new Interceptor() {
            @NonNull
            @Override
            public Response intercept(@NonNull Chain chain) throws IOException {
                Request originalRequest = chain.request();


                Request request = originalRequest.newBuilder()
                        .addHeader("Connection", "close")
                        .header("Content-Type", "application/json")
                        .header("Accept-Encoding", "gzip, deflate, br")
                        .method(originalRequest.method(), originalRequest.body())
                        .build();
//                        .header("Content-Type", "application/json")
//                        .method(originalRequest.method(), originalRequest.body())
//                        .build();

                return chain.proceed(request);
            }
        });
        OkHttpClient client = httpClient.build();

        retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
        Log.d(TAG, "getRetrofitInstance: "+retrofit);
        return retrofit;
//        retrofit = new Retrofit.Builder()
//                .baseUrl(Constants.BASE_URL)
//                .addConverterFactory(ScalarsConverterFactory.create())
//                .addConverterFactory(GsonConverterFactory.create())
//                .client(client)
//                .build();
//
//        return retrofit;


    }



}
