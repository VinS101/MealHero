package dltone.com.mealhero;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.speech.tts.Voice;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.widget.Toast;

import com.skobbler.ngx.SKCoordinate;
import com.skobbler.ngx.SKMaps;
import com.skobbler.ngx.map.SKAnimationSettings;
import com.skobbler.ngx.map.SKAnnotation;
import com.skobbler.ngx.map.SKCoordinateRegion;
import com.skobbler.ngx.map.SKMapCustomPOI;
import com.skobbler.ngx.map.SKMapPOI;
import com.skobbler.ngx.map.SKMapSurfaceListener;
import com.skobbler.ngx.map.SKMapSurfaceView;
import com.skobbler.ngx.map.SKMapViewHolder;
import com.skobbler.ngx.map.SKPOICluster;
import com.skobbler.ngx.map.SKScreenPoint;
import com.skobbler.ngx.navigation.SKAdvisorSettings;
import com.skobbler.ngx.navigation.SKNavigationListener;
import com.skobbler.ngx.navigation.SKNavigationManager;
import com.skobbler.ngx.navigation.SKNavigationSettings;
import com.skobbler.ngx.navigation.SKNavigationState;
import com.skobbler.ngx.poitracker.SKDetectedPOI;
import com.skobbler.ngx.poitracker.SKPOITrackerListener;
import com.skobbler.ngx.poitracker.SKPOITrackerManager;
import com.skobbler.ngx.poitracker.SKTrackablePOI;
import com.skobbler.ngx.poitracker.SKTrackablePOIType;
import com.skobbler.ngx.positioner.SKCurrentPositionListener;
import com.skobbler.ngx.positioner.SKCurrentPositionProvider;
import com.skobbler.ngx.positioner.SKPosition;
import com.skobbler.ngx.positioner.SKPositionerManager;
import com.skobbler.ngx.routing.SKRouteAdvice;
import com.skobbler.ngx.routing.SKRouteInfo;
import com.skobbler.ngx.routing.SKRouteJsonAnswer;
import com.skobbler.ngx.routing.SKRouteListener;
import com.skobbler.ngx.routing.SKRouteManager;
import com.skobbler.ngx.routing.SKRouteSettings;
import com.skobbler.ngx.routing.SKViaPoint;
import com.skobbler.ngx.util.SKLogging;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class NavigationActivity extends FragmentActivity implements SKCurrentPositionListener, SKMapSurfaceListener, SKRouteListener, SKNavigationListener, SKPOITrackerListener
{
    /**
     * Application context object
     */
    private MealHeroApplication MHApp;

    /**
     *  ID Tag
     */
    private static final String TAG = "NavigationActivity";

    /**
     * Current position provider
     */
    private SKCurrentPositionProvider currentPositionProvider;

    /**
     * Current position
     */
    private SKPosition currentPosition;

    /**
     * timestamp for the last currentPosition
     */
    private long currentPositionTime;

    /**
     *  flag for initializing navigation after location get
     */
    private Boolean _locationInitialized = false;

    /**
     * Tells if a navigation is ongoing
     */
    private boolean skToolsNavigationInProgress;

    /**
     * counts the consecutive received positions with an accuracy greater than 150
     */
    private byte numberOfConsecutiveBadPositionReceivedDuringNavi;

    /**
     * Surface view for displaying the map
     */
    private SKMapSurfaceView mapView;

    /**
     * the view that holds the map view
     */
    private SKMapViewHolder mapHolder;

    /**
     * POIs to be detected on route
     */
    private Map<Integer, SKTrackablePOI> trackablePOIs;

    /**
     * Trackable POIs that are currently rendered on the map
     */
    private Map<Integer, SKTrackablePOI> drawnTrackablePOIs;

    private SKPOITrackerManager poiTrackingManager;

    private ArrayList<SKViaPoint> _pointsList = new ArrayList<>();

    private List<SKCoordinate> _coordinatesList = new ArrayList<>();

    private List<Client> _clientsToDisplay = new ArrayList<>();

    // Audio advisor
    private TextToSpeech textToSpeechEngine;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        //MealHeroUtils.initializeLibrary(this);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_navigation);
        MHApp = (MealHeroApplication) getApplication();

        _clientsToDisplay = ClientProvider.GetAssignedClients(MHApp.getLoggedInVolunteer(), MHApp);

        if (_clientsToDisplay == null)
        {
            Toast.makeText(getApplicationContext(), "Unable to retrieve user data. Log out and try again.", Toast.LENGTH_LONG).show();
            return;
        }

        if ( !ValidateAddresses() )
        {
            Toast.makeText(getApplicationContext(), "Unable to Validate Addresses. Check client addresses and try again.", Toast.LENGTH_LONG).show();
            return;
        }

        setupAudioAdvisor();

        currentPositionProvider = new SKCurrentPositionProvider(this);
        currentPositionProvider.setCurrentPositionListener(this);
        currentPositionProvider.requestLocationUpdates(MealHeroUtils.hasGpsModule(this), MealHeroUtils.hasNetworkModule(this), false);

        mapHolder = (SKMapViewHolder) findViewById(R.id.map_surface_holder);
        mapHolder.setMapSurfaceListener(this);
        mapView = mapHolder.getMapSurfaceView();

        currentPositionProvider.requestUpdateFromLastPosition();

    }

    private void setupAudioAdvisor()
    {
        if (textToSpeechEngine == null)
        {
            textToSpeechEngine = new TextToSpeech(NavigationActivity.this, new TextToSpeech.OnInitListener()
            {
                @Override
                public void onInit(int status)
                {
                    if (status == TextToSpeech.SUCCESS)
                    {
                        int result = textToSpeechEngine.setLanguage(Locale.ENGLISH);
                        if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED)
                        {
                            Toast.makeText(NavigationActivity.this, "Text-To-Speech: Missing data or language not supported. No speech advisor will be used.", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else
                    {
                        Toast.makeText(NavigationActivity.this, "Text-to-speech could not be initialized. No speech advisor will be used.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        final SKAdvisorSettings advisorSettings = new SKAdvisorSettings();
        advisorSettings.setLanguage(SKAdvisorSettings.SKAdvisorLanguage.LANGUAGE_EN);
        advisorSettings.setAdvisorConfigPath(MHApp.getMapResourcesDirPath() + "/Advisor");
        advisorSettings.setResourcePath(MHApp.getMapResourcesDirPath()+"/Advisor/Languages");
        advisorSettings.setAdvisorVoice("en");
        advisorSettings.setAdvisorType(SKAdvisorSettings.SKAdvisorType.TEXT_TO_SPEECH);
        SKRouteManager.getInstance().setAudioAdvisorSettings(advisorSettings);
    }

    private Boolean ValidateAddresses()
    {
        _coordinatesList.clear();

        for (Client client : _clientsToDisplay)
        {
            Double latitude = client.getLatitude();
            Double longitude = client.getLongitude();
            if (latitude == -1 || longitude == -1)
            {
                return false;
            }
            _coordinatesList.add(new SKCoordinate(longitude, latitude));
        }
        return true;
    }

    private void launchRouteCalculation()
    {
        // get a route object and populate it with the desired properties
        SKRouteSettings route = new SKRouteSettings();

        SKCoordinate currentPositionCoord = new SKCoordinate(currentPosition.getCoordinate().getLongitude(), currentPosition.getCoordinate().getLatitude());
        // set start point
        route.setStartCoordinate(currentPositionCoord);

        mapView.addAnnotation(createAnnotationFromCoordinate(UniqueID.getID(), currentPositionCoord, SKAnnotation.SK_ANNOTATION_TYPE_GREEN), SKAnimationSettings.ANIMATION_PIN_DROP);


        // set the number of routes to be calculated
        route.setNoOfRoutes(1);

        // set the route mode
        route.setRouteMode(SKRouteSettings.SKRouteMode.CAR_FASTEST);

        // set whether the route should be shown on the map after it's computed
        route.setRouteExposed(true);

        // set the route listener to be notified of route calculation events
        SKRouteManager.getInstance().setRouteListener(this);

        // using SKViaPoint to add waypoints
        _pointsList = new ArrayList<>();

        for (SKCoordinate coord : _coordinatesList)
        {
            if (coord == null)
            {
                Toast.makeText(this, "Error retrieving addresses. Verify client addresses or contact an administrator.", Toast.LENGTH_SHORT).show();
                abort(); //get out
            }

            SKViaPoint temp = new SKViaPoint(UniqueID.getID(), coord);
            _pointsList.add(temp);
        }

        SKCoordinate destCoord = MHApp.getCentralMOWHub();
        route.setDestinationCoordinate(destCoord);


        setupAnnotations(currentPositionCoord, _pointsList, destCoord);

        route.setViaPoints(_pointsList);
        route.setDestinationIsPoint(false);
        // pass the route to the calculation routine
        SKRouteManager.getInstance().calculateRoute(route);
    }

    private void setupAnnotations(SKCoordinate currentPositionCoord, ArrayList<SKViaPoint> pointsList, SKCoordinate destCoord)
    {
        mapView.addAnnotation(createAnnotationFromCoordinate(UniqueID.getID(), currentPositionCoord, SKAnnotation.SK_ANNOTATION_TYPE_GREEN), SKAnimationSettings.ANIMATION_PIN_DROP);

        for (SKViaPoint point : pointsList)
        {
            mapView.addAnnotation(createAnnotationFromCoordinate(UniqueID.getID(), point.getPosition(), SKAnnotation.SK_ANNOTATION_TYPE_RED), SKAnimationSettings.ANIMATION_PIN_DROP);
        }

        mapView.addAnnotation(createAnnotationFromCoordinate(UniqueID.getID(), destCoord, SKAnnotation.SK_ANNOTATION_TYPE_DESTINATION_FLAG), SKAnimationSettings.ANIMATION_PIN_DROP);
    }

    private SKAnnotation createAnnotationFromCoordinate(int id, SKCoordinate coordinate, int skAnnotationType)
    {
        SKAnnotation annotation = new SKAnnotation(id);
        annotation.setLocation(coordinate);
        annotation.setUniqueID(id);
        annotation.setMininumZoomLevel(5);
        annotation.setAnnotationType(skAnnotationType);

        return annotation;
    }


    private long backPressedTime = 0;

    @Override
    public void onBackPressed()
    {
        long t = System.currentTimeMillis();
        if (t - backPressedTime > 2000)
        {
            backPressedTime = t;
            Toast.makeText(this, "Press back again to exit navigation.", Toast.LENGTH_SHORT).show();
        }
        else
        {
            abort();
        }
    }

    private void abort()
    {
        if (skToolsNavigationInProgress)
        {
            SKNavigationManager.getInstance().stopNavigation();
        }
        currentPositionProvider.stopLocationUpdates();
        SKRouteManager.getInstance().clearCurrentRoute();
        mapView.deleteAllAnnotationsAndCustomPOIs();

        if (textToSpeechEngine != null)
        {
            textToSpeechEngine.stop();
            textToSpeechEngine.shutdown();
        }
        finish(); //bye
    }
    @Override
    protected void onPause()
    {
        super.onPause();
        mapHolder.onPause();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        mapHolder.onResume();
        //setUpMapIfNeeded();
    }

    //region SKMapSurfaceLister overrides
    @Override
    public void onActionPan()
    {

    }

    @Override
    public void onActionZoom()
    {

    }

    @Override
    public void onSurfaceCreated(SKMapViewHolder skMapViewHolder)
    {
        mapView = mapHolder.getMapSurfaceView();
    }

    @Override
    public void onMapRegionChanged(SKCoordinateRegion skCoordinateRegion)
    {

    }

    @Override
    public void onMapRegionChangeStarted(SKCoordinateRegion skCoordinateRegion)
    {

    }

    @Override
    public void onMapRegionChangeEnded(SKCoordinateRegion skCoordinateRegion)
    {

    }

    @Override
    public void onDoubleTap(SKScreenPoint skScreenPoint)
    {

    }

    @Override
    public void onSingleTap(SKScreenPoint skScreenPoint)
    {

    }

    @Override
    public void onRotateMap()
    {

    }

    @Override
    public void onLongPress(SKScreenPoint skScreenPoint)
    {

    }

    @Override
    public void onMapPOISelected(SKMapPOI mapPOI)
    {

    }

    @Override
    public void onAnnotationSelected(SKAnnotation skAnnotation)
    {

    }

    @Override
    public void onCustomPOISelected(SKMapCustomPOI skMapCustomPOI)
    {

    }

    @Override
    public void onCompassSelected()
    {

    }

    @Override
    public void onCurrentPositionSelected()
    {

    }

    @Override
    public void onObjectSelected(int i)
    {

    }

    @Override
    public void onInternationalisationCalled(int i)
    {

    }

    @Override
    public void onBoundingBoxImageRendered(int i)
    {

    }

    @Override
    public void onGLInitializationError(String s)
    {

    }

    @Override
    public void onScreenshotReady(Bitmap bitmap)
    {

    }

    @Override
    public void onInternetConnectionNeeded() {

    }

    @Override
    public void onMapActionDown(SKScreenPoint skScreenPoint)
    {

    }

    @Override
    public void onMapActionUp(SKScreenPoint skScreenPoint)
    {

    }

    @Override
    public void onPOIClusterSelected(SKPOICluster skpoiCluster)
    {

    }
    //endregion

    //region SKCurrentPositionListener overrides
    @Override
    public void onCurrentPositionUpdate(SKPosition skPosition)
    {
        this.currentPositionTime = System.currentTimeMillis();
        this.currentPosition = skPosition;
        SKPositionerManager.getInstance().reportNewGPSPosition(currentPosition);
        if (skToolsNavigationInProgress)
        {
            if (this.currentPosition.getHorizontalAccuracy() >= 150)
            {
                numberOfConsecutiveBadPositionReceivedDuringNavi++;
                if (numberOfConsecutiveBadPositionReceivedDuringNavi >= 3)
                {
                    numberOfConsecutiveBadPositionReceivedDuringNavi = 0;
                    onGPSSignalLost();
                }
            }
            else
            {
                numberOfConsecutiveBadPositionReceivedDuringNavi = 0;
                onGPSSignalRecovered();
            }
        }

        if (mapView != null && !_locationInitialized)
        {
            _locationInitialized = true;
            launchRouteCalculation();
        }
    }


    /**
     * Called when the gps signal was lost
     */
    private void onGPSSignalLost() {
        //navigationManager.showSearchingForGPSPanel();
    }

    /**
     * Called when the gps signal was recovered after a loss
     */
    private void onGPSSignalRecovered()
    {
       // navigationManager.hideSearchingForGPSPanel();
    }
    //endregion

    //region SKRouteListener overrides
    @Override
    public void onRouteCalculationCompleted(SKRouteInfo skRouteInfo)
    {
        //TODO: implement
    }

    @Override
    public void onRouteCalculationFailed(SKRoutingErrorCode skRoutingErrorCode)
    {
        //TODO: implement
    }

    @Override
    public void onAllRoutesCompleted()
    {
        SKNavigationSettings navigationSettings = new SKNavigationSettings();
        navigationSettings.setNavigationType(SKNavigationSettings.SKNavigationType.SIMULATION);

        SKNavigationManager navigationManager = SKNavigationManager.getInstance();

        navigationManager.setMapView(mapView);
        navigationManager.setNavigationListener(this);

        navigationManager.startNavigation(navigationSettings);

        skToolsNavigationInProgress = true;
    }

    @Override
    public void onServerLikeRouteCalculationCompleted(SKRouteJsonAnswer skRouteJsonAnswer)
    {

    }

    @Override
    public void onOnlineRouteComputationHanging(int i)
    {

    }
    //endregion

    //region SKNavigationLister overrides
    @Override
    public void onDestinationReached()
    {
        //TODO: implement
    }

    @Override
    public void onSignalNewAdviceWithInstruction(String s)
    {
        SKLogging.writeLog(TAG, " onSignalNewAdviceWithInstruction " + s, Log.DEBUG);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            ttsGreater21(s);
        }
        else
        {
            ttsUnder20(s);
        }
    }

    @SuppressWarnings("deprecation")
    private void ttsUnder20(String s)
    {
        HashMap<String, String> map = new HashMap<>();
        map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "MessageId");
        textToSpeechEngine.speak(s, TextToSpeech.QUEUE_ADD, map);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void ttsGreater21(String s)
    {
        String utteranceId = this.hashCode() + "";
        textToSpeechEngine.speak(s, TextToSpeech.QUEUE_ADD, null, utteranceId);
    }

    @Override
    public void onSignalNewAdviceWithAudioFiles(String[] strings, boolean b)
    {

    }

    @Override
    public void onSpeedExceededWithAudioFiles(String[] strings, boolean b)
    {

    }

    @Override
    public void onSpeedExceededWithInstruction(String s, boolean b)
    {

    }

    @Override
    public void onUpdateNavigationState(SKNavigationState skNavigationState)
    {

    }

    @Override
    public void onReRoutingStarted()
    {
        //TODO: implement
    }

    @Override
    public void onFreeDriveUpdated(String s, String s1, String s2, SKNavigationState.SKStreetType skStreetType, double v, double v1)
    {

    }

    @Override
    public void onViaPointReached(int i)
    {
        Client c = getClientFromViaPointID(i);
        if (c == null) return;

        Toast.makeText(this, "Reached client " + c.getName(), Toast.LENGTH_SHORT).show();
    }

    private Client getClientFromViaPointID(int i)
    {
        SKViaPoint pointReached = null;
        for (SKViaPoint vP : _pointsList)
        {
            if (vP.getUniqueId() == i)
            {
                pointReached = vP;
            }
        }
        if (pointReached == null) return null;

        Client gegClient = null;
        for (Client c : _clientsToDisplay)
        {
            if (c.getLatitude() == pointReached.getPosition().getLatitude())
            {
                if (c.getLongitude() == pointReached.getPosition().getLongitude())
                {
                    gegClient = c;
                }
            }
        }
        return gegClient;
    }

    @Override
    public void onVisualAdviceChanged(boolean firstVisualAdviceChanged, boolean secondVisualAdviceChanged, SKNavigationState skNavigationState)
    {

    }

    @Override
    public void onTunnelEvent(boolean b)
    {

    }
    //endregion

    //region POITrackerListener
    @Override
    public void onUpdatePOIsInRadius(double v, double v1, int i)
    {

    }

    @Override
    public void onReceivedPOIs(SKTrackablePOIType skTrackablePOIType, List<SKDetectedPOI> list)
    {

    }
    //endregion
}
