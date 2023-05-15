package com.example.amore;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class TestActivity extends AppCompatActivity {

    ImageView item_image;
    TextView item_name;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        item_image = findViewById(R.id.item_image);
        item_name = findViewById(R.id.item_name);
        reference = FirebaseDatabase.getInstance().getReference().child("Equipment");

        String EquipmentKey = getIntent().getStringExtra("EquipmentKey");
        reference.child(EquipmentKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String equipmentName = snapshot.child("EquipmentName").getValue().toString();
                    String imageUrl = snapshot.child("ImageUrl").getValue().toString();
                    Picasso.get().load(imageUrl).into(item_image);
                    item_name.setText(equipmentName);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}