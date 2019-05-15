package com.example.deliveryman;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EditProfileActivity extends AppCompatActivity {
    private EditText txt_Name, txt_Mail, txt_Phone, txt_Description;
    //*********Define variables to read from camera and put in ImageView
    private Uri image_uri;
    private String current_image_uri;
    private de.hdodenhof.circleimageview.CircleImageView imgProfile;
    ImageButton btnSelectPhoto;
    private Bitmap imageBitmap;
    private static final int PICK_IMAGE = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 2;
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 3;
    // End
    private Button btn_Confirm;
    //...
    FirebaseDatabase firebaseDatabase;
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        //Assign variable to Id of each view
        setupUIViews();
        //read information from databse
        readInfo();
        //****************************** Camera

        btnSelectPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                selectImage();
            }
        });
        // End

        //Click on Register -here I called it btn_Confirm
        btn_Confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!validateName(txt_Name.getText().toString())) {
                    txt_Name.requestFocus();

                } else if (!validatePhone(txt_Phone.getText().toString())) {
                    txt_Phone.requestFocus();
                } else if (!validateEmail(txt_Mail.getText().toString())) {
                    txt_Mail.requestFocus();
                }else {
                    // if (image_uri==null){
                    // image_uri = Uri.parse("android.resource://com.example.customer/drawable/" + R.drawable.personal);
                    // }
                    updateInfo();
                }
            }
        });
        //End of click on button Confirm
    }

    //**** To make it more clean we assign variable to each view here
    private void setupUIViews() {
        txt_Name = findViewById(R.id.updateFullName);
        txt_Mail = findViewById(R.id.updateMail);
        txt_Phone = findViewById(R.id.updatePhone);
        txt_Description = findViewById(R.id.updateDescription);
        btn_Confirm = findViewById(R.id.btnUpdate);
        btnSelectPhoto = findViewById(R.id.updateSelectPhoto);
        imgProfile = findViewById(R.id.updateImgProfile);
    }

    //************Validate each view
    private boolean validateName(String Name) {
        int characters = Name.trim().length();
        if (characters > 20) {
            txt_Name.setError("Name is too long ( maximum is 20)");
            return false;
        } else if (characters < 1) {
            txt_Name.setError("Name can not be empty");
            return false;
        } else {
            txt_Name.setError(null);
            return true;
        }
    }


    //*************Validate Phone
    private boolean validatePhone(String Phone) {

        int characters = Phone.trim().length();
        if (characters < 1) {
            txt_Phone.setError("Phone can not be empty");
            return false;
        } else if (characters > 15 || characters < 10) {
            txt_Phone.setError("Invalid phone number");
            return false;
        } else {
            String phonePatteren = "^([0-9\\+]|\\(\\d{1,3}\\))[0-9\\-\\. ]{3,15}$";
            Pattern pattern = Pattern.compile(phonePatteren);
            Matcher matcher = pattern.matcher(Phone);
            if (matcher.matches()) {
                return true;
            } else {
                txt_Phone.setError("Invalid phone number");
                return false;
            }
        }
    }

    //...........
    //*******Validate Email
    private boolean validateEmail(String Email) {
        int characters = Email.trim().length();
        if (characters < 1) {
            return true;
        } else if (characters > 25) {
            txt_Mail.setError("Invalid Email address(it is too long)");
            return false;
        } else {
            String emailPattern = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
            Pattern pattern = Pattern.compile(emailPattern);
            Matcher matcher = pattern.matcher(Email);
            if (matcher.matches()) {
                return true;
            } else {
                txt_Mail.setError("Invalid Email address ");
                return false;
            }
        }
    }

    // *****************Camera
    // *****************This part create dialog box
    private void selectImage() {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
            }
        }
        final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Delete"};
        AlertDialog.Builder builder = new AlertDialog.Builder(EditProfileActivity.this);

        builder.setTitle("Add Photo!");

        builder.setItems(options, new DialogInterface.OnClickListener() {

            @Override

            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Take Photo")) {
                    openCamera();
                } else if (options[item].equals("Choose from Gallery")) {
                    openGallery();
                } else if (options[item].equals("Delete")) {
                    int drawableResource = R.drawable.personal;
                    Drawable d = getResources().getDrawable(drawableResource);
                    imgProfile.setImageDrawable(d);

                    dialog.dismiss();
                }
            }
        });


        builder.show();
    }

    //..........................
    //*****Open Gallery
    public void openGallery() {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }
    //.....................

    //***********Open Camera
    public void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    //........................
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {
            image_uri = data.getData();
            imgProfile.setImageURI(image_uri);
        } else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
            image_uri = getImageUri(this, imageBitmap);
            imgProfile.setImageURI(image_uri);
        }
    }

    //..................
    //*****
    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    //..................
    //******* We want when we rotate screen image does not change
    //We use these 2 below fuctions
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (image_uri != null) {
            outState.putString("image", image_uri.toString());
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        String image = savedInstanceState.getString("image", ""); // Value that was saved will restore to variable
        image_uri = Uri.parse(image);
        imgProfile.setImageURI(image_uri);
    }
    private void readInfo(){
        //....
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseDatabase= FirebaseDatabase.getInstance();
        //get reference
        databaseReference = FirebaseDatabase.getInstance().getReference("RidersProfile");
        databaseReference.child(firebaseAuth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                RiderProfile riderProfile=dataSnapshot.getValue(RiderProfile.class);
                txt_Name.setText(riderProfile.getName());
                txt_Phone.setText(riderProfile.getPhone());
                txt_Mail.setText(riderProfile.getEmail());
                txt_Description.setText(riderProfile.getShortdescription());
                current_image_uri=riderProfile.getImageUrl();
                Picasso.get()
                        .load(riderProfile.getImageUrl())
                        .placeholder(R.drawable.personal)
                        .fit()
                        .centerCrop()
                        .into(imgProfile);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(EditProfileActivity.this,databaseError.getCode(),Toast.LENGTH_LONG).show();
            }
        });
        Button btn_Back=findViewById(R.id.btnBack);
        btn_Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(EditProfileActivity.this,RidersProfileActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    //.............
    private void updateInfo(){
        final String name = txt_Name.getText().toString().trim();
        String email = txt_Mail.getText().toString().trim();
        String phone = txt_Phone.getText().toString().trim();
        String description = txt_Description.getText().toString().trim();
        RiderProfile riderProfile = new RiderProfile();
        riderProfile.setName(name);
        riderProfile.setEmail(email);
        riderProfile.setPhone(phone);
        riderProfile.setShortdescription(description);
        if (image_uri!=null){
            riderProfile.setImageUrl(String.valueOf(image_uri));
        }
        else riderProfile.setImageUrl(current_image_uri);
        databaseReference.child(firebaseAuth.getUid()).setValue(riderProfile);
        finish();


    }
}
