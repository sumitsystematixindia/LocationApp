package com.mlins.kdtree;

import java.util.ArrayList;
import java.util.Vector;

// K-D Tree node class

class HyperNode<T> {

    protected HyperPoint k;
    protected HyperNode<T> left, right;
    protected boolean deleted;
    T v;

    private HyperNode(HyperPoint key, T val) {

        k = key;
        v = val;
        left = null;
        right = null;
        deleted = false;
    }

    // Method ins  Gonnet & Baeza-Yates
    protected static <T> HyperNode<T> insert(HyperPoint key, T val, HyperNode<T> t, int lev, int K) throws Exception {

        if (t == null) {
            t = new HyperNode<T>(key, val);
        } else if (key.equals(t.k)) {

            // "re-insert"
            if (t.deleted) {
                t.deleted = false;
                t.v = val;
            }

        } else if (key.coordinates[lev] > t.k.coordinates[lev]) {
            t.right = insert(key, val, t.right, (lev + 1) % K, K);
        } else {
            t.left = insert(key, val, t.left, (lev + 1) % K, K);
        }

        return t;
    }

    protected static <T> HyperNode<T> search(HyperPoint key, HyperNode<T> t, int K) {

        for (int lev = 0; t != null; lev = (lev + 1) % K) {

            if (!t.deleted && key.equals(t.k)) {
                return t;
            } else if (key.coordinates[lev] > t.k.coordinates[lev]) {
                t = t.right;
            } else {
                t = t.left;
            }
        }

        return null;
    }

    protected static <T> void rangeSearch(HyperPoint lowk, HyperPoint uppk, HyperNode<T> t, int lev, int K, Vector<HyperNode<T>> v) {

        if (t == null)
            return;
        if (lowk.coordinates[lev] <= t.k.coordinates[lev]) {
            rangeSearch(lowk, uppk, t.left, (lev + 1) % K, K, v);
        }
        int j;
        for (j = 0; j < K && lowk.coordinates[j] <= t.k.coordinates[j] && uppk.coordinates[j] >= t.k.coordinates[j]; j++)
            ;
        if (j == K) {
            //don't add points with tag deleted=true
            if (t.deleted == false) {
                v.add(t);
            }
        }
        if (uppk.coordinates[lev] > t.k.coordinates[lev]) {
            rangeSearch(lowk, uppk, t.right, (lev + 1) % K, K, v);
        }
    }

    // Method Nearest Neighbor from Andrew Moore's thesis.
    protected static <T> void findNearestNeighbor(HyperNode<T> kd, HyperPoint target, HyperRectangle hr, double max_dist_sqd, int lev, int K,
                                                  NearestNeighborsQueue nnl) {

        if (kd == null) {
            return;
        }

        int s = lev % K;

        HyperPoint pivot = kd.k;
        double pivot_to_target = HyperPoint.squredDistance(pivot, target);


        HyperRectangle left_hr = hr;
        HyperRectangle right_hr = (HyperRectangle) hr.clone();
        left_hr.max.coordinates[s] = pivot.coordinates[s];
        right_hr.min.coordinates[s] = pivot.coordinates[s];

        boolean target_in_left = target.coordinates[s] < pivot.coordinates[s];

        HyperNode<T> nearer_kd;
        HyperRectangle nearer_hr;
        HyperNode<T> further_kd;
        HyperRectangle further_hr;


        if (target_in_left) {
            nearer_kd = kd.left;
            nearer_hr = left_hr;
            further_kd = kd.right;
            further_hr = right_hr;
        } else {
            nearer_kd = kd.right;
            nearer_hr = right_hr;
            further_kd = kd.left;
            further_hr = left_hr;
        }


        //findNearestNeighbor(nearer_kd, target, nearer_hr, max_dist_sqd, (lev + 1) % K, K, nnl);
        findNearestNeighbor(nearer_kd, target, nearer_hr, max_dist_sqd, lev + 1, K, nnl);

        //  @SuppressWarnings("unchecked")
        //	HyperNode<T> nearest =  (HyperNode<T>) nnl.getHighestElement();
        double dist_sqd;

        if (!nnl.isFull()) {
            dist_sqd = Double.MAX_VALUE;
        } else {
            dist_sqd = nnl.getMaxPriority();
        }


        max_dist_sqd = Math.min(max_dist_sqd, dist_sqd);


        HyperPoint closest = further_hr.closest(target);
        if (HyperPoint.eucledianDistance(closest, target) < Math.sqrt(max_dist_sqd)) {


            if (pivot_to_target < dist_sqd) {


                //   nearest = kd;


                dist_sqd = pivot_to_target;


                if (!kd.deleted) {
                    nnl.addElement(kd, dist_sqd);
                }


                if (nnl.isFull()) {
                    max_dist_sqd = nnl.getMaxPriority();
                } else {
                    max_dist_sqd = Double.MAX_VALUE;
                }
            }


            findNearestNeighbor(further_kd, target, further_hr, max_dist_sqd, lev + 1, K, nnl);
            //findNearestNeighbor(further_kd, target, further_hr, max_dist_sqd, (lev + 1) % K, K, nnl);

            //   @SuppressWarnings("unchecked")
            //	HyperNode<T> temp_nearest = (HyperNode<T>) nnl.getHighestElement();
            double temp_dist_sqd = nnl.getMaxPriority();


            if (temp_dist_sqd < dist_sqd) {

                //  nearest = temp_nearest;
                dist_sqd = temp_dist_sqd;
            }
        } else if (pivot_to_target < max_dist_sqd) {
            // nearest = kd;
            dist_sqd = pivot_to_target;
        }
    }

    private static String pad(int n) {
        String s = "";
        for (int i = 0; i < n; ++i) {
            s += " ";
        }
        return s;
    }

    public static void hyperRectCopy(HyperRectangle hrect_src, HyperRectangle hrect_dst) {
        hyperPointCopy(hrect_src.min, hrect_dst.min);
        hyperPointCopy(hrect_src.max, hrect_dst.max);
    }

    public static void hyperPointCopy(HyperPoint hpoint_src, HyperPoint hpoint_dst) {
        for (int i = 0; i < hpoint_dst.coordinates.length; ++i) {
            hpoint_dst.coordinates[i] = hpoint_src.coordinates[i];
        }
    }

    protected String toString(int depth) {
        String s = k + "  " + v + (deleted ? "*" : "");
        if (left != null) {
            s = s + "\n" + pad(depth) + "L " + left.toString(depth + 1);
        }
        if (right != null) {
            s = s + "\n" + pad(depth) + "R " + right.toString(depth + 1);
        }
        return s;
    }

    protected void getNode(int depth, ArrayList<T> nodesList) {
        nodesList.add(v);
        if (left != null && deleted == false) {
            left.getNode(depth + 1, nodesList);
        }
        if (right != null && deleted == false) {
            right.getNode(depth + 1, nodesList);
        }
    }
}
