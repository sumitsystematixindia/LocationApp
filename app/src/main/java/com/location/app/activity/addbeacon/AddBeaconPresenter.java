package com.location.app.activity.addbeacon;

import android.content.Context;

import com.google.gson.JsonObject;

import com.location.app.model.BeaconDataResponse;


public class AddBeaconPresenter  implements  AddBeaconPresenterViewModel.Presenter,AddBeaconPresenterViewModel.Model.OnFinishedListener{

    private AddBeaconPresenterViewModel.View addBeaconView;

    private AddBeaconPresenterViewModel.Model addBeaconModel;

    public AddBeaconPresenter(AddBeaconPresenterViewModel.View addBeaconView) {
        this.addBeaconView = addBeaconView;
        this.addBeaconModel = new AddBeaconApiModel();
    }

    @Override
    public void onDestroy() {
        this.addBeaconView = null;
    }


    @Override
    public void requestBeaconData(Context context, JsonObject data) {
        if (addBeaconView != null) {
            addBeaconView.showProgress();
        }
        addBeaconModel.addBeacon(context,this,data);
    }


    @Override
    public void onFinishedBeaconData(BeaconDataResponse beaconDataResponse) {
        addBeaconView.setBeaconData(beaconDataResponse);
        if (addBeaconView != null) {
            addBeaconView.hideProgress();
        }
    }

    @Override
    public void onFailure(Throwable t) {
        addBeaconView.onResponseFailure(t);
        if (addBeaconView != null) {
            addBeaconView.hideProgress();
        }
    }
}
