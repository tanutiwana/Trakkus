//tanveer kaur
package com.example.trakkus;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;

import com.example.trakkus.Model.MyLocation;
import com.example.trakkus.Utils.Commonx;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class TrackingActivity extends FragmentActivity implements OnMapReadyCallback, ValueEventListener {

    private GoogleMap mMap;
    DatabaseReference trackUserLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        registerEventRealtime();
    }

    private void registerEventRealtime() {
        trackUserLocation = FirebaseDatabase.getInstance()
                .getReference(Commonx.PUBLIC_LOCATION)
                .child(Commonx.trackingUser.getUid());
        trackUserLocation.addValueEventListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        trackUserLocation.addValueEventListener(this);
    }

    @Override
    protected void onStop() {
        trackUserLocation.removeEventListener(this);
        super.onStop();
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
        //enable zoom User Interface
        mMap.getUiSettings().setZoomControlsEnabled(true);

        //set Custom skin for Map style
        boolean success = googleMap.setMapStyle(MapStyleOptions
                .loadRawResourceStyle(this, R.raw.my_uber_style));
    }

    @Override
    public void onDataChange(@NonNull DataSnapshot snapshot) {

        if (snapshot.getValue() != null) {

            //for clear the pervious markaer
            mMap.clear();

            MyLocation location = snapshot.getValue(MyLocation.class);

            LatLng userMarker = new LatLng(location.getLatitude(), location.getLongitude());
            mMap.addMarker(new MarkerOptions().position(userMarker)
                    .title(Commonx.trackingUser.getEmail())
                    .snippet(Commonx.getDateFormatted(Commonx.convertTimeStampintoDate(location.getTime()))));

            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userMarker, 15f));


        }

    }

    @Override
    public void onCancelled(@NonNull DatabaseError error) {


    }
}
