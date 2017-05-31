package com.example.tryston.runwithfriends;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.widget.ImageView;

import org.json.JSONArray;
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
    public String getToken(String username, String password)
    {
        try
        {
            URL url = new URL(connectionString + "token/");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);

            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            urlConnection.setRequestProperty("Accept", "application/json");

            String query = "grant_type=password&username=" + URLEncoder.encode("a@a.com", "UTF-8") + "&password=foobar";
            Log.e("query", query);

            OutputStreamWriter out = new OutputStreamWriter(urlConnection.getOutputStream());
            out.write(query);
            String s = download.execute(urlConnection).get();
            Log.e("from server got back: ", s);

        }
        catch (Exception e)
        {
            Log.e("Some exception", e.toString());
        }
        return "valid";
    }

    @Override
    public String register(String username, String password) {
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
