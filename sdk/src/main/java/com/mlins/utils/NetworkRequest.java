package com.mlins.utils;

public class NetworkRequest {
    public byte[] result = null;
    String url;
    Object lock;

    public NetworkRequest(String nUrl) {
        this.url = nUrl;


    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Object getLock() {
        return lock;
    }

    public void setLock(Object lock) {
        this.lock = lock;
    }

}
