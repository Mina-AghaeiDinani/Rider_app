package com.example.deliveryman;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class PendingCookingOrdersActivity extends AppCompatActivity {
    //Navigation
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.sign_out:
                    firebaseAuth.signOut();
                    finish();
                    startActivity(new Intent(PendingCookingOrdersActivity.this,LoginActivity.class));
                    return true;
                case R.id.profile:
                    startActivity(new Intent(PendingCookingOrdersActivity.this,RidersProfileActivity.class));
                    finish();
                    return true;
                case R.id.menu:
                    Intent  intent = new Intent(PendingCookingOrdersActivity.this, MainActivity.class);
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_cooking_orders);
        //.....
        firebaseAuth=FirebaseAuth.getInstance();
        //Navigation
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.nav_PendingOrders);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
//.....................
        //Initiate Recycler view for readind data
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerPendingOrders);
      new FirebaseDatabasePendingOrders().readOrders(new FirebaseDatabasePendingOrders.DataStatus() {
            @Override
            public void DataIsLoaded(List<CartInfo> cartInfos, List<String> keys) {
                findViewById(R.id.loading_pb11).setVisibility(View.GONE);
                new RecyclerView_Orders().setConfig(mRecyclerView, PendingCookingOrdersActivity.this, cartInfos, keys);

            }
        });


    }
}
