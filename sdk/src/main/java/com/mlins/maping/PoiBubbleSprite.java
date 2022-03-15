package com.mlins.maping;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.view.View;
import android.view.View.MeasureSpec;

import com.mlins.utils.PoiData;
import com.mlins.utils.PropertyHolder;
import com.mlins.views.TouchImageView;
import com.spreo.nav.interfaces.IPoi;
import com.spreo.spreosdk.R;

public class PoiBubbleSprite extends IconSprite {

    // private float txtWidth = 0;
    // private String txt = "";
    // private Typeface tf = PropertyHolder.getInstance().getClalitFont();
    TouchImageView tiv = null;
    private IPoi Poi = null;
    private RectF bubbleRect;
    private long creationTime;
    private boolean isHidden = false;
    private String customText = null;
    private boolean userBubble = false;
//	private View customView = null;

    private Bitmap customBitmap = null;

    public PoiBubbleSprite(Bitmap icon, IPoi poi, long t, TouchImageView tiv) {
        super(icon);
        Poi = poi;
        setCreationTime(t);
        this.tiv = tiv;
    }

    public PoiBubbleSprite(Bitmap icon, IPoi poi, long t, TouchImageView tiv,
                           String customtext) {
        super(icon);
        Poi = poi;
        setCreationTime(t);
        this.tiv = tiv;
        customText = customtext;
    }

    public static int nthIndexOf(String source, String sought, int n) {
        int index = source.indexOf(sought);
        if (index == -1)
            return -1;

        for (int i = 1; i < n; i++) {
            index = source.indexOf(sought, index + 1);
            if (index == -1)
                return -1;
        }
        return index;
    }

    @Override
    public void draw(Canvas canvas) {

        float scale = 1.0f;
        float tivScale = tiv.getSaveScale();
        // System.out.println(tivScale);

        if (tivScale <= 2.0) {
            scale = 0.5f;
        } else if (tivScale > 2.0 && tivScale <= 2.5) {
            scale = 0.6f;
        } else if (tivScale > 2.5 && tivScale <= 3.0) {
            scale = 0.7f;
        } else if (tivScale > 3.0 && tivScale <= 3.5) {
            scale = 0.8f;
        } else if (tivScale > 3.5 && tivScale <= 4.0) {
            scale = 0.9f;
        }

        if (customBitmap != null) {
            Bitmap viewbitmap = getScaleBitmap(scale, customBitmap);//
            Paint p = new Paint();
            int width = viewbitmap.getWidth();
            int height = viewbitmap.getHeight();

            int x = super.getBounds().centerX();  //super.getBounds().left + (super.getBounds().width()/2);
            int y = super.getBounds().centerY(); //super.getBounds().top + (super.getBounds().height()/2);
            canvas.drawBitmap(viewbitmap, x - width / 2,
                    y - height, p);

            float bmleft = x - width / 2;
            float bmtop = y - height;
            float bmright = x + width / 2;
            float bmottom = y;
            bubbleRect = new RectF(bmleft, bmtop, bmright, bmottom);

//			for debug
//			Paint pp = new Paint();
//			pp.setColor(Color.RED);
//			pp.setStyle(Style.STROKE);
//			canvas.drawRect(bubbleRect, pp);

            return;
        }

        if (Poi == null || Poi.getIcon() == null) {
            return;
        }
        // TODO Auto-generated method stub
        float x = super.getBounds().centerX();
        float y = super.getBounds().centerY();

        int maxW = 480;
        int maxH = 240;
        // int yGap = 100;
        // int txtyGap = 0;


        scale = scale * PropertyHolder.getInstance().getPoiBubbleScaleFactor();
        if (Poi != null && Poi.getIcon() != null) {
            // yGap = Poi.getIcon().getHeight();
            y = y + (Poi.getIcon().getHeight() / 2.0f) * scale;
        }
        Paint p = new Paint();
        p.setColor(Color.WHITE);
        p.setTextSize(scale * 40);
        p.setTypeface(Typeface.DEFAULT_BOLD);
        // p.setTypeface(tf);
        String txt = null;

        if (customText != null) {
            txt = customText;
        } else {
            txt = Poi.getpoiDescription();
        }

        // txtWidth = w;
        // this.txt = txt;
        Resources resource = PropertyHolder.getInstance().getMlinsContext()
                .getResources();
        Bitmap userBitmap = PropertyHolder.getInstance().getPoiBubbleIcon();
        Bitmap bitmap = null;
        if (userBitmap == null) {
            bitmap = BitmapFactory.decodeResource(resource,
                    R.drawable.poi_bubble_white);
        } else {
            bitmap = userBitmap;
        }
        // if(txt!=null && txt.startsWith("Development")){
        // Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, (int)
        // (1.1f*w) + 60 , 80, false);
        int bmDstWidth = (int) (maxW * scale);
        int bmDstHeight = (int) (maxH * scale);
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, bmDstWidth,
                bmDstHeight, false);
        int xScaledPos = (int) (x - (maxW / 2.0) * scale);

        int yScaledPos = (int) (y - (maxH) * scale - (Poi.getIcon().getHeight())
                * scale);

        Paint transparentpainthack = new Paint();
        transparentpainthack.setAlpha(PropertyHolder.getInstance()
                .getPoiBubbleTransparentLevel());
        canvas.drawBitmap(resizedBitmap, xScaledPos, yScaledPos,
                transparentpainthack);

        float cx = x;
        float cy = yScaledPos + (maxH / 2) * scale;
        int left = (int) (cx - bmDstWidth / 2);
        int right = (int) (cx + bmDstWidth / 2);
        int top = (int) (cy - bmDstHeight / 2);
        int bottom = (int) (cy + bmDstHeight / 2);

        bubbleRect = new RectF(left, top, right, bottom);

        // XXX NIR: TEXT POSITION
        if (txt.length() > 14 && !txt.contains(" ")) {
            txt = txt.substring(0, 12) + "...";
            float w = p.measureText(txt, 0, txt.length());
            float xtxt = (float) (cx + (bmDstWidth * (0.16)));
            // float ytxt = (float) (cy - 0.015 * cy);
            float txtXscaledPos = xtxt - w / 2 - 25 * scale;
            float txtYscaledPos = cy - p.getTextSize() / 2; // + txtyGap *
            // scale;
            canvas.drawText(txt, txtXscaledPos, txtYscaledPos, p);

        } else if (txt.length() > 14 && txt.contains(" ")) {
            String txt1 = "";
            String txt2 = "";
            if (nthIndexOf(txt.substring(0, 14), " ", 2) != -1) {
                // two texts lines test
                txt1 = txt.substring(0,
                        nthIndexOf(txt.substring(0, 14), " ", 2));
                txt2 = txt
                        .substring(nthIndexOf(txt.substring(0, 14), " ", 2) + 1);
                if (txt2.length() > 14) {
                    txt2 = txt2.substring(0, 12) + "...";
                }

            } else if (nthIndexOf(txt.substring(0, 14), " ", 2) == -1
                    && (nthIndexOf(txt.substring(0, 14), " ", 1) != -1)) {
                txt1 = txt.substring(0,
                        nthIndexOf(txt.substring(0, 14), " ", 1));
                txt2 = txt
                        .substring(nthIndexOf(txt.substring(0, 14), " ", 1) + 1);
                if (txt2.length() > 14) {
                    txt2 = txt2.substring(0, 12) + "...";
                }

            } else {
                txt1 = txt.substring(0, 15);
                txt2 = txt.substring(16);
                if (txt2.length() > 14) {
                    txt2 = txt2.substring(0, 12) + "...";
                }

            }
            float w1 = p.measureText(txt1, 0, txt1.length());
            float w2 = p.measureText(txt2, 0, txt2.length());
            float xtxt = (float) (cx + (bmDstWidth * (0.16)));
            float ytxt1 = cy;
            float txtXscaledPos1 = xtxt - w1 / 2 - 25 * scale;
            float txtXscaledPos2 = xtxt - w2 / 2 - 25 * scale;
            float txtYscaledPos1 = (float) ((ytxt1 - 35 * scale)); // - (txtyGap
            // *
            // scale));
            float txtYscaledPos2 = (float) txtYscaledPos1 + 50 * scale;
            canvas.drawText(txt1, txtXscaledPos1, txtYscaledPos1, p);
            canvas.drawText(txt2, txtXscaledPos2, txtYscaledPos2, p);

        } else {
            float w = p.measureText(txt, 0, txt.length());
            float xtxt = (float) (cx + (bmDstWidth * (0.16)));
            float ytxt = (float) (cy - 0.015 * cy);
            float txtXscaledPos = xtxt - w / 2 - 25 * scale;
            float txtYscaledPos = cy - p.getTextSize() / 2; // + txtyGap *
            // scale;
            canvas.drawText(txt, txtXscaledPos, txtYscaledPos, p);
        }
        // XXX NIR end of text position

        // XXX for debug - uncomment this
        // Paint pp = new Paint();
        // pp.setColor(Color.RED);
        // pp.setStyle(Style.STROKE);
        // canvas.drawRect(bubbleRect, pp);
        // }

    }

    public boolean isClicked(PointF p) {

        boolean result = false;

        // if(txt!=null && txt.startsWith("Cleaning")){
        if (p != null) {
            PointF pv = new PointF(p.x, p.y);
            pv = tiv.mapToView(p);

            if (bubbleRect != null
                    && bubbleRect.contains((int) pv.x, (int) pv.y)) {
                result = true;
            }
            // }
        }
        return result;
    }

    public Bitmap getViewAsBitmap(View v) {
        // Get the dimensions of the view so we can re-layout the view at its
        // current size
        // and create a bitmap of the same size

        int width = v.getWidth();
        int height = v.getHeight();
        v.setDrawingCacheEnabled(true);

        if (width == 0 || height == 0) {
            v.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
                    MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
            v.layout(0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());
            width = v.getWidth();
            height = v.getHeight();
        }
        v.buildDrawingCache(true);

        // Create a bitmap backed Canvas to draw the view into
        Bitmap b = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);

        // Now that the view is laid out and we have a canvas, ask the view to
        // draw itself into the canvas
        v.draw(c);

        return b;
    }

    public IPoi getPoi() {
        return Poi;
    }

    public void setPoi(PoiData poi) {
        Poi = poi;
    }

    public Bitmap getScaleBitmap(double scaleFactor, Bitmap bm) {
        Bitmap outBitmap = null;
        if (bm != null) {
            int width = (int) (bm.getWidth() * scaleFactor);
            int height = (int) (bm.getHeight() * scaleFactor);
            outBitmap = (Bitmap.createScaledBitmap(bm, width, height, false));
        }
        return outBitmap;

    }

    //
    // private Paint mPaint;
    // private RectF bubble = new RectF();
    //
    // public PoiBubbleSprite () {
    //
    // }
    //
    // public PoiBubbleSprite(PointF p) {
    // bubble.left = p.x - 30;
    // bubble.top = p.y - 46;
    // bubble.right = p.x + 30;
    // bubble.bottom = p.y - 16;
    // }
    //
    // @Override
    // public void draw(Canvas canvas) {
    // mPaint = new Paint();
    // mPaint.setColor(Color.GRAY);
    // canvas.drawRect(bubble, mPaint);
    // }
    //
    // @Override
    // public int getOpacity() {
    // // TODO Auto-generated method stub
    // return 0;
    // }
    //
    // @Override
    // public void setAlpha(int alpha) {
    // // TODO Auto-generated method stub
    //
    // }
    //
    // @Override
    // public void setColorFilter(ColorFilter cf) {
    // // TODO Auto-generated method stub
    //
    // }

    public long getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(long creationTime) {
        this.creationTime = creationTime;
    }

    public boolean isHidden() {
        return isHidden;
    }

    public void setHidden(boolean isHidden) {
        this.isHidden = isHidden;
    }

    public String getCustomText() {
        return customText;
    }

    public void setCustomText(String customText) {
        this.customText = customText;
    }

    public boolean isUserBubble() {
        return userBubble;
    }

    public void setUserBubble(boolean userBubble) {
        this.userBubble = userBubble;
    }


    public void setCustomView(View customView) {
        this.customBitmap = getViewAsBitmap(customView);

    }

}