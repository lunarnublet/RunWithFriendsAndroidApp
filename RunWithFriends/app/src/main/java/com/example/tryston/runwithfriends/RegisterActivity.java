package com.example.tryston.runwithfriends;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
    }

    public void OnRegisterClick(View view)
    {
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
                    String token = LoginActivity.GetManager().register(username, password);
                    if(LoginActivity.GetManager().validToken(token))
                    {
                        LoginActivity.writCSV(token);
                        Intent intent = new Intent(this, MainActivity.class);
                        startActivity(intent);
                    }
                    else
                    {
                        Toast.makeText(this, "Username is taken.",Toast.LENGTH_SHORT).show();
                    }
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
