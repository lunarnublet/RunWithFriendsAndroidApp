package com.example.tryston.runwithfriends.maps;

import android.os.AsyncTask;
import android.util.Log;

import com.example.tryston.runwithfriends.RouteReceiver;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Tryston on 5/30/2017.
 */

public class GoogleMapsAPIHelper {

    private ArrayList<LatLng> points;
    private double distance;
    private RouteReceiver reciever;

    public GoogleMapsAPIHelper(/*RouteReceiver receiver*/)
    {
        this.reciever = reciever;
        points = new ArrayList<>();
        distance = 0.0;
    }
    public DirectionsResult execute(LatLng origin, LatLng dest)
    {
        // Getting URL to the Google Directions API
        String url = getDirectionsUrl(origin, dest);

        DownloadTask downloadTask = new DownloadTask();

        // Start downloading json data from Google Directions API
        try {
            String dlResult = downloadTask.execute(url).get();
            ParserTask parserTask = new ParserTask();

            List<LatLng> points = parserTask.execute(dlResult).get();
            return new DirectionsResult(points, distance);
        } catch (Exception e) {
            return null;
        }
    }
    private String getDirectionsUrl(LatLng origin, LatLng dest){

        // Origin of route
        String str_origin = "origin="+origin.latitude+","+origin.longitude;

        // Destination of route
        String str_dest = "destination="+dest.latitude+","+dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";

        String units = "units=metric";

        // Building the parameters to the web service
        String parameters = str_origin+"&"+str_dest+"&"+sensor + "&mode=walking" + "&" + units;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;

        return url;
    }
    /** A method to download json data from url */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while( ( line = br.readLine()) != null){
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        }catch(Exception e){
            Log.d("Downloading url Exc", e.toString());
        }finally{
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    // Fetches data from url passed
    private class DownloadTask extends AsyncTask<String, Void, String> {

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try{
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            }catch(Exception e){
                Log.d("Background Task",e.toString());
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);
        }
    }

    /** A class to parse the Google Places in JSON format */
    private class ParserTask extends AsyncTask<String, Integer, List<LatLng> >{

        // Parsing the data in non-ui thread
        @Override
        protected List<LatLng> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;
            List<LatLng> points = new ArrayList<LatLng>();

            try{
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                // Starts parsing data
                routes = parser.parse(jObject);
                distance = parser.getDistance();

                for(int i=0; i < routes.size(); i++){

                    // Fetching i-th route
                    List<HashMap<String, String>> path = routes.get(i);

                    // Fetching all the points in i-th route
                    for(int j=0;j<path.size();j++){
                        HashMap<String,String> point = path.get(j);

                        double lat = Double.parseDouble(point.get("lat"));
                        double lng = Double.parseDouble(point.get("lng"));
                        LatLng position = new LatLng(lat, lng);

                        points.add(position);
                    }

                }
            }catch(Exception e){
                e.printStackTrace();
            }
            return points;
        }

        // Executes in UI thread, after the parsing process
//        @Override
//        protected void onPostExecute(List<List<HashMap<String, String>>> result) {

            // Traversing through all the routes
//            for(int i=0;i<result.size();i++){
//                points = new ArrayList<LatLng>();
//
//                // Fetching i-th route
//                List<HashMap<String, String>> path = result.get(i);
//
//                // Fetching all the points in i-th route
//                for(int j=0;j<path.size();j++){
//                    HashMap<String,String> point = path.get(j);
//
//                    double lat = Double.parseDouble(point.get("lat"));
//                    double lng = Double.parseDouble(point.get("lng"));
//                    LatLng position = new LatLng(lat, lng);
//
//                    points.add(position);
//                }
//
//            }

//            RouteFound();
//        }
    }

    private void RouteFound() {
        reciever.onRouteFound(points, distance);
    }
}


