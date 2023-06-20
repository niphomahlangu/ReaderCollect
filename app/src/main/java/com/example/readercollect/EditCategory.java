package com.example.readercollect;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class EditCategory extends AppCompatActivity {

    //variable declarations
    String categoryId, userId;
    EditText edit_categoryName, edit_categoryLimit;
    Button btnUpdateCategory;
    ProgressBar progressBar_editCategory;
    DatabaseReference dbCategoryRef;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_category);

        //variable initialization
        categoryId = getIntent().getStringExtra("CategoryId");
        edit_categoryName = findViewById(R.id.edit_categoryName);
        edit_categoryLimit = findViewById(R.id.edit_categoryLimit);
        btnUpdateCategory = findViewById(R.id.btnUpdateCategory);
        progressBar_editCategory = findViewById(R.id.progressBar_editCategory);
        firebaseAuth = FirebaseAuth.getInstance();
        userId = firebaseAuth.getCurrentUser().getUid();
        dbCategoryRef = FirebaseDatabase.getInstance().getReference().child(userId).child("Category").child(categoryId);

        dbCategoryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String categoryName = snapshot.child("CategoryName").getValue().toString();
                String maxItems = snapshot.child("MaxNum").getValue().toString();

                edit_categoryName.setText(categoryName);
                edit_categoryLimit.setText(maxItems);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        btnUpdateCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String categoryName = edit_categoryName.getText().toString();
                int maxNum=Integer.parseInt(edit_categoryLimit.getText().toString());
                HashMap hashMap = new HashMap();

                hashMap.put("CategoryName", categoryName);
                hashMap.put("MaxNum", maxNum);

                dbCategoryRef.updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful()){
                            Toast.makeText(EditCategory.this, "Updated successfully.", Toast.LENGTH_SHORT).show();
                            finish();
                        }else{
                            Toast.makeText(EditCategory.this, "Update Failed!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
}