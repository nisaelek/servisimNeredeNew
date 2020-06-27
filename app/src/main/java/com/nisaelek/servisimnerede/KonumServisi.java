package com.nisaelek.servisimnerede;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class KonumServisi extends Service {
    //Servis Hizmetleri
    private LocationManager locationManager;
    private LocationListener locationListener;
    private boolean lokasyonBilgisiAlindi = false;
    private FirebaseAuth mAuth;
    private DatabaseReference konumDatabase;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mAuth=FirebaseAuth.getInstance();
        konumDatabase= FirebaseDatabase.getInstance().getReference().child(mAuth.getCurrentUser().getUid()).child("Konumlar");
        //lokasyon servisini tanımladık
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                //lokasyon değiştiğinde
                if (lokasyonBilgisiAlindi==false){
                   // lokasyon değişmediyse kaydet,enlem,boylam bilgileri allındı
                    lokasyonBilgisiKaydet(location.getLatitude(),location.getLongitude());
                }
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };

    }

    private void lokasyonBilgisiKaydet(double enlem, double boylam) {

        lokasyonBilgisiAlindi = true;

        Map<String,Object> map = new HashMap<>();
        map.put("enlem",enlem);
        map.put("boylam",boylam);
        map.put("zaman",System.currentTimeMillis());

        konumDatabase.push().setValue(map).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                alarmAyarla();
                stopSelf();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                lokasyonBilgisiAlindi=false;
                Toast.makeText(getApplicationContext(), "Konumda hata oluştu",
                        Toast.LENGTH_SHORT).show();
                Log.e("Hata oluştu",e.getMessage());
            }
        });
    }

    private void alarmAyarla() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent servisIntent = new Intent(this , KonumServisi.class);
       // Alarmkodu ile benzersiz yapıypruz.
        PendingIntent pi = PendingIntent.getService(this, TakipListesiActivity.alarm_kod ,servisIntent, 0);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP,System.currentTimeMillis()+300000,pi);
        }else {
            alarmManager.set(AlarmManager.RTC_WAKEUP,System.currentTimeMillis()+300000,pi);
        }
    }
//Servis kapatıldığında çalışacak.
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (locationManager != null){
            locationManager.removeUpdates(locationListener);
            locationManager=null;
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //gps Network provider yerine kullandık
        Criteria criteria = new Criteria();
        //Guc tuketimi
        criteria.setPowerRequirement(Criteria.POWER_MEDIUM);
        //daha hassas bir konum
        criteria.setAccuracy(Criteria.ACCURACY_FINE);

        criteria.setSpeedRequired(false);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(false);
        String provider = locationManager.getBestProvider(criteria, true);
            //versiyon kontrolu yaptık
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            //konum isteği gönderilecek
            locationManager.requestLocationUpdates(provider,0,0,locationListener);
        }else {

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                // İzin vermediniz izin alınmamış
                //ActivityCompat
                //.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }else {
                //konum isteği gönderilecek
                locationManager.requestLocationUpdates(provider,0,0,locationListener);
            }
        }

        return super.onStartCommand(intent, flags, startId);
    }
}
