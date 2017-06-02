package com.example.tryston.runwithfriends;

/**
 * Created by Tryston on 5/19/2017.
 */

public interface RouteServerCommunicator {
    boolean add(Route route, String authToken);
    boolean remove(Route route, String authToken);
    APIResponse getRoutes(String authToken);
}
