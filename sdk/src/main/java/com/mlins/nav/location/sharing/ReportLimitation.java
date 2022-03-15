package com.mlins.nav.location.sharing;

import com.mlins.project.Campus;
import com.mlins.project.ProjectConf;
import com.mlins.utils.gis.Location;
import com.spreo.nav.interfaces.ILocation;

public enum ReportLimitation {

    INDOOR, IN_CAMPUS, ALWAYS;

    boolean pass(ILocation location) {
        switch (this) {
            case INDOOR:
                return Location.isInDoor(location);
            case IN_CAMPUS:
                Campus campus = ProjectConf.getInstance().getSelectedCampus();
                return campus != null && campus.contains(location);
            case ALWAYS:
                return true;
        }
        return false;
    }


}
