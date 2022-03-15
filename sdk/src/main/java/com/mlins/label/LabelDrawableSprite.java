package com.mlins.label;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextPaint;

import com.mlins.utils.FacilityConf;
import com.mlins.utils.FacilityContainer;
import com.mlins.views.TouchImageView;

@Deprecated
public class LabelDrawableSprite extends BitmapDrawable {

    public static final int H_ANGLE_NORMAL_VAL = 0;
    public static final int H_ANGLE_FLIP_VAL = 180;
    public static final int V_ANGLE_NORMAL_VAL = 90;
    public static final int V_ANGLE_FLIP_VAL = 270;
    public static final int V_ANGLE_RANGE_VAL = 45;
    public static final int H_ANGLE_RANGE_VAL = 45;
    public static final float MIN_DISPLAYED_ZOOM = 0.6f;
    // label txt params
    private TextPaint txtPnt = null;
    private TouchImageView tiv = null;
    private RectF area = new RectF();
    private int txtDirection = LabelData.HORIZONTAL;
    private float txtSize = LabelData.NOT_VALID_TXT_SIZE_TAG;
    private String secondLineTxt = null;
    private String firstLinetxt = null;
    // rotation params
    private float hAngle = H_ANGLE_NORMAL_VAL;
    private float vAngle = V_ANGLE_NORMAL_VAL;
    private RectF offsetArea = null;

    private LabelData label = null;

    public LabelDrawableSprite(LabelData label, TouchImageView tiv) {
        super();
        if (label != null) {
            this.label = label;
            area = label.getRect();
            firstLinetxt = label.getFirstLineTxt();
            txtDirection = LabelData.HORIZONTAL; //label.getTxtDirection();
            txtPnt = new TextPaint(Paint.LINEAR_TEXT_FLAG);
            txtPnt.setStyle(LabelData.PAINT_STYLE);
            txtPnt.setStrokeWidth(LabelData.STROKE_WIDTH);
            txtPnt.setTypeface(LabelData.FONT_TYPE_FACE);
            txtPnt.setColor(LabelData.LABEL_COLOR);

            txtSize = label.getTxtSize();
            secondLineTxt = label.getSecondLineTxt();

        }
        this.tiv = tiv;
    }


    public LabelData getLabel() {
        return label;
    }


    @Override
    public void draw(Canvas canvas) {
        try {
            float tivScale = tiv.getSaveScale();
            // System.out.println("tivScale == > " + tivScale);

            if (tivScale < MIN_DISPLAYED_ZOOM) {
                return;
            }

            Rect rect = getBounds();
            float x = rect.left;
            float y = rect.top;

            offsetArea = new RectF();

            offsetArea.left = area.left + x;
            offsetArea.right = area.right + x;
            offsetArea.bottom = area.bottom + y;
            offsetArea.top = area.top + y;

            // XXX  UNCOMMENT FOR DEBUGG
            /**
             Paint p = new Paint();
             p.setAntiAlias(true);
             p.setTypeface(Typeface.DEFAULT_BOLD);
             p.setStyle(Style.STROKE);
             p.setStrokeWidth(1);
             p.setColor(Color.BLUE);
             canvas.drawRect(offsetArea, p);
             */

            if (txtSize == LabelData.NOT_VALID_TXT_SIZE_TAG) {
                return;
            }

            txtPnt.setTextSize(txtSize);

            Rect txtRect = new Rect();

            txtPnt.getTextBounds(firstLinetxt, 0, firstLinetxt.length(), txtRect);


            Rect secondTxtRect = new Rect();
            float sw = 0;
            float sh = 0;
            if (secondLineTxt != null) {
                txtPnt.getTextBounds(secondLineTxt, 0, secondLineTxt.length(), secondTxtRect);
                sw = txtPnt.measureText(secondLineTxt); //Math.max(secondTxtRect.height(), secondTxtRect.width());
                sh = Math.min(secondTxtRect.height(), secondTxtRect.width());
            }

            float cx = offsetArea.centerX() - LabelData.WMARGIN / 2;
            float cy = offsetArea.centerY() - LabelData.HMARGIN / 2;

            float tx = 0;
            float ty = 0;

            float mapAngle = -1;
            FacilityConf facConf = FacilityContainer.getInstance().getCurrent();
            if (facConf != null) {
                mapAngle = ((tiv.getImageRotation()) + 360) % 360;
            }

            float w = txtPnt.measureText(firstLinetxt); //Math.max(txtRect.height(), txtRect.width());
            float h = Math.min(txtRect.height(), txtRect.width());
            int marginBetweenLines = (int) ((h / 3.0) < 3 ? 3 : (h / 3.0));

            if (txtDirection == LabelData.HORIZONTAL) { // horizontal rect
                canvas.save();
                updateHorizontalFlipAngle(mapAngle);
                canvas.rotate(hAngle, cx, cy);
            } else { // vertical rect
                canvas.save();
                updateVerticalFlipAngle(mapAngle);
                canvas.rotate(vAngle, cx, cy);
            }


            if (secondLineTxt != null) { // multi lines


                tx = cx - (w / 2);
                ty = cy - (marginBetweenLines / 2.0f);
                canvas.drawText(firstLinetxt, (float) tx, (float) ty, txtPnt);


                tx = cx - (sw / 2);
                float ny = cy + (marginBetweenLines / 2) + sh;
                canvas.drawText(secondLineTxt, tx, ny, txtPnt);

            } else // one line
            {
                tx = cx - (w / 2);
                ty = cy + (h / 2);
                canvas.drawText(firstLinetxt, (float) tx, (float) ty, txtPnt);
            }

            canvas.restore();

        } catch (Throwable t) {
            t.printStackTrace();
        }

    }


    public boolean isClicked(PointF p) {

        boolean result = false;

        // if(txt!=null && txt.startsWith("Cleaning")){
        if (p != null) {
            //PointF pv = new PointF(p.x, p.y);
            //pv = tiv.mapToView(p);

            if (offsetArea != null
                    && offsetArea.contains((int) p.x, (int) p.y)) {
                result = true;
            }
            // }
        }
        return result;
    }

    private void updateHorizontalFlipAngle(float mapAngle) {

        //System.out.println("H angle ==>" + mapAngle);
        if (mapAngle == -1) {
            return;
        }

        if (mapAngle <= (180 + H_ANGLE_RANGE_VAL)
                && mapAngle >= (180 - H_ANGLE_RANGE_VAL)) {
            hAngle = H_ANGLE_FLIP_VAL;
            //System.out.println("flip H 180");
        } else if ((mapAngle <= H_ANGLE_RANGE_VAL && mapAngle >= 0)
                || (mapAngle <= 360 && mapAngle >= 360 - H_ANGLE_RANGE_VAL)) {
            hAngle = H_ANGLE_NORMAL_VAL;
            //System.out.println("flip H 0");

        }
    }

    private void updateVerticalFlipAngle(float mapAngle) {

        // System.out.println("V angle ==>" + mapAngle );
        if (mapAngle == -1) {
            return;
        }

        if (mapAngle <= (90 + V_ANGLE_RANGE_VAL)
                && mapAngle >= (90 - V_ANGLE_RANGE_VAL)) {
            vAngle = V_ANGLE_FLIP_VAL;
        } else if (mapAngle <= (270 + V_ANGLE_RANGE_VAL)
                && mapAngle >= (270 - V_ANGLE_RANGE_VAL)) {
            vAngle = V_ANGLE_NORMAL_VAL;
        }
    }


    @Override
    public int getOpacity() {
        return 0;
    }

    @Override
    public void setAlpha(int alpha) {

    }

    @Override
    public void setColorFilter(ColorFilter cf) {
    }


}
