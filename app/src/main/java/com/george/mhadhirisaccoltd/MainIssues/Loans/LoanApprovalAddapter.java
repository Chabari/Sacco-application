package com.george.mhadhirisaccoltd.MainIssues.Loans;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
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

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by George on 3/10/2019.
 */

public class LoanApprovalAddapter extends RecyclerView.Adapter<LoanApprovalAddapter.ViewHolder> {
    private FirebaseFirestore firebaseFirestore;
    private Context context;
    private List<LoanApprovalList>loanApprovalLists;
    private FirebaseAuth auth;
    private ProgressDialog progressDialog;

    public LoanApprovalAddapter(List<LoanApprovalList> loanApprovalLists) {
        this.loanApprovalLists = loanApprovalLists;
    }

    @Override
    public LoanApprovalAddapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.loanapprovalitemsdisplay,parent,false);
        context=parent.getContext();
        auth=FirebaseAuth.getInstance();
        firebaseFirestore=FirebaseFirestore.getInstance();
        progressDialog = new ProgressDialog(context);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final LoanApprovalAddapter.ViewHolder holder, int position) {

        final String owner_id=loanApprovalLists.get(position).getUser_id();

        firebaseFirestore.collection("Membership").document("Members").collection("Approval").document(owner_id).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            if (task.getResult().exists()){
                                String fname=task.getResult().getString("fname");
                                String lname=task.getResult().getString("lname");
                                final String phone=task.getResult().getString("telephone1");
                                final String idnumber=task.getResult().getString("id_number");
                                final String fullname=fname+" "+lname;firebaseFirestore.collection("Loans").document("ActualLoan").collection("NotApproved").document(owner_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                        if (task.isSuccessful()){
                                            if (task.getResult().exists()){
                                                final String amount=task.getResult().getString("amount");
                                                final String cat=task.getResult().getString("category");


                                                holder.settingDetails(fullname,idnumber,phone,amount,cat);



                                                holder.mLinear.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        final Dialog dialog = new Dialog(context);
                                                        dialog.setContentView(R.layout.dialog_loan_approval);
                                                        dialog.setTitle("Loan Approval");
                                                        Button mDecline = (Button)dialog.findViewById(R.id.btnDecline_loan);
                                                        Button mAccept = (Button)dialog.findViewById(R.id.btnAccept_loan);
                                                        TextView mTextView= (TextView)dialog.findViewById(R.id.tv_loan_details);

                                                        //setting details
                                                        mTextView.setText("Full Name: "+fullname+"\nId Number: "+idnumber
                                                                +"\nAmount: "+amount+"\nPhone Number: "+phone+"\nLoan Category: "+cat);


                                                        mDecline.setOnClickListener(new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View view) {
                                                                new AlertDialog.Builder(context)
                                                                        .setTitle("Loan Declination")
                                                                        .setMessage("Are you sure you want to decline this loan request?")
                                                                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                                            @Override
                                                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                                                firebaseFirestore.collection("Loans").document("LonInterest").collection("NotApproved").document(owner_id).delete();
                                                                                firebaseFirestore.collection("Loans").document("ActualLoan").collection("NotApproved").document(owner_id).delete();
                                                                                dialogInterface.dismiss();

                                                                            }
                                                                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                                        dialogInterface.dismiss();
                                                                    }
                                                                }).show();
                                                            }
                                                        });
                                                        mAccept.setOnClickListener(new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View view) { new AlertDialog.Builder(context)
                                                                    .setTitle("Loan Acceptance")
                                                                    .setMessage("Are you sure you want to accept this loan request?")
                                                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                                        @Override
                                                                        public void onClick(DialogInterface dialogInterface, int i) {

                                                                            holder.loaning(amount,cat,owner_id);
                                                                            holder.sendNotification(owner_id,auth.getCurrentUser().getUid());
                                                                            dialogInterface.dismiss();

                                                                        }
                                                                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                                                                        @Override
                                                                        public void onClick(DialogInterface dialogInterface, int i) {
                                                                            dialogInterface.dismiss();
                                                                        }
                                                                    }).show();

                                                            }
                                                        });

                                                        dialog.show();
                                                    }
                                                });
                                            }
                                        }
                                    }
                                });
                            }
                        }
                    }
                });

    }

    @Override
    public int getItemCount() {
        return loanApprovalLists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mFullname,mIdnumber,mPhone,mAmount,mType;
        private LinearLayout mLinear;
        private View view;

        public ViewHolder(View itemView) {
            super(itemView);
            view=itemView;

            mLinear=(LinearLayout)view.findViewById(R.id.Linear_layout5);

        }
        public void settingDetails(String fullname,String idnumber,String phone,String amount,String cat){
            mFullname=(TextView)view.findViewById(R.id.tvfullname_loan_approvaldisplay);
            mIdnumber=(TextView)view.findViewById(R.id.tvidnumber_loan_approvaldisplay);
            mPhone=(TextView)view.findViewById(R.id.tv_phone_loan_approvaldisplay);
            mAmount=(TextView)view.findViewById(R.id.tv_amount_loan_approvaldisplay);
            mType=(TextView)view.findViewById(R.id.tv_loantype_loan_approvaldisplay);
            mPhone.setText(phone);
            mIdnumber.setText(idnumber);
            mFullname.setText(fullname);
            mAmount.setText(amount);
            mType.setText(cat);
        }



        public void  loaning(final String amount, final String cat, final String user_id){
            firebaseFirestore.collection("Loans").document("LonInterest").collection("NotApproved").document(user_id).delete();
            firebaseFirestore.collection("Loans").document("ActualLoan").collection("NotApproved").document(user_id).delete();

            mAmount.setText("");
            progressDialog.setMessage("Loan processing...");
            progressDialog.show();

            float finalAmount = 0;
            if (cat.equals("Long Term Loan")){

                float initialAmountShort = Float.parseFloat(amount);
                float interestShort = (float) (0.12*initialAmountShort);
                finalAmount = interestShort+initialAmountShort;

            }else if (cat.equals("Short Term Loan")){

                float initialAmountLong = Float.parseFloat(amount);
                float interestLong = (float) (0.16*initialAmountLong);
                finalAmount = interestLong+initialAmountLong;
            }


            Map<String, Object> objectMap = new HashMap<>();
            objectMap.put("amount",amount);
            objectMap.put("time", FieldValue.serverTimestamp());
            objectMap.put("user_id",user_id);
            objectMap.put("category",cat);



            final Map<String, Object> objectMap_withIntrest = new HashMap<>();
            objectMap_withIntrest.put("amount",""+finalAmount);
            objectMap_withIntrest.put("time", FieldValue.serverTimestamp());
            objectMap_withIntrest.put("user_id",user_id);
            objectMap_withIntrest.put("category",cat);

            firebaseFirestore.collection("Loans").document("ActualLoan").collection("Approved").document(user_id).set(objectMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    progressDialog.dismiss();
                    new android.support.v7.app.AlertDialog.Builder(context)
                            .setMessage("Loan Approval has been successfully accepted")
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setCancelable(false)
                            .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).show();
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(context, "Error:\n"+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });


            firebaseFirestore.collection("Loans").document("LonInterest").collection("Approved").document(user_id).set(objectMap_withIntrest);

        }

        public void sendNotification(String UserId, String MyUserId){
            Map<String ,String > stringStringMap = new HashMap<>();
            stringStringMap.put("from",MyUserId);
            stringStringMap.put("type","admin_rights");
            firebaseFirestore.collection("Notifications").document(UserId).collection("MyNotifications").add(stringStringMap);
        }
    }


}
