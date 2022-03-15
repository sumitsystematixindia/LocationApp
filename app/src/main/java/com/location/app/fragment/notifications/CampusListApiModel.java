package com.location.app.fragment.notifications;


import static android.content.ContentValues.TAG;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.location.app.model.CampusDataResponce;
import com.location.app.network.ApiClient;
import com.location.app.network.ApiInterface;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class CampusListApiModel implements CampusListPresenterViewModel.Model {



    @Override
    public void getCampusData(Context context ,OnFinishedListener onFinishedListener) {
        ApiInterface apiService = ApiClient.getRetrofitInstance(context).create(ApiInterface.class);

//        Call<CampusDataResponce> stringCall = apiService.getCampusData();
//        stringCall.enqueue(new Callback<CampusDataResponce>() {
//            @Override
//            public void onResponse(Call<CampusDataResponce> call, Response<CampusDataResponce> response) {
//
//                try{
//                    CampusDataResponce campusDataResponce = response.body();
//
//                }
//                catch (Exception e)
//                {
//                    Log.d(TAG, "onResponse: "+e.getMessage());
//                }
//                android.util.Log.d(TAG, "onResponse: "+response.body());
//
//            }
//
//            @Override
//            public void onFailure(Call<CampusDataResponce> call, Throwable t) {
//                android.util.Log.d(TAG, "onFailure: "+t.getMessage());
//
//            }
//        });

        Call<CampusDataResponce> call = apiService.getCampusData();
        android.util.Log.d(TAG, "getCampusData: "+call.toString());
        call.enqueue(new Callback<CampusDataResponce>() {
            @Override
            public void onResponse(@NonNull Call<CampusDataResponce> call, @NonNull Response<CampusDataResponce> response) {
                try {
                    android.util.Log.d(TAG, "onResponse: "+response);
                    if (response.isSuccessful()) {
                     //  System.out.println(response.body().string());//conve
                    CampusDataResponce campusDataResponce = response.body();
                        android.util.Log.d(TAG, "onResponse: "+campusDataResponce);
                    onFinishedListener.onFinishedCampusData(campusDataResponce);
                      //  Toast.makeText(context.getApplicationContext(), "Api Data Received code" + response, Toast.LENGTH_SHORT).show();
                    } else {

                        if (response.code() >= 500) {
                            String mError = new String();
                            mError = response.message();
                            Toast.makeText(context, mError, Toast.LENGTH_LONG).show();
                        } else if (response.code() >= 400) {

                            Toast.makeText(context.getApplicationContext(), "Api Data Responce code" + response, Toast.LENGTH_SHORT).show();

                        }

                    }
                }catch (Exception ex)
                {
                 ex.printStackTrace();
                }

            }

            @Override
            public void onFailure(@NonNull Call<CampusDataResponce> call, Throwable t) {
                // Log error here since request
                Log.d("ERRRRRRRRROR12345", t.getMessage());
                onFinishedListener.onFailure(t);
            }
        });
    }

}
