package com.example.tryston.runwithfriends;

import java.util.ArrayList;

/**
 * Created by Tryston on 5/19/2017.
 */

public class SavedRoutes {
    private ArrayList<Route> routes;
    private RouteServerCommunicator communicator;
    SavedRoutes(RouteServerCommunicator communicator)
    {
        this.communicator = communicator;
        this.routes = new ArrayList<>();
    }
    public void Add(Route route)
    {
        if(communicator.add(route))
        {
            routes.add(route);
        }
    }
    public void Remove(Route route)
    {
        if (communicator.remove(route))
        {
            routes.remove(route);
        }
    }
    public Route Get(int i)
    {
        if(i >= 0 && i < routes.size())
        {
            return routes.get(i);
        }
        else
        {
            throw new IndexOutOfBoundsException("Out of bounds of the routes arraylist in SavedRoutes");
        }
    }

}
