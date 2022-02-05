package com.example.creatinguser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    private EditText email, password, repeat_password;
    private Button registerBtn;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initializeViews();

        progressDialog = new ProgressDialog(this);
        firebaseAuth = FirebaseAuth.getInstance();

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateUserInfoAndSignUp();
            }
        });
    }

    private void validateUserInfoAndSignUp() {
        String emailText = email.getText().toString();
        String passwordText = password.getText().toString();
        String repeatPasswordText = repeat_password.getText().toString();

        if(TextUtils.isEmpty(email.getText().toString())){
            email.setError("Please fill in your email");
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()){
            email.setError("Please enter the correct format for an email");
            return;
        }
        if(TextUtils.isEmpty(password.getText().toString())){
            password.setError("Please fill in the password");
            return;
        }
        if(TextUtils.isEmpty(repeat_password.getText().toString())){
            repeat_password.setError("Please fill in the password again");
            return;
        }
        if(!passwordText.equals(repeatPasswordText)){
            repeat_password.setError("Passwords do not match");
            return;
        }
        progressDialog.setTitle("Signing you up");
        progressDialog.setMessage("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        firebaseAuth.createUserWithEmailAndPassword(emailText, passwordText).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    final FirebaseFirestore db = FirebaseFirestore.getInstance();
                    String currentUserID = firebaseAuth.getCurrentUser().getUid().toString();
                    Map<String, Object> map = new HashMap<>();
                    map.put("userID", currentUserID);
                    db.collection("Profile").document(currentUserID)
                            .set(map)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    progressDialog.dismiss();
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
                    progressDialog.dismiss();
                    sendUserToMainActivity();
                }else{
                    progressDialog.dismiss();
                    String message = task.getException().getMessage();
                    Toast toast = Toast.makeText(getApplicationContext(),message, Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        });
    }

    private void sendUserToMainActivity(){
        Intent mainIntent = new Intent(RegisterActivity.this, HomePage.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(mainIntent);
        finish();
    }

    private void initializeViews() {
        email = findViewById(R.id.register_email);
        password = findViewById(R.id.register_password);
        repeat_password = findViewById(R.id.register_password_repeat);
        registerBtn = findViewById(R.id.register_button);
    }

    public void toSignIn(View view) {
        Intent loginIntent = new Intent(RegisterActivity.this, LoginPage.class);
        startActivity(loginIntent);
        finish();
    }
}