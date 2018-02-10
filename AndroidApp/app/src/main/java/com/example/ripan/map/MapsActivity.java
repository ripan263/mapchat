package com.example.ripan.map;

import android.location.Location;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMyLocationClickListener, GoogleMap.OnMyLocationButtonClickListener {

    private GoogleMap mMap;
    EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        editText = (EditText) findViewById(R.id.editText);
        editText.setImeActionLabel("Custom text", KeyEvent.KEYCODE_ENTER);
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                String title = textView.getText().toString();
                LatLng curLocation = new LatLng(location.getLatitude(), location.getLongitude());
                Marker marker = mMap.addMarker(new MarkerOptions().position(curLocation).title(title));
                textView.setVisibility(View.GONE);
                marker.showInfoWindow();
                return true;
            }
        });

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
        // Handler handler= new Handler();
        // Add a marker in Sydney and move the camera
        mMap.setMyLocationEnabled(true);
        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);
      /*  Location myLocation= googleMap.getMyLocation();
        LatLng dublin = new LatLng(myLocation.getLatitude(),myLocation.getLongitude());
        final Marker marker=mMap.addMarker(new MarkerOptions().position(dublin).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(dublin));
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                marker.remove();
            }
        },10000);
*/


    }

    Location location;

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(this, "Current location:\n" + location.getLatitude() + " , " + location.getLongitude(), Toast.LENGTH_LONG).show();
        editText.setVisibility(View.VISIBLE);
        this.location = location;

    }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();

        return false;
    }
}
