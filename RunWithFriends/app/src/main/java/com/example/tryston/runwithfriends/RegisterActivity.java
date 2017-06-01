package com.example.tryston.runwithfriends;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.MalformedJsonException;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
    }

    public void OnRegisterClick(View view)
    {
        CredentialsManager manager = new Server("http://10.0.2.2:19842/");
        EditText editText = (EditText)findViewById(R.id.UsernameEdit);
        String username = editText.getText().toString().trim();
        editText = (EditText)findViewById(R.id.PasswordEdit);
        String password = editText.getText().toString().trim();
        editText = (EditText)findViewById(R.id.ConfirmPasswordEdit);
        String confirm = editText.getText().toString().trim();
        if(!username.equals(""))
        {
            if(!password.equals(""))
            {
                if(password.equals(confirm))
                {
                    APIResponse response = manager.register(username, password);

                    switch (response.code) {
                        case OK:
                            APIResponse tokenResponse = manager.getToken(username, password);

                            try {
                                JSONObject obj = new JSONObject(tokenResponse.response);
                                StorageHelper.putToken(this, obj.getString("access_token"));

                                Intent intent = new Intent(this, MainActivity.class);
                                startActivity(intent);

                            } catch (Exception e) {
                                Log.e("REGISTER", "Registration failed");
                            }
                            break;
                        case BAD_REQUEST:
                            try {
                                // TODO: error handle
                                JSONObject err = new JSONObject(response.response);
                                Toast.makeText(this, err.getJSONArray("ModelState").get(0).toString(), Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {
                                Toast.makeText(this, "It broke", Toast.LENGTH_SHORT).show();
                            }
                            break;
                        default:
                            Toast.makeText(this, "It broke", Toast.LENGTH_SHORT).show();
                    }

//                    String token = LoginActivity.GetManager().register(username, password);
//                    if(LoginActivity.GetManager().validToken(token))
//                    {
//                        LoginActivity.writCSV(token);
//                        Intent intent = new Intent(this, MainActivity.class);
//                        startActivity(intent);
//                    }
//                    else
//                    {
//                        Toast.makeText(this, "Username is taken.",Toast.LENGTH_SHORT).show();
//                    }
                }
                else
                {
                    Toast.makeText(this, "The passwords do not match.",Toast.LENGTH_SHORT).show();
                }
            }
            else
            {
                Toast.makeText(this, "Enter a desired password.",Toast.LENGTH_SHORT).show();
            }
        }
        else
        {
            Toast.makeText(this, "Enter a desired username.",Toast.LENGTH_SHORT).show();
        }

    }
}
