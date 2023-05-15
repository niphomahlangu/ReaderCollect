package com.example.readercollect;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

public class Category extends AppCompatActivity {

    //variable declarations
    EditText txtCategoryName, txtMaxNum;
    ProgressBar progressBar;
    Button btnAddCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        //variable initialization
        txtCategoryName = findViewById(R.id.txtCategoryName);
        txtMaxNum = findViewById(R.id.txtMaxNum);
        progressBar = findViewById(R.id.progressBar);
        btnAddCategory = findViewById(R.id.btnAddCategory);

        //add category
        btnAddCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String category = txtCategoryName.getText().toString().trim();
                int maxNum = Integer.parseInt(txtMaxNum.getText().toString().trim());

                //validate input fields
                if(TextUtils.isEmpty(category)){
                    txtCategoryName.setError("FIELD IS EMPTY!");
                    return;
                }

                //insert values into database
            }
        });
    }
}