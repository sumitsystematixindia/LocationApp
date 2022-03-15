package com.mlins.utils.sort;

import com.mlins.project.Campus;
import com.mlins.project.ProjectConf;
import com.mlins.utils.FacilityConf;
import com.spreo.sdk.data.SpreoDataProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CampusHelper {

    private final Campus campus;
    private final List<FacilityHelper> facilities;

    public CampusHelper(String campusID){
        this.campus = ProjectConf.getInstance().getCampus(campusID);;
        this.facilities = getFacilities(campusID);
    }

    public String getID(){
        return campus.getId();
    }

    public List<FacilityHelper> getFacilities() {
        return facilities;
    }

    public static List<FacilityHelper> getFacilities(String campusID){
        Map<String, FacilityConf> facilitiesMap = ProjectConf.getInstance().getCampus(campusID).getFacilitiesConfMap();

        List<FacilityHelper> result = new ArrayList<>();
        for (String facilityID : facilitiesMap.keySet()) {
            result.add(new FacilityHelper(campusID, facilityID));
        }

        return result;
    }

    public static List<CampusHelper> getProjectCampuses(){
        List<String> campusesList = SpreoDataProvider.getCampusesList();

        List<CampusHelper> result = new ArrayList<>();

        for (String campusID : campusesList) {
            result.add(new CampusHelper(campusID));
        }

        return result;
    }

    @Override
    public boolean equals(Object obj) {

        if(super.equals(obj))
            return true;

        if(!(obj instanceof CampusHelper))
            return false;

        return getID().equals(((CampusHelper) obj).getID());
    }
}
