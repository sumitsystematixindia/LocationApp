package com.location.app.activity.addbeacon;

import android.content.Context;

import com.google.gson.JsonObject;
import com.location.app.fragment.notifications.CampusListPresenterViewModel;
import com.location.app.model.BeaconDataResponse;
import com.location.app.model.CampusDataResponce;

public interface AddBeaconPresenterViewModel {

    interface Model {

        interface OnFinishedListener {

            void onFinishedBeaconData(BeaconDataResponse beaconDataResponce);

            void onFailure(Throwable t);
        }


        void addBeacon(Context context ,  OnFinishedListener onFinishedListener, JsonObject jsonObject);
    }

    interface View {

        void showProgress();

        void hideProgress();

        void onResponseFailure(Throwable throwable);
        void setBeaconData(BeaconDataResponse beaconDataResponse);

    }

    interface Presenter {

        void onDestroy();

        void requestBeaconData(Context context, JsonObject jsonObject);


    }
}
