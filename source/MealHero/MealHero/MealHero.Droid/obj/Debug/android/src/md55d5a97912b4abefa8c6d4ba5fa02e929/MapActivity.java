package md55d5a97912b4abefa8c6d4ba5fa02e929;


public class MapActivity
	extends android.app.Activity
	implements
		mono.android.IGCUserPeer,
		com.google.android.gms.maps.OnMapReadyCallback,
		com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks,
		com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener,
		com.google.android.gms.location.LocationListener
{
	static final String __md_methods;
	static {
		__md_methods = 
			"n_onCreate:(Landroid/os/Bundle;)V:GetOnCreate_Landroid_os_Bundle_Handler\n" +
			"n_onMapReady:(Lcom/google/android/gms/maps/GoogleMap;)V:GetOnMapReady_Lcom_google_android_gms_maps_GoogleMap_Handler:Android.Gms.Maps.IOnMapReadyCallbackInvoker, Xamarin.GooglePlayServices.Maps\n" +
			"n_onConnected:(Landroid/os/Bundle;)V:GetOnConnected_Landroid_os_Bundle_Handler:Android.Gms.Common.Apis.IGoogleApiClientConnectionCallbacksInvoker, Xamarin.GooglePlayServices.Base\n" +
			"n_onConnectionSuspended:(I)V:GetOnConnectionSuspended_IHandler:Android.Gms.Common.Apis.IGoogleApiClientConnectionCallbacksInvoker, Xamarin.GooglePlayServices.Base\n" +
			"n_onConnectionFailed:(Lcom/google/android/gms/common/ConnectionResult;)V:GetOnConnectionFailed_Lcom_google_android_gms_common_ConnectionResult_Handler:Android.Gms.Common.Apis.IGoogleApiClientOnConnectionFailedListenerInvoker, Xamarin.GooglePlayServices.Base\n" +
			"n_onLocationChanged:(Landroid/location/Location;)V:GetOnLocationChanged_Landroid_location_Location_Handler:Android.Gms.Location.ILocationListenerInvoker, Xamarin.GooglePlayServices.Location\n" +
			"";
		mono.android.Runtime.register ("MealHero.Droid.MapActivity, MealHero.Droid, Version=1.0.0.0, Culture=neutral, PublicKeyToken=null", MapActivity.class, __md_methods);
	}


	public MapActivity () throws java.lang.Throwable
	{
		super ();
		if (getClass () == MapActivity.class)
			mono.android.TypeManager.Activate ("MealHero.Droid.MapActivity, MealHero.Droid, Version=1.0.0.0, Culture=neutral, PublicKeyToken=null", "", this, new java.lang.Object[] {  });
	}


	public void onCreate (android.os.Bundle p0)
	{
		n_onCreate (p0);
	}

	private native void n_onCreate (android.os.Bundle p0);


	public void onMapReady (com.google.android.gms.maps.GoogleMap p0)
	{
		n_onMapReady (p0);
	}

	private native void n_onMapReady (com.google.android.gms.maps.GoogleMap p0);


	public void onConnected (android.os.Bundle p0)
	{
		n_onConnected (p0);
	}

	private native void n_onConnected (android.os.Bundle p0);


	public void onConnectionSuspended (int p0)
	{
		n_onConnectionSuspended (p0);
	}

	private native void n_onConnectionSuspended (int p0);


	public void onConnectionFailed (com.google.android.gms.common.ConnectionResult p0)
	{
		n_onConnectionFailed (p0);
	}

	private native void n_onConnectionFailed (com.google.android.gms.common.ConnectionResult p0);


	public void onLocationChanged (android.location.Location p0)
	{
		n_onLocationChanged (p0);
	}

	private native void n_onLocationChanged (android.location.Location p0);

	java.util.ArrayList refList;
	public void monodroidAddReference (java.lang.Object obj)
	{
		if (refList == null)
			refList = new java.util.ArrayList ();
		refList.add (obj);
	}

	public void monodroidClearReferences ()
	{
		if (refList != null)
			refList.clear ();
	}
}
