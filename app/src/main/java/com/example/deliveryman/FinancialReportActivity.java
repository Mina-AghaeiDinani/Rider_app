package com.example.deliveryman;

import android.content.Intent;
import android.hardware.camera2.TotalCaptureResult;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class FinancialReportActivity extends AppCompatActivity {
    //Navigation
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.sign_out:
                    firebaseAuth.signOut();
                    finish();
                    startActivity(new Intent(FinancialReportActivity.this, LoginActivity.class));
                    return true;
                case R.id.profile:
                    startActivity(new Intent(FinancialReportActivity.this, RidersProfileActivity.class));
                    finish();
                    return true;
                case R.id.menu:
                    Intent intent = new Intent(FinancialReportActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    return true;
            }
            return false;
        }
    };
    //................
    private RecyclerView mRecyclerView;
    FirebaseAuth firebaseAuth;
    private static Double totalDistance;
    private static String restaurantId, customerId, riderId;
    //Locations
    private static Double latA, lngA, latB, lngB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_financial_report);
        //.....
        firebaseAuth = FirebaseAuth.getInstance();
        riderId = firebaseAuth.getCurrentUser().getUid();
        // Toast.makeText(FinancialReportActivity.this,riderId,Toast.LENGTH_SHORT).show();
        //Navigation
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.nav_Financial);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        //Initiate Recycler view for readind data
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerFinancial);
        new FirebaseDatabaseFinancial().readOrders(new FirebaseDatabaseFinancial.DataStatus() {
            @Override
            public void DataIsLoaded(List<CartInfo> cartInfos, List<String> keys) {
                findViewById(R.id.loading_financial).setVisibility(View.GONE);
                new RecyclerView_Financial().setConfig(mRecyclerView, FinancialReportActivity.this, cartInfos, keys);

            }
        }, riderId);
        //Compute total distance and total fee

        //get references
        DatabaseReference mRefOrderInfo = FirebaseDatabase.getInstance()
                .getReference("OrderInfo");

        totalDistance = 0.0;
        mRefOrderInfo.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot keyNode : dataSnapshot.getChildren()) {
                    CartInfo cart = keyNode.getValue(CartInfo.class);
                    Toast.makeText(FinancialReportActivity.this, riderId, Toast.LENGTH_SHORT).show();

                    if (cart.getRiderId() != null && cart.getRiderId().contains(riderId)) {

                        customerId = cart.getCustomerId();
                        restaurantId = cart.getRestaurantId();
                        //...................
                        //get references to get Latitude and Longitude of customer
                        DatabaseReference mRefCustomerLocation = FirebaseDatabase.getInstance()
                                .getReference("CustomersLocation").child(customerId);

                        //Location of customer
                        mRefCustomerLocation.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                LocationOfPlaces locationOfPlaces = dataSnapshot.getValue(LocationOfPlaces.class);
                                latA = locationOfPlaces.getLat();
                                lngA = locationOfPlaces.getLng();
                                //get reference of restaurant
                                DatabaseReference mRefRestaurantLocation = FirebaseDatabase.getInstance()
                                        .getReference("RestaurantsLocation").child(restaurantId);
                                //Location of restaurant
                                mRefRestaurantLocation.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        LocationOfPlaces locationOfPlaces = dataSnapshot.getValue(LocationOfPlaces.class);
                                        latB = locationOfPlaces.getLat();
                                        lngB = locationOfPlaces.getLng();
                                        //Compute distance

                                        double earthRadius = 6371;
                                        double latDiff = Math.toRadians(latB - latA);
                                        double lngDiff = Math.toRadians(lngB - lngA);
                                        double a = Math.sin(latDiff / 2) * Math.sin(latDiff / 2) +
                                                Math.cos(Math.toRadians(latA)) *
                                                        Math.cos(Math.toRadians(latB)) * Math.sin(lngDiff / 2) *
                                                        Math.sin(lngDiff / 2);
                                        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
                                        double distance = earthRadius * c;

                                        //Assign to field
                                        totalDistance = totalDistance + distance;

                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        // ...
                                    }
                                });

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                // ...
                            }
                        });
                        //...................
                    }
                }
                //first get id of views
                TextView tvDistance = findViewById(R.id.traveledDistance);
                TextView tvFee = findViewById(R.id.earnedFee);
                tvDistance.setText(String.format("%.2f", totalDistance) + " Km");
                Double Fee = totalDistance * 0.5;
                tvFee.setText(String.format("%.2f", Fee) + "$");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
