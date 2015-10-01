package dltone.com.mealhero;

/**
 * w-garcia 9/30/2015.
 */

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;

import com.ibm.mobile.services.core.IBMBluemix;
import com.ibm.mobile.services.core.internal.IBMBluemixException;
import com.ibm.mobile.services.data.IBMData;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public final class MealHeroApplication extends Application
{
    private static final String APP_ID = "applicationID";
    private static final String APP_SECRET = "applicationSecret";
    private static final String APP_ROUTE = "applicationRoute";
    private static final String PROPS_FILE = "mealhero.properties";

    public static final int EDIT_ACTIVITY_RC = 1;
    private static final String CLASS_NAME = MealHeroApplication.class.getSimpleName();

    public MealHeroApplication() {
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity,Bundle savedInstanceState) {
                Log.d(CLASS_NAME, "Activity created: " + activity.getLocalClassName());
            }
            @Override
            public void onActivityStarted(Activity activity) {
                Log.d(CLASS_NAME, "Activity started: " + activity.getLocalClassName());
            }
            @Override
            public void onActivityResumed(Activity activity) {
                Log.d(CLASS_NAME, "Activity resumed: " + activity.getLocalClassName());
            }
            @Override
            public void onActivitySaveInstanceState(Activity activity,Bundle outState) {
                Log.d(CLASS_NAME, "Activity saved instance state: " + activity.getLocalClassName());
            }
            @Override
            public void onActivityPaused(Activity activity) {
                Log.d(CLASS_NAME, "Activity paused: " + activity.getLocalClassName());
            }
            @Override
            public void onActivityStopped(Activity activity) {
                Log.d(CLASS_NAME, "Activity stopped: " + activity.getLocalClassName());
            }
            @Override
            public void onActivityDestroyed(Activity activity) {
                Log.d(CLASS_NAME, "Activity destroyed: " + activity.getLocalClassName());
            }
        });
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        // Read from properties file
        Properties props = new Properties();
        Context context = getApplicationContext();
        try
        {
            AssetManager aM = context.getAssets();
            props.load(aM.open(PROPS_FILE));
        }
        catch (FileNotFoundException e)
        {
            Log.e(CLASS_NAME, "Properties file not found.", e);
            return;
        }
        catch (IOException e)
        {
            Log.e(CLASS_NAME, "Properties file could not be read readily.", e);
            return;
        }

        // initialize IBM core backend-as-a-service
        try
        {
            IBMBluemix.initialize(this,
                    props.getProperty(APP_ID),
                    props.getProperty(APP_SECRET),
                    props.getProperty(APP_ROUTE));
        }
        catch (IBMBluemixException e)
        {
            Log.e(CLASS_NAME, "Bluemix failed to authenticate. Check properties file.", e);
            return;
        }

        Log.e(CLASS_NAME, "We did it...");
        // initialize IBM Data Service
        IBMData.initializeService();
        // register the Item Specialization


    }

}
