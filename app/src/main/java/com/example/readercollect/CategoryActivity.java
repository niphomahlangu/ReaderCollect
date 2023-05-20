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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class CategoryActivity extends AppCompatActivity {

    //variable declarations
    EditText txtCategoryName, txtMaxNum;
    ProgressBar progressBar;
    Button btnCreateCategory;
    DatabaseReference dbReference;
    FirebaseAuth firebaseAuth;
    String userId;
    String currentDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        //variable initialization
        txtCategoryName = findViewById(R.id.txtCategoryName);
        txtMaxNum = findViewById(R.id.txtMaxNum);
        progressBar = findViewById(R.id.progressBar);
        btnCreateCategory = findViewById(R.id.btnCreateCategory);
        firebaseAuth = FirebaseAuth.getInstance();
        dbReference = FirebaseDatabase.getInstance().getReference();
        userId = firebaseAuth.getCurrentUser().getUid();
        currentDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());

        //create category
        btnCreateCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String category = txtCategoryName.getText().toString().trim();
                int maxNum = Integer.parseInt(txtMaxNum.getText().toString().trim());

                //validate input fields
                if(TextUtils.isEmpty(category)){
                    txtCategoryName.setError("FIELD IS EMPTY!");
                    return;
                }

                if(maxNum < 1){
                    txtMaxNum.setError("VALUE MUST MORE THAN ZERO!");
                    return;
                }

                //set progress bar
                progressBar.setVisibility(View.VISIBLE);

                //insert values into database
                HashMap hashMap = new HashMap();
                hashMap.put("CategoryName", category);
                hashMap.put("MaxNum", maxNum);
                hashMap.put("DateCreated", currentDate);
                //get key
                String key = dbReference.push().getKey();

                //insert values into database
                dbReference.child(userId).child("Category").child(key).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(CategoryActivity.this, "Category added successfully.", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(CategoryActivity.this, MainActivity.class));
                            finish();
                        }else {
                            Toast.makeText(CategoryActivity.this, "Error: "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                progressBar.setVisibility(View.GONE);
            }
        });
    }
}