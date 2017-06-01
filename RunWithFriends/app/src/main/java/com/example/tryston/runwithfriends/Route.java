package com.example.tryston.runwithfriends;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by Tryston on 5/19/2017.
 */

public class Route {
    private LatLng start;
    private LatLng end;
    private double distance;
    private String name;
    private int id;
    public Route(LatLng start, LatLng end, float distance, String name)
    {
        this.start = start;
        this.end = end;
        this.name = name;
        this.distance = distance;
    }

    public LatLng GetEnd()
    {
        return end;
    }

    public LatLng GetStart()
    {
        return start;
    }

    public double GetDistance()
    {
        return distance;
    }

    public String GetName()
    {
        return name;
    }
    public int GetId(){ return id; }
}
