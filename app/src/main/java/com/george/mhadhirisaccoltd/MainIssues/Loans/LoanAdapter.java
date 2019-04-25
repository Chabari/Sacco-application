package com.george.mhadhirisaccoltd.MainIssues.Loans;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.george.mhadhirisaccoltd.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.List;

/**
 * Created by sikanga on 25/02/2019.
 */

public class LoanAdapter extends RecyclerView.Adapter<LoanAdapter.ViewHolder> {

    private Context context;
    private List<LoanList> loanLists;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    public LoanAdapter(List<LoanList> loanLists) {
        this.loanLists = loanLists;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.statement_item_display, parent, false);
        context = parent.getContext();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        final String user_id= loanLists.get(position).getUser_id();
        final String postId = loanLists.get(position).postID;
        String current_user_id = firebaseAuth.getCurrentUser().getUid();
        if (user_id.equals(current_user_id)){

            firebaseFirestore.collection("Membership").document("Members").collection("Approval").document(user_id).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if (task.isSuccessful()){
                            if (task.getResult().exists()){

                                String fname = task.getResult().getString("fname");
                                String lname = task.getResult().getString("lname");
                                final String name = fname+" "+lname;
                                final String  phone = task.getResult().getString("telephone1");
                                final String  idnumber = task.getResult().getString("id_number");
                                final String  email= task.getResult().getString("email");


                                firebaseFirestore.collection("MembershipNumbers").document(user_id).get()
                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                                if (task.isSuccessful()){
                                                    if (task.getResult().exists()){
                                                        final String mnumber = task.getResult().getString("number");
                                                        firebaseFirestore.collection("Statements").document(user_id).collection("MyStatements").document(postId).get()
                                                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                                                        if (task.isSuccessful()){
                                                                            if (task.getResult().exists()){
                                                                                String cat = task.getResult().getString("category");

                                                                                String amount = loanLists.get(position).getAmount();
                                                                                Date date = loanLists.get(position).getTimeStamp();
                                                                                String mDate = date.toString();

                                                                                String title = "";

                                                                                if (cat.equals("Make Deposit"))
                                                                                {
                                                                                    title = "Deposit";
                                                                                }else if (cat.equals("Pay Loan")){
                                                                                    title = "Loan Payment";
                                                                                }else {
                                                                                    title = "Loan";
                                                                                }
                                                                                holder.setmName(name,phone,email,mnumber,idnumber,amount,mDate,cat,title);

                                                                            }
                                                                        }
                                                                    }
                                                                }).addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                Toast.makeText(context, "CategoryError: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                                                            }
                                                        });;
                                                    }
                                                }
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(context, "MemberValuesError: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });


                            }
                        }
                    }
                });

        }



    }

    @Override
    public int getItemCount() {
        return loanLists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mName,mEmail,mPhone,mNumber,mId,mAmount,mDate,mTitle;
        private View view;
        public ViewHolder(View itemView) {
            super(itemView);

            view = itemView;
        }

        public void setmName(String name,String phone,String email, String mnumber,String id,String amount,String date,String cat,String Title){
            mName = (TextView)view.findViewById(R.id.tv_fullname_display);
            mEmail = (TextView)view.findViewById(R.id.tv_email_display);
            mPhone = (TextView)view.findViewById(R.id.tv_phonenumber_display);
            mNumber = (TextView)view.findViewById(R.id.tv_membernumber_display);
            mId = (TextView)view.findViewById(R.id.tv_idnumber_display);
            mAmount = (TextView)view.findViewById(R.id.tv_amount_display);
            mDate = (TextView)view.findViewById(R.id.tv_date_display);
            mTitle = (TextView)view.findViewById(R.id.tv_statement_title);

            mName.setText("Fullname: "+name);
            mPhone.setText("Phone Number: "+phone);
            mId.setText("ID Number: "+id);
            mEmail.setText("Email: "+email);
            mNumber.setText("Membership Number: "+mnumber);
            mAmount.setText("Amount: KSH "+amount+"\n\nCategoty: "+cat);
            mDate.setText("Date: "+date);
            mTitle.setText(Title);

        }
    }
}
