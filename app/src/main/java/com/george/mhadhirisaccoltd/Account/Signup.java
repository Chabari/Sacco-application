package com.george.mhadhirisaccoltd.Account;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.george.mhadhirisaccoltd.MainPanel;
import com.george.mhadhirisaccoltd.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.Token;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hanks.htextview.base.HTextView;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class Signup extends AppCompatActivity {
    private EditText mEmail;
    private Button mSignup,mAlready;
    private Spinner spinner;
    private FirebaseAuth auth;
    private String user_id;
    private FirebaseFirestore firebaseFirestore;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.linear_layout2);
        AnimationDrawable animationDrawable = (AnimationDrawable) linearLayout.getBackground();
        animationDrawable.setEnterFadeDuration(2500);
        animationDrawable.setExitFadeDuration(5000);
        animationDrawable.start();//ends here animation thing

        auth=FirebaseAuth.getInstance();
        firebaseFirestore=FirebaseFirestore.getInstance();
        progressDialog=new ProgressDialog(this);
        mEmail=(EditText)findViewById(R.id.edt_email_account);
        mSignup=(Button)findViewById(R.id.btn_create_account_account);
        mAlready=(Button)findViewById(R.id.btn_have_account);

        mAlready.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        mSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email=mEmail.getText().toString().trim();
                String emailpattern="[a-zA-Z0-9._-]+@[mhadhirisacco]+\\.+[ac]+\\.+[ke]+";
                
                if (TextUtils.isEmpty(email)){
                    Toast.makeText(Signup.this, "Enter Email", Toast.LENGTH_SHORT).show();
                }/*else if (!email.matches(emailpattern)){
                    Toast.makeText(Signup.this,"Invalid email address please enter your Mhadhiri registered email", Toast.LENGTH_SHORT).show();
                }*/else {
                    progressDialog.setMessage("Sending email link..");
                    progressDialog.show();

                    thingsdonehere(email);

                }
            }
        });
    }



    private void universityupload(@NonNull Task<UploadTask.TaskSnapshot> task, final String university, String user_id) {

        String admin = "not_admin";

        Map<String, String> stringStringMap = new HashMap<>();
        stringStringMap.put("university",university);
        stringStringMap.put("rights",admin);
        firebaseFirestore.collection("Users").document(user_id)
                .set(stringStringMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(Signup.this, "Account successfully created..", Toast.LENGTH_SHORT).show();
                    finish();
                    startActivity(new Intent(Signup.this,MainPanel.class));
                }else {
                    Toast.makeText(Signup.this,"University upload error"+ task.getException().toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }



    private void thingsdonehere(final String email) {
        auth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull final FirebaseAuth firebaseAuth) {
               firebaseAuth.createUserWithEmailAndPassword(email,UUID.randomUUID().toString()).addOnCompleteListener(
                       new OnCompleteListener<AuthResult>() {
                           @Override
                           public void onComplete(@NonNull Task<AuthResult> task) {

                              if (task.isSuccessful()){
                                  String user_id = task.getResult().getUser().getUid();
                                  sendingPrev(user_id);

                                  firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(
                                          new OnCompleteListener<Void>() {
                                              @Override
                                              public void onComplete(@NonNull Task<Void> task) {
                                                  if (task.isSuccessful()){
                                                      new AlertDialog.Builder(Signup.this)
                                                              .setTitle("Message")
                                                              .setMessage("A link has been sent to your email, open it to set your new login password.")
                                                              .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                                  @Override
                                                                  public void onClick(DialogInterface dialogInterface, int i) {
                                                                      dialogInterface.dismiss();
                                                                      onBackPressed();
                                                                  }
                                                              }).show();
                                                  }else {
                                                      Toast.makeText(Signup.this,"Authentication error"+task.getException().toString(),Toast.LENGTH_SHORT).show();
                                                  }

                                                  progressDialog.dismiss();
                                              }
                                          }
                                  );
                              }
                           }
                       }
               );
            }
        });
    }


    private void sendingPrev(String UserID){
        String rights = "not_admin";

        String deviceToken = FirebaseInstanceId.getInstance().getToken();

        Map<String, Object> stringStringMap = new HashMap<>();
        stringStringMap.put("user_id",UserID);
        stringStringMap.put("rights",rights);
        stringStringMap.put("developer_rights","not_developer");
        stringStringMap.put("device_token",deviceToken);
        stringStringMap.put("time", FieldValue.serverTimestamp());

        firebaseFirestore.collection("Users").document(UserID).set(stringStringMap);



    }


}
