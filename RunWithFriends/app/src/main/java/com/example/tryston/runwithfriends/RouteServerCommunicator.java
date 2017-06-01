package com.example.tryston.runwithfriends;

/**
 * Created by Tryston on 5/19/2017.
 */

public interface RouteServerCommunicator {
    boolean add(Route route);
    boolean remove(Route route);
    APIResponse getRoutes(String authToken);
}
