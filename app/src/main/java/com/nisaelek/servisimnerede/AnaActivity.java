package com.nisaelek.servisimnerede;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.List;

public class AnaActivity extends AppCompatActivity {
    private DatabaseReference userDatabase, takipDatabase, konumlarDatabase;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private FirebaseInstanceId mID;
    private TextInputLayout textInputLayout;
    private TextInputEditText kullaniciAdi;
    private ListView listView;
    private List<Takip> takipList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ana);
    }
// Tum butonlara onclick verdik ve diğer actv ye atadık
    public void takiplgit(View view) {
        Intent intent = new Intent(AnaActivity.this, TakipListesiActivity.class);
        startActivity(intent);
    }


    public void profileGit(View view) {
        Intent intent = new Intent(AnaActivity.this, ProfileActivity.class);
        startActivity(intent);
    }

    public void uploadaGit(View view) {
        Intent intent = new Intent(AnaActivity.this, UploadActivity.class);
        startActivity(intent);
    }
    public void shareGit(View view) {
        Intent intent = new Intent(AnaActivity.this, ShareActivity.class);
        startActivity(intent);
    }

/*
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(TakipListesiActivity.this, AyarlarActivity.class));
            return true;
        }

        int id = item.getItemId();
        if (id == R.id.cikisbtn) {
            mAuth.signOut();
            startActivity(new Intent(AnaActivity.this, KayitActivity.class));
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);

 */
    }


