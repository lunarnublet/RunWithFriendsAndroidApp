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
    public Route(LatLng start, LatLng end, double distance, String name)
    {
        this.start = start;
        this.end = end;
        this.name = name;
        this.distance = distance;
    }

    public LatLng getEnd()
    {
        return end;
    }

    public LatLng getStart()
    {
        return start;
    }

    public double getDistance()
    {
        return distance;
    }

    public String getName()
    {
        return name;
    }
    public int getId(){ return id; }

    public Route(int id, LatLng start, LatLng end, double distance, String name) {
        this(start, end, distance, name);
        this.id = id;
    }
}
