package com.mlins.locator;

public class AssociativeDataSorter {
    public AssociativeData data;
    private double d;


    public AssociativeDataSorter(AssociativeData associativeData, double d2) {
        this.data = associativeData;
        this.setD(d2);
    }

    public int compare(AssociativeDataSorter other) {

        return Double.compare(getD(), other.getD());
    }

    public double getD() {
        return d;
    }

    public void setD(double d) {
        this.d = d;
    }
}
