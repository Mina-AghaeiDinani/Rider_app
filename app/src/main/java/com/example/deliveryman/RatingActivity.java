package com.example.deliveryman;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

public class RatingActivity extends AppCompatActivity {
    private RatingBar rBar;
    private EditText tView;
    private Button btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating);
        //................
        rBar = (RatingBar) findViewById(R.id.ratingBar);
        tView = (EditText) findViewById(R.id.edtSuggestion);
        btn = (Button)findViewById(R.id.btnFeedback);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int noofstars = rBar.getNumStars();
                float getrating = rBar.getRating();
                Toast.makeText(RatingActivity.this,"Rating: "+getrating+"/"+noofstars,Toast.LENGTH_SHORT).show();
                //we have to multiply previous rate in database by number of people who
                // vote add this new value to them  then divided by total
                // tView.setText("Rating: "+getrating+"/"+noofstars);
            }
        });

    }
}
