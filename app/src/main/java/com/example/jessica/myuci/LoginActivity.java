package com.example.jessica.myuci;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    // Declare Variables
    Button loginBtn;
    Button signupBtn;
    String emailStr;
    String passwordStr;
    EditText input_email;
    EditText input_password;

    private CheckBox saveLoginCheckBox;
    private SharedPreferences loginPreferences;
    private SharedPreferences.Editor loginPrefsEditor;
    private Boolean saveLogin;

    /** Called when the activity is first created. */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("login", "here");

        final Firebase myFirebaseRef = new Firebase("https://radiant-torch-7261.firebaseio.com/");
        Log.d("login", "get myfirebase ref");
        // Get the view from main.xml
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        input_email = (EditText) findViewById(R.id.input_email);
        input_password = (EditText) findViewById(R.id.input_password);

        loginBtn = (Button) findViewById(R.id.btn_login);
        signupBtn = (Button)findViewById(R.id.btn_signup);

        saveLoginCheckBox = (CheckBox)findViewById(R.id.checkBox);
        loginPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        loginPrefsEditor = loginPreferences.edit();
        saveLogin = loginPreferences.getBoolean("saveLogin", false);
        if(loginPreferences.contains("curUser")) {
            Log.d("login", "remember user");
            input_email.setText(loginPreferences.getString("curUser", ""));
        }
        if (saveLogin == true) {
            input_email.setText(loginPreferences.getString("username", ""));
            input_password.setText(loginPreferences.getString("password", ""));
            saveLoginCheckBox.setChecked(true);
        }

        // Login Button Click Listener
        loginBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                // Retrieve the text entered from the EditText
                emailStr = input_email.getText().toString();
                passwordStr = input_password.getText().toString();
                myFirebaseRef.authWithPassword(emailStr, passwordStr, new Firebase.AuthResultHandler() {
                    @Override
                    public void onAuthenticated(AuthData authData) {
                        Log.d("login", "success");
                        Toast.makeText(getApplicationContext(),
                                "login success",
                                Toast.LENGTH_LONG).show();

                        if (saveLoginCheckBox.isChecked()) {
                            loginPrefsEditor.putBoolean("saveLogin", true);
                            loginPrefsEditor.putString("username", emailStr);
                            loginPrefsEditor.putString("password", passwordStr);
                            loginPrefsEditor.putString("curPassword", passwordStr);
                            loginPrefsEditor.commit();
                        } else {
                            loginPrefsEditor.clear();
                            loginPrefsEditor.commit();
                        }
                        loginPrefsEditor.putString("curUser", emailStr);
                        loginPrefsEditor.commit();
                    }

                    @Override
                    public void onAuthenticationError(FirebaseError firebaseError) {
                        // there was an error
                        Log.d("login", "wrong password or no such user, try again");
                        Toast.makeText(getApplicationContext(),
                                "wrong password or user not registered, try again",
                                Toast.LENGTH_LONG).show();
                    }
                });


            }
        });
        // Sign up Button Click Listener
        signupBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {

                // Retrieve the text entered from the EditText
                emailStr = input_email.getText().toString();
                passwordStr = input_password.getText().toString();

                // Force user to fill up the form
                if (emailStr.equals("") && passwordStr.equals("")) {
                    Toast.makeText(getApplicationContext(),
                            "Please complete the sign up form",
                            Toast.LENGTH_LONG).show();

                } else {
                    Log.d("login", "creatUser");
                    Log.d("login", emailStr + " " + passwordStr);
                    myFirebaseRef.createUser(emailStr, passwordStr, new Firebase.ValueResultHandler<Map<String, Object>>() {
                        @Override
                        public void onSuccess(Map<String, Object> result) {
                            Toast.makeText(getApplicationContext(),
                                    "Successfully created user acount, now log in",
                                    Toast.LENGTH_LONG).show();
                            Log.d("login", "Successfully created user account with uid: " + result.get("uid"));
                        }

                        @Override
                        public void onError(FirebaseError firebaseError) {
                            // there was an error
                            Toast.makeText(getApplicationContext(),
                                    "Invalid email, or user already existed",
                                    Toast.LENGTH_LONG).show();
                            Log.d("login", "error");
                        }
                    });
                }
            }
        });

    }

}
