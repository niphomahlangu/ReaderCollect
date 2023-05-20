package com.example.readercollect;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HomeFragment extends Fragment implements View.OnClickListener{

    //Declare variables
    FloatingActionButton btnAddCategory;
    View view;
    RecyclerView categoryList;
    DatabaseReference categoryDbRef;
    FirebaseAuth firebaseAuth;
    String userId;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_home, container, false);

        //initialize variables
        categoryList = view.findViewById(R.id.categoryList);
        btnAddCategory = view.findViewById(R.id.btnAddCategory);
        firebaseAuth = FirebaseAuth.getInstance();
        userId = firebaseAuth.getCurrentUser().getUid();
        categoryDbRef = FirebaseDatabase.getInstance().getReference().child(userId).child("Category");

        categoryList.setLayoutManager(new LinearLayoutManager(getContext()));
        btnAddCategory.setOnClickListener(this);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<Category>().setQuery(categoryDbRef, Category.class).build();
        FirebaseRecyclerAdapter<Category, CategoryViewHolder> adapter = new FirebaseRecyclerAdapter<Category, CategoryViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CategoryViewHolder holder, int position, @NonNull Category model) {
                String categoryId = getRef(position).getKey();
                categoryDbRef.child(categoryId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String categoryName = snapshot.child("CategoryName").getValue().toString();
                        String maxItems = snapshot.child("MaxNum").getValue().toString();
                        String dateCreated = snapshot.child("DateCreated").getValue().toString();

                        holder.categoryName_view.setText(categoryName);
                        holder.maxItems_view.setText(maxItems);
                        holder.categoryDate_view.setText(dateCreated);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @NonNull
            @Override
            public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_view, parent,false);
                CategoryViewHolder viewHolder = new CategoryViewHolder(v);
                return viewHolder;
            }
        };
        categoryList.setAdapter(adapter);
        adapter.startListening();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnAddCategory: startActivity(new Intent(getActivity(), CategoryActivity.class));
            break;
        }
    }

    //my view holder
    public static class CategoryViewHolder extends RecyclerView.ViewHolder
    {
        TextView categoryName_view, maxItems_view, categoryDate_view;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);

            categoryName_view = itemView.findViewById(R.id.categoryName_view);
            maxItems_view = itemView.findViewById(R.id.maxItems_view);
            categoryDate_view = itemView.findViewById(R.id.categoryDate_view);
        }
    }
}