package com.example.tryston.runwithfriends;

/**
 * Created by Tryston on 5/19/2017.
 */

public interface CredentialsManager {
    boolean validToken(String token);
    APIResponse getToken(String username, String password);
    APIResponse register(String username, String password);
}
