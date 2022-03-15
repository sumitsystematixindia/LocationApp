package com.spreo.nav.interfaces;

import android.graphics.Bitmap;

public interface INavInstruction {
    public static final int OUTDOOR_INSTRUCTION_TAG = 999;
    public static final int DESTINATION_INSTRUCTION_TAG = 888;

    String getText();

    void setText(String text);

    Bitmap getSignBitmap();

    int getId();

    double getDistance();

    void setType(int type);

    int getType();
}
