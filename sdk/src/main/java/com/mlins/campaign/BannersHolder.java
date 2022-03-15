package com.mlins.campaign;

import com.mlins.utils.Cleanable;
import com.mlins.utils.Lookup;
import com.mlins.utils.PropertyHolder;
import com.mlins.utils.logging.Log;
import com.spreo.geofence.GeoFenceObject;
import com.spreo.geofence.GeoFenceRect;
import com.spreo.nav.interfaces.ILocation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BannersHolder implements Cleanable {

    private static final String TAG = BannersHolder.class.getName();

    private static final String imagesSuffixes[] = {".png", "jpg", ".gif", ".tiff", "jpeg"};
    private boolean isDeafult = false;
    private ArrayList<BannerObject> allBanners;
    private ArrayList<BannerObject> favoritesBanners = new ArrayList<BannerObject>();

    private BannersHolder() {}

    public static BannersHolder getInstance() {
        BannersHolder instance = null;
        try {
            Log.getInstance().debug(TAG,
                    "Enter, BannerConfObject getInstance()");

            instance = Lookup.getInstance().get(BannersHolder.class);

            Log.getInstance()
                    .debug(TAG, "Exit, BannerConfObject getInstance()");
        } catch (Throwable t) {
            Log.getInstance().error(TAG, t.getMessage(), t);
        }
        return instance;
    }

    public static void releaseInstance() {
        Lookup.getInstance().remove(BannersHolder.class);
    }

    public void clean() {}

    public void addFavorite(BannerObject fbanner) {
        if (!favoritesBanners.contains(fbanner)) {
            fbanner.setIsFavorite(true);
            favoritesBanners.add(fbanner);
            saveFavorites();
        }
    }

    public void removeFavorite(BannerObject fbanner) {
        if (favoritesBanners.contains(fbanner)) {
            fbanner.setIsFavorite(false);
            favoritesBanners.remove(fbanner);
            saveFavorites();
        }
    }

    private void deleteFavorites() {
        for (BannerObject o : favoritesBanners) {
            o.setIsFavorite(false);
        }
        favoritesBanners.clear();
    }

    public void saveFavorites() {
        File dir = PropertyHolder.getInstance().getFacilityDir();
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String filename = "banners" + File.separator + "favorite_banners.txt";
        File file = new File(dir, filename);

        if (file.exists()) {
            file.delete();
        }
        try {
            file.createNewFile();
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        BufferedWriter out = null;
        StringBuffer sb = new StringBuffer();
        for (BannerObject o : favoritesBanners) {
            sb.append(o.getFileName() + "\n");
        }
        try {
            out = new BufferedWriter(new FileWriter(file, true));
            out.write(sb.toString());
            out.flush();
        } catch (IOException e) {
            e.toString();
        } finally {
            if (out != null)
                try {
                    out.close();
                } catch (Exception e2) {
                    Log.getInstance().error("", e2.getMessage());
                    e2.printStackTrace();
                }
        }
    }

    public void loadFavorites() {
        File dir = PropertyHolder.getInstance().getFacilityDir();
        String filename = "banners" + File.separator + "favorite_banners.txt";
        File file = new File(dir, filename);

        if (!file.exists()) {
            return;
        } else {
            deleteFavorites();
            BufferedReader in = null;
            try {
                in = new BufferedReader(new FileReader(file));
                String line = null;
                while ((line = in.readLine()) != null) {
                    for (BannerObject o : allBanners) {
                        if (o.getFileName().equals(line)) {
                            addFavorite(o);
                            break;
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (in != null)
                    try {
                        in.close();
                    } catch (Exception e2) {
                        Log.getInstance().error("", e2.getMessage());
                        e2.printStackTrace();
                    }
            }
        }
    }

    public void LoadAllBannersConf() {
        setAllBanners(new ArrayList<BannerObject>());
        File dir = PropertyHolder.getInstance().getFacilityDir();
        String filename = "banners" + File.separator + "bannersconf.txt";
        File file = new File(dir, filename);

        if (!file.exists()) {
            // file.mkdirs();
            loadAllDefaultBanners();
        } else {
            BufferedReader in = null;
            try {
                in = new BufferedReader(new FileReader(file));
                String line = null;
                while ((line = in.readLine()) != null) {
                    BannerObject conf = new BannerObject();
                    conf.Parse(line);
                    getAllBanners().add(conf);
                }
                isDeafult = false;
            } catch (IOException e) {
                e.printStackTrace();

            } finally {
                if (in != null)
                    try {
                        in.close();
                    } catch (Exception e2) {
                        Log.getInstance().error("", e2.getMessage());
                        e2.printStackTrace();
                    }
            }
            loadFavorites();
        }
    }

    private void loadAllDefaultBanners() {

        setAllBanners(new ArrayList<BannerObject>());
        File dir = PropertyHolder.getInstance().getFacilityDir();
        String filename = "banners";
        File file = new File(dir, filename);
        if (file.isDirectory()) {

            String imagesNameList[] = file.list(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String filename) {

                    if (filename != null) {
                        for (String suffix : imagesSuffixes) {
                            if (filename.toLowerCase().endsWith(suffix)) {
                                return true;
                            }
                        }
                    }
                    return false;
                }
            });

            for (String imgBannerName : imagesNameList) {
                getAllBanners().add(new BannerObject(imgBannerName));
            }

            isDeafult = true;
        }

    }

    public BannerObject getBannerByLocationAndDestination(ILocation loc,
                                                          ILocation destination) {
        // object to send
        BannerObject banner = null;

        // setting two lists of available banners
        List<BannerObject> availablesBanners = new ArrayList<BannerObject>();

        // addig banners to lists according to location and destination
        for (BannerObject evreryBanner : getAllBanners()) {

            if (isDeafult) {
                availablesBanners.add(evreryBanner);
            } else if (evreryBanner.getBannerLocations().size() == 0) {
                availablesBanners.add(evreryBanner);
            } else if (evreryBanner.isInside(loc)) {
                availablesBanners.add(evreryBanner);
            }
        }
        Random locationRnd = new Random();

        if (availablesBanners.size() > 0) {
            int amountOfdestinationsBanners = availablesBanners.size();
            // destinationsRnd.nextInt(amountOfdestinationsBanners);
            int index = locationRnd.nextInt(amountOfdestinationsBanners);
            banner = availablesBanners.get(index);
        }
        return banner;
    }


    public List<IBanner> getBannersInZone(GeoFenceObject zone) {

        List<IBanner> bannersList = new ArrayList<IBanner>();
        GeoFenceRect rectZone = (GeoFenceRect) zone;

        for (BannerObject evreryBanner : allBanners) {

            if (evreryBanner.isInside(rectZone)) {
                bannersList.add(evreryBanner);
            }
        }

        return bannersList;
    }

    public ArrayList<BannerObject> getAllBanners() {
        return allBanners;
    }

    public void setAllBanners(ArrayList<BannerObject> allBanners) {
        this.allBanners = allBanners;
    }

    public ArrayList<BannerObject> getFavoritesBanners() {
        return favoritesBanners;
    }

    public void setFavoritesBanners(ArrayList<BannerObject> favoritesBanners) {
        this.favoritesBanners = favoritesBanners;
    }

}
