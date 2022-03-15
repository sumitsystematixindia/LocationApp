package com.location.app.fragment.notifications;


import android.content.Context;
import com.location.app.model.CampusDataResponce;


public class CampusListPresenter implements CampusListPresenterViewModel.Presenter, CampusListPresenterViewModel.Model.OnFinishedListener {

    private CampusListPresenterViewModel.View campusListView;

    private CampusListPresenterViewModel.Model campusListModel;

    public CampusListPresenter(CampusListPresenterViewModel.View campusListView) {
        this.campusListView = campusListView;
        this.campusListModel = new CampusListApiModel();
    }

    @Override
    public void onDestroy() {
        this.campusListView = null;
    }

    @Override
    public void requestCampusData(Context context) {
        if (campusListView != null) {
            campusListView.showProgress();
        }
        campusListModel.getCampusData(context,this);
    }


    @Override
    public void onFinishedCampusData(CampusDataResponce campusDataResponce) {
        campusListView.setCampusData(campusDataResponce);
        if (campusListView != null) {
            campusListView.hideProgress();
        }
    }

    @Override
    public void onFailure(Throwable t) {
        campusListView.onResponseFailure(t);
        if (campusListView != null) {
            campusListView.hideProgress();
        }
    }
}
