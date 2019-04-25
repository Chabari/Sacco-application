package com.george.mhadhirisaccoltd.Account;


import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.george.mhadhirisaccoltd.MainIssues.Activities.MembershipApproval;
import com.george.mhadhirisaccoltd.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

/**
 * Created by George on 3/9/2019.
 */

public class RequestsAdapter extends RecyclerView.Adapter<RequestsAdapter.ViewHolder>{

    private Context context;
    private List<MembershipList> membershipLists;
    private FirebaseAuth auth;
    private FirebaseFirestore firebaseFirestore;

    public RequestsAdapter(List<MembershipList> membershipLists) {
        this.membershipLists = membershipLists;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.itemdisplayapproval,parent,false);
        context = parent.getContext();
        auth = FirebaseAuth.getInstance();
        firebaseFirestore =FirebaseFirestore.getInstance();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final String owner_user_id = membershipLists.get(position).getUser_id();
        final String user_document  =membershipLists.get(position).postID;

        //seting the values to the item
        firebaseFirestore.collection("Membership").document("Members").collection("Requests").document(owner_user_id).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            if (task.getResult().exists()){
                                final String fullname = task.getResult().getString("fname")+" "+task.getResult().getString("lname");
                                final String id = task.getResult().getString("id_number");
                                final String phone = task.getResult().getString("telephone1");
                                firebaseFirestore.collection("Users").document(owner_user_id).get()
                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if (task.isSuccessful()){
                                                    if (task.getResult().exists()){
                                                        String university = task.getResult().getString("university");
                                                        holder.setAllDetails(fullname,university,id);


                                                        //clicking
                                                        holder.mLinear.setOnClickListener(new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View view) {
                                                                Intent intent = new Intent(context, MembershipApproval.class);
                                                                intent.putExtra("owner_user_id",owner_user_id);
                                                                intent.putExtra("owner_document_id",user_document);
                                                                intent.putExtra("phone",phone);
                                                                view.getContext().startActivity(intent);
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
        return membershipLists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView Fullname,University,IDnumber;
        private LinearLayout mLinear;
        private View view;

        public ViewHolder(View itemView) {
            super(itemView);
            view =itemView;
            mLinear = (LinearLayout)view.findViewById(R.id.itemdisplayapproval);
        }

        public void setAllDetails(String fullname,String university,String idnumber){

            Fullname = (TextView)view.findViewById(R.id.tv_namesisplayapproval);
            University = (TextView)view.findViewById(R.id.tv_universityisplayapproval);
            IDnumber = (TextView)view.findViewById(R.id.tv_idnumberisplayapproval);

            Fullname.setText(fullname);
            University.setText(university);
            IDnumber.setText(idnumber);
        }
    }
}
