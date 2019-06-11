package com.example.deliveryman;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.deliveryman.RiderProfile;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity {
    private StorageReference mStorageRef;
    private StorageTask mUploadTask;
    private EditText txt_Name, txt_Mail, txt_Phone, txt_Description, txt_Password;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    //*********Define variables to read from camera and put in ImageView
    private Uri image_uri;
    private de.hdodenhof.circleimageview.CircleImageView imgProfile;
    ImageButton btnSelectPhoto;
    private Bitmap imageBitmap;
    private static final int PICK_IMAGE = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 2;
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 3;
    // End
    private Button btn_Confirm;
    private TextView tvRegisterd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        //Assign variable to Id of each view
        setupUIViews();
        //****************************** Camera

        btnSelectPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                selectImage();
            }
        });
        // End


        progressBar.setVisibility(View.GONE);
        //Authentication
        mAuth = FirebaseAuth.getInstance();


        //***Checking validation of Name
        txt_Name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validateName(txt_Name.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        //Validate email
        txt_Mail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validateEmail(txt_Mail.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        //********Check validation of phone
        txt_Phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validatePhone(txt_Phone.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        //********Check validation of phone
        txt_Password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validatePassword(txt_Password.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        //Click on Register button
        btn_Confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!validateName(txt_Name.getText().toString())) {
                    txt_Name.requestFocus();

                } else if (!validatePhone(txt_Phone.getText().toString())) {
                    txt_Phone.requestFocus();
                } else if (!validateEmail(txt_Mail.getText().toString())) {
                    txt_Mail.requestFocus();
                } else if (!validatePassword(txt_Password.getText().toString())) {
                    txt_Password.requestFocus();
                } else {
                    if (image_uri == null) {
                        image_uri = Uri.parse("android.resource://com.example.customer/drawable/" + R.drawable.personal);
                    }
                    registerUser();
                }
            }
        });
        //End of click on button Confirm
        //Click on Already registered
        tvRegisterd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                finish();
            }
        });
    }

    //**** To make it more clean we assign variable to each view here
    private void setupUIViews() {
        txt_Name = findViewById(R.id.edtFullName);
        txt_Password = findViewById(R.id.edtPassword);
        txt_Mail = findViewById(R.id.etMail);
        txt_Phone = findViewById(R.id.etPhone);
        txt_Description = findViewById(R.id.etDescription);
        progressBar = findViewById(R.id.progressbar);
        btn_Confirm = findViewById(R.id.btnRegister);
        tvRegisterd = findViewById(R.id.tvRegistered);
        btnSelectPhoto = findViewById(R.id.btnSelectPhoto);
        imgProfile = findViewById(R.id.imgProfile);
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

    private boolean validatePassword(String Password) {
        int characters = Password.trim().length();
        if (characters < 1) {
            txt_Password.setError("You must set a password");
            return false;
        } else if (characters < 7) {
            txt_Password.setError("Password is too weak");
            return false;
        } else {
            txt_Password.setError(null);
            return true;
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
        AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);

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

    //....................................
    @Override
    protected void onStart() {
        super.onStart();

        if (mAuth.getCurrentUser() != null) {
            //handle the already login user
        }
    }

    // Saving information in real time database and doing authentication
    private void registerUser() {
        final String name = txt_Name.getText().toString().trim();
        final String email = txt_Mail.getText().toString().trim();
        final String password = txt_Password.getText().toString().trim();
        final String phone = txt_Phone.getText().toString().trim();

        final String description = txt_Description.getText().toString().trim();
        progressBar.setVisibility(View.VISIBLE);
        btn_Confirm.setEnabled(false);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            addInfo();
                            /*RiderProfile riderProfile = new RiderProfile();
                            riderProfile.setName(name);
                            riderProfile.setEmail(email);
                            riderProfile.setPhone(phone);
                            riderProfile.setShortdescription(description);
                            riderProfile.setImageUrl(String.valueOf(image_uri));
                            FirebaseDatabase.getInstance().getReference("RidersProfile")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(riderProfile).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful()) {
                                        Toast.makeText(SignUpActivity.this, "Registration has been done...", Toast.LENGTH_LONG).show();
                                        startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                                        finish();


                                    } else {
                                        // display a failure message
                                    }
                                }
                            });*/

                        } else {
                            //If email has been already registered
                            if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                Toast.makeText(SignUpActivity.this, "You are already registered!", Toast.LENGTH_LONG).show();
                                startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                                finish();
                            } else {
                                Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });
    }

    private void sendEmailVerification() {
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser != null) {
            firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {

                        Toast.makeText(SignUpActivity.this, "Verification Email has sent...", Toast.LENGTH_LONG).show();
                        mAuth.signOut();
                        startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                        finish();
                    } else {
                        Toast.makeText(SignUpActivity.this, "Verification Email hasn't been sent...", Toast.LENGTH_LONG).show();

                    }
                }
            });
        }
    }

    private void sendUserData() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        //will save it under Uid
        DatabaseReference myRef = firebaseDatabase.getReference(mAuth.getUid());
        RiderProfile riderProfile = new RiderProfile(txt_Name.getText().toString().trim(), txt_Phone.getText().toString().trim(), txt_Mail.getText().toString().trim(), txt_Description.getText().toString().trim(), String.valueOf(image_uri),0,0);

        myRef.setValue(riderProfile);

    }
    private String getExtension(Uri uri) {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(cr.getType(uri));
    }
    private void addInfo() {
        Toast.makeText(SignUpActivity.this, "Please wait...", Toast.LENGTH_LONG).show();
        mStorageRef = FirebaseStorage.getInstance().getReference("RidersProfile");
        if (image_uri != null) {
            final StorageReference fileReferences = mStorageRef.child(System.currentTimeMillis() + "." + getExtension(image_uri));
            mUploadTask = fileReferences.putFile(image_uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    progressBar.setProgress(0);
                                }
                            }, 500);
                            fileReferences.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    RiderProfile riderProfile = new RiderProfile(
                                            txt_Name.getText().toString().trim(),
                                            txt_Phone.getText().toString().trim(),
                                            txt_Mail.getText().toString().trim(),
                                            txt_Description.getText().toString().trim(),
                                            uri.toString(),0,0);
                                    FirebaseDatabase.getInstance().getReference("RidersProfile")
                                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                            .setValue(riderProfile);
                                    startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                                     finish();



                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(SignUpActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            progressBar.setProgress((int) progress);
                        }
                    });

        } else
            Toast.makeText(SignUpActivity.this, "no photo has been selected", Toast.LENGTH_LONG).show();


    }

}
