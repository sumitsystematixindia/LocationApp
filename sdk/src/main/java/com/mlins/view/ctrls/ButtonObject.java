package com.mlins.view.ctrls;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;

public class ButtonObject extends BaseCtrl {

    protected View parent;
    protected int x = 0;
    protected int y = 0;
    protected RectF rect;
    Context context;
    BitmapDrawable drawlble = new BitmapDrawable();
    int width = 0;
    int height = 0;
    Bitmap bitmap;
    public ButtonObject() {

    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
        width = bitmap.getWidth() + 15;
        height = bitmap.getWidth() + 15; //height?
        rect = new RectF(x, y, x + width + 15, y + height + 15);

    }

    public boolean clickTest(float cx, float cy) {
        if (isHidden)
            return false;
        if (!rect.contains(cx, cy)) {
            int i = 0;
            return false;
        } else {

            OnClick();

            return true;

        }

    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
        rect = new RectF(x, y, x + width, y + height);
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
        rect = new RectF(x, y, x + width, y + height);
    }

    @Override
    public void draw(Canvas canvas) {
        if (!isHidden) {
            canvas.drawBitmap(bitmap, x, y, drawlble.getPaint());
        }


    }

    public int getOpacity() {
        // TODO Auto-generated method stub
        return 0;
    }

    public void setAlpha(int alpha) {
        drawlble.setAlpha(alpha);

    }

    public void setColorFilter(ColorFilter filter) {
        drawlble.setColorFilter(filter);

    }

    public void OnClick() {
        // do nothing
    }

    @Override
    public void setParent(View parent) {
        context = parent.getContext();
        this.parent = parent;
        drawlble = new BitmapDrawable(context.getResources());

    }

    @Override
    public void setIsHidden(boolean v) {
        // TODO Auto-generated method stub
        super.setIsHidden(v);
    }

}
