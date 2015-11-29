package dltone.com.mealhero;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.skobbler.ngx.SKCoordinate;
import com.skobbler.ngx.SKDeveloperKeyException;
import com.skobbler.ngx.SKMaps;
import com.skobbler.ngx.SKMapsInitSettings;
import com.skobbler.ngx.SKPrepareMapTextureListener;
import com.skobbler.ngx.SKPrepareMapTextureThread;
import com.skobbler.ngx.map.SKMapViewStyle;
import com.skobbler.ngx.navigation.SKAdvisorSettings;
import com.skobbler.ngx.routing.SKRouteManager;
import com.skobbler.ngx.util.SKLogging;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class MapPreviewActivity extends AppCompatActivity implements SKPrepareMapTextureListener
{
    ArrayAdapter<Client> lvArrayAdapter;
    ArrayList<Client> mClientsToDisplay = new ArrayList<>();
    MealHeroApplication MHApp;

    private static final String VOLUNTEER = "dltone.com.mealhero.VOLUNTEER";
    private static final String TAG = "MapPreviewActivity";
    private static final int MENU_ADMIN = Menu.FIRST;
    private static final int MENU_SETTINGS = Menu.FIRST + 1;
    private static final int MENU_LOGOUT = Menu.FIRST + 2;

    private String mapResourceDirPath;

    private Button _btnNavigate;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_preview);

        /* Use application class to maintain global state. */
        MHApp = (MealHeroApplication) getApplication();
        
        mClientsToDisplay = ClientProvider.GetAssignedClients(MHApp.getLoggedInVolunteer(), MHApp);

        /* Set up the array adapter for items list view. */
        ListView volunteerListView = (ListView) findViewById(R.id._lvwClients);
        lvArrayAdapter = new ArrayAdapter<>(this, R.layout.simple_list_item, mClientsToDisplay);
        volunteerListView.setAdapter(lvArrayAdapter);

        lvArrayAdapter.notifyDataSetChanged();

        _btnNavigate = (Button) findViewById(R.id._btnNavigate);
        _btnNavigate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if (mClientsToDisplay.isEmpty())
                {
                    Toast.makeText(getApplicationContext(), "No clients assigned to you. Please contact an administrator.", Toast.LENGTH_SHORT).show();
                    return;
                }
                else
                {
                    openNavigation();
                }
            }
        });

        _btnNavigate.setEnabled(false);

        /* Initialize SKMaps */
        String applicationPath = chooseStoragePath(this);

        if (applicationPath != null)
        {
            mapResourceDirPath = applicationPath + "/" + "SKMaps/";
            MHApp.setMapResourcesDirPath(mapResourceDirPath);
            final SKPrepareMapTextureThread prepThread = new SKPrepareMapTextureThread(this, mapResourceDirPath, "SKMaps.zip", this);
            prepThread.start();
        }
        else
        {
            // show a dialog and finish
        }


    }

    @Override
    public void onMapTexturesPrepared(final boolean prepared)
    {
        // get object holding map initialization settings
        SKMapsInitSettings initMapSettings = new SKMapsInitSettings();

        final String  mapResourcesPath = mapResourceDirPath;
        // set path to map resources and initial map style
        initMapSettings.setMapResourcesPaths(mapResourcesPath,
                new SKMapViewStyle(mapResourcesPath + "daystyle/", "daystyle.json"));

        final SKAdvisorSettings advisorSettings = new SKAdvisorSettings();
        advisorSettings.setLanguage(SKAdvisorSettings.SKAdvisorLanguage.LANGUAGE_EN);
        advisorSettings.setAdvisorConfigPath(MHApp.getMapResourcesDirPath() +"/Advisor");
        advisorSettings.setResourcePath(MHApp.getMapResourcesDirPath()+"/Advisor/Languages");
        advisorSettings.setAdvisorVoice("en");
        advisorSettings.setAdvisorType(SKAdvisorSettings.SKAdvisorType.AUDIO_FILES);
        SKRouteManager.getInstance().setAudioAdvisorSettings(advisorSettings);

        try
        {
            SKMaps.getInstance().initializeSKMaps(this, initMapSettings);
            _btnNavigate.setEnabled(true);
        }
        catch (SKDeveloperKeyException exception)
        {
            exception.printStackTrace();
            //showApiKeyErrorDialog(context);
        }

        // if prepared is true means that the resources are in place and
        //the library can be initialized
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_map_preview, menu);

        return true;
    }


    /**
     * Gets called every time the user presses the menu button.
     * Use if your menu is dynamic.
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        menu.clear();
        //if (mVolunteerToDisplay.getPermission().equalsIgnoreCase("Admin"))
        menu.add(0, MENU_ADMIN, Menu.NONE, R.string.action_admin);

        menu.add(0, MENU_SETTINGS, Menu.NONE, R.string.action_settings);
        menu.add(0, MENU_LOGOUT, Menu.NONE, R.string.action_logout);

        getMenuInflater().inflate(R.menu.menu_map_preview, menu);

        return super.onPrepareOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch(id)
        {
            case MENU_ADMIN:
                openAdministration();
                break;
            case MENU_SETTINGS:
                break;
            case MENU_LOGOUT:
                logout();
                break;
            case R.id.action_refresh:
                refresh();
                break;
        }
        //noinspection SimplifiableIfStatement
        //if (id == R.id.action_settings)
        //{
        //    return true;
        //}

        return super.onOptionsItemSelected(item);
    }

    public static final long KILO = 1024;

    public static final long MEGA = KILO * KILO;

    /**
     * Find the best storage path based on device properties
     * @param context
     * @return
     */
    public static String chooseStoragePath(Context context) {
        if (getAvailableMemorySize(Environment.getDataDirectory().getPath()) >= 50 * MEGA)
        {
            if (context != null && context.getFilesDir() != null)
            {
                return context.getFilesDir().getPath();
            }
        }
        else
        {
            if ((context != null) && (context.getExternalFilesDir(null) != null))
            {
                if (getAvailableMemorySize(context.getExternalFilesDir(null).toString()) >= 50 * MEGA)
                {
                    return context.getExternalFilesDir(null).toString();
                }
            }
        }

        SKLogging.writeLog(TAG, "There is not enough memory on any storage, but return internal memory",
                SKLogging.LOG_DEBUG);

        if (context != null && context.getFilesDir() != null) {
            return context.getFilesDir().getPath();
        }
        else
        {
            if ((context != null) && (context.getExternalFilesDir(null) != null))
            {
                return context.getExternalFilesDir(null).toString();
            }
            else
            {
                return null;
            }
        }
    }

    /**
     * get the available internal memory size
     *
     * @return available memory size in bytes
     */
    public static long getAvailableMemorySize(String path) {
        StatFs statFs = null;
        try {
            statFs = new StatFs(path);
        } catch (IllegalArgumentException ex) {
            SKLogging.writeLog("SplashActivity", "Exception when creating StatF ; message = " + ex,
                    SKLogging.LOG_DEBUG);
        }
        if (statFs != null) {
            Method getAvailableBytesMethod = null;
            try {
                getAvailableBytesMethod = statFs.getClass().getMethod("getAvailableBytes");
            } catch (NoSuchMethodException e) {
                SKLogging.writeLog(TAG, "Exception at getAvailableMemorySize method = " + e.getMessage(),
                        SKLogging.LOG_DEBUG);
            }

            if (getAvailableBytesMethod != null) {
                try {
                    SKLogging.writeLog(TAG, "Using new API for getAvailableMemorySize method !!!", SKLogging.LOG_DEBUG);
                    return (Long) getAvailableBytesMethod.invoke(statFs);
                } catch (IllegalAccessException e) {
                    return (long) statFs.getAvailableBlocks() * (long) statFs.getBlockSize();
                } catch (InvocationTargetException e) {
                    return (long) statFs.getAvailableBlocks() * (long) statFs.getBlockSize();
                }
            } else {
                return (long) statFs.getAvailableBlocks() * (long) statFs.getBlockSize();
            }
        } else {
            return 0;
        }
    }

    private void openNavigation()
    {
        Intent intent = new Intent(MapPreviewActivity.this, NavigationActivity.class);
        startActivity(intent);
    }

    private void openAdministration()
    {
        Intent intent = new Intent(MapPreviewActivity.this, AdministrationActivity.class);
        startActivity(intent);
    }

    private void logout()
    {
        Intent intent = new Intent(MapPreviewActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void refresh()
    {
        finish();
        Intent intent = new Intent(MapPreviewActivity.this, MapPreviewActivity.class);
        startActivity(intent);
    }

}
