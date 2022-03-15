package com.mlins.labels;

import android.graphics.Color;
import android.util.Log;

import com.mlins.project.Campus;
import com.mlins.project.ProjectConf;
import com.mlins.utils.FacilityConf;
import com.mlins.utils.FileUtils;
import com.mlins.utils.PropertyHolder;
import com.mlins.utils.ResourceDownloader;
import com.mlins.utils.ServerConnection;
import com.spreo.nav.interfaces.ILabel;
import com.spreo.nav.interfaces.IPoi;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LabelsContainer {

    private static final String TAG = LabelsContainer.class.getName();

    private String uri = "spreo_labels.json";
    private Map<String, List<ILabel>> campuseLevelMap = new HashMap<>();
    private Map<String, List<ILabel>> facilityLevelMap = new HashMap<>();
    private Map<String, List<ILabel>> floorLevelMap = new HashMap<>();


    //private static LabelsDataHolder instance = null;

    //@SuppressLint("UseSparseArrays")
    //private Map<Integer, List<LabelOverlay>> labelsToFloorMap = new HashMap<Integer, List<LabelOverlay>>();

//	public static LabelsDataHolder getInstance() {
//		if (instance == null) {
//			instance = new LabelsDataHolder();
//		}
//		return instance;
//	}
//
//	public static void releaseInstance() {
//		if (instance != null) {
//			instance.clean();
//			instance = null;
//		}
//	}

    public LabelsContainer() {
        super();
    }


    public boolean addFacilityData(String campusId, String facilityId) {
        boolean isSucceded = false;
        try {
            String content = "";
            if (PropertyHolder.useZip) {
                String url = ServerConnection.getResourcesUrl() + facilityId + "/" + uri;
                byte[] bytes = ResourceDownloader.getInstance().getUrl(url);
                if (bytes != null) {
                    content = new String(bytes);
                }
            } else {
                File root = PropertyHolder.getInstance().getProjectDir();
                File campusdir = new File(root, campusId);
                File facilitycDir = new File(campusdir, facilityId);
                File labelsfile = new File(facilitycDir, uri);
                content = FileUtils.getFileContent(labelsfile);
            }
            if (!content.equals("")) {
                List<ILabel> list = parseLabelsJson(campusId, facilityId, content);
                addLabels(campusId, facilityId, list);
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return isSucceded;

    }

    @Deprecated
    public boolean load() {

        boolean isSucceded = false;
        clean();

//		FacilityConf facConf = FacilityContainer.getInstance().getSelected();
//
//		if (facConf == null) {
//			return isSucceded;
//		}
//
//		String uri = facConf.getLabelsConfFileName();
//		byte data[] = ResourceDownloader.getInstance().getLocalCopy(uri);
//		if (data != null && data.length > 0) {
//			String content = new String(data);
//			isSucceded = parseLabelsJson(content);
//			if(isSucceded){
//				computeDrawables();
//			}
//		}
//		return isSucceded;


        try {


            ProjectConf.getInstance().loadCampuses();
            Map<String, Campus> campuses = ProjectConf.getInstance().getCampusesMap();
            if (campuses != null) {
                for (Campus campus : campuses.values()) {

                    if (campus != null) {

                        String campusId = campus.getId();
                        PropertyHolder.getInstance().setCampusId(campusId);
                        campus.loadFacilities();

                        Map<String, FacilityConf> facilitiesmap = campus.getFacilitiesConfMap();

                        if (facilitiesmap != null && facilitiesmap.size() > 0) {

                            for (String facilityId : facilitiesmap.keySet()) {
                                try {

//									PropertyHolder.getInstance().setFacilityID(facilityId);
//									
//									String uri = ServerConnection.getBaseUrlOfFacilityResList(facilityId, campusId);
//									byte[] conf = ResourceDownloader.getInstance().getLocalCopy(uri);
//									String resjson = new String(conf);
//		
//									FacilityConf facConf = new FacilityConf(facilityId);
//									facConf.ParseFloors(resjson);
//									PoiDataHelper poiHelper = new PoiDataHelper();
//									List<IPoi> list =  poiHelper.getAllFacilityPois(facConf);


//									byte data[] = ResourceDownloader.getInstance().getLocalCopy(uri);
//									if (data != null && data.length > 0) {
//										String content = new String(data);
//										List<ILabel> list = parseLabelsJson(campusId, facilityId, content);
//										addLabels(campusId, facilityId, list);
//									}


                                    File campusdir = PropertyHolder.getInstance().getCampusDir();
                                    File facilitycDir = new File(campusdir, facilityId);
                                    File labelsfile = new File(facilitycDir, uri);
                                    String content = FileUtils.getFileContent(labelsfile);
                                    List<ILabel> list = parseLabelsJson(campusId, facilityId, content);
                                    addLabels(campusId, facilityId, list);

                                } catch (Throwable t) {
                                    t.printStackTrace();
                                }
                            }
                        }
                    }
                }
            }

            //ProjectConf.getInstance().setPoisContainer(poisContainer);

        } catch (Throwable t) {
            t.printStackTrace();
        }

        //computeDrawables();

        return isSucceded;
    }

    public void computeDrawables() {
        try {
            for (List<ILabel> lblList : campuseLevelMap.values()) {
                for (ILabel label : lblList) {
                    if (label != null) {
                        if (label instanceof LabelOverlay) {
                            ((LabelOverlay) label).computeState();
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void clean() {
        campuseLevelMap.clear();
        facilityLevelMap.clear();
        floorLevelMap.clear();
    }

    private List<ILabel> parseLabelsJson(String campusId, String facilityId, String content) {

        List<ILabel> labelsList = new ArrayList<>();
        try {

            JSONTokener jsonTokener = new JSONTokener(content);

            JSONObject json = (JSONObject) jsonTokener.nextValue();

            String defFont = null;
            int defforegroundColor = 0;
            int defbackgroundColor = 0;
            int defborderWidth = 0;
            int defborderColor = 0;
            int defborderRoundCournerPx = 0;
            boolean deffontBold = false;
            boolean deffontItalic = false;
            boolean deffontUnderline = false;
            int defminTextSize = 0;
            int defmaxTextSize = 0;
            int defTextSize = -1;


            try {
                defminTextSize = json.getInt("min_txt_size");
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                defmaxTextSize = json.getInt("max_txt_size");
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (defminTextSize != 0 && defmaxTextSize != 0 && defminTextSize == defmaxTextSize) {
                defminTextSize = (int) (defminTextSize * 1.5);
                defmaxTextSize = defminTextSize;
            }

            try {
                defTextSize = json.getInt("def_txt_size");
            } catch (Exception e) {
                Log.e(TAG, "Error while reading defTextSize", e);
                //TODO: handle situation where deftextSize is unavaliable
            }

            try {
                String defforegroundColorCode = json.getString("foreground_color");
                defforegroundColor = Color.parseColor(defforegroundColorCode);
            } catch (Exception e) {
                e.printStackTrace();
            }


            try {
                String defbackgroundColorCode = json.getString("background_color");
                defbackgroundColor = Color.parseColor(defbackgroundColorCode);
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                defborderWidth = json.getInt("border_width");
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                String defborderColorCode = json.getString("border_color");
                defborderColor = Color.parseColor(defborderColorCode);
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                defborderRoundCournerPx = json.getInt("border_round_courner_px");
            } catch (Exception e) {
                e.printStackTrace();
            }


            try {
                deffontBold = json.getBoolean("font_bold");
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                deffontItalic = json.getBoolean("font_italic");
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                deffontUnderline = json.getBoolean("font_underline");
            } catch (Exception e) {
                e.printStackTrace();
            }


            try {
                defFont = json.getString("font");
            } catch (Exception e) {
                e.printStackTrace();
            }


            JSONArray jArrObj = json.getJSONArray("labels");

            for (int i = 0; i < jArrObj.length(); i++) {

                try {

                    JSONObject gObj = jArrObj.getJSONObject(i);
                    LabelOverlay o = new LabelOverlay();

                    try {
                        String placeId = gObj.getString("place_id");
                        o.setPlaceId(placeId);
                    } catch (Exception e) {
                        System.out
                                .println("Label:parseAsList error key place_id");
                        e.printStackTrace();
                    }

                    try {
                        int fontSize = gObj.getInt("font_size");
                        o.setTxtSize(fontSize != -1 ? fontSize : defTextSize);

                    } catch (Exception ignored) {
                        o.setTxtSize(defTextSize);
                    }

                    try {
                        double top = gObj.getDouble("top");
                        double left = gObj.getDouble("left");

                        double right = gObj.getDouble("right");
                        double bottom = gObj.getDouble("bottom");

                        o.setRect(left, top, right, bottom);
                    } catch (Exception e) {
                        System.out.println("Label:parseAsList error keys top/left/bottom/right");
                        e.printStackTrace();
                    }

                    try {
                        int floor = gObj.getInt("floor");
                        o.setFloor(floor);
                    } catch (Exception e) {
                        System.out.println("Label:parseAsList error key floor");
                        e.printStackTrace();
                    }

                    try {
                        String desc = gObj.getString("description");
                        o.setDescription(desc);
                    } catch (Exception e) {
                        System.out.println("Label:parseAsList error key description");
                        e.printStackTrace();
                    }


                    try {
                        double angle = gObj.getDouble("angle");
                        o.setAngle(angle);
                    } catch (Exception e) {
                        System.out.println("Label:parseAsList error key angle");
                        e.printStackTrace();
                    }


                    try {
                        String foregroundColor = gObj.getString("foreground_color");
                        o.setForegroundColor(Color.parseColor(foregroundColor));
                    } catch (Exception e) {
                        o.setForegroundColor(defforegroundColor);
                    }


                    try {
                        String backgroundColor = gObj.getString("background_color");
                        o.setBackgroundColor(Color.parseColor(backgroundColor));
                    } catch (Exception e) {
                        o.setBackgroundColor(defbackgroundColor);
                    }

                    try {
                        int borderWidth = gObj.getInt("border_width");
                        o.setBorderWidth(borderWidth);
                    } catch (Exception e) {
                        o.setBorderWidth(defborderWidth);
                    }

                    try {
                        String borderColor = gObj.getString("border_color");
                        o.setBorderColor(Color.parseColor(borderColor));
                    } catch (Exception e) {
                        o.setBorderColor(defborderColor);
                    }

                    try {
                        int borderRoundCournerPx = gObj.getInt("border_round_courner_px");
                        o.setBorderRoundCournerPx(borderRoundCournerPx);
                    } catch (Exception e) {
                        o.setBorderRoundCournerPx(defborderRoundCournerPx);
                    }


                    try {
                        boolean fontBold = gObj.getBoolean("font_bold");
                        o.setFontBold(fontBold);
                    } catch (Exception e) {
                        o.setFontBold(deffontBold);

                    }
                    try {
                        boolean fontItalic = gObj.getBoolean("font_italic");
                        o.setFontItalic(fontItalic);
                    } catch (Exception e) {
                        o.setFontItalic(deffontItalic);
                    }
                    try {
                        boolean fontUnderline = gObj.getBoolean("font_underline");
                        o.setFontUnderline(fontUnderline);
                    } catch (Exception e) {
                        o.setFontUnderline(deffontUnderline);

                    }

                    try {
                        String font = gObj.getString("font");
                        o.setFont(font);
                    } catch (Exception e) {
                        o.setFont(defFont);
                    }

                    if (defmaxTextSize != 0) {
                        o.setMaxTextSize(defmaxTextSize);
                    }

                    if (defminTextSize != 0) {
                        o.setMinTextSize(defminTextSize);
                    }


                    o.setCampusId(campusId);
                    o.setFacilityId(facilityId);
                    labelsList.add(o);

                } catch (Throwable t) {
                    t.printStackTrace();
                }

            }


        } catch (Throwable t) {
            t.printStackTrace();
        }

        return labelsList;
    }

//	public List<LabelOverlay> getLabelsbyFloor(int floor){
//		return labelsToFloorMap.get(floor);
//	}
//	
//	public List<LabelOverlay> getLabelsOfCurrentFloor(){
//		
//		FacilityConf facConf = FacilityContainer.getInstance().getSelected();
//
//		if (facConf == null) {
//			return new ArrayList<LabelOverlay>();
//		}
//		
//		
//		int floor = facConf.getSelectedFloor();
//		
//		
//		return labelsToFloorMap.get(floor);
//	}

    /**
     * public void drawLabels(TouchImageView tiv) {
     * <p>
     * <p>
     * LayerObject labelsLayer = tiv.getLayerByName("labels_layer");
     * labelsLayer.clearSprites();
     * <p>
     * <p>
     * <p>
     * FacilityConf facConf = FacilityContainer.getInstance().getSelected();
     * <p>
     * if (facConf == null) {
     * return;
     * }
     * <p>
     * <p>
     * int floor = facConf.getSelectedFloor();
     * <p>
     * List<LabelData> list = labelsToFloorMap.get(floor);
     * <p>
     * if(list == null){
     * return;
     * }
     * <p>
     * for (LabelData label : list) {
     * if(label!=null){
     * LabelDrawableSprite ls = new LabelDrawableSprite(label, tiv);
     * ls.setAlpha(180);
     * tiv.addDrawableLabel(ls);
     * }
     * <p>
     * }
     * <p>
     * tiv.invalidate();
     * }
     */


    ///////////////////////////////////
    public boolean addLabel(String campusId, ILabel label) {
        if (campusId == null || label == null) {
            return false;
        }

        List<ILabel> campusLabels = campuseLevelMap.get(campusId);
        if (campusLabels == null) {
            campusLabels = new ArrayList<>();
            campuseLevelMap.put(campusId, campusLabels);
        }
        return campusLabels.add(label);

    }


    public void addLabels(String campusId, String facilityId, List<ILabel> list) {

        if (list == null || list.size() == 0) {
            return;
        }

        for (ILabel Label : list) {
            if (Label == null) {
                continue;
            }
            addLabel(campusId, facilityId, Label);
        }


    }


    public boolean addLabel(String campusId, String facilityId, ILabel Label) {


        if (addLabel(campusId, Label) && facilityId != null) {

            String fkey = campusId + "_" + facilityId;
            List<ILabel> facilityLabels = facilityLevelMap.get(fkey);

            if (facilityLabels == null) {
                facilityLabels = new ArrayList<>();
                facilityLevelMap.put(fkey, facilityLabels);
            }

            facilityLabels.add(Label);

            int floor = (int) Label.getFloor(); // floor


            String flkey = campusId + "_" + facilityId + "_" + floor;

            List<ILabel> floorLabels = floorLevelMap.get(flkey);
            if (floorLabels == null) {
                floorLabels = new ArrayList<>();
                floorLevelMap.put(flkey, floorLabels);
            }
            floorLabels.add(Label);


        }

        return true;

    }

    public List<ILabel> getAllFloorLabelsList(String campusId,
                                              String facilityId, int floor) {

        String key = campusId + "_" + facilityId + "_" + floor;
        List<ILabel> iLabelsList = floorLevelMap.get(key);

        if (iLabelsList == null) {
            iLabelsList = new ArrayList<>();
        }

        return iLabelsList;

    }


    public List<ILabel> getAllFacilityLabelsList(String campusId,
                                                 String facilityId) {
        String key = campusId + "_" + facilityId;
        List<ILabel> iLabelsList = facilityLevelMap.get(key);

        if (iLabelsList == null) {
            iLabelsList = new ArrayList<>();
        }

        return iLabelsList;
    }


    public List<ILabel> getAllCampusLabelsList(String campusId) {

        List<ILabel> iLabelsList = campuseLevelMap.get(campusId);

        if (iLabelsList == null) {
            iLabelsList = new ArrayList<>();
        }

        return iLabelsList;
    }


    public List<ILabel> getAllLabels() {
        List<ILabel> iLabelsList = new ArrayList<>();
        for (List<ILabel> list : campuseLevelMap.values()) {
            iLabelsList.addAll(list);
        }
        return iLabelsList;
    }

    public void translateLabels() {
        try {
            String campusid = PropertyHolder.getInstance().getCampusId();
            if (campusid != null) {
                List<IPoi> poilist = ProjectConf.getInstance().getAllCampusPoisList(campusid);
                List<ILabel> labels = getAllLabels();
                if (poilist != null && labels != null) {
                    for (ILabel label : labels) {
                        for (IPoi poi : poilist) {
                            if (label != null && poi != null) {
                                String placeid = label.getPlaceId();
                                String url = poi.getUrl();
                                if (placeid != null && url != null && placeid.equals(url)) {
                                    if (label instanceof LabelOverlay) {
                                        LabelOverlay labelOverlay = (LabelOverlay) label;
                                        labelOverlay.setDescription(poi.getpoiDescription());
                                        labelOverlay.computeState();
                                    }
                                }
                            }
                        }
                    }
                }

            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
    ///////////////////////////////////


}
