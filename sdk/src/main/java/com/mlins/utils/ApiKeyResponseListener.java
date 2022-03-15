package com.mlins.utils;

public interface ApiKeyResponseListener {
    final static int SDK_ENABLED = 0;
    final static int ERROR_TYPE_CONNECTION = 1;
    final static int ERROR_TYPE_INVALID_APIKEY = 2;
    final static int NO_RESPONSE = 3;

    public void apiKeyResponse(int response);
}
