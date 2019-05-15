package com.example.deliveryman;


import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class ApplyHelpActivity extends AppCompatActivity {
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    Intent intent = new Intent(ApplyHelpActivity.this, LoginActivity.class);
                    startActivity(intent);
                    return true;
                case R.id.navigation_apply_now:
                    intent = new Intent(ApplyHelpActivity.this, SignUpActivity.class);
                    startActivity(intent);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply_help);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigationApply);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        Button btnApplyNow=findViewById(R.id.btnApplyNow);
        btnApplyNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ApplyHelpActivity.this, SignUpActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
