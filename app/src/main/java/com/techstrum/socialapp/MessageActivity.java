package com.techstrum.socialapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mikhaellopez.circularimageview.CircularImageView;

public class MessageActivity extends AppCompatActivity {
    CircularImageView message_profilePic;
    TextView username;
    FirebaseUser fuser;
    DatabaseReference dBaseRef;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        Toolbar toolbar=findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        message_profilePic=findViewById(R.id.message_profilePic);
        username=findViewById(R.id.username);
        intent=getIntent();

        fuser= FirebaseAuth.getInstance().getCurrentUser();
        dBaseRef= FirebaseDatabase.getInstance().getReference("Users") ;   }
}