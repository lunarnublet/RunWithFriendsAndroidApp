package com.example.tryston.runwithfriends;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Dallin on 6/1/2017.
 */

public class StorageHelper {

    public static final String SHARED_PREFERENCES = "com.example.tryston.runwithfriends";
    public static final String TOKEN = SHARED_PREFERENCES + ".token";

    public static SharedPreferences getSharedPrefs(Context context) {
        return context.getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE);
    }

    public static void putToken(Context context, String token) {
        SharedPreferences prefs = getSharedPrefs(context);
        prefs.edit().putString(StorageHelper.TOKEN, token).apply();
    }

    public static String getToken(Context context) {
        SharedPreferences prefs = getSharedPrefs(context);
        return prefs.getString(TOKEN, "");
    }


}
