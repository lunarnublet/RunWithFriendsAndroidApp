package com.example.tryston.runwithfriends.maps;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Dallin on 6/3/2017.
 */

public class LatLngEx {
    private LatLngEx() {}

    public static boolean isInRange(LatLng point, LatLng comparer, double latEpsilon, double lngEpsilon) {
        boolean isInLatRange = equals(point.latitude, comparer.latitude, latEpsilon);
        boolean isInLngRange = equals(point.longitude, comparer.longitude, lngEpsilon);

        return isInLatRange && isInLngRange;
    }

    private static boolean equals(double a, double b, double epsilon){
        return a == b || Math.abs(a - b) < epsilon;
    }
}
