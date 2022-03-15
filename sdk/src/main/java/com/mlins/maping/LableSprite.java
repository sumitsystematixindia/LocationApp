package com.mlins.maping;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;

import com.mlins.utils.LableObject;
import com.mlins.utils.PropertyHolder;
import com.mlins.utils.gis.Location;
import com.mlins.views.TouchImageView;

public class LableSprite extends IconSprite {

    public Rect rect;
    Location location;
    String txt;
    TouchImageView touch_image_view;
    private Typeface tf;

    public LableSprite(LableObject obj, Bitmap tmp, TouchImageView tiv) {
        super(tmp);
        if (obj != null) {
            location = obj.getLocation();
            txt = obj.getTxt();
        }
        touch_image_view = tiv;
    }

    @Override
    public void draw(Canvas canvas) {
        float currentZoom = touch_image_view.getSaveScale();
        tf = PropertyHolder.getInstance().getClalitFontBold();
        double scaleFactor = 1.0;
        if (currentZoom < 1.0) {
            scaleFactor = 0.6;
        }
        int Woffset = (int) (20 * scaleFactor);
        int Hoffset = (int) (40 * scaleFactor);
        rect = getBounds();
        float x = rect.left;
        float y = rect.top;
        Paint bkgP = new Paint();
        //bkgP.setColor(Color.rgb(44,153,201));
        bkgP.setColor(Color.WHITE);
        bkgP.setStyle(Paint.Style.FILL);

        bkgP.setStrokeWidth(2.0f);
        bkgP.setTextSize((float) (scaleFactor * 40.0f));
        bkgP.setTypeface(tf);
        float w = bkgP.measureText(txt, 0, txt.length());
        x += w / 2.0f;
        RectF bgrect = new RectF(x - 0.5f * w - Woffset, y - Hoffset, x - 0.5f * w + (int) (1.1f * w) + Woffset, y + Hoffset - 15);
        //bkgP.setStyle(Style.STROKE);
        canvas.drawRoundRect(bgrect, 12, 12, bkgP);

        //RectF bkgR = new RectF(20, 20, 20, 20);

        bkgP.setStrokeWidth(3);
        bkgP.setColor(Color.rgb(44, 153, 201));
        bkgP.setStyle(Paint.Style.STROKE);
        canvas.drawRoundRect(bgrect, 12, 12, bkgP);
        bkgP.setStrokeWidth(2.0f);
        bkgP.setStyle(Paint.Style.FILL);
        canvas.drawText(txt, (float) x - 0.5f * w, (float) y, bkgP);
//		bkgP.setColor(Color.RED);
//		bkgP.setStrokeWidth(10.0f);
//		canvas.drawPoint(x, y, bkgP);

    }


    @Override
    public int getOpacity() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void setAlpha(int alpha) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        // TODO Auto-generated method stub

    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location loc) {
        this.location = loc;
    }

    public String getTxt() {
        return txt;
    }

    public void setTxt(String txt) {
        this.txt = txt;
    }

    public Rect getRect() {
        return rect;
    }


}
