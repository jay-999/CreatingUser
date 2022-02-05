package com.example.creatinguser;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.util.Patterns;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class Registration extends AppCompatActivity implements View.OnClickListener{

    public static final String TAG = "UserInformation";
    public static final String USERNAME_KEY = "username";
    public static final String PASSWORD_KEY = "password";
    public static final String EMAIL_KEY =  "email";
    //Naming widget references such as EditText and Button
    private EditText register_username;
    private EditText register_password;
    private EditText register_email;
    private EditText register_retypepassword;
    private Button register_signup_button;
    TextView retrieveUserData;
    private FirebaseAnalytics mFirebaseAnalytics;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    Context context;

    private AwesomeValidation awesomeValidation;
    private FirebaseAuth mAuth;

    //A Hashmap reference is created and is set as null currently
    HashMap<String,String> newMember=null;
    ArrayList<String> sportFansList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        retrieveUserData = (TextView) findViewById(R.id.userData);
        mAuth = FirebaseAuth.getInstance();

        //getApplicationContext() renders the current context of the Application which can be used in various ways.
        context=getApplicationContext();

        awesomeValidation=new AwesomeValidation(ValidationStyle.BASIC);

        // Mapping the actual widget objects in the activity_registration.xml to the corresponding mentioned references
        register_username=findViewById(R.id.register_uname);
        register_password=findViewById(R.id.register_password);
        register_email=findViewById(R.id.register_email);
        register_retypepassword=findViewById(R.id.register_retype_password);
        register_signup_button=findViewById(R.id.signup_button);

        //Adding validation to the Registration fields like username , password , email and phone
        awesomeValidation.addValidation(this, R.id.register_uname, "^[A-Za-z\\s]{1,}[\\.]{0,1}[A-Za-z\\s]{0,}$", R.string.username_error);
        awesomeValidation.addValidation(this, R.id.register_email, Patterns.EMAIL_ADDRESS, R.string.email_error);

        // Regular Expression to put a constraint on the password entered.This Regular Expression states that the password entered should
        // A digit must appear at least once, An lowercase letter,uppercase letter and a special character should appear at least once.
        String regexPassword="^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&+=])(?=\\S+$).{4,}$";
        awesomeValidation.addValidation(this, R.id.register_password, regexPassword, R.string.password_error);

        // to validate a confirmation field (don't validate any rule other than confirmation on confirmation field)
        awesomeValidation.addValidation(this, R.id.register_retype_password, R.id.register_password, R.string.password_mismatch);
        register_signup_button.setOnClickListener(this);

        //Preserving the Hashmap that we get from the login page irrespective of contents in the hashmap.If the hashmap is empty we can still add a new user to this hashmap
        newMember=(HashMap<String,String>)getIntent().getSerializableExtra("data");



    }
    // This method returns true if the Entered user already exists in the hashmap and false otherwise
    private boolean isUserAlreadyExists(String user){
        if(newMember.containsKey(user)){
            return true;
        }
        return false;
    }
    public void retrieveData(View v) {
        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            retrieveUserData.setText("");
                            sportFansList.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                String username, password;
                                username = document.getString(USERNAME_KEY);
                                password = document.getString(PASSWORD_KEY);
                                sportFansList.add("Username: " + username + "\nPassword: " + password + "\n");
                            }
                            Collections.sort(sportFansList);
                            for (String fan : sportFansList) {
                                retrieveUserData.append(fan);
                            }
                        }
                    }
                });
    }

    private void submitForm() {
        //first validate the form then move ahead
        //if this becomes true that means validation is successful
        if (awesomeValidation.validate() ) {

            //Check for existing user with the given username and move further
            if(isUserAlreadyExists(register_username.getText().toString()))
                Toast.makeText(getApplicationContext(),"User Already Exists !",Toast.LENGTH_LONG).show();
            else {

                Toast.makeText(this, "Registration Successful", Toast.LENGTH_LONG).show();

                //If Registration is successful then put the entered username and password in the hashmap and send back the hashmap reference to the MainActivity using intent object.
                String current_username = register_username.getText().toString();
                String current_password = register_password.getText().toString();
                String current_email= register_email.getText().toString();
//                newMember.put(current_username, current_password);
                newMember.put(USERNAME_KEY, current_username);
                newMember.put(PASSWORD_KEY,current_password);
                newMember.put(EMAIL_KEY,current_email);
                mAuth.createUserWithEmailAndPassword(current_email,current_password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
//                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(Registration.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
//                            updateUI(null);
                        }
                    }
                });

                Intent intent = new Intent(getApplicationContext(), LoginPage.class);
                intent.putExtra("data", newMember);
                startActivity(intent);
            }

            // Add a new document with a generated ID
            db.collection("users")
                    .add(newMember)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error adding document", e);
                        }
                    });

            //process the data further
        }
    }

    @Override
    public void onClick(View view) {
        if (view == register_signup_button) {
            submitForm();
        }
    }
}