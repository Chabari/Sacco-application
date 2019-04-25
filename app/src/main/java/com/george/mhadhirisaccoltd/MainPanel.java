package com.george.mhadhirisaccoltd;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.george.mhadhirisaccoltd.Account.Login;
import com.george.mhadhirisaccoltd.Account.RequestIssueList;
import com.george.mhadhirisaccoltd.MainIssues.Activities.AllUsersList;
import com.george.mhadhirisaccoltd.MainIssues.Activities.Membeship;
import com.george.mhadhirisaccoltd.MainIssues.Activities.Profile_settings;
import com.george.mhadhirisaccoltd.MainIssues.Fragments.About;
import com.george.mhadhirisaccoltd.MainIssues.Fragments.Account;
import com.george.mhadhirisaccoltd.MainIssues.Fragments.Home;
import com.george.mhadhirisaccoltd.MainIssues.Fragments.Notifications;
import com.george.mhadhirisaccoltd.MainIssues.Loans.LoanApprovalIssues;
import com.george.mhadhirisaccoltd.MainIssues.Loans.LoanRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.sql.Time;
import java.util.Calendar;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;
import q.rorbin.badgeview.QBadgeView;

public class MainPanel extends AppCompatActivity {;
    private FirebaseAuth auth;
    private FirebaseFirestore firebaseFirestore;
    private String user_id;

    //declaring frags
    private Home home;
    private Toolbar toolbar;
    private CircleImageView mProfile;
    private Account account;
    private Notifications notifications;
    private TextView mName;
   // private Statement2 statement;
    private About about;
    private BottomNavigationView navigation;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_panel);


        auth=FirebaseAuth.getInstance();
        user_id = auth.getCurrentUser().getUid();
        firebaseFirestore  = FirebaseFirestore.getInstance();

        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        toolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mName=(TextView)findViewById(R.id.tvnametoolbar);
        mProfile=(CircleImageView)findViewById(R.id.profile_image);

        //objects
        home = new Home();
        account = new Account();
        notifications = new Notifications();
        //statement = new Statement2();
        about = new About();

        firebaseFirestore.collection("Loans").document("LonInterest").collection("Approved").document(user_id).get()
                .addOnCompleteListener(
                        new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()){
                                    if (task.getResult().exists()){
                                        Timestamp date =  task.getResult().getTimestamp("time");
                                        String Amount = task.getResult().getString("amount");
                                        String cat = task.getResult().getString("category");

                                      /* String givenDateString = initialDate;
                                        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");*/
                                        try {
                                           /* Date mDate = sdf.parse(givenDateString);
                                            long timeInMilliseconds = mDate.getTime();*/

                                            Calendar current = Calendar.getInstance();

                                            long currentDateTime = current.getTimeInMillis();

                                            long diff = currentDateTime-(date.getSeconds()*1000);


                                            long secs = diff/1000;
                                            long min = secs/60;
                                            long hr = min/60;
                                            long day = hr/24;

                                            if (day  == 20  && cat.equals("Short Term Loan")){
                                                String message = "You are reminded to pay your loan of KSH "+Amount+"\n of time period 2 months";
                                                addNotification(message);
                                                showDialog(message);
                                            }else if(day  == 50  && cat.equals("Short Term Loan")){
                                                String message = "You are reminded to pay your loan of KSH "+Amount+"\n of time period 2 months";
                                                addNotification(message);
                                                showDialog(message);
                                            }else if(day  == 60  && cat.equals("Short Term Loan")){
                                                String message = "Today is the deadline to pay your loan of KSH "+Amount+"\n of time period 2 months";
                                                addNotification(message);
                                                showDialog(message);
                                            }else if(day  > 60  && cat.equals("Short Term Loan")){
                                                String message = "You are required to pay your loan of KSH "+Amount+"\n of time period 2 months";
                                                addNotification(message);
                                                showDialog(message);
                                            }




                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }


                                    }
                                }
                            }
                        }
                );


        BottomNavigationMenuView bottomNavigationMenuView = (BottomNavigationMenuView) navigation.getChildAt(0);
        final View v = bottomNavigationMenuView.getChildAt(2);
        View v1 =bottomNavigationMenuView.getChildAt(3);  // number of menu from left

        new QBadgeView(this).bindTarget(v1).setBadgeNumber(9);







        initializeFragment();
        checkImage();

        mProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainPanel.this, Profile_settings.class));
            }
        });



        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.main_container);

                switch (item.getItemId()){

                    case R.id.navigation_home:
                        fragmentReplacement(home,currentFragment);


                        firebaseFirestore.collection("Membership").document("Members").collection("Requests").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()){
                                    if (task.getResult().exists()){
                                        String fname=task.getResult().getString("fname");
                                        String lname=task.getResult().getString("lname");
                                        String fullname=fname+" "+lname;
                                        mName.setText(fullname);


                                    }else {
                                        firebaseFirestore.collection("Membership").document("Members").collection("Approval").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if (task.isSuccessful()){
                                                    if (task.getResult().exists()){
                                                        String fname=task.getResult().getString("fname");
                                                        String lname=task.getResult().getString("lname");
                                                        String fullname=fname+" "+lname;
                                                        mName.setText(fullname);


                                                    }else {
                                                        checkImage();

                                                    }
                                                }
                                            }
                                        });
                                    }
                                }
                            }
                        });

                        return true;
                    case R.id.navigation_notifications:
                        fragmentReplacement(notifications,currentFragment);
                        mName.setText("Notifications");
                        return true;
                    case R.id.navigation_statements:
                        //fragmentReplacement(statement,currentFragment);
                        Intent intent=new Intent(getApplicationContext(), com.george.mhadhirisaccoltd.MainIssues.Activities.Statement.class);
                        startActivity(intent);
                        mName.setText("Statements");

                        return true;
                    case R.id.navigation_about:
                        fragmentReplacement(about,currentFragment);
                        mName.setText("About Mhadhiri");
                        return true;
                    case R.id.navigation_Account:
                        fragmentReplacement(account,currentFragment);
                        mName.setText("My Account");
                        return true;
                    default:
                        return false;


                }
            }
        });



    }

    @Override
    public void invalidateOptionsMenu() {
        super.invalidateOptionsMenu();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);

        final MenuItem viewrequest = menu.findItem(R.id.action_viewrequests);
        final MenuItem viewloans=menu.findItem(R.id.action_view_loan_requests);
        final MenuItem viewUsers=menu.findItem(R.id.action_viewusers);
        firebaseFirestore.collection("Users").document(user_id).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if (task.isSuccessful()){
                            if (task.getResult().exists()){
                                String rights = task.getResult().getString("rights");
                                if (rights.equals("admin")){
                                    viewrequest.setVisible(true);
                                    viewloans.setVisible(true);
                                    viewUsers.setVisible(true);
                                }
                            }
                        }
                    }
                });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.main_container);

        //open homefragment
        int id = item.getItemId();
        if (id == R.id.action_membership){
            firebaseFirestore.collection("Membership").document("Members").collection("Requests").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()){
                        if (task.getResult().exists()){
                            Toast.makeText(MainPanel.this, "Your request is not yet approved wait for approval", Toast.LENGTH_LONG).show();


                        }else {
                            firebaseFirestore.collection("Membership").document("Members").collection("Approval").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()){
                                        if (task.getResult().exists()){
                                            Toast.makeText(MainPanel.this, "You are already a member For any changes contact or visit main office", Toast.LENGTH_LONG).show();


                                        }else {
                                            startActivity(new Intent(MainPanel.this, Membeship.class));

                                        }
                                    }
                                }
                            });
                        }
                    }
                }
            });

        }
        if (id == R.id.action_home){
            fragmentReplacement(home,currentFragment);
        }
        if (id == R.id.action_account) {
            //open homefragment
            fragmentReplacement(account,currentFragment);
            mName.setText("My Account");
        }
        if (id == R.id.action_notifications){
            fragmentReplacement(notifications,currentFragment);
           mName.setText("Notifications");
        }
        if (id == R.id.action_statement){
           // fragmentReplacement(statement,currentFragment);
            Intent intent=new Intent(getApplicationContext(), com.george.mhadhirisaccoltd.MainIssues.Activities.Statement.class);
            startActivity(intent);
        }

        if (id == R.id.action_about){
            fragmentReplacement(about,currentFragment);
            mName.setText("About Mhadhiri");
        }

        if (id == R.id.action_requestloan){
            Intent intent=new Intent(getApplicationContext(), LoanRequest.class);
            startActivity(intent);

        }

        if (id == R.id.action_viewrequests){
            Intent intent=new Intent(getApplicationContext(), RequestIssueList.class);
            startActivity(intent);

        }
        if (id == R.id.action_view_loan_requests){
            Intent intent=new Intent(getApplicationContext(), LoanApprovalIssues.class);
            startActivity(intent);

        }
        if (id == R.id.action_viewusers){
            Intent intent=new Intent(getApplicationContext(), AllUsersList.class);
            startActivity(intent);

        }

        if (id == R.id.action_share){
            String message = "Hi....!! You can now use Mhadhiri Sacco Android App to....";

            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT,message);
            sendIntent.setType("text/plain");
            Intent.createChooser(sendIntent,"Share Glac app via");
            startActivity(sendIntent);

        }
        if (id == R.id.action_logout){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Exiting...");
            builder.setMessage("Are you sure you want to logout from your account?");
// Add the buttons
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    startActivity(new Intent(MainPanel.this,Login.class));
                    auth.signOut();
                    finish();
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();

        }
        if (id == R.id.action_rate_us){
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=Mhadhiri Sacco")));

        }

        return super.onOptionsItemSelected(item);
    }



    private void initializeFragment(){


        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.main_container,home);
        fragmentTransaction.add(R.id.main_container,notifications);
        fragmentTransaction.add(R.id.main_container,about);
        //fragmentTransaction.add(R.id.main_container,statement);
        fragmentTransaction.add(R.id.main_container,account);

        fragmentTransaction.hide(notifications);
        fragmentTransaction.hide(account);
        fragmentTransaction.hide(about);
        //fragmentTransaction.hide(statement);
        fragmentTransaction.commit();
        firebaseFirestore.collection("Membership").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    if (task.getResult().exists()){
                        String fname=task.getResult().getString("fname");
                        String lname=task.getResult().getString("lname");
                        String fullname=fname+" "+lname;
                        mName.setText(fullname);
                        checkImage();

                    }else {
                        checkImage();
                    }
                }
            }
        });


    }


    private void fragmentReplacement(Fragment fragment,Fragment currentFragment){

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if(fragment == home){

            fragmentTransaction.hide(notifications);
            fragmentTransaction.hide(account);
            fragmentTransaction.hide(about);
           // fragmentTransaction.hide(statement);

        }
        if(fragment == account){

            fragmentTransaction.hide(notifications);
            fragmentTransaction.hide(home);
            fragmentTransaction.hide(about);
          //  fragmentTransaction.hide(statement);

        }
        if(fragment == notifications){

            fragmentTransaction.hide(home);
            fragmentTransaction.hide(account);
            fragmentTransaction.hide(about);
          //  fragmentTransaction.hide(statement);

        }
       /* if(fragment == statement){

            fragmentTransaction.hide(home);
            fragmentTransaction.hide(account);
            fragmentTransaction.hide(about);
            fragmentTransaction.hide(notifications);

        }*/
        if(fragment == about){

            fragmentTransaction.hide(home);
            fragmentTransaction.hide(account);
            fragmentTransaction.hide(notifications);
            //fragmentTransaction.hide(statement);

        }
        fragmentTransaction.show(fragment);
        //fragmentTransaction.replace(R.id.main_container,fragment);
        fragmentTransaction.commit();



    }

    private void checkImage(){
        firebaseFirestore.collection("ProfileImages").document(user_id)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()){
                    if (!task.getResult().exists()){
                        mName.setText("<--Pick Image");
                    }else {
                        String imageUrl = task.getResult().getString("imageUrl");
                        RequestOptions requestOptions = new RequestOptions();
                        requestOptions.centerCrop();
                        Glide.with(MainPanel.this).applyDefaultRequestOptions(requestOptions).load(imageUrl).into(mProfile);
                    }
                }
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();

        final BottomNavigationMenuView bottomNavigationMenuView = (BottomNavigationMenuView) navigation.getChildAt(0);

        final View v = bottomNavigationMenuView.getChildAt(2);

        try
        {
            firebaseFirestore.collection("StatementsCounts").document(user_id).collection("Statements").document("MyStatements").get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                            if (task.isSuccessful()){
                                if (task.getResult().exists()){
                                    int count = Integer.parseInt(task.getResult().getString("count"));
                                    new QBadgeView(MainPanel.this).bindTarget(v).setBadgeNumber(count);
                                }else {
                                    new QBadgeView(MainPanel.this).bindTarget(v).setBadgeNumber(0);

                                }
                            }
                        }
                    });

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void addNotification(String Message) {
        NotificationCompat.Builder builder =
                (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.abc)
                        .setContentTitle("Mhadhiri Sacco Ltd")
                        .setContentText(Message);

        Intent notificationIntent = new Intent(this, MainPanel.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);

        // Add as notification
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, builder.build());
    }

    private void showDialog(String Message){
        new AlertDialog.Builder(MainPanel.this)
                .setIcon(R.drawable.ic_action_name)
                .setMessage(Message)
                .setTitle("Loan Payment Reminder")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).show();
    }

    boolean pressed = false;

    @Override
    public void onBackPressed() {
        if (pressed){
            super.onBackPressed();
           // auth.signOut();
            finish();
            return;
        }

        this.pressed = true;
        Toast.makeText(this, "Double tap to Exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                pressed = false;
            }
        },500);
    }
}
