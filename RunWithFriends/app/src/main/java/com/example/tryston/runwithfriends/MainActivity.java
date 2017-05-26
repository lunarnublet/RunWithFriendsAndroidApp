package com.example.tryston.runwithfriends;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Cap;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;


import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback, RouteSelection {

    private GoogleMap mMap;

    LocationManager locationManager;
    LocationListener locationListener;

    LatLng currentLocation;
    Route currentRoute;
    Circle removableCircle;
    ArrayList<String> savedRoutes;
    View fragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

//        savedRoutes = new ArrayList<>();
//        savedRoutes.add("one");
//        savedRoutes.add("two");
//        savedRoutes.add("three");
//        savedRoutes.add("four");
//
//        fragment = findViewById(R.id.fragment);
//        fragment.setVisibility(View.GONE);
//
//        fragment.setVisibility(View.VISIBLE);

        DownloadMaterial downloadMaterial = new DownloadMaterial();
        TextView textView = (TextView)findViewById(R.id.textView);
        try
        {
            downloadMaterial.execute("http://10.10.2.2").get();

        }catch(Exception e)
        {
            Log.e("onCreate Error", e.toString());

        }


    }

    public class DownloadMaterial extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String result = "";
            URL url;
            HttpURLConnection urlConnection;
            try {
                url = new URL(params[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestProperty("Content-type", "application/x-www-form-urlencoded");
                urlConnection.setRequestProperty("Accept", "application/json");
                urlConnection.setRequestMethod("POST");
                OutputStreamWriter stream = new OutputStreamWriter(urlConnection.getOutputStream());
                JSONObject object = new JSONObject();
                object.put("grant_type", "password");
                object.put("username", "a@a.com");
                object.put("password", "password!");

                stream.write(object.toString());

                urlConnection.connect();
                InputStream inputStream = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(inputStream);
                int in = reader.read();
                while (in != -1) {
                    char c = (char) in;
                    result += c;
                    in = reader.read();
                }

                Log.e("result", result);
                return result;
            } catch (Exception e) {
                Log.e("onCreate Error", e.toString());
                return "Web search failed";
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);


            try {
                JSONObject jsonObject = new JSONObject(s);
                String string = jsonObject.getString("route");
                Log.e("route", string);
                JSONArray jsonArray = new JSONArray(string);
                for (int i = 0; i < jsonArray.length(); ++i) {
                    JSONObject jsonObject1 = jsonArray.getJSONObject(0);
                    Log.e("main", jsonObject1.getString("main"));
                    Log.e("description", jsonObject1.getString("description"));
                }
            } catch (Exception e) {
                Log.e("Json failed", e.toString());
            }
        }
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
                currentLocation = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
                createDot();
                moveCameraToCurrentLocation();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }

    }

    public void createDot()
    {
        CircleOptions c = new CircleOptions();
        CircleOptions circleOptions = new CircleOptions();
        circleOptions.center(currentLocation);
        circleOptions.fillColor(Color.rgb(0,206,209));
        circleOptions.radius(9);
        circleOptions.strokeColor(Color.rgb(0,139,139));
        circleOptions.strokeWidth(7);
        removableCircle = mMap.addCircle(circleOptions);
    }

    public void moveDot()
    {
        removableCircle.remove();
        createDot();
    }

    public void moveCameraToCurrentLocation()
    {
        CameraPosition position = new CameraPosition.Builder()
                .target(currentLocation)      // Sets the center of the map to location user
                .zoom(17)                   // Sets the zoom
                .bearing(0)                // Sets the orientation of the camera to east
                .tilt(0)                   // Sets the tilt of the camera to 30 degrees
                .build();                   // Creates a CameraPosition from the builder
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(position));
    }

    public void userLocationChanged(Location location)
    {
        currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
        moveDot();
    }

    @Override
    public void Selected(Route route) {
        currentRoute = route;
    }

    public void OnNewRoute(View view)
    {
        Intent intent = new Intent(this, CreateRouteActivity.class);
        double lat = currentLocation.latitude;
        double lon = currentLocation.longitude;
        intent.putExtra("latitude", lat);
        intent.putExtra("longitude", lon);
        startActivity(intent);
    }

    public void OnCenterButtonClicked(View view)
    {
        moveCameraToCurrentLocation();
    }

}
