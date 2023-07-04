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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

public class EditBook extends AppCompatActivity {

    //variable declaration
    ImageView edit_image;
    EditText edit_bookName;
    TextView edit_progressView;
    ProgressBar edit_progressBar;
    Button btnUpdateBook;
    FirebaseAuth firebaseAuth;
    DatabaseReference dbBookReference;
    StorageReference storageReference;
    String currentUser, bookId, categoryId, bookName, imageUri;
    Uri myImageUri;
    boolean isImageAdded, cameraIsAdded;
    private static final int REQUEST_CODE_IMAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_book);

        //variable initialization
        edit_image = findViewById(R.id.edit_image);
        edit_bookName = findViewById(R.id.edit_bookName);
        edit_progressView = findViewById(R.id.edit_progressView);
        edit_progressBar = findViewById(R.id.edit_progressBar);
        btnUpdateBook = findViewById(R.id.btnUpdateBook);
        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser().getUid();
        categoryId = getIntent().getStringExtra("CategoryId");
        bookId = getIntent().getStringExtra("BookId");
        bookName = getIntent().getStringExtra("BookName");
        imageUri = getIntent().getStringExtra("ImageUri");
        dbBookReference = FirebaseDatabase.getInstance().getReference().child(currentUser).child(categoryId).child(bookId);
        storageReference = FirebaseStorage.getInstance().getReference().child("BookImage");
        isImageAdded = false;
        cameraIsAdded = false;

        //make progressBar and progressView gone
        edit_progressView.setVisibility(View.GONE);
        edit_progressBar.setVisibility(View.GONE);

        //load the selected book
        loadBookData();

        btnUpdateBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadChanges();
            }
        });
    }

    private void uploadChanges() {
        edit_progressView.setVisibility(View.VISIBLE);
        edit_progressBar.setVisibility(View.VISIBLE);

        String key = dbBookReference.push().getKey();

        storageReference.child(key+".jpg").putFile(myImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                storageReference.child(key+".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        //update book name
                        bookName = edit_bookName.getText().toString();
                        //update imageUri
                        imageUri = uri.toString();

                        HashMap hashMap = new HashMap();
                        hashMap.put("BookName",bookName);
                        hashMap.put("ImageUri",imageUri);

                        dbBookReference.updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                            @Override
                            public void onComplete(@androidx.annotation.NonNull Task task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(EditBook.this, "Updated successfully.", Toast.LENGTH_SHORT).show();
                                    edit_progressView.setVisibility(View.GONE);
                                    edit_progressBar.setVisibility(View.GONE);
                                }else {
                                    Toast.makeText(EditBook.this, "Failed to update.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@androidx.annotation.NonNull UploadTask.TaskSnapshot snapshot) {
                double progress = (snapshot.getBytesTransferred()*100)/snapshot.getTotalByteCount();
                edit_progressBar.setProgress((int)progress);
                edit_progressView.setText(progress+"%");
            }
        });
    }

    private void loadBookData() {
        dbBookReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@androidx.annotation.NonNull DataSnapshot snapshot) {
                //load image
                Picasso.get().load(imageUri).into(edit_image);
                //load book name
                edit_bookName.setText(bookName);
            }

            @Override
            public void onCancelled(@androidx.annotation.NonNull DatabaseError error) {

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
            myImageUri = data.getData();
            isImageAdded = true;
            edit_image.setImageURI(myImageUri);
        }else {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            //convert Bitmap object to Uri
            myImageUri = getImageUri(getApplicationContext(), photo);
            isImageAdded = true;
            edit_image.setImageURI(myImageUri);
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