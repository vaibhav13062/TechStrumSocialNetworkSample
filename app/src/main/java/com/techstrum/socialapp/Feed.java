package com.techstrum.socialapp;

import com.google.firebase.storage.StorageReference;

public class Feed {
    StorageReference image;
    String name;
    public Feed(StorageReference image, String name){
        this.image=image;
        this.name=name;
    }

    public String getName() {
        return name;
    }

    public StorageReference getImage() {
        return image;
    }
}
