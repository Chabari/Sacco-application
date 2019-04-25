package com.george.mhadhirisaccoltd.MainIssues.Fragments;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class Account extends Fragment {
    private View view;
    private TextView mLoan,mAmountDue,mPayed,mAccountAmount,mMessage;
    private EditText mAmount;
    private Button mMakePayment;
    private Spinner mSpinner;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth mAuth;
    private String amount,amountdue,user_id,amountpayed,loan;
    private ProgressDialog mProgressDialog;
    private CardView cardMessage;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_account, container, false);

        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        user_id  = mAuth.getCurrentUser().getUid();

        mProgressDialog = new ProgressDialog(getContext());

        mLoan = (TextView)view.findViewById(R.id.tvLoanAmount);
        mMessage = (TextView)view.findViewById(R.id.tv_message);
        cardMessage = (CardView) view.findViewById(R.id.cardMessage);
        mAmountDue = (TextView)view.findViewById(R.id.tvLoanDue);
        mPayed = (TextView)view.findViewById(R.id.tvAmountPayedLoan);
        mAccountAmount = (TextView)view.findViewById(R.id.tvAccountAmount);
        mAmount = (EditText)view.findViewById(R.id.edt_amount_payment);
        mMakePayment = (Button)view.findViewById(R.id.btn_paying);
        mSpinner = (Spinner)view.findViewById(R.id.sp_payment);


        mSpinner.setSelection(0);

        realTime();


        mMakePayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                firebaseFirestore.collection("Membership").document("Members").collection("Approval").document(user_id)
                        .get().addOnCompleteListener(
                        new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()){
                                    if (task.getResult().exists()){
                                        amount = mAmount.getText().toString().trim();
                                        final String cat = mSpinner.getSelectedItem().toString();

                                        if (mSpinner.getSelectedItemPosition() == 0){
                                            Toast.makeText(getContext(), "Select Payement...", Toast.LENGTH_SHORT).show();
                                        }
                                        else
                                        {
                                            if (mSpinner.getSelectedItemPosition() == 2){

                                                if (TextUtils.isEmpty(amount)) {
                                                    Toast.makeText(getContext(), "Enter Amount to deposit", Toast.LENGTH_SHORT).show();
                                                }else {

                                                    firebaseFirestore.collection("Deposits").document(user_id).get()
                                                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                                                    if (task.isSuccessful()){
                                                                        if (task.getResult().exists()){
                                                                            String Amount = task.getResult().getString("amount");
                                                                            int initialAmount = Integer.parseInt(Amount);
                                                                            final int newAmount = initialAmount+Integer.parseInt(amount);
                                                                            new AlertDialog.Builder(getContext())
                                                                                    .setTitle("Confirmation")
                                                                                    .setIcon(R.drawable.ic_action_name)
                                                                                    .setMessage("Are you sure you want to proceed?\nPayment Type: "+cat+"\nAmount: KSH "+amount)
                                                                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                                                        @Override
                                                                                        public void onClick(DialogInterface dialogInterface, int i) {
                                                                                            mProgressDialog.setMessage("Processing your deposit...");
                                                                                            mProgressDialog.show();
                                                                                            makeDepo(""+newAmount,cat);
                                                                                            processStatements(amount,cat);

                                                                                            gettintMyAccount(mAccountAmount);
                                                                                        }
                                                                                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                                                                                @Override
                                                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                                                    dialogInterface.dismiss();
                                                                                }
                                                                            }).show();
                                                                        }else {
                                                                            new AlertDialog.Builder(getContext())
                                                                                    .setTitle("Confirmation")
                                                                                    .setMessage("Are you sure you want to proceed?\nPayment Type: "+cat+"\nAmount: KSH "+amount)
                                                                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                                                        @Override
                                                                                        public void onClick(DialogInterface dialogInterface, int i) {
                                                                                            mProgressDialog.setMessage("Processing your deposit...");
                                                                                            mProgressDialog.show();
                                                                                            makeDepo(amount,cat);
                                                                                            processStatements(amount,cat);

                                                                                            gettintMyAccount(mAccountAmount);
                                                                                        }
                                                                                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                                                                                @Override
                                                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                                                    dialogInterface.dismiss();
                                                                                }
                                                                            }).setCancelable(false).show();
                                                                        }
                                                                    }

                                                                }
                                                            });

                                                }
                                            }else {
                                                if (mSpinner.getSelectedItemPosition() == 1){
                                                    if (TextUtils.isEmpty(amount)) {
                                                        Toast.makeText(getContext(), "Enter Loan Amount to Pay", Toast.LENGTH_SHORT).show();
                                                    }else {
                                                        firebaseFirestore.collection("Loans").document("LonInterest").collection("Approved").document(user_id).get()
                                                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                                                        if (task.isSuccessful()){
                                                                            if (task.getResult().exists()){
                                                                                String Amount = task.getResult().getString("amount");
                                                                                float initialAmount = Float.parseFloat(Amount);
                                                                                final float newAmount = initialAmount-Float.parseFloat(amount);
                                                                                new AlertDialog.Builder(getContext())
                                                                                        .setTitle("Confirmation")
                                                                                        .setIcon(R.drawable.ic_action_name)
                                                                                        .setMessage("Are you sure you want to proceed?\nPayment Type: "+cat+"\nAmount: KSH "+amount)
                                                                                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                                                            @Override
                                                                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                                                                mProgressDialog.setMessage("Processing your payment...");
                                                                                                mProgressDialog.show();
                                                                                                makePayMent(""+newAmount,cat);
                                                                                                setmPayed(amount);
                                                                                                processStatements(amount,cat);
                                                                                                gettintMyAccount(mAccountAmount);
                                                                                                settingLoanDue(mAmountDue);
                                                                                                getMpayed(mPayed);

                                                                                                if (newAmount<=0){
                                                                                                    loanComplition();
                                                                                                }
                                                                                            }
                                                                                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                                                                                    @Override
                                                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                                                        dialogInterface.dismiss();
                                                                                    }
                                                                                }).setCancelable(false).show();
                                                                            }else {
                                                                                Toast.makeText(getContext(), "Seems you dont't have an outstanding loan...", Toast.LENGTH_LONG).show();
                                                                            }
                                                                        }

                                                                    }
                                                                });
                                                    }

                                                }
                                            }
                                        }
                                    }else {
                                        Toast.makeText(getContext(), "Seems you are not a member yet...please apply for membership to access these services.", Toast.LENGTH_LONG).show();
                                    }
                                }
                            }
                        }
                );

            }
        });


        return view;
    }

    private void gettintMyAccount(final TextView textView){
        firebaseFirestore.collection("Deposits").document(user_id).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if (task.isSuccessful()){
                            if (task.getResult().exists()){
                                String Amount = task.getResult().getString("amount");
                                textView.setText("KSH "+Amount);
                            }else {
                                textView.setText("KSH "+0);
                            }
                        }
                    }
                });
    }
    private void settingLoan(final TextView textView){
        firebaseFirestore.collection("Loans").document("ActualLoan").collection("Approved").document(user_id).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            if (task.getResult().exists()){
                                String Amount = task.getResult().getString("amount");
                                textView.setText("Actual Loan: KSH "+Amount);
                            }else {

                                textView.setText("Actual Loan: KSH "+0);

                            }
                        }
                    }
                });

    }

    private void settingMessage(final TextView mMessage, final CardView cardMessage){
        firebaseFirestore.collection("Loans").document("ActualLoan").collection("NotApproved").document(user_id).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            if (task.getResult().exists()){
                                mMessage.setText("Loan awaiting for approval...");
                                cardMessage.setVisibility(View.VISIBLE);
                            }else {

                                cardMessage.setVisibility(View.GONE);
                            }
                        }
                    }
                });

    }
    private void settingLoanDue(final TextView textView){
        firebaseFirestore.collection("Loans").document("LonInterest").collection("Approved").document(user_id).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            if (task.getResult().exists()){
                                String Amount = task.getResult().getString("amount");
                                textView.setText("Loan Due: KSH "+Amount);
                            }else {
                                textView.setText("Loan Due: KSH "+0);
                            }
                        }
                    }
                });

    }

    private void makeDepo(final String Amount, final String Cat){
        Map<String, Object> stringObjectMap = new HashMap<>();
        stringObjectMap.put("user_id",user_id);
        stringObjectMap.put("amount",Amount);
        stringObjectMap.put("timeStamp", FieldValue.serverTimestamp());

        firebaseFirestore.collection("Deposits").document(user_id).set(stringObjectMap).
                addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()){
                            Toast.makeText(getContext(), "Amount successfully deposited.", Toast.LENGTH_SHORT).show();
                            mProgressDialog.dismiss();

                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "Something Went Wrong\n"+e.getMessage(), Toast.LENGTH_SHORT).show();
                mProgressDialog.dismiss();
            }
        });
    }


    private void makePayMent(final String Amount, final String Cat){
        Map<String, Object> stringObjectMap = new HashMap<>();
        stringObjectMap.put("user_id",user_id);
        stringObjectMap.put("amount",Amount);
        stringObjectMap.put("timeStamp", FieldValue.serverTimestamp());

        firebaseFirestore.collection("Loans").document("LonInterest").collection("Approved").document(user_id).set(stringObjectMap).
                addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()){
                            Toast.makeText(getContext(), "Amount successfully sent.", Toast.LENGTH_SHORT).show();
                            mProgressDialog.dismiss();

                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "Something Went Wrong\n"+e.getMessage(), Toast.LENGTH_SHORT).show();
                mProgressDialog.dismiss();
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

    private void setmPayed(String Amount){
        Map<String, String> stringStringMap = new HashMap<>();
        stringStringMap.put("amount",Amount);
        firebaseFirestore.collection("RecentPaymentsLoan").document(user_id).set(stringStringMap);
    }

    private void getMpayed(final TextView textView){

        firebaseFirestore.collection("RecentPaymentsLoan").document(user_id).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            if (task.getResult().exists()){
                                String amount = task.getResult().getString("amount");
                                textView.setText("Recent Loan Payment: KSH "+amount);

                            }else {
                                textView.setText("Recent Loan Payment: No Payment");
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

    private void processStatements(String amount,String cat){

        Map<String, Object> objectMap = new HashMap<>();
        objectMap.put("amount",amount);
        objectMap.put("timeStamp", FieldValue.serverTimestamp());
        objectMap.put("user_id",user_id);
        objectMap.put("category",cat);

        firebaseFirestore.collection("Statements").document(user_id).collection("MyStatements").add(objectMap);
        gettingCounts();
    }

    @Override
    public void onStart() {
        super.onStart();
        realTime();
    }

    @Override
    public void onResume() {
        super.onResume();
        realTime();
    }

    private void loanComplition(){
        firebaseFirestore.collection("Loans").document("ActualLoan").collection("Approved").document(user_id).delete()
        .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    new AlertDialog.Builder(getContext())
                            .setIcon(R.drawable.ic_action_check)
                            .setTitle("Confirmation")
                            .setMessage("You have completed paying your loan. We value you as Mhadhiri Sacco Ltd Member")
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    firebaseFirestore.collection("RecentPaymentsLoan").document(user_id).delete();
                                    firebaseFirestore.collection("Loans").document("LonInterest").collection("Approved").document(user_id).delete();
                                    dialogInterface.dismiss();
                                }
                            }).setCancelable(false)
                            .show();
                }
            }
        });
    }

    void realTime(){
        final Handler handler = new Handler();

        final Runnable rn = new Runnable() {
            @Override
            public void run() {


                gettintMyAccount(mAccountAmount);
                settingLoan(mLoan);
                settingLoanDue(mAmountDue);
                getMpayed(mPayed);
                settingMessage(mMessage,cardMessage);
                handler.postDelayed(this,100);
            }
        };

        handler.postDelayed(rn,100);
    }
}
