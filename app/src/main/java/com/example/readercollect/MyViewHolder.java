package com.example.readercollect;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MyViewHolder extends RecyclerView.ViewHolder{
    ImageView bookImage_itemView;
    TextView bookName_itemView;
    public MyViewHolder(@NonNull View itemView) {
        super(itemView);

        bookImage_itemView = itemView.findViewById(R.id.bookImage_itemView);
        bookName_itemView = itemView.findViewById(R.id.bookName_itemView);
    }
}
