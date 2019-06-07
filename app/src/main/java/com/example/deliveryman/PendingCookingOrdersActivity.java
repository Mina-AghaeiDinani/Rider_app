package com.example.deliveryman;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class PendingCookingOrdersActivity extends AppCompatActivity implements NotificationDialogActivity.NotificationDialogListener {
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
    private static FragmentManager fragmentManager;
    private String myRiderID;
    DatabaseReference databaseOrder;


    public void onStart() {
        fragmentManager = getSupportFragmentManager();
        super.onStart();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragmentManager = getSupportFragmentManager();
        setContentView(R.layout.activity_pending_cooking_orders);
        //.....
        firebaseAuth=FirebaseAuth.getInstance();
        myRiderID =  FirebaseAuth.getInstance().getCurrentUser().getUid();

        //LocalBroadcastManager.getInstance(this).registerReceiver(onNotice,
        //        new IntentFilter("message_received"));
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
                int i;
                for(i=0; i<cartInfos.size(); i++){
                    Log.d("INSIDEFOR", "orderRideId: " + cartInfos.get(i).getRiderId());
                    if(cartInfos.get(i).getRiderId()!=null) {
                        if (cartInfos.get(i).getRiderId().equals(myRiderID) && cartInfos.get(i).getStatus().equals("accepted") ) {
                            Log.d("INSIDEIF", "We are inside if, id: " + keys.get(i));
                            openNotificationDialog(keys.get(i));
                        }
                    }
                }
            }
        });


    }

    private BroadcastReceiver onNotice= new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("BROADCAST", "inside broadcast");
            String orderID = intent.getStringExtra("msg_order_id");
            openNotificationDialog(orderID);

        }
    };

    public void openNotificationDialog(String order_id){ //Method called when a new element is added from the database

        Intent notifiIntent = new Intent(this, OrderNotificationActivity.class);
        notifiIntent.putExtra("orderID", order_id);
        startActivity(notifiIntent);
        /*NotificationDialogActivity notificationDialog = new NotificationDialogActivity();
        Bundle notificationBundle = new Bundle();
        notificationBundle.putString("new_orderID",order_id);
        Log.d("BUNDLE", "bundle:"+ notificationBundle);

        notificationDialog.setArguments(notificationBundle);
        notificationDialog.show(fragmentManager, "Notification received");*/
    }

    @Override
    public void acceptOrder(String order) {
        /* First, we cancel all the notification pending since we opened the corresponding activity*/
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();

        databaseOrder = FirebaseDatabase.getInstance().getReference()
                .child("OrderInfo").child(order);

        databaseOrder.child("status").setValue("in course");

        //TODO: Open Activity showing the order

        Toast.makeText(this, "Course accepted", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void declineOrder(String order) {
        /* First, we cancel all the notification pending since we opened the corresponding activity*/
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();

        Toast.makeText(this, "Course declined", Toast.LENGTH_SHORT).show();


    }

    @Override
    public void onBackPressed() {
        Log.d("CDA", "onBackPressed Called");
        Intent setIntent = new Intent(this, MainActivity.class);

        startActivity(setIntent);
    }
}
