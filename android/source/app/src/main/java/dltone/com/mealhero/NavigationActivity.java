/*
  _____  _   _______ ____               ______       _                       _
 |  __ \| | |__   __/ __ \             |  ____|     | |                     (_)
 | |  | | |    | | | |  | |_ __   ___  | |__   _ __ | |_ ___ _ __ _ __  _ __ _ ___  ___  ___
 | |  | | |    | | | |  | | '_ \ / _ \ |  __| | '_ \| __/ _ \ '__| '_ \| '__| / __|/ _ \/ __|
 | |__| | |____| | | |__| | | | |  __/ | |____| | | | ||  __/ |  | |_) | |  | \__ \  __/\__ \
 |_____/|______|_|  \____/|_| |_|\___| |______|_| |_|\__\___|_|  | .__/|_|  |_|___/\___||___/
                                                                 | |
                                                                 |_|
 */
package dltone.com.mealhero;

import android.annotation.TargetApi;
import android.app.DialogFragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.skobbler.ngx.SKCoordinate;
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
import com.skobbler.ngx.routing.SKRouteInfo;
import com.skobbler.ngx.routing.SKRouteJsonAnswer;
import com.skobbler.ngx.routing.SKRouteListener;
import com.skobbler.ngx.routing.SKRouteManager;
import com.skobbler.ngx.routing.SKRouteSettings;
import com.skobbler.ngx.routing.SKViaPoint;
import com.skobbler.ngx.util.SKGeoUtils;
import com.skobbler.ngx.util.SKLogging;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

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

    // Points list for calculating the route
    private ArrayList<SKViaPoint> _pointsList = new ArrayList<>();

    // Its a coordinates list with coordinates
    private List<SKCoordinate> _coordinatesList = new ArrayList<>();

    // original copy from initial DB query
    private List<Client> _clientsToDisplay = new ArrayList<>();

    // Used for keeping track of which clients have been visited.
    private List<Client> _clientsToVisit = new ArrayList<>();

    // Used for auto sorting of clients during address validation
    private Map<Double, Client> _sortedDistanceToClientsTree;

    // To be used for UI list
    private List<Client> _sortedClientsListByDistance = new ArrayList<>();

    // To be used for calculating visitation order and adding to _pointsList
    private Map<Double, SKCoordinate> _sortedDistanceToCoordinatesTree;

    // Audio advisor
    private TextToSpeech textToSpeechEngine;

    //UI References
    private ListView mClientListView;

    //List of Clients
    private ClientListAdapter clientAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        //MealHeroUtils.initializeLibrary(this);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_navigation);
        MHApp = (MealHeroApplication) getApplication();

        _clientsToDisplay = ClientProvider.GetAssignedClients(MHApp.getLoggedInVolunteer(), MHApp);
        _clientsToVisit.addAll(_clientsToDisplay);

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
            SKCoordinate coord = new SKCoordinate(longitude, latitude);
            _coordinatesList.add(coord);
        }

        return true;
    }

    private Boolean InitiateGreedySort()
    {
        _sortedDistanceToCoordinatesTree = new TreeMap<>();
        _sortedDistanceToClientsTree = new TreeMap<>();

        //  Populate trees
        for (Client client : _clientsToDisplay)
        {
            Double latitude = client.getLatitude();
            Double longitude = client.getLongitude();
            SKCoordinate coord = new SKCoordinate(longitude, latitude);

            // Greedy sort clients based on air distance (TreeMap automatically sorts by key)
            SKCoordinate currentPositionCoord = new SKCoordinate(currentPosition.getCoordinate().getLongitude(), currentPosition.getCoordinate().getLatitude());

            Double airDistance = SKGeoUtils.calculateAirDistanceBetweenCoordinates(currentPositionCoord, coord);

            _sortedDistanceToCoordinatesTree.put(airDistance, coord);

            _sortedDistanceToClientsTree.put(airDistance, client);
        }

        _sortedClientsListByDistance = new ArrayList<>();
        _sortedClientsListByDistance.addAll(_sortedDistanceToClientsTree.values());

        // using SKViaPoint to add waypoints for calculation in LaunchRouteCalculation()
        _pointsList = new ArrayList<>();

        for (SKCoordinate coord : _sortedDistanceToCoordinatesTree.values())
        {
            if (coord == null)
            {
                return false;
            }

            SKViaPoint temp = new SKViaPoint(UniqueID.getID(), coord);
            _pointsList.add(temp);
        }

        return true;
    }

    private void LaunchRouteCalculation()
    {
        if (!InitiateGreedySort())
        {
            Toast.makeText(this, "Error retrieving addresses. Verify client addresses or contact an administrator.", Toast.LENGTH_SHORT).show();
            abort();
        }
        // get a route object and populate it with the desired properties
        SKRouteSettings route = new SKRouteSettings();

        SKCoordinate currentPositionCoord = new SKCoordinate(currentPosition.getCoordinate().getLongitude(), currentPosition.getCoordinate().getLatitude());
        // set start point, set start annotation
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

        SKCoordinate destCoord = _pointsList.get(_pointsList.size() - 1).getPosition();
        // add destination to route, set end annotation
        route.setDestinationCoordinate(destCoord);

        // setup route UI
        setupAnnotations(currentPositionCoord, _pointsList, destCoord);
        setupClientUIOverlay();

        if (_pointsList.size() > 1)
        {
            route.setViaPoints(_pointsList);
        }
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

    private void setupClientUIOverlay()
    {
        mClientListView = (ListView) findViewById(R.id._clientNavList);
        //Set up array adapter
        clientAdapter = new ClientListAdapter(this, (ArrayList<Client>) _sortedClientsListByDistance);
        mClientListView.setAdapter(clientAdapter);

        //Notify adapter of data change
        clientAdapter.notifyDataSetChanged();

        mClientListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Intent intent = new Intent(getApplicationContext(), ClientDetailActivity.class);
                intent.putExtra("ClientID", _sortedClientsListByDistance.get((int) id).getObjectId());
                startActivity(intent);
            }
        });


        View item = clientAdapter.getView(0, null, mClientListView);
        item.measure(0, 0);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (1.5 * item.getMeasuredHeight()));
        mClientListView.setLayoutParams(params);

        // Aesthetics 0xFF444444
        //region Hex Opacity Values
        /*  100% — FF
            95% — F2
            90% — E6
            85% — D9
            80% — CC
            75% — BF
            70% — B3
            65% — A6
            60% — 99
            55% — 8C
            50% — 80
            45% — 73
            40% — 66
            35% — 59
            30% — 4D
            25% — 40
            20% — 33
            15% — 26
            10% — 1A
            5% — 0D
            0% — 00 */
        //endregion

        mClientListView.setItemsCanFocus(true);
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
        Client c = ClientProvider.GetClientFromCoordinate((ArrayList<Client>) _sortedClientsListByDistance, skAnnotation.getLocation());
        if (c == null) return;

        mClientListView.requestFocus();
        mClientListView.setSelection(_sortedClientsListByDistance.indexOf(c));
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

            // Client coordinate redundancy check
            Iterator<Client> iter = _clientsToVisit.iterator();
            while(iter.hasNext())
            {
                Client c = iter.next();
                SKCoordinate clientCoord = new SKCoordinate(c.getLongitude(), c.getLatitude());
                Double airDistance = SKGeoUtils.calculateAirDistanceBetweenCoordinates(currentPosition.getCoordinate(), clientCoord);
                if (airDistance <= 100)
                {
                    // Client is in the area
                    performLoggingOnVisitedClientList(c);
                    iter.remove();
                }

            }

        }

        if (mapView != null && !_locationInitialized)
        {
            _locationInitialized = true;
            LaunchRouteCalculation();
        }

    }

    private void performLoggingOnVisitedClientList(final Client c)
    {
        SKNavigationManager.getInstance().decreaseSimulationSpeed(100);
        Toast.makeText(this, "Reached client " + c.getName(), Toast.LENGTH_SHORT).show();
        String clientAlert = "Approaching delivery for " + c.getName();
        // Announce the arrival with TTS
        onSignalNewAdviceWithInstruction(clientAlert);
        SKNavigationManager.getInstance().increaseSimulationSpeed(100);

        // Start modification of annotation
        SKAnnotation annotationToModify = null;
        for (SKAnnotation skA : mapView.getAllAnnotations())
        {
            if (skA.getLocation().getLatitude() == c.getLatitude())
            {
                if (skA.getLocation().getLongitude() == c.getLongitude())
                {
                    annotationToModify = skA;
                    break;
                }
            }
        }

        //Create dialog + set listener
        AddClientNoteDialog dialog = new AddClientNoteDialog();
        dialog.setNoticeDialogListener(new NoticeDialogListener() {
            @Override
            public void onDialogPositiveClick(DialogFragment dialog) {
                c.appendLog(((AddClientNoteDialog) dialog).getNote());
            }

            @Override
            public void onDialogNegativeClick(DialogFragment dialog) {
                Log.e(getClass().getName(), "Note add cancelled.");
            }
        });

        if (annotationToModify == null) return;

        mapView.deleteAnnotation(annotationToModify.getUniqueID());
        annotationToModify.setAnnotationType(SKAnnotation.SK_ANNOTATION_TYPE_GREEN);
        mapView.addAnnotation(annotationToModify, SKAnimationSettings.ANIMATION_PIN_DROP);

        // Update the clientList
        mClientListView.requestFocus();
        mClientListView.setSelection(_sortedClientsListByDistance.indexOf(c));
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
        navigationSettings.setNavigationType(MHApp.NavigationType);

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
        if (_pointsList.size() == 1)
        {
            performLoggingOnVisitedClientList(_clientsToVisit.get(0));
        }
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

        if (_clientsToVisit.contains(c))
        {
            performLoggingOnVisitedClientList(c);
            _clientsToVisit.remove(c);
        }

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
