package com.spreo.geofence;


public class Beacon {


    private String id = null;
    private String uuid = "UNKNOWN";
    private int major = -1;
    private int minor = -1;


    public Beacon() {
        super();
    }


    public Beacon(String id, String uuid, int major, int minor) {
        super();
        this.id = id;
        this.uuid = uuid;
        this.major = major;
        this.minor = minor;
    }


    public String getUuid() {
        return uuid;
    }


    public void setUuid(String uuid) {
        this.uuid = uuid;
    }


    public int getMajor() {
        return major;
    }


    public void setMajor(int major) {
        this.major = major;
    }


    public int getMinor() {
        return minor;
    }


    public void setMinor(int minor) {
        this.minor = minor;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Beacon other = (Beacon) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }


}
