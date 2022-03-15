package com.mlins.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ImageView;

import com.mlins.maping.GisSprite;
import com.mlins.maping.GridDrawable;
import com.mlins.maping.LayerObject;
import com.mlins.orientation.OrientationMonitor;
import com.mlins.utils.AnimationsHolder;
import com.mlins.utils.FacilityConf;
import com.mlins.utils.FacilityContainer;
import com.mlins.utils.PoiDataHelper;
import com.mlins.utils.PropertyHolder;
import com.mlins.utils.logging.Log;
import com.spreo.nav.enums.MapRotationType;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

public class TouchImageView extends ImageView implements OnTouchListener {

    public static final int IMG_TYPE_REGULAR = 0;
    public static final int IMG_TYPE_ROUTEOVERVIEW = 1;
    public static final float ZOOM_LEVEL_THR = 2.7f;
    protected static final boolean GRID_DEFAULT_VISIBILITY = false;
    protected static final int CLICK_PROX = 25; // 5px square.
    protected static final long CLICK_PERIOD = 400;
    // Touch interpreted in one of these 3 modes
    protected static final int NONE = 0;
    protected static final int DRAG = 1;
    protected static final int ZOOM = 2;
    protected static final String TAG = TouchImageView.class.getName();
    //private float skewX=0f;
    //private float skewY=0f;
    protected static final float MAX_SKEW_X = 0.32f;
    public Map<String, LayerObject> mLayers = new LinkedHashMap<String, LayerObject>();
    protected int mMode = NONE;
    protected boolean isInDeleteMode = false;
    protected RectF deleteMatrixArea = new RectF();
    protected PointF deleteRectmLast;// = new PointF();
    protected PointF deleteRectmStart;// = new PointF();
    protected float mMinScale = 0.5f;
    protected float mMaxScale = 8f;
    protected PointF mTouchLocation = new PointF();
    protected Matrix mImageMatrix = new Matrix();
    protected Matrix mInverse = new Matrix();
    protected PointF mLast = new PointF();
    protected PointF mStart = new PointF();
    protected float mOrigWidth, mOrigHeight;
    protected float saveScale = 1f;
    protected float mImageRotation = 0;
    protected float mImageOffsetRotation = 0;
    protected int type = IMG_TYPE_REGULAR;
    protected float currSkewX = 0f;
    protected ScaleGestureDetector mScaleDetector;
    //private float currSkewY=0f;
    //private int DRAG_DIRECTION=0;
    //float firstFingerX=0;
    //float firstFingerY=0;
    //float secondFingerX=0;
    //float secondFingerY=0;
    //float fingersDist=0;
    protected GridDrawable mGrid = new GridDrawable();
    protected OrientationObsrver mRotor = new OrientationObsrver();
    protected PointF mCenter; // in image coordinates!
    protected boolean mCompassMode = false;
    protected boolean mRestoreCompassMode = false;
    protected boolean mAnimated = false;
    protected boolean followMeMode = true;
    protected boolean justMoved = false;
    protected boolean isFirstTime = true;
    private Bitmap mBitmap;
    private int orientationCounter = 0;
    private float lastRotation = -999;
    private int orientationCounterThreshold = 5;
    private float rotationThreshold = 5;
    public TouchImageView(Context context) {
        this(context, null);
    }
    public TouchImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setClickable(true);
        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
        setImageMatrix(mImageMatrix);
        setScaleType(ScaleType.MATRIX);

        LayerObject labelsDrawableLayer = new LayerObject();
        mLayers.put("labels_layer", labelsDrawableLayer);
        labelsDrawableLayer.setScaleMode(LayerObject.MODE_SCALE_SPRITES);


        Drawable d = getDrawable();
        if (d != null) {
            mGrid.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
        } // else it will be set at setImageBitmap()
        LayerObject gridLayer = new LayerObject();
        gridLayer.addSprite(mGrid);
        gridLayer.setScaleMode(LayerObject.MODE_SCALE_SPRITES);
        mGrid.setVisible(GRID_DEFAULT_VISIBILITY, false);
        mLayers.put("grid", gridLayer);

        LayerObject lablesLayer = new LayerObject();
        lablesLayer.setScaleMode(LayerObject.MODE_SCALE_LAYER);
        mLayers.put("lables", lablesLayer);

        LayerObject gisLayer = new LayerObject();
        mLayers.put("gis", gisLayer);
        gisLayer.setScaleMode(LayerObject.MODE_SCALE_SPRITES);

        LayerObject pathLayer = new LayerObject();
        mLayers.put("path", pathLayer);
        pathLayer.setScaleMode(LayerObject.MODE_SCALE_SPRITES);

        LayerObject poiLayer = new LayerObject();
        mLayers.put("poi", poiLayer);
        poiLayer.setScaleMode(LayerObject.MODE_SCALE_LAYER);

        LayerObject bubleLayer = new LayerObject();
        mLayers.put("buble", bubleLayer);
        bubleLayer.setScaleMode(LayerObject.MODE_SCALE_LAYER);

        LayerObject parkingLayer = new LayerObject();
        mLayers.put("parking", parkingLayer);
        parkingLayer.setScaleMode(LayerObject.MODE_SCALE_LAYER);

        LayerObject markLayer = new LayerObject();
        mLayers.put("marks", markLayer);

        LayerObject socialLayer = new LayerObject();
        mLayers.put("social", socialLayer);

//		LayerObject pathLayer = new LayerObject();
//		mLayers.put("path", pathLayer);
//		pathLayer.setScaleMode(LayerObject.MODE_SCALE_SPRITES);

        LayerObject instructionsLayer = new LayerObject();
        mLayers.put("instructions", instructionsLayer);
        instructionsLayer.setScaleMode(LayerObject.MODE_SCALE_LAYER);

        LayerObject geofenceLayer = new LayerObject();
        mLayers.put("geofence", geofenceLayer);
        geofenceLayer.setScaleMode(LayerObject.MODE_SCALE_SPRITES);

        LayerObject switchLayer = new LayerObject();
        mLayers.put("switch", switchLayer);
        switchLayer.setScaleMode(LayerObject.MODE_SCALE_SPRITES);

        LayerObject multipathLayer = new LayerObject();
        mLayers.put("multipath", multipathLayer);
        multipathLayer.setScaleMode(LayerObject.MODE_SCALE_SPRITES);

        setOnTouchListener(this);
        // XXX: temp code
        // setCompassMode(true, true);
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public float getImageRotation() {
        return mImageRotation;
    }

    public void setImageRotation(float degrees) {
        PointF pivot = getCenter();

        if (pivot != null) {
            mImageMatrix.postRotate(-mImageRotation, pivot.x, pivot.y);
            mImageMatrix.postRotate(degrees + mImageOffsetRotation, pivot.x,
                    pivot.y);
            mImageRotation = degrees + mImageOffsetRotation;
            setImageMatrix(mImageMatrix);
            invalidate();
        }
    }

    public float getImageOffsetRotation() {
        return mImageOffsetRotation;
    }

    public void setImageOffsetRotation(float mImageOffsetRotation) {
        this.mImageOffsetRotation = mImageOffsetRotation;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        setCompassMode(false);
        mScaleDetector.onTouchEvent(event);

        float[] fa = new float[]{event.getX(), event.getY()};
        getImageMatrix().invert(mInverse);
        mInverse.mapPoints(fa);
        mTouchLocation.set(fa[0], fa[1]);

        PointF curr = new PointF(event.getX(), event.getY());

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (isInDeleteMode) {
                    mMode = NONE;
                    deleteRectmStart = new PointF(event.getX(), event.getY());
                } else {
                    mLast.set(curr);
                    mStart.set(mLast);
                    mMode = DRAG;
                }
                break;

            case MotionEvent.ACTION_MOVE:
                if (isInDeleteMode) {
                    mMode = NONE;
                    //deleteRectmLast = new PointF(event.getX(), event.getY());
                } else {

                    if (mMode == DRAG) {

                        //if(event.getPointerCount() ==1){
                        float deltaX = curr.x - mLast.x;
                        float deltaY = curr.y - mLast.y;
                        aboutToMove(deltaX, deltaY);
                        mImageMatrix.postTranslate(deltaX, deltaY);
                        limitScroll();
                        mLast.set(curr.x, curr.y);
                        if (Math.abs(deltaX) > 5 || Math.abs(deltaY) > 5) {
                            setJustMoved(true);
                        }
                        // for skew with two-fingers uncomment the following code
                        //}
//					else if(event.getPointerCount() ==2){
//
//						android.util.Log.d("Two-finger-drag", "Two-finger-drag");
//
//
//						float currfingersDist=(float) Math.sqrt(Math.pow((event.getX(0) - event.getX(1)),2) +
//			 					   Math.pow((event.getY(0) - event.getY(1)),2));
//						float deltaFingerDist=Math.abs(currfingersDist-fingersDist);
//
//						 if((event.getY(0)-firstFingerY)>0
//								 && event.getY(1)-secondFingerY>0
//								 && deltaFingerDist<1){
//							// System.out.println("drag down");
//							 android.util.Log.d("Two-finger-drag", "drag down");
//
//
//								mImageMatrix.postSkew(-1.0f*currSkewX, skewY);
//
//								currSkewX=currSkewX+0.07f;
//
//								if(currSkewX>MAX_SKEW_X){
//									currSkewX = MAX_SKEW_X;
//								}
//
//								mImageMatrix.postSkew(currSkewX, skewY);
//						 }
//
//
//						 if((event.getY(0)-firstFingerY)<0
//								 && event.getY(1)-secondFingerY<0
//								 && deltaFingerDist<1){
//							 //System.out.println("drag up");
//							 android.util.Log.d("Two-finger-drag", "drag up");
//
//							 if(currSkewX>=0){
//							 	mImageMatrix.postSkew(-1.0f*currSkewX, skewY);
//							 }
//								currSkewX=currSkewX-0.07f;
//
//								if(currSkewX<0){
//									currSkewX = 0;
//								}
//
//								mImageMatrix.postSkew(currSkewX, skewY);
//						 }
//
//
//
//						 if((event.getX(0)-firstFingerX)>0
//								 && event.getX(1)-secondFingerX>0){
//							 System.out.println("drag right");
//						 }
//
//
//						 if((event.getX(0)-firstFingerX)<0
//								 && event.getX(1)-secondFingerX<0){
//							 System.out.println("drag left");
//						 }
//
//
//
//						 firstFingerX=event.getX(0);
//						 firstFingerY=event.getY(0);
//						 secondFingerX=event.getX(1);
//						 secondFingerY=event.getY(1);
//
//						 fingersDist=(float) Math.sqrt(Math.pow((firstFingerX - secondFingerX),2) +
//								 					   Math.pow((firstFingerY - secondFingerY),2));
//
//						System.out.println("(fX,fy)="+firstFingerX+","+firstFingerY);
//						System.out.println("(sX,sy)="+secondFingerX+","+secondFingerY);
//
//
//					}
                    }
                }
                break;

            case MotionEvent.ACTION_UP:
                if (isInDeleteMode) {
                    deleteRectmLast = new PointF(event.getX(), event.getY());
                }
                setCompassMode(mRestoreCompassMode);
                mMode = NONE;
                float dx = curr.x - mStart.x;
                float dy = curr.y - mStart.y;
                float dr = dx * dx + dy * dy;
                long dt = event.getEventTime() - event.getDownTime();
                if (dr < CLICK_PROX && dt < CLICK_PERIOD) {
                    performClick();
                }
                setJustMoved(false);
                break;

            case MotionEvent.ACTION_POINTER_UP:
                mMode = NONE;
                break;
        }

        setImageMatrix(mImageMatrix);
        invalidate();
        return true;
    }

    protected void aboutToMove(float deltaX, float deltaY) {
        if (Math.abs(deltaX) > 5 || Math.abs(deltaY) > 5) {
            setCompassMode(false, false);
            setFollowMeMode(false);
        }

    }

    public boolean getFollowMeMode() {
        return followMeMode;
    }

    public void setFollowMeMode(boolean b) {
        followMeMode = b;

    }

    public void setMaxZoom(float x) {
        mMaxScale = x;
    }

    public PointF getTouchLocation() {
        return mTouchLocation;
    }

    public void showGrid(boolean visible) {
        mGrid.setVisible(visible, false);
    }

    //Addtry
    protected void limitScroll() {
        try {
            RectF cb = getContentBounds();
            int vcx = getWidth() / 2;
            int vcy = getHeight() / 2;

            float xBalance = 0;
//		if (cb.width() > vcx * 2) { // limit to view center x to allow rotation
            // around corners.
            if (cb.left > vcx)
                xBalance = vcx - cb.left;
            else if (cb.right < vcx)
                xBalance = vcx - cb.right;
            // xBalance = cb.left > 0 ? - cb.left : (cb.right < vw ? vw -
            // cb.right : 0); // Schwiss Clamp to center.
//		} else { // center in view.
//			xBalance = vcx - (cb.left + cb.width() / 2);
//		}

            float yBalance = 0;
//		if (cb.height() > vcy * 2) { // as for x.
            if (cb.top > vcy)
                yBalance = vcy - cb.top;
            else if (cb.bottom < vcy)
                yBalance = vcy - cb.bottom;
            // yBalance = cb.top > 0 ? - cb.top : (cb.bottom < getHeight() ?
            // getHeight() - cb.bottom : 0); // Schwiss Clamp to edges.
//		} else {
//			yBalance = vcy - (cb.top + cb.height() / 2);
//		}

            if (xBalance != 0 || yBalance != 0) {
                mImageMatrix.postTranslate(xBalance, yBalance);
            }

            invalidate();
        } catch (Throwable t) {
            Log.getInstance().error(TAG, t.getMessage(), t);
        }
    }

    public float getZoomLevel() {
        float[] values = new float[9];
        mImageMatrix.getValues(values);
        float scale = values[Matrix.MSCALE_X];
        return scale;
    }

    //XXX Null pointer exp
    protected RectF getContentBounds() {

        Drawable d = getDrawable();
        if (d != null) {
            RectF r = new RectF(d.getBounds());
            mImageMatrix.mapRect(r);
            return r;
        }
        return new RectF();
    }

    public void setImageRotationAnimation(float degrees) {
/*
//		clearAnimation();
		RotateImageAnimation rot = new RotateImageAnimation(degrees, this);
		rot.setDuration(550);
		rot.setFillAfter(true);
//		startAnimation(rot);
		AnimationsHolder.getInstance().addAnimation(rot, this);
*/
        applyRotationAnimation(degrees);

    }

    public void updateCompassRotation() {
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        int screenRotation = wm.getDefaultDisplay().getRotation();
        float degrees = OrientationMonitor.getInstance().getAzimuth(screenRotation);
        applyRotationAnimation(-degrees);

//		WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
//		int screenRotation = wm.getDefaultDisplay().getRotation();
//		float scrot = OrientationMonitor.getInstance().getAzimuth(screenRotation);
//		//float r = getImageRotation();
//		//setImageRotation(-rot);
//		lastRotation =scrot;
//		float degrees = -scrot;
//		RotateImageAnimation rot = new RotateImageAnimation(degrees, this);
//		rot.setDuration(400);
//		rot.setFillAfter(true);
////		startAnimation(rot);
//		AnimationsHolder.getInstance().addAnimation(rot, this);
////	   setImageRotationAnimation(-rot);
    }

    public void applyRotationAnimation(float degrees) {

        float toAngle = (degrees + 360) % 360;
        float fromAngle = 0;

//		if (PropertyHolder.getInstance().isLocationPlayer()) {
//			fromAngle = ((getImageRotation() - FacilityContainer.getInstance().getSelected().getFloorRotation()) + 360)%360;
//		} else {
        FacilityConf facConf = FacilityContainer.getInstance().getCurrent();
        if (facConf != null) {
            fromAngle = ((getImageRotation() - facConf.getFloorRotation()) + 360) % 360;
        }
//		}

        RotateImageAnimation animation = new RotateImageAnimation(this, fromAngle, toAngle);
        animation.setDuration(550);
        animation.setFillEnabled(true);
        animation.setFillAfter(true);
        AnimationsHolder.getInstance().addAnimation(animation, this);
    }

    public PointF getCenter() { // in view coordinates!
        PointF center = new PointF(0, 0);
        if (mCenter == null) {
            if (getDrawable() != null) {
                Rect db = getDrawable().getBounds();
                center = new PointF(db.centerX(), db.centerY());
            }
        } else {
            center = mCenter;
        }
        return mapToView(center);
    }

    public void SetCenter(PointF center) { // by image coordinates!
        if (center == null)
            return;

        mCenter = center;
        center = mapToView(center);
        mImageMatrix.postTranslate(getWidth() / 2 - center.x, getHeight() / 2
                - center.y);
        setImageMatrix(mImageMatrix);

    }

    public void moveTo(PointF nCenter) {
        if (nCenter == null)
            return;
//		clearAnimation();
        CenterImageAnimation trans = new CenterImageAnimation(nCenter, this);
        trans.setDuration(500);
        trans.setFillAfter(true);
//		startAnimation(trans);
        AnimationsHolder.getInstance().addAnimation(trans, this);
    }

    public PointF mapToView(PointF p) {
        float[] fa = {p.x, p.y};
        mImageMatrix.mapPoints(fa);
        return new PointF(fa[0], fa[1]);
    }

    public PointF invertMapToView(PointF p) {
        float[] fa = {p.x, p.y};
        Matrix m = new Matrix();
        mImageMatrix.invert(m);
        m.mapPoints(fa);
        return new PointF(fa[0], fa[1]);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int viewWidth = getWidth();
        int viewHeight = getHeight();
        if (viewWidth == 0 || viewHeight == 0) // Normally on first (measure)
            // layout pass.
            return;

        if (saveScale == 1) {
            // Fit to screen.
            Drawable dr = getDrawable();
            int drHeight;
            int drWidth;
            if (dr == null || (drWidth = dr.getIntrinsicWidth()) == 0
                    || (drHeight = dr.getIntrinsicHeight()) == 0)
                return;

            float scaleX = (float) viewWidth / drWidth;
            float scaleY = (float) viewHeight / drHeight;
            float scale = Math.min(scaleX, scaleY);
            mImageMatrix.setScale(scale, scale);

            // Center the image
            mOrigWidth = scale * drWidth;
            mOrigHeight = scale * drHeight;
            float redundantYSpace = (viewHeight - mOrigHeight) / 2;
            float redundantXSpace = (viewWidth - mOrigWidth) / 2;
            mImageMatrix.postTranslate(redundantXSpace, redundantYSpace);

            mImageMatrix.postRotate(mImageRotation, drWidth / 2, drHeight / 2);
            setImageMatrix(mImageMatrix);
        }
        limitScroll();
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        if (bm == null)
            return;
//		if(mBitmap!=null){
//			mBitmap.recycle();
//		}

        mBitmap = bm;
        super.setImageBitmap(bm);
        Rect r = new Rect(0, 0, bm.getWidth(), bm.getHeight());
        mGrid.setBounds(r);
    }

    @Override
    protected void onDraw(Canvas canvas) {
//		float values[]=new float[9];
//		Matrix m=new Matrix();
//		m.getValues(values);
//		values[7]=values[7]+0.0002f;
//		values[8]=values[8]+0.001f;
//		m.setValues(values);
//		canvas.setMatrix(m);
        super.onDraw(canvas);
        drawLayers(canvas);
        // XXX: Visual debug assisting code
        drawDebug(false, canvas);
        //draw rectangle to delete matrix if needed

        drawDeleteMatrixRect(isInDeleteMode, canvas);
    }

    protected void drawDebug(boolean b, Canvas canvas) {
        if (b) {
            RectF r = getContentBounds();
            Paint p = new Paint();
            p.setAlpha(50);
            canvas.drawRect(r, p);

            int centerx = (int) (r.width() / 2 + r.left);
            int centery = (int) (r.height() / 2 + r.top);
            p.setStrokeWidth(20);
            p.setColor(Color.RED);
            canvas.drawPoint(centerx, centery, p);

            p.setColor(Color.BLUE);
            p.setStrokeWidth(10);
            PointF pivot = getCenter();
            canvas.drawPoint(pivot.x, pivot.y, p);

            p.setColor(Color.MAGENTA);
            // float[] fa = new float[9];
            // mImageMatrix.getValues(fa);
            // PointF prev = new PointF(fa[Matrix.MTRANS_X],
            // fa[Matrix.MTRANS_Y]);
            PointF prev = getTouchLocation();
            float[] fa = new float[]{prev.x, prev.y};
            mImageMatrix.mapPoints(fa);
            canvas.drawPoint(fa[0], fa[1], p);
        }
    }

    protected void drawLayers(Canvas canvas) {

        for (String layerName : mLayers.keySet()) {
            LayerObject layer = mLayers.get(layerName);
            boolean isShow = PropertyHolder.getInstance().isShowLayerOnMap(layerName);
            if (layer != null) {
                if (isShow) {
                    layer.show();
                } else {
                    layer.hide();
                }
            }
            layer.draw(canvas, mImageMatrix);
        }
    }

    public void drawDeleteMatrixRect(boolean b, Canvas canvas) {
        if (b) {


            isInDeleteMode = b;
            int tempmMode = mMode;
            mMode = NONE;
            Paint p = new Paint();
            p.setStyle(Style.STROKE);
            p.setStrokeWidth(5);
            p.setColor(Color.GREEN);
            if (deleteRectmLast != null && deleteRectmStart != null) {

                deleteMatrixArea.set(deleteRectmStart.x, deleteRectmStart.y, deleteRectmLast.x, deleteRectmLast.y);
                //RectF r = new RectF(deleteRectmStart.x, deleteRectmStart.y, deleteRectmLast.x, deleteRectmLast.y);
                //canvas.drawRect(r, p);
                canvas.drawRect(deleteMatrixArea, p);

                //remember to null those after the file writing
                deleteRectmStart = null;
                deleteRectmLast = null;
                isInDeleteMode = false;
            }
            mMode = tempmMode;

        }
    }

    public RectF getDeleteMatrixArea() {
        return deleteMatrixArea;
    }

    public void setDeleteMatrixArea(RectF deleteMatrixArea) {
        this.deleteMatrixArea = deleteMatrixArea;
    }

    public int getmMode() {
        return mMode;
    }

    public void setmMode(int mMode) {
        this.mMode = mMode;
    }

    public boolean getIsInDeleteMode() {
        return isInDeleteMode;
    }

    public void setIsInDeleteMode(boolean deleteMode) {
        this.isInDeleteMode = deleteMode;
    }

    public LayerObject getLayerByName(String name) {
        return mLayers.get(name);
    }

    public void addMark(Drawable sd) {
        LayerObject layer = mLayers.get("marks");
        layer.addSprite(sd);
    }

    public void clearMarks() {
        mLayers.get("marks").clearSprites();
    }

    public void addPOI(Drawable poi) {
        LayerObject layer = mLayers.get("poi");
        layer.addSprite(poi);
    }

    public void addDrawableLabel(Drawable label) {
        LayerObject layer = mLayers.get("labels_layer");
        layer.addSprite(label);
    }

    public void addLables(Drawable gts) {
        LayerObject layer = mLayers.get("lables");
        layer.addSprite(gts);
    }

    public Map<String, LayerObject> getLayers() {
        return mLayers;
    }

    public void setCompassMode(boolean compass, boolean persist) {
        mRestoreCompassMode = persist;

        setCompassMode(compass);
    }

    public boolean isAnimated() {
        return mAnimated;
    }

    public void setAnimated(boolean animate) {
        mAnimated = animate;
    }

    public boolean isCompassMode() {
        return mCompassMode;
    }

    public void setCompassMode(boolean compass) {


        if (compass == false) {
            clearAnimation();
        }
        if (mCompassMode == compass)
            return;

        if (compass) {
            mRotor.connectSensors();
        } else {
            clearAnimation();
            mRotor.disconnectSensors();
            if (!mRestoreCompassMode) {
                // setImageRotation(0);
            }
        }
        mCompassMode = compass;
    }

    public void addGis(GisSprite gs) {
        LayerObject layer = mLayers.get("gis");
        layer.addSprite(gs);

    }

    public void setZoom(float zoom) {
        float factor = zoom;
        float oldZ = saveScale;
        float origScale = saveScale;
        saveScale *= factor;
        if (saveScale > mMaxScale) {
            saveScale = mMaxScale;
            factor = mMaxScale / origScale;
        } else if (saveScale < mMinScale) {
            saveScale = mMinScale;
            factor = mMinScale / origScale;
        }

        if (mOrigWidth * saveScale <= getWidth()
                || mOrigHeight * saveScale <= getHeight())
            mImageMatrix.postScale(factor, factor, getWidth() / 2,
                    getHeight() / 2);
        else
            mImageMatrix.postScale(factor, factor);
        // mImageMatrix.postScale(factor, factor,
        // detector.getFocusX(), detector.getFocusY());
        //invalidate();
        //XXX ZOOM FIX
        limitScroll();

        float currentZ = saveScale;

        if (type != IMG_TYPE_ROUTEOVERVIEW) {
            if (currentZ >= ZOOM_LEVEL_THR && oldZ <= ZOOM_LEVEL_THR
                    || currentZ <= ZOOM_LEVEL_THR && oldZ >= ZOOM_LEVEL_THR) {
                PoiDataHelper.getInstance().drawPois(TouchImageView.this);
                invalidate();
            }
        }
        setImageMatrix(mImageMatrix);

        invalidate();
    }

    public float getSaveScale() {
        return saveScale;
    }

    ;

    public void setSaveScale(float saveScale) {
        this.saveScale = saveScale;
    }

    public boolean isJustMoved() {
        return justMoved;
    }

    public void setJustMoved(boolean justMoved) {
        this.justMoved = justMoved;
    }

    public void setMapSkew(float skewX) {

        //	if(isFirstTime){
        //
        //				isFirstTime=false;

        //				perspectiveProjectionOnTop();
        //perspectiveProjectionOnBottom();

        mImageMatrix.postSkew(-1.0f * currSkewX, 0);


        if (skewX > MAX_SKEW_X) {
            skewX = MAX_SKEW_X;
        }

        mImageMatrix.postSkew(skewX, 0);

        currSkewX = skewX;


        limitScroll();

        setImageMatrix(mImageMatrix);

        invalidate();
        //	}
    }

    private void perspectiveProjectionOnBottom() {
        float values[] = new float[9];
        mImageMatrix.getValues(values);
//		values[0]=values[0]+0.001f;
//		values[1]=values[1]+0.001f;
//		values[2]=values[2]+0.001f;
//		values[3]=values[3]+0.001f;
//		values[4]=values[4]+0.001f;
//		values[5]=values[5]+0.001f;
//		values[6]=values[6]-0.0005f;   //(+right)  +0.001f//
        values[7] = values[7] + 0.001f; //(+top)    //-0.0002f; (-bottom)
        values[8] = values[8] + 0.5f;

        mImageMatrix.setValues(values);


        limitScroll();

        setImageMatrix(mImageMatrix);

        invalidate();
    }

    private void perspectiveProjectionOnTop() {
        float values[] = new float[9];
        mImageMatrix.getValues(values);
//		values[0]=values[0]+0.001f;
//		values[1]=values[1]+0.001f;
//		values[2]=values[2]+0.001f;
//		values[3]=values[3]+0.001f;
//		values[4]=values[4]+0.001f;
//		values[5]=values[5]+0.001f;
//		values[6]=values[6]-0.0005f;
        values[7] = values[7] + 0.002f;
        values[8] = values[8] + 0.1f;

        mImageMatrix.setValues(values);


        limitScroll();

        setImageMatrix(mImageMatrix);

        invalidate();
    }

    private void perspectiveFake3D(float rotateAngle) {

//		//Create a new image bitmap and attach a brand new canvas to it
//		Bitmap tempBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.RGB_565);
//		Canvas tempCanvas = new Canvas(tempBitmap);
//
//		//Draw the image bitmap into the cavas
//		Bitmap bitmap = ((BitmapDrawable)this.getDrawable()).getBitmap();
//
//		float values[]=new float[9];
//
//		Matrix m=new Matrix();
//		m.getValues(values);
//		values[6]= 0.1f;
//		m.setValues(values);
//
//		tempCanvas.drawBitmap(bitmap, 0, 0, null);
//		tempCanvas.setMatrix (m);
//		this.draw(tempCanvas);
//		//limitScroll();
//		invalidate();

        float values[] = new float[9];

        mImageMatrix.getValues(values);
        System.out.println("perspectiveFake3D=" + rotateAngle);

        if (rotateAngle > 0 && rotateAngle < 180) {
            Matrix m = new Matrix();
            values[6] = 0.002f;
            m.setValues(values);


            setImageMatrix(m);
            limitScroll();
            invalidate();
        } else {


            setImageMatrix(mImageMatrix);
            limitScroll();
            invalidate();
        }

    }

    public Bitmap getmBitmap() {
        return mBitmap;
    }

    public void recoverState(Matrix mapmatrix, float mapzoom) {

        mImageMatrix = mapmatrix;
        setSaveScale(mapzoom);
        setImageMatrix(mapmatrix);
        PoiDataHelper.getInstance().drawPois(TouchImageView.this);
        invalidate();
    }

    protected class ScaleListener extends
            ScaleGestureDetector.SimpleOnScaleGestureListener {


        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            mMode = ZOOM;
            setJustMoved(true);
            return true;
        }

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            float factor = detector.getScaleFactor();
            float oldZ = saveScale;
            float origScale = saveScale;
            saveScale *= factor;
            if (saveScale > mMaxScale) {
                saveScale = mMaxScale;
                factor = mMaxScale / origScale;
            } else if (saveScale < mMinScale) {
                saveScale = mMinScale;
                factor = mMinScale / origScale;
            }

            if (mOrigWidth * saveScale <= getWidth()
                    || mOrigHeight * saveScale <= getHeight())
                mImageMatrix.postScale(factor, factor, getWidth() / 2,
                        getHeight() / 2);
            else
                mImageMatrix.postScale(factor, factor, detector.getFocusX(),
                        detector.getFocusY());

            limitScroll();

            float currentZ = saveScale;
            if (type == IMG_TYPE_ROUTEOVERVIEW) {
                return true;
            }

            if (currentZ >= ZOOM_LEVEL_THR && oldZ <= ZOOM_LEVEL_THR ||
                    currentZ <= ZOOM_LEVEL_THR && oldZ >= ZOOM_LEVEL_THR) {
                PoiDataHelper.getInstance().drawPois(TouchImageView.this);
                invalidate();
            }

            return true;
        }
    }

    protected class OrientationObsrver implements Observer {
        OrientationMonitor mMonitor = OrientationMonitor.getInstance();

        protected void connectSensors() {
            mMonitor.addObserver(this);
        }

        protected void disconnectSensors() {
            mMonitor.deleteObserver(this);
        }

        @Override
        public void update(Observable omon, Object ignore) {
//			orientationCounter++;
//			//if (orientationCounter >= orientationCounterThreshold) {
//				WindowManager wm = (WindowManager) getContext()
//						.getSystemService(Context.WINDOW_SERVICE);
//				int screenRotation = wm.getDefaultDisplay().getRotation();
//				float rot = ((OrientationMonitor) omon)
//						.getAzimuth(screenRotation);
//				float r = getImageRotation();
//			//	if (Math.abs(rot - lastRotation) > rotationThreshold) {
//					setImageRotation(-rot);
//					lastRotation = rot;
//			//	}
//				orientationCounter = 0;
//			//}
        }
    }

    public class CenterImageAnimation extends Animation {

        // public CenterImageAnimation(Context context, AttributeSet attrs) {
        // super(context, attrs);
        // }

        TouchImageView view = null;
        private PointF toCenter;
        private float dX;
        private float dY;
        private PointF center = null;

        public CenterImageAnimation(PointF toCenter, TouchImageView tiv) {
            super();

            view = tiv;
            if (mCenter == null) {
                mCenter = new PointF(tiv.getWidth() / 2, tiv.getHeight() / 2);
            }
            this.toCenter = toCenter;

            dX = toCenter.x - mCenter.x;
            dY = toCenter.y - mCenter.y;

        }

        @Override
        protected void applyTransformation(float interpolatedTime,
                                           Transformation t) {
            float rit = 1.0f - interpolatedTime;
            center = new PointF(toCenter.x - rit * dX, toCenter.y - rit * dY);
            view.SetCenter(center);
            //if (PropertyHolder.getInstance().isRotatingMap()) {
            if (PropertyHolder.getInstance().getRotatingMapType() == MapRotationType.COMPASS) {
                float degrees = OrientationMonitor.getInstance().getAzimuth();
                view.setImageRotation(-degrees);
                //			PointF c = mapToView(mCenter);
                //			mImageMatrix.postTranslate(getWidth() / 2 - c.x, getHeight() / 2
                //					- c.y);
                //			setImageMatrix(mImageMatrix);
                //perspectiveFake3D(degrees);
            }
        }


    }

}