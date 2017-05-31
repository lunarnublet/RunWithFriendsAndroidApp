package com.example.tryston.runwithfriends;

import android.app.Activity;
import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.Key;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class CreateRouteActivity extends FragmentActivity implements OnMapReadyCallback, RouteReciever {

    private GoogleMap mMap;
    LatLng startPoint;
    LatLng endPoint;
    LatLng recievedLocation;
    private static Context context;
    GoogleMapsAPIHelper helper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_route);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        helper = new GoogleMapsAPIHelper(this);

        startPoint = null;
        endPoint = null;
        Intent i = getIntent();
        double lat = i.getDoubleExtra("latitude", 0);
        double lon = i.getDoubleExtra("longitude", 0);
        recievedLocation = new LatLng(lat, lon);
        context = getApplicationContext();
        EditText e = new EditText(this);
        e.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(keyCode == KeyEvent.KEYCODE_ENTER)
                {
                    EditText text = (EditText)v;
                    String address = text.getText().toString();
                }
                return false;
            }
        });
    }

    public static Context getAppContext() {
        return CreateRouteActivity.context;
    }

    public void putDot()
    {
        CircleOptions circleOptions = new CircleOptions();
        circleOptions.center(recievedLocation);
        circleOptions.fillColor(Color.rgb(0,206,209));
        circleOptions.radius(9);
        circleOptions.strokeColor(Color.rgb(0,139,139));
        circleOptions.strokeWidth(7);
        mMap.addCircle(circleOptions);
    }

    public void centerOnMapLocation()
    {
        putDot();
        CameraPosition position = new CameraPosition.Builder()
                .target(recievedLocation)      // Sets the center of the map to location user
                .zoom(17)                   // Sets the zoom
                .bearing(0)                // Sets the orientation of the camera to east
                .tilt(0)                   // Sets the tilt of the camera to 30 degrees
                .build();                   // Creates a CameraPosition from the builder
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(position));
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
        mMap.setMapType(mMap.MAP_TYPE_TERRAIN);
        centerOnMapLocation();

        // Add a marker in Sydney and move the camera
        if(mMap!=null){

            // Setting onclick event listener for the map
            mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

                @Override
                public void onMapClick(LatLng point) {
//                    if(!((CheckBox)findViewById(R.id.UseMapCheck)).isChecked())
//                    {
//                        return;
//                    }
                    // Already two locations
                    if(startPoint != null && endPoint != null)
                    {
                        startPoint = null;
                        endPoint = null;
                        TextView text = (TextView) findViewById(R.id.StartText);
                        text.setText(null);
                        text = (TextView) findViewById(R.id.EndText);
                        text.setText(null);
                        mMap.clear();
                        putDot();
                    }


                    // Creating MarkerOptions
                    MarkerOptions options = new MarkerOptions();

                    // Setting the position of the marker
                    options.position(point);

                    /**
                     * For the start location, the color of marker is GREEN and
                     * for the end location, the color of marker is RED.
                     */
                    GetAddress g = null;

                    if(startPoint == null)
                    {
                        startPoint = point;
                    }
                    else
                    {
                        endPoint = point;
                    }



                    if(endPoint == null)
                    {
                        options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                        g = new GetAddress(R.id.StartText);
                    }
                    else
                    {
                        options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                        g = new GetAddress(R.id.EndText);
                    }
                    g.execute(point);



                    // Add new marker to the Google Map Android API V2
                    mMap.addMarker(options);

                    //Checks, whether start and end locations are captured
                    if(startPoint != null && endPoint != null){

                        helper.Execute(startPoint, endPoint);
                    }
                }
            });
        }

    }

    @Override
    public void OnRouteFound(ArrayList<LatLng> points) {
        PolylineOptions lineOptions = new PolylineOptions();
        lineOptions.addAll(points);
        lineOptions.width(2);
        lineOptions.color(Color.RED);
        lineOptions.width(15);
        mMap.addPolyline(lineOptions);
    }


    //
//
//
//    private class GetLocation extends AsyncTask<LatLng, Void, String>
//    {
//        int id;
//        private GetAddress(int editTextId)
//        {
//            id = editTextId;
//        }
//        @Override
//        protected String doInBackground(LatLng... params) {
//            LatLng point = params[0];
//            Geocoder geocoder;
//            List<Address> addresses = null;
//            String address = "";
//            geocoder = new Geocoder(CreateRouteActivity.getAppContext(), Locale.getDefault());
//            try
//            {
//                addresses = geocoder.getFromLocation(point.latitude, point.longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
//            }
//            catch (Exception e)
//            {
//                Log.e("Geocoder", "Fucked up somehow");
//            }
//            if(addresses != null)
//            {
//                address = addresses.get(0).getAddressLine(0);
//            }
//            return address;
//        }
//        @Override
//        protected void onPostExecute(String s) {
//            super.onPostExecute(s);
//            EditText e = (EditText)findViewById(id);
//            e.setText(s);
//        }
//    }
    private class GetAddress extends AsyncTask<LatLng, Void, String>
    {
        int id;
        private GetAddress(int editTextId)
        {
            id = editTextId;
        }
        @Override
        protected String doInBackground(LatLng... params) {
            LatLng point = params[0];
            Geocoder geocoder;
            List<Address> addresses = null;
            String address = "";
            geocoder = new Geocoder(CreateRouteActivity.getAppContext(), Locale.getDefault());

            try
            {
                addresses = geocoder.getFromLocation(point.latitude, point.longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            }
            catch (Exception e)
            {
                Log.e("Geocoder", "Fucked up somehow");
            }
            if(addresses != null)
            {
                address = addresses.get(0).getAddressLine(0);
            }

            return address;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            TextView e = (TextView) findViewById(id);
            e.setText(s);

        }
    }

    public void NullClick(View view)
    {

    }

//    public void CheckOnClick(View view)
//    {
//        EditText editText;
//        CheckBox box = (CheckBox) view;
//
//
//
//        if(box.isChecked())
//        {
//            editText = (EditText) findViewById(R.id.startEditText);
//            editText.setHint("Click On The Map");
//            editText = (EditText) findViewById(R.id.endEditText);
//            editText.setHint("Click On The Map");
//        }
//        else
//        {
//            editText = (EditText) findViewById(R.id.startEditText);
//            editText.setHint("Click On The Map");
//            editText = (EditText) findViewById(R.id.endEditText);
//            editText.setHint("Click On The Map");
//        }
//    }

    private boolean isEmpty(EditText etText) {
        return etText.getText().toString().trim().length() == 0;
    }

    public void AddRouteClick(View view)
    {
        if(startPoint != null)
        {
            if(endPoint != null)
            {
                EditText text = (EditText)findViewById(R.id.RouteNameEdit);
                if(!isEmpty(text))
                {
                    Intent intent = new Intent();
                    intent.putExtra("startlatitude", startPoint.latitude);
                    intent.putExtra("startlongitude", startPoint.longitude);
                    intent.putExtra("endlatitude", endPoint.latitude);
                    intent.putExtra("endlongitude", endPoint.longitude);
                    intent.putExtra("routename", text.getText().toString());
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"There is no name for the route",Toast.LENGTH_LONG).show();
                }
            }
            else
            {
                Toast.makeText(getApplicationContext(),"There is no end point for the route",Toast.LENGTH_LONG).show();
            }
        }
        else
        {
            Toast.makeText(getApplicationContext(),"There is no start point for the route",Toast.LENGTH_LONG).show();
        }

    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        //getMenuInflater().inflate(R.menu.main, menu);
//        return true;
//    }
}
