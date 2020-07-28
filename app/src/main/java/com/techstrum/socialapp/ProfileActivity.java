package com.techstrum.socialapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.mikhaellopez.circularimageview.CircularImageView;

import java.io.File;
import java.io.IOException;

public class ProfileActivity extends AppCompatActivity {
    TextView profile_name;
    TextView profile_email;
    TextView profile_phone;
    TextView profile_gender;
    TextView profile_date;
    TextView profile_address;
    TextView profile_city;
    TextView profile_country;
    TextView profile_pinCode;
    ConstraintLayout cons;
    CircularImageView profile_picture;
    FirebaseAuth fAuth;
    FirebaseUser user;
    FirebaseDatabase fBase;
    FirebaseStorage storage;
    StorageReference storageRef;
    StorageReference downloadRef;
    DatabaseReference myRef;
    File localFile;
    User user1;
    ProgressBar profile_progressBar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        profile_name=findViewById(R.id.profile_name);
        profile_email=findViewById(R.id.profile_email);
        profile_phone=findViewById(R.id.profile_phone);
        profile_gender=findViewById(R.id.profile_gender);
        profile_date=findViewById(R.id.profile_date);
        profile_address=findViewById(R.id.profile_address);
        profile_city=findViewById(R.id.profile_city);
        profile_country=findViewById(R.id.profile_country);
        profile_pinCode=findViewById(R.id.profile_pinCode);
        cons=findViewById(R.id.cons);
        profile_picture=findViewById(R.id.profile_picture);
        profile_progressBar=findViewById(R.id.profile_progressBar);
        user = FirebaseAuth.getInstance().getCurrentUser();
        fBase = FirebaseDatabase.getInstance();
        fAuth=FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        storageRef=storage.getReference();


        if(user!=null){
            profile_progressBar.setVisibility(View.VISIBLE);
            //Signed In
            cons.setAlpha(1);
            myRef = fBase.getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
            downloadRef=storageRef.child("Profile Pictures").child("Feed Images").child(fAuth.getCurrentUser().getUid()+"_800x800.jpg");
            try {
                localFile = File.createTempFile("ProfileImage",".jpg");
            } catch (IOException e) {
                e.printStackTrace();
            }
            downloadRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    String tempPath = localFile.getPath();
                    Bitmap bitmap= BitmapFactory.decodeFile(tempPath);
                    profile_picture.setImageBitmap(bitmap);
                    profile_progressBar.setVisibility(View.INVISIBLE);
                    //downloded
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {profile_progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(ProfileActivity.this, "fail"+e.getMessage(), Toast.LENGTH_SHORT).show();
                    //failed

                }
            });


            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {//data found

                    user1 =dataSnapshot.getValue(User.class);
                    profile_name.setText(user1.name);
                    profile_email.setText(user1.email);
                    profile_phone.setText(user1.phone);
                    profile_date.setText(user1.date);
                    profile_address.setText(user1.address);
                    profile_city.setText(user1.city);
                    profile_country.setText(user1.country);
                    profile_gender.setText(user1.gender);
                    profile_pinCode.setText(user1.pinCode);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    //data not Found

                }
            });

        }
        else{ //not signed in
            cons.setAlpha(0);
            Toast.makeText(this, "First Login OR Sign UP", Toast.LENGTH_LONG).show();
            startActivity(new Intent(getApplicationContext(),LoginActivity.class));
            finish();
        }

    }

}