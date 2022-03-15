package com.mlins.utils;

import com.mlins.locator.AssociativeData;
import com.mlins.locator.AssociativeDataSorter;

public class Associativefloor extends AssociativeDataSorter {

    private double z;

    public Associativefloor(AssociativeData associativeData, double d2) {
        super(associativeData, d2);
        // TODO Auto-generated constructor stub
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }

}
