package com.example.tryston.runwithfriends;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by Tryston on 5/19/2017.
 */

public class Route {
    private LatLng start;
    private LatLng end;
    private float distance;
    private String name;
    public Route(LatLng start, LatLng end, float distance, String name)
    {
        this.start = start;
        this.end = end;
        this.name = name;
    }

    public LatLng GetEnd()
    {
        return end;
    }

    public LatLng GetStart()
    {
        return start;
    }

    public float getDistance()
    {
        return distance;
    }

    public String getName()
    {
        return name;
    }
}
