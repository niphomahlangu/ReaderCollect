package com.example.readercollect;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class MyBooks extends AppCompatActivity {

    RecyclerView bookList;
    String CategoryId, currentUser;
    DatabaseReference booksDbRef;
    FirebaseAuth firebaseAuth;
    FirebaseRecyclerOptions<Book> options;
    FirebaseRecyclerAdapter<Book, MyViewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_books);

        bookList = findViewById(R.id.bookList);
        CategoryId = getIntent().getStringExtra("CategoryId");
        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser().getUid();
        booksDbRef = FirebaseDatabase.getInstance().getReference().child(currentUser).child("Category").child(CategoryId).child("Books");

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
                holder.bookName_itemView.setText(model.getBookName());
                Picasso.get().load(model.getImageUri()).into(holder.bookImage_itemView);
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