package com.example.tryston.runwithfriends;

/**
 * Created by Tryston on 5/19/2017.
 */

public interface CredentialsManager {
    boolean validToken(String token);
    String getToken(String username, String password);
    String register(String username, String password);
}
