package com.example.tryston.runwithfriends;

/**
 * Created by Tryston on 5/19/2017.
 */

public class Server implements RouteServerCommunicator, CredentialsManager {
    @Override
    public boolean validToken(String token) {
        return false;
    }

    @Override
    public String getToken(String username, String password) {
        return null;
    }

    @Override
    public boolean add(Route route) {
        return false;
    }

    @Override
    public boolean remove(Route route) {
        return false;
    }
}
