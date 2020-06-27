package com.nisaelek.servisimnerede;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentTransaction;

public class AyarlarActivity extends PreferenceActivity implements
        SharedPreferences.OnSharedPreferenceChangeListener {
    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.ayarlar);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        preferences.registerOnSharedPreferenceChangeListener(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s){
        boolean isKonum = sharedPreferences.getBoolean("konumSwitch",false);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent servisIntent = new Intent(this , KonumServisi.class);
        PendingIntent pi = PendingIntent.getService(this, TakipListesiActivity.alarm_kod, servisIntent, 0);
        if (isKonum){

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP,System.currentTimeMillis()+1000,pi);
                startForegroundService(servisIntent);
                return;

            }else {
                alarmManager.set(AlarmManager.RTC_WAKEUP,System.currentTimeMillis()+1000,pi);
            }
        }else {
            alarmManager.cancel(pi);
        }
    }
}
