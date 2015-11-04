package dltone.com.mealhero;

import android.graphics.Bitmap;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.skobbler.ngx.SKCoordinate;
import com.skobbler.ngx.map.SKAnnotation;
import com.skobbler.ngx.map.SKCoordinateRegion;
import com.skobbler.ngx.map.SKMapCustomPOI;
import com.skobbler.ngx.map.SKMapFragment;
import com.skobbler.ngx.map.SKMapPOI;
import com.skobbler.ngx.map.SKMapSurfaceListener;
import com.skobbler.ngx.map.SKMapSurfaceView;
import com.skobbler.ngx.map.SKMapViewHolder;
import com.skobbler.ngx.map.SKPOICluster;
import com.skobbler.ngx.map.SKScreenPoint;
import com.skobbler.ngx.navigation.SKNavigationListener;
import com.skobbler.ngx.navigation.SKNavigationManager;
import com.skobbler.ngx.navigation.SKNavigationSettings;
import com.skobbler.ngx.navigation.SKNavigationState;
import com.skobbler.ngx.positioner.SKCurrentPositionListener;
import com.skobbler.ngx.positioner.SKCurrentPositionProvider;
import com.skobbler.ngx.positioner.SKPosition;
import com.skobbler.ngx.positioner.SKPositionerManager;
import com.skobbler.ngx.routing.SKRouteInfo;
import com.skobbler.ngx.routing.SKRouteJsonAnswer;
import com.skobbler.ngx.routing.SKRouteListener;
import com.skobbler.ngx.routing.SKRouteManager;
import com.skobbler.ngx.routing.SKRouteSettings;

import java.util.ArrayList;
import java.util.List;

public class NavigationActivity extends FragmentActivity implements SKCurrentPositionListener, SKMapSurfaceListener, SKRouteListener, SKNavigationListener
{
    /**
     * Application context object
     */
    private MealHeroApplication app;

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

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        //MealHeroUtils.initializeLibrary(this);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_navigation);
        app = (MealHeroApplication) getApplication();

        currentPositionProvider = new SKCurrentPositionProvider(this);
        currentPositionProvider.setCurrentPositionListener(this);
        currentPositionProvider.requestLocationUpdates(MealHeroUtils.hasGpsModule(this), MealHeroUtils.hasNetworkModule(this), false);

        mapHolder = (SKMapViewHolder) findViewById(R.id.map_surface_holder);
        mapHolder.setMapSurfaceListener(this);
        mapView = mapHolder.getMapSurfaceView();

        currentPositionProvider.requestUpdateFromLastPosition();
        //setUpMapIfNeeded();
        /*
        if (currentPosition == null)
        {
            Toast.makeText(this, "Failed to get current location. Try again later", Toast.LENGTH_LONG).show();
            finish();
            return;
        }*/

        launchRouteCalculation();

    }

    private void launchRouteCalculation() {
        // get a route object and populate it with the desired properties
        SKRouteSettings route = new SKRouteSettings();
        // set start and destination points
        route.setStartCoordinate(new SKCoordinate(-122.397674, 37.761278));
        route.setDestinationCoordinate(new SKCoordinate(-122.448270, 37.738761));
        // set the number of routes to be calculated
        route.setNoOfRoutes(1);
        // set the route mode
        route.setRouteMode(SKRouteSettings.SKRouteMode.CAR_FASTEST);
        // set whether the route should be shown on the map after it's computed
        route.setRouteExposed(true);
        // set the route listener to be notified of route calculation
        // events
        SKRouteManager.getInstance().setRouteListener(this);
        // pass the route to the calculation routine
        SKRouteManager.getInstance().calculateRoute(route);

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

    }

    @Override
    public void onRouteCalculationFailed(SKRoutingErrorCode skRoutingErrorCode)
    {

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

    }

    @Override
    public void onSignalNewAdviceWithInstruction(String s)
    {

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

    }

    @Override
    public void onFreeDriveUpdated(String s, String s1, String s2, SKNavigationState.SKStreetType skStreetType, double v, double v1)
    {

    }

    @Override
    public void onViaPointReached(int i)
    {

    }

    @Override
    public void onVisualAdviceChanged(boolean b, boolean b1, SKNavigationState skNavigationState)
    {

    }

    @Override
    public void onTunnelEvent(boolean b)
    {

    }
    //endregion
}
