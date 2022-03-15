package com.mlins.barometer;

public class BarometerStatus {

    private long timeStamp = 0;
    private int status = 0;

    public BarometerStatus(int status) {
        super();
        this.timeStamp = System.currentTimeMillis();
        this.status = status;
    }

    public BarometerStatus(BarometerStatus lastTrendsStatus) {

        this.timeStamp = lastTrendsStatus.timeStamp;
        this.status = lastTrendsStatus.status;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getTimeStamp() {
        return timeStamp;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (timeStamp ^ (timeStamp >>> 32));
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
        BarometerStatus other = (BarometerStatus) obj;
        if (timeStamp != other.timeStamp)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "BarometerStatus [timeStamp=" + timeStamp + ", status=" + status + "]";
    }

}
