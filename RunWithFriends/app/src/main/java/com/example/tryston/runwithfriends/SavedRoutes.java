package com.example.tryston.runwithfriends;

import android.content.Context;
import android.os.storage.StorageManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Tryston on 5/19/2017.
 */

public class SavedRoutes {
    private ArrayList<Route> routes;
    private RouteServerCommunicator communicator;
    private String authToken;

    SavedRoutes(RouteServerCommunicator communicator)
    {
        this.communicator = communicator;
        this.routes = new ArrayList<>();
    }

    void init(Context c) {
        authToken = StorageHelper.getToken(c);
        APIResponse response = communicator.getRoutes(authToken);

        switch(response.code) {
            case OK:
                try {
                    JSONObject obj = new JSONObject(response.response);
                    JSONArray routes = obj.getJSONArray("routes");

                    for(int i = 0; i < routes.length(); ++i)
                    {
                        JSONObject routeJSON = routes.getJSONObject(i);

                        Route route = WebHelper.parseJSONRoute(routeJSON);

                        if (route != null) {
                            this.routes.add(route);
                        }
                    }
                } catch (Exception e) {
                    Log.e("ROUTES", "JSON failure");
                }
                break;
            default:
                Log.e("ROUTES", "Failed to get routes");
                break;
        }
    }


    public void Add(Route route)
    {
        if(communicator.add(route, authToken))
        {
            routes.add(route);
        }
    }
    public void Remove(Route route)
    {
        if (communicator.remove(route, authToken))
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
    public int Count()
    {
        return routes.size();
    }

}
