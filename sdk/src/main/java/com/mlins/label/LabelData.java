package com.mlins.label;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.text.TextPaint;

import com.google.android.gms.maps.model.GroundOverlay;
import com.spreo.nav.interfaces.ILabel;

@Deprecated
public class LabelData implements ILabel {

    public static final int VERTICAL = -1;
    public static final int HORIZONTAL = 1;
    public static final int STROKE_WIDTH = 1;
    public static int NOT_VALID_TXT_SIZE_TAG = -1;
    public static int MAX_TXT_SIZE = 20;
    public static int MIN_TXT_SIZE = 4;
    public static int WMARGIN = 0; // in pixel
    public static int HMARGIN = 0; // in pixel
    public static Typeface FONT_TYPE_FACE = Typeface.DEFAULT;
    public static int LABEL_COLOR = Color.parseColor("#666666");
    public static Paint.Style PAINT_STYLE = Paint.Style.FILL;
    // drawing computation parameters
    private static String DEF_FONT = "helvetica";
    private String placeId = null;
    private int floor = -100;
    private double rectTop = -1;
    private double rectLeft = -1;
    private double rectBottom = -1;
    private double rectRight = -1;
    private String description = null;
    private String font = DEF_FONT;
    //private int txtDirection = HORIZONTAL;
    private RectF area = new RectF();
    private float areaW = 0;
    private float areaH = 0;
    private float txtSize = 0;
    private String firstLineTxt = "";
    private String secondLineTxt = null;

    private String resultTxt = null;
    private double angle;


    public LabelData() {
        super();
    }


    public LabelData(String placeId, int floor, double rectTop,
                     double rectLeft, double rectBottom, double rectRight) {
        super();
        this.placeId = placeId;

        this.floor = floor;
        this.rectTop = rectTop;
        this.rectLeft = rectLeft;
        this.rectBottom = rectBottom;
        this.rectRight = rectRight;

    }

    public static int getWMARGIN() {
        return WMARGIN;
    }

    public static void setWMARGIN(int wMARGIN) {
        WMARGIN = wMARGIN;
    }

    public static int getHMARGIN() {
        return HMARGIN;
    }

    public static void setHMARGIN(int hMARGIN) {
        HMARGIN = hMARGIN;
    }

    public static Typeface getFONT_TYPE_FACE() {
        return FONT_TYPE_FACE;
    }

    public static void setFONT_TYPE_FACE(Typeface fONT_TYPE_FACE) {
        FONT_TYPE_FACE = fONT_TYPE_FACE;
    }

    public static int getLABEL_COLOR() {
        return LABEL_COLOR;
    }

    public static void setLABEL_COLOR(int lABEL_COLOR) {
        LABEL_COLOR = lABEL_COLOR;
    }

    public static Paint.Style getPAINT_STYLE() {
        return PAINT_STYLE;
    }

    public static void setPAINT_STYLE(Paint.Style pAINT_STYLE) {
        PAINT_STYLE = pAINT_STYLE;
    }

    public static int getStrokeWidth() {
        return STROKE_WIDTH;
    }

    private void setTypefaceFont() {
        if (this.font != null) {
            try {
                FONT_TYPE_FACE = Typeface.create(this.font, Typeface.NORMAL);
            } catch (Throwable t) {
                t.printStackTrace();
                FONT_TYPE_FACE = Typeface.DEFAULT;
            }
        }

    }

    @Override
    public RectF getRect() {
        return new RectF((float) rectLeft, (float) rectTop, (float) rectRight, (float)rectBottom);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public int getFloor() {
        return floor;
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }

    public double getRectTop() {
        return rectTop;
    }

    public void setRectTop(double rectTop) {
        this.rectTop = rectTop;
    }

    public double getRectLeft() {
        return rectLeft;
    }

    public void setRectLeft(double rectLeft) {
        this.rectLeft = rectLeft;
    }

    public double getRectBottom() {
        return rectBottom;
    }

    public void setRectBottom(double rectBottom) {
        this.rectBottom = rectBottom;
    }

    public double getRectRight() {
        return rectRight;
    }

    public void setRectRight(double rectRight) {
        this.rectRight = rectRight;
    }

    public String getFont() {
        return font;
    }

    public void setFont(String font) {
        this.font = font;
    }

    public void computeState() {


        area = getRect();
        firstLineTxt = getDescription();

        //float rh = area.bottom - area.top;
        //float rw = area.right - area.left;


        //	if (rw >= rh) {
        //		txtDirection = HORIZONTAL;
        areaW = Math.max(area.height(), area.width());// - WMARGIN * 2;
        areaH = Math.min(area.height(), area.width());// - HMARGIN * 2;
//		} else {
//			txtDirection = VERTICAL;
//			areaW = Math.min(area.height(), area.width());// - WMARGIN * 2;
//			areaH = Math.max(area.height(), area.width());// - HMARGIN * 2;
//		}
        setTypefaceFont();
        fitLabelText();
    }

    public String getFirstLineTxt() {
        return firstLineTxt;
    }

    private void fitLabelText() {

        if (firstLineTxt == null) {
            return;
        }

        String tempTxt = firstLineTxt;
        String resultTempTxt = firstLineTxt;

        int idx = tempTxt.indexOf(" "); // take the first word if possible
        if (idx < 0) { // if it is one word
            // one-line
            this.txtSize = findTextSize(resultTempTxt, false);
            firstLineTxt = resultTxt;
        } else {
            // multi-line
            resultTempTxt = tempTxt.substring(0, idx);
            float txtSize1 = findTextSize(resultTempTxt, true);
            firstLineTxt = resultTxt;
            this.txtSize = txtSize1;

            if (idx < tempTxt.length() && resultTxt != null
                    && !resultTxt.endsWith("...")) {
                String secondTxtLine = tempTxt.substring(idx, tempTxt.length());
                secondTxtLine = secondTxtLine.trim();
                float txtSize2 = findTextSize(secondTxtLine, true);
                this.txtSize = Math.min(txtSize1, txtSize2);
                secondLineTxt = resultTxt;
            }

        }

    }

    private float findTextSize(String lableStr, boolean isMultyLines) {

        float textSize = NOT_VALID_TXT_SIZE_TAG;
        String tempTxt = lableStr;
        String resultTempTxt = tempTxt;

        do {

            textSize = getTextSize(resultTempTxt, isMultyLines);

            if (textSize == NOT_VALID_TXT_SIZE_TAG && tempTxt.length() > 3) {
                tempTxt = tempTxt.substring(0, tempTxt.length() - 1);
                resultTempTxt = tempTxt + "...";
            } else {
                break;
            }

        } while (textSize == NOT_VALID_TXT_SIZE_TAG);

        this.resultTxt = resultTempTxt;

        return textSize;

    }

    private float getTextSize(String lableStr, boolean isMultyLines) {


        float resultTextSize = NOT_VALID_TXT_SIZE_TAG;

        if (lableStr == null) {
            return resultTextSize;
        }

//		if(!lableStr.contains("WET")){
//			return resultTextSize;
//		}

        if (areaW < 0 || areaH < 0) {
            return resultTextSize;
        }

        Rect txtRect = null;

        float txtRectW = 0;
        float txtRectH = 0;


        TextPaint paint = new TextPaint();
        paint.setStyle(PAINT_STYLE);
        paint.setStrokeWidth(STROKE_WIDTH);
        paint.setTypeface(FONT_TYPE_FACE);
        paint.setColor(LABEL_COLOR);
        float mesuredH = areaH;
        for (float mesuredTxtSize = MIN_TXT_SIZE; mesuredTxtSize <= MAX_TXT_SIZE; mesuredTxtSize += 0.5) {

            paint.setTextSize(mesuredTxtSize);
            txtRect = new Rect();

            paint.getTextBounds(lableStr, 0, lableStr.length(), txtRect);

            txtRectW = paint.measureText(lableStr, 0, lableStr.length()) + WMARGIN * 2; //Math.min(txtRect.height(), txtRect.width())+ WMARGIN * 2;

            txtRectH = txtRect.height() + HMARGIN * 2; //Math.min(txtRect.height(), txtRect.width())+ HMARGIN * 2;

//			if(txtDirection == HORIZONTAL){
//				txtRectH = Math.min(txtRect.height(), txtRect.width())+ HMARGIN * 2;
//			}
//			else if(txtDirection == VERTICAL){
//				txtRectH = Math.max(txtRect.height(), txtRect.width())+ HMARGIN * 2;
//			}

            if (isMultyLines) {
                mesuredH = mesuredH * 0.60f;
            }
            if (txtRectW < areaW && txtRectH < mesuredH) {
                resultTextSize = mesuredTxtSize;
            } else {
                break;
            }

        }

        return resultTextSize;

    }

//	public int getTxtDirection() {
//		return txtDirection;
//	}

    public float getTxtSize() {
        return txtSize;
    }

    public String getSecondLineTxt() {
        return secondLineTxt;
    }

    public void setSecondLineTxt(String secondLineTxt) {
        this.secondLineTxt = secondLineTxt;
    }

    @Override
    public String toString() {
        return "Label [placeId=" + placeId + ",  floor=" + floor + ", rectTop="
                + rectTop + ", rectLeft=" + rectLeft + ", rectBottom="
                + rectBottom + ", rectRight=" + rectRight + ", description="
                + description + "]";
    }

    public double getAngle() {
        return angle;
    }

    public void setAngle(double angle) {
        this.angle = angle;
    }

    @Override
    public String getCampusId() {
        // TODO Auto-generated method stub
        return null;
    }


    @Override
    public void setCampusId(String campusId) {
        // TODO Auto-generated method stub

    }


    @Override
    public String getFacilityId() {
        // TODO Auto-generated method stub
        return null;
    }


    @Override
    public void setFacilityId(String faciltyId) {
        // TODO Auto-generated method stub

    }


    @Override
    public int getForegroundColor() {
        // TODO Auto-generated method stub
        return 0;
    }


    @Override
    public void setForegroundColor(int foregroundColor) {
        // TODO Auto-generated method stub

    }


    @Override
    public int getBackgroundColor() {
        // TODO Auto-generated method stub
        return 0;
    }


    @Override
    public void setBackgroundColor(int backgroundColor) {
        // TODO Auto-generated method stub

    }


    @Override
    public int getBorderWidth() {
        // TODO Auto-generated method stub
        return 0;
    }


    @Override
    public void setBorderWidth(int borderWidth) {
        // TODO Auto-generated method stub

    }


    @Override
    public int getBorderColor() {
        // TODO Auto-generated method stub
        return 0;
    }


    @Override
    public void setBorderColor(int borderColor) {
        // TODO Auto-generated method stub

    }


    @Override
    public int getBorderRoundCournerPx() {
        // TODO Auto-generated method stub
        return 0;
    }


    @Override
    public void setBorderRoundCournerPx(int borderRoundCournerPx) {
        // TODO Auto-generated method stub

    }


    @Override
    public boolean isFontBold() {
        // TODO Auto-generated method stub
        return false;
    }


    @Override
    public void setFontBold(boolean fontBold) {
        // TODO Auto-generated method stub

    }


    @Override
    public boolean isFontItalic() {
        // TODO Auto-generated method stub
        return false;
    }


    @Override
    public void setFontItalic(boolean fontItalic) {
        // TODO Auto-generated method stub

    }


    @Override
    public boolean isFontUnderline() {
        // TODO Auto-generated method stub
        return false;
    }


    @Override
    public void setFontUnderline(boolean fontUnderline) {
        // TODO Auto-generated method stub

    }

    @Override
    public double getRotation() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void setRotation(double mapBearingAngle) {
        // TODO Auto-generated method stub

    }

    @Override
    public GroundOverlay getGOverlay() {
        // TODO Auto-generated method stub
        return null;
    }


    @Override
    public void removeFromMap() {
        // TODO Auto-generated method stub

    }


    @Override
    public void setVisible(boolean visible) {
        // TODO Auto-generated method stub

    }


    @Override
    public void setStyle(String font, int foregroundColor, int backgroundColor,
                         int borderWidth, int borderColor, int borderRoundCournerPx,
                         boolean fontBold, boolean fontItalic, boolean fontUnderline) {
        // TODO Auto-generated method stub

    }


}
