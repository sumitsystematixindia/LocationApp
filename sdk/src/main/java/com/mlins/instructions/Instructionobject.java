package com.mlins.instructions;

import com.mlins.aStar.GisSegment;
import com.mlins.aStar.aStarData;
import com.mlins.project.Campus;
import com.mlins.project.ProjectConf;
import com.mlins.utils.FacilityConf;
import com.mlins.utils.PropertyHolder;
import com.mlins.utils.ResourceTranslator;
import com.mlins.utils.gis.Location;
import com.spreo.nav.interfaces.INavInstruction;

import java.util.ArrayList;
import java.util.List;

public class Instructionobject implements Instruction {
    Location location = new Location();
    private List<Integer> images = new ArrayList<Integer>();
    private List<Integer> texts = new ArrayList<Integer>();
    private List<String> sounds = new ArrayList<String>();
    private GisSegment segment = new GisSegment();
    private List<Instruction> instructions = new ArrayList<Instruction>();
    private boolean mPlayed = false;
    private int type = 0;
    private String poiName = "";
    private String toFloor = "";
    private boolean preInstructionPlayed = false;
    private int id = -1;
    private String destinationName = "Destination";

    private final double distance;

    private String toFacilty = null;

    private NavInstruction simplifiedInstruction = null;

    /**
     * @param facilityID - facility id for indoor navigation instructions
     */
    public Instructionobject(String facilityID, GisSegment segment) {
        getInstructions().add(this);
        this.segment = segment;
        distance = calcDistance(facilityID, segment);
    }

    public Instructionobject(double totalDistance) {
        getInstructions().add(this);
        this.distance = totalDistance;
    }

    public Instructionobject(List<Instruction> list) {
        this(calcTotalDistance(list));
    }

    private static double calcTotalDistance(List<Instruction> list){
        double result = 0;
        for (Instruction instruction : list) {
            result += instruction.getDistance();
        }
        return result;
    }

    private double calcDistance(String facilityID, GisSegment segment){
        Campus campus = ProjectConf.getInstance().getSelectedCampus();
        if (campus != null) {
            String campusid = campus.getId();
            if (campusid != null) {
                FacilityConf fac = campus.getFacilityConf(facilityID);
                if(fac != null ) {
                    double pixelsToMetter = fac.getFloor((int)segment.getLine().getZ()).getPixelsToMeter();
                    return segment.getLine().getLength() / pixelsToMetter;
                }
            }
        }
        return 0;
    }

    public double getDistance() {
        return distance;
    }

    public List<Integer> getImage() {
        List<Integer> result = new ArrayList<Integer>();
        for (Instruction instruction : getInstructions()) {
            result.addAll(instruction.getSingleImage());
        }
        return result;

    }

    public void addImage(int image) {
        images.add(image);
    }

    public List<Integer> getText() {
        return texts;
    }

    public void addText(int text) {
        texts.add(text);
    }

    public List<String> getSound() {
        List<String> result = new ArrayList<String>();
//		int counter = 0;
//		for (Instruction instruction : instructions) {
//			if (counter > 0) {
//				result.add("and_then");
//			}
//			result.addAll(instruction.getSingleSound());
//			counter++;
//		}

        int len = getInstructions().size();
        if (len > 0) {
            int index = 0;
            for (; index < (len - 1); index++) {
                Instruction instruction = getInstructions().get(index);
                result.addAll(instruction.getSingleSound());
                result.add("and_then");

            }
            Instruction inst = getInstructions().get(index);
            result.addAll(inst.getSingleSound());

        }
        return result;
    }

    public void addSound(String sound) {
        sounds.add(sound);
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public GisSegment getSegment() {
        return segment;
    }

    public void setSegment(GisSegment segment) {
        this.segment = segment;
    }

    public boolean hasPlayed() {
        return mPlayed;
    }

    public void setPlayed(boolean played) {
        mPlayed = played;
    }

    @Override
    public void addInstruction(Instruction instruction) {
        getInstructions().add(instruction);
    }

    @Override
    public List<Integer> getSingleImage() {

        return images;
    }

    @Override
    public List<String> getSingleSound() {

        return sounds;
    }

    public List<Instruction> getInstructions() {
        return instructions;
    }

    public void setInstructions(List<Instruction> instructions) {
        this.instructions = instructions;
    }

    @Override
    public String getPoiName() {
        return poiName;
    }

    @Override
    public void setPoiName(String poiName) {
        this.poiName = poiName;
    }

    @Override
    public int getType() {
        return type;
    }

    @Override
    public void setType(int type) {
        this.type = type;
    }

    @Override
    public String getTofloor() {
        return toFloor;
    }

    @Override
    public void setTofloor(String floor) {
        this.toFloor = floor;
    }

    @Override
    public String toString() {
        String result = PropertyHolder.getInstance().getMlinsContext().getResources().getString(getText().get(0));
        if (type == Instruction.TYPE_SWITCH_FLOOR) {
            result += " " + getTofloor();
            if (PropertyHolder.getInstance().isDisplaySwitchFloorInstructionExtra()) {
                int extraid = ResourceTranslator.getInstance().getTranslatedResourceId("string", "switch_floor_extra");
                String extratxt = PropertyHolder.getInstance().getMlinsContext().getResources().getString(extraid);
                result += " " + extratxt;
            }
        } else if (type == Instruction.TYPE_DESTINATION) {
            Location destpoi = aStarData.getInstance().getPoilocation();
            if (destpoi != null && destpoi.getPoi() != null) {
                String tmptext = destpoi.getPoi().getpoiDescription();
                if (tmptext != null && !tmptext.isEmpty()) {
                    destinationName = tmptext;
                }
            }
//		String instext = destinationName + " " + result;
            String instext = result + " " + destinationName;
            result = instext;
        }

        if (toFacilty != null) {
            result += " " + toFacilty;
        }

        if (poiName != null && !poiName.isEmpty()) {
            int tmptxt = ResourceTranslator.getInstance().getTranslatedResourceId("string", "by_the_for_instruction");
            String suffix = PropertyHolder.getInstance().getMlinsContext().getResources().getString(tmptxt);
            String bythe = suffix + " " + poiName;
            result += " " + bythe;
        }
        return result;
    }

    @Override
    public void setPreInstructionPlayed(boolean played) {
        this.preInstructionPlayed = played;
    }

    @Override
    public boolean hasPreInstructionPlayed() {
        return preInstructionPlayed;
    }

    @Override
    public int getID() {
        return id;
    }

    @Override
    public void setID(int id) {
        this.id = id;
    }

    public String getDestinationName() {
        return destinationName;
    }

    public void setDestinationName(String destinationName) {
        this.destinationName = destinationName;
    }

    public void setImages(List<Integer> list) {
        images = list;
    }

    public void setTexts(List<Integer> list) {
        texts = list;
    }

    public void setSounds(List<String> list) {
        sounds = list;
    }


    public String getToFacilty() {
        return toFacilty;
    }

    public void setToFacilty(String toFacilty) {
        this.toFacilty = toFacilty;
    }

    @Override
    public NavInstruction getSimplifiedInstruction() {
        return simplifiedInstruction;
    }

    @Override
    public void setSimplifiedInstruction(NavInstruction instruction) {
        simplifiedInstruction = instruction;
    }
}
