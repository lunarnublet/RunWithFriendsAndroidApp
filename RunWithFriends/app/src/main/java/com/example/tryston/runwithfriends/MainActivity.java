package com.example.tryston.runwithfriends;

import android.Manifest;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.KeyEvent;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;


import java.util.ArrayList;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback, RouteSelection, RouteReciever {

    private GoogleMap mMap;

    LocationManager locationManager;
    LocationListener locationListener;

    LatLng currentLocation;
    Route currentRoute;
    Circle removableCircle;
    Server server;
    SavedRoutes savedRoutes;
    SavedRoutesFragment fragment;

    FragmentManager manager;
    FragmentTransaction transaction;

    GoogleMapsAPIHelper helper;

    private static final int LOCATION_SERVICE_REQUEST = 92;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        manager = getFragmentManager();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        if (mapFragment == null) {
            mapFragment = SupportMapFragment.newInstance();
        }
        mapFragment.getMapAsync(this);

        helper = new GoogleMapsAPIHelper(this);

        server = new Server("http://10.0.2.2:19842/");
        savedRoutes = new SavedRoutes(server);
        savedRoutes.init(this);

        transaction = manager.beginTransaction();
        fragment = new SavedRoutesFragment();

        fragment.SetRouteSelection(this);

        transaction.add(R.id.fragment_container, fragment, "fragment");
        transaction.commit();
        transaction.hide(fragment);
    }



    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        UpdateFragment();
        mMap = googleMap;

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                //new method for location
                userLocationChanged(location);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        // Enable MyLocation Button in the Map
//            mMap.setMyLocationEnabled(true);
//            mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
//                @Override
//                public boolean onMyLocationButtonClick() {
//                    try
//                    {
//                        mMap.setMyLocationEnabled(true);
//                    }
//                    catch (SecurityException e)
//                    {
//                        Log.e("OnMyLocationButtonClick", )
//                    }
//                    return false;
//                }
//            });
        mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        // Add a marker in Sydney and move the camera
        mMap.setMinZoomPreference(10);
        mMap.setMaxZoomPreference(20);
        if (Build.VERSION.SDK_INT < 22) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

        } else {
            if ((ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                Location lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (lastLocation != null) {
                    currentLocation = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
                    createDot();
                    moveCameraToLocation(currentLocation);
                }
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_SERVICE_REQUEST);
            }
        }

    }

    public void createDot()
    {
        if (currentLocation != null) {
            CircleOptions circleOptions = new CircleOptions();
            circleOptions.center(currentLocation);
            circleOptions.fillColor(Color.rgb(0,206,209));
            circleOptions.radius(9);
            circleOptions.strokeColor(Color.rgb(0,139,139));
            circleOptions.strokeWidth(7);
            removableCircle = mMap.addCircle(circleOptions);
        }
    }

    public void moveDot()
    {
        removableCircle.remove();
        createDot();
    }

    public void moveCameraToLocation(LatLng location)
    {
        CameraPosition position = new CameraPosition.Builder()
                .target(location)      // Sets the center of the map to location user
                .zoom(17)                   // Sets the zoom
                .bearing(0)                // Sets the orientation of the camera to east
                .tilt(0)                   // Sets the tilt of the camera to 30 degrees
                .build();                   // Creates a CameraPosition from the builder
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(position));
    }

    public void userLocationChanged(Location location)
    {
        currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
        updateLocationUI();
    }

    private void updateLocationUI() {
        if (removableCircle == null) {
            createDot();
            moveCameraToLocation(currentLocation);
        } else {
            moveDot();
        }
    }

    @Override
    public void Selected(int i)
    {
        currentRoute = savedRoutes.Get(i);
        helper.Execute(currentRoute.getStart(), currentRoute.getEnd());
        OnListButtonClicked(null);
        mMap.clear();

        if (currentLocation == null) {
            currentLocation = currentRoute.getStart();
        }
        createDot();
        MarkerOptions marker = new MarkerOptions();
        marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        marker.position(currentRoute.getStart());
        mMap.addMarker(marker);
        marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        marker.position(currentRoute.getEnd());
        mMap.addMarker(marker);
        moveCameraToLocation(currentRoute.getStart());
    }

    public void OnNewRoute(View view)
    {
        Intent intent = new Intent(this, CreateRouteActivity.class);
        double lat = currentLocation.latitude;
        double lon = currentLocation.longitude;
        intent.putExtra("latitude", lat);
        intent.putExtra("longitude", lon);
        startActivityForResult(intent, 1);
    }

    public void OnCenterButtonClicked(View view)
    {
        moveCameraToLocation(currentLocation);
    }

    public void OnListButtonClicked(View view)
    {
        transaction = manager.beginTransaction();
        if(fragment.isHidden())
        {
            transaction.show(fragment);
            transaction.commit();
        }
        else
        {
            transaction.hide(fragment);
            transaction.commit();
        }

    }

    public void UpdateFragment()
    {
        ArrayList<String> routeNames = new ArrayList<>();
        for(int i = 0; i < savedRoutes.Count(); ++i)
        {
            String display = savedRoutes.Get(i).getName() + ": " + savedRoutes.Get(i).getDistance() + "km";
            if(savedRoutes.Get(i).getIsLoopRoute())
            {
                display += " (loop)";
            }
            routeNames.add(display);
        }
        fragment.SetRouteNames(routeNames);
        if(fragment.isHidden())
        {
            OnListButtonClicked(null);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case (1) : {
                if (resultCode == Activity.RESULT_OK) {
                    double startlat = data.getDoubleExtra("startlatitude", 0.0);
                    double startlon = data.getDoubleExtra("startlongitude", 0.0);
                    double endlat = data.getDoubleExtra("endlatitude", 0.0);
                    double endlon = data.getDoubleExtra("endlongitude", 0.0);
                    double distance = data.getDoubleExtra("distance", 0.0);
                    String name = data.getStringExtra("routename");
                    boolean loop = data.getBooleanExtra("looproute", false);
                    Route route = new Route(new LatLng(startlat, startlon), new LatLng(endlat, endlon),distance, name, loop);
                    savedRoutes.Add(route);
                    UpdateFragment();
                }
                break;
            }
        }
    }

    @Override
    public void OnRouteFound(ArrayList<LatLng> points, double distance) {
        PolylineOptions lineOptions = new PolylineOptions();
        lineOptions.addAll(points);
        lineOptions.width(2);
        lineOptions.color(Color.RED);
        lineOptions.width(15);
        mMap.addPolyline(lineOptions);
    }
    public void OnDeleteClick(View view)
    {

        savedRoutes.Remove(currentRoute);
        currentRoute = null;
        UpdateFragment();
        mMap.clear();
        createDot();
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        final double step = .0001;
        switch (keyCode) {
            case KeyEvent.KEYCODE_W:
                moveAround(currentLocation.latitude + step, currentLocation.longitude);
                return true;
            case KeyEvent.KEYCODE_A:
                moveAround(currentLocation.latitude, currentLocation.longitude - step);
                return true;
            case KeyEvent.KEYCODE_S:
                moveAround(currentLocation.latitude - step, currentLocation.longitude);
                return true;
            case KeyEvent.KEYCODE_D:
                moveAround(currentLocation.latitude, currentLocation.longitude + step);
                return true;
            default:
                return super.onKeyUp(keyCode, event);
        }
    }

    private void moveAround(double lat, double lng) {
        currentLocation = new LatLng(lat, lng);
        updateLocationUI();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case LOCATION_SERVICE_REQUEST: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if ((ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)) {
                        Location lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        if (lastLocation != null) {
                            currentLocation = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
                            createDot();
                            moveCameraToLocation(currentLocation);
                        }
                    }
                }
            }
        }
    }
}
