package com.george.mhadhirisaccoltd.MainIssues.Fragments;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.NotificationCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.george.mhadhirisaccoltd.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * A simple {@link Fragment} subclass.
 */
public class Home extends Fragment {
    private View view;
    private TextView mWelcome;
    private String user_id;
    private FirebaseAuth auth;
    private TextView mMembership;
    private FirebaseFirestore firebaseFirestore;
    private Handler mHandler;
    private Runnable mRunnable;
    private int mInterval = 300; // milliseconds
    private boolean initialState = true;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_home, container, false);

        firebaseFirestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        user_id = auth.getCurrentUser().getUid();

        mWelcome = (TextView) view.findViewById(R.id.welcome);
        mMembership = (TextView) view.findViewById(R.id.tv_Membership);
        mHandler = new Handler();

        firebaseFirestore.collection("Membership").document("Members").collection("Requests").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().exists()) {
                        String fname = task.getResult().getString("fname");
                        String lname = task.getResult().getString("lname");
                        String fullname = fname + " " + lname;
                        mWelcome.setText("\tWelcome " + fullname + ", to Mhadhiri Sacco Ltd where you can apply for loans and view your statements. \nAll the above mentioned processes can be applicable if you are a member...");
                        mMembership.setText("Awaiting for approval");
                        mMembership.setVisibility(View.VISIBLE);

                    } else {
                        firebaseFirestore.collection("Membership").document("Members").collection("Approval").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    if (task.getResult().exists()) {
                                        String fname = task.getResult().getString("fname");
                                        String lname = task.getResult().getString("lname");
                                        String fullname = fname + " " + lname;
                                        mWelcome.setText("\tWelcome " + fullname + ", to Mhadhiri Sacco Ltd where you can apply for loans and view your statements. \nAll the above mentioned processes can be applicable if you are a member...");

                                    } else {
                                        mWelcome.setText("New Member!!.\n\tWelcome, to Mhadhiri Sacco Ltd where you can apply for loans and view your statements. \nAll the above mentioned processes can be applicable if you are a member.\nPress the above plus symbol to join membership...");
                                        mMembership.setText("New Member");
                                        mMembership.setVisibility(View.VISIBLE);
                                    }
                                }
                            }
                        });
                    }
                }
            }
        });

        mRunnable = new Runnable() {

            @Override
            public void run() {
                // Do the task
                blink(mMembership);
            }
        };

        mHandler.postDelayed(mRunnable,mInterval);


        return view;
    }

    void blink(TextView textView) {

        if (initialState) {
            initialState = false;
            textView.setTextColor(Color.RED);
        } else {
            initialState = true;
            textView.setTextColor(Color.BLACK);
        }

        mHandler.postDelayed(mRunnable,mInterval);

    }
}
