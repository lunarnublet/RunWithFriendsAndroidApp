package com.example.tryston.runwithfriends;

import android.net.ParseException;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Dallin on 6/1/2017.
 */

public class WebHelper {
    private WebHelper() {}

    public static String parseToken(String jsonString) {
        try {
            JSONObject obj = new JSONObject(jsonString);
            return obj.getString("access_token");
        } catch (JSONException e) {
            return null;
        }
    }

    public static Route parseJSONRoute(JSONObject obj) {
        try {
            int id = obj.getInt("id");
            String name = obj.getString("name");
            String origin = obj.getString("origin");
            String destination = obj.getString("destination");
            double distance = obj.getDouble("distance");

            LatLng originLatLng = latLngFromString(origin);
            LatLng destLatLng = latLngFromString(destination);

            if (originLatLng == null || destLatLng == null)
            {
                return null;
            }

            return new Route(id, originLatLng, destLatLng, distance, name);

        } catch (JSONException e) {
            return null;
        }
    }

    public static LatLng latLngFromString(String latLng) {
        String[] arr = latLng.split(",");

        if (arr.length == 2){
            try {
                double lat = Double.parseDouble(arr[0]);
                double lng = Double.parseDouble(arr[1]);

                return new LatLng(lat, lng);

            } catch (ParseException e) {
                return null;
            }
        }

        return null;
    }

    public static String latLngToString(LatLng latLng) {
        StringBuilder sb = new StringBuilder();

        sb.append(latLng.latitude);
        sb.append(',');
        sb.append(latLng.longitude);

        return sb.toString();
    }

    public static String getEncodedPairs(Map<String, String> pairs) {
        StringBuilder sb = new StringBuilder();

        boolean isFirst = true;
        for (Map.Entry<String, String> pair : pairs.entrySet()) {
            if (isFirst)
                isFirst = false;
            else
                sb.append("&");

            try {
                sb.append(URLEncoder.encode(pair.getKey(), "UTF-8"));
                sb.append("=");
                sb.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                // java ...
            }
        }
        return sb.toString();
    }
}
