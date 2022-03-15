package com.mlins.ibeacon;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.mlins.utils.Cleanable;
import com.mlins.utils.Lookup;
import com.mlins.utils.PropertyHolder;
import com.mlins.wireless.IResultReceiver;
import com.mlins.wireless.Scannable;
import com.mlins.wireless.WlBlip;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class IBeaconScanner implements /** IBeaconConsumer,*/Scannable, Cleanable {

    private static final int REQUEST_ENABLE_BT = 1200;

    HashMap<String, WlBlip> blipsMap = new HashMap<String, WlBlip>();
    private Context ctx = null;
    private List<IResultReceiver> listeneres = new ArrayList<IResultReceiver>();
    private List<WlBlip> blips = new ArrayList<WlBlip>();
    private HashMap<String, IBeaconFilterData> filterBlipsMap = new HashMap<String, IBeaconFilterData>();
    /**
     * private IBeaconManager iBeaconManager = null;
     */
    private InitializerThread initializerThread = null;
    private boolean initialized = false;


    public static IBeaconScanner getInstance() {
        return Lookup.getInstance().get(IBeaconScanner.class);
    }

    public void clean(){
        stopScanning();
    }

    @SuppressLint("NewApi")
    public static void initBluetoothAdapter(Context ctx) {
        if (android.os.Build.VERSION.SDK_INT < 18) {
            Toast.makeText(ctx, "Sorry: IBeacon is not supported by your device", Toast.LENGTH_LONG).show();
            return;
        }
        BluetoothAdapter bluetoothAdapter = null;
        // Initializes Bluetooth adapter.
        final BluetoothManager bluetoothManager =
                (BluetoothManager) ctx.getApplicationContext().getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();

        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            //activity.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            enableBtIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ctx.startActivity(enableBtIntent);
        }


    }

    @Override
    public void startScanning() {
        initScanning();
    }

    private void initScanning() {
        /**
         ctx =PropertyHolder.getInstance().getMlinsContext();
         iBeaconManager = IBeaconManager.getInstanceForApplication(ctx);

         if(iBeaconManager.checkAvailability()){
         //iBeaconManager.bind(this);
         bind();
         initialized=true;
         if(initializerThread!=null){
         initializerThread.stopThread();
         initializerThread=null;
         }
         Toast.makeText(ctx, "IBeacon scanner started", Toast.LENGTH_LONG).show();
         }
         else{
         //Toast.makeText(ctx, "Sorry: IBeacon is not supported by your device", Toast.LENGTH_LONG).show();
         if(initializerThread==null){
         initializerThread = new InitializerThread();
         initializerThread.start();
         }
         }
         */

    }

    /**
     @Override public Context getApplicationContext() {
     return ctx;
     }

     @Override public void onIBeaconServiceConnect() {

     iBeaconManager.setRangeNotifier(new RangeNotifier() {

     @Override public void didRangeBeaconsInRegion(Collection<IBeacon> beacons,
     Region arg1) {

     //List <WlBlip> detects=new ArrayList<WlBlip>();

     for (Iterator<IBeacon> iterator = beacons.iterator(); iterator.hasNext();) {
     IBeacon iBeacon = (IBeacon) iterator.next();

     String res = "major: " + iBeacon.getMajor() + " minor: "
     + iBeacon.getMinor() + " rssi : "
     + iBeacon.getRssi() + " pattery: "
     + iBeacon.getTxPower() + "\n";

     //Log.i("IBeaconScanner",res);

     String uuid=iBeacon.getMajor()+"m"+iBeacon.getMinor();

     WlBlip blip=new WlBlip(uuid, uuid, iBeacon.getRssi(), 0, System.currentTimeMillis());

     //detects.add(blip);
     blipsMap.put(uuid, blip);

     //System.out.println(res);

     }


     //deliverResults(detects);

     deliverResults();

     }



     });

     try {
     iBeaconManager.startRangingBeaconsInRegion(new Region("MLINS","2F234454-CF6D-4A0F-ADF2-F4911BA9FFA6", null, null));

     } catch (RemoteException e) {
     }

     }
     */


//	private void deliverResults(List<WlBlip> detects) {
//		//List<WlBlip> filteredDetects = makeFilter(detects);
//		//this.blips=filteredDetects;
//		for (IResultReceiver o : listeneres) {
//			o.onRecieve(detects);
//		}
//		
//	}	

//	private List<WlBlip> makeFilter(List<WlBlip> detects) {
//		List<WlBlip> filteredDetects=new ArrayList<WlBlip>();
//		for(WlBlip blip:detects){
//			try
//			{
//				//String  filter[]=blip.BSSID.split("m");
//				//Integer.valueOf(filter[0]);
//				//Integer.valueOf(filter[1]);
//				WlBlip filteredBlip=new WlBlip(blip.SSID,  blip.BSSID, blip. level, blip.frequency, blip.timestamp);
//				filteredDetects.add(filteredBlip);
//				
//				
//			}
//			catch(Exception e){
//				// not our Radious ibeacon!
//			}
//			
//		}
//		return filteredDetects;
//	}

    /**
     * private void unBind() {
     * iBeaconManager.unBind(this);
     * }
     * <p>
     * private void bind() {
     * iBeaconManager.bind(this);
     * }
     *
     * @Override public void unbindService(ServiceConnection serviceConnection) {
     * unBind();
     * ctx.unbindService(serviceConnection);
     * }
     * @Override public boolean bindService(Intent intent,ServiceConnection serviceConnection, int i) {
     * bind();
     * return ctx.bindService(intent, serviceConnection, i);
     * }
     */
    public void subscribeForResults(IResultReceiver receiver) {
        if (!listeneres.contains(receiver)) {
            listeneres.add(receiver);
        }
    }

    public boolean unsubscibeForResults(IResultReceiver receiver) {
        if (listeneres.contains(receiver)) {
            return listeneres.remove(receiver);
        }
        return false;
    }

    public void stopScanning() {
        /**    unBind(); */
        listeneres.clear();
        blipsMap.clear();
        Toast.makeText(ctx, "IBeacon scanner stopped", Toast.LENGTH_LONG).show();
    }

    public void deliverResults() {
        boolean filterEnabled = PropertyHolder.getInstance().isBleLevelfilter();
        List<WlBlip> result = new ArrayList<WlBlip>();
        List<WlBlip> temp = new ArrayList<WlBlip>();
        temp.addAll(blipsMap.values());
        long time = System.currentTimeMillis();
        for (WlBlip o : temp) {
            if (time - o.timestamp < 3000) {
                if (filterEnabled) {
                    IBeaconFilterData filterdata = null;
                    if (filterBlipsMap.containsKey(o.BSSID)) {
                        filterdata = filterBlipsMap.get(o.BSSID);
                    } else {
                        filterdata = new IBeaconFilterData(o.BSSID);
                        filterBlipsMap.put(o.BSSID, filterdata);
                    }
                    if (filterdata != null) {
                        o.level = filterdata.getFilteredLevel(o.level);
                        result.add(o);
                    }
                } else {
                    result.add(o);
                }
            } else {
                if (filterEnabled) {
                    if (filterBlipsMap.containsKey(o.BSSID)) {
                        filterBlipsMap.remove(o.BSSID);
                    }
                }
                //System.out.println(o.BSSID + " too old ");
            }
        }
        setIBeaconBlips(result);
        for (IResultReceiver o : listeneres) {
            o.onRecieve(result);
        }
    }

    public void resumeScanning() {
        /** bind(); */

    }

    public List<WlBlip> getIBeaconBlips() {
        return blips;
    }

    public void setIBeaconBlips(List<WlBlip> blips) {
        this.blips = blips;
    }

    @Override
    public List<WlBlip> getBlipsList() {
        return blips;
    }


    private class InitializerThread extends Thread {

        public static final int Stop = 0;
        public static final int Play = 1;
        public static final int Pause = 2;
        public boolean mRunning = true;
        int state;
        private boolean mStarted;

        public InitializerThread() {
            super("InitializerThread");
        }

        @Override
        public void run() {

            while (mRunning && !initialized) {
                long delay = 2000;

                try {
                    Thread.sleep(delay);
                    initScanning();

                } catch (Throwable e) {
                    e.printStackTrace();
                }


            }
            mStarted = false;
        }

        public boolean isRunning() {
            return mRunning;
        }

        public void setRunning(boolean mRunning) {
            this.mRunning = mRunning;
        }

        @Override
        public synchronized void start() {
            mStarted = true;
            mRunning = true;
            super.start();
        }

        public boolean getIsStarted() {
            return mStarted;
        }

        public void stopThread() {
            state = Stop;
            mRunning = false;
        }

    }


}