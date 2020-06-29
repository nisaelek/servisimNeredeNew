package com.nisaelek.servisimnerede;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private double enlem, boylam;
    private String kullaniciAdi, profilUrl;
    DatabaseReference konumBilgisi;
    ArrayList<Map<String,Object>> konumlar=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        enlem = getIntent().getDoubleExtra("enlem", 0);
        boylam = getIntent().getDoubleExtra("boylam", 0);
        kullaniciAdi = getIntent().getStringExtra("kullanici_adi");
        profilUrl = getIntent().getStringExtra("profil_url");

        final String userId = getIntent().getStringExtra("userId");
        konumBilgisi = FirebaseDatabase.getInstance().getReference("Konumlar").child(userId); //tıkalan kullanıcının keyini alıp konumlar içerisinde bulduk konumlarını

        konumBilgisi.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {

                    for (DataSnapshot data:snapshot.getChildren()){
                        Double _enlem = data.child("enlem").getValue(Double.class);
                        Double _boylam = data.child("boylam").getValue(Double.class);
                        Long _zaman = data.child("zaman").getValue(Long.class);

                        //Hashmap oluşturuypruz ve enlem boylam zamanı içinde tutuyoruz.
                        Map<String,Object> map = new HashMap<>();
                        map.put("enlem",_enlem);
                        map.put("boylam",_boylam);
                        map.put("zaman",_zaman);
                        konumlar.add(map);
                    }

                    konumlariharitayaEkle();
                    /*Double _enlem = snapshot.child("enlem").getValue(Double.class);
                    Double _boylam = snapshot.child("boylam").getValue(Double.class);

                    enlem=_enlem;
                    boylam=_boylam;*/
                }

                //Toast.makeText(MapsActivity.this, snapshot.getValue().toString(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //konumlarDatabase.child("")
        // Add a marker in Sydney and move the camera
       /* LatLng takipEdilen = new LatLng(enlem, boylam);
        mMap.addMarker(new MarkerOptions().position(takipEdilen).title(kullaniciAdi).snippet("ServisKonum!").icon(BitmapDescriptorFactory
                .defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));*/
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(takipEdilen));


        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);


        mMap.setMyLocationEnabled(true);



        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {

                LinearLayout layout = new LinearLayout(MapsActivity.this);
                layout.setOrientation(LinearLayout.VERTICAL);

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                layout.setLayoutParams(params);

                ImageView profil = new ImageView(MapsActivity.this);
                profil.setScaleType(ImageView.ScaleType.CENTER_CROP);
                TextView isim = new TextView(MapsActivity.this);
                TextView aciklama = new TextView(MapsActivity.this);


                isim.setText(marker.getTitle());
                isim.setTextColor(Color.BLACK);
                isim.setGravity(Gravity.CENTER_HORIZONTAL);
                aciklama.setText(marker.getSnippet());
                aciklama.setGravity(Gravity.CENTER_HORIZONTAL);

                layout.addView(profil, 200, 200);
                layout.addView(isim);
                layout.addView(aciklama);
                Picasso.get().load(profilUrl).into(profil, new Callback() {
                    @Override
                    public void onSuccess() {
                        //yeni ekledim
                        Toast.makeText(getApplicationContext(), " Başarılı", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(Exception e) {
                        Toast.makeText(MapsActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });


                return layout;
            }
        });


    }

    public void konumlariharitayaEkle(){
//ilk olarak mapActivity yüklenirken intent içerisinde gelen kullanıcının id si ile konumlar tablosunda o kullanıcının bütün konularını çekiyorum
//daha sonra herbir konumu yukarıda tanımladığım konumlar arrayinin içine atıyorum bütün konuları array içerisine attıktan sonra
//konumlariharitayaEkle() methodunu çalıştırarak o konumları harita üzerinde işaretletiyorum
        for (Map<String,Object> _konum:konumlar){
            LatLng takipEdilen = new LatLng((Double)_konum.get("enlem"), (Double)_konum.get("boylam"));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(takipEdilen, 10));
            mMap.addMarker(new MarkerOptions().position(takipEdilen).title(kullaniciAdi).snippet("ServisKonum!").icon(BitmapDescriptorFactory
                    .defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
        }
    }
}
