package com.george.mhadhirisaccoltd.Account;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.george.mhadhirisaccoltd.MainIssues.Activities.Statement;
import com.george.mhadhirisaccoltd.MainIssues.Loans.LoanList;
import com.george.mhadhirisaccoltd.R;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class RequestIssueList extends AppCompatActivity {
    private FirebaseFirestore firebaseFirestore;
    private List<MembershipList> membershipLists;
    private RequestsAdapter requestsAdapter;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_issue_list);

        firebaseFirestore = FirebaseFirestore.getInstance();
        membershipLists = new ArrayList<>();
        requestsAdapter = new RequestsAdapter(membershipLists);

        mRecyclerView = (RecyclerView)findViewById(R.id.recycleR_requests);
        mSwipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_requests);

        //seting recycler
        mRecyclerView.setLayoutManager(new LinearLayoutManager(RequestIssueList.this));
        mRecyclerView.setAdapter(requestsAdapter);

        Query query =  firebaseFirestore.collection("Membership").document("Members").collection("Requests").orderBy("timeStamp", Query.Direction.DESCENDING);
        query.addSnapshotListener(RequestIssueList.this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

                    if (doc.getType() == DocumentChange.Type.ADDED) {

                        String owner_document_id = doc.getDocument().getId();
                        MembershipList membershipList = doc.getDocument().toObject(MembershipList.class).withId(owner_document_id);

                        membershipLists.add(membershipList);

                        requestsAdapter.notifyDataSetChanged();

                    }
                }

            }
        });
    }
}
