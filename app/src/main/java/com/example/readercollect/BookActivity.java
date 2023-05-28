package com.example.readercollect;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

public class BookActivity extends AppCompatActivity {

    //variable declarations
    ImageView selectBook_imageView;
    EditText txtBookName;
    TextView progressView;
    ProgressBar book_progressBar;
    Button btnCreateBook;
    private static final int REQUEST_CODE_IMAGE = 1;
    StorageReference storageRef;
    Uri imageUri;
    boolean isImageAdded = false;
    FirebaseAuth firebaseAuth;
    DatabaseReference bookDbRef;
    String currentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book);

        //variable initialization
        selectBook_imageView = findViewById(R.id.selectBook_imageView);
        progressView = findViewById(R.id.progressView);
        book_progressBar = findViewById(R.id.book_progressBar);
        txtBookName = findViewById(R.id.txtBookName);
        btnCreateBook = findViewById(R.id.btnCreateBook);
        storageRef = FirebaseStorage.getInstance().getReference().child("BookImage");
        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser().getUid();
        bookDbRef = FirebaseDatabase.getInstance().getReference().child(currentUser).child("Books");

        //select image from internal storage
        selectBook_imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                /*Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(intent,REQUEST_CODE_IMAGE);*/
            }
        });

        //upload book name and image
        btnCreateBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String imageName = txtBookName.getText().toString();
                //check if text field empty
                if(TextUtils.isEmpty(imageName)){
                    txtBookName.setError("Name is required!");
                    return;
                }
                if(isImageAdded!=false && imageName!=null){
                    uploadImage(imageName);
                }
            }
        });
    }


    private void uploadImage(final String imageName) {

        progressView.setVisibility(View.VISIBLE);
        book_progressBar.setVisibility(View.VISIBLE);

        final String key = bookDbRef.push().getKey();
        //put file in the storage
        storageRef.child(key+".jpg").putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                storageRef.child(key+".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        //assign data to corresponding data fields
                        HashMap hashMap = new HashMap();
                        hashMap.put("BookName",imageName);
                        hashMap.put("ImageUri",uri.toString());

                        //insert data into the database
                        bookDbRef.child(key).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(BookActivity.this, "Book uploaded successfully.", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(BookActivity.this, MyBooks.class));
                                finish();
                            }
                        });
                    }
                });
            }
        });
    }

    //action menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.photo_action_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.itemAddPhoto:
                //show bottom sheet dialogue
                showDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottomsheet_layout);

        LinearLayout cameraLayout = dialog.findViewById(R.id.layoutCamera);
        LinearLayout galleryLayout = dialog.findViewById(R.id.layoutGallery);

        galleryLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_CODE_IMAGE);
                //startActivityForResult(intent,REQUEST_CODE_IMAGE);
            }
        });

        dialog.show();
        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_IMAGE && data!=null) {
            imageUri = data.getData();
            isImageAdded = true;
            selectBook_imageView.setImageURI(imageUri);
        }
    }
}