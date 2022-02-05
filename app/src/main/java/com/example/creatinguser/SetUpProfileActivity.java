package com.example.creatinguser;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.hbb20.CountryCodePicker;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class SetUpProfileActivity extends AppCompatActivity {


    private EditText first_name, last_name, username, status, phone, team;
    CountryCodePicker countryCodePicker;
    private Button complete_profile;
    private ProgressDialog progressDialog;
    private String currentUserID;
    private Toolbar toolbar;
    String fnumber;
    boolean checker= false;


    private FirebaseAuth firebaseAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_profile);
        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(SetUpProfileActivity.this);
        currentUserID = firebaseAuth.getCurrentUser().getUid().toString();

        if(checker){
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent mainIntent = new Intent(SetUpProfileActivity.this, ProfileActivity.class);
                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(mainIntent);
                }
            });
        }

        toolbar = findViewById(R.id.update_profile_toolbar);
        toolbar.setTitle("Update Your Profile");
        initializeViews();

        retrieveUserProfile();
        complete_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateProfile();
            }
        });


    }



    private void updateProfile(){
        final String fnameText = first_name.getText().toString();
        final String lnameText = last_name.getText().toString();
        final String usernameText = username.getText().toString();
        final String status_text = status.getText().toString();
        final String team_text = team.getText().toString();
        final String phone_text = phone.getText().toString();


        if(TextUtils.isEmpty(username.getText().toString())){
            username.setError("PLease fill in your location");
            return;
        }
        if(TextUtils.isEmpty(first_name.getText().toString())){
            first_name.setError("Please fill in the firstname");
            return;
        }
        if(TextUtils.isEmpty(last_name.getText().toString())){
            last_name.setError("Please fill in the lastname");
            return;
        }

        if(TextUtils.isEmpty(status.getText().toString())){
            status.setError("Please fill in your status");
            return;
        }
        if(TextUtils.isEmpty(team.getText().toString())){
            team.setError("Please fill in your favourite team");
            return;
        }
        if(TextUtils.isEmpty(phone.getText().toString())){
            phone.setError("Please fill in your phone number");
            return;
        }

        final String formattedNumber = formatPhoneNumber(phone_text);
        progressDialog.setTitle("Setting your profile");
        progressDialog.setMessage("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> map = new HashMap<>();
        map.put("firstname", fnameText);
        map.put("lastname",lnameText);
        map.put("status", status_text);
        map.put("username", usernameText);
        map.put("phoneNumber", formattedNumber);
        map.put("team", team_text);


        db.collection("Profile").document(currentUserID)
                .update(map)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressDialog.dismiss();
                        Toast toast = Toast.makeText(getApplicationContext(), "Profile Updated successfully", Toast.LENGTH_LONG);
                        toast.show();
                        Intent mainIntent = new Intent(getApplicationContext(), ProfileActivity.class);
                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(mainIntent);
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                String message = e.getMessage();
                Toast toast = Toast.makeText(getApplicationContext(),"Error: "+ message, Toast.LENGTH_LONG);
                toast.show();
                progressDialog.dismiss();
            }
        });
    }


    private String formatPhoneNumber(String number) {
        fnumber = number.replaceFirst("^0+(?!$)", "");
        fnumber = "+" + countryCodePicker.getSelectedCountryCode() + fnumber;
        return fnumber;
    }

    private void initializeViews() {
        last_name = findViewById(R.id.update_lastname);
        first_name= findViewById(R.id.update_firstname);
        status = findViewById(R.id.update_status);
        username = findViewById(R.id.update_username);
        complete_profile = findViewById(R.id.update_profile_button);
        team = findViewById(R.id.update_team);
        phone = findViewById(R.id.update_phone);
        countryCodePicker = findViewById(R.id.update_country_code);
    }

    private void retrieveUserProfile(){
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        currentUserID = firebaseAuth.getCurrentUser().getUid().toString();
        final DocumentReference docRef = db.collection("Profile").document(currentUserID);

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot documentSnapshot = task.getResult();
                if(documentSnapshot.getData().get("firstname") != null){
                    checker = true;
                    String firstname_db = documentSnapshot.getData().get("firstname").toString();
                    String lastname_db = documentSnapshot.getData().get("lastname").toString();
                    String status_db = documentSnapshot.getData().get("status").toString();
                    String username_db = documentSnapshot.getData().get("username").toString();
                    String phone_db = documentSnapshot.getData().get("phoneNumber").toString();
                    String team_db = documentSnapshot.getData().get("team").toString();
                    PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();

                    try {
                        Phonenumber.PhoneNumber numberProto = phoneUtil.parse(phone_db, "");

                        System.out.println("Country code: " + numberProto.getCountryCode());
                        phone_db = String.valueOf(numberProto.getNationalNumber());
                    } catch (NumberParseException e) {
                        System.err.println("NumberParseException was thrown: " + e.toString());
                    }

                    phone.setText(phone_db);
                    team.setText(team_db);
                    first_name.setText(firstname_db);
                    last_name.setText(lastname_db);
                    status.setText(status_db);
                    username.setText(username_db);
                }
            }

        });
    }
}