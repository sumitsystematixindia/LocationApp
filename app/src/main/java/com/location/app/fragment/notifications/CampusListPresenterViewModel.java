package com.location.app.fragment.notifications;


import android.content.Context;


import com.location.app.model.CampusDataResponce;

public interface CampusListPresenterViewModel {

    interface Model {

        interface OnFinishedListener {

            void onFinishedCampusData(CampusDataResponce campusDataResponce);

            void onFailure(Throwable t);
        }


        void getCampusData(Context context ,OnFinishedListener onFinishedListener);
    }

    interface View {

        void showProgress();

        void hideProgress();

        void onResponseFailure(Throwable throwable);
        void setCampusData(CampusDataResponce campusDataResponce);

    }

    interface Presenter {

        void onDestroy();


        void requestCampusData(Context context);

    }
}
