package com.nisaelek;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.protobuf.Any;
import com.nisaelek.servisimnerede.R;

import java.util.ArrayList;
import java.util.List;

public class AramaActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
    private SearchView karamakutusu;
    private ListView kliste;

    String[]Kullanicilar={"veli_1593380440825","veli_1593423656728","ogrenci_1593414667730","ogrenci_1593441443505","ogrenci_1593422823175"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arama);
        karamakutusu = (SearchView) findViewById(R.id.aramakutusu);
        kliste = (ListView) findViewById(R.id.liste);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,Kullanicilar);
        kliste.setAdapter(adapter);
       kliste.setTextFilterEnabled(true);
        setupArama();

    }

    private void setupArama() {
        karamakutusu.setIconifiedByDefault(false);
        karamakutusu.setOnQueryTextListener(this);
        karamakutusu.setSubmitButtonEnabled(true);
    }

    ;


    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (TextUtils.isEmpty(newText)) {
            kliste.clearTextFilter();

        } else {
            kliste.setFilterText(newText);
        }
        return true;

    }
}

