package com.example.tryston.runwithfriends;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.tryston.runwithfriends.api.APIResponse;
import com.example.tryston.runwithfriends.repository.CredentialsManager;
import com.example.tryston.runwithfriends.repository.StorageHelper;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import com.example.tryston.runwithfriends.repository.Server;

public class LoginActivity extends AppCompatActivity {

    private static CredentialsManager manager;
    String token;
    private static String fileName;
    private static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        context = getApplicationContext();

        manager = new Server("http://10.0.2.2:19842/", this);
        fileName = "tokenfile.csv";

//        StorageHelper.putToken(this, "");
        token = StorageHelper.getToken(this);
//        token = readCSV();
        if (!token.equals("") && manager.validToken(token)) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }

    public void OnRegisterClick(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    public void OnLogInClick(View view) {
        EditText editText = (EditText) findViewById(R.id.UsernameEdit);
        String username = editText.getText().toString().trim();
        editText = (EditText) findViewById(R.id.PasswordEdit);
        String password = editText.getText().toString().trim();

        if (!username.equals("")) {
            if (!password.equals("")) {
                APIResponse response = manager.getToken(username, password);

                if (response.code.equals(APIResponse.Code.BAD_REQUEST)) {
                    Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show();
                } else if (response.code.equals(APIResponse.Code.UNKNOWN)) {
                    Toast.makeText(this, "Uh oh", Toast.LENGTH_SHORT).show();
                } else {

                    try {
                        JSONObject obj = new JSONObject(response.response);
                        StorageHelper.putToken(this, obj.getString("access_token"));

                        Intent intent = new Intent(this, MainActivity.class);
                        startActivity(intent);
                    } catch (Exception e) {
                        Log.e("LOGIN", "It broke");
                        Toast.makeText(this, "Login failed.", Toast.LENGTH_SHORT).show();
                    }
//                    if (manager.validToken(token)) {
//                        writCSV(token);
//                    } else {
//                        Toast.makeText(this, "Wrong username or password.", Toast.LENGTH_SHORT).show();
//                    }
                }
            } else {
                Toast.makeText(this, "Enter your password.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Enter your username.", Toast.LENGTH_SHORT).show();
        }
    }

    public static void writCSV(String token) {
        Context context = LoginActivity.GetContext();
        try {
            File externalStorage = context.getFilesDir();
            Log.e("external storage: ", externalStorage.toString());
            File file = new File(context.getExternalFilesDir(null), fileName);

            FileOutputStream stream = new FileOutputStream(file, false);
            OutputStreamWriter out = new OutputStreamWriter(stream);
            out.write(token);
            out.close();
        } catch (Exception e) {
            Log.e("Writer ", e.toString());
        }
    }

    public static String readCSV() {
        String result = "";
        Context context = LoginActivity.GetContext();

        try {
            File externalStorage = context.getFilesDir();
            Log.e("external storage: ", externalStorage.toString());
            File file = new File(context.getExternalFilesDir(null), fileName);

            FileInputStream stream = new FileInputStream(file);
            InputStreamReader reader = new InputStreamReader(stream);
            int in = reader.read();
            while (in != -1) {
                char c = (char) in;
                result += c;
                in = reader.read();
            }
            Log.e("result", result);
        } catch (Exception e) {
            Log.e("Reader ", e.toString());
        }
        return result;
    }

    private static Context GetContext() {
        return LoginActivity.context;
    }

    public static CredentialsManager GetManager() {
        return LoginActivity.manager;
    }

}
