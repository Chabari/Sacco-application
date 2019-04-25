package com.george.mhadhirisaccoltd.MainIssues.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.george.mhadhirisaccoltd.Account.AccountSettings;
import com.george.mhadhirisaccoltd.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Profile_settings extends AppCompatActivity {

    private ImageView mProfile;
    private Button mSubmit;
    private FirebaseAuth auth;
    private StorageReference storageReference;
    private String user_id;
    private FirebaseFirestore firebaseFirestore;
    private ProgressDialog progressDialog;
    private Uri imageUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_settings);

        auth=FirebaseAuth.getInstance();
        user_id = auth.getCurrentUser().getUid();

        firebaseFirestore=FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        progressDialog=new ProgressDialog(this);
        mSubmit=(Button)findViewById(R.id.btnSubmitProfile);
        mProfile=(ImageView)findViewById(R.id.img_prof);


        //picking image
        mProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.activity(imageUri)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(Profile_settings.this);

            }
        });

        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageUpload(user_id);
            }
        });



    }

    private void imageUpload(final String UserId){
        if (imageUri !=null){
            progressDialog.setMessage("Uploading your profile image...");
            progressDialog.show();
            String randomName = UUID.randomUUID().toString();
            final StorageReference reference = storageReference.child("UserProfileImages").child(randomName+".jpg");
            reference.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                    if (task.isSuccessful()){
                        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {

                                Map<String, String>stringStringMap = new HashMap<>();
                                stringStringMap.put("imageUrl",uri.toString());
                                firebaseFirestore.collection("ProfileImages").document(UserId).set(stringStringMap)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                                if (task.isSuccessful()){
                                                    progressDialog.dismiss();
                                                    Toast.makeText(Profile_settings.this, "Image successfully uploaded...Reload the application to view your image", Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        progressDialog.dismiss();
                                        Toast.makeText(Profile_settings.this, "Something went wrong..\n"+e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });
                    }
                }
            });


        }else {
            Toast.makeText(this, "Pick image...", Toast.LENGTH_SHORT).show();
        }
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                imageUri  = result.getUri();
                mProfile.setImageURI(imageUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}
