package com.example.amore;

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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;


public class BookingsFragment extends Fragment {
    private View view;
    private RecyclerView bookingsList;
    private DatabaseReference databaseReference;
    /*FirebaseAuth customerId;*/
    String customerId, iD, booking_date, equipId, imageName, imageUri;

    public BookingsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_bookings, container, false);
        bookingsList = view.findViewById(R.id.bookingsList);
        bookingsList.setLayoutManager(new LinearLayoutManager(getContext()));

        customerId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        databaseReference = FirebaseDatabase.getInstance().getReference();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions options =
                new FirebaseRecyclerOptions.Builder<Booking>()
                        .setQuery(databaseReference.child("Bookings").child(customerId), Booking.class)
                        .build();

        FirebaseRecyclerAdapter<Booking,BookingViewHolder> adapter
                = new FirebaseRecyclerAdapter<Booking, BookingViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull BookingViewHolder holder, int position, @NonNull Booking model) {
                iD = getRef(position).getKey();
                //Bookings table
                databaseReference.child("Bookings").child(customerId).child(iD).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        booking_date = snapshot.child("Date").getValue().toString();
                        imageUri = snapshot.child("EquipmentImage").getValue().toString();
                        imageName = snapshot.child("EquipmentName").getValue().toString();

                        holder.bookingDate.setText(booking_date);
                        holder.bookingEquipName.setText(imageName);
                        Picasso.get().load(imageUri).into(holder.bookingImage);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @NonNull
            @Override
            public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.booking_item, parent, false);
                BookingViewHolder viewHolder = new BookingViewHolder(v);
                return viewHolder;
            }
        };
        bookingsList.setAdapter(adapter);
        adapter.startListening();
    }

    //view holder for booking_item layout
    public static class BookingViewHolder extends RecyclerView.ViewHolder{
        ImageView bookingImage;
        TextView bookingEquipName, bookingDate;

        public BookingViewHolder(@NonNull View itemView) {
            super(itemView);

            bookingImage = itemView.findViewById(R.id.bookingImage);
            bookingEquipName = itemView.findViewById(R.id.bookingEquipName);
            bookingDate = itemView.findViewById(R.id.bookingDate);
        }
    }
}