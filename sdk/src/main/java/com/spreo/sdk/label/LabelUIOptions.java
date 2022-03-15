package com.spreo.sdk.label;

import android.graphics.Color;

public class LabelUIOptions {

    private String font = null;
    private int foregroundColor = -1;
    private int backgroundColor = -1;
    private int borderWidth = 0;
    private int borderColor = -1;
    private int borderRoundCournerPx = 0;
    private boolean fontBold = false;
    private boolean fontItalic = false;
    private boolean fontUnderline = false;


    public LabelUIOptions(String font, String foregroundColor,
                          String backgroundColor, int borderWidth, String borderColor,
                          int borderRoundCournerPx, boolean fontBold, boolean fontItalic,
                          boolean fontUnderline) {
        super();
        this.font = font;
        setForegroundColor(foregroundColor);
        setBackgroundColor(backgroundColor);
        this.borderWidth = borderWidth;
        setBorderColor(borderColor);
        this.borderRoundCournerPx = borderRoundCournerPx;
        this.fontBold = fontBold;
        this.fontItalic = fontItalic;
        this.fontUnderline = fontUnderline;
    }

    public LabelUIOptions() {
    }

    public String getFont() {
        return font;
    }

    public void setFont(String font) {
        this.font = font;
    }

    public int getForegroundColor() {
        return foregroundColor;
    }

    public void setForegroundColor(String foregroundColor) {
        this.foregroundColor = Color.parseColor(foregroundColor);
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = Color.parseColor(backgroundColor);
    }

    public int getBorderWidth() {
        return borderWidth;
    }

    public void setBorderWidth(int borderWidth) {
        this.borderWidth = borderWidth;
    }

    public int getBorderColor() {
        return borderColor;
    }

    public void setBorderColor(String borderColor) {
        this.borderColor = Color.parseColor(borderColor);
    }

    public int getBorderRoundCournerPx() {
        return borderRoundCournerPx;
    }

    public void setBorderRoundCournerPx(int borderRoundCournerPx) {
        this.borderRoundCournerPx = borderRoundCournerPx;
    }

    public boolean isFontBold() {
        return fontBold;
    }

    public void setFontBold(boolean fontBold) {
        this.fontBold = fontBold;
    }

    public boolean isFontItalic() {
        return fontItalic;
    }

    public void setFontItalic(boolean fontItalic) {
        this.fontItalic = fontItalic;
    }

    public boolean isFontUnderline() {
        return fontUnderline;
    }

    public void setFontUnderline(boolean fontUnderline) {
        this.fontUnderline = fontUnderline;
    }


}
