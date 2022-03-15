package com.mlins.maping;

import static android.content.ContentValues.TAG;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class LayerObject {

    public static final int MODE_SCALE_NONE = 0;    // draw in screen coordinates.
    public static final int MODE_SCALE_LAYER = 1;   // sprites maintain size.
    public static final int MODE_SCALE_SPRITES = 2; // also scale sprite size.

    private static final int DEFAULT_SCALE_MODE = MODE_SCALE_SPRITES;
    List<Drawable> mSprites;
    boolean hidden = false;
    private float[] mScratchF = new float[2];
    private Rect mScratch = new Rect();
    private Rect mStore = new Rect();
    private int mScaleMode = DEFAULT_SCALE_MODE;

    public LayerObject() {
        mSprites = new ArrayList<Drawable>();
    }

    public void addSprite(Drawable sprite) {
        mSprites.add(sprite);
    }

    public void clearSprites() {

        for (Drawable d : mSprites) {
            if (d instanceof BitmapDrawable) {
                Bitmap bm = ((BitmapDrawable) d).getBitmap();
                if (bm != null) {
                    bm.recycle();
                }
            }
        }

        mSprites.clear();
    }

    public Drawable removeSprite(int index) {
        return mSprites.remove(index);
    }

    public boolean removeSprite(Drawable obj) {
        return mSprites.remove(obj);
    }

    public void draw(Canvas canvas, Matrix imageMatrix) {
        if (hidden) return;

        for (Drawable s : mSprites) {
            if (!s.isVisible()) continue;

            if (mScaleMode == MODE_SCALE_NONE) {
                s.draw(canvas);
                Log.d(TAG, "draw: " );
            } else if (mScaleMode == MODE_SCALE_SPRITES) {
                canvas.save();
                canvas.concat(imageMatrix);
                s.draw(canvas);
                canvas.restore();
                Log.d(TAG, "draw: gd" + imageMatrix.toString());
            } else if (mScaleMode == MODE_SCALE_LAYER) {
                mStore.set(s.getBounds());
                mScratchF[0] = mStore.exactCenterX();
                mScratchF[1] = mStore.exactCenterY();
                imageMatrix.mapPoints(mScratchF);
                mScratch.set(mStore);
                Log.d(TAG, "draw: ghg");
                mScratch.offsetTo((int) mScratchF[0], (int) mScratchF[1]);
                if (!(s instanceof LableSprite || s instanceof PoiBubbleSprite || s instanceof IconSprite)) {
                    mScratch.offset(-mStore.width() / 2,  -mStore.height());

                } else {
                    mScratch.offset(-mStore.width() / 2, -mStore.height() / 2);

                }
                s.setBounds(mScratch);
                s.draw(canvas);
                s.setBounds(mStore);
            }
        }
    }

    public int getScaleMode() {
        return mScaleMode;
    }

    public void setScaleMode(int scaleMode) {
        mScaleMode = scaleMode;
    }

    public void hide() {
        hidden = true;
    }

    public void show() {
        hidden = false;
    }

    public void addAll(List<Drawable> objs) {
        mSprites.addAll(objs);

    }

    public List<Drawable> getmSprites() {
        return mSprites;
    }
}
