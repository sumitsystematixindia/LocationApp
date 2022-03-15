package com.mlins.switchfloor;

import android.graphics.PointF;

import java.util.ArrayList;
import java.util.List;

public class SwitchFloorObj {
    private PointF Point;
    private int Z;
    private int Id;
    private String Description;
    private String Type;
    private List<Integer> FromFloor = new ArrayList<Integer>();
    private List<Integer> ToFloor = new ArrayList<Integer>();
    private int minFloorDifference;
    private int goingToFloor;

    public SwitchFloorObj() {

    }


    public void parse(String line) {
        String[] fields = line.split("\t");
        Point = new PointF();
        Point.x = (Float.parseFloat(fields[1]));
        Point.y = (Float.parseFloat(fields[2]));
        Z = Integer.parseInt(fields[3]);
        Id = Integer.parseInt(fields[4]);
        Description = fields[5];
        Type = fields[6];
        String[] vals;
        String from = fields[7];
        vals = from.split(",");
        for (int i = 0; i < vals.length; i++) {
            if (!vals[i].equals("")) {
                FromFloor.add(Integer.parseInt(vals[i]));
            }
        }
        String to = fields[8];
        vals = to.split(",");
        for (int i = 0; i < vals.length; i++) {
            if (!vals[i].equals("")) {
                ToFloor.add(Integer.parseInt(vals[i]));
            }
        }
    }


    public PointF getPoint() {
        return Point;
    }

    public void setPoint(PointF point) {
        Point = point;
    }

    public void setPoint(float px, float py) {
        Point = new PointF(px, py);
    }

    public int getZ() {
        return Z;
    }

    public void setZ(int z) {
        Z = z;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        this.Id = id;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public List<Integer> getFromFloor() {
        return FromFloor;
    }

    public void setFromFloor(String text) {
        FromFloor.clear();
        String[] vals;
        vals = text.split(",");
        for (int i = 0; i < vals.length; i++) {
            FromFloor.add(Integer.parseInt(vals[i]));
        }

    }

    public void setFromFloor(List<Integer> fromFloor) {
        FromFloor = fromFloor;
    }

    public List<Integer> getToFloor() {
        return ToFloor;
    }

    public void setToFloor(String text) {
        ToFloor.clear();
        String[] vals;
        vals = text.split(",");
        for (int i = 0; i < vals.length; i++) {
            ToFloor.add(Integer.parseInt(vals[i]));
        }

    }

    public void setToFloor(List<Integer> toFloor) {
        ToFloor = toFloor;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }


    public int getMinFloorDifference() {
        return minFloorDifference;
    }


    public void setMinFloorDifference(int minFloorDifference) {
        this.minFloorDifference = minFloorDifference;
    }


    public int getGoingToFloor() {
        return goingToFloor;
    }


    public void setGoingToFloor(int goingToFloor) {
        this.goingToFloor = goingToFloor;
    }

}
