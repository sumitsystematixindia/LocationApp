package com.mlins.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.Transformation;

import com.mlins.label.LabelDrawableSprite;
import com.mlins.label.LabelsInfoHelper;
import com.mlins.maping.IconSprite;
import com.mlins.maping.LayerObject;
import com.mlins.maping.LineSprite;
import com.mlins.maping.PoiBubbleSprite;
import com.mlins.maping.RectSprite;
import com.mlins.orientation.OrientationMonitor;
import com.mlins.utils.AnimationsHolder;
import com.mlins.utils.FacilityConf;
import com.mlins.utils.FacilityContainer;
import com.mlins.utils.GisDrawHelper;
import com.mlins.utils.PoiData;
import com.mlins.utils.PoiDataHelper;
import com.mlins.utils.PropertyHolder;
import com.mlins.utils.logging.Log;
import com.mlins.view.ctrls.BaseCtrl;
import com.mlins.view.ctrls.FloorList;
import com.mlins.view.ctrls.FloorMenu;
import com.mlins.view.ctrls.TurnMap;
import com.spreo.geofence.GeoFenceHelper;
import com.spreo.geofence.GeoFenceObject;
import com.spreo.geofence.GeoFenceRect;
import com.spreo.interfaces.SpreoMapViewListener;
import com.spreo.nav.enums.MapRotationType;
import com.spreo.nav.interfaces.IPoi;
import com.spreo.spreosdk.R;

import java.util.ArrayList;
import java.util.List;

public class d2dTouchImageView extends MyLocationView implements
        OnTouchListener, IFloorSetter {
    private final static String TAG = "com.com.mlins.views.d2dTouchImageView";

    private static final long BUBBLE_HIDE_TIME = 3000;
    protected Context ctx;
    protected PoiBubbleSprite poibubble = null;
    protected List<PoiBubbleSprite> openPoiBubbles = new ArrayList<PoiBubbleSprite>();
    List<BaseCtrl> ctrls = new ArrayList<BaseCtrl>();
    private int width;
    private int height;
    private IFloorSetter navView;
    private FloorList flist;
    private FloorMenu floorButton;
    private TurnMap turnmap;
    private long timeOfTouchEvent;
    private List<SpreoMapViewListener> poiListeners = new ArrayList<SpreoMapViewListener>();

    public d2dTouchImageView(Context context) {
        super(context);
        ctx = context;

    }

    public d2dTouchImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOnTouchListener(this);
    }

    public void setListener(SpreoMapViewListener listener) {
        if (!poiListeners.contains(listener)) {
            poiListeners.add(listener);
        }
    }

    protected void aboutToMove(float deltaX, float deltaY) {
        super.aboutToMove(deltaX, deltaY);
        if (Math.abs(deltaX) > 5 || Math.abs(deltaY) > 5) {
            if (turnmap != null && !PropertyHolder.getInstance().isSdkObserverMode()) {
//				turnmap.setIsHidden(false);
            }

        }

    }

    public void hideTarget() {
        if (turnmap != null) {
            turnmap.setIsHidden(true);
        }
    }

    public void showTarget() {
        if (turnmap != null) {
            turnmap.setIsHidden(false);
        }
    }

    public void createCtrls() {
        Log.getInstance().debug(TAG, "Enter, createCtrls()");

        ctrls.clear();

        turnmap = new TurnMap();
        turnmap.setX(30);
        turnmap.setY(height - 300);
        Bitmap turnbitmap = BitmapFactory.decodeResource(getResources(),
                R.drawable.target);
        turnmap.setBitmap(turnbitmap);
        turnmap.setIsHidden(true);
        ctrls.add(turnmap);
        setCtrlParent();
        Log.getInstance().debug(TAG, "Exit, createCtrls()");

    }

    private void setCtrlParent() {
        for (BaseCtrl element : ctrls) {
            element.setParent(this);
        }

    }

    public void addCtrl(BaseCtrl ctrl) {
        ctrls.add(ctrl);
        ctrl.setParent(this);
    }

    public void removeCtrl(BaseCtrl ctrl) {
        ctrls.remove(ctrl);
    }

    @Override
    public void draw(Canvas canvas) {
        // TODO Auto-generated method stub
        super.draw(canvas);
        for (BaseCtrl element : ctrls) {
            element.draw(canvas);
        }


    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {


        ctx = PropertyHolder.getInstance().getMlinsContext();

        long t = System.currentTimeMillis();
        setTimeOfTouchEvent(t);
        if (event.getAction() == MotionEvent.ACTION_UP && !isJustMoved()) {
            for (BaseCtrl element : ctrls) {
                if (element.clickTest(event.getX(), event.getY())) {
                    return true;
                }
            }
            PointF p = new PointF(getTouchLocation().x, getTouchLocation().y);
            if (poibubble != null) {
                IPoi poi = poibubble.getPoi();
                boolean clicked = poibubble.isClicked(mapToView(p));
                if (clicked) {

                    //openPoiDialog(poibubble.getPoi(), v);

                    SetCenter(poi.getPoint());
                    getLayers().get("buble").removeSprite(poibubble);
                    invalidate();

                    return true;
                }
                getLayers().get("buble").removeSprite(poibubble);
                poibubble = null;
            }

            IPoi clickedPoiBuble = null;
            for (PoiBubbleSprite o : openPoiBubbles) {
                if (!o.isHidden() && o.isClicked(p)) {
                    //openPoiDialog(o.getPoi(), v);
                    clickedPoiBuble = o.getPoi();


//					
                    //	return true;
                }
            }

            if (clickedPoiBuble != null) {
                for (SpreoMapViewListener l : poiListeners) {
                    l.onBubbleClick(clickedPoiBuble);
                }
                return true;
            }

            PoiData poi = PoiDataHelper.getInstance().poiExists(p);
            if (poi != null) {

                for (SpreoMapViewListener l : poiListeners) {
                    l.onPoiClick(poi);
                }
//				SetCenter(poi.getPoint());
//				invalidate();

                return true;
            }

            // Label on click check
            LayerObject labelsSpritesLayer = getLayers().get("labels_layer");
            if (labelsSpritesLayer != null) {
                List<Drawable> lblsList = labelsSpritesLayer.getmSprites();
                if (lblsList != null) {
                    for (Drawable d : lblsList) {
                        if (d != null && d instanceof LabelDrawableSprite) {
                            LabelDrawableSprite lds = (LabelDrawableSprite) d;
                            if (lds.isClicked(p)) {
                                for (SpreoMapViewListener l : poiListeners) {
                                    l.onLabelClick(lds.getLabel());
                                }
                                return true;
                            }
                        }
                    }
                }
            }
        }

//		ILocation loc = LocationFinder.getInstance().getCurrentLocation();
//		AnimationsHolder.getInstance().reset();
//		if (loc != null && loc.getLocationType() == LocationFinder.INDOOR_MODE) {
//			PointF locpoint = new PointF((float) loc.getX(), (float) loc.getY());
//			setMyLocation(locpoint);
//		}
        return super.onTouch(v, event);
    }


    public void openClosePoiBubbles(PointF myloc) {
        Log.getInstance().debug(TAG, "Enter, openClosePoiBubbles()");

        float bubblerange = PropertyHolder.getInstance().getBubbleRange();
        int poiBubblesCount = PropertyHolder.getInstance().getPoiBubblesCount();
        List<PoiData> pois = PoiDataHelper.getInstance().getInRangePois(myloc, FacilityContainer.getInstance().getSelected().getFloorRotation(), bubblerange, poiBubblesCount);
        long now = System.currentTimeMillis();
        List<PoiBubbleSprite> oldlist = new ArrayList<PoiBubbleSprite>(openPoiBubbles);
        List<IPoi> tmpPoiList = new ArrayList<IPoi>();
        for (PoiBubbleSprite bubble : oldlist) {
            if (!bubble.isUserBubble()) {
                if (pois.contains(bubble.getPoi())) {
                    bubble.setCreationTime(now);
                    tmpPoiList.add(bubble.getPoi());

                } else if (now - bubble.getCreationTime() > BUBBLE_HIDE_TIME
                        || pois.size() >= poiBubblesCount) {
                    openPoiBubbles.remove(bubble);
                    IPoi bubblepoi = bubble.getPoi();
                    notifyBubbleClosed(bubblepoi);
                }
            }
        }

        for (PoiData o : pois) {
            boolean exists = tmpPoiList.contains(o);

            if (!o.isShowPoiBubble() || exists)
                continue;
            CreatePoibubble(o, null);
        }

        LayerObject layer = getLayers().get("buble");
        layer.clearSprites();
        for (PoiBubbleSprite o : openPoiBubbles) {
            if (o.isHidden() == false) {
                layer.addSprite(o);
            }
        }

//	invalidate();

//		openPoiBubbles.clear();
//		
//		for (PoiData o : pois) {
//			if (!o.isShowPoiBubble())
//				continue;
//				CreatePoibubble(o);	
//		}
////		
//		LayerObject layer =  getLayers().get("buble");
//		layer.clearSprites();
//		for (PoiBubbleSprite o : openPoiBubbles) {
//			//if (o.isHidden() == false) {
//				layer.addSprite(o);
//			//}
//		}
//	invalidate();
        Log.getInstance().debug(TAG, "Exit, openClosePoiBubbles()");
    }

    protected void removeBubble(IPoi poi) {
        Log.getInstance().debug(TAG, "Enter, removeBubble()");

        PoiBubbleSprite tmp = null;
        for (PoiBubbleSprite o : openPoiBubbles) {
            if (o.getPoi().equals(poi)) {
                tmp = o;
                break;
            }
        }

        if (tmp != null) {
            openPoiBubbles.remove(tmp);
            LayerObject layer = getLayers().get("buble");
            if (layer != null) {
                layer.removeSprite(tmp);
            }

            notifyBubbleClosed(poi);
        }
        Log.getInstance().debug(TAG, "Exit, removeBubble()");
    }

    public void removeAllBubbles() {
        List<IPoi> tmp = new ArrayList<IPoi>();
        for (PoiBubbleSprite o : openPoiBubbles) {
            IPoi poi = o.getPoi();
            if (poi != null) {
                tmp.add(poi);
            }
        }

        for (IPoi p : tmp) {
            removeBubble(p);
        }
    }

    protected boolean inOpenList(PoiData poi) {
        Log.getInstance().debug(TAG, "Enter, inOpenList()");

        boolean result = false;
        for (PoiBubbleSprite o : openPoiBubbles) {
            if (o.getPoi().equals(poi)) {
                result = true;
                Log.getInstance().debug(TAG, "Exit, inOpenList()");
                break;
            }
        }
        Log.getInstance().debug(TAG, "Exit, inOpenList()");
        return result;
    }

    public PoiBubbleSprite CreatePoibubble(IPoi poi, String customtext) {

        Log.getInstance().debug(TAG, "Enter, CreatePoibubble()");
        PoiBubbleSprite result = null;
        Bitmap bubblebmp = BitmapFactory.decodeResource(getResources(),
                R.drawable.poibubble);
        if (customtext != null) {
            result = new PoiBubbleSprite(bubblebmp, poi,
                    System.currentTimeMillis(), this, customtext);
        } else {
            result = new PoiBubbleSprite(bubblebmp, poi,
                    System.currentTimeMillis(), this);
        }
        PointF bubbleloc = poi.getPoint();
        result.setLoc(bubbleloc);
        View customview = null;
        for (SpreoMapViewListener l : poiListeners) {
            if (l != null) {
                try {
                    View v = l.aboutToOpenBubble(poi);
                    if (v != null) {
                        customview = v;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        if (customview != null) {
            result.setCustomView(customview);
        }

        openPoiBubbles.add(result);
        notifyBubbleOpened(poi);
        Log.getInstance().debug(TAG, "Exit, CreatePoibubble()");
        return result;
    }

    public void createUserPoiBubble(IPoi poi, String customtext) {
        if (isPoiBubbleExist(poi))
            return;
        PoiBubbleSprite sprite = CreatePoibubble(poi, customtext);
        sprite.setUserBubble(true);
        LayerObject layer = getLayers().get("buble");
        layer.addSprite(sprite);
    }


    private boolean isPoiBubbleExist(IPoi poi) {

        for (PoiBubbleSprite bubble : openPoiBubbles) {

            if (bubble.getPoi() == poi)
                return true;
        }

        return false;
    }

    public void setPreferedSize(int x, int y) {
        width = x;
        height = y;
        for (BaseCtrl element : ctrls) {
            element.setScreenSize(x, y);
        }
    }

    public void setFloorNumber(int nfloor) {
        Log.getInstance().debug(TAG, "Enter, setFloorNumber()");
//		if (nfloor != FacilityConf.getInstance().getSelectedFloor() || PropertyHolder.getInstance().isSdkObserverMode()) {

        //XXX NDK
        //AsociativeMemoryLocator.getInstance().setLastAverage(null);
        //NdkLocationFinder.getInstance().resetLastpt();

        FacilityConf facConf = FacilityContainer.getInstance().getSelected();
        facConf.setSelected(nfloor);

//			if (PropertyHolder.getInstance().isLocationPlayer()) {
////				GisData.getInstance().loadSelectedGisLines(nfloor);
//				GisData.getInstance().loadGisLines(nfloor);
//			}

        PropertyHolder.getInstance().loadProperties();
        LayerObject layer = getLayerByName("poi");
        layer.clearSprites();
        PoiDataHelper.getInstance().loadPois();
        LayerObject bubleslayer = getLayers().get("buble");
        bubleslayer.clearSprites();

        PoiDataHelper.getInstance().drawPois(this);
        GisDrawHelper.getInstance().drawGis(this);
        LabelsInfoHelper.getInstance().drawLabels(this);
        if (navView != null) {
            navView.setFloorMap();
        }

        if (flist != null && floorButton != null) {
            flist.setIsHidden(true);
            floorButton.setIsHidden(false);
        }
        // setCompassMode(true);
        invalidate();
        //if (PropertyHolder.getInstance().isRotatingMap()) {
        if (PropertyHolder.getInstance().getRotatingMapType() == MapRotationType.COMPASS) {
            setCompassMode(true, true);

        }
        try {

            if (!PropertyHolder.getInstance().isSdkObserverMode() && PropertyHolder.getInstance().getRotatingMapType() != MapRotationType.STATIC) {
                setImageRotation(-FacilityContainer.getInstance().getSelected().getFloorRotation());
                setImageOffsetRotation(FacilityContainer.getInstance().getSelected().getFloorRotation());
            }

        } catch (Throwable t) {
            Log.getInstance().error(TAG, t.getMessage(), t);

        }

//			List<String> floorsound = new ArrayList<String>();
//			
////			if (nfloor > 0) {
//				floorsound.add("floor");
//				int title = nfloor + 1; // FacilityConf.getInstance().getFloorTitle(nfloor);
//				//			if (!title.isEmpty()){
//				floorsound.add("floor" + title);
//				//			}
//				SoundPlayer.getInstance().play(floorsound);
////			}
        Log.getInstance().debug(TAG, "Exit, setFloorNumber()");
//		}
    }

    public void drawGeofence() {
        Log.getInstance().debug(TAG, "Enter, drawGeofence()");
        FacilityConf facConf = FacilityContainer.getInstance().getSelected();
        int floor = facConf.getSelectedFloor();
        LayerObject geofencelayer = getLayerByName("geofence");
        geofencelayer.clearSprites();
        List<GeoFenceObject> gofences = GeoFenceHelper.getInstance().getZonesByZ(floor);
        for (GeoFenceObject o : gofences) {
            GeoFenceRect g = (GeoFenceRect) o;
            RectF r = g.getZone();
            RectSprite rect = new RectSprite(r);
            geofencelayer.addSprite(rect);
        }
        geofencelayer.show();
        Log.getInstance().debug(TAG, "Exit, drawGeofence()");
    }

    public void drawSwitchFloors() {
//		List<SwitchFloorObj> allswitchs = SwitchFloorHolder.getInstance().getSwichFloorPoints();
//		List<SwitchFloorObj> floorswitchs = new ArrayList<SwitchFloorObj>();
//		FacilityConf facConf =  FacilityContainer.getInstance().getSelected();
//		int currentz = facConf.getSelectedFloor();
//		for (SwitchFloorObj o : allswitchs) {
//			if (o.getZ() == currentz) {
//				floorswitchs.add(o);
//			}
//		}
//		
//		LayerObject switchlayer = getLayerByName("switch");
//		switchlayer.clearSprites();
//		for (SwitchFloorObj s : floorswitchs) {
//			PointF point = s.getPoint();
//			int color = Color.parseColor("#000000");
//			int r = 5;
//			int x = (int) point.x;
//			int y = (int) point.y;
//			ShapeDrawable sd = new ShapeDrawable(new OvalShape());
//			sd.getPaint().setColor(color);
//			sd.setBounds(x - r, y - r, x + r, y + r);
////			addMark(sd);
//			switchlayer.addSprite(sd);
//		}
//		
//		switchlayer.show();

    }

    public void setActivity(IFloorSetter navview) {
        navView = navview;

    }

    @Override
    public void setFloorMap() {

    }


    @Override
    public void setNavigationPath() {
        Log.getInstance().debug(TAG, "Enter, setNavigationPath()");
        navView.setNavigationPath();
        Log.getInstance().debug(TAG, "Exit, setNavigationPath()");

    }

    public void drawTurnBack(PointF wifipoint, double imageangle) {
        Log.getInstance().debug(TAG, "Enter, drawTurnBack()");
        Context context = PropertyHolder.getInstance().getMlinsContext();
        Bitmap turnbackbmp = BitmapFactory.decodeResource(
                context.getResources(), R.drawable.turn_back);
        turnbackbmp = LineSprite.RotateBitmap(turnbackbmp, imageangle);
        LayerObject layer = getLayerByName("path");
        IconSprite is = new IconSprite(turnbackbmp);
        is.setLoc(wifipoint);
        layer.addSprite(is);
        Log.getInstance().debug(TAG, "Exit, drawTurnBack()");
    }

    public void checkTimeout() {
        Log.getInstance().debug(TAG, "Enter, checkTimeout()");
        long t = System.currentTimeMillis();
        for (PoiBubbleSprite o : openPoiBubbles) {
            if (!o.equals(poibubble) && (t - o.getCreationTime()) > 4000) {
                o.setHidden(true);
            }
        }
        Log.getInstance().debug(TAG, "Exit, checkTimeout()");
    }

    public long getTimeOfTouchEvent() {
        return timeOfTouchEvent;
    }

    public void setTimeOfTouchEvent(long timeOfTouchEvent) {
        this.timeOfTouchEvent = timeOfTouchEvent;
    }

    public void moveMyLocationAndMapTo(PointF p) {
        Log.getInstance().debug(TAG, "Enter, moveMyLocationAndMapTo()");
        if (p == null)
            return;
//		clearAnimation();
        CenterMyLocationImageAnimation trans = new CenterMyLocationImageAnimation(p, this);
        trans.setDuration(300);
        trans.setFillAfter(true);
//		startAnimation(trans);
        //AnimationsHolder.getInstance().reset();
        AnimationsHolder.getInstance().addAnimation(trans, this);
        Log.getInstance().debug(TAG, "Exit, moveMyLocationAndMapTo()");
    }

    public void moveMyLocationTo(PointF p) {
        Log.getInstance().debug(TAG, "Enter, moveMyLocationTo()");
        if (p == null)
            return;
//		clearAnimation();
        MoveMyLocationImageAnimation trans = new MoveMyLocationImageAnimation(p, this);
        trans.setDuration(300);
        trans.setFillAfter(true);
//		startAnimation(trans);
        AnimationsHolder.getInstance().reset();
        AnimationsHolder.getInstance().addAnimation(trans, this);


        Log.getInstance().debug(TAG, "Exit, moveMyLocationTo()");
    }

    public void reDrawPois() {
        PoiDataHelper.getInstance().drawPois(this);
    }

    private void notifyBubbleOpened(IPoi poi) {
        if (poi == null) {
            return;
        }
        for (SpreoMapViewListener l : poiListeners) {
            if (l != null) {
                try {
                    l.onBubbleOpend(poi);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void notifyBubbleClosed(IPoi poi) {
        if (poi == null) {
            return;
        }
        for (SpreoMapViewListener l : poiListeners) {
            if (l != null) {
                try {
                    l.onBubbleClosed(poi);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * display or hide pois on map
     *
     * @param display true for display pois, false for hiding pois
     */
    public void displayPois(boolean display) {
        if (display) {

            PropertyHolder.getInstance().setLayerOnMap("poi", true);
            PropertyHolder.getInstance().setLayerOnMap("buble", true);
//			LayerObject poilayer = getLayerByName("poi");
//			poilayer.show();
//			LayerObject bubleslayer = getLayerByName("buble");
//			bubleslayer.show();

            this.invalidate();

        } else {

            PropertyHolder.getInstance().setLayerOnMap("poi", false);
            PropertyHolder.getInstance().setLayerOnMap("buble", false);

//			LayerObject poilayer = getLayerByName("poi");
//			poilayer.clearSprites();
//			poilayer.hide();
//
//			LayerObject bubleslayer = getLayerByName("buble");
//			bubleslayer.clearSprites();
//			bubleslayer.hide();

            this.invalidate();


        }
    }

    public class CenterMyLocationImageAnimation extends Animation {
        private final static String TAG = "com.com.mlins.views.CenterMyLocationImageAnimation";


        // public CenterImageAnimation(Context context, AttributeSet attrs) {
        // super(context, attrs);
        // }
        d2dTouchImageView view = null;
        private PointF toCenter;
        private float dX;
        private float dY;
        private PointF center = null;

        public CenterMyLocationImageAnimation(PointF toCenter, d2dTouchImageView tiv) {
            super();
            Log.getInstance().debug(TAG, "Enter, CenterMyLocationImageAnimation()");

            view = tiv;
            if (mCenter == null) {
                mCenter = new PointF(tiv.getWidth() / 2, tiv.getHeight() / 2);
            }
            this.toCenter = toCenter;

            dX = toCenter.x - mCenter.x;
            dY = toCenter.y - mCenter.y;
            Log.getInstance().debug(TAG, "Exit, CenterMyLocationImageAnimation()");
        }

        @Override
        protected void applyTransformation(float interpolatedTime,
                                           Transformation t) {
            Log.getInstance().debug(TAG, "Enter, applyTransformation()");
            float rit = 1.0f - interpolatedTime;
            center = new PointF(toCenter.x - rit * dX, toCenter.y - rit * dY);
            setMyLocation(center);
            view.SetCenter(center);
            //if (PropertyHolder.getInstance().isRotatingMap()) {
            if (PropertyHolder.getInstance().getRotatingMapType() == MapRotationType.COMPASS) {
                float degrees = OrientationMonitor.getInstance().getAzimuth();
//				view.setImageRotation(-degrees);
                //			PointF c = mapToView(mCenter);
                //			mImageMatrix.postTranslate(getWidth() / 2 - c.x, getHeight() / 2
                //					- c.y);
                //			setImageMatrix(mImageMatrix);
                //perspectiveFake3D(degrees);
            }
            Log.getInstance().debug(TAG, "Exit, applyTransformation()");
        }


    }

    public class MoveMyLocationImageAnimation extends Animation {
        private final static String TAG = "com.com.mlins.views.MoveMyLocationImageAnimation";

        // public CenterImageAnimation(Context context, AttributeSet attrs) {
        // super(context, attrs);
        // }
        d2dTouchImageView view = null;
        private PointF toCenter;
        private float dX;
        private float dY;
        private PointF center = null;

        public MoveMyLocationImageAnimation(PointF toCenter, d2dTouchImageView tiv) {
            super();
            Log.getInstance().debug(TAG, "Enter, MoveMyLocationImageAnimation()");
            view = tiv;
            if (mCenter == null) {
                mCenter = new PointF(tiv.getWidth() / 2, tiv.getHeight() / 2);
            }
            this.toCenter = toCenter;

            dX = toCenter.x - mCenter.x;
            dY = toCenter.y - mCenter.y;
            Log.getInstance().debug(TAG, "Exit, MoveMyLocationImageAnimation()");
        }

        @Override
        protected void applyTransformation(float interpolatedTime,
                                           Transformation t) {
            Log.getInstance().debug(TAG, "Enter, applyTransformation()");
            float rit = 1.0f - interpolatedTime;
            center = new PointF(toCenter.x - rit * dX, toCenter.y - rit * dY);
            setMyLocation(center);
            //if (PropertyHolder.getInstance().isRotatingMap()) {
            if (PropertyHolder.getInstance().getRotatingMapType() == MapRotationType.COMPASS) {
                float degrees = OrientationMonitor.getInstance().getAzimuth();
//				view.setImageRotation(-degrees);
                //			PointF c = mapToView(mCenter);
                //			mImageMatrix.postTranslate(getWidth() / 2 - c.x, getHeight() / 2
                //					- c.y);
                //			setImageMatrix(mImageMatrix);
                //perspectiveFake3D(degrees);
            }
            Log.getInstance().debug(TAG, "Exit, applyTransformation()");
        }
    }
}
