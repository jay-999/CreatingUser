package com.example.creatinguser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "UserInformation";
    public static final String EMAIL_KEY = "email";
    public static final String PASSWORD_KEY = "password";
    private FirebaseAnalytics mFirebaseAnalytics;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    EditText usernameEditText;
    EditText passwordEditText;
    TextView retrieveUserData;
    ArrayList<String> sportFansList = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        retrieveUserData = (TextView) findViewById(R.id.userData);
    }

    public void loginAction(View view){
        usernameEditText = (EditText) findViewById(R.id.email);
        passwordEditText = (EditText) findViewById(R.id.password);
        Map<String,Object> userInfo = new HashMap<>();
        userInfo.put(EMAIL_KEY, usernameEditText.getText().toString());
        userInfo.put(PASSWORD_KEY,passwordEditText.getText().toString());

        // Add a new document with a generated ID
        db.collection("users")
                .add(userInfo)
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

    }
    public void retrieveData(View v){
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
                                String email, password;
                                email=document.getString(EMAIL_KEY);
                                password=document.getString(PASSWORD_KEY);
                                sportFansList.add("Username: "+email +"\nPassword: "+password+"\n");
                            }
                            Collections.sort(sportFansList);
                            for(String fan: sportFansList){
                                retrieveUserData.append(fan);
                            }
                        }
                    }
                });

    }
}