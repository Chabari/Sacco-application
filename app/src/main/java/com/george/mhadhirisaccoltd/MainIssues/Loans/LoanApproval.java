package com.george.mhadhirisaccoltd.MainIssues.Loans;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.george.mhadhirisaccoltd.R;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoanApproval extends AppCompatActivity {
    private TextView mLoantextview;
    private Button mAccept,mDecline;
    private FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loan_approval);
    }
}
