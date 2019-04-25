package com.george.mhadhirisaccoltd.MainIssues.Loans;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.george.mhadhirisaccoltd.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;


public class LoanRequest extends AppCompatActivity {
    private Button mSubmit;
    private TextView mName,mEmail,mPhone,mNumber,mId;
    private EditText mAmount;
    private Spinner mSpinner;

    private FirebaseAuth auth;
    private FirebaseFirestore firebaseFirestore;
    private ProgressDialog progressDialog;
    private String user_id,amount,name,email,phone,mnumber,idnumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loan_request);

        auth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        user_id = auth.getCurrentUser().getUid();
        progressDialog=new ProgressDialog(this);

        mName = (TextView)findViewById(R.id.tv_fullname);
        mEmail = (TextView)findViewById(R.id.tv_email);
        mPhone = (TextView)findViewById(R.id.tv_phonenumber);
        mNumber = (TextView)findViewById(R.id.tv_membernumber);
        mId = (TextView)findViewById(R.id.tv_idnumber);

        mSubmit = (Button)findViewById(R.id.btn_request);
        mAmount = (EditText)findViewById(R.id.edt_amount);
        mSpinner = (Spinner)findViewById(R.id.sp_loancats);


        mSpinner.setSelection(0);

        setTitle("Loan Request");

        //getint the values
        initializeTheValues();
        confirmation(user_id);

        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                amount = mAmount.getText().toString().trim();
                final String cat = mSpinner.getSelectedItem().toString();
                if (TextUtils.isEmpty(amount)){
                    Toast.makeText(LoanRequest.this, "Enter amount to request...", Toast.LENGTH_SHORT).show();
                }else  if (mSpinner.getSelectedItemPosition() == 0) {
                    Toast.makeText(LoanRequest.this, "Select Loan Category", Toast.LENGTH_SHORT).show();
                }else {



                    int amnt = Integer.parseInt(amount);

                   if (mSpinner.getSelectedItemPosition() == 2){
                       if (amnt>50000){
                           Toast.makeText(LoanRequest.this, "Amount requested is too high...", Toast.LENGTH_SHORT).show();
                       }else {
                           firebaseFirestore.collection("Membership").document("Members").collection("Approval").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                               @Override
                               public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                   if (task.isSuccessful()) {

                                       if (task.getResult().exists()) {

                                           new AlertDialog.Builder(LoanRequest.this)
                                                   .setIcon(R.drawable.ic_info_black_24dp)
                                                   .setTitle("Loan Payment Period")
                                                   .setMessage("Loan should be fully paid within two months").
                                                   setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                       @Override
                                                       public void onClick(DialogInterface dialogInterface, int i) {
                                                           loaning(amount,cat,mSpinner);
                                                           dialogInterface.dismiss();
                                                       }
                                                   }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                               @Override
                                               public void onClick(DialogInterface dialogInterface, int i) {
                                                   dialogInterface.dismiss();
                                               }
                                           }).show();

                                       } else {
                                           Toast.makeText(LoanRequest.this, "Seems you are not a member please check your membership details", Toast.LENGTH_LONG).show();
                                       }
                                   }
                               }
                           });

                       }
                   }else  if (mSpinner.getSelectedItemPosition() == 1){
                           firebaseFirestore.collection("Membership").document("Members").collection("Approval").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                               @Override
                               public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                   if (task.isSuccessful()) {

                                       if (task.getResult().exists()) {

                                           new AlertDialog.Builder(LoanRequest.this)
                                                   .setIcon(R.drawable.ic_info_black_24dp)
                                                   .setTitle("Loan Payment Period")
                                                   .setMessage("Loan should be fully paid within ONE year").
                                                   setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                       @Override
                                                       public void onClick(DialogInterface dialogInterface, int i) {
                                                           loaning(amount,cat,mSpinner);
                                                           dialogInterface.dismiss();
                                                       }
                                                   }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                               @Override
                                               public void onClick(DialogInterface dialogInterface, int i) {
                                                   dialogInterface.dismiss();
                                               }
                                           }).show();

                                       } else {
                                           Toast.makeText(LoanRequest.this, "Seems you are not a member please check your membership details", Toast.LENGTH_LONG).show();
                                       }
                                   }
                               }
                           });


                   }

                }
            }
        });
    }

    private void initializeTheValues() {
        firebaseFirestore.collection("Membership").document("Members").collection("Approval").document(user_id).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if (task.isSuccessful()){
                            if (task.getResult().exists()){
                               String fname = task.getResult().getString("fname");
                               String lname = task.getResult().getString("lname");
                               name = fname+" "+lname;

                               phone = task.getResult().getString("telephone1");
                               idnumber = task.getResult().getString("id_number");
                               email= task.getResult().getString("email");

                               //setting the values
                                mName.setText("Fullname: "+name);
                                mPhone.setText("Phone Number: "+phone);
                                mId.setText("ID Number: "+idnumber);
                                mEmail.setText("Email: "+email);

                               firebaseFirestore.collection("MembershipNumbers").document(user_id).get()
                                       .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                           @Override
                                           public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                               if (task.isSuccessful()){
                                                   if (task.getResult().exists()){
                                                       mnumber = task.getResult().getString("number");
                                                       mNumber.setText("Membership Number: "+mnumber);
                                                   }
                                               }
                                           }
                                       });
                            }
                        }
                    }
                });
    }

    public void processStatements(String amount,String cat){

        Map<String, Object> objectMap = new HashMap<>();
        objectMap.put("amount",amount);
        objectMap.put("time", FieldValue.serverTimestamp());
        objectMap.put("user_id",user_id);
        objectMap.put("category",cat);

        firebaseFirestore.collection("Statements").document(user_id).collection("MyStatements").add(objectMap);
        gettingCounts();
    }

    private void  loaning(final String amount, final String cat, Spinner spinner){

        mAmount.setText("");
        progressDialog.setMessage("Submitting your Loan Request");
        progressDialog.show();

        float finalAmount = 0;
        if (spinner.getSelectedItemPosition() == 2){

            float initialAmountShort = Float.parseFloat(amount);
            float interestShort = (float) (0.16*initialAmountShort);
            finalAmount = interestShort+initialAmountShort;

        }else if (spinner.getSelectedItemPosition() == 1){

            float initialAmountLong = Float.parseFloat(amount);
            float interestLong = (float) (0.12*initialAmountLong);
            finalAmount = interestLong+initialAmountLong;
        }



        Map<String, Object> objectMap = new HashMap<>();
        objectMap.put("amount",amount);
        objectMap.put("time", FieldValue.serverTimestamp());
        objectMap.put("user_id",user_id);
        objectMap.put("category",cat);



        Map<String, Object> objectMap_withIntrest = new HashMap<>();
        objectMap_withIntrest.put("amount",""+finalAmount);
        objectMap_withIntrest.put("time", FieldValue.serverTimestamp());
        objectMap_withIntrest.put("user_id",user_id);
        objectMap_withIntrest.put("category",cat);

        firebaseFirestore.collection("Loans").document("ActualLoan").collection("NotApproved").document(user_id).set(objectMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                progressDialog.dismiss();
                new android.support.v7.app.AlertDialog.Builder(LoanRequest.this)
                        .setIcon(R.drawable.ic_info_black_24dp)
                        .setMessage("Your loan request has been submitted successfully. Please wait for approval within 24Hrs")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setCancelable(false)
                        .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                               // processStatements(amount,cat);
                                dialog.dismiss();
                            }
                        }).show();
            }
        })
                .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(LoanRequest.this, "Error:\n"+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        firebaseFirestore.collection("Loans").document("LonInterest").collection("NotApproved").document(user_id).set(objectMap_withIntrest);

    }

    private void confirmation(String UserId){
        firebaseFirestore.collection("Loans").document("ActualLoan").collection("Approved").document(UserId).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            if (task.getResult().exists()){
                                new AlertDialog.Builder(LoanRequest.this)
                                        .setTitle("Pending..")
                                        .setIcon(R.drawable.ic_action_name)
                                        .setMessage("You already have a pending loan")
                                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                mSubmit.setEnabled(false);
                                                dialogInterface.dismiss();
                                            }
                                        }).show();
                            }
                        }
                    }
                });
    }

    private void gettingCounts(){

        firebaseFirestore.collection("StatementsCounts").document(user_id).collection("Statements").document("MyStatements").get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if (task.isSuccessful()){
                            if (task.getResult().exists()){
                                String count = task.getResult().getString("count");
                                int newCount = Integer.parseInt(count)+1;
                                String theCount = ""+newCount;
                                settingCount(theCount);
                            }else {
                                settingCount("1");
                            }
                        }
                    }
                });
    }

    private void settingCount(String count){
        Map<String,String> stringObjectMap = new HashMap<>();
        stringObjectMap.put("count",count);
        firebaseFirestore.collection("StatementsCounts").document(user_id).collection("Statements").document("MyStatements").set(stringObjectMap);
    }

    private void putDueAmount(String amount){

    }
}
