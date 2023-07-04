package com.example.readercollect;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
                        Picasso.get().load(bookImage).into(holder.favorite_bookImage);

                        //show options
                        holder.favorite_options.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                showOptionsDialogue(favoriteId);
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
            public FavoriteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.favorite_item, parent, false);
                FavoriteViewHolder viewHolder = new FavoriteViewHolder(view);
                return viewHolder;
            }
        };
        favoritesList.setAdapter(adapter);
        adapter.startListening();
    }

    private void showOptionsDialogue(String favoriteId) {
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.favorite_options_bottomsheet);

        LinearLayout removeFavourite = dialog.findViewById(R.id.layoutRemoveFavorite);

        removeFavourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                favoritesDbRef.child(favoriteId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(getContext(), "Book has been removed.", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(getContext(), "Failed to remove book.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    public static class FavoriteViewHolder extends RecyclerView.ViewHolder
    {
        ImageView favorite_bookImage, favorite_options;
        TextView favorite_bookName;
        public FavoriteViewHolder(@NonNull View itemView) {
            super(itemView);

            favorite_bookImage = itemView.findViewById(R.id.favorite_bookImage);
            favorite_bookName = itemView.findViewById(R.id.favorite_bookName);
            favorite_options = itemView.findViewById(R.id.favorite_options);
        }
    }
}