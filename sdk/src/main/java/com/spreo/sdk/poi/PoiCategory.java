package com.spreo.sdk.poi;

import com.mlins.utils.PoiType;

/**
 * This class handle the data of the category of a poi.
 *
 * @author Spreo
 */
public class PoiCategory extends PoiType {

    /**
     * PoiCategory constructor
     *
     * @param icon        of poi category
     * @param type        of poi category
     * @param description of poi category
     * @param showInCatgories of poi category
     * @param showInMapFilter of poi category
     */
    public PoiCategory(String icon, String type, String description, boolean showInCatgories, boolean showInMapFilter) {
        super(icon, type, description, showInCatgories, showInMapFilter);
    }

}
