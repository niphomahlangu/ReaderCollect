package com.example.amore;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;


public class HomeFragment extends Fragment {

    private View view;
    private RecyclerView myEquipmentList;
    private DatabaseReference equipmentRef;
    private String empId;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_home, container, false);

        myEquipmentList = view.findViewById(R.id.equipmentList);
        myEquipmentList.setLayoutManager(new LinearLayoutManager(getContext()));

        equipmentRef = FirebaseDatabase.getInstance().getReference().child("Equipment");

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions options =
                new FirebaseRecyclerOptions.Builder<Equipment>()
                        .setQuery(equipmentRef, Equipment.class)
                        .build();

        FirebaseRecyclerAdapter<Equipment, EquipmentViewHolder> adapter
                = new FirebaseRecyclerAdapter<Equipment, EquipmentViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull EquipmentViewHolder holder, int position, @NonNull Equipment model)
            {
                String equipmentId = getRef(position).getKey();
               equipmentRef.child(equipmentId).addValueEventListener(new ValueEventListener() {
                   @Override
                   public void onDataChange(@NonNull DataSnapshot snapshot) {
                       String equipmentImg = snapshot.child("ImageUrl").getValue().toString();
                       String equipmentName = snapshot.child("EquipmentName").getValue().toString();

                       holder.txtEquipmentName.setText(equipmentName);
                       Picasso.get().load(equipmentImg).into(holder.equipmentImage);
                       holder.view.setOnClickListener(new View.OnClickListener() {
                           @Override
                           public void onClick(View view) {
                               Intent intent = new Intent(getActivity(),BookingsActivity.class);
                               intent.putExtra("EquipmentKey",equipmentId);
                               startActivity(intent);
                           }
                       });
                   }

                   @Override
                   public void onCancelled(@NonNull DatabaseError error) {

                   }
               });
            }

            @NonNull
            @Override
            public EquipmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_view, parent, false);
                EquipmentViewHolder viewHolder = new EquipmentViewHolder(v);
                return viewHolder;
            }
        };
        myEquipmentList.setAdapter(adapter);
        adapter.startListening();
    }

    public static class EquipmentViewHolder extends RecyclerView.ViewHolder
    {
        TextView txtEquipmentName;
        ImageView equipmentImage;
        View view;

        public EquipmentViewHolder(@NonNull View itemView) {
            super(itemView);

            txtEquipmentName = itemView.findViewById(R.id.imageTextView);
            equipmentImage = itemView.findViewById(R.id.imageView);
            view = itemView;
        }
    }
}
