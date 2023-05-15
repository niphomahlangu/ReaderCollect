package com.example.readercollect;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {

    //variable declarations
    EditText txtEmail, txtPassword;
    Button btnLogin;
    TextView regLink;
    ProgressBar login_progressBar;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //hide action bar
        getSupportActionBar().hide();

        //initialize variables
        txtEmail = findViewById(R.id.txtUserEmail);
        txtPassword = findViewById(R.id.txtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        regLink = findViewById(R.id.regLink);
        login_progressBar = findViewById(R.id.login_progressBar);
        firebaseAuth = FirebaseAuth.getInstance();

        //proceed if user is already signed in
        if(firebaseAuth.getCurrentUser()!=null){
            Toast.makeText(this, "Welcome back.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(Login.this, MainActivity.class));
            finish();
        }

        //login
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = txtEmail.getText().toString().trim();
                String password = txtPassword.getText().toString().trim();

                if(TextUtils.isEmpty(email)){
                    txtEmail.setError("Email is required!");
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    txtPassword.setError("Password is required!");
                    return;
                }
                //validate password length
                if(password.length()<6){
                    txtPassword.setError("Password is too short.");
                    return;
                }

                //show progress bar
                login_progressBar.setVisibility(View.VISIBLE);

                firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(Login.this, "Logged in successfully.", Toast.LENGTH_SHORT).show();
                            //navigate to home page
                            startActivity(new Intent(getApplicationContext(),MainActivity.class));
                            finish();
                        }else {
                            Toast.makeText(Login.this, "Error: "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            //make progress bar disappear
                            login_progressBar.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });

        regLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Login.this, Register.class));
            }
        });
    }
}