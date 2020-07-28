package com.techstrum.socialapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class UpdateActivity extends AppCompatActivity {
    EditText update_name;
    EditText update_phone;
    EditText update_date;
    Spinner gender_spinner;
    EditText update_address;
    EditText update_city;
    EditText update_country;
    EditText update_pinCode;
    CircularImageView update_profilePic;
    FirebaseUser user;
    FirebaseDatabase fDatabase;
    FirebaseAuth fAuth;
    User user1;
    DatabaseReference myRef;
    ConstraintLayout constraintLayout;
    TextView update_verify;
    Button button_verify;
    FirebaseStorage storage;
    Button buttonUpload;
    private Uri selectedImage;
    StorageReference storageRef;
    StorageReference imageRef;
    File localFile= null;
    ProgressBar update_progressBar;
    ArrayAdapter<CharSequence> genderAdapter;
     Calendar myCalendar;

    //mCropImageUri




    public void verify_email(View view){
        //on verify button click
        user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                //sucess
                Toast.makeText(UpdateActivity.this, "Verification Link Sent", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //fail
                Toast.makeText(UpdateActivity.this, "Verification Link Not Sent", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void updateLabel() {
        String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        update_date.setText(sdf.format(myCalendar.getTime()));
    }
    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        }


    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        update_profilePic=findViewById(R.id.update_picturePic);
        update_name=findViewById(R.id.update_name);
        update_phone=findViewById(R.id.update_phone);
        update_date=findViewById(R.id.update_date);
        gender_spinner=(Spinner)findViewById(R.id.gender_spinner);
        genderAdapter=ArrayAdapter.createFromResource(this,R.array.Gender_Names,android.R.layout.simple_spinner_item);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        update_address=findViewById(R.id.update_address);
        update_city=findViewById(R.id.update_city);
        update_country=findViewById(R.id.update_country);
        update_pinCode=findViewById(R.id.update_pinCode);
        constraintLayout=findViewById(R.id.constraint);
        update_verify=findViewById(R.id.update_verify);
        button_verify=findViewById(R.id.button_verify);
        myCalendar = Calendar.getInstance();
        update_progressBar=findViewById(R.id.update_progressBar);
        gender_spinner.setAdapter(genderAdapter);
        user = FirebaseAuth.getInstance().getCurrentUser();
        fDatabase = FirebaseDatabase.getInstance();
        fAuth=FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        StorageReference downloadRef;
        update_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(UpdateActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        if(user!=null){//user Signed in
            update_progressBar.setVisibility(View.VISIBLE);
            constraintLayout.setAlpha(1);
            downloadRef=storageRef.child("Profile Pictures").child(FirebaseAuth.getInstance().getCurrentUser().getUid()+".jpg");
            myRef = fDatabase.getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());//Reference
            imageRef=storageRef.child("Profile Pictures").child(FirebaseAuth.getInstance().getCurrentUser().getUid()+".jpg");


            try {
                localFile = File.createTempFile("ProfileImage",".jpg");
            } catch (IOException e) {
                e.printStackTrace();
            }
            downloadRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                  String tempPath = localFile.getPath();
                  Bitmap bitmap=BitmapFactory.decodeFile(tempPath);
                  update_profilePic.setImageBitmap(bitmap);
                    update_progressBar.setVisibility(View.INVISIBLE);
                    //downloded
               }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    update_progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(UpdateActivity.this, "Cant Upload", Toast.LENGTH_SHORT).show();
                 //failed

                    }
            });
            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {//data found

                    user1 =dataSnapshot.getValue(User.class);
                    update_name.setText(user1.name);
                    update_phone.setText(user1.phone);
                    update_date.setText(user1.date);
                    update_address.setText(user1.address);
                    update_city.setText(user1.city);
                    update_country.setText(user1.country);
                    if(user1.gender!=null){
                        int spinnerPos=genderAdapter.getPosition(user1.gender);
                        gender_spinner.setSelection(spinnerPos);
                    }
                   // update_gender.setText(user1.gender);
                    update_pinCode.setText(user1.pinCode);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {//data not Found

                }
            });
            if(!user.isEmailVerified()){
                update_verify.setVisibility(View.VISIBLE);
                button_verify.setVisibility(View.VISIBLE);
            }


        }
        else{ //not signed in
            constraintLayout.setAlpha(0);
            Toast.makeText(this, "First Login OR Sign UP", Toast.LENGTH_LONG).show();
            startActivity(new Intent(getApplicationContext(),LoginActivity.class));
            finish();
        }



        buttonUpload=findViewById(R.id.buttonUpload);
        buttonUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSelectImageClick(v);
            }
        });

    }
    public void onSelectImageClick(View view) {//new
        CropImage.startPickImageActivity(this);
    }
public void updateSubmit (View view){
        user1.name=update_name.getText().toString();
        user1.phone=update_phone.getText().toString();
       // user1.date=update_date.getText().toString();
       // user1.address=update_address.getText().toString();
        user1.city=update_city.getText().toString();
        user1.country=update_country.getText().toString();
        //user1.pinCode=update_pinCode.getText().toString();
        user1.gender=gender_spinner.getSelectedItem().toString();
       // user1.gender=update_gender.getText().toString();

    FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(user1).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
        public void onComplete(@NonNull Task<Void> task) {
            if(task.isSuccessful()){
               Toast.makeText(UpdateActivity.this, " SAVED ", Toast.LENGTH_SHORT).show();
               //startActivity(new Intent(getApplicationContext(),MainActivity.class));
               // finish();
            }
            else{
                Toast.makeText(UpdateActivity.this, "NOT SAVED TO DATABASE", Toast.LENGTH_SHORT).show();
            }
        }
    });

}
public void changePassword(View view){
    final EditText resetMail=new EditText(view.getContext());
    AlertDialog.Builder passwordResetDialog =new AlertDialog.Builder(view.getContext());
    passwordResetDialog.setTitle("Reset Password");
    passwordResetDialog.setMessage("Enter Your Email To Receive Reset Link");
    passwordResetDialog.setView(resetMail);
    passwordResetDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            //yes
            String mail= resetMail.getText().toString();
            fAuth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(UpdateActivity.this, "Reset Link Sent", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(),MainActivity.class));
                    finish();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(UpdateActivity.this, "ERROR! Reset Link Not Sent"+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    });
    passwordResetDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            //no
        }
    });
    passwordResetDialog.create().show();
}

    @Override
    @SuppressLint("NewApi")
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // handle result of pick image chooser
        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri imageUri = CropImage.getPickImageResultUri(this, data);

            // For API >= 23 we need to check specifically that we have permissions to read external storage.
            if (CropImage.isReadExternalStoragePermissionsRequired(this, imageUri)) {
                // request permissions and handle the result in onRequestPermissionsResult()
                selectedImage = imageUri;
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
            } else {
                // no permissions required or already grunted, can start crop image activity
                startCropImageActivity(imageUri);
            }
        }

        // handle result of CropImageActivity
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {//crop succsedful
                (update_profilePic).setImageURI(result.getUri());
                UploadTask uploadTask=imageRef.putFile(result.getUri());//uploading image
                uploadTask.addOnFailureListener(new OnFailureListener() {//cheking Upload
                    @Override
                    public void onFailure(@NonNull Exception e) {

                       //Not Uploaded

                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(UpdateActivity.this, "Uploded", Toast.LENGTH_SHORT).show();
                        //Successful Upload
                    }
                });


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Toast.makeText(this, "Cropping failed: " + result.getError(), Toast.LENGTH_LONG).show();
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (selectedImage != null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // required permissions granted, start crop image activity
            startCropImageActivity(selectedImage);
        } else {
            Toast.makeText(this, "Cancelling, required permissions are not granted", Toast.LENGTH_LONG).show();
        }
    }
    private void startCropImageActivity(Uri imageUri) {
        CropImage.activity(imageUri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setMultiTouchEnabled(true).setAspectRatio(1,1)
                .start(this);
    }




}


