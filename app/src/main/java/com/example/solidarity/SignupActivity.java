package com.example.solidarity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class SignupActivity extends AppCompatActivity {

    public static final String TAG = "SignupActivity";
    private EditText etUsernameSignup;
    private EditText etPasswordSignup;
    private EditText etEmailSignup;
    private Button btnSignup;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        etUsernameSignup = findViewById(R.id.etUsernameSignup);
        etPasswordSignup = findViewById(R.id.etPasswordSignup);
        etEmailSignup = findViewById(R.id.etEmailSignup);
        btnSignup = findViewById(R.id.btnSignup);


        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = etUsernameSignup.getText().toString();
                String password = etPasswordSignup.getText().toString();
                String email = etEmailSignup.getText().toString();
                signupUser(username, password, email);
            }
        });



    }


    private void signupUser(String username, String password, String email) {
        Log.i(TAG, "Attempting to sign up user " + username);
        ParseUser user = new ParseUser();
        user.setUsername(username);
        user.setPassword(password);
        if (!email.isEmpty()) {
            user.setEmail(email);
        }
        user.put("radius", 60);

        user.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with Sign Up", e);
                    Toast.makeText(SignupActivity.this, "Issue with Sign Up!", Toast.LENGTH_SHORT).show();
                    return;
                }
                // Sign up didn't succeed. Look at the ParseException
                // to figure out what went wrong
                Toast.makeText(SignupActivity.this, "Success on Sign Up!", Toast.LENGTH_SHORT).show();
            }
        });

        finish();
    }


}