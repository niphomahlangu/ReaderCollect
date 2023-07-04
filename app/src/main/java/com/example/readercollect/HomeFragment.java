package com.example.readercollect;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
    AlertDialog.Builder builder;

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
        builder = new AlertDialog.Builder(getActivity());

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

                        //click on item
                        holder.view.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(getActivity(), MyBooks.class);
                                intent.putExtra("CategoryId", categoryId);
                                startActivity(intent);
                            }
                        });

                        holder.btnCategory_options.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                showOptionsBottomSheet(categoryId);
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
            public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_view, parent,false);
                CategoryViewHolder viewHolder = new CategoryViewHolder(v);
                return viewHolder;
            }
        };
        categoryList.setAdapter(adapter);
        adapter.startListening();
    }

    private void showOptionsBottomSheet(String categoryId) {
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.category_options_bottomsheet);

        LinearLayout option_editCategory = dialog.findViewById(R.id.option_editCategory);
        LinearLayout option_deleteCategory = dialog.findViewById(R.id.option_deleteCategory);

        option_editCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                Intent intent = new Intent(getActivity(),EditCategory.class);
                intent.putExtra("CategoryId", categoryId);
                startActivity(intent);
            }
        });

        option_deleteCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                //dialog box
                builder.setIcon(R.drawable.book);
                builder.setTitle("Delete Category");
                builder.setMessage("Warning: All items in this category will be deleted.");
                builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        categoryDbRef.child(categoryId).setValue(null).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(getActivity(), "Category deleted successfully.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

                builder.show();
            }
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
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
        ImageView btnCategory_options;
        View view;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);

            categoryName_view = itemView.findViewById(R.id.categoryName_view);
            maxItems_view = itemView.findViewById(R.id.maxItems_view);
            categoryDate_view = itemView.findViewById(R.id.categoryDate_view);
            btnCategory_options = itemView.findViewById(R.id.btnCategory_options);
            view = itemView;
        }
    }
}