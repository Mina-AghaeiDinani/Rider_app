package com.example.deliveryman;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.example.deliveryman.directionHelper.FetchURL;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;

import java.util.List;

import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.maps.model.Marker;


import com.example.deliveryman.directionHelper.TaskLoadedCallback;

public class NavigateActivity extends AppCompatActivity implements OnMapReadyCallback, TaskLoadedCallback {
    private String someVariable;
    private static String key, customerId, restaurantId;
    private static String slatA;
    private DatabaseReference mRefCustomerLocation;
    private DatabaseReference mRefRestaurantLocation;
    //Location
    private Double latA;
    private Double latB;
    private Double lngA;
    private Double lngB;

    //Map
    private GoogleMap mMap;
    private static MarkerOptions customerPlace, restaurantPlace;
    private static MarkerOptions rest1, rest2;
    private Polyline currentPolyline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigate);
        //get Ids from previous activity
        key = getIntent().getStringExtra("key");
        restaurantId = getIntent().getStringExtra("restaurantId");
        customerId = getIntent().getStringExtra("customerId");
        // Toast.makeText(NavigateActivity.this,restaurantId+"     "+customerId,Toast.LENGTH_LONG).show();
        readCustomerLocation();
        readRestaurantLocation();

        position();
        /* readRestaurantLocation1(new FirebaseCallback() {
            @Override
            public MarkerOptions onCallback(Double lat, Double lng) {
                rest1 = new MarkerOptions().position(new LatLng(lat, lng)).title("Restaurant Place");
                return rest1;
            }
        });*/


    }

    private void position() {
        if (latA == null || latB==null || lngA == null || lngB==null)
            return;
        customerPlace = new MarkerOptions().position(new LatLng(latA, lngA)).title("Customer Place");
        restaurantPlace = new MarkerOptions().position(new LatLng(latB, lngB)).title("Restaurant Place");

        //..............
        Button btnGetDirection = findViewById(R.id.btnGetDirection);
        btnGetDirection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toast.makeText(NavigateActivity.this, "" + latA, Toast.LENGTH_LONG).show();
                new FetchURL(NavigateActivity.this).execute(getUrl(customerPlace.getPosition(), restaurantPlace.getPosition(), "walking"), "walking");
            }
        });
        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.mapNearBy);

        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Log.d("mylog", "Added Markers");
        mMap.addMarker(customerPlace);
        mMap.addMarker(restaurantPlace);
        float zoomLevel = 16.0f; //This goes up to 21
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(customerPlace.getPosition(), zoomLevel));

        Circle circle = mMap.addCircle(new CircleOptions()
                .center(new LatLng(customerPlace.getPosition().latitude,customerPlace.getPosition().longitude))
                .radius(10000)
                .strokeColor(Color.parseColor("#2271cce7"))
                .fillColor(Color.parseColor("#2271cce7")));
    }

    private String getUrl(LatLng origin, LatLng dest, String directionMode) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        // Mode
        String mode = "mode=" + directionMode;
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + mode;
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + getString(R.string.map_api_key);
        return url;
    }

    @Override
    public void onTaskDone(Object... values) {
        if (currentPolyline != null)
            currentPolyline.remove();
        currentPolyline = mMap.addPolyline((PolylineOptions) values[0]);
    }

    private void readCustomerLocation() {
        mRefCustomerLocation = FirebaseDatabase.getInstance()
                .getReference("CustomersLocation").child(customerId);
        //Location of customer
        mRefCustomerLocation.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                LocationOfPlaces locationOfPlaces = dataSnapshot.getValue(LocationOfPlaces.class);
                latA = locationOfPlaces.getLat();
                lngA = locationOfPlaces.getLng();
                position();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // ...
            }
        });
    }

    private void readRestaurantLocation() {
        mRefRestaurantLocation = FirebaseDatabase.getInstance()
                .getReference("RestaurantsLocation").child(restaurantId);
        //Location of customer
        mRefRestaurantLocation.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                LocationOfPlaces locationOfPlaces = dataSnapshot.getValue(LocationOfPlaces.class);
                latB = locationOfPlaces.getLat();
                lngB = locationOfPlaces.getLng();
                position();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // ...
            }
        });
    }

    //.............
    //.............
    private void readRestaurantLocation1(final FirebaseCallback firebaseCallback) {
        //get reference of restaurant
        mRefRestaurantLocation = FirebaseDatabase.getInstance()
                .getReference("RestaurantsLocation").child(restaurantId);
        //Location of restaurant
        mRefRestaurantLocation.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                LocationOfPlaces locationOfPlaces = dataSnapshot.getValue(LocationOfPlaces.class);
                latB = locationOfPlaces.getLat();
                lngB = locationOfPlaces.getLng();

                firebaseCallback.onCallback(latB, lngB);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // ...
            }
        });
    }

    private interface FirebaseCallback {
        MarkerOptions onCallback(Double lat, Double lng);


    }

    //...............
    // private void getRestaurantDetails(LocationOfPlaces bikerlatlng) {
    //    DatabaseReference cRef = FirebaseDatabase.getInstance().getReference().child("RestaurantsLocation")
    //    .child(restaurantId);
    // final LocationOfPlaces dest_biker = bikerlatlng;
    // cRef.addListenerForSingleValueEvent(new ValueEventListener() {
    //  @Override
    //  public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
    // if (dataSnapshot.getValue() != null) {
    //  destLat = (Double) dataSnapshot.child("latitude").getValue();
    //  destLong = (Double) dataSnapshot.child("longitude").getValue();
    // rest_addr = dataSnapshot.child("streetAddress").getValue().toString();

    // String distance_rest = HaversineDistance.calculateDistance(myLocation.getLatitude(), myLocation.getLongitude(), destLat, destLong);
    //    LocationOfPlaces dest_rest = new LocationOfPlaces(destLat, destLong);

    //     mMap.addMarker(new MarkerOptions().position(dest_rest).title("Restaurant location").icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_restaurant)));
    //    getCustomerDetails(dest_biker, dest_rest);
    //   } else {
    //erasePloylines();
    //     }
    //    }

    //   @Override
    //    public void onCancelled(@NonNull DatabaseError databaseError) {
    //     }
    //  });

    // }
    //.................

}
