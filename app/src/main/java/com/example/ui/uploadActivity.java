package com.example.ui;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class uploadActivity<firebase> extends AppCompatActivity {


    private FloatingActionButton uploadButton;
    private ImageView uploadimage;
    EditText uploadcaption;
    ProgressBar progressBar;
    private Uri imageurl;

    final private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Images");
    final private StorageReference  storageReference = FirebaseStorage.getInstance().getReference();





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        uploadButton = findViewById(R.id.uploadbutton);
        uploadcaption = findViewById(R.id.uploadcaption);
        uploadimage = findViewById(R.id.uploadimage);
        progressBar= findViewById(R.id.progressbar);
        progressBar.setVisibility(View.INVISIBLE);
        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                      if (result.getResultCode()== Activity.RESULT_OK){
                          Intent data = result.getData();
                          imageurl = data.getData();
                          uploadimage.setImageURI(imageurl);
                      }else {
                          Toast.makeText(uploadActivity.this, "No image Selected ", Toast.LENGTH_SHORT).show();
                      }
                    }
                }
        );
       uploadimage.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               Intent  PhotoPicker = new Intent();
               PhotoPicker.setAction(Intent.ACTION_GET_CONTENT);
               PhotoPicker.setType("image/*");
               activityResultLauncher.launch(PhotoPicker);
           }
       });
       uploadButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               if(imageurl != null){
                   uploadToFirebase(imageurl);
               }else {
                   Toast.makeText(uploadActivity.this, " please select image ", Toast.LENGTH_SHORT).show();
               }
           }
       });

    }
    //Outside onCreate
    private void uploadToFirebase(Uri uri)
    {
        String caption = uploadcaption.getText().toString();
        final StorageReference imageReference = storageReference.child(System.currentTimeMillis()+"."+ getFileExtension(uri));
   imageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
       @Override
       public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
          imageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
              @Override
              public void onSuccess(Uri uri) {

                   Data Data=new Data(uri.toString(),caption);
                   String key = databaseReference.push().getKey();
                   databaseReference.child(key).setValue(Data);
                // ProgressBar.setVisibility(View.INVISIBLE);
                  Toast.makeText(uploadActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                  Intent intent=new Intent(uploadActivity.this,MainActivity.class);
                  startActivity(intent);
                  finish();
              }
          });


       }
   }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
       @Override
       public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {

       }
   });
    }
    private String getFileExtension(Uri Fileurl){
   ContentResolver contentResolver = getContentResolver();
        MimeTypeMap Mime = MimeTypeMap.getSingleton();
        return Mime.getExtensionFromMimeType(contentResolver.getType(Fileurl));
    }

}
