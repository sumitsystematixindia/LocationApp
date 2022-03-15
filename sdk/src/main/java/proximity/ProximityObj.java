package proximity;

import org.json.JSONObject;

public class ProximityObj {
    private String BSSID = null;
    private String message = null;

    public ProximityObj() {

    }

    public ProximityObj(String bssid, String msg) {
        BSSID = bssid;
        message = msg;
    }

    public ProximityObj(JSONObject json) {

    }

    public String getBSSID() {
        return BSSID;
    }

    public void setBSSID(String bSSID) {
        BSSID = bSSID;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
