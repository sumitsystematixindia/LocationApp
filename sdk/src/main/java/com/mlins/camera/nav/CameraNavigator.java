package com.mlins.camera.nav;

import android.opengl.GLSurfaceView;
import android.os.Handler;
import android.widget.FrameLayout;

import com.mlins.utils.Cleanable;
import com.mlins.utils.Lookup;
import com.spreo.geofence.GeoFenceRect;

public class CameraNavigator implements Cleanable {

    // ======Camera Nav
    private CameraPreview mCameraNavPreview = null;
    private GLSurfaceView mCameraNavGLView = null;
    private direction mCameraNavDirection = null;
    private FrameLayout cameraNavLayout = null;
    private float mCameraNavAngle = 0;
    private Handler mCameraNavHandler = null;
    private boolean isCameraNavState = false;
    private GeoFenceRect currentElevatorZone = null;
    private int lastSelectedFloor = -100;
    // ======Camera Nav

    public CameraNavigator() {
        super();
    }

    public static CameraNavigator getInstance() {
        return Lookup.getInstance().get(CameraNavigator.class);
    }

    public static void releaseInstance() {
        Lookup.getInstance().remove(CameraNavigator.class);
    }

    public void clean() {
        mCameraNavPreview = null;
        mCameraNavGLView = null;
        mCameraNavDirection = null;
        cameraNavLayout = null;
        mCameraNavAngle = 0;
        mCameraNavHandler = null;
        isCameraNavState = false;
        currentElevatorZone = null;
        lastSelectedFloor = -100;
    }

    public CameraPreview getmCameraNavPreview() {
        return mCameraNavPreview;
    }

    public void setmCameraNavPreview(CameraPreview mCameraNavPreview) {
        this.mCameraNavPreview = mCameraNavPreview;
    }

    public GLSurfaceView getmCameraNavGLView() {
        return mCameraNavGLView;
    }

    public void setmCameraNavGLView(GLSurfaceView mCameraNavGLView) {
        this.mCameraNavGLView = mCameraNavGLView;
    }

    public direction getmCameraNavDirection() {
        return mCameraNavDirection;
    }

    public void setmCameraNavDirection(direction mCameraNavDirection) {
        this.mCameraNavDirection = mCameraNavDirection;
    }

    public FrameLayout getCameraNavLayout() {
        return cameraNavLayout;
    }

    public void setCameraNavLayout(FrameLayout cameraNavLayout) {
        this.cameraNavLayout = cameraNavLayout;
    }

    public float getmCameraNavAngle() {
        return mCameraNavAngle;
    }

    public void setmCameraNavAngle(float mCameraNavAngle) {
        this.mCameraNavAngle = mCameraNavAngle;
    }

    public Handler getmCameraNavHandler() {
        return mCameraNavHandler;
    }

    public void setmCameraNavHandler(Handler mCameraNavHandler) {
        this.mCameraNavHandler = mCameraNavHandler;
    }

    public boolean isCameraNavState() {
        return isCameraNavState;
    }

    public void setCameraNavState(boolean isCameraNavState) {
        this.isCameraNavState = isCameraNavState;
    }

    public GeoFenceRect getCurrentElevatorZone() {
        return currentElevatorZone;
    }

    public void setCurrentElevatorZone(GeoFenceRect currentElevatorZone) {
        this.currentElevatorZone = currentElevatorZone;
    }

    public int getLastSelectedFloor() {
        return lastSelectedFloor;
    }

    public void setLastSelectedFloor(int lastSelectedFloor) {
        this.lastSelectedFloor = lastSelectedFloor;
    }


}
