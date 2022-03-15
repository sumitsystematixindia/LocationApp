package com.spreo.sdk.label;

import com.mlins.project.ProjectConf;
import com.spreo.nav.interfaces.ILabel;

import java.util.List;

public class LabelsUtils {

    /**
     * Get all labels
     *
     * @return labels list
     */
    public static List<ILabel> getAllLabels() {
        return ProjectConf.getInstance().getAllLabelsList();
    }

    /**
     * Get all labels of the campus
     *
     * @param campusId
     * @return labels list
     */
    public static List<ILabel> getCampusLabels(String campusId) {
        return ProjectConf.getInstance().getAllCampusLabelsList(campusId);
    }

    /**
     * Get all labels of the facility
     *
     * @param campusId
     * @param facilityId
     * @return labels list
     */
    public static List<ILabel> getFacilityLabels(String campusId, String facilityId) {
        return ProjectConf.getInstance().getAllFacilityLabelsList(campusId, facilityId);
    }


}
