package com.spreo.sdk.interfaces;

public interface SDKMsgsListener {

    /**
     * this method will be called when GPS provider status changed
     *
     * @param isEnabled
     */
    void onGPSproviderStatusChanged(boolean isEnabled);
    //void onBluetoothProviderDisabled();

}
