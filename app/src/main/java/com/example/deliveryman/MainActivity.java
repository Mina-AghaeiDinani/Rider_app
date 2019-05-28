package com.example.deliveryman;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.example.deliveryman.helper.FirebaseHelper;

import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    //Define variables to show image and name in header
    de.hdodenhof.circleimageview.CircleImageView imgProfileNav;
    de.hdodenhof.circleimageview.CircleImageView imgGetReady;
    de.hdodenhof.circleimageview.CircleImageView imgRest;
    de.hdodenhof.circleimageview.CircleImageView imgPendingOrders;
    de.hdodenhof.circleimageview.CircleImageView imgFinancialReports;
    DatabaseReference databaseRefLocation;
    TextView txtFullNameNav;
    //to do logout we need it
    private FirebaseAuth firebaseAuth;
    private RecyclerView mRecyclerView;
    private FirebaseHelper firebaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Easy Eat");
        setSupportActionBar(toolbar);
        // ...define firebase Auth
        firebaseAuth = FirebaseAuth.getInstance();
        databaseRefLocation = FirebaseDatabase.getInstance().getReference("RidersLocation")
                .child(firebaseAuth.getCurrentUser().getUid());
        //going to get ready
        imgGetReady=findViewById(R.id.imgGetReady);
        final TextView tvGetReady=findViewById(R.id.tvReady);
        imgGetReady.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgGetReady.setEnabled(false);
                tvGetReady.setText("You are enable");
                imgRest.setEnabled(true);
               // startActivityForResult(new Intent(MainActivity.this, MapActivity.class));
                startActivity(new Intent(MainActivity.this, MapActivity.class));
            }
        });
        //sign out to take a rest
        imgRest=findViewById(R.id.imgRest);
        imgRest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvGetReady.setText("Ready to work");

                //firebaseHelper.deleteDriver();
                imgGetReady.setEnabled(true);
                Toast.makeText(MainActivity.this,"Have a good day",Toast.LENGTH_LONG).show();
                databaseRefLocation.removeValue();
                firebaseAuth.signOut();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
            }
        });
        // Pending Orders
        imgPendingOrders=findViewById(R.id.imgOrders);
        imgPendingOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,PendingCookingOrdersActivity.class);
                startActivity(intent);
                finish();
            }
        });
        //..............
        imgFinancialReports=findViewById(R.id.imgReport);
        imgFinancialReports.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,OrderNotificationActivity.class);
                startActivity(intent);
                finish();

            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);


        //Put name and Image in header view
        View headView = navigationView.getHeaderView(0);
        imgProfileNav = headView.findViewById(R.id.imgProfileNav);
        txtFullNameNav = headView.findViewById(R.id.txtFullNameNav);
        //getting Image from profile and name
        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("RidersProfile");
        databaseReference1.child(firebaseAuth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                RiderProfile riderProfile = dataSnapshot.getValue(RiderProfile.class);
                txtFullNameNav.setText(riderProfile.getName());
                Picasso.get()
                        .load(riderProfile.getImageUrl())
                        .placeholder(R.drawable.personal)
                        .fit()
                        .centerCrop()
                        .into(imgProfileNav);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, databaseError.getCode(), Toast.LENGTH_LONG).show();
            }
        });

        //...............................

        //

        //Initiate Receycler view
        //...............................

        //

        //Initiate Receycler view
      /*  mRecyclerView = (RecyclerView) findViewById(R.id.recycler_orders);
        new FirebaseDatabaseHelper().readFoods(new FirebaseDatabaseHelper.DataStatus() {
            @Override
            public void DataIsLoaded(List<Cart> carts, List<String> keys) {
                findViewById(R.id.loading_foods_pb).setVisibility(View.GONE);
                new RecyclerView_Config().setConfig(mRecyclerView, MainActivity.this, carts, keys);
            }

        });*/
        //
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_log_out) {
            // Handle the log out
            firebaseAuth.signOut();
            finish();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));

        }
        if (id == R.id.nav_profile) {

            startActivity(new Intent(MainActivity.this, RidersProfileActivity.class));

        }



        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
