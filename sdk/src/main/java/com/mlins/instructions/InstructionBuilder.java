package com.mlins.instructions;


import android.graphics.PointF;
import android.graphics.RectF;

import com.mlins.aStar.FloorNavigationPath;
import com.mlins.aStar.GisSegment;
import com.mlins.aStar.NavigationPath;
import com.mlins.aStar.aStarData;
import com.mlins.aStar.aStarMath;
import com.mlins.dualmap.RouteCalculationHelper;
import com.mlins.project.Campus;
import com.mlins.project.ProjectConf;
import com.mlins.utils.FacilityConf;
import com.mlins.utils.FacilityContainer;
import com.mlins.utils.Lookup;
import com.mlins.utils.MathUtils;
import com.mlins.utils.PropertyHolder;
import com.mlins.utils.ResourceTranslator;
import com.mlins.utils.gis.GisPoint;
import com.spreo.geofence.GeoFenceHelper;
import com.spreo.geofence.GeoFenceObject;
import com.spreo.geofence.GeoFenceRect;
import com.spreo.nav.enums.LocationMode;
import com.spreo.nav.interfaces.INavInstruction;
import com.spreo.spreosdk.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class InstructionBuilder {

    private List<Instruction> currentInstructions = new ArrayList<Instruction>();
    private Instruction nextInstruction = null;
    private List<Instruction> currentMergedInstructions = new ArrayList<Instruction>();

    private Instruction lastNotifiedInstruction = null;

    public static InstructionBuilder getInstance() {
        return Lookup.getInstance().get(InstructionBuilder.class);
    }

    public static void releaseInstance() {
        Lookup.getInstance().remove(InstructionBuilder.class);
    }


    private String getSoundUri(int floorIndex, FacilityConf fac) {

        String id = null;

        if (fac == null) {
            return id;
        }

        try {

            String soundName = fac.getFloorSoundUri(floorIndex);
            //XXX debug
            id = soundName.split("\\.")[0];

        } catch (Throwable t) {
            t.printStackTrace();
        }

        return id;
    }


    public List<Instruction> getInstractions(NavigationPath nav) {
        getCurrentInstructions().clear();
        lastNotifiedInstruction = null;

        List<FloorNavigationPath> paths = nav.getFullPath();
        List<Instruction> instructions = new ArrayList<Instruction>();
        if (paths == null || paths.size() == 0) {
            setCurrentInstructions(instructions);
            return instructions;
        }

        FacilityConf facility = FacilityContainer.getInstance().getSelected();

        String facid = nav.getFacilityId();

        if (facid != null && !facid.isEmpty()) {

            Campus campus = ProjectConf.getInstance().getSelectedCampus();

            if (campus != null) {

                FacilityConf tmp = campus.getFacilityConf(facid);

                if (tmp != null) {

                    facility = tmp;

                }

            }

        }

        instructions.addAll(getAngleInstractions(paths.get(0), facility));
        int counter = 0;
        for (FloorNavigationPath o : paths) {
            List<GisSegment> path = o.getPath();
            counter++;
            if (paths.size() > counter && paths.get(paths.indexOf(o) + 1) != null && paths.get(paths.indexOf(o) + 1).getPath().size() > 0) {
                FloorNavigationPath Secoendpath = paths.get(paths.indexOf(o) + 1);

                int z1 = (int) o.getZ();
                int z2 = (int) paths.get(paths.indexOf(o) + 1).getZ();
                Instructionobject elavator = new Instructionobject(facility.getId(), path.get(path.size() - 1));
//				elavator.location = aStarData.getInstance().getCurrentPath().getElevator();
                elavator.location.setX(elavator.getSegment().getLine().getPoint2().getX());
                elavator.location.setY(elavator.getSegment().getLine().getPoint2().getY());
                elavator.location.setZ(elavator.getSegment().getLine().getZ());
                elavator.location.setType(LocationMode.INDOOR_MODE);
                elavator.setType(Instruction.TYPE_SWITCH_FLOOR);

                NavInstruction simplified = paths.get(0).getSimplifiedInstruction();
                if (PropertyHolder.getInstance().isSimplifiedInstruction() && simplified != null) {
                    elavator.setSimplifiedInstruction(simplified);
                }

                if (z2 > z1) {
                    int tmptxt = ResourceTranslator.getInstance().getTranslatedResourceId("string", "elvator_up");
                    elavator.addImage(R.drawable.go_up);
                    elavator.addText(tmptxt);
//					if (z2 > 0) {
                    elavator.addSound("elevator_up");
                    int title = z2 + 1; //FacilityConf.getInstance().getFloorTitle(z2);
                    String flTitle = "floor" + title;
                    String floorUri = getSoundUri(z2, facility);

                    if (floorUri != null) {
                        flTitle = floorUri;
                    }
                    elavator.addSound(flTitle);
//					}
                } else if (z2 < z1) {
                    int tmptxt = ResourceTranslator.getInstance().getTranslatedResourceId("string", "elvator_down");
                    elavator.addImage(R.drawable.go_down);
                    elavator.addText(tmptxt);
//					if (z2 > 0) {
                    elavator.addSound("elvator_down");
                    int title = z2 + 1; //FacilityConf.getInstance().getFloorTitle(z2);
                    String flTitle = "floor" + title;
                    String floorUri = getSoundUri(z2, facility);

                    if (floorUri != null) {
                        flTitle = floorUri;
                    }
                    elavator.addSound(flTitle);
//					}
                }
//				elavator.setTofloor(String.valueOf(z2));
                elavator.setTofloor(facility.getFloor(z2).getTitle());
                instructions.add(elavator);
                instructions.addAll(getAngleInstractions(Secoendpath, facility));
//				Instructionobject end = new Instructionobject();
//				end.location.setX(Secoendpath.get(Secoendpath.size()-1).getLine().getPoint2().getX());
//				end.location.setY(Secoendpath.get(Secoendpath.size()-1).getLine().getPoint2().getY());
//				end.location.setZ(Secoendpath.get(Secoendpath.size()-1).getLine().getZ());
//				end.setSegment(Secoendpath.get(Secoendpath.size()-1));
//				end.addImage(R.drawable.destination2);
//				end.addText(R.string.destination);
//				end.addSound("destination");
//				instructions.add(end);
            } else {
                Instructionobject end = new Instructionobject(facility.getId(), path.get(path.size() - 1));
                end.location.setX(path.get(path.size() - 1).getLine().getPoint2().getX());
                end.location.setY(path.get(path.size() - 1).getLine().getPoint2().getY());
                end.location.setZ(path.get(path.size() - 1).getLine().getZ());
                end.location.setType(LocationMode.INDOOR_MODE);

                NavInstruction simplified = paths.get(0).getSimplifiedInstruction();
                if (PropertyHolder.getInstance().isSimplifiedInstruction() && simplified != null) {
                    end.setSimplifiedInstruction(simplified);
                }

                if (aStarData.getInstance().getExternalDestination() != null) {
                    int tmptxt = ResourceTranslator.getInstance().getTranslatedResourceId("string", "exit_building");
                    end.addImage(R.drawable.exit_building);
                    end.addText(tmptxt);
                    end.setType(Instruction.TYPE_EXIT);
                } else {
                    int tmptxt = ResourceTranslator.getInstance().getTranslatedResourceId("string", "simplified");
                    end.setType(Instruction.TYPE_DESTINATION);
                    end.addImage(R.drawable.destination);
                    end.addText(tmptxt);
                    end.addSound("destination");
                }
                instructions.add(end);
            }
        }


        setCurrentInstructions(instructions);
        if (!PropertyHolder.getInstance().isSimplifiedInstruction()) {
            mergeInstructions(instructions);
        }
//		List<Instruction> a = getCurrentMergedInstructions();
        return instructions;
    }

    private void mergeInstructions(List<Instruction> instructions) {
        getCurrentMergedInstructions().clear();
        Instruction currentInst = null;
        for (Instruction instruction : instructions) {
            if (currentInst == null) {
                currentInst = instruction;
                continue;
            }

            GisSegment s = instruction.getSegment();

            if (instruction.getSound().isEmpty() || instruction.getType() == Instruction.TYPE_DESTINATION || instruction.getType() == Instruction.TYPE_STRAIGHT || currentInst.getType() == Instruction.TYPE_STRAIGHT || currentInst.getType() == Instruction.TYPE_SWITCH_FLOOR || currentInst.getInstructions().size() >= 2 || getsize(s) > PropertyHolder.getInstance().getInstructionsDistance()) {
                getCurrentMergedInstructions().add(currentInst);
                currentInst = instruction;
            } else {
                currentInst.addInstruction(instruction);

            }
            if (!getCurrentMergedInstructions().contains(currentInst)) {
                getCurrentMergedInstructions().add(currentInst);
            }
        }


    }

    public List<Instruction> getAngleInstractions(FloorNavigationPath floorpath, FacilityConf facilityConf) {

        List<GisSegment> path = floorpath.getPath();

        List<Instruction> instructions = new ArrayList<Instruction>();
        float segmentsangle[] = new float[path.size()];
        int counter = 0;
        for (GisSegment s : path) {
            segmentsangle[counter] = aStarMath.getSegmentAngle(s);
            counter++;
        }
        counter = 0;
        Instructionobject current;
        for (GisSegment s : path) {

            if (s != null && s.getLine() != null && counter < segmentsangle.length - 1) {
//				if (getsize(s) > PropertyHolder.getInstance().getInstructionsDistance()) {
                current = new Instructionobject(facilityConf.getId(), s);
                current.location.setX(s.getLine().getPoint2().getX());
                current.location.setY(s.getLine().getPoint2().getY());
                current.location.setZ(s.getLine().getZ());
                current.location.setType(LocationMode.INDOOR_MODE);

                NavInstruction simplified = floorpath.getSimplifiedInstruction();
                if (PropertyHolder.getInstance().isSimplifiedInstruction() && simplified != null) {
                    current.setSimplifiedInstruction(simplified);
                }
//				}

                float sangle = segmentsangle[counter];
                float nangle = segmentsangle[counter + 1];
                float angle = aStarMath.getAngleToNext(sangle, nangle);


                if (angle >= -15 && angle <= 15) {
                    int tmptxt = ResourceTranslator.getInstance().getTranslatedResourceId("string", "continue_straight");
                    current.addImage(R.drawable.continue_straight);
                    current.addText(tmptxt);
                    if (PropertyHolder.getInstance().isPlayStraightSound()) {
                        if (!shouldMuteInstruction(s, facilityConf.getId())) {
                            current.addSound("straight");
                        }
                    }
                    current.setType(Instruction.TYPE_STRAIGHT);
                    instructions.add(current);
                } else if (angle >= -135 && angle <= -45) {
                    int tmptxt = ResourceTranslator.getInstance().getTranslatedResourceId("string", "turn_left");
                    current.addImage(R.drawable.turn_left);
                    current.addText(tmptxt);
                    if (!shouldMuteInstruction(s, facilityConf.getId())) {
                        current.addSound("turn_left");
                    }
                    instructions.add(current);
                } else if (angle > -45 && angle < -15) {
//					if (isJunction(s.getLine().point2)) {
                    int tmptxt = ResourceTranslator.getInstance().getTranslatedResourceId("string", "left_hall");
                    current.addImage(R.drawable.left_hall);
                    current.addText(tmptxt);
                    if (!shouldMuteInstruction(s, facilityConf.getId())) {
                        current.addSound("left_hall");
                    }
                    instructions.add(current);
//					}
                } else if (angle >= 45 && angle <= 135) {
                    int tmptxt = ResourceTranslator.getInstance().getTranslatedResourceId("string", "turn_right");
                    current.addImage(R.drawable.turn_right);
                    current.addText(tmptxt);
                    if (!shouldMuteInstruction(s, facilityConf.getId())) {
                        current.addSound("turn_right");
                    }
                    instructions.add(current);
                } else if (angle > 15 && angle < 45) {
//					if (isJunction(s.getLine().point2)) {
                    int tmptxt = ResourceTranslator.getInstance().getTranslatedResourceId("string", "right_hall");
                    current.addImage(R.drawable.right_hall);
                    current.addText(tmptxt);
                    if (!shouldMuteInstruction(s, facilityConf.getId())) {
                        current.addSound("right_hall");
                    }
                    instructions.add(current);
//					}
                }

            }


            counter++;
        }


        return instructions;
    }

    private boolean shouldMuteInstruction(GisSegment s, String facid) {
        boolean result = false;
        List<GeoFenceObject> glist = GeoFenceHelper.getInstance().getMuteGeofences(facid, (int) s.getLine().getZ());
        for (GeoFenceObject o: glist) {
            if (o.isContains((float)s.getLine().getPoint2().getX(), (float)s.getLine().getPoint2().getY())){
                result = true;
                break;
            }
        }
        return result;
    }


//    private boolean isJunction(GisPoint p) {
//        boolean result = false;
//        List<aStarPoint> tree = aStarData.getInstance().segmentTree;
//        aStarPoint point = null;
//        for (aStarPoint ap : tree) {
//            if (ap.getPoint().equals(p)) {
//                point = ap;
//            }
//        }
//        if (point != null && point.Segments.size() > 2) {
//            result = true;
//        }
//        return result;
//    }

    private double getsize(GisSegment s) {
        double result = aStarMath.findDistance(s.getLine().getPoint1(), s.getLine().getPoint2());
        return result / FacilityContainer.getInstance().getSelected().getPixelsToMeter();
    }

    public INavInstruction findSimplifiedInstruction(Instruction instruction) {
        INavInstruction result = null;
        GisSegment segment = instruction.getSegment();
        HashMap<String, NavigationPath> pathmap = RouteCalculationHelper.getInstance().getIndoorNavPaths();

        return result;
    }

    public Instruction findCloseInstruction(PointF p1) {
        Instruction result = null;
        GisSegment segment = findSegment(p1);
        if (segment != null) {
            for (Instruction i : getCurrentInstructions()) {
                int id1 = segment.getId();
                int id2 = i.getSegment().getId();
                if (id1 == id2) {
                    result = i;
                    break;
                }
            }
        }


        return result;
    }

    public Instruction findCloseMergedInstruction(PointF p1) {

        Instruction result = null;
        GisSegment segment = findSegment(p1);
        if (segment != null) {
            for (Instruction i : getCurrentMergedInstructions()) {
                int id1 = segment.getId();
                int id2 = i.getSegment().getId();
                if (id1 == id2) {
                    result = i;
                    break;
                }
            }
        }


        return result;
    }

    public GisSegment findSegment(PointF p1) {
        FacilityConf fac = FacilityContainer.getInstance().getCurrent();
        if (fac != null) {
            int floorz = fac.getSelectedFloor();
            NavigationPath nav = aStarData.getInstance().getCurrentPath();
            List<List<GisSegment>> list = nav.getPathByZ(floorz);
            for (List<GisSegment> p : list) {
                for (GisSegment o : p) {
                    double d = distancefromsegment(p1, o);
                    if (d == 0) {
                        return o;
                    }
                }
            }
        }
        return null;
    }

    private double distancefromsegment(PointF p, GisSegment s) {
        double result = 0;
        GisPoint p1 = new GisPoint(p.x, p.y, s.getLine().getZ());
        GisPoint p2 = aStarMath.findClosePointOnSegment(p1, s);
        result = aStarMath.findDistance(p1, p2);
        if (result < 1) {
            result = 0;
        }
        return result;
    }

    public Instruction findSwitchFloorInstruction(int z, GeoFenceRect currentelevetorzone) {
        Instruction result = null;
        for (Instruction o : currentInstructions) {
            if (currentelevetorzone != null && o.getLocation().getZ() == currentelevetorzone.getZ() && o.getType() == Instruction.TYPE_SWITCH_FLOOR) {
                RectF zonerect = currentelevetorzone.getZone();
                float switchx = (float) o.getLocation().getX();
                float switchy = (float) o.getLocation().getY();
                if (zonerect.contains(switchx, switchy)) {
                    result = o;
                }
            }
        }
        return result;
    }

    public Instruction findCloseSwitchFloorInstruction(int z, PointF p) {
        Instruction result = null;
        int switchradius = PropertyHolder.getInstance().getPlaySwitchFloorRadius();
        for (Instruction o : currentInstructions) {
            if (p != null && o.getLocation().getZ() == z && o.getType() == Instruction.TYPE_SWITCH_FLOOR) {
                float switchx = (float) o.getLocation().getX();
                float switchy = (float) o.getLocation().getY();
                PointF switchpoint = new PointF(switchx, switchy);
                double d = MathUtils.distance(p, switchpoint);
                FacilityConf facConf = FacilityContainer.getInstance().getSelected();
                if (facConf != null) {
                    d = d / facConf.getPixelsToMeter();
                    if (d <= switchradius) {
                        result = o;
                        break;
                    }
                }
            }
        }
        return result;
    }

    public void clear() {
        currentInstructions.clear();
        currentMergedInstructions.clear();
        nextInstruction = null;
        lastNotifiedInstruction = null;
    }

    public List<Instruction> getCurrentInstructions() {
        return currentInstructions;
    }

    private void setCurrentInstructions(List<Instruction> currentInstructions) {
        this.currentInstructions = currentInstructions;
        int auto_increment_id = 0;
        for (Instruction inst : this.currentInstructions) {
            if (inst != null) {
                inst.setID(auto_increment_id);
                auto_increment_id++;
            }
        }
    }

    public List<INavInstruction> getNavInstructions() {
        List<INavInstruction> navInstList = new ArrayList<INavInstruction>();

        for (Instruction instruction : currentInstructions) {
            if (instruction != null) {
                navInstList.add(new NavInstruction(instruction));
            }
        }
        return navInstList;

    }

    public void setSimplifiedInstructions(List<Instruction> currentInstructions) {
        this.currentInstructions = currentInstructions;
    }


    public NavInstruction getNextNavInstruction(Instruction instruction) {
        return getNextNavInstruction(instruction, false);
    }

    public NavInstruction getNextNavInstruction(Instruction instruction, boolean checkLastNotified) {
        NavInstruction navInst = null;

        if (checkLastNotified && lastNotifiedInstruction != null && instruction != null
                && instruction.getID() == lastNotifiedInstruction.getID()) {
            return null;
        }

        if (instruction != null) {
            navInst = new NavInstruction(instruction);
            lastNotifiedInstruction = instruction;
        }
        return navInst;
    }

    public Instruction getNextInstruction() {
        return nextInstruction;
    }

    public void setNextInstruction(Instruction nextInstruction) {
        this.nextInstruction = nextInstruction;
    }

    public List<Instruction> getCurrentMergedInstructions() {
        return currentMergedInstructions;
    }

    public void setCurrentMergedInstructions(
            List<Instruction> currentMergedInstructions) {
        this.currentMergedInstructions = currentMergedInstructions;
    }


}
