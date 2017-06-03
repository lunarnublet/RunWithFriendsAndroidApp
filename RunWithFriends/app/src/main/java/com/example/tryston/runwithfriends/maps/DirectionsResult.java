package com.example.tryston.runwithfriends.maps;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

/**
 * Created by Dallin on 6/3/2017.
 */

public class DirectionsResult {
    public final List<LatLng> points;
    public final double distance;

    public DirectionsResult(List<LatLng> points, double distance) {
        this.points = points;
        this.distance = distance;
    }
}
