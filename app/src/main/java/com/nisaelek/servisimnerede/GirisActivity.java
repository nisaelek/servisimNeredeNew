package com.nisaelek.servisimnerede;
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
import android.os.PersistableBundle;
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

public class GirisActivity extends AppCompatActivity {
    private TextInputLayout tilEmail, tilSifre, tilKullaniciAdi;
    private TextInputEditText email, sifre, kullaniciadi;
    private TextView btnRegisterRedirect;
    private ProgressBar progresBarCircle;
    private ImageView profilPhoto;
    private Uri profilPhotoUri = null;
    Bitmap selectedImage;
    private static final int RESIM_SEC = 1;
    private FirebaseAuth mAuth;
    private StorageReference mStorageRef;
    private DatabaseReference databaseReference;
    private ValueEventListener databaseEventListener;

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
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
        } //else {
            //((Button) findViewById(R.id.buttonGiris)).setText("Takip Listesine Git");
          //  Intent intent = new Intent(GirisActivity.this, ProfilActivity.class);
         //   startActivity(intent);
         //   finish();
        //}

        if (databaseReference == null) {
            databaseReference = FirebaseDatabase.getInstance().getReference().child("Kullanicilar")
                    .child(user.getUid());
        }



///Burası zaten yorum satırı idi  Bunlar da NEEE ?  birinci versiyondaki kodlar silmeye korktum da en son silcem hmm ben olsam hemen sılerdım risk sevmiyorum boyle durumlarda
    /*    databaseEventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                progresBarCircle.setVisibility(View.GONE);
                String ad = dataSnapshot.child("kullanici_adi").getValue(String.class);
                kullaniciadi.setText(ad);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                progresBarCircle.setVisibility(View.GONE);
            }
       });*/

/*
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_giris);




        //initialize
        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() != null) {
            startActivity(new Intent(this, TakipListesiActivity.class));
            finish();
        }

        mStorageRef = FirebaseStorage.getInstance().getReference();
        tilEmail = findViewById(R.id.tilEmail);
        tilKullaniciAdi = findViewById(R.id.tilKullaniciAdi);
        email = findViewById(R.id.email);
        kullaniciadi = findViewById(R.id.kullaniciadi);
        tilSifre = findViewById(R.id.tilSifre);
        sifre = findViewById(R.id.sifre);
        progresBarCircle = findViewById(R.id.progresBarCircle);
        btnRegisterRedirect = (TextView) findViewById(R.id.btn_RegisterRedirect);


        if (mAuth.getCurrentUser() != null) {
            databaseReference = FirebaseDatabase.getInstance().getReference().child("Kullanicilar")
                    .child(mAuth.getCurrentUser().getUid());
        } else {
            databaseReference = FirebaseDatabase.getInstance().getReference().child("Kullanicilar");
        }

        btnRegisterRedirect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GirisActivity.this, KayitActivity.class);
                startActivity(intent);
            }
        });


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

    public void girisYap(View view) {


        if (!validateForm()) {
            return;
        }
        if (email.getText().toString().isEmpty() && tilSifre.getEditText().getText().toString().isEmpty()) {
            Toast.makeText(getApplicationContext(), "Lütfen Geçerlı Emaıl veya Şıfre Gırınız", Toast.LENGTH_LONG).show();
        } else {

        progresBarCircle.setVisibility(View.VISIBLE);

        mAuth.signInWithEmailAndPassword(email.getText().toString(), sifre.getText().toString())
                .addOnCompleteListener(
                        this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if (task.isSuccessful()) {
                                    progresBarCircle.setVisibility(View.GONE);
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    updateUI(user);
                                    databaseReference.child(user.getUid()).addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            //progresBarCircle.setVisibility(View.INVISIBLE);

                                            Long role = dataSnapshot.child("kullanici_role").getValue(Long.class).longValue();

                                            if (role == R.id.veli) {
                                                Intent redirectIntent = new Intent(GirisActivity.this, TakipListesiActivity.class);
                                                startActivity(redirectIntent);
                                                finish();
                                            } else if (role == R.id.ogrenci) {

                                                Intent redirectIntent = new Intent(GirisActivity.this, TakipListesiActivity.class);
                                                startActivity(redirectIntent);
                                                finish();
                                            } else if (role == R.id.sofor) {
                                                Intent redirectIntent = new Intent(GirisActivity.this, TakipListesiActivity.class);
                                                startActivity(redirectIntent);
                                                finish();
                                            }

                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                            //progresBarCircle.setVisibility(View.GONE);
                                        }
                                    });
                                  //Burası da yorum satiri idi
 */
                                    //Toast.makeText(GirisActivity.this, "Giriş başarılı", Toast.LENGTH_SHORT)
                                    //       .show();
                                    //((Button) findViewById(R.id.buttonGiris)).setText("Takip Listesine Git");
                                    //databaseReference.child(user.getUid()).child("kullanici_role")
                                   /* if (gelenRole=="Sofor"){
                                        Intent redirectIntent=new Intent(GirisActivity.this,ProfilActivity.class);
                                        startActivity(redirectIntent);
                                        finish();
                                    }
                                    else if (gelenRole=="ogr"){
                                        Intent redirectIntent=new Intent(GirisActivity.this,TakipListesiActivity.class);
                                        startActivity(redirectIntent);
                                        finish();
                                    }*/
/*
                                } else {
                                    Toast.makeText(GirisActivity.this, "Giriş başarısız", Toast.LENGTH_SHORT)
                                            .show();
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class GirisActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    EditText email;
    EditText password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_giris);
        email = findViewById(R.id.userEmail);
        password = findViewById(R.id.userPassword);
        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() != null){
            if (mAuth.getCurrentUser().isEmailVerified()){
                startActivity(new Intent(getApplicationContext(),AnaActivity.class));
            }

        }

    }

    public void btnLogin(View view){

        if (!TextUtils.isEmpty(email.getText()) && !TextUtils.isEmpty(password.getText())){

            mAuth.signInWithEmailAndPassword(email.getText().toString(),password.getText().toString()).addOnCompleteListener(
                    this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                String user = mAuth.getCurrentUser().getEmail();
                                if (mAuth.getCurrentUser().isEmailVerified()){
                                    Toast.makeText(getApplicationContext(),"Hoş Geldiniz "+user , Toast.LENGTH_SHORT)
                                            .show();
                                    startActivity(new Intent(getApplicationContext(),AnaActivity.class));
                                }else {
                                    mAuth.signOut();
                                    Toast.makeText(getApplicationContext(), "Lütfen Epostanızı Doğrulayın !", Toast.LENGTH_SHORT)
                                            .show();
                                }


                            } else {
                                Toast.makeText(getApplicationContext(), task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT)
                                        .show();

                            }
                        }}
            );
        }
    }

    public void kaytOl(View view){
        startActivity(new Intent(getApplicationContext(),KayitActivity.class));
    }
}

