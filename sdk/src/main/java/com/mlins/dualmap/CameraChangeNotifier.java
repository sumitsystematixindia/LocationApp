package com.mlins.dualmap;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.spreo.interfaces.CameraChangeListener;

import java.util.ArrayList;

class CameraChangeNotifier {

    private GoogleMap map;

    private ArrayList<CameraChangeListener> listeners = new ArrayList<>();

    void setup(GoogleMap map) {
        this.map = map;
        map.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                notifyListeners(CameraChangeNotifier.this.map.getCameraPosition());
            }
        });
        notifyListeners(map.getCameraPosition());
    }

    void addCameraChangeListener(CameraChangeListener listener){
        if(!listeners.contains(listener))
            listeners.add(listener);
    }

    void removeCameraChangeListener(CameraChangeListener listener){
        listeners.remove(listener);
    }

    void notifyListeners(CameraPosition cameraPosition){
        ArrayList<CameraChangeListener> listeners = new ArrayList<>(this.listeners);
        for (CameraChangeListener listener : listeners) {
            listener.onCameraChange(cameraPosition);
        }
    }

}
