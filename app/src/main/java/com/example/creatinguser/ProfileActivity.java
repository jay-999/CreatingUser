package com.example.creatinguser;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    CircleImageView circleImageView;
    TextView username, firstname, lastname, status, phone, team;
    Button changeImage, updateProfile;
    private String currentUserID;
    private Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        toolbar = findViewById(R.id.profile_toolbar);
        toolbar.setTitle("My Profile");

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(ProfileActivity.this, HomePage.class);
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(mainIntent);
            }
        });

        firebaseAuth = FirebaseAuth.getInstance();

        initializeViews();
        retrieveProfile();

        changeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent loginIntent = new Intent(ProfileActivity.this, UploadProfileActivity.class);
                loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(loginIntent);
            }
        });


        updateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent loginIntent = new Intent(ProfileActivity.this, SetUpProfileActivity.class);
                loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(loginIntent);
            }
        });
    }

    private  void  initializeViews(){
        circleImageView = findViewById(R.id.profile_image);
        username = findViewById(R.id.profile_username);
        firstname = findViewById(R.id.profile_firstname);
        lastname = findViewById(R.id.profile_lastname);
        changeImage = findViewById(R.id.profile_change_btn);
        updateProfile = findViewById(R.id.profile_update_btn);
        status = findViewById(R.id.profile_status);
        team = findViewById(R.id.profile_team);
        phone = findViewById(R.id.profile_phone);
    }

    private void retrieveProfile(){
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        currentUserID = firebaseAuth.getCurrentUser().getUid().toString();
        final DocumentReference docRef = db.collection("Profile").document(currentUserID);

        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if(value == null || !value.exists()){
                    Intent loginIntent = new Intent(ProfileActivity.this, SetUpProfileActivity.class);
                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(loginIntent);
                    finish();
                }
                else{
                    String firstname_db = value.getData().get("firstname").toString();
                    String lastname_db = value.getData().get("lastname").toString();
                    String phone_db = value.getData().get("phoneNumber").toString();
                    String team_db = value.getData().get("team").toString();
                    if(value.getData().get("imageUrl") != null){
                        String image_db = value.getData().get("imageUrl").toString();
                        Picasso.get().load(image_db).into(circleImageView);
                    }
                    String status_db = value.getData().get("status").toString();
                    String username_db = value.getData().get("username").toString();

                    firstname.setText(firstname_db);
                    lastname.setText(lastname_db);
                    status.setText(status_db);
                    username.setText("@"+username_db);
                    team.setText(team_db);
                    phone.setText(phone_db);
                }
            }
        });
    }
}