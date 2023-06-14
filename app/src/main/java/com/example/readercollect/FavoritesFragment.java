package com.example.readercollect;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;


public class FavoritesFragment extends Fragment {

    View view;
    RecyclerView favoritesList;
    DatabaseReference favoritesDbRef;
    FirebaseAuth firebaseAuth;
    String userId;
    public FavoritesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_favorites, container, false);
        favoritesList = view.findViewById(R.id.favoritesList);
        firebaseAuth = FirebaseAuth.getInstance();
        userId = firebaseAuth.getCurrentUser().getUid();
        favoritesDbRef = FirebaseDatabase.getInstance().getReference().child(userId).child("Favorites");

        favoritesList.setLayoutManager(new LinearLayoutManager(getContext()));
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<Favorite>().setQuery(favoritesDbRef, Favorite.class).build();

        FirebaseRecyclerAdapter<Favorite, FavoriteViewHolder> adapter = new FirebaseRecyclerAdapter<Favorite, FavoriteViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull FavoriteViewHolder holder, int position, @NonNull Favorite model) {
                String favoriteId = getRef(position).getKey();
                favoritesDbRef.child(favoriteId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String bookName = snapshot.child("BookName").getValue().toString();
                        String bookImage = snapshot.child("ImageUri").getValue().toString();

                        holder.favorite_bookName.setText(bookName);
                        Picasso.get().load(model.getImageUri()).into(holder.favorite_bookImage);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @NonNull
            @Override
            public FavoriteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.favorite_item, parent, false);
                FavoriteViewHolder viewHolder = new FavoriteViewHolder(view);
                return viewHolder;
            }
        };
        favoritesList.setAdapter(adapter);
        adapter.startListening();
    }

    public static class FavoriteViewHolder extends RecyclerView.ViewHolder
    {
        ImageView favorite_bookImage;
        TextView favorite_bookName;
        public FavoriteViewHolder(@NonNull View itemView) {
            super(itemView);

            favorite_bookImage = itemView.findViewById(R.id.favorite_bookImage);
            favorite_bookName = itemView.findViewById(R.id.favorite_bookName);
        }
    }
}