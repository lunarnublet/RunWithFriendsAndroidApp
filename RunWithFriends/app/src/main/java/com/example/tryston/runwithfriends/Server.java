package com.example.tryston.runwithfriends;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.widget.ImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Tryston on 5/19/2017.
 */

public class Server implements RouteServerCommunicator, CredentialsManager {

    String connectionString;
    DownloadMaterial download;
    public Server(String connectionString)
    {
        this.connectionString = connectionString;
        download = new DownloadMaterial();
    }

    @Override
    public boolean validToken(String token)
    {

        return true;
    }

    @Override
    public APIResponse getToken(String username, String password)
    {
        try
        {
            String endpointStr = connectionString + "token";

            HashMap<String, String> body = new HashMap<>();
            body.put("grant_type", "password");
            body.put("username", username);
            body.put("password", password);

            APIResponse response = new APIConsumer().execute(endpointStr, "POST", "application/x-www-form-url-encoded",
                    "application/json", WebHelper.getEncodedPairs(body)).get();

            Log.e("API", response.code.toString());
            Log.e("API", response.getResponse());

            return response;
        }
        catch (Exception e)
        {
            Log.e("API", e.toString());
            return null;
        }
//        return "valid";
    }

    @Override
    public APIResponse register(String username, String password) {
        try {
            String endpointStr = connectionString + "api/account/register";
            JSONObject body = new JSONObject();
            body.put("email", username);
            body.put("password", password);
            body.put("confirmpassword", password);

            APIResponse response = new APIConsumer().execute(endpointStr, "POST", "application/json",
                    "application/json", body.toString()).get();

            return response;
        } catch (Exception e) {
            Log.e("API", e.toString());
            return null;
        }
    }

    @Override
    public boolean add(Route route, String authToken)
    {
        String endpointStr = connectionString + "api/routes/";
        JSONObject body = new JSONObject();
        try {
            body.put("name", route.getName());
            body.put("origin", WebHelper.latLngToString(route.getStart()));
            body.put("destination", WebHelper.latLngToString(route.getEnd()));
            body.put("distance", route.getDistance());
            body.put("is_loop_route", route.getIsLoopRoute());

            APIResponse response = new APIConsumer().execute(endpointStr, "POST", "application/json",
                    "application/json", body.toString(), "Bearer " + authToken).get();

        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override
    public boolean remove(Route route, String authToken)
    {
        if (route.getId() <= 0)
        {
            return false;
        }

        String endpointStr = connectionString + "api/routes/" + route.getId();
        try {
            APIResponse response = new APIConsumer().execute(endpointStr, "DELETE", null,
                    null, null, "Bearer " + authToken).get();
            int bar = 0;
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override
    public APIResponse getRoutes(String authToken) {
        try {
            String endpointStr = connectionString + "api/routes";
            APIResponse response = new APIConsumer().execute(endpointStr, "GET", null,
                    "application/json", null, "Bearer " + authToken).get();

            Log.e("SERVER", "getRoutes.CODE = " + response.code.toString());

            return response;
        } catch (Exception e) {
            Log.e("API", e.toString());
            return null;
        }
    }

    /*
        0 - URL
        1 - Request Type
        2 - Content-Type
        3 - Accept
        4 - Body
        5 - Authorization
     */
    private class APIConsumer extends AsyncTask<String, Void, APIResponse>
    {
        @Override
        protected APIResponse doInBackground(String... params) {
            HttpURLConnection connection = null;
            StringBuilder result = new StringBuilder(512);
            APIResponse response = new APIResponse();

            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();

                connection.setRequestMethod(params[1]);

                if (params.length > 3) {
                    if (params[2] != null) {
                        connection.setRequestProperty("Content-Type", params[2]);
                    }
                    if (params[3] != null) {
                        connection.setRequestProperty("Accept", params[3]);
                    }

                    if (params.length > 5 && params[5] != null) {
                        connection.setRequestProperty("Authorization", params[5]);
                    }

                    if (params[4] != null) {
                        String postBody = params[4];

                        OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
                        out.write(postBody);
                        out.flush();
                    }
                }

                connection.connect();
                InputStream inputStream = connection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                int in;
                while ((in = inputStreamReader.read()) != -1) {
                    char c = (char) in;
                    result.append(c);
                }
                response.response = result.toString();
                response.code = APIResponse.Code.fromInt(connection.getResponseCode());
                response.message = APIResponse.ALRIGHT;

            } catch (Exception e) {
                Log.e("SERVER", e.toString());
                response.message = APIResponse.FAIL;
                connection.getErrorStream();
                try {
                    response.code = APIResponse.Code.fromInt(connection.getResponseCode());
                } catch (IOException ioe) {
                    response.code = APIResponse.Code.UNKNOWN;
                }
                StringBuilder errResponse = new StringBuilder();
                try {
                    int in;
                    while ((in = connection.getErrorStream().read()) != -1) {
                        errResponse.append((char)in);
                    }
                    response.response = errResponse.toString();
                } catch (IOException ioe) {
                    response.response = "";
                }
            }

            return response;
        }
    }


    public class DownloadMaterial extends AsyncTask<HttpURLConnection, Void, String>
    {
        @Override
        protected String doInBackground(HttpURLConnection... params) {
            StringBuilder result = new StringBuilder();
            HttpURLConnection urlConnection = params[0];
            try {

                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                int in;
                while ((in = inputStreamReader.read()) != -1) {
                    char c = (char) in;
                    result.append(c);
                }

            } catch (Exception e) {
                Log.e("DownloadDocument error ", e.toString());
                String err = "";
                try {
                    int in;
                    while ((in = urlConnection.getErrorStream().read()) != -1) {
                        err += (char)in;
                    }
                    Log.e("err", err);
                } catch (IOException ioe) {

                }
                return "Web search failed.";
            }
            Log.e("result", result.toString());
            return result.toString();
        }
    }
}
