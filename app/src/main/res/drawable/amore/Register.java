package com.example.amore;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Register extends AppCompatActivity {
    public static final String TAG = "TAG";
    EditText txtName, txtSurname, txtNewPassword, txtConfirmPass, txtEmail, txtPhoneNum;
    Button btnRegister;
    TextView loginLink, passStrength;
    FirebaseAuth firebaseAuth;
    DatabaseReference dbReference;
    ProgressBar progressBar;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //hide action bar
        getSupportActionBar().hide();

        txtName = findViewById(R.id.txtName);
        txtSurname = findViewById(R.id.txtSurname);
        txtPhoneNum = findViewById(R.id.txtPhoneNum);
        txtNewPassword = findViewById(R.id.txtNewPassword);
        txtConfirmPass = findViewById(R.id.txtConfirmPass);
        txtEmail = findViewById(R.id.txtEmail);

        loginLink = findViewById(R.id.loginLink);
        btnRegister = findViewById(R.id.btnRegister);
        passStrength = findViewById(R.id.passStrength);
        progressBar = findViewById(R.id.progressBar);
        firebaseAuth = FirebaseAuth.getInstance();

        dbReference = FirebaseDatabase.getInstance().getReference();

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = txtName.getText().toString().trim();
                String surname = txtSurname.getText().toString().trim();
                String phoneNum = txtPhoneNum.getText().toString().trim();
                String newPassword = txtNewPassword.getText().toString().trim();
                String confirmPass = txtConfirmPass.getText().toString().trim();
                String email = txtEmail.getText().toString().trim();

                //validate empty fields
                if(TextUtils.isEmpty(name)){
                    txtName.setError("Name is required!");
                    return;
                }
                if(TextUtils.isEmpty(surname)){
                    txtSurname.setError("Surname is required!");
                    return;
                }
                if(TextUtils.isEmpty(email)){
                    txtEmail.setError("Email is required!");
                    return;
                }
                if(TextUtils.isEmpty(phoneNum)){
                    txtPhoneNum.setError("Phone number is required!");
                    return;
                }
                if(TextUtils.isEmpty(newPassword)){
                    txtNewPassword.setError("Password is required!");
                    return;
                }

                //validate password length
                if(newPassword.length()<6){
                    txtNewPassword.setError("Password is too short.");
                    return;
                }

                //check for password match
                if(!confirmPass.equals(newPassword)){
                    txtConfirmPass.setError("Passwords do not match");
                    return;
                }

                //show progress bar
                progressBar.setVisibility(View.VISIBLE);

                //register user into firebase database
                firebaseAuth.createUserWithEmailAndPassword(email,newPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            //get user id
                            userId = firebaseAuth.getCurrentUser().getUid();
                            //pass user data to user class
                            User user = new User(name,surname,phoneNum);
                            //pass user object to the database
                            dbReference.child("User").child(userId).setValue(user);

                            Toast.makeText(Register.this, "Registration successful.", Toast.LENGTH_SHORT).show();
                            //navigate to home page
                            startActivity(new Intent(getApplicationContext(),MainActivity.class));
                            finish();
                        }
                        else {
                            Toast.makeText(Register.this, "Error: "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            //make progress bar disappear
                            progressBar.setVisibility(View.GONE);
                        }

                    }
                });
            }
        });

        //navigate to Login page
        loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Register.this, Login.class));
            }
        });
    }
}