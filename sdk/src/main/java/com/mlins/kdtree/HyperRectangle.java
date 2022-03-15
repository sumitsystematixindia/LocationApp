package com.mlins.kdtree;

/**
 * Hyper-Rectangle class supporting KDTree class
 */
class HyperRectangle {

    protected HyperPoint min;
    protected HyperPoint max;

    protected HyperRectangle(int ndims) {
        min = new HyperPoint(ndims);
        max = new HyperPoint(ndims);
    }

    protected HyperRectangle(HyperPoint vmin, HyperPoint vmax) {

        min = (HyperPoint) vmin.clone();
        max = (HyperPoint) vmax.clone();
    }

    protected static HyperRectangle infiniteHRect(int d) {

        HyperPoint vmin = new HyperPoint(d);
        HyperPoint vmax = new HyperPoint(d);

        for (int i = 0; i < d; ++i) {
            vmin.coordinates[i] = Double.NEGATIVE_INFINITY;
            vmax.coordinates[i] = Double.POSITIVE_INFINITY;
        }

        return new HyperRectangle(vmin, vmax);
    }

    protected Object clone() {

        return new HyperRectangle(min, max);
    }

    protected HyperPoint closest(HyperPoint t) {

        HyperPoint p = new HyperPoint(t.coordinates.length);

        for (int i = 0; i < t.coordinates.length; ++i) {
            if (t.coordinates[i] <= min.coordinates[i]) {
                p.coordinates[i] = min.coordinates[i];
            } else if (t.coordinates[i] >= max.coordinates[i]) {
                p.coordinates[i] = max.coordinates[i];
            } else {
                p.coordinates[i] = t.coordinates[i];
            }
        }

        return p;
    }

    protected HyperRectangle intersection(HyperRectangle r) {

        HyperPoint newmin = new HyperPoint(min.coordinates.length);
        HyperPoint newmax = new HyperPoint(min.coordinates.length);

        for (int i = 0; i < min.coordinates.length; ++i) {
            newmin.coordinates[i] = Math.max(min.coordinates[i],
                    r.min.coordinates[i]);
            newmax.coordinates[i] = Math.min(max.coordinates[i],
                    r.max.coordinates[i]);
            if (newmin.coordinates[i] >= newmax.coordinates[i])
                return null;
        }

        return new HyperRectangle(newmin, newmax);
    }

    protected double area() {

        double a = 1;

        for (int i = 0; i < min.coordinates.length; ++i) {
            a *= (max.coordinates[i] - min.coordinates[i]);
        }

        return a;
    }

    public String toString() {
        return min + "\n" + max + "\n";
    }
}
