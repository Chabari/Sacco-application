package com.george.mhadhirisaccoltd.MainIssues.Activities;

import android.app.Dialog;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.george.mhadhirisaccoltd.MainIssues.Loans.LoanAdapter;
import com.george.mhadhirisaccoltd.MainIssues.Loans.LoanList;
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

public class Statement extends AppCompatActivity {

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
    private View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statement);


        loanLists = new ArrayList<>();
        recyclerView2 = (RecyclerView)findViewById(R.id.post_list_two3);
        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe3);

        loanAdapter = new LoanAdapter(loanLists);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(Statement.this,1);
        recyclerView2.setLayoutManager(layoutManager);
        recyclerView2.setItemAnimator(new DefaultItemAnimator());
        recyclerView2.setAdapter(loanAdapter);

        auth = FirebaseAuth.getInstance();
        current_user_id = auth.getCurrentUser().getUid();

        setTitle("Statements");
        firebaseFirestore = FirebaseFirestore.getInstance();
        //openingDialog();
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

    @Override
    protected void onStart() {
        super.onStart();
        restartStatementCount();
        openingDialog();
    }
    private void restartStatementCount(){
        firebaseFirestore.collection("StatementsCounts").document(current_user_id).collection("Statements").document("MyStatements").delete();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void openingDialog(){


        final Dialog dialog = new Dialog(Statement.this);
        dialog.setContentView(R.layout.dialog_statementchooser);
        dialog.setCancelable(false);
        dialog.setTitle("Statement Types");

        final Spinner spinner = (Spinner)dialog.findViewById(R.id.sp_statementtypes);
        Button cancel = (Button)dialog.findViewById(R.id.btn_cancel);
        Button go = (Button)dialog.findViewById(R.id.btn_go);

        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (spinner.getSelectedItemPosition()==0){
                    Toast.makeText(Statement.this, "Select statement type...", Toast.LENGTH_SHORT).show();
                }else {
                    if (spinner.getSelectedItemPosition()==1){
                        Query query = firebaseFirestore.collection("Statements").document(current_user_id).collection("MyStatements").orderBy("timeStamp", Query.Direction.DESCENDING).limit(3);
                        query.addSnapshotListener(Statement.this, new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                                for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

                                    if (doc.getType() == DocumentChange.Type.ADDED) {

                                        String postID = doc.getDocument().getId();
                                        LoanList loanList = doc.getDocument().toObject(LoanList.class).withId(postID);

                                        loanLists.add(loanList);

                                        loanAdapter.notifyDataSetChanged();

                                    } else {
                                        Toast.makeText(Statement.this, "No Statements for you ", Toast.LENGTH_SHORT).show();
                                    }
                                }

                            }
                        });
                        dialog.dismiss();
                    }else if (spinner.getSelectedItemPosition()==2){
                        Query query = firebaseFirestore.collection("Statements").document(current_user_id).collection("MyStatements").orderBy("timeStamp", Query.Direction.DESCENDING);
                        query.addSnapshotListener(Statement.this, new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                                for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

                                    if (doc.getType() == DocumentChange.Type.ADDED) {

                                        String postID = doc.getDocument().getId();
                                        LoanList loanList = doc.getDocument().toObject(LoanList.class).withId(postID);

                                        loanLists.add(loanList);

                                        loanAdapter.notifyDataSetChanged();

                                    } else {
                                        Toast.makeText(Statement.this, "No Statements for you ", Toast.LENGTH_SHORT).show();
                                    }
                                }

                            }
                        });
                        dialog.dismiss();
                    }
                }

            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                onBackPressed();

            }
        });
        dialog.show();


    }
}
