package com.example.creatinguser;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class Welcome extends AppCompatActivity {

    //Naming widget references such as TextView
    private TextView welcome;
    private Button logout_button;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        //Mapping the actual activity_welcome.xml widget objects to the above mentioned references.
        welcome=findViewById(R.id.welcomeView);
        logout_button = findViewById(R.id.logout_button);


        // Receiving the intent object from MainActivity and Retrieving the String associated with that intent.
        String str= getIntent().getStringExtra("message");
        String currentText=welcome.getText().toString();

        //Displaying welcome user text
        welcome.setText(currentText+" "+str+" !");


        logout_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                FirebaseAuth.getInstance().getCurrentUser();
                FirebaseAuth.getInstance().signOut();
                Toast toast = Toast.makeText(Welcome.this, "Signout Complete", Toast.LENGTH_LONG);
                toast.show();
                Intent intent = new Intent(getApplicationContext(), LoginPage.class);
                startActivity(intent);
            }
        });


    }
}