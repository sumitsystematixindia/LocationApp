package com.mlins.maping;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;

@SuppressWarnings("deprecation")
public class IconSprite extends BitmapDrawable {

    private static final int MAX_POIW = 400;
    private static final int MAX_POIH = 400;
    PointF loc;
    Bitmap mBitmap;

    public IconSprite(Bitmap icon) {
        super(icon);
        mBitmap = icon;
    }

    public PointF getLoc() {
        return loc;
    }

    public void setLoc(PointF loc) {
        this.loc = loc;
        // if the bitmap is not null set the bounds
        if (mBitmap != null) {
            setBounds();
        }
    }

    private void setBounds() {
        if (mBitmap != null) {
            int width = mBitmap.getWidth();
            int height = mBitmap.getHeight();
            Rect r = new Rect(Math.round(loc.x - width / 2),
                    Math.round(loc.y - height / 2), Math.round(loc.x + width / 2),
                    Math.round(loc.y + height / 2));
            super.setBounds(r);
        }

    }

    public Bitmap getmBitmap() {
        return mBitmap;
    }

    public void setmBitmap(Bitmap mBitmap) {
        this.mBitmap = mBitmap;

        // set bounds
        if (loc != null)
            setBounds();
    }

    public void scaleBitmap(double scaleFactor) {

        if (mBitmap == null)
            return;

        Bitmap outBitmap;

        int width = Math.min(MAX_POIW, (int) (mBitmap.getWidth() * scaleFactor));
        int height = Math.min(MAX_POIH, (int) (mBitmap.getHeight() * scaleFactor));
        outBitmap = (Bitmap.createScaledBitmap(mBitmap, width, height, false));
        mBitmap.recycle();
        mBitmap = outBitmap;

    }

    @Override
    public void draw(Canvas canvas) {

        if (mBitmap != null) {
            Rect rect = getBounds();
            canvas.drawBitmap(mBitmap, rect.left, rect.top, getPaint());
        }

    }

    @Override
    public int getOpacity() {
        // TODO Auto-generated method stub
        return 0;
    }

//	@Override
//	public void setAlpha(int alpha) {
//		
//
//	}

    @Override
    public void setColorFilter(ColorFilter cf) {
        // TODO Auto-generated method stub

    }

}
