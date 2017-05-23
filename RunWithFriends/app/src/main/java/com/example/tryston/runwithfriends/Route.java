package com.example.tryston.runwithfriends;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by Tryston on 5/19/2017.
 */

public class Route {
    ArrayList<LatLng> points;
    float distance;
    public Route(ArrayList<LatLng> points)
    {
        this.points = points;
    }

    public LatLng GetEnd()
    {
        if(points.size() > 0)
        {
            return points.get(points.size() - 1);
        }
        else
        {
            return null;
        }
    }

    public LatLng GetStart()
    {
        if(points.size() > 0)
        {
            return points.get(0);
        }
        else
        {
            return null;
        }
    }
}
