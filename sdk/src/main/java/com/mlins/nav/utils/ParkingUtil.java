package com.mlins.nav.utils;

import android.util.Log;

import com.mlins.utils.Lookup;
import com.mlins.utils.PropertyHolder;
import com.mlins.utils.gis.Location;
import com.spreo.nav.interfaces.ILocation;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ParkingUtil {

    private ILocation parkingLocation = null;
    private String parkingFileName = "parking.json";

    public static ParkingUtil getInstance() {
        return Lookup.getInstance().get(ParkingUtil.class);
    }

    public static void releaseInstance() {
        Lookup.getInstance().remove(ParkingUtil.class);
    }

    public void load() {
        String jsonString = "";
        File dir = PropertyHolder.getInstance().getProjectDir();
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File pfile = new File(dir, parkingFileName);
        if (!pfile.exists()) {
            return;
        }
        BufferedReader in = null;
        try {
            in = new BufferedReader(new FileReader(pfile));
            String line = null;
            while ((line = in.readLine()) != null) {
                jsonString += line;
            }
        } catch (IOException e) {
            e.toString();
        } finally {
            if (in != null)
                try {
                    in.close();
                } catch (Exception e2) {
                    Log.e("", e2.getMessage());
                    e2.printStackTrace();
                }
        }

        try {
            JSONObject parkingjson = new JSONObject(jsonString);
            Location loc = new Location();
            loc.parse(parkingjson);
            setParkingLocation(loc);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public void save(ILocation location) {
        JSONObject parkingobj = location.getAsJson();
        String parkingstring = parkingobj.toString();
        File dir = PropertyHolder.getInstance().getProjectDir();
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File gfile = new File(dir, parkingFileName);
        if (gfile.exists()) {
            gfile.delete();
        }
        try {
            gfile.createNewFile();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        BufferedWriter out = null;
        try {
            out = new BufferedWriter(new FileWriter(gfile, true));
            out.write(parkingstring);
            out.flush();
        } catch (IOException e) {
            e.toString();
        } finally {
            if (out != null)
                try {
                    out.close();
                    setParkingLocation(location);
                } catch (Exception e2) {
                    Log.e("", e2.getMessage());
                    e2.printStackTrace();
                }
        }
    }

    public void delete() {
        File dir = PropertyHolder.getInstance().getProjectDir();
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File gfile = new File(dir, parkingFileName);
        if (gfile.exists()) {
            gfile.delete();
        }
        setParkingLocation(null);
    }

    public ILocation getParkingLocation() {
        return parkingLocation;
    }

    private void setParkingLocation(ILocation parkingLocation) {
        this.parkingLocation = parkingLocation;
    }
}
