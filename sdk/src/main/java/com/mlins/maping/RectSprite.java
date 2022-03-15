package com.mlins.maping;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.RectF;

public class RectSprite extends LableSprite {

    private RectF area = null;

    public RectSprite(RectF area) {
        super(null, null, null);
        this.area = area;
    }

    @Override
    public void draw(Canvas canvas) {


        Rect rect = getBounds();
        float x = rect.left;
        float y = rect.top;

        RectF offsetArea = new RectF();

        Paint p = new Paint();
        p.setStyle(Style.STROKE);
        p.setStrokeWidth(5);
        p.setColor(Color.GREEN);
        offsetArea.left = area.left + x;
        offsetArea.right = area.right + x;
        offsetArea.bottom = area.bottom + y;
        offsetArea.top = area.top + y;
        canvas.drawRect(offsetArea, p);


    }

}
