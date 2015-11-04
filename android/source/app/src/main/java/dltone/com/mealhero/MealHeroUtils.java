package dltone.com.mealhero;

import android.app.Activity;
import android.content.Context;
import android.location.LocationManager;

import com.skobbler.ngx.SKDeveloperKeyException;
import com.skobbler.ngx.SKMaps;
import com.skobbler.ngx.SKMapsInitSettings;
import com.skobbler.ngx.map.SKMapViewStyle;
import com.skobbler.ngx.navigation.SKAdvisorSettings;
import com.skobbler.ngx.util.SKLogging;

/**
 * Created by Owner on 11/3/2015.
 */
public class MealHeroUtils
{
    /**
     * Initializes the SKMaps framework
     */
    public static boolean initializeLibrary(final Activity context) {
        SKLogging.enableLogs(true);

        // get object holding map initialization settings
        SKMapsInitSettings initMapSettings = new SKMapsInitSettings();

        final String  mapResourcesPath = ((MealHeroApplication)context.getApplicationContext()).getAppPrefs().getStringPreference("mapResourcesPath");
        // set path to map resources and initial map style
        initMapSettings.setMapResourcesPaths(mapResourcesPath,
                new SKMapViewStyle(mapResourcesPath + "daystyle/", "daystyle.json"));

        final SKAdvisorSettings advisorSettings = initMapSettings.getAdvisorSettings();
        advisorSettings.setAdvisorConfigPath(mapResourcesPath +"/Advisor");
        advisorSettings.setResourcePath(mapResourcesPath +"/Advisor/Languages");
        advisorSettings.setLanguage(SKAdvisorSettings.SKAdvisorLanguage.LANGUAGE_EN);
        advisorSettings.setAdvisorVoice("en");
        initMapSettings.setAdvisorSettings(advisorSettings);

        // EXAMPLE OF ADDING PREINSTALLED MAPS
//         initMapSettings.setPreinstalledMapsPath(((DemoApplication)context.getApplicationContext()).getMapResourcesDirPath()
//         + "/PreinstalledMaps");
        // initMapSettings.setConnectivityMode(SKMaps.CONNECTIVITY_MODE_OFFLINE);

        // Example of setting light maps
        // initMapSettings.setMapDetailLevel(SKMapsInitSettings.SK_MAP_DETAIL_LIGHT);
        // initialize map using the settings object

        try {
            SKMaps.getInstance().initializeSKMaps(context, initMapSettings);
            return true;
        }catch (SKDeveloperKeyException exception){
            exception.printStackTrace();
            //showApiKeyErrorDialog(context);
            return false;
        }
    }

    /**
     * Checks if the current device has a GPS module (hardware)
     * @return true if the current device has GPS
     */
    public static boolean hasGpsModule(final Context context) {
        final LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        for (final String provider : locationManager.getAllProviders()) {
            if (provider.equals(LocationManager.GPS_PROVIDER)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the current device has a  NETWORK module (hardware)
     * @return true if the current device has NETWORK
     */
    public static boolean hasNetworkModule(final Context context) {
        final LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        for (final String provider : locationManager.getAllProviders()) {
            if (provider.equals(LocationManager.NETWORK_PROVIDER)) {
                return true;
            }
        }
        return false;
    }
}
