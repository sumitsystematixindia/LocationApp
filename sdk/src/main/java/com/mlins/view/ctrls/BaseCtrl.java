package com.mlins.view.ctrls;

import android.graphics.Canvas;
import android.view.View;

public abstract class BaseCtrl {
    int screenWidth = 0;
    int screenHeight = 0;
    boolean isHidden = false;

    public void setIsHidden(boolean v) {
        isHidden = v;
    }

    public abstract void draw(Canvas canvas);

    public abstract boolean clickTest(float f, float g);

    public abstract void setParent(View parent);

    public void setScreenSize(int x, int y) {
        screenWidth = x;
        screenHeight = y;

    }

}
