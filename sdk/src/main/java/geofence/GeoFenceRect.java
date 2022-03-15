package geofence;

import android.graphics.RectF;

public class GeoFenceRect extends GeoFenceObject {
    private String name = null;
    private RectF zone = null;
    private Integer z = null;
    private String id = null;

    public GeoFenceRect() {

    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean parse(String line) {
        boolean result = false;
        String[] vals = line.split("\t");
        Float left = null;
        Float top = null;
        Float right = null;
        Float bottom = null;
        if (vals.length >= 2 && vals[1] != null) {
            name = vals[1];
        }
        if (vals.length >= 3 && vals[2] != null) {
            String[] rect = vals[2].split(",");
            if (rect.length == 4) {
                if (rect[0] != null) {
                    left = Float.parseFloat(rect[0]);
                }
                if (rect[1] != null) {
                    top = Float.parseFloat(rect[1]);
                }
                if (rect[2] != null) {
                    right = Float.parseFloat(rect[2]);
                }
                if (rect[3] != null) {
                    bottom = Float.parseFloat(rect[3]);
                }

                if (left != null && top != null && right != null
                        && bottom != null) {
                    zone = new RectF(left, top, right, bottom);
                }
            }
        }
        if (vals.length >= 4 && vals[3] != null) {
            z = Integer.parseInt(vals[3]);
        }

        if (vals.length >= 5 && vals[4] != null) {
            id = vals[4];
        }

        if (name != null && zone != null && id != null) {
            result = true;
        }
        return result;
    }

    @Override
    public boolean isContains(float x, float y) {
        boolean result = false;
        if (zone != null && zone.contains(x, y)) {
            result = true;
        }
        return result;
    }

    @Override
    public Integer getZ() {
        return z;
    }

    public void setZ(Integer z) {
        this.z = z;
    }

    public RectF getZone() {
        return zone;
    }

    public void setZone(RectF zone) {
        this.zone = zone;
    }

    @Override
    public String getId() {
        return id;
    }


}
