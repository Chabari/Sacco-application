package com.george.mhadhirisaccoltd.Account;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.george.mhadhirisaccoltd.MainPanel;
import com.george.mhadhirisaccoltd.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;
import java.util.Map;

public class Login extends AppCompatActivity {

    private EditText mEmail,mPassword;
    private Button mLogin,mforgot,mcreate;
    private ProgressDialog progressDialog;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //setting animation to the background
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.linear_layout);
        AnimationDrawable animationDrawable = (AnimationDrawable) linearLayout.getBackground();
        animationDrawable.setEnterFadeDuration(2500);
        animationDrawable.setExitFadeDuration(5000);
        animationDrawable.start();//ends here animation thing

        auth=FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        progressDialog=new ProgressDialog(this);
        mEmail=(EditText)findViewById(R.id.edt_email_login);
        mPassword=(EditText)findViewById(R.id.edt_password_login);
        mforgot=(Button) findViewById(R.id.btn_forgot);
        mLogin=(Button)findViewById(R.id.btn_login);
        mcreate=(Button)findViewById(R.id.btn_create_account);

        mforgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(),Forgot_Password.class);
                startActivity(intent);
            }
        });
        mcreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(),Signup.class);
                startActivity(intent);
            }
        });
        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email=mEmail.getText().toString().trim();
                String password=mPassword.getText().toString().trim();

                if (TextUtils.isEmpty(email)){
                    Toast.makeText(Login.this, "Enter Email..", Toast.LENGTH_SHORT).show();
                }else if (TextUtils.isEmpty(password)){
                    Toast.makeText(Login.this, "Enter password..", Toast.LENGTH_SHORT).show();
                }else {
                    progressDialog.setMessage("Authenticating...");
                    progressDialog.show();

                    getingyouready(email,password);
                }
            }
        });
    }
    private void getingyouready(final String email, final String password){
        auth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            updateDeviceToken(auth.getCurrentUser().getUid());                            //open the main panel
                            startActivity(new Intent(Login.this, MainPanel.class));
                            finish();
                        }else {
                            Toast.makeText(Login.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                        }

                        progressDialog.dismiss();
                    }
                });
            }
        });
    }

    void updateDeviceToken(String UserId){

        String deviceToken = FirebaseInstanceId.getInstance().getToken();
        Map<String, Object> stringStringMap = new HashMap<>();
        stringStringMap.put("device_token",deviceToken);
        firebaseFirestore.collection("Users").document(UserId).update(stringStringMap);
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (firebaseUser !=null){
            startActivity(new Intent(Login.this, MainPanel.class));
            finish();
        }
    }
}
