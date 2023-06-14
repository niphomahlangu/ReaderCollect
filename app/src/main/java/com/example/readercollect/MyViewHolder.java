package com.example.readercollect;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MyViewHolder extends RecyclerView.ViewHolder{
    ImageView bookImage_itemView, btn_books_options;
    TextView bookName_itemView, bookDate_itemView;
    public MyViewHolder(@NonNull View itemView) {
        super(itemView);

        bookImage_itemView = itemView.findViewById(R.id.favorite_bookImage);
        bookName_itemView = itemView.findViewById(R.id.bookName_itemView);
        bookDate_itemView = itemView.findViewById(R.id.book_dateCreated);
        btn_books_options = itemView.findViewById(R.id.favorite_options);
    }
}
