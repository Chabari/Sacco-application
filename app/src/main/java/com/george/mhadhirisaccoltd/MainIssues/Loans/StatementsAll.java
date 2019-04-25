package com.george.mhadhirisaccoltd.MainIssues.Loans;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.george.mhadhirisaccoltd.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class StatementsAll extends AppCompatActivity {
    private FirebaseAuth auth;
    private RecyclerView recyclerView1,recyclerView2;
    private List<LoanList> loanLists;
    private LoanAdapter loanAdapter;

    private FirebaseFirestore firebaseFirestore;
    private Boolean isRecentPost = true;
    private DocumentSnapshot lastVisible;
    private String user_country;
    private String current_user_id;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statements_all);

        loanLists = new ArrayList<>();
        recyclerView2 = (RecyclerView)findViewById(R.id.post_list_two12);
        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_two);

        loanAdapter = new LoanAdapter(loanLists);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(StatementsAll.this,1);
        recyclerView2.setLayoutManager(layoutManager);
        recyclerView2.setItemAnimator(new DefaultItemAnimator());
        recyclerView2.setAdapter(loanAdapter);

        auth = FirebaseAuth.getInstance();
        current_user_id = auth.getCurrentUser().getUid();


        firebaseFirestore = FirebaseFirestore.getInstance();
        Query query = firebaseFirestore.collection("Loans").document(current_user_id).collection("MyStatements").orderBy("timeStamp", Query.Direction.DESCENDING);
        query.addSnapshotListener(StatementsAll.this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

                    if (doc.getType() == DocumentChange.Type.ADDED) {

                        String postID = doc.getDocument().getId();
                        LoanList loanList = doc.getDocument().toObject(LoanList.class).withId(postID);

                        loanLists.add(loanList);

                        loanAdapter.notifyDataSetChanged();

                    } else {
                        Toast.makeText(StatementsAll.this, "No post for ", Toast.LENGTH_SHORT).show();
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
                        recyclerView2.setAdapter(loanAdapter);

                    }
                },300);
            }
        });
    }
}
