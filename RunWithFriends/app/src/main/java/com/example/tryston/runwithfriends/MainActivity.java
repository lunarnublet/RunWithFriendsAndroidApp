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
import android.widget.Toast;

import com.example.tryston.runwithfriends.maps.DirectionsResult;
import com.example.tryston.runwithfriends.maps.GoogleMapsAPIHelper;
import com.example.tryston.runwithfriends.maps.LatLngEx;
import com.example.tryston.runwithfriends.model.Route;
import com.example.tryston.runwithfriends.repository.SavedRoutes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;


import java.util.ArrayList;
import java.util.List;

import com.example.tryston.runwithfriends.repository.Server;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback, RouteSelection, RouteReceiver {

    private GoogleMap mMap;

    private LocationManager locationManager;
    private LocationListener locationListener;

    private LatLng currentLocation;
    private Route currentRoute;
    private Circle removableCircle;
    private SavedRoutes savedRoutes;
    private SavedRoutesFragment fragment;

    private FragmentManager manager;
    private FragmentTransaction transaction;

    private GoogleMapsAPIHelper gmapsApi;

    private static final int LOCATION_SERVICE_REQUEST = 92;

    private boolean isNavigatorDrawn = false;
    private List<LatLng> currentRoutePolylineCache;

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

        gmapsApi = new GoogleMapsAPIHelper(/*this*/);

        savedRoutes = new SavedRoutes(new Server("http://10.0.2.2:19842/"));
        savedRoutes.init(this);

        transaction = manager.beginTransaction();
        fragment = new SavedRoutesFragment();

        fragment.SetRouteSelection(this);

        transaction.add(R.id.fragment_container, fragment, "fragment");
        transaction.commit();
        transaction.hide(fragment);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        updateRoutesFragment();
        mMap = googleMap;

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if (isNavigatorDrawn && !shouldDrawNavigatorLine(currentLocation, currentRoute.getStart())) {
                    mMap.clear();

                    addMarker(currentRoute.getStart(), BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                    addMarker(currentRoute.getEnd(), BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

                    if (currentRoutePolylineCache != null) {
                        drawPolyline(currentRoutePolylineCache, 15f, Color.RED);
                    }
                    isNavigatorDrawn = false;
                }

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

    private void createDot()
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

    private void moveDot()
    {
        removableCircle.remove();
        createDot();
    }

    private void moveCameraToLocation(LatLng location)
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
        onListButtonClicked(null);
        mMap.clear();
        createDot();
        addMarker(currentRoute.getStart(), BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        addMarker(currentRoute.getEnd(), BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        moveCameraToLocation(currentRoute.getStart());

        DirectionsResult routeDirections = gmapsApi.execute(currentRoute.getStart(), currentRoute.getEnd());
        drawPolyline(routeDirections.points, 15f, Color.RED);
        currentRoutePolylineCache = routeDirections.points;

        if (shouldDrawNavigatorLine(currentLocation, currentRoute.getStart())) {
            isNavigatorDrawn = true;
            DirectionsResult navigatorDirections = gmapsApi.execute(currentLocation, currentRoute.getStart());
            drawPolyline(navigatorDirections.points, 15f, Color.BLACK);
        } else {
            isNavigatorDrawn = false;
        }
    }

    private boolean shouldDrawNavigatorLine(LatLng current, LatLng routeStart) {
        final double epsilon = 0.0001;
        return !(LatLngEx.isInRange(current, routeStart, epsilon, epsilon));
    }

    private void addMarker(LatLng position, BitmapDescriptor bmpDescriptor) {
        MarkerOptions marker = new MarkerOptions();
        marker.icon(bmpDescriptor);
        marker.position(position);
        mMap.addMarker(marker);
    }

    public void onNewRoute(View view)
    {
        Intent intent = new Intent(this, CreateRouteActivity.class);
        double lat = currentLocation.latitude;
        double lon = currentLocation.longitude;
        intent.putExtra("latitude", lat);
        intent.putExtra("longitude", lon);
        startActivityForResult(intent, 1);
    }

    public void onCenterButtonClicked(View view)
    {
        moveCameraToLocation(currentLocation);
    }

    public void onListButtonClicked(View view)
    {
        transaction = manager.beginTransaction();
        if(fragment.isHidden())
        {
            transaction.show(fragment);
            transaction.commit();
            findViewById(R.id.route_controls).setVisibility(View.VISIBLE);
        }
        else
        {
            transaction.hide(fragment);
            transaction.commit();
            findViewById(R.id.route_controls).setVisibility(View.INVISIBLE);
        }

    }

    public void updateRoutesFragment()
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
            onListButtonClicked(null);
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
                    updateRoutesFragment();
                }
                break;
            }
        }
    }

    @Override
    public void onRouteFound(ArrayList<LatLng> points, double distance) {
        PolylineOptions lineOptions = new PolylineOptions();
        lineOptions.addAll(points);
        lineOptions.width(2);
        lineOptions.color(Color.RED);
        lineOptions.width(15);
        mMap.addPolyline(lineOptions);
    }

    private void drawPolyline(List<LatLng> points, float width, int color) {
        PolylineOptions polylineOptions = new PolylineOptions();
        polylineOptions.addAll(points);
        polylineOptions.width(2);
        polylineOptions.color(color);
        polylineOptions.width(width);
        mMap.addPolyline(polylineOptions);
    }


    public void onDeleteClick(View view)
    {
        if (currentRoute != null) {
            savedRoutes.Remove(currentRoute);
            currentRoute = null;
            updateRoutesFragment();
            mMap.clear();
            createDot();
        } else {
            Toast.makeText(this, "Select a route", Toast.LENGTH_SHORT).show();
        }
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
        if (isNavigatorDrawn && !shouldDrawNavigatorLine(currentLocation, currentRoute.getStart())) {
            mMap.clear();

            addMarker(currentRoute.getStart(), BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            addMarker(currentRoute.getEnd(), BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

            if (currentRoutePolylineCache != null) {
                drawPolyline(currentRoutePolylineCache, 15f, Color.RED);
            }
            isNavigatorDrawn = false;
        }
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
