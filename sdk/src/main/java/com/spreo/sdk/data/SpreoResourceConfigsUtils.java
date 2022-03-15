package com.spreo.sdk.data;

import android.content.Context;

import com.mlins.labels.LabelsContainer;
import com.mlins.project.ProjectConf;
import com.mlins.res.setup.ConfigsLoader;
import com.mlins.res.setup.ConfigsUpdater;
import com.mlins.utils.Lookup;
import com.mlins.utils.PropertyHolder;
import com.spreo.interfaces.ConfigsLoadListener;
import com.spreo.interfaces.ConfigsUpdaterListener;
import com.spreo.interfaces.LanguageLoadListener;
import com.spreo.sdk.setting.SettingsProvider;

public class SpreoResourceConfigsUtils {
    /**
     * SpreoResourceConfigsUtils.subscribeToResourceUpdateService listener is
     * used as an updating service to Subscribe to update events.. Once register
     * is subscribed you can get a notification when update ended with a result
     * either successfully or unsuccessfully
     *
     * @param configsUpdaterListener
     * @return true or false.
     * <p>
     * <p>
     * example in Main:
     * <p>
     * <pre>
     * <code>
     * 		public class MainActivity extends Activity implements ConfigsUpdaterListener, ConfigsLoadListener {
     *
     * 			SpreoResourceConfigsUtils.subscribeToResourceUpdateService(this);
     * 			SpreoResourceConfigsUtils.update(this);
     * 			//rest of your code here...
     * 		}
     * </code>
     * </pre>
     */
    public static boolean subscribeToResourceUpdateService(ConfigsUpdaterListener configsUpdaterListener) {

        return ConfigsUpdater.getInstance().registerListener(configsUpdaterListener);

    }

    /**
     * SpreoResourceConfigsUtils.update is used to update context to update
     * events and start the update.
     * *         example in Main:
     * <p>
     * <pre>
     * <code>
     * 		public class MainActivity extends Activity implements ConfigsUpdaterListener, ConfigsLoadListener {
     *
     * 			SpreoResourceConfigsUtils.subscribeToResourceUpdateService(this);
     * 			SpreoResourceConfigsUtils.update(this);
     * 			//rest of your code here...
     *        }
     * </code>
     * </pre>
     *
     * @param ctx - Context
     */
    public static void update(Context ctx) {
        PropertyHolder.getInstance().setMlinsContext(ctx);
        ConfigsUpdater.getInstance().download(ctx);
    }

    /**
     * SpreoResourceConfigsUtils.unSubscribeFromResourceUpdateService listener
     * is used to end an updating service or end Subscribe to update events..
     *
     * @param configsUpdaterListener
     * @return true or false.
     * <p>
     * example:
     * <p>
     * <pre>
     * <code>
     * public void onPostConfigsDownload(ResUpdateStatus status) {
     * 		if(status.equals(ResUpdateStatus.OK)){
     * 				SpreoResourceConfigsUtils.unSubscribeFromResourceUpdateService(this);
     * 				//rest of your code here...
     * 		}
     * }
     * </code>
     * </pre>
     */
    public static boolean unSubscribeFromResourceUpdateService(ConfigsUpdaterListener configsUpdaterListener) {
        return ConfigsUpdater.getInstance().unregisterListener(configsUpdaterListener);
    }

    /**
     * subscribe to resourceLoadService in order to get notifications for loading status
     * <p>
     * <pre>
     *  example:
     * <code>
     * public void onPostConfigsDownload(ResUpdateStatus status) {
     *
     * SpreoResourceConfigsUtils.unSubscribeFromResourceUpdateService(this);
     * SpreoResourceConfigsUtils.subscribeToResourceLoadService(this);
     * String campusid = SpreoDataProvider.getCampusId();
     * String facilityid = SpreoDataProvider.getFacilityId();
     * SpreoResourceConfigsUtils.loadFacility(campusid, facilityid);
     * //rest of your code here...
     * </code>
     * </pre>
     *
     * @param configsLoadListener
     * @return true if succeed to subscribe. otherwise, returns false.
     */
    public static boolean subscribeToResourceLoadService(ConfigsLoadListener configsLoadListener) {
        return ConfigsLoader.getInstance().registerListener(configsLoadListener);
    }

    /**
     * unSubscribe from resourceLoadService
     * <pre>
     *  example:
     * <code>
     * public void onPostConfigsLoad(LoadStatus status) {
     * 			SpreoResourceConfigsUtils.unSubscribeFromResourceLoadService(this);
     * 			SpreoLocationProvider.getInstance().startLocationService(ctx, ScanMode.BLE);
     * }
     * </code>
     * </pre>
     *
     * @param configsLoadListener
     * @return true if succeed to subscribe. otherwise, returns false.
     */
    public static boolean unSubscribeFromResourceLoadService(ConfigsLoadListener configsLoadListener) {
        return ConfigsLoader.getInstance().unregisterListener(configsLoadListener);
    }

    /**
     * loads a specific campus data by providing campusId
     * <pre>
     *  example:
     * <code>
     * public void onPostConfigsDownload(ResUpdateStatus status) {
     *
     * SpreoResourceConfigsUtils.unSubscribeFromResourceUpdateService(this);
     * SpreoResourceConfigsUtils.subscribeToResourceLoadService(this);
     * String campusid = SpreoDataProvider.getCampusId();
     * SpreoResourceConfigsUtils.loadCampus(campusid);
     * //rest of your code here...
     * </code>
     * </pre>
     *
     * @param campusId
     * @return true if succeed to load campus. otherwise, returns false.
     */
    public static boolean loadCampus(String campusId) {
        return ConfigsLoader.getInstance().loadCampus(campusId);
    }

    /**
     * loads a specific facility data by providing campusId and facilityId
     * <pre>
     *  example:
     * <code>
     * public void onPostConfigsDownload(ResUpdateStatus status) {
     *
     * SpreoResourceConfigsUtils.unSubscribeFromResourceUpdateService(this);
     * SpreoResourceConfigsUtils.subscribeToResourceLoadService(this);
     * String campusid = SpreoDataProvider.getCampusId();
     * String facilityid = SpreoDataProvider.getFacilityId();
     * SpreoResourceConfigsUtils.loadFacility(campusid, facilityid);
     * //rest of your code here...
     * </code>
     * </pre>
     *
     * @param campusId
     * @param facilityId
     * @return true if succeed to load facility. otherwise, returns false.
     */
    public static boolean loadFacility(String campusId, String facilityId) {
        return ConfigsLoader.getInstance().loadFacility(campusId, facilityId);
    }


    /**
     * subscribe to Load Language in order to get notifications for loading language status
     *
     * @param languageLoadListener
     * @return true if succeed to subscribe. otherwise, returns false.
     */
    public static boolean subscribeToLanguageLoadService(LanguageLoadListener languageLoadListener) {
        return ConfigsLoader.getInstance().registerLangLoadListener(languageLoadListener);
    }

    /**
     * unSubscribe from load language notifications
     *
     * @param languageLoadListener
     * @return true if succeed to subscribe. otherwise, returns false.
     */
    public static boolean unSubscribeFromLanguageLoadService(LanguageLoadListener languageLoadListener) {
        return ConfigsLoader.getInstance().unregisterLangLoadListener(languageLoadListener);
    }


    /**
     * load application with a specific language
     * If the language is not supported then the default language will be loaded
     *
     * @param language
     */
    public static void loadApplicationLanguage(String language) {
        if (language != null && !language.equals("")) {
            SettingsProvider.getInstance().setAppLanguage(language);
            ConfigsLoader.getInstance().registerLangLoadListener(new LanguageLoadListener() {
                @Override
                public void onPreLoadLanguage() {
                }

                @Override
                public void onPostLoadLanguage() {
                    ConfigsLoader.getInstance().unregisterLangLoadListener(this);
                    String campusid = PropertyHolder.getInstance().getCampusId();
                    if (campusid != null) {
                        LabelsContainer labelsContainer = ProjectConf.getInstance().getLabelsContainer();
                        if (labelsContainer != null) {
                            labelsContainer.translateLabels();
                        }
                    }
                }
            });
            ConfigsLoader.getInstance().reloadLangaugeData();
        }
    }


    /**
     * Initialize data without using network connection
     *
     * @param ctx
     * @return true if succeeded otherwise return false
     */
    public static boolean initializeData(Context ctx) {
        PropertyHolder.getInstance().setMlinsContext(ctx);
        return ConfigsUpdater.getInstance().intializeData(ctx);

    }

    /**
     * Sets your Spreo api key
     *
     * @param apikey
     */
    public static void setSpreoApiKey(String apikey) {
        Lookup.getInstance().clean();
        ConfigsUpdater.getInstance().setReqApikey(apikey);
    }


}
