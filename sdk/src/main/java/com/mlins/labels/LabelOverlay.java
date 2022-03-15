package com.mlins.labels;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Build;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.mlins.ndk.wrappers.NdkConversionUtils;
import com.mlins.ndk.wrappers.NdkLocation;
import com.mlins.utils.FacilityConf;
import com.mlins.utils.MathUtils;
import com.spreo.nav.interfaces.ILabel;

import java.util.ArrayList;

public class LabelOverlay implements ILabel {

    private static final String TAG = LabelOverlay.class.getName();

    private static final float LAST_LINE_TEXT_LENGTH_BEFORE_ELLIPSIZE_PERCENT = 0.9f;

    private final static double SCALE_IMG = 5;
    private final static double LIMIT_SIZE_PX = 512;
    private final static int WMARGIN = 4; // in pixel
    private final static int HMARGIN = 2; // in pixel
    //private static final int VERTICAL = -1;
    //private static final int HORIZONTAL = 1;
    private static final int STROKE_WIDTH = 1;
    private final static double LABEL_RANGE_ROTATION = 100;
    private static Style PAINT_STYLE = Style.FILL;
    private static int NOT_VALID_TXT_SIZE_TAG = -1;
    private String placeId = null;

    private int areaWidth;
    private int areaHeight;

    // drawing computation parameters
    private RectF rect = new RectF(0, 0, 0, 0);
    private String description = null;
    private int minTextSize = 1;
    private int maxTextSize = 80;
    private String font = null;
    private Typeface fontTypeFace = Typeface.DEFAULT;
    private RectF area = new RectF();
    private float areaW = 0;
    private float areaH = 0;
    private float txtSize = 0;
    private String zeroLineTxt = "";
    private String firstLineTxt = "";
    private String secondLineTxt = null;
    private String resultTxt = null;
    private double angle;
    private String campusId = null;
    private String facilityId = null;
    private int floor = -100;
    private int foregroundColor = 0;
    private int backgroundColor = 0;
    private int borderWidth = 0;
    private int borderColor = 0;
    private int borderRoundCournerPx = 0;
    private boolean fontBold = false;
    private boolean fontItalic = false;
    private boolean fontUnderline = false;
    private int state = 0;

    private GroundOverlay gOverlay = null;

    private double currentAngle = -1;

    private boolean visible = true;

    public LabelOverlay() {
        super();
    }


    public LabelOverlay(String placeId, int floor, double rectTop,
                        double rectLeft, double rectBottom, double rectRight) {
        super();
        this.placeId = placeId;

        this.floor = floor;
        setRect(rectLeft, rectTop, rectRight, rectBottom);
    }

    public LabelOverlay(LabelOverlay l) {
        super();
        this.placeId = l.placeId;
        this.facilityId = l.facilityId;
        this.floor = l.floor;

        rect.set(l.getRect());

        this.description = l.description;
        this.zeroLineTxt = l.zeroLineTxt;
        this.firstLineTxt = l.firstLineTxt;
        this.setTxtSize(l.txtSize);
        this.areaW = l.areaW;
        this.areaH = l.areaH;
        this.foregroundColor = l.foregroundColor;
        this.backgroundColor = l.backgroundColor;
        this.borderWidth = l.borderWidth;
        this.borderColor = l.borderColor;
        this.borderRoundCournerPx = l.borderRoundCournerPx;
        this.fontBold = l.fontBold;
        this.fontItalic = l.fontItalic;
        this.fontUnderline = l.fontUnderline;
        this.font = l.font;
        this.secondLineTxt = l.secondLineTxt;
        this.resultTxt = l.resultTxt;
        this.fontTypeFace = l.fontTypeFace;
        this.angle = l.angle;
        this.state = l.state;
        this.currentAngle = l.currentAngle;
        this.area = l.area;
        this.minTextSize = l.minTextSize;
        this.maxTextSize = l.maxTextSize;
    }

    public static int getStrokeWidth() {
        return STROKE_WIDTH;
    }

    public void setMaxTextSize(int maxTextSize) {
        this.maxTextSize = maxTextSize;
    }

    public void setMinTextSize(int minTextSize) {
        this.minTextSize = minTextSize;
    }

    private void setTypefaceFont() {
        if (this.font != null) {
            try {

                int typeFace = Typeface.NORMAL;
                if (isFontBold()) {
                    typeFace = Typeface.BOLD;
                }
                if (isFontBold() && isFontItalic()) {
                    typeFace = Typeface.BOLD_ITALIC;
                }

                if (!isFontBold() && isFontItalic()) {
                    typeFace = Typeface.ITALIC;
                }


                fontTypeFace = Typeface.create(this.font, typeFace);


            } catch (Throwable t) {
                t.printStackTrace();
                fontTypeFace = Typeface.DEFAULT;
            }
        }

    }

    public RectF getRect() {
        return new RectF(rect);
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

    public String getFont() {
        return font;
    }

    public void setFont(String font) {
        this.font = font;
    }

    @Override
    public void setStyle(String font, int foregroundColor,
                         int backgroundColor, int borderWidth, int borderColor,
                         int borderRoundCournerPx, boolean fontBold, boolean fontItalic, boolean fontUnderline) {


        try {
            if (foregroundColor != -1) {
                setForegroundColor(foregroundColor);
            }

            if (backgroundColor != -1) {
                setBackgroundColor(backgroundColor);
            }

            if (borderColor != -1) {
                setBorderColor(borderColor);
            }
            setBorderWidth(borderWidth);
            setBorderRoundCournerPx(borderRoundCournerPx);
            setFontItalic(fontItalic);
            setFontBold(fontBold);
            setFontUnderline(fontUnderline);

            if (font != null) {
                setFont(font);
            }

            computeState();

            Bitmap bm = createBitmap(areaWidth, areaHeight);

            if (bm == null) {
                return;
            }

            BitmapDescriptor img = BitmapDescriptorFactory.fromBitmap(bm);

            try {
                bm.recycle();
                bm = null;
            } catch (Throwable t) {
                t.printStackTrace();
            }

            if (gOverlay != null) {
                gOverlay.setImage(img);
                img = null;
            }

        } catch (Throwable e) {
            e.printStackTrace();
        }

    }

    public void computeState() {
        area = getRect();
        double width = rect.width();
        double height = rect.height();

        double ratioWH = width / height;
        double ratioHW = height / width;

        double scaleImgFactor = SCALE_IMG;


        width = width * scaleImgFactor;
        height = height * scaleImgFactor;


        // limit the w/h of label drawer
        if (width > LIMIT_SIZE_PX) {
            width = LIMIT_SIZE_PX;
            height = width * ratioHW;
        }

        if (height > LIMIT_SIZE_PX) {
            height = LIMIT_SIZE_PX;
            width = width * ratioWH;
        }

        areaW = (int) width;
        areaH = (int) height;

        firstLineTxt = getDescription();
        setTypefaceFont();
        fitLabelText();
    }

    public float getRectHeight() {
        return areaH;
    }

	/*
	public void computeState() {

	
		area = getRect();
		firstLineTxt = getDescription();

	//	float rh = area.bottom - area.top;
	//	float rw = area.right - area.left;



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
	*/

    public float getRectWidth() {
        return areaW;
    }

    public String getFirstLineTxt() {
        return firstLineTxt;
    }

    public void setFirstLineTxt(String firstLineTxt) {
        this.firstLineTxt = firstLineTxt;
    }

    private void fitLabelText() {
        boolean changeTextSize = txtSize < 0;
        if (firstLineTxt == null) {
            return;
        }
        String tempTxt = firstLineTxt;
        String resultTempTxt = firstLineTxt;
        ArrayList<Integer> idxs = new ArrayList<>();
        int idx = tempTxt.indexOf(" ");
        while (idx != -1) {
            idxs.add(idx);
            idx = tempTxt.indexOf(" ", idx + 1);
        }
        double min = tempTxt.length();
        for (int id : idxs) {
            if (Math.abs(id - tempTxt.length() / 2.0) < min) {
                min = Math.abs(id - tempTxt.length() / 2.0);
                idx = id;
            }
        } // find space clothe to the middle
        TextPaint txtPnt = new TextPaint();
        txtPnt.setStyle(PAINT_STYLE);
        txtPnt.setStrokeWidth(STROKE_WIDTH);
        txtPnt.setTypeface(getFontTypeFace());
        if (isFontUnderline()) {
            txtPnt.setFlags(txtPnt.getFlags() | Paint.UNDERLINE_TEXT_FLAG);
        }
        txtPnt.setColor(getForegroundColor());
        txtPnt.setAntiAlias(true);
        if (getTxtSize() == NOT_VALID_TXT_SIZE_TAG) {
            txtPnt.setTextSize(findTextSize(tempTxt, false));
        } else {
            txtPnt.setTextSize(getTxtSize());
        }
        float measuredWidth = txtPnt.measureText(tempTxt) * 1.2f;

        if (idx < 0 || measuredWidth < getRectWidth()) { // if it is one word
            // one-line
            float findTextSize = findTextSize(resultTempTxt, false);
            if (changeTextSize) {
                setTxtSize(findTextSize);
            }
            firstLineTxt = resultTxt;
            secondLineTxt = null;
        } else {
            // multi-line
            resultTempTxt = tempTxt.substring(0, idx);
            float txtSize1 = findTextSize(resultTempTxt, true);
            firstLineTxt = resultTxt;
            if (changeTextSize) {
                this.setTxtSize(txtSize1);
            }
            if (idx < tempTxt.length() && resultTxt != null && !resultTxt.endsWith("...")) {
                String secondTxtLine = tempTxt.substring(idx, tempTxt.length());
                secondTxtLine = secondTxtLine.trim();
                float txtSize2 = findTextSize(secondTxtLine, true);
                if (changeTextSize) {
                    this.setTxtSize(Math.min(txtSize1, txtSize2));
                }
                secondLineTxt = resultTxt;
                idx = firstLineTxt.lastIndexOf(" ");
                measuredWidth = txtPnt.measureText(firstLineTxt) * 1.25f;
                Rect txtRect = new Rect();
                txtPnt.getTextBounds(firstLineTxt, 0, firstLineTxt.length(), txtRect);
                float h3 = txtRect.height() * 3;
                if (idx > 0 && measuredWidth > getRectWidth() && h3 < getRectHeight()) {
                    zeroLineTxt = firstLineTxt.substring(0, idx);
                    firstLineTxt = firstLineTxt.substring(idx, firstLineTxt.length());
                }
            } else {
                secondLineTxt = null;
            }
        }
    }

    private float findTextSize(String lableStr, boolean isMultyLines) {

        float textSize = NOT_VALID_TXT_SIZE_TAG;
        String tempTxt = lableStr;
        String resultTempTxt = tempTxt;


        textSize = getTextSize(resultTempTxt, isMultyLines);
        if (textSize == NOT_VALID_TXT_SIZE_TAG) {
            textSize = 22;
        }

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
        paint.setTypeface(fontTypeFace);
        paint.setColor(foregroundColor);
        if (isFontUnderline()) {
            paint.setFlags(paint.getFlags() | Paint.UNDERLINE_TEXT_FLAG);
        }
        float mesuredH = areaH;
        int hMargin = HMARGIN * 2;
        if (isMultyLines) {
            mesuredH = mesuredH * 0.50f;
            hMargin = HMARGIN;
        }

        for (float mesuredTxtSize = minTextSize; mesuredTxtSize <= maxTextSize; mesuredTxtSize += 0.2) {

            paint.setTextSize(mesuredTxtSize);
            txtRect = new Rect();

            paint.getTextBounds(lableStr, 0, lableStr.length(), txtRect);

            txtRectW = paint.measureText(lableStr, 0, lableStr.length()) + WMARGIN * 2; //Math.min(txtRect.height(), txtRect.width())+ WMARGIN * 2;

            txtRectH = txtRect.height() + hMargin; //Math.min(txtRect.height(), txtRect.width())+ HMARGIN * 2;

//			if(txtDirection == HORIZONTAL){
//				txtRectH = Math.min(txtRect.height(), txtRect.width())+ HMARGIN * 2;
//			}
//			else if(txtDirection == VERTICAL){
//				txtRectH = Math.max(txtRect.height(), txtRect.width())+ HMARGIN * 2;
//			}


            if (txtRectW < areaW && txtRectH < mesuredH) {
                resultTextSize = mesuredTxtSize;
            } else {
                break;
            }

        }

        return resultTextSize;

    }

    public Typeface getFontTypeFace() {
        return fontTypeFace;
    }

    public void setFontTypeFace(Typeface fontTypeFace) {
        this.fontTypeFace = fontTypeFace;
    }

//	public int getTxtDirection() {
//		return txtDirection;
//	}

    public float getTxtSize() {
        return txtSize;
    }

    public void setTxtSize(float txtSize) {
        this.txtSize = txtSize;
    }

    public String getSecondLineTxt() {
        return secondLineTxt;
    }

    public void setSecondLineTxt(String secondLineTxt) {
        this.secondLineTxt = secondLineTxt;
    }

    @Override
    public String toString() {
        return super.toString() + " Label [placeId=" + placeId + ",  floor=" + floor + ", rectTop="
                + rect.top+ ", rectLeft=" + rect.left + ", rectBottom="
                + rect.bottom + ", rectRight=" + rect.right + ", description="
                + description + "]";
    }

    public double getAngle() {
        return angle;
    }

    public void setAngle(double angle) {
        this.angle = angle;
    }

    public String getCampusId() {
        return campusId;
    }

    public void setCampusId(String campusId) {
        this.campusId = campusId;
    }

    public String getFacilityId() {
        return facilityId;
    }

    public void setFacilityId(String faciltyId) {
        this.facilityId = faciltyId;
    }


    public int getForegroundColor() {
        return foregroundColor;
    }


    public void setForegroundColor(int foregroundColor) {
        this.foregroundColor = foregroundColor;
    }


    public int getBackgroundColor() {
        return /*0xFFFFFFFF;//*/backgroundColor;
    }


    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }


    public int getBorderWidth() {
        return /*1;//*/borderWidth;
    }


    public void setBorderWidth(int borderWidth) {
        this.borderWidth = borderWidth;
    }


    public int getBorderColor() {
        return /*0xff000000;//*/borderColor;
    }


    public void setBorderColor(int borderColor) {
        this.borderColor = borderColor;
    }


    public int getBorderRoundCournerPx() {
        return borderRoundCournerPx;
    }


    public void setBorderRoundCournerPx(int borderRoundCournerPx) {
        this.borderRoundCournerPx = borderRoundCournerPx;
    }


    public boolean isFontBold() {
        return fontBold;
    }


    public void setFontBold(boolean fontBold) {
        this.fontBold = fontBold;
    }


    public boolean isFontItalic() {
        return fontItalic;
    }


    public void setFontItalic(boolean fontItalic) {
        this.fontItalic = fontItalic;
    }


    public boolean isFontUnderline() {
        return fontUnderline;
    }


    public void setFontUnderline(boolean fontUnderline) {
        this.fontUnderline = fontUnderline;
    }


    private Bitmap convertToBitmap() {

        Bitmap image = null;
        if (getTxtSize() == -1) {
            return null;
        }

        try {


            //===
            //XXX for debugging draw rect
//            setBorderWidth(1);
//            setBorderColor(Color.BLACK);

            double scaleImgFactor = SCALE_IMG;
            double width = getRectWidth();
            double height = getRectHeight();

            RectF labelRectArea = new RectF();

            labelRectArea.left = 0;
            labelRectArea.top = 0;

            labelRectArea.right = (int) width;
            labelRectArea.bottom = (int) height;


            image = Bitmap.createBitmap((int) width, (int) height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(image);


            // background
            if (getBackgroundColor() != 0) {
                Paint bkPnt = new Paint();
                bkPnt.setAntiAlias(true);
                bkPnt.setStyle(Style.FILL);
                bkPnt.setColor(getBackgroundColor());
                canvas.drawRect(labelRectArea, bkPnt);
            }

            // border

            if (getBorderWidth() > 0 && getBorderColor() != 0) {
                Paint borderPnt = new Paint();
                borderPnt.setAntiAlias(true);
                borderPnt.setStyle(Style.STROKE);
                borderPnt.setStrokeWidth((int) (getBorderWidth() * scaleImgFactor));
                borderPnt.setColor(getBorderColor());
                canvas.drawRoundRect(labelRectArea,
                        (int) (getBorderRoundCournerPx() * scaleImgFactor),
                        (int) (getBorderRoundCournerPx() * scaleImgFactor),
                        borderPnt);
            }


            // text
            String firstLinetxt = getFirstLineTxt();
            TextPaint txtPnt = new TextPaint();
            txtPnt.setStyle(PAINT_STYLE);
            txtPnt.setStrokeWidth(STROKE_WIDTH);
            txtPnt.setTypeface(getFontTypeFace());
            if (isFontUnderline()) {
                txtPnt.setFlags(txtPnt.getFlags() | Paint.UNDERLINE_TEXT_FLAG);
            }
            txtPnt.setColor(getForegroundColor());
            txtPnt.setAntiAlias(true);

            float txtSize = (int) (getTxtSize());
            String secondLineTxt = getSecondLineTxt();
            txtPnt.setTextSize(txtSize);

            float cx = labelRectArea.centerX() - WMARGIN / 2;
            float cy = labelRectArea.centerY() - HMARGIN / 2;

            float tx = 0;
            float ty = 0;

            Rect txtRect = new Rect();
            txtPnt.getTextBounds(firstLinetxt, 0, firstLinetxt.length(), txtRect);
            float w = txtPnt.measureText(firstLinetxt);
            float h = txtRect.height();


            Rect secondTxtRect = new Rect();
            float sw = 0;
            float sh = 0;
            if (secondLineTxt != null) {
                txtPnt.getTextBounds(secondLineTxt, 0, secondLineTxt.length(), secondTxtRect);
                sw = txtPnt.measureText(secondLineTxt);
                sh = secondTxtRect.height();
            }

            int marginBetweenLines = (int) ((h / 10.0) < 4 ? 2 : (h / 10.0));

            if (secondLineTxt != null) { // multi lines

                if (zeroLineTxt != null) {
                    tx = cx - (txtPnt.measureText(zeroLineTxt) / 2);
                    ty = cy - (marginBetweenLines / 2f) - sh;
                    canvas.drawText(zeroLineTxt, tx, ty, txtPnt);
                }

                tx = cx - (w / 2);
                ty = cy - (marginBetweenLines / 2f);
                canvas.drawText(firstLinetxt, tx, ty, txtPnt);


                tx = cx - (sw / 2);
                float ny = cy + (marginBetweenLines / 2f) + sh;
                canvas.drawText(secondLineTxt, tx, ny, txtPnt);

            } else // one line
            {
                tx = cx - (w / 2);
                ty = cy + (h / 2);
                canvas.drawText(firstLinetxt, tx, ty, txtPnt);
            }


        } catch (Throwable t) {
            t.printStackTrace();
        }

        return image;
    }

    private Bitmap createBitmap(int areaPixelWidth, int areaPixelHeight){

        this.areaWidth = areaPixelWidth;
        this.areaHeight = areaPixelHeight;

        Log.e("DEBUG", "Has to create bitmap: (" + areaPixelWidth + ", " + areaPixelHeight + ')');

        if(areaPixelWidth == 0 || areaPixelHeight == 0) {
            Log.e(TAG, "Can't create bitmap for: " + this + ", areaPixelWidth == 0 || areaPixelHeight == 0");
            return null;
        }

        //TODO: need to reduce bitmap size to save memory if text is small and we don't have to draw background or border
        //TODO: need to reduce bitmap size and scale(reduce) font size accordingly for large areas to prevent possible OOM (if bitmap is too large)

        int bitmapWidth = areaPixelWidth;
        int bitmapHeight = areaPixelHeight;

        //drawing

        TextPaint txtPnt = new TextPaint();
        txtPnt.setStyle(PAINT_STYLE);
        txtPnt.setStrokeWidth(STROKE_WIDTH);
        txtPnt.setTypeface(getFontTypeFace());
        if (isFontUnderline()) {
            txtPnt.setFlags(txtPnt.getFlags() | Paint.UNDERLINE_TEXT_FLAG);
        }

        txtPnt.setColor(getForegroundColor());
        txtPnt.setAntiAlias(true);
        txtPnt.setTextSize(txtSize);

        Bitmap result = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);

        StaticLayout textLayout =
                new StaticLayout(
                        description,
                        txtPnt,
                        bitmapWidth,
                        Layout.Alignment.ALIGN_CENTER,
                        1.0f,
                        0.0f,
                        false);

        if(description.startsWith("HANES"))
            Log.e(TAG, "GOT IT");

        if(textLayout.getHeight() > bitmapHeight) { //truncating adding three dots at the end

            int lastLine = textLayout.getLineForVertical(bitmapHeight);

            if(lastLine != 0) { // has several lines, fist line fits the bitmap
                lastLine = lastLine - 1;

                // adding three dot's at the end

                /*TODO:
                    Now three dots at the end of first line hide several characters of the first line
                    even if first line is short enough to fit all characters plus three dots.
                    This can be improved by adding several spaces to the first line for this particular case
                */

                textLayout =
                        new StaticLayout(
                                description,
                                0,
                                textLayout.getLineEnd(lastLine),
                                txtPnt,
                                bitmapWidth,
                                Layout.Alignment.ALIGN_CENTER,
                                1.0f,
                                0,
                                false,
                                TextUtils.TruncateAt.END,
                                (int) (textLayout.getLineWidth(lastLine) * LAST_LINE_TEXT_LENGTH_BEFORE_ELLIPSIZE_PERCENT)
                        );
            } else { // even first line doesn't fit the bitmap

                float fontSizeReduceStep = 0.5f;

                for(float adjustedFontSize = txtPnt.getTextSize() - fontSizeReduceStep; adjustedFontSize > 0.5f && textLayout.getHeight() > bitmapHeight; adjustedFontSize -= fontSizeReduceStep) {

                    txtPnt.setTextSize(adjustedFontSize);
                    textLayout =
                            new StaticLayout(
                                    description,
                                    txtPnt,
                                    bitmapWidth,
                                    Layout.Alignment.ALIGN_CENTER,
                                    1.0f,
                                    0.0f,
                                    false);
                }

                Log.e(TAG, "Area height is to small, font size was reduced from: " + txtSize + " to: " + txtPnt.getTextSize());
            }
        }

        //background
        if (getBackgroundColor() != 0) {
            Paint bkPnt = new Paint();
            bkPnt.setAntiAlias(true);
            bkPnt.setStyle(Style.FILL);
            bkPnt.setColor(getBackgroundColor());
            canvas.drawRect(0, 0, bitmapWidth, bitmapHeight, bkPnt);
        }

        // border
        if (getBorderWidth() > 0 && getBorderColor() != 0) {
            Paint borderPnt = new Paint();
            borderPnt.setAntiAlias(true);
            borderPnt.setStyle(Style.STROKE);
            borderPnt.setColor(getBorderColor());

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                canvas.drawRoundRect(0, 0, bitmapWidth, bitmapHeight,
						(int) (getBorderRoundCournerPx()),
						(int) (getBorderRoundCournerPx()),
						borderPnt);
            } else {
                //TODO: draw round rect for API LEVELS  < Build.VERSION_CODES.LOLLIPOP
                Log.e(TAG, "Sorry border width and border colors are not supported right now");
            }
        }

        if(textLayout.getHeight() < bitmapHeight) {
            canvas.translate(0, (bitmapHeight - textLayout.getHeight())/2);
        }

        textLayout.draw(canvas);

        return result;
    }

    public void prepareOverlay(GoogleMap googleMap, FacilityConf facilityConf) {

        LatLng sw = convertToLatlng(rect.right, rect.top, facilityConf);
        LatLng ne = convertToLatlng(rect.left, rect.bottom, facilityConf);

        LatLngBounds bounds = new LatLngBounds(ne, // North east corner
                sw); // South west corner

        //distance between corners of rectangle area  (diagonal)
        double meters = MathUtils.distance(sw, ne);

        // Computing bitmap size
        // Converting distance in meters into number of pixel
        // according to https://groups.google.com/forum/#!topic/google-maps-js-api-v3/hDRO4oHVSeM
        // and https://gis.stackexchange.com/questions/7430/what-ratio-scales-do-google-maps-zoom-levels-correspond-to

        float targetZoomLevelForBitmap = 20;//PropertyHolder.getInstance().getDefaultDualMapZoom();

        double bitmapDiagonal = meters / (156543.03392 * Math.cos(sw.latitude * Math.PI / 180) / Math.pow(2, targetZoomLevelForBitmap));

        //that was bitmap diagonal size, now computing bitmap dimensions
        //using some school math

        double k = (double) rect.width() / rect.height();
        double sqrt = Math.sqrt(k*k + 1);

        double width = k * bitmapDiagonal / sqrt;
        double height = bitmapDiagonal / sqrt;

        Bitmap bm = createBitmap((int) width, (int) height);

        if (bm == null) {
            return;
        }

        BitmapDescriptor img = BitmapDescriptorFactory.fromBitmap(bm);

        try {
            bm.recycle();
            bm = null;
        } catch (Throwable t) {
            t.printStackTrace();
        }

        gOverlay = googleMap
                .addGroundOverlay(new GroundOverlayOptions()
                        .image(img).zIndex(3f)
                        .positionFromBounds(bounds)
                        .bearing((float) getAngle())
                        .transparency(0));
        gOverlay.setClickable(true);
        gOverlay.setVisible(visible);

        img = null;
    }

    public GroundOverlay getGOverlay() {
        return gOverlay;
    }

    private LatLng convertToLatlng(double x, double y, FacilityConf facilityConf) {

        LatLng result = null;

        try {

            if (facilityConf != null) {
                NdkLocation point = new NdkLocation(x, y);
                point.setZ(-1); // non relevant

                NdkLocation covertedPoint = new NdkLocation();

                NdkConversionUtils converter = new NdkConversionUtils();

                double rotationAngle = facilityConf.getRot_angle();

                converter.convertPoint(point, facilityConf.getConvRectTLlon(),
                        facilityConf.getConvRectTLlat(), facilityConf.getConvRectTRlon(),
                        facilityConf.getConvRectTRlat(), facilityConf.getConvRectBLlon(),
                        facilityConf.getConvRectBLlat(), facilityConf.getConvRectBRlon(),
                        facilityConf.getConvRectBRlat(), facilityConf.getMapWidth(),
                        facilityConf.getMapHight(), rotationAngle, covertedPoint);

                if (covertedPoint != null) {
                    result = new LatLng(covertedPoint.getLat(),
                            covertedPoint.getLon());
                }
            }

        } catch (Throwable t) {
            t.printStackTrace();
        }

        return result;
    }


    private double MODULE360(double value) {
        return value % 360.0;
    }

    public void removeFromMap() {
        if (gOverlay != null) {
            gOverlay.remove();
        }
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
        if (gOverlay != null) {
            gOverlay.setVisible(visible);
        }
    }

    @Override
    public double getRotation() {
        return currentAngle;
    }

    public void setRotation(double mapBearingAngle) {

        if (gOverlay == null) {
            return;
        }

        //System.out.println("map bearing ==> " + mapBearingAngle + " current angle ==> " + currentAngle);

        double labelAngle = getAngle();

        if (currentAngle == -1) {
            currentAngle = labelAngle;
        }


        double minDiffInDeg1 = currentAngle - mapBearingAngle;
        minDiffInDeg1 = MODULE360(minDiffInDeg1 + 180) - 180;
        if (minDiffInDeg1 < 0) {
            minDiffInDeg1 = 360 + minDiffInDeg1;
        }
        double minDiffInDeg2 = mapBearingAngle - currentAngle;
        minDiffInDeg1 = MODULE360(minDiffInDeg1 + 180) - 180;
        if (minDiffInDeg2 < 0) {
            minDiffInDeg2 = 360 + minDiffInDeg1;
        }
        double minDiffInDeg = Math.min(Math.abs(minDiffInDeg2), Math.abs(minDiffInDeg1));


        if (minDiffInDeg > LABEL_RANGE_ROTATION) {
            currentAngle = MODULE360(currentAngle + 180);
        }

        gOverlay.setBearing((float) currentAngle);


        //System.out.println("map bearing ==> " + mapBearingAngle + " current angle ==> " + currentAngle);
    }

    public void setRect(double left, double top, double right, double bottom) {
        rect.set((float)left, (float)top, (float)right, (float)bottom);
    }



//	public int getFloor() {
//		return (label == null) ? -100 : label.getFloor();
//	}
//
//	public String getCampusId() {
//		return (label == null) ? null : label.getCampusId();
//	}
//
//	public String getFacilityId() {
//		return (label == null) ? null : label.getFacilityId();
//	}
//	
//	public ILabel getLabelData() {
//		return label;
//	}


}
