//AddCatch
package com.mlins.aStar;

import com.mlins.utils.gis.GisLine;
import com.mlins.utils.logging.Log;

import java.util.List;

public class GisSegment {
    private static final String TAG = GisSegment.class.getName();
    private GisLine Line = null;
    private int id = 0;
    private double weight;
    private int parent = -1;
    private double g = 0;

    public GisSegment(GisLine l, int sid) {
        setLine(l);
        setId(sid);
        setWeight(calcweight());

    }

    public GisSegment() {

    }

    public double calcweight() {
        double result = 0;
        double p1x = getLine().getPoint1().getX();
        double p1y = getLine().getPoint1().getY();
        double p2x = getLine().getPoint2().getX();
        double p2y = getLine().getPoint2().getY();

        result = Math.sqrt((p1x - p2x) * (p1x - p2x) + (p1y - p2y)
                * (p1y - p2y));

        return result;
    }

    public GisLine getLine() {
        try {
            return Line;
        } catch (Throwable t) {
            Log.getInstance().error(TAG, "--!!!Attention!!!--");
            Log.getInstance().error(TAG, "--public GisLine getLine()--");
            Log.getInstance().error(TAG, t.getMessage(), t);
            Log.getInstance().error(TAG, "--End of Error--");
        }
        return Line;

    }

    public void setLine(GisLine line) {
        Line = line;
    }

    public int getId() {
        try {
            return id;
        } catch (Throwable t) {
            Log.getInstance().error(TAG, "--!!!Attention!!!--");
            Log.getInstance().error(TAG, "--public int getId()--");
            Log.getInstance().error(TAG, t.getMessage(), t);
            Log.getInstance().error(TAG, "--End of Error--");
        }
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getWeight() {
        try {
            return weight;
        } catch (Throwable t) {
            Log.getInstance().error(TAG, "--!!!Attention!!!--");
            Log.getInstance().error(TAG, "--public double getWeight()--");
            Log.getInstance().error(TAG, t.getMessage(), t);
            Log.getInstance().error(TAG, "--End of Error--");
        }
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public int getParent() {
        try {
            return parent;
        } catch (Throwable t) {
            Log.getInstance().error(TAG, "--!!!Attention!!!--");
            Log.getInstance().error(TAG, "--public int getParent()--");
            Log.getInstance().error(TAG, t.getMessage(), t);
            Log.getInstance().error(TAG, "--End of Error--");
        }
        return parent;
    }

    public void setParent(int parent) {
        this.parent = parent;
    }

    public double getG() {
        try {
            return g;
        } catch (Throwable t) {
            Log.getInstance().error(TAG, "--!!!Attention!!!--");
            Log.getInstance().error(TAG, "--public double getG()--");
            Log.getInstance().error(TAG, t.getMessage(), t);
            Log.getInstance().error(TAG, "--End of Error--");
        }
        return g;
    }

    public void setG(double g) {
        this.g = g;
    }

    @Override
    public String toString() {
        try {
            return "Segment:" + Line + ": ID(" + id + "), parent(" + parent
                    + "),G(" + g + ")";
        } catch (Throwable t) {
            Log.getInstance().error(TAG, "--!!!Attention!!!--");
            Log.getInstance().error(TAG, "--public String toString()--");
            Log.getInstance().error(TAG, t.getMessage(), t);
            Log.getInstance().error(TAG, "--End of Error--");
        }
        return "Segment:" + Line + ": ID(" + id + "), parent(" + parent
                + "),G(" + g + ")";
    }

    List<GisSegment> getNeighbours() {
        throw new IllegalStateException("Use aStarSegment to get neighbours for segment");
    }


}
