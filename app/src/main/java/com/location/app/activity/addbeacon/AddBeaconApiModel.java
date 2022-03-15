package com.location.app.activity.addbeacon;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.location.app.model.BeaconDataResponse;
import com.location.app.model.CampusDataResponce;
import com.location.app.network.ApiClient;
import com.location.app.network.ApiInterface;
import com.mlins.utils.logging.Log;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddBeaconApiModel implements  AddBeaconPresenterViewModel.Model{
    @Override
    public void addBeacon(Context context, OnFinishedListener onFinishedListener, JsonObject jsonObject) {
        ApiInterface apiService = ApiClient.getRetrofitInstance(context).create(ApiInterface.class);


        Call<BeaconDataResponse> call = apiService.addBeaconData(jsonObject);
        call.enqueue(new Callback<BeaconDataResponse>() {


            @Override
            public void onResponse(@NonNull Call<BeaconDataResponse> call, @NonNull Response<BeaconDataResponse> response) {
                try {
                    if (response.isSuccessful()) {
                        BeaconDataResponse beaconDataResponse = response.body();
                        onFinishedListener.onFinishedBeaconData(beaconDataResponse);

                    } else {

                        if (response.code() >= 500) {
                            Gson gson = new GsonBuilder().create();
                            String mError = new String();
                            mError = response.message();
                            Toast.makeText(context, mError, Toast.LENGTH_LONG).show();
                        } else if (response.code() >= 400) {

                           // Toast.makeText(context.getApplicationContext(), "Api Data Responce code" + response, Toast.LENGTH_SHORT).show();

                        }

                    }
                }catch (Exception ex)
                {
                    ex.printStackTrace();
                }

            }

            @Override
            public void onFailure(@NonNull Call<BeaconDataResponse> call, Throwable t) {
                // Log error here since request
                Log.d("ERRRRRRRRROR12345", t.toString());
                onFinishedListener.onFailure(t);
            }
        });
    }
}
