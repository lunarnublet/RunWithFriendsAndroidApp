package com.example.tryston.runwithfriends;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by Tryston on 5/30/2017.
 */

public interface RouteReceiver {
    void onRouteFound(ArrayList<LatLng> points, double distance);
}
