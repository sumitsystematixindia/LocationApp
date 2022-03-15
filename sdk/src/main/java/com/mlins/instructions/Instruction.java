package com.mlins.instructions;

import com.mlins.aStar.GisSegment;
import com.mlins.utils.gis.Location;
import com.spreo.nav.interfaces.INavInstruction;

import java.util.List;

public interface Instruction {

    public static final int TYPE_SWITCH_FLOOR = 1;
    public static final int TYPE_DESTINATION = 2;
    public static final int TYPE_STRAIGHT = 3;
    public static final int TYPE_EXIT = 4;

    List<Integer> getImage();

    void addImage(int image);

    List<Integer> getText();

    void addText(int text);

    List<String> getSound();

    void addSound(String sound);

    Location getLocation();

    void setLocation(Location location);

    GisSegment getSegment();

    void setSegment(GisSegment segment);

    boolean hasPlayed();

    void setPlayed(boolean played);

    void addInstruction(Instruction instruction);

    List<Integer> getSingleImage();

    List<String> getSingleSound();

    List<Instruction> getInstructions();

    int getType();

    void setType(int type);

    String getPoiName();

    void setPoiName(String poiName);

    String getTofloor();

    void setTofloor(String floor);

    boolean hasPreInstructionPlayed();

    void setPreInstructionPlayed(boolean played);

    int getID();

    void setID(int id);

    void setDestinationName(String name);

    double getDistance();

    String getToFacilty();

    void setToFacilty(String toFacilty);

    NavInstruction getSimplifiedInstruction();

    void setSimplifiedInstruction(NavInstruction instruction);

}
