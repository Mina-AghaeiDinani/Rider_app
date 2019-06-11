package com.example.deliveryman;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class RidersProfileActivity extends AppCompatActivity {
    //Navigation
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.sign_out:
                    firebaseAuth.signOut();
                    finish();
                    startActivity(new Intent(RidersProfileActivity.this, LoginActivity.class));
                    return true;

                case R.id.order:
                    Intent intent = new Intent(RidersProfileActivity.this, PendingCookingOrdersActivity.class);
                    startActivity(intent);
                    finish();
                    return true;
                case R.id.btn_home:
                    Intent intent2 = new Intent(RidersProfileActivity.this, MainActivity.class);
                    startActivity(intent2);
                    finish();
                    return true;


            }
            return false;
        }
    };
    //................
    TextView tvName, tvEmail, tvAddress, tvPhone, tvDescription;
    de.hdodenhof.circleimageview.CircleImageView imgProfile;
    private  RatingBar rate;
    //...
    FirebaseDatabase firebaseDatabase;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_riders_profile);
        //Rating bar
        rate = (RatingBar) findViewById(R.id.ratingBarRider);

        //Navigation
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.nav_profile);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        //***********start of code related to create a toolbar
        //we have to write codes in different functions
        Toolbar toolbar = findViewById(R.id.toolbarEdit);
        setSupportActionBar(toolbar);
        //end of code related to toolbar

        //
        tvName = findViewById(R.id.tvName);
        tvEmail = findViewById(R.id.tvMail);
        tvPhone = findViewById(R.id.tvPhone);
        tvDescription = findViewById(R.id.tvDescription);
        imgProfile = findViewById(R.id.imgPro);

        //....
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        //get reference
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("RidersProfile");
        databaseReference.child(firebaseAuth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                RiderProfile customersProfile = dataSnapshot.getValue(RiderProfile.class);
                tvName.setText(customersProfile.getName());
                tvPhone.setText(customersProfile.getPhone());
                tvEmail.setText(customersProfile.getEmail());
                //rate is added
                rate.setRating(customersProfile.getRating());
                tvDescription.setText(customersProfile.getShortdescription());
                Picasso.get()
                        .load(customersProfile.getImageUrl())
                        .placeholder(R.drawable.personal)
                        .fit()
                        .centerCrop()
                        .into(imgProfile);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(RidersProfileActivity.this, databaseError.getCode(), Toast.LENGTH_LONG).show();
            }
        });

    }

    //**************These codes belong to what toolbar is doing
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.editmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle toolbar item clicks here.
        int id = item.getItemId();
        //If Edit_button has been pressed go to the Edit activity
        if (id == R.id.btn_edit) {
            Intent i = new Intent(this, EditProfileActivity.class);
            startActivity(i);
        }
        return super.onOptionsItemSelected(item);
    }
    //End of code related to the toolbar

}
