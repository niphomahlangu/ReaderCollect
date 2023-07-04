package com.example.readercollect;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class MyBooks extends AppCompatActivity {

    RecyclerView bookList;
    String CategoryId, currentUser;
    DatabaseReference booksDbRef, dbReference;
    FirebaseAuth firebaseAuth;
    FirebaseRecyclerOptions<Book> options;
    FirebaseRecyclerAdapter<Book, MyViewHolder> adapter;
    int readBooksCount, totalBookCount, unreadBooksCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_books);

        bookList = findViewById(R.id.bookList);
        CategoryId = getIntent().getStringExtra("CategoryId");
        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser().getUid();
        dbReference = FirebaseDatabase.getInstance().getReference().child(currentUser);
        booksDbRef = dbReference.child("Category").child(CategoryId).child("Books");
        readBooksCount = 0;
        unreadBooksCount = 0;

        bookList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        bookList.setHasFixedSize(true);

        loadBooks();
    }

    //loads books from from a particular category
    private void loadBooks() {
        options = new FirebaseRecyclerOptions.Builder<Book>().setQuery(booksDbRef, Book.class).build();
        adapter = new FirebaseRecyclerAdapter<Book, MyViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@androidx.annotation.NonNull MyViewHolder holder, int position, @androidx.annotation.NonNull Book model) {
                String bookId = getRef(position).getKey();
                holder.bookName_itemView.setText(model.getBookName());
                Picasso.get().load(model.getImageUri()).into(holder.bookImage_itemView);
                holder.bookDate_itemView.setText(model.getDate());

                if(model.getStatus().equals("read")){
                    //mark as read symbol
                    holder.mark.setVisibility(View.VISIBLE);
                    //count the number of read books
                    readBooksCount = bookList.getAdapter().getItemCount();
                }

                //total book count
                totalBookCount = bookList.getAdapter().getItemCount();
                //count the number of unread books
                unreadBooksCount = totalBookCount - readBooksCount;

                //percentage of read books

                //options button
                holder.btn_books_options.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showDialog(bookId, model.getBookName(), model.getImageUri(), model.getDate());
                    }
                });
            }

            @androidx.annotation.NonNull
            @Override
            public MyViewHolder onCreateViewHolder(@androidx.annotation.NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.book_item, parent, false);
                return new MyViewHolder(view);
            }
        };
        adapter.startListening();
        bookList.setAdapter(adapter);
    }

    private void showDialog(String bookId, String bookName, String imageUri, String date) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.options_bottomsheet_layout);

        LinearLayout addToFavLayout = dialog.findViewById(R.id.layoutAddToFavorites);
        LinearLayout editBookLayout = dialog.findViewById(R.id.layoutEdit);
        LinearLayout deleteBookLayout = dialog.findViewById(R.id.layoutDelete);
        LinearLayout markAsRead = dialog.findViewById(R.id.layoutMarkAsRead);

        addToFavLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();

                HashMap hashMap = new HashMap();
                hashMap.put("BookName",bookName);
                hashMap.put("ImageUri",imageUri);

                String favoriteId = dbReference.push().getKey();
                dbReference.child("Favorites").child(favoriteId).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@androidx.annotation.NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(MyBooks.this, "Added to favorites.", Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(MyBooks.this, "Failed to add to favorites.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        markAsRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //close dialog
                dialog.dismiss();

                HashMap hashMap = new HashMap();
                hashMap.put("BookName",bookName);
                hashMap.put("ImageUri",imageUri);
                hashMap.put("Date",date);
                hashMap.put("Status","read");

                booksDbRef.child(bookId).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@androidx.annotation.NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(MyBooks.this, "Marked as read.", Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(MyBooks.this, "Error: "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        editBookLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                Intent intent = new Intent(MyBooks.this, EditBook.class);
                intent.putExtra("CategoryId",CategoryId);
                intent.putExtra("BookId",bookId);
                intent.putExtra("BookName",bookName);
                intent.putExtra("ImageUri",imageUri);
                startActivity(intent);
            }
        });
        deleteBookLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    //action menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.books_action_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.item_addBook:
                Intent intent = new Intent(MyBooks.this, BookActivity.class);
                intent.putExtra("CategoryId", CategoryId);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}