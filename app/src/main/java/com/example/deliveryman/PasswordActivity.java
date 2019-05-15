package com.example.deliveryman;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class PasswordActivity extends AppCompatActivity {
    private EditText edtResetPassword;
    private Button btnResetPassword;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);

        ///
        edtResetPassword = findViewById(R.id.edtResetPassword);
        btnResetPassword = findViewById(R.id.btnResetPassword);
        firebaseAuth = FirebaseAuth.getInstance();

        //click on reset
        btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userEmail = edtResetPassword.getText().toString().trim();
                if (userEmail.isEmpty()) {
                    Toast.makeText(PasswordActivity.this, "Please enter your registered Email", Toast.LENGTH_SHORT).show();
                } else {
                    firebaseAuth.sendPasswordResetEmail(userEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(PasswordActivity.this, "Password reset email has been sent...", Toast.LENGTH_LONG).show();
                                startActivity(new Intent(PasswordActivity.this, LoginActivity.class));
                                finish();
                            }
                            else{
                                Toast.makeText(PasswordActivity.this, "Error in sending password reset email!", Toast.LENGTH_LONG).show();

                            }
                        }
                    });
                }
            }
        });


    }
}
