package com.mlins.utils;

public class FacilityContainer {


    private FacilityConf current = null;
    private FacilityConf selected = null;


    public static FacilityContainer getInstance() {
        return Lookup.getInstance().get(FacilityContainer.class);
    }


    public FacilityConf getCurrent() {
        return current;
    }

    public void setCurrent(FacilityConf current) {
        this.current = current;
    }

    public FacilityConf getSelected() {
        return selected;
    }

    public void setSelected(FacilityConf selected) {
        this.selected = selected;
    }


}
