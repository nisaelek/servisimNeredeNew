package com.nisaelek.servisimnerede;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/*
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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;

public class KayitActivity extends AppCompatActivity {
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
    private RadioGroup radioButtonGroup;
    private FirebaseInstanceId mId;
    private String userType;
    private RadioButton vel1;
    private RadioButton study;
    private RadioButton drver;

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

        if (databaseReference!=null && databaseEventListener!=null)
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
        }else{
            databaseReference = FirebaseDatabase.getInstance().getReference().child("Kullanicilar");
        }

       // ((Button) findViewById(R.id.buttonGiris)).setText("Takip Listesine Git");

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
        setContentView(R.layout.activity_kayit);

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
        radioButtonGroup=(RadioGroup) findViewById(R.id.radioUserRole);
        TextView btnLoginRedirect = (TextView) findViewById(R.id.btnLoginRedirect);





        if (mAuth.getCurrentUser() != null) {
            databaseReference = FirebaseDatabase.getInstance().getReference().child("Kullanicilar")
                    .child(mAuth.getCurrentUser().getUid());
        }else{
            databaseReference = FirebaseDatabase.getInstance().getReference().child("Kullanicilar");
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
                profildegisecek=true;


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
                    ImageDecoder.Source source = ImageDecoder.createSource(this.getContentResolver(),profilPhotoUri);
                    selectedImage = ImageDecoder.decodeBitmap(source);
                    profilPhoto.setImageBitmap(selectedImage);
                } else {
                    selectedImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(),profilPhotoUri);
                    profilPhoto.setImageBitmap(selectedImage);
                }


            } catch (IOException e) {
                Toast.makeText(getApplicationContext(),e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
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


    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));

    }


    public void kayitOl(View view) {
        if (!validateForm()) {
            return;
        }

        vel1 = findViewById(R.id.veli);
        study = findViewById(R.id.ogrenci);
        drver = findViewById(R.id.sofor);
        userType = "";

        if (vel1.isChecked()) {
            userType = "veli_";
        } else if (study.isChecked()) {
            userType = "ogrenci_";
        } else if (drver.isChecked()) {
            userType = "sofor_";
        }

        if (userType.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Lütfen Kullanıcı Rolü Seçınız !!", Toast.LENGTH_LONG).show();
        } else {



        progresBarCircle.setVisibility(View.VISIBLE);

        mAuth.createUserWithEmailAndPassword(email.getText().toString(), sifre.getText().toString())

                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progresBarCircle.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                         FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(userType + new Date().getTime()).build();
                            user.updateProfile(profileUpdates);
                            DatabaseReference newReference = databaseReference.child(task.getResult().getUser().getUid());
                            newReference.child("kullanici_eposta").setValue(email.getText().toString());
                            newReference.child("kullanici_adi").setValue(userType + new Date().getTime());
                            int selectedId = radioButtonGroup.getCheckedRadioButtonId();
                            newReference.child("kullanici_role").setValue(selectedId);

                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(KayitActivity.this, "Kayıt başarılı",
                                    Toast.LENGTH_SHORT).show();
                            //   Intent intent=new Intent(KayitActivity.this,GirisActivity.class);
                            //    startActivity(intent);
                            startActivity(new Intent(KayitActivity.this, GirisActivity.class));
                            finish();
                            //return;


                        } else {
                            Toast.makeText(KayitActivity.this,
                                    "Authentication failed. " + task.getException(),
                                    Toast.LENGTH_SHORT).show();
                            Log.w("TAG", "createUserWithEmail:failure", task.getException());
                        }
                    }
                });
    }
    }


    private boolean validateForm() {
        boolean valid = true;
        tilEmail.setError(null);
        tilSifre.setError(null);

        if (TextUtils.isEmpty(email.getText()) || TextUtils.isEmpty(sifre.getText())) {
            if (TextUtils.isEmpty(email.getText())) {
                tilEmail.setError("Lütfen mail adresinizi giriniz");
                valid = false;
            } else {
                if (!email.getText().toString().contains("@")) {
                    tilEmail.setError("Lütfen geçerli bir mail adresi giriniz");
                    valid = false;
                }
            }
        }

        if (TextUtils.isEmpty(sifre.getText())) {
            tilSifre.setError("Lütfen şifrenizi giriniz");
            valid = false;
        }
        return valid;
    }
}
*/
public class KayitActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    EditText email;
    EditText password;
    FirebaseUser currentUser;
    DatabaseReference databaseReference;
    private RadioGroup radioButtonGroup;
    private FirebaseInstanceId mId;
    private String userType;
    private RadioButton vel1;
    private RadioButton study;
    private RadioButton drver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kayit);
        databaseReference = FirebaseDatabase.getInstance().getReference("Kullanicilar");
        mAuth = FirebaseAuth.getInstance();
        email = findViewById(R.id.upEmail);
        password = findViewById(R.id.upPassword);
        currentUser = mAuth.getCurrentUser();
        radioButtonGroup = (RadioGroup) findViewById(R.id.radioUserRole);
    }

    public void btnSignup(View view) {

        kayitOl(view);
    }

    public void giris_Yap(View view) {
        startActivity(new Intent(getApplicationContext(), GirisActivity.class));
    }


    public void kayitOl(View view) {


        vel1 = findViewById(R.id.veli);
        study = findViewById(R.id.ogrenci);
        drver = findViewById(R.id.sofor);
        userType = "";

        if (vel1.isChecked()) {
            userType = "veli_";
        } else if (study.isChecked()) {
            userType = "ogrenci_";
        } else if (drver.isChecked()) {
            userType = "sofor_";
        }

        if (userType.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Lütfen Kullanıcı Rolü Seçınız !!", Toast.LENGTH_LONG).show();
        } else {
            mAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(userType + user.getEmail()).build();
                                user.updateProfile(profileUpdates);
                                //Burda bu method sayesinde sendEMAİLvERİFİCATİON eMAİL doğrulama olayını yapıyor
                                user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {


                                        int selectedId = radioButtonGroup.getCheckedRadioButtonId();
                                        Map<String, Object> map = new HashMap<>();
                                        map.put("kullanici_eposta", email.getText().toString());
                                        map.put("kullanici_adi", userType + new Date().getTime());
                                        map.put("kullanici_role", selectedId);
                                        databaseReference.child(user.getUid()).setValue(map);

                                        Toast.makeText(KayitActivity.this, "Email Doğrulama  Maili Yollandı",
                                                Toast.LENGTH_SHORT).show();

                                        startActivity(new Intent(KayitActivity.this, GirisActivity.class));
                                        finish();
                                    }
                                });


                            } else {
                                Toast.makeText(KayitActivity.this,
                                        "Authentication failed. " + task.getException(),
                                        Toast.LENGTH_SHORT).show();
                                Log.w("TAG", "createUserWithEmail:failure", task.getException());
                            }
                        }
                    });


    /*

     if (currentUser != null){
                 currentUser.sendEmailVerification().addOnCompleteListener(this, new OnCompleteListener<Void>() {
                     @Override
                     public void onComplete(@NonNull Task<Void> task2) {
                         if(task2.isSuccessful()){
                             Toast.makeText(getApplicationContext(),"Maıl Gönderıldı",Toast.LENGTH_LONG).show();
                         } else {

                             Toast.makeText(getApplicationContext(),task2.getException().getMessage(),Toast.LENGTH_LONG).show();
                         }
                     }
                 });
             } else {
                 Toast.makeText(getApplicationContext(),"Curren user null",Toast.LENGTH_LONG).show();
             }
     */

        }
    }

    private boolean validateForm() {
        email.setError(null);
        return false;
    }
}