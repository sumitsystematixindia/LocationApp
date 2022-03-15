package com.mlins.maping;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

import com.mlins.utils.LableObject;
import com.mlins.views.TouchImageView;

public class SignalSrcSprite extends LableSprite {

    public SignalSrcSprite(LableObject obj, Bitmap tmp, TouchImageView tiv) {
        super(obj, tmp, tiv);
    }


    @Override
    public void draw(Canvas canvas) {
        float currentZoom = touch_image_view.getSaveScale();
        double scaleFactor = 1.0;
        if (currentZoom < 1.0) {
            scaleFactor = 0.6;
        }
        int Woffset = (int) (20 * scaleFactor);
        int Hoffset = (int) (40 * scaleFactor);
        Rect rect = getBounds();
        float x = rect.left;
        float y = rect.top;


        Paint bkgP = new Paint();
        bkgP.setColor(Color.WHITE);
        bkgP.setAlpha(150);

        bkgP.setStrokeWidth(2.0f);
        bkgP.setTextSize((float) (scaleFactor * 30.0f));
        float w = bkgP.measureText(txt, 0, txt.length());
        x += w / 2.0f;
        RectF bgrect = new RectF(x - 0.5f * w - Woffset, y - Hoffset, x - 0.5f * w + (int) (1.1f * w) + Woffset, y + Hoffset - 15);


        //bkgP.setStyle(Style.STROKE);
        bkgP.setStrokeWidth(10);


        canvas.drawRoundRect(bgrect, 6, 6, bkgP);
        //RectF bkgR = new RectF(20, 20, 20, 20);


        bkgP.setStrokeWidth(2.0f);
        bkgP.setColor(Color.BLACK);


        canvas.drawText(txt, (float) x - 0.5f * w, (float) y, bkgP);


    }

}
