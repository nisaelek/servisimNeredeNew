package com.nisaelek.servisimnerede;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TakipListesiActivity extends AppCompatActivity {
    public static final int alarm_kod = 1999;
    private BottomSheetBehavior bottomSheetBehavior;
    private DatabaseReference userDatabase, takipDatabase, konumlarDatabase;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private FirebaseInstanceId mID;
    private TextInputLayout textInputLayout;
    private TextInputEditText kullaniciAdi;
    private ListView listView;
    private List<Takip> takipList;
    private TakipListesiAdapter adapter;

    @Override
    protected void onStart() {
        super.onStart();
    }
//izin Kontrolu
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "İzin alındı", Toast.LENGTH_SHORT).show();
            }

        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_takip_listesi);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //hata verdi yorum satırı yaptım. bi ka
      setSupportActionBar(toolbar);


        textInputLayout = findViewById(R.id.til);
        kullaniciAdi = findViewById(R.id.takip_kullanici_adi);
        listView = findViewById(R.id.takipListesi);
        View view = findViewById(R.id.bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(view);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        takipList = new ArrayList<>();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        adapter = new TakipListesiAdapter(this, takipList);
        listView.setAdapter(adapter);

        if (user == null) {
            startActivity(new Intent(this, KayitActivity.class));
            finish();
            return;
        }

        userDatabase = FirebaseDatabase.getInstance().getReference().child("Kullanicilar");
        takipDatabase = FirebaseDatabase.getInstance().getReference().child("Takiplesenler");
        konumlarDatabase = FirebaseDatabase.getInstance().getReference().child("Konumlar");

        /*ref.child("id").child("Konumlar").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                // data available in snapshot.value()
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });*/
   
        //String userName = mAuth.getCurrentUser().getDisplayName();
        //setTitle(userName);

        // Android M ve üzeri için lokasyon izni alma işlemi
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                // izin isteği
                ActivityCompat
                        .requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }

        //Takip edilenlerin listesi
        takipDatabase.orderByChild("takipEden").equalTo(user.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Log.d("Takip veritabanı", dataSnapshot.toString());
                        takipList.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Takip takip = snapshot.getValue(Takip.class);
                            takipList.add(takip);
                        }
                        findViewById(R.id.progress).setVisibility(View.GONE);
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                //((FloatingActionButton)view).hide();
            }
        });

        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        //bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                        break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:
                        fab.hide();
                        break;
                    case BottomSheetBehavior.STATE_HIDDEN:
                        fab.show();
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:

                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View view, float v) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_takip_listesi, menu);
        return true;
    }

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

        if (id == R.id.cikisbtn) {
            mAuth.signOut();
            startActivity(new Intent(TakipListesiActivity.this, KayitActivity.class));
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    public void AnaActivityGit(View view){
        startActivity(new Intent(TakipListesiActivity.this, AnaActivity.class));
    }

    public void clickEkle(View view) {

        textInputLayout.setError(null);

        if (TextUtils.isEmpty(kullaniciAdi.getText())) {
            textInputLayout.setError("kullanıcı adınızı giriniz");
            return;
        }

        String kullaniciadi = kullaniciAdi.getText().toString();
        Query queryRef = userDatabase.orderByChild("kullanici_adi").equalTo(kullaniciadi);
        queryRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.d("takipListesi", dataSnapshot.toString());

                if (dataSnapshot.exists()) {
                    String ad = dataSnapshot.child("kullanici_adi").getValue(String.class);
                    String id = dataSnapshot.child("kullanici_id").getValue(String.class);
                    String url = dataSnapshot.child("profil_url").getValue(String.class);
                    Toast.makeText(TakipListesiActivity.this, ad + " kullanıcısı bulundu", Toast.LENGTH_SHORT)
                            .show();
                    takipEkle(user.getUid(), dataSnapshot.getKey());
                    //takipEkle(user.getUid(),id);
                }

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
   public void haritaGoster(View view){
        Intent intent=new Intent(getApplicationContext(),MapsActivity.class);
        startActivity(intent);
    }

    private void takipEkle(String takipEden, String takipEdilen) {
        Takip takip = new Takip(takipEden, takipEdilen);
        takipDatabase.push().setValue(takip).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(TakipListesiActivity.this, "Başarılı bir şekilde eklediniz", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(TakipListesiActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private class TakipListesiAdapter extends BaseAdapter {

        private final Context context;
        private final List<Takip> takipList;

        public TakipListesiAdapter(Context c, List<Takip> takipList) {
            this.context = c;
            this.takipList = takipList;
        }

        @Override
        public int getCount() {
            return takipList.size();
        }

        @Override
        public Object getItem(int i) {
            if (takipList.size() == 0) {
                return null;
            }

            return takipList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if (takipList.size() == 0) {
                return null;
            }

            LinearLayout container = (LinearLayout) ((Activity) context).getLayoutInflater().
                    inflate(R.layout.takip_item, null);

            Takip takip = takipList.get(i);

            final TextView ad = container.findViewById(R.id.ad);
            final TextView zaman = container.findViewById(R.id.zaman);
            final TextView adresler = container.findViewById(R.id.adresler);
            //final TextView uzaklik = container.findViewById(R.id.uzaklik);
            final ImageView profil = container.findViewById(R.id.profilItem);

            final Konum[] konum = new Konum[1];
            final String[] url = new String[1];

            //kullanıcı adı ve profil resmini set etme işlemi
            userDatabase.child(takip.getTakipEdilen()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String kullaniciadi = dataSnapshot.child("kullanici_adi").getValue(String.class);
                        url[0] = dataSnapshot.child("profil_url").getValue(String.class);

                        ad.setText(kullaniciadi);
                        String currentDateTimeString = java.text.DateFormat.getDateTimeInstance().format(new Date());
                        zaman.setText(currentDateTimeString);
                        Picasso.get().load(url[0]).into(profil);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(context, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

            //Tarih, saat ve adres bilgisini set etme işlemi  //şımdı bakıyorum bkle az tmm
            konumlarDatabase.child(takip.getTakipEdilen()).orderByValue().limitToLast(1)
                    .addValueEventListener(
                            new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    Log.d("Konumlar", dataSnapshot.toString());
                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                        konum[0] = snapshot.getValue(Konum.class);
                                        setKonumBilgisi(konum[0]);
                                    }
                                }

                                private void setKonumBilgisi(Konum konum) {

                                    SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                                    Date date = new Date(konum.getZaman());
                                    zaman.setText(format.format(date));

                                    String adres = "";
                                    Geocoder geocoder = new Geocoder(context, Locale.getDefault());

                                    try {
                                        List<Address> addressList = geocoder
                                                .getFromLocation(konum.getEnlem(), konum.getBoylam(), 1);
                                        for (Address adr : addressList) {
                                            Log.d("Adres", "setKonumBilgisi: " + adr.toString());
                                            adres += adr.getAddressLine(0);
                                            for (int i = 0; i < adr.getMaxAddressLineIndex(); i++) {
                                                //adres+=adr.getAddressLine(i)+" , ";
                                            }
                                        }
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }

                                    adresler.setText(adres);

                /*
                  //Uzaklık bilgisi hesaplama
                Location location = new Location("");
                location.setLatitude(konum.getEnlem());
                location.setLongitude(konum.getBoylam());

                Location mevcutKonum = new Location("");
                location.setLatitude(enlem);
                location.setLongitude(boylam);
                Log.d("Konumlar", "setKonumBilgisi: "+enlem+"  "+boylam+"  "+position+"  konum"+konum.getEnlem()+" "+konum.getBoylam());

                float[] distance = new float[2];
                Location.distanceBetween(enlem, boylam, konum.getEnlem(), konum.getBoylam(), distance);
                int m = (int) distance[0];
                //uzaklik.setText(m + " metre");

               try {
                  int mesafe = (int) mevcutKonum.distanceTo(location);
                  uzaklik.setText(mesafe + " metre");

                } catch (Exception e) {
                  Log.e("Hata", "setKonumBilgisi: " + e.getMessage());
                }

                 */

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    //Hata
                                }
                            });

            container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Mapsactivity açılacak

                    Intent intent = new Intent(context, MapsActivity.class);
                    intent.putExtra("enlem",konum[0].getEnlem());
                    intent.putExtra("boylam",konum[0].getBoylam());
                    intent.putExtra("profil_url",url[0]);
                    intent.putExtra("kullanici_adi",ad.getText().toString());
                    context.startActivity(intent);



                }
            });

            return container;

        }
    }
    }

