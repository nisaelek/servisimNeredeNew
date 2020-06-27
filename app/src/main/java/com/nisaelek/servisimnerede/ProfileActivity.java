package com.nisaelek.servisimnerede;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;

public class ProfileActivity extends AppCompatActivity {
    private TextInputLayout tilEmail, tilSifre, tilKullaniciAdi;
    private TextInputEditText email, sifre, kullaniciadi;
    private ProgressBar progresBarCircle;
    private ImageView profilPhoto;
    private Uri profilPhotoUri = null;
    Bitmap selectedImage;
    private static final int RESIM_SEC = 1;
    private FirebaseAuth mAuth;
    private StorageReference mStorageRef;
    private DatabaseReference databaseReference;
    private ValueEventListener databaseEventListener;
    private boolean profildegisecek = false;
    private Button signOut;

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            progresBarCircle.setVisibility(View.VISIBLE);
            updateUI(user);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (databaseReference != null && databaseEventListener != null)
            databaseReference.removeEventListener(databaseEventListener);

    }

    private void updateUI(FirebaseUser user) {

        if (user == null) {
            kullaniciadi.setText(null);
            profilPhoto.setImageResource(R.drawable.ic_profile);
            email.setText(null);
            sifre.setText(null);
            return;

        }

        if (databaseReference != null) {
            databaseReference = FirebaseDatabase.getInstance().getReference().child("Kullanicilar")
                    .child(user.getUid());
        }


        if (profildegisecek) {
            progresBarCircle.setVisibility(View.GONE);
            return;
        }

        databaseEventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                progresBarCircle.setVisibility(View.GONE);

                String ad = dataSnapshot.child("kullanici_adi").getValue(String.class);
                String url = dataSnapshot.child("profil_url").getValue(String.class);

                kullaniciadi.setText(ad);
                Picasso.get().load(url).into(profilPhoto);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progresBarCircle.setVisibility(View.GONE);
            }
        });


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //initialize
        mAuth = FirebaseAuth.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        tilEmail = findViewById(R.id.tilEmail);
        tilKullaniciAdi = findViewById(R.id.tilKullaniciAdi);
        email = findViewById(R.id.email);
        kullaniciadi = findViewById(R.id.kullaniciadi);
        tilSifre = findViewById(R.id.tilSifre);
        sifre = findViewById(R.id.sifre);
        progresBarCircle = findViewById(R.id.progresBarCircle);
        profilPhoto = findViewById(R.id.profile);

/*
        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                Intent intent=new Intent(ProfilActivity.this,GirisActivity.class);
                startActivity(intent);
                finish();
            }
        });
        */

        if (mAuth.getCurrentUser() != null) {
            databaseReference = FirebaseDatabase.getInstance().getReference().child("Kullanicilar")
                    .child(mAuth.getCurrentUser().getUid());
            databaseReference.child("kullanici_eposta").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                   String kullanici_id= dataSnapshot.getValue().toString();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
          /* databaseReference.child("kullanici_adi").removeValue(new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference _databaseReference) {
                    _databaseReference.getParent().;
                }
            });*/
        }

        profilPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //kameradan resim almak için
                //Intent intent2 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                //startActivityForResult(intent2,RESIM_CEK);
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Resim seçiniz"), RESIM_SEC);
                profildegisecek = true;


            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESIM_SEC && resultCode == RESULT_OK && data != null) {

            profilPhotoUri = data.getData();

            try {

                if (Build.VERSION.SDK_INT >= 28) {
                    ImageDecoder.Source source = ImageDecoder.createSource(this.getContentResolver(), profilPhotoUri);
                    selectedImage = ImageDecoder.decodeBitmap(source);
                    profilPhoto.setImageBitmap(selectedImage);
                } else {
                    selectedImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), profilPhotoUri);
                    profilPhoto.setImageBitmap(selectedImage);
                }


            } catch (IOException e) {
                Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }


        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 1, Menu.NONE, "Çıkış").setIcon(R.drawable.ic_exit)
                .setShowAsAction(
                        MenuItem.SHOW_AS_ACTION_ALWAYS);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        mAuth.signOut();
        updateUI(null);
        return super.onOptionsItemSelected(item);
    }

    public void profilKaydet(View view) {

        tilKullaniciAdi.setError(null);
        if (profilPhotoUri == null || TextUtils.isEmpty(kullaniciadi.getText())) {

            if (profilPhotoUri == null) {
                Toast.makeText(this, "Lütfen profil fotoğrafınızı belirleyiniz.", Toast.LENGTH_SHORT)
                        .show();
            }

            if (TextUtils.isEmpty(kullaniciadi.getText())) {
                tilKullaniciAdi.setError("Lütfen kullanıcı adınızı giriniz.");
            }

            return;
        }

        //Fireabase profil bilgisini kayıt işlemi
        progresBarCircle.setVisibility(View.VISIBLE);

        final String kullaniciAdi = kullaniciadi.getText().toString();
        String uzanti = getFileExtension(profilPhotoUri);

        StorageReference childRef = mStorageRef.child("KullaniciProfili")
                .child(mAuth.getCurrentUser().getUid()).child(kullaniciAdi + "." + uzanti);
        childRef.putFile(profilPhotoUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                Task<Uri> result = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        databaseReference.child("kullanici_adi").setValue(kullaniciAdi);
                        databaseReference.child("kullanici_id").setValue(mAuth.getCurrentUser().getUid());
                        databaseReference.child("profil_url").setValue(uri.toString());
                        profildegisecek = false;

                    }
                });

                Toast.makeText(ProfileActivity.this, "Yuppi", Toast.LENGTH_SHORT).show();
                progresBarCircle.setVisibility(View.GONE);
            }
        });


    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));

    }



}
