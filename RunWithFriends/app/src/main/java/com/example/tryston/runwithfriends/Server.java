package com.example.tryston.runwithfriends;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Tryston on 5/19/2017.
 */

public class Server implements RouteServerCommunicator, CredentialsManager {

    String connectionString;
    public Server(String connectionString)
    {
        this.connectionString = connectionString;
    }

    @Override
    public boolean validToken(String token)
    {
        return true;
    }

    @Override
    public String getToken(String username, String password)
    {
        return "valid";
    }

    @Override
    public boolean add(Route route)
    {
        return true;
    }

    @Override
    public boolean remove(Route route)
    {
        return true;
    }
}
