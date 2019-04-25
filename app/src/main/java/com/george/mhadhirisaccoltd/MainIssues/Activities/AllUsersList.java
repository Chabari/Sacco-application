package com.george.mhadhirisaccoltd.MainIssues.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.george.mhadhirisaccoltd.Account.MembershipList;
import com.george.mhadhirisaccoltd.Account.RequestIssueList;
import com.george.mhadhirisaccoltd.R;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class AllUsersList extends AppCompatActivity {
    private FirebaseFirestore firebaseFirestore;
    private All_UsersAdapter all_usersAdapter;
    private List<All_Users_List> all_users_lists;
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_users_list);

        mRecyclerView = (RecyclerView)findViewById(R.id.recyclerview_users_list);
        firebaseFirestore = FirebaseFirestore.getInstance();
        all_users_lists = new ArrayList<>();
        all_usersAdapter=new All_UsersAdapter(all_users_lists);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(AllUsersList.this));
        mRecyclerView.setAdapter(all_usersAdapter);


        Query query =  firebaseFirestore.collection("Users").orderBy("time", Query.Direction.DESCENDING);
        query.addSnapshotListener(AllUsersList.this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

                    if (doc.getType() == DocumentChange.Type.ADDED) {

                        String owner_document_id = doc.getDocument().getId();
                        All_Users_List all_users_list = doc.getDocument().toObject(All_Users_List.class).withId(owner_document_id);

                        all_users_lists.add(all_users_list);

                        all_usersAdapter.notifyDataSetChanged();

                    }
                }

            }
        });
    }
}
