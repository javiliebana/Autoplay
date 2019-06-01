package com.example.myapplication;

import android.media.MediaPlayer;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private String current_user="wilxair";
    public ArrayList<Marker> lista_marker;
    public ArrayList<String>lista_audios;
    Button btn_autoplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        btn_autoplay=findViewById(R.id.btn_autolpay);
        btn_autoplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                autoPlay();
            }
        });

    }

    private void autoPlay() {

        int i=0;
        for (Marker marker:lista_marker){
            animacion(marker,i);
            i++;

        }
    }
    private void animacion(Marker marker,int i){
        try {
            MediaPlayer mediaPlayer=new MediaPlayer();
            mediaPlayer.setDataSource(lista_audios.get(i));

            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    boolean audio=true;
                    mp.start();
                    while(audio==true){
                        if(mp.isPlaying()){
                            //LatLng coordenadas =marker.getPosition();
                            //CameraUpdate ubicacion = CameraUpdateFactory.newLatLngZoom(coordenadas, 5);
                            //mMap.moveCamera(ubicacion);
                        }else{
                            audio=false;
                        }
                    }
                }
            });
            mediaPlayer.prepare();


        } catch (IOException e) {
            e.printStackTrace();
        }
        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(),5));
        /*LatLng coordenadas =marker.getPosition();
        CameraUpdate ubicacion = CameraUpdateFactory.newLatLngZoom(coordenadas, 5);
        mMap.moveCamera(ubicacion);*/
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        /*LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));*/

        FirebaseDatabase firebase = FirebaseDatabase.getInstance();
        DatabaseReference bbdd = FirebaseDatabase.getInstance().getReference("puntos");
        Query q=bbdd.orderByChild("creador").equalTo(current_user);
        q.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            //saca datos y los catualiza en la vista
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<Marker> lista_markerFirebase =new ArrayList<>();
                ArrayList<String> lista_audioFirebase =new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Punto p = snapshot.getValue(Punto.class);
                    String coord =p.getCoord();
                    String[] latlong = coord.split(",");
                    String url=p.getUrl();
                    lista_audioFirebase.add(url);
                    double latitude = Double.parseDouble(latlong[0]);
                    double longitude = Double.parseDouble(latlong[1]);
                    LatLng location = new LatLng(latitude, longitude);
                    Marker marker=mMap.addMarker(new MarkerOptions().position(location).title(p.getTitulo()));
                    lista_markerFirebase.add(marker);


                }
                lista_marker=lista_markerFirebase;
                lista_audios=lista_audioFirebase;

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
