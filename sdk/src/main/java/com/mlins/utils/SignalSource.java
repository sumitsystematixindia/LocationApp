package com.mlins.utils;


public class SignalSource implements Comparable<SignalSource> {

    private String sourceName = "";
    private double x = 0.0;
    private double y = 0.0;
    private Double level = 0.0;

    public SignalSource(String sourceName, double x, double y, Double level) {
        super();
        this.sourceName = sourceName;
        this.x = x;
        this.y = y;
        this.level = level;
    }


    public String getSourceName() {
        return sourceName;
    }


    public double getX() {
        return x;
    }


    public double getY() {
        return y;
    }


    public Double getLevel() {
        return level;
    }


    @Override
    public int compareTo(SignalSource sigSrc) {
        return sigSrc.level.compareTo(level);
    }


    @Override
    public String toString() {
        return "SignalSource [sourceName=" + sourceName + ", x=" + x + ", y="
                + y + ", level=" + level + "]";
    }


}