package com.george.mhadhirisaccoltd.Account;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.george.mhadhirisaccoltd.R;
import com.google.android.gms.tasks.OnCompleteListener;
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

public class AccountSettings extends AppCompatActivity {

    private Button msubmit;
    private FirebaseAuth auth;
    private FirebaseFirestore firebaseFirestore;
    private Uri imageUri = null;
    private ImageView mprofile;
    private TextInputEditText mFName,mLName,mID,mPhone,mEmail;
    private StorageReference storageReference;
    private String user_id,downloadUrl;
    private ProgressDialog progressDialog;
    private Spinner spinner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settings);

        //setting animation to the background
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.linear_layout4);
        AnimationDrawable animationDrawable = (AnimationDrawable) linearLayout.getBackground();
        animationDrawable.setEnterFadeDuration(2500);
        animationDrawable.setExitFadeDuration(5000);
        animationDrawable.start();//ends here animation thing

        progressDialog = new ProgressDialog(this);

        storageReference = FirebaseStorage.getInstance().getReference();
        auth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        user_id = auth.getCurrentUser().getUid();

        msubmit = (Button) findViewById(R.id.submit_profile);
        mFName = (TextInputEditText)findViewById(R.id.edt_firstname_account);
        mLName = (TextInputEditText)findViewById(R.id.edt_lastname_account);
        mID = (TextInputEditText)findViewById(R.id.edt_id_account);
        mPhone = (TextInputEditText)findViewById(R.id.edt_phone_number_account);
        mEmail = (TextInputEditText)findViewById(R.id.edt_email_account);
        mprofile =(ImageView) findViewById(R.id.imgProfile_account);
        spinner = (Spinner)findViewById(R.id.spinner_university_account);

        spinner.setSelection(0);
        setTitle("Account Settings");

        mprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.activity(imageUri)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(AccountSettings.this);


            }
        });


        msubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final  String fname = mFName.getText().toString().trim();
                final  String lname = mLName.getText().toString().trim();
                final  String id_number = mID.getText().toString().trim();
                final  String phone_number = mPhone.getText().toString().trim();
                final  String email = mEmail.getText().toString().trim();
                final  String university = spinner.getSelectedItem().toString();

                if (TextUtils.isEmpty(fname)){
                    mFName.setError("Enter Your First Name..!");
                }else if (TextUtils.isEmpty(lname)){
                    mLName.setError("Enter Your Last Name..!");
                }else if (TextUtils.isEmpty(id_number)){
                    mID.setError("Enter Your ID Number..!");
                }else if (TextUtils.isEmpty(phone_number)){
                    mPhone.setError("Enter Your Phone Number..!");
                }else if (phone_number.length()>10 || phone_number.length()<10){
                    mPhone.setError("Enter a valid phone number");
                }else if (id_number.length()>8){
                    mPhone.setError("Enter a valid id number..!");
                }else if (TextUtils.isEmpty(email)){
                    mEmail.setError("Enter Your Valid Email..!");
                }else if (spinner.getSelectedItemPosition() == 0){
                    Toast.makeText(AccountSettings.this, "Select Your University..!", Toast.LENGTH_SHORT).show();
                }else {
                        progressDialog.setMessage("Uploading your personal details...");
                        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        progressDialog.show();
                        uploadImage();
                        fireStoreUpload(null,fname,lname,id_number,phone_number,university,email);


                }


            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.account_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        return true;
    }

    private void uploadImage(){
        if (imageUri!=null){
            final StorageReference reference = storageReference.child("ProfileImages").child(user_id+".jpg");
            reference.putFile(imageUri);
        }else {
            Toast.makeText(this, "Image cannot be empty..", Toast.LENGTH_SHORT).show();
        }
    }

    private void fireStoreUpload(@NonNull Task<UploadTask.TaskSnapshot> task, final String fname, final String lname, final String id_number, final String phone, final String university, final String email) {


        StorageReference reference = storageReference.child("ProfileImages").child(user_id+".jpg");
         reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
             @Override
             public void onSuccess(Uri uri) {
                 Map<String, String> stringMap = new HashMap<>();
                 stringMap.put("fname", fname);
                 stringMap.put("lname",lname);
                 stringMap.put("id_number",id_number);
                 stringMap.put("phone",phone);
                 stringMap.put("email",email);
                 stringMap.put("university",university);
                 stringMap.put("imageUrl",uri.toString());

                 firebaseFirestore.collection("Users").document(user_id).set(stringMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                     @Override
                     public void onComplete(@NonNull Task<Void> task) {

                         if (task.isSuccessful()){
                             Toast.makeText(getApplicationContext(),"User settings are uploaded",Toast.LENGTH_LONG);
                             onBackPressed();


                         }else {
                             String errorMessage = task.getException().getMessage();
                             Toast.makeText(getApplicationContext(),"Server error!..\n"+errorMessage,Toast.LENGTH_LONG);
                         }

                         progressDialog.dismiss();

                     }
                 });
             }
         });



    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                imageUri  = result.getUri();
                mprofile.setImageURI(imageUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}
