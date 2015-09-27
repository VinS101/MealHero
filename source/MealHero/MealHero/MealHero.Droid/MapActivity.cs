/*
 * Washington Garcia
 * wgarcia2013@fau.edu
 * CEN4010 Prin. of Software Engineering
 * Google Maps & Location API Xamarin implementation
 * Recommended to test on actual device, emulators I tested had no support for v2 api. Tested on ZTE Zmax.
 */

using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

using Android.App;
using Android.Content;
using Android.OS;
using Android.Runtime;
using Android.Views;
using Android.Widget;
using Android.Support.V4.App;
using Android.Gms.Common;
using Android.Gms.Common.Apis;
using Android.Gms.Maps;
using Android.Gms.Maps.Model;
using Android.Gms.Location;
using Android.Locations;

namespace MealHero.Droid
{
	/// <summary>
	/// Main activity.
	/// </summary>
	/// 
	[Activity(Label = "MapActivity")]
	public class MapActivity : Activity, IOnMapReadyCallback, IGoogleApiClientConnectionCallbacks, IGoogleApiClientOnConnectionFailedListener, Android.Gms.Location.ILocationListener
	{
		private TextView _LocationView;
		private LocationRequest _LocationRequest;
		GoogleApiClientBuilder _clientBuilder;
		IGoogleApiClient _client;
		GoogleMap _googleMap;
		MarkerOptions _myLocationMarker;

		protected override void OnCreate (Bundle bundle)
		{
			RequestWindowFeature(WindowFeatures.NoTitle);

			base.OnCreate (bundle);

			_LocationView = new TextView (this);

			SetContentView (Resource.Layout.Map);

			// GoogleApiClientBuilder accepts values normally accepted for GoogleAPIClient, use .Build to get an interface we can use as a client. 
			_clientBuilder = new GoogleApiClientBuilder (this);
			_clientBuilder.AddApi (LocationServices.API);
			_clientBuilder.AddConnectionCallbacks (this);
			_clientBuilder.AddOnConnectionFailedListener (this);
			_client = _clientBuilder.Build ();

			_client.Connect ();
		}

		/// <summary>
		/// Called when the connected event is raised.
		/// </summary>
		/// <param name="bundle">Bundle.</param>
		public void OnConnected(Bundle bundle)
		{
			_LocationRequest = LocationRequest.Create ();
			_LocationRequest.SetPriority (LocationRequest.PriorityHighAccuracy);
			_LocationRequest.SetInterval (1000);

			// Subscribe us to location updates and call our implementation of OnLocationChanged from ILocationListener
			LocationServices.FusedLocationApi.RequestLocationUpdates (_client, _LocationRequest, this);

			// Find the area on the screen to use as map and tell google services to put a map there using our API key. 
			// If it's grey something went wrong with authentication. Check your Google Console settings. 
			// Note: F12 on "map" in Resource.id.map, to access the assembly, then look on the left panel under Resources>layout, open Main.axml that is our screen. Bug with xamarin.
			var mapFragment = (MapFragment)FragmentManager.FindFragmentById (Resource.Id.map);
			mapFragment.GetMapAsync (this);
		}

		/// <summary>
		/// Callback for the map ready event.
		/// </summary>
		/// <param name="googleMap">Google map.</param>
		public void OnMapReady (GoogleMap googleMap)
		{
			// Obtain a reference of the map we just received. 
			_googleMap = googleMap;

			if (_client.IsConnected) 
			{
				Location initialLocation = LocationServices.FusedLocationApi.GetLastLocation (_client);
				LatLng initialCoords = new LatLng (initialLocation.Latitude, initialLocation.Longitude);

				if (_googleMap != null) 
				{
					// Move the camera to our latest position and add a marker. 
					CameraPosition newCameraPosition = new CameraPosition (initialCoords, 1, 1, 1);

					_googleMap.MoveCamera (CameraUpdateFactory.NewCameraPosition (newCameraPosition));
					_googleMap.MoveCamera (CameraUpdateFactory.ZoomTo (4));

					_myLocationMarker = new MarkerOptions ();
					_myLocationMarker.SetTitle ("This is my location");
					_myLocationMarker.SetPosition (initialCoords);

					_googleMap.AddMarker (_myLocationMarker);
				}
			}
		}

		/// <summary>
		/// Raises the connection suspended event.
		/// </summary>
		/// <param name="i">The index.</param>
		public void OnConnectionSuspended(int i)
		{
			//todo [washington] not implemented
		}

		/// <summary>
		/// Raises the connection failed event.
		/// </summary>
		/// <param name="result">Result.</param>
		public void OnConnectionFailed(ConnectionResult result)
		{
			//todo [washington] not implemented
		}

		/// <summary>
		/// Raises the location changed event.
		/// </summary>
		/// <param name="location">Location.</param>
		public void OnLocationChanged(Location location)
		{
			try
			{
				MoveMarker (location);
			}
			catch (Exception e) 
			{
				// I tried
				Toast.MakeText (this, e.Message, ToastLength.Long);
			}

		}

		/// <summary>
		/// Moves the marker after location has changed. 
		/// </summary>
		/// <param name="location">Location.</param>
		public void MoveMarker(Location location)
		{
			if(_googleMap != null)
			{
				LatLng myNewPosition = new LatLng (location.Latitude, location.Longitude);
				_myLocationMarker.SetPosition (myNewPosition); //This doesn't seem to do anything
			}
		}
	}
}


