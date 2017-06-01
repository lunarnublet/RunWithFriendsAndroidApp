package com.example.tryston.runwithfriends;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Dallin on 6/1/2017.
 */

public class WebHelper {
    private WebHelper() {}

    public static String parseToken(String jsonString) {
        try {
            JSONObject obj = new JSONObject(jsonString);
            return obj.getString("access_token");
        } catch (JSONException e) {
            return null;
        }
    }

    public static String getEncodedPairs(Map<String, String> pairs) {
        StringBuilder sb = new StringBuilder();

        boolean isFirst = true;
        for (Map.Entry<String, String> pair : pairs.entrySet()) {
            if (isFirst)
                isFirst = false;
            else
                sb.append("&");

            try {
                sb.append(URLEncoder.encode(pair.getKey(), "UTF-8"));
                sb.append("=");
                sb.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                // java ...
            }
        }
        return sb.toString();
    }
}
