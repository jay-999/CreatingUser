package com.example.creatinguser;

import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.util.Patterns;
import android.widget.TextView;
import android.widget.Toast;
import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.HashMap;

public class LoginPage extends AppCompatActivity {
    public static final String TAG = "UserInformation";

    //Naming widget references such as EditText and Button
    private EditText login_username;
    private EditText login_password;
    private Button login_button;
    private Button signup_button;
    TextView retrieveUserData;

    //A Hashmap reference is created and is currently set to null.
    HashMap<String, String> credentials = null;
    Context context;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_page);
        retrieveUserData = (TextView) findViewById(R.id.userData);
        mAuth = FirebaseAuth.getInstance();

        //getApplicationContext() renders the current context of the Application which can be used in various ways.
        context = getApplicationContext();

        // Mapping the actual widget objects in the activity_registration.xml to the corresponding mentioned references
        login_username = findViewById(R.id.login_uname);
        login_password = findViewById(R.id.login_pass);
        login_button = findViewById(R.id.login_button);
        signup_button = findViewById(R.id.singup_btn);

        //Creating an HashMap object and assigning to the above mentioned 'credentials' hashmap reference.
        credentials = new HashMap<String, String>();

        // Setting an onClick listener to Login button
        login_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast toast;

                // If the 'credentials' hashmap has no data in it and login button is clicked,then a toast message saying 'please sign up' will be displayed.
                if (credentials == null) {
                    toast = Toast.makeText(context, "Please Sign up ", Toast.LENGTH_LONG);
                    toast.show();
                } else {
                    // Retrieving the data associated with the login and password fields
                    String current_username = login_username.getText().toString();
                    String current_password = login_password.getText().toString();
                    mAuth.signInWithEmailAndPassword(current_username, current_password)
                            .addOnCompleteListener(LoginPage.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
//                                         Sign in success, update UI with the signed-in user's information
                                        Log.d(TAG, "signInWithEmail:success");
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        Intent intent = new Intent(getApplicationContext(), HomePage.class);
                                        //intent.putExtra("message", current_username);
                                        startActivity(intent);
//                                        updateUI(user);
                                    }
                                    else {
                                        // If sign in fails, display a message to the user.
                                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                                        Toast.makeText(LoginPage.this, "Authentication failed. Check Email or Password",
                                                Toast.LENGTH_SHORT).show();
//                                        updateUI(null);
                                    }
                                }
                            });

                    //Checking if entered username is present in the hashmap.If Yes then, then further validation is processed. Else a toast message displayed saying the user to sign up
//                    if (credentials.get(current_username) != null) {
//
//                        // Checking if the current user credentials are correct. If yes, then He is redirected to welcome page. Else a toast message is displayed saying password incorrect.
//                        if (current_password.equals(credentials.get(current_username))) {
//                            Intent intent = new Intent(getApplicationContext(), Welcome.class);
//                            intent.putExtra("message", current_username);
//                            startActivity(intent);
//                        } else {
//                            toast = Toast.makeText(context, "Password incorrect " + current_username, Toast.LENGTH_LONG);
//                            toast.show();
//                            login_password.getText().clear();
//                        }
//                    } else {
//                        toast = Toast.makeText(context, "Please Sign Up " + current_username, Toast.LENGTH_LONG);
//                        toast.show();
//                        login_password.getText().clear();
//                    }
                }

            }
        });

// setting an onclick event listener to Signup button. Once the clicks on this button, He is redirected to Registration page
        signup_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Registration.class);
                String current_username = login_username.getText().toString();
                String current_password = login_password.getText().toString();
                intent.putExtra("data", credentials);
                startActivity(intent);
            }
        });
    }
}