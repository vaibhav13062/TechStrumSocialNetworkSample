package com.techstrum.socialapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    FirebaseDatabase database ;
    DatabaseReference myRef;
    DrawerLayout drawerLayout;
    FirebaseUser user;
    FirebaseStorage storage;
    StorageReference storageRef;
    StorageReference downloadRef;
    StorageReference feedRef;
    StorageReference imageRef;
    ArrayList<StorageReference> feedUsers=new ArrayList<StorageReference>();
    ListView listView;
    List<Feed> feedList;
    ArrayList<String> feedName=new ArrayList<String>();
  //  Button button;
    SwipeRefreshLayout swipeRefresh;
    MyListAdapter adapter;
    TextView name_pro;
    TextView email_pro;
    CircularImageView profile_pro;
    ProgressBar progressBar_pro;



    private ActionBarDrawerToggle toggle;
    File localFile= null;
    User user1;
    NavigationView navigationView;
    private void navigation_buttons(){//1
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.nav_home:
                        drawerLayout.closeDrawer(Gravity.LEFT);
                        //Home Intent
                        break;
                    case R.id.nav_profile:
                        startActivity(new Intent(getApplicationContext(),ProfileActivity.class));
                        //Navigation Intent
                        break;
                    case R.id.nav_logout:
                        if(user!=null){
                            logout();
                        }
                        else{
                            login();
                        }
                        break;
                    case R.id.nav_myFeed:
                        startActivity(new Intent(getApplicationContext(),MyFeedActivity.class));
                        //My Feed Activity
                        break;
                }
                return true;
            }
        });
    }
    private void logout(){//2
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(),LoginActivity.class));
    }
    private void login(){
        startActivity(new Intent(getApplicationContext(),LoginActivity.class));
        finish();
    }//3

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

         drawerLayout =  findViewById(R.id.drawer_layout);
        toggle= new ActionBarDrawerToggle(this, drawerLayout,R.string.open,R.string.close);
         user = FirebaseAuth.getInstance().getCurrentUser();
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        feedList = new ArrayList<>();
        listView = (ListView) findViewById(R.id.listView);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
      //  button=findViewById(R.id.button);
      //  button.setVisibility(View.VISIBLE);
        View hView = navigationView.getHeaderView(0);
        swipeRefresh=findViewById(R.id.swipeRefresh);
        name_pro = (TextView)hView.findViewById(R.id.name_pro);
         email_pro =(TextView)hView.findViewById(R.id.email_pro);
         profile_pro =hView.findViewById(R.id.profile_pro);
         // progressBar_pro=hView.findViewById(R.id.progressBar_pro);
        navigation_buttons(); //Navigation Buttons Switch Statements
        FloatingActionButton fab = findViewById(R.id.fab); //floating Action Button on main activity
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),EditorActivity.class));
                //Floating Action Button Code
            }
        });
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();



        if (user != null) {  //user is signed in
//            progressBar_pro.setVisibility(View.VISIBLE);
            downloadRef=storageRef.child("Profile Pictures").child("thumbnails").child(FirebaseAuth.getInstance().getCurrentUser().getUid()+"_200x200.jpg");
            feedRef=storageRef.child("Uploded Pictures");

            myRef = database.getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());//getting data from data base

            feedShow();
            swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    feedShow();
                    swipeRefresh.setRefreshing(false);
                }
            });




        } else {//if user is no signed In
            Menu menu=navigationView.getMenu();
            MenuItem nav_logout=menu.findItem(R.id.nav_logout);
            nav_logout.setTitle("Login");
            nav_logout.setIcon(R.drawable.ic_baseline_publish_24);

        }

    }


    @Override//On Items Pressed
    public boolean onOptionsItemSelected(MenuItem item) {
        if (toggle.onOptionsItemSelected(item)) { //app bar navigation drawer Button
            return true;
        }
        if(item.getItemId()==R.id.update_set){//app Bar Side menu
            startActivity(new Intent(getApplicationContext(),UpdateActivity.class));
        }
        else if(item.getItemId()==R.id.aboutus_set){
            startActivity(new Intent(getApplicationContext(),AboutActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }
   /* public void getPic(View view) {
        if(user!=null) {
            button.setVisibility(View.INVISIBLE);
            for (int i = 0; i < feedUsers.size(); i++) {
                feedList.add(new Feed(feedUsers.get(i), feedName.get(i)));

            }
            MyListAdapter adapter = new MyListAdapter(this, R.layout.feed_layout, feedList);
            listView.setAdapter(adapter);
        }
        else{
            Toast.makeText(this, "LOGIN FIRST", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getApplicationContext(),LoginActivity.class));

        }
    }*/

   @Override //Side Menu
    public boolean onCreateOptionsMenu(Menu m) {
   MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.appbarmenu,m);
        return super.onCreateOptionsMenu(m);
   }
   public void feedShow(){
       feedUsers.clear();
       feedName.clear();
       feedList.clear();
       myRef.addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {//data found
               user1 =dataSnapshot.getValue(User.class);
               name_pro.setText(user1.name);
               email_pro.setText(user1.email);
               try {
                   localFile = File.createTempFile("ProfileImage",".jpg");
               } catch (IOException e) {
                   e.printStackTrace();
               }
               downloadRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                   @Override
                   public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                     //  progressBar_pro.setVisibility(View.VISIBLE);
                       String tempPath = localFile.getPath();
                       Bitmap bitmap= BitmapFactory.decodeFile(tempPath);
                       profile_pro.setImageBitmap(bitmap);
                      // progressBar_pro.setVisibility(View.INVISIBLE);

                       //downloded
                   }
               }).addOnFailureListener(new OnFailureListener() {
                   @Override
                   public void onFailure(@NonNull Exception e) {
                     //  progressBar_pro.setVisibility(View.INVISIBLE);
                       Toast.makeText(MainActivity.this, "Upload Your Image", Toast.LENGTH_SHORT).show();
                       //failed

                   }
               });
           }
           @Override
           public void onCancelled(@NonNull DatabaseError databaseError) {//data not Found
           }
       });

       feedRef.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
           @Override
           public void onSuccess(ListResult listResult) {

               for(StorageReference prefix:listResult.getPrefixes()){
                   imageRef=prefix.child("Feed Images");
                   imageRef.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
                       @Override
                       public void onSuccess(ListResult listResult1) {
                           for(StorageReference item:listResult1.getItems()){
                               feedUsers.add(item);
                               feedName.add(prefix.getName());

                           }

                           for (int i = 0; i < feedUsers.size(); i++) {
                               feedList.add(new Feed(feedUsers.get(i), feedName.get(i)));

                           }
                           adapter = new MyListAdapter(MainActivity.this, R.layout.feed_layout, feedList);
                           listView.setAdapter(adapter);
                           adapter.notifyDataSetChanged();


                       }
                   }).addOnFailureListener(new OnFailureListener() {
                       @Override
                       public void onFailure(@NonNull Exception e) {

                       }
                   });
               }

               //for(StorageReference item:listResult.getItems()){
               //}
           }
       }).addOnFailureListener(new OnFailureListener() {
           @Override
           public void onFailure(@NonNull Exception e) {
               //fiaal
               Toast.makeText(MainActivity.this, "Prefix"+e.getMessage(), Toast.LENGTH_SHORT).show();
               Log.i("Tag","Fail");
           }
       });
   }






}
