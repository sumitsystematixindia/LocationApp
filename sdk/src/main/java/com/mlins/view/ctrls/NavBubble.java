package com.mlins.view.ctrls;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.View;

public class NavBubble extends BaseCtrl {

    protected View parent;
    Context context;
    private Paint mPaint;
    private RectF bubble = new RectF();
    private Bitmap image = null;
    private String text = null;
    private int textLeft;
    private int textBottom;

    public NavBubble() {

    }

    public NavBubble(int width) {
        bubble.left = 0;
        bubble.top = 0;
        bubble.right = width;
        bubble.bottom = 50;


    }

    @Override
    public void draw(Canvas canvas) {
        mPaint = new Paint();
        mPaint.setColor(Color.WHITE);
        mPaint.setAlpha(128);
        canvas.drawRect(bubble, mPaint);
        mPaint.setAlpha(255);
        if (image != null) {
            canvas.drawBitmap(image, 15, 10, mPaint);
        }
        if (text != null && text.length() != 0) {
            mPaint.setColor(Color.BLACK);
            mPaint.setFakeBoldText(true);
            mPaint.setTextSize(50);
            textBottom = (int) bubble.bottom - 10;
            textLeft = (int) bubble.left + image.getWidth() + 65;
            // TODO Auto-generated method stub
            String txt = text + "";
            canvas.drawText(txt, textLeft, textBottom, mPaint);
        }

    }

    @Override
    public boolean clickTest(float f, float g) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void setParent(View parent) {
        context = parent.getContext();
        this.parent = parent;

    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

}
