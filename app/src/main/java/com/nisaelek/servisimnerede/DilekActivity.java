package com.nisaelek.servisimnerede;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

public class DilekActivity extends AppCompatActivity {
    Bitmap selectedImage;

    EditText commentText,commentText2;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    Uri imageData;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dilek);


        commentText = findViewById(R.id.commentText);
        commentText2 = findViewById(R.id.commentText2);
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();


    }

    public void gonderUpload(View view) {


                    //Download URL
//imageName adlı bir tablo oluşturduk
                    StorageReference newReference = FirebaseStorage.getInstance().getReference(String.valueOf(commentText));

                    newReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            String downloadUrl = uri.toString();

                            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
//assert test etmek için kullanılır ve onay
                            assert firebaseUser != null;
                            String userEmail = firebaseUser.getEmail();
//Açıklama satırını tanımladık
                            String comment = commentText.getText().toString();
                            Date date = new Date();
                            System.out.println(date.toString());

                            if (!userEmail.isEmpty() && !downloadUrl.isEmpty() && !comment.isEmpty()){
                                HashMap<String, Object> postData = new HashMap<>();
                                //Hashmap oluşturduk ve boylelikle birden çok alt sutun oluşturduk
                                postData.put("useremail", userEmail);
                                postData.put("name", commentText2);
                                postData.put("comment", comment);
                                postData.put("date", date);
                                FirebaseFirestore db = FirebaseFirestore.getInstance();
                                db.collection("Posts").document().set(postData).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(DilekActivity.this, "Succes", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(getApplicationContext(),AnaActivity.class));
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(DilekActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }



                        }
                    });


                }



    public void gonderilenSikayet(View view) {
        Intent intent = new Intent(DilekActivity.this,DileksActivity.class);
        startActivity(intent);
        finish();
    }

}
