package geofence;

public abstract class GeoFenceObject {
    static public GeoFenceObject getObjectFromLine(String line) {
        GeoFenceObject result = null;
        if (line != null) {
            String[] vals = line.split("\t");
            if (vals.length >= 1 && vals[0].equals("rectf")) {
                result = new GeoFenceRect();
                if (!result.parse(line)) {
                    result = null;
                }
            }
        }
        return result;
    }

    public abstract String getName();

    abstract Integer getZ();

    abstract boolean parse(String line);

    abstract boolean isContains(float x, float y);

    public abstract String getId();
}
