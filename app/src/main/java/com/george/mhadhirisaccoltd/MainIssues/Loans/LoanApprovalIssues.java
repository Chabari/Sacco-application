package com.george.mhadhirisaccoltd.MainIssues.Loans;

import android.app.Dialog;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.george.mhadhirisaccoltd.Account.MembershipList;
import com.george.mhadhirisaccoltd.Account.RequestIssueList;
import com.george.mhadhirisaccoltd.MainIssues.Activities.Statement;
import com.george.mhadhirisaccoltd.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class LoanApprovalIssues extends AppCompatActivity {
    private FirebaseAuth auth;
    private FirebaseFirestore firebaseFirestore;
    private List<LoanApprovalList>loanApprovalLists;
    private RecyclerView mRecyclerView;
    private LoanApprovalAddapter loanApprovalAddapter;
    private LinearLayout linearLayout;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loan_approval_issues);

        auth=FirebaseAuth.getInstance();
        firebaseFirestore=FirebaseFirestore.getInstance();

        loanApprovalLists=new ArrayList<>();
        mRecyclerView=(RecyclerView)findViewById(R.id.recycleR_requests_loan_issues);
        swipeRefreshLayout=(SwipeRefreshLayout)findViewById(R.id.swipe_requests_loan_issues);

        linearLayout=(LinearLayout)findViewById(R.id.Linear_layout5);

        loanApprovalAddapter=new LoanApprovalAddapter(loanApprovalLists);
        RecyclerView.LayoutManager layoutManager= new GridLayoutManager(LoanApprovalIssues.this,1);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(loanApprovalAddapter);

        Query query =   firebaseFirestore.collection("Loans").document("ActualLoan").collection("NotApproved").orderBy("time", Query.Direction.DESCENDING);
        query.addSnapshotListener(LoanApprovalIssues.this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

                    if (doc.getType() == DocumentChange.Type.ADDED) {

                        String owner_document_id = doc.getDocument().getId();
                        LoanApprovalList loanApprovalList = doc.getDocument().toObject(LoanApprovalList.class).withId(owner_document_id);

                        loanApprovalLists.add(loanApprovalList);

                        loanApprovalAddapter.notifyDataSetChanged();

                    }
                }

            }
        });


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        swipeRefreshLayout.setRefreshing(false);
                        mRecyclerView.setAdapter(loanApprovalAddapter);

                    }
                },300);
            }
        });


    }
}
