package com.george.mhadhirisaccoltd.Account;

import android.app.ProgressDialog;
import android.content.DialogInterface;
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

import com.george.mhadhirisaccoltd.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class Forgot_Password extends AppCompatActivity {


    private EditText mEmail;
    private Button mButtonReset;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot__password);

        //setting animation to the background
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.linear_layout3);
        AnimationDrawable animationDrawable = (AnimationDrawable) linearLayout.getBackground();
        animationDrawable.setEnterFadeDuration(2500);
        animationDrawable.setExitFadeDuration(5000);
        animationDrawable.start();//ends here animation thing


        auth = FirebaseAuth.getInstance();

        mEmail = (EditText)findViewById(R.id.edt_emailreset);
        mButtonReset = (Button) findViewById(R.id.btn_reset);

        mButtonReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email = mEmail.getText().toString().trim();
                if ((!TextUtils.isEmpty(email))){

                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(Forgot_Password.this);
                    builder.setTitle("Password resetting...");
                    builder.setMessage("A link will be sent to this email "+email+"\nProceed?");
// Add the buttons
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            final ProgressDialog progressDialog = new ProgressDialog(Forgot_Password.this);
                            progressDialog.setMessage("Sending reset link...");
                            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                            progressDialog.show();
                            auth.sendPasswordResetEmail(email)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                progressDialog.dismiss();
                                                Toast.makeText(Forgot_Password.this, "We have sent you instructions to reset your password!", Toast.LENGTH_SHORT).show();
                                            } else {
                                                progressDialog.dismiss();
                                                Toast.makeText(Forgot_Password.this, "Failed to send reset email!", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }
                    });
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    });
                    android.app.AlertDialog dialog = builder.create();
                    dialog.show();
                }else {
                    mEmail.setError("Enter valid email address..!");
                }
            }
        });
    }
}
