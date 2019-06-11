package com.example.deliveryman;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RatingActivity extends AppCompatActivity {
    private RatingBar rBar;
    private EditText tView;
    private Button btn;
    private String restID;
    private TextView ratingTextView;
    DatabaseReference databaseOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating);
        //................
        rBar = (RatingBar) findViewById(R.id.ratingBar);
        tView = (EditText) findViewById(R.id.edtSuggestion);
        btn = (Button)findViewById(R.id.btnFeedback);
        ratingTextView = (TextView) findViewById(R.id.ratingTextView);

        String restNname = getIntent().getStringExtra("RestaurantName");
        restID = getIntent().getStringExtra("RestaurantID");
        ratingTextView.setText("Leave your opinion on "+restNname);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int noofstars = rBar.getNumStars();
                final float getrating = rBar.getRating();
                Toast.makeText(RatingActivity.this,"Rating: "+getrating+"/"+noofstars,Toast.LENGTH_SHORT).show();
                databaseOrder = FirebaseDatabase.getInstance().getReference()
                        .child("Restaurants").child(restID);

                databaseOrder.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        RestaurantsProfile restaurantsProfile = dataSnapshot.getValue(RestaurantsProfile.class);
                        if(restaurantsProfile.getNumCustomerOpinion()!=0){
                            int numCustomerOpinion = restaurantsProfile.getNumCustomerOpinion();
                            int newnumCustomerOpinion = numCustomerOpinion+1;
                            float newrating = restaurantsProfile.getRating();
                            newrating = (newrating*numCustomerOpinion + getrating)/newnumCustomerOpinion;
                            databaseOrder.child("numCustomerOpinion").setValue(numCustomerOpinion);
                            databaseOrder.child("rating").setValue(newrating);
                        }else{
                            databaseOrder.child("NumCustomerOpinion").setValue(1);
                            databaseOrder.child("rating").setValue(getrating);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                //we have to multiply previous rate in database by number of people who
                // vote add this new value to them  then divided by total
                // tView.setText("Rating: "+getrating+"/"+noofstars);
                Intent intent = new Intent(RatingActivity.this, PendingCookingOrdersActivity.class);
                startActivity(intent);
            }
        });

    }
}
