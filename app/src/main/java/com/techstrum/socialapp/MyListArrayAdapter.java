package com.techstrum.socialapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.List;

class MyListAdapter extends ArrayAdapter<Feed> {

    //the list values in the List of type hero
    List<Feed> feedList;

    //activity context
    Context context;

    //the layout resource file for the list items
    int resource;

    //constructor initializing the values
    public MyListAdapter(Context context, int resource, List<Feed> feedList) {
        super(context, resource, feedList);
        this.context = context;
        this.resource = resource;
        this.feedList = feedList;
    }


    //this will return the ListView Item as a View
    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        //we need to get the view of the xml for our list item
        //And for this we need a layoutinflater
        LayoutInflater layoutInflater = LayoutInflater.from(context);

        //getting the view
        View view = layoutInflater.inflate(resource, null, false);

        //getting the view elements of the list from the view

        ImageView imageView = view.findViewById(R.id.uploded_image);
        TextView textViewName = view.findViewById(R.id.user_name_TextView);
        ProgressBar progressBar2=view.findViewById(R.id.progressBar2);
        progressBar2.setVisibility(View.VISIBLE);

        //getting the hero of the specified position
        Feed feed = feedList.get(position);

        //adding values to the list item
        File localFile1 = null;
        try {
            localFile1 = File.createTempFile("Uploded temp Image",".jpg");
        } catch (IOException e) {
            e.printStackTrace();
        }
        File finalLocalFile = localFile1;
        feed.image.getFile(localFile1).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                String tempPath = finalLocalFile.getPath();
                Bitmap bitmap= BitmapFactory.decodeFile(tempPath);
                progressBar2.setVisibility(View.INVISIBLE);
                imageView.setImageBitmap(bitmap);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //fail
            }
        });
        textViewName.setText(feed.getName());




        //finally returning the view
        return view;
    }

}
