package com.example.readercollect;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

public class BookActivity extends AppCompatActivity {

    //variable declarations
    ImageView addedImage_imageView;
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
    String currentUser, categoryId;
    boolean cameraIsAdded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book);

        //variable initialization
        addedImage_imageView = findViewById(R.id.addedImage_imageView);
        progressView = findViewById(R.id.progressView);
        book_progressBar = findViewById(R.id.book_progressBar);
        txtBookName = findViewById(R.id.txtBookName);
        btnCreateBook = findViewById(R.id.btnCreateBook);
        storageRef = FirebaseStorage.getInstance().getReference().child("BookImage");
        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser().getUid();
        categoryId = getIntent().getStringExtra("CategoryId");
        bookDbRef = FirebaseDatabase.getInstance().getReference().child(currentUser).child("Category").child(categoryId).child("Books");
        cameraIsAdded = false;

        //hide progressBar
        progressView.setVisibility(View.GONE);
        book_progressBar.setVisibility(View.GONE);

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
                            }
                        });
                    }
                });
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@androidx.annotation.NonNull UploadTask.TaskSnapshot snapshot) {
                double progress = (snapshot.getBytesTransferred()*100)/snapshot.getTotalByteCount();
                book_progressBar.setProgress((int)progress);
                progressView.setText(progress+"%");
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

        cameraLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, REQUEST_CODE_IMAGE);
                cameraIsAdded = true;
            }
        });

        galleryLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_CODE_IMAGE);
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
        if (requestCode == REQUEST_CODE_IMAGE && data!=null && cameraIsAdded==false) {
            imageUri = data.getData();
            isImageAdded = true;
            addedImage_imageView.setImageURI(imageUri);
        }else {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            //convert Bitmap object to Uri
            imageUri = getImageUri(getApplicationContext(), photo);
            isImageAdded = true;
            addedImage_imageView.setImageURI(imageUri);
        }
    }

    //returns Uri of the camera image
    private Uri getImageUri(Context applicationContext, Bitmap photo) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(applicationContext.getContentResolver(), photo, "Title", null);
        return Uri.parse(path);
    }
}