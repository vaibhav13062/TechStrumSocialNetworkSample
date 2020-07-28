package com.techstrum.socialapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MyFeedActivity extends AppCompatActivity {
    ListView myFeed_listView;
    ArrayList<StorageReference> feedUsers=new ArrayList<StorageReference>();
    List<Feed> feedList;
    ArrayList<String> feedName=new ArrayList<String>();
    FirebaseUser user;
    FirebaseStorage storage;
    StorageReference storageRef;
    StorageReference feedRef;
    FirebaseDatabase database ;
    SwipeRefreshLayout swiperefresh;
    FloatingActionButton fab1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_feed);
        myFeed_listView=findViewById(R.id.myFeed_listView);
        feedList = new ArrayList<>();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        swiperefresh=findViewById(R.id.swiperefresh);

       // fab1 = findViewById(R.id.fab1); //floating Action Button on main activity
        user = FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null){
            FeedUser();

             //adapter= new MyListAdapter(this,R.layout.feed_layout, feedList);
           //  myFeed_listView.setAdapter(adapter);
            // adapter.notifyDataSetChanged();



           swiperefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
               @Override
               public void onRefresh() {
                   FeedUser();
                   swiperefresh.setRefreshing(false);

                }
           });

        }
        else {
            Toast.makeText(this, "Login First", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getApplicationContext(),LoginActivity.class));
            finish();
        }


    }

   public void FeedUser(){
        feedUsers.clear();
        feedName.clear();
        feedList.clear();
        feedRef=storageRef.child("Uploded Pictures").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail()).child("Feed Images");
        feedRef.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                for(StorageReference item:listResult.getItems()){
                    feedUsers.add(item);
                    feedName.add(item.getParent().getParent().getName());
                    Log.i("Tag",item.toString());
                    Log.i("Tag2",item.getParent().getName());
                }
                for (int i = 0; i < feedUsers.size(); i++) {
                    feedList.add(new Feed(feedUsers.get(i), feedName.get(i)));
                }
                MyListAdapter adapter = new MyListAdapter(MyFeedActivity.this,R.layout.feed_layout, feedList);
                myFeed_listView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }

        });
    }




    //}
}
