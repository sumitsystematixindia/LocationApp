package com.spreo.nav.interfaces;

import android.graphics.Rect;
import android.graphics.RectF;

import com.google.android.gms.maps.model.GroundOverlay;

public interface ILabel {

    public RectF getRect();

    public String getDescription();

    public void setDescription(String description);

    public String getPlaceId();

    public void setPlaceId(String placeId);

    public int getFloor();

    public void setFloor(int floor);


    public String getCampusId();

    public void setCampusId(String campusId);

    public String getFacilityId();

    public void setFacilityId(String faciltyId);


    public int getForegroundColor();

    public void setForegroundColor(int foregroundColor);


    public int getBackgroundColor();

    public void setBackgroundColor(int backgroundColor);


    public int getBorderWidth();


    public void setBorderWidth(int borderWidth);


    public int getBorderColor();


    public void setBorderColor(int borderColor);


    public int getBorderRoundCournerPx();

    public void setBorderRoundCournerPx(int borderRoundCournerPx);


    public boolean isFontBold();


    public void setFontBold(boolean fontBold);


    public boolean isFontItalic();


    public void setFontItalic(boolean fontItalic);


    public boolean isFontUnderline();

    public void setFontUnderline(boolean fontUnderline);

    public double getRotation();

    public void setRotation(double mapBearingAngle);

    public GroundOverlay getGOverlay();

    public void removeFromMap();

    public void setVisible(boolean visible);

    public void setStyle(String font, int foregroundColor,
                         int backgroundColor, int borderWidth, int borderColor,
                         int borderRoundCournerPx, boolean fontBold, boolean fontItalic, boolean fontUnderline);
}
