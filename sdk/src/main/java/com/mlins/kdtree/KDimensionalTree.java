package com.mlins.kdtree;

import android.graphics.PointF;

import com.mlins.utils.gis.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * WIKIPEDIA: In computer science, a k-d tree (short for k-dimensional tree) is
 * a space-partitioning data structure for organizing points in a k-dimensional
 * space. k-d trees are a useful data structure for several applications, such
 * as searches involving a multidimensional search key (e.g. range searches and
 * nearest neighbor searches). k-d trees are a special case of binary space
 * partitioning trees.
 */
public class KDimensionalTree<T> {

    private int m_K;

    private HyperNode<T> m_root;

    private int m_count;

    /**
     * Creates a KD-tree with  k.
     *
     * @param k number of dimensions
     */
    public KDimensionalTree(int k) {

        m_K = k;
        m_root = null;
    }

    /**
     * Creates a KD-tree for two dimensions.
     */
    public KDimensionalTree() {
        m_K = 2;
        m_root = null;
    }


    /**
     * Insert element. Uses algorithm described in author = {G.H.
     * Gonnet and R. Baeza-Yates}, title = {Handbook of Algorithms and Data
     * Structures},
     */
    public void addElement(double[] key, T value) throws Exception {

        if (key.length != m_K) {
            throw new Exception("KeySize EXP");
        } else
            try {
                m_root = HyperNode.insert(new HyperPoint(key), value, m_root,
                        0, m_K);
            } catch (Exception e) {
                throw new Exception("KeyDuplicate EXP");
            }

        m_count++;
    }


    /**
     * Insert a element. Uses algorithm described in author = {G.H.
     * Gonnet and R. Baeza-Yates}, title = {Handbook of Algorithms and Data
     * Structures},
     * Works only in case k=2 (for two-dimentions case)
     */
    public void addElement(PointF ptKey, T value) throws Exception {

        if (ptKey == null)
            throw new Exception("ptKey is NULL EXP");

        double[] key = {ptKey.x, ptKey.y};

        if (key.length != m_K) {
            throw new Exception("KeySize EXP");
        } else
            try {
                m_root = HyperNode.insert(new HyperPoint(key), value, m_root,
                        0, m_K);
            } catch (Exception e) {
                throw new Exception("KeyDuplicate EXP");
            }

        m_count++;
    }


    public void addElement(Location ptKey, T value) throws Exception {

        if (ptKey == null)
            throw new Exception("ptKey is NULL EXP");

        double[] key = {ptKey.getX(), ptKey.getY(), ptKey.getZ()};

        if (key.length != m_K) {
            throw new Exception("KeySize EXP");
        } else
            try {
                m_root = HyperNode.insert(new HyperPoint(key), value, m_root,
                        0, m_K);
            } catch (Exception e) {
                throw new Exception("KeyDuplicate EXP");
            }

        m_count++;
    }


    /**
     * Find  element whose key is equal to key. Uses algorithm of Gonnet
     * & Baeza-Yates. (works only for k=2)
     */
    public T search(double[] key) throws Exception {

        if (key.length != m_K) {
            throw new Exception("KeySize");
        }

        HyperNode<T> kd = HyperNode.search(new HyperPoint(key), m_root, m_K);

        if (kd == null)
            return null;

        return (T) kd.v;
        //return (kd == null ? null : kd.v);
    }


    /**
     * Find element whose key is equal to key. Uses algorithm of Gonnet
     * & Baeza-Yates.
     */
    public T search(PointF ptKey) throws Exception {

        if (ptKey == null)
            throw new Exception("ptKey is NULL EXP");

        double[] key = {ptKey.x, ptKey.y};

        if (key.length != m_K) {
            throw new Exception("KeySize");
        }

        HyperNode<T> kd = HyperNode.search(new HyperPoint(key), m_root, m_K);

        if (kd == null)
            return null;

        return (T) kd.v;
        //return (kd == null ? null : kd.v);
    }

    public T search(Location ptKey) throws Exception {

        if (ptKey == null)
            throw new Exception("ptKey is NULL EXP");

        double[] key = {ptKey.getX(), ptKey.getY(), ptKey.getZ()};

        if (key.length != m_K) {
            throw new Exception("KeySize");
        }

        HyperNode<T> kd = HyperNode.search(new HyperPoint(key), m_root, m_K);

        if (kd == null)
            return null;

        return (T) kd.v;
        //return (kd == null ? null : kd.v);
    }

    /**
     * Delete an element. Instead of  deleting element and
     * rebuilding tree, marks element as deleted.
     */
    public void delete(double[] key) throws Exception {

        if (key.length != m_K) {
            throw new Exception("KeySizeException");
        } else {

            HyperNode<T> t = HyperNode.search(new HyperPoint(key), m_root, m_K);
            if (t == null) {
                throw new Exception("KeyMissing");
            } else {
                t.deleted = true;
            }

            m_count--;
        }
    }


    /**
     * Delete an element
     */
    public void delete(PointF ptKey) throws Exception {

        double[] key = {ptKey.x, ptKey.y};

        if (key.length != m_K) {
            throw new Exception("KeySizeException");
        } else {

            HyperNode<T> t = HyperNode.search(new HyperPoint(key), m_root, m_K);
            if (t == null) {
                throw new Exception("KeyMissing");
            } else {
                t.deleted = true;
            }

            m_count--;
        }
    }


    public void delete(Location ptKey) throws Exception {

        double[] key = {ptKey.getX(), ptKey.getY(), ptKey.getZ()};

        if (key.length != m_K) {
            throw new Exception("KeySizeException");
        } else {

            HyperNode<T> t = HyperNode.search(new HyperPoint(key), m_root, m_K);
            if (t == null) {
                throw new Exception("KeyMissing");
            } else {
                t.deleted = true;
            }

            m_count--;
        }
    }


    /**
     * Find element whose key is nearest neighbor to key. Implements the
     * Nearest Neighbor algorithm of author = Andrew Moore, title = An
     * introductory tutorial on kd-trees,
     */

    public T nearest(double[] key) throws Exception {

        T[] nbrs = nearest(key, 1);
        return (T) nbrs[0];
    }


    /**
     * Find  element whose key is nearest neighbor to key.  (works only for k=2)
     */
    @SuppressWarnings("unchecked")
    public T nearest(PointF ptKey) throws Exception {

        if (ptKey == null)
            throw new Exception("ptKey is NULL EXP");

        double[] key = {ptKey.x, ptKey.y};

        Object[] nbrs = nearest(key, 1);
        return (T) nbrs[0];
    }

    @SuppressWarnings("unchecked")
    public T nearest(Location ptKey) throws Exception {

        if (ptKey == null)
            throw new Exception("ptKey is NULL EXP");

        double[] key = {ptKey.getX(), ptKey.getY(), ptKey.getZ()};

        Object[] nbrs = nearest(key, 1);
        return (T) nbrs[0];
    }


    /**
     * Find element whose keys are N nearest neighbors to key. Uses
     * algorithm above (Neighbors are returned in ascending order of distance to
     * key).
     */
    @SuppressWarnings("unchecked")
    public T[] nearest(double[] key, int n) throws Exception,
            IllegalArgumentException {

        if (n < 0 || n > m_count) {
            throw new IllegalArgumentException("Number of neighbors cannot"
                    + " be negative or greater than number of nodes");
        }

        if (key.length != m_K) {
            throw new Exception("KeySize");
        }

        Object[] nbrs = new Object[n];
        NearestNeighborsQueue nnl = new NearestNeighborsQueue(n);

        HyperRectangle hr = HyperRectangle.infiniteHRect(key.length);
        double max_dist_sqd = Double.MAX_VALUE;
        HyperPoint keyp = new HyperPoint(key);

        HyperNode.findNearestNeighbor(m_root, keyp, hr, max_dist_sqd, 0, m_K,
                nnl);

        for (int i = 0; i < n; ++i) {
            HyperNode<T> kd = (HyperNode<T>) nnl.removeHighestElement();
            nbrs[n - i - 1] = kd.v;
        }

        return (T[]) nbrs;
    }


    /**
     * Find element whose keys are n nearest neighbors to key.
     * (works for k=2)
     */
    @SuppressWarnings("unchecked")
    public List<T> nearest(PointF ptKey, int n) throws Exception,
            IllegalArgumentException {

        if (ptKey == null)
            throw new Exception("ptKey is NULL EXP");

        double[] key = {ptKey.x, ptKey.y};


        if (n < 0) {
            throw new IllegalArgumentException("Number of neighbors cannot"
                    + " be negative or greater than number of nodes");
        } else if (n > m_count) {
            n = m_count;
        }

        if (key.length != m_K) {
            throw new Exception("KeySize");
        }

        Object[] nbrs = new Object[n];
        NearestNeighborsQueue nnl = new NearestNeighborsQueue(n);

        HyperRectangle hr = HyperRectangle.infiniteHRect(key.length);
        double max_dist_sqd = Double.MAX_VALUE;
        HyperPoint keyp = new HyperPoint(key);

        HyperNode.findNearestNeighbor(m_root, keyp, hr, max_dist_sqd, 0, m_K,
                nnl);

        for (int i = 0; i < n; ++i) {
            HyperNode<T> kd = (HyperNode<T>) nnl.removeHighestElement();
            nbrs[n - i - 1] = kd.v;
        }

        List<T> res = new ArrayList<T>();
        for (int i = 0; i < nbrs.length; i++) {
            res.add((T) nbrs[i]);
        }
        return res;
        //return (T[])nbrs;
    }


    @SuppressWarnings("unchecked")
    public List<T> nearest(Location ptKey, int n) throws Exception,
            IllegalArgumentException {

        if (ptKey == null)
            throw new Exception("ptKey is NULL EXP");

        double[] key = {ptKey.getX(), ptKey.getY(), ptKey.getZ()};

        if (n < 0 || n > m_count) {
            throw new IllegalArgumentException("Number of neighbors cannot"
                    + " be negative or greater than number of nodes");
        }

        if (key.length != m_K) {
            throw new Exception("KeySize");
        }

        Object[] nbrs = new Object[n];
        NearestNeighborsQueue nnl = new NearestNeighborsQueue(n);

        HyperRectangle hr = HyperRectangle.infiniteHRect(key.length);
        double max_dist_sqd = Double.MAX_VALUE;
        HyperPoint keyp = new HyperPoint(key);

        HyperNode.findNearestNeighbor(m_root, keyp, hr, max_dist_sqd, 0, m_K,
                nnl);

        for (int i = 0; i < n; ++i) {
            HyperNode<T> kd = (HyperNode<T>) nnl.removeHighestElement();
            nbrs[n - i - 1] = kd.v;
        }

        List<T> res = new ArrayList<T>();
        for (int i = 0; i < nbrs.length; i++) {
            res.add((T) nbrs[i]);
        }
        return res;

        //return (T[])nbrs;
    }


    /**
     * Range search. Uses algorithm of Gonnet & Baeza-Yates.
     */
    public ArrayList<T> getObjectsInRange(double[] lowk, double[] uppk) throws Exception {

        if (lowk.length != uppk.length) {
            throw new Exception("KeySize");
        } else if (lowk.length != m_K) {
            throw new Exception("KeySize");
        } else {
            Vector<HyperNode<T>> v = new Vector<HyperNode<T>>();
            HyperNode.rangeSearch(new HyperPoint(lowk), new HyperPoint(uppk),
                    m_root, 0, m_K, v);
            ArrayList<T> o = new ArrayList<T>(v.size());
            for (int i = 0; i < v.size(); ++i) {
                HyperNode<T> n = v.elementAt(i);
                o.add(i, n.v);
            }

            return o;
        }
    }


    /**
     * Range search. Uses algorithm of Gonnet & Baeza-Yates.
     * Works only in case of two-dimensions  (i.e., k=2)
     */
    public ArrayList<T> getObjectsInRange(PointF lowPt, PointF uppPt) throws Exception {

        if (lowPt == null || uppPt == null)
            throw new Exception("lowPt is Null or uppPt is Null");

        double[] lowk = {lowPt.x, lowPt.y};
        double[] uppk = {uppPt.x, uppPt.y};

        if (lowk.length != uppk.length) {
            throw new Exception("KeySize");
        } else if (lowk.length != m_K) {
            throw new Exception("KeySize");
        } else {
            Vector<HyperNode<T>> v = new Vector<HyperNode<T>>();
            HyperNode.rangeSearch(new HyperPoint(lowk), new HyperPoint(uppk),
                    m_root, 0, m_K, v);
            ArrayList<T> o = new ArrayList<T>(v.size());
            for (int i = 0; i < v.size(); ++i) {
                HyperNode<T> n = v.elementAt(i);
                o.add(i, n.v);
            }

            return o;
        }
    }


    public ArrayList<T> getObjectsInRange(Location lowPt, Location uppPt) throws Exception {

        if (lowPt == null || uppPt == null)
            throw new Exception("lowPt is Null or uppPt is Null");

        double[] lowk = {lowPt.getX(), lowPt.getY(), lowPt.getZ()};
        double[] uppk = {uppPt.getX(), uppPt.getY(), uppPt.getZ()};

        if (lowk.length != uppk.length) {
            throw new Exception("KeySize");
        } else if (lowk.length != m_K) {
            throw new Exception("KeySize");
        } else {
            Vector<HyperNode<T>> v = new Vector<HyperNode<T>>();
            HyperNode.rangeSearch(new HyperPoint(lowk), new HyperPoint(uppk),
                    m_root, 0, m_K, v);
            ArrayList<T> o = new ArrayList<T>(v.size());
            for (int i = 0; i < v.size(); ++i) {
                HyperNode<T> n = v.elementAt(i);
                o.add(i, n.v);
            }

            return o;
        }
    }

    public String toString() {
        return m_root.toString(0);
    }

    public ArrayList<T> getAllNodes() {
        ArrayList<T> nodes = new ArrayList<T>();
        if (m_root != null) {
            m_root.getNode(0, nodes);
        }
        return nodes;
    }

}
