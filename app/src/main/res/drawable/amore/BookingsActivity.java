package com.example.amore;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.HashMap;

public class BookingsActivity extends AppCompatActivity {

    ImageView itemImageView;
    EditText txtAddress;
    TextView itemTextView;
    Spinner mySpinner;
    Button btnBook, dateButton;
    DatePickerDialog datePickerDialog;

    DatabaseReference reference;
    String EquipmentKey, date;
    ProgressBar progressBar;
    String equipmentName, imageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookings);

        itemImageView = findViewById(R.id.itemImageView);
        itemTextView = findViewById(R.id.itemTextView);
        date = getTodaysDate();
        btnBook = findViewById(R.id.btnBook);
        txtAddress = findViewById(R.id.txtAddress);
        dateButton = findViewById(R.id.datePickerButton);
        mySpinner = findViewById(R.id.spinner);
        progressBar = findViewById(R.id.progressBar3);

        iniDatePicker();
        dateButton.setText(getTodaysDate());

        reference = FirebaseDatabase.getInstance().getReference().child("Equipment");

        EquipmentKey = getIntent().getStringExtra("EquipmentKey");
        reference.child(EquipmentKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    equipmentName = snapshot.child("EquipmentName").getValue().toString();
                    imageUrl = snapshot.child("ImageUrl").getValue().toString();
                    Picasso.get().load(imageUrl).into(itemImageView);
                    itemTextView.setText(equipmentName);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //populate transport request spinner
        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(BookingsActivity.this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.spinnerItems));
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mySpinner.setAdapter(myAdapter);

        //make equipment booking
        btnBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //show progress bar
                progressBar.setVisibility(View.VISIBLE);
                bookEquipment();
            }
        });
    }

    private void bookEquipment() {
        DatabaseReference bookingsDbReference = FirebaseDatabase.getInstance().getReference().child("Bookings");
        String bookingId = bookingsDbReference.push().getKey();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String eventAddress = txtAddress.getText().toString().trim();
        String transport = mySpinner.getSelectedItem().toString();

        //check if txtAddress is empty
        if(TextUtils.isEmpty(eventAddress)){
            txtAddress.setError("Address required!");
            //hide progressBar
            progressBar.setVisibility(View.GONE);
            return;
        }

        //fields for the bookings table
        HashMap hashMap1 = new HashMap();
        //hashMap1.put("CustomerId",userId);
        hashMap1.put("Date",date);
        hashMap1.put("EventAddress",eventAddress);
        hashMap1.put("Transport",transport);
        hashMap1.put("EquipmentId",EquipmentKey);
        hashMap1.put("EquipmentImage",imageUrl);
        hashMap1.put("EquipmentName",equipmentName);

        try {
            //insert data into Bookings table
            bookingsDbReference.child(userId).child(bookingId).setValue(hashMap1).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {

                    //pass data to main activity
                    Intent intent = new Intent(BookingsActivity.this,MainActivity.class);
                    startActivity(intent);
                    Toast.makeText(BookingsActivity.this, "Equipment booking successful.", Toast.LENGTH_SHORT).show();
                    //hide progressBar
                    progressBar.setVisibility(View.GONE);
                }
            });
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private String getTodaysDate() {
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        month = month+1;
        int year = calendar.get(Calendar.YEAR);
        return makeDateString(day,month,year);
    }

    private void iniDatePicker() {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int day, int month, int year) {
                month = month+1;
                date = makeDateString(day, month, year);
                dateButton.setText(date);
            }
        };
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);

        int style = AlertDialog.THEME_HOLO_LIGHT;

        datePickerDialog = new DatePickerDialog(this, style, dateSetListener, day, month, year);
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
    }

    private String makeDateString(int day, int month, int year) {
        return day + " " + getMonthFormat(month)  + " " + year;
    }

    private String getMonthFormat(int month) {
        if(month ==1)
            return "JAN";
        if(month ==2)
            return "FEB";
        if(month ==3)
            return "MAR";
        if(month ==4)
            return "APR";
        if(month ==5)
            return "MAY";
        if(month ==6)
            return "JUN";
        if(month ==7)
            return "JUL";
        if(month ==8)
            return "AUG";
        if(month ==9)
            return "SEP";
        if(month ==10)
            return "OCT";
        if(month ==11)
            return "NOV";
        if(month ==12)
            return "DEC";

        //Default month
        return "JAN";
    }

    //onClick listener for datePickerButton
    public void openDatePicker(View view) {
        datePickerDialog.show();
    }

}