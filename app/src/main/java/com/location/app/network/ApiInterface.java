package com.location.app.network;


import com.google.gson.JsonObject;
import com.location.app.model.BeaconDataResponse;
import com.location.app.model.CampusDataResponce;
import com.location.app.utils.Constants;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;


public interface ApiInterface {


    //------PRODUCT LIST API-----//
    @GET(Constants.GETCAMPUSDATA)
    Call<CampusDataResponce> getCampusData();
  //  Call<String> getCampusData();

    //------PRODUCT LIST API-----//
    @POST(Constants.ADDBEACON)
    Call<BeaconDataResponse> addBeaconData(@Body JsonObject jsonObject);



}
