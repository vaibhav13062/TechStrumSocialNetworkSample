package com.techstrum.socialapp;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.io.File;
import java.util.Objects;
import ly.img.android.pesdk.assets.filter.basic.FilterPackBasic;
import ly.img.android.pesdk.assets.font.basic.FontPackBasic;
import ly.img.android.pesdk.assets.frame.basic.FramePackBasic;
import ly.img.android.pesdk.assets.overlay.basic.OverlayPackBasic;
import ly.img.android.pesdk.assets.sticker.emoticons.StickerPackEmoticons;
import ly.img.android.pesdk.assets.sticker.shapes.StickerPackShapes;
import ly.img.android.pesdk.backend.model.EditorSDKResult;
import ly.img.android.pesdk.backend.model.constant.Directory;
import ly.img.android.pesdk.backend.model.state.CameraSettings;
import ly.img.android.pesdk.backend.model.state.SaveSettings;
import ly.img.android.pesdk.backend.model.state.manager.SettingsList;
import ly.img.android.pesdk.ui.activity.CameraPreviewBuilder;
import ly.img.android.pesdk.ui.model.state.UiConfigFilter;
import ly.img.android.pesdk.ui.model.state.UiConfigFrame;
import ly.img.android.pesdk.ui.model.state.UiConfigOverlay;
import ly.img.android.pesdk.ui.model.state.UiConfigSticker;
import ly.img.android.pesdk.ui.model.state.UiConfigText;
import ly.img.android.pesdk.ui.utils.PermissionRequest;
import ly.img.android.serializer._3.IMGLYFileWriter;

public class EditorActivity extends Activity implements PermissionRequest.Response {
    FirebaseUser user;
    FirebaseStorage storage;
    StorageReference storageRef;
    FirebaseAuth fAuth;
    StorageReference imageRef;
    Uri edited_image;
    FirebaseDatabase fBase;
    DatabaseReference myRef;
    UploadTask uploadTask;


    User user1;

    // Important permission request for Android 6.0 and above, don't forget to add this!
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        PermissionRequest.onRequestPermissionsResult(requestCode, permissions, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void permissionGranted() {}

    @Override
    public void permissionDenied() {
        /* TODO: The Permission was rejected by the user. The Editor was not opened,
         * Show a hint to the user and try again. */
    }

    public static int PESDK_RESULT = 1;

    private SettingsList createPesdkSettingsList() {

        // Create a empty new SettingsList and apply the changes on this referance.
        SettingsList settingsList = new SettingsList();

        // If you include our asset Packs and you use our UI you also need to add them to the UI,
        // otherwise they are only available for the backend
        // See the specific feature sections of our guides if you want to know how to add our own Assets.

        settingsList.getSettingsModel(UiConfigFilter.class).setFilterList(
                FilterPackBasic.getFilterPack()
        );

        settingsList.getSettingsModel(UiConfigText.class).setFontList(
                FontPackBasic.getFontPack()
        );

        settingsList.getSettingsModel(UiConfigFrame.class).setFrameList(
                FramePackBasic.getFramePack()
        );

        settingsList.getSettingsModel(UiConfigOverlay.class).setOverlayList(
                OverlayPackBasic.getOverlayPack()
        );

        settingsList.getSettingsModel(UiConfigSticker.class).setStickerLists(
                StickerPackEmoticons.getStickerCategory(),
                StickerPackShapes.getStickerCategory()
        );

        // Set custom camera image export settings
        settingsList.getSettingsModel(CameraSettings.class)
                .setExportDir(Directory.DCIM, "TechStrum")
                .setExportPrefix("camera_");

        // Set custom editor image export settings
        settingsList.getSettingsModel(SaveSettings.class)
                .setExportDir(Directory.DCIM, "TechStrum")
                .setExportPrefix("result_")
                .setSavePolicy(SaveSettings.SavePolicy.RETURN_ALWAYS_ONLY_OUTPUT);

        return settingsList;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);


        fAuth=FirebaseAuth.getInstance();
        fBase = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();
        openCamera();


    }

    private void openCamera() {
        SettingsList settingsList = createPesdkSettingsList();

        new CameraPreviewBuilder(this)
                .setSettingsList(settingsList)
                .startActivityForResult(this, PESDK_RESULT);
    }
    private  void update(){

        user1.uploads++;
        FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(user1).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, android.content.Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (resultCode == RESULT_OK && requestCode == PESDK_RESULT) {
            // Editor has saved an Image.
            EditorSDKResult data = new EditorSDKResult(intent);

            data.notifyGallery(EditorSDKResult.UPDATE_RESULT & EditorSDKResult.UPDATE_SOURCE);

            Log.i("PESDK", "Source image is located here " + data.getSourceUri());
            Log.i("PESDK", "Result image is located here " + data.getResultUri());
                edited_image=data.getResultUri();
            if(user!=null) {
                myRef = fBase.getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {//data found

                        user1 =dataSnapshot.getValue(User.class);
                        update();
                        imageRef=storageRef.child("Uploded Pictures").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser().getEmail())).child(user1.uploads+".jpg");
                        uploadTask=imageRef.putFile(edited_image);
                        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                Toast.makeText(EditorActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();

                                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                                finish();

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(EditorActivity.this, "Not Uploaded"+e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        //data not Found
                    }
               });


            }
            // TODO: Do something with the result image

            // OPTIONAL: read the latest state to save it as a serialisation
            SettingsList lastState = data.getSettingsList();
            new IMGLYFileWriter(lastState).writeJson(new File(
                    Environment.getExternalStorageDirectory(),
                    "serialisationReadyToReadWithPESDKFileReader.json"
            ));

        } else if (resultCode == RESULT_CANCELED && requestCode == PESDK_RESULT) {
            // Editor was canceled

            startActivity(new Intent(getApplicationContext(),MainActivity.class));
            finish();

            // TODO: Do something...
        }
    }
}
