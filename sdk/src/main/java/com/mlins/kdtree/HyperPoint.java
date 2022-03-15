package com.mlins.kdtree;


/**
 * Hyper-Point class supporting KDTree class
 */
class HyperPoint {

    protected double[] coordinates;

    protected HyperPoint(int n) {
        coordinates = new double[n];
    }

    protected HyperPoint(double[] x) {

        coordinates = new double[x.length];
        for (int i = 0; i < x.length; ++i)
            coordinates[i] = x[i];
    }

    protected static double squredDistance(HyperPoint x, HyperPoint y) {

        double dist = 0;

        for (int i = 0; i < x.coordinates.length; ++i) {
            double diff = (x.coordinates[i] - y.coordinates[i]);
            dist += (diff * diff);
        }

        return dist;

    }

    protected static double eucledianDistance(HyperPoint x, HyperPoint y) {

        return Math.sqrt(squredDistance(x, y));
    }

    protected Object clone() {

        return new HyperPoint(coordinates);
    }

    protected boolean equals(HyperPoint p) {

        for (int i = 0; i < coordinates.length; ++i)
            if (coordinates[i] != p.coordinates[i])
                return false;

        return true;
    }

    public String toString() {
        String s = "";
        for (int i = 0; i < coordinates.length; ++i) {
            s = s + coordinates[i] + " ";
        }
        return s;
    }

}
