package com.george.mhadhirisaccoltd.MainIssues.Activities;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.george.mhadhirisaccoltd.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.Manifest.permission.SEND_SMS;

public class MembershipApproval extends AppCompatActivity {

    private TextView mTextView;
    private Button mDecline, mAccept;
    private FirebaseFirestore firebaseFirestore;
    private BroadcastReceiver sentStatusReceiver,deliveredStatusReceiver;
    private final static int REQUEST_SMS = 0;
    private ProgressDialog progressDialog;
    private String user_id,owner_id,phone;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_membership_approval);

        firebaseFirestore = FirebaseFirestore.getInstance();
        mTextView = (TextView) findViewById(R.id.Approvalmembership);
        mAccept = (Button) findViewById(R.id.Acceptmembership);
        mDecline = (Button) findViewById(R.id.Declinemembership);
        progressDialog=new ProgressDialog(this);

        owner_id = getIntent().getExtras().getString("owner_user_id");
        phone = getIntent().getExtras().getString("phone");

        firebaseFirestore.collection("Membership").document("Members").collection("Requests").document(owner_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()){
                    if (task.getResult().exists()){
                        String fname= task.getResult().getString("fname");
                        String lname=task.getResult().getString("lname");
                        final String idnumber=task.getResult().getString("id_number");
                        final String fullname=fname+" "+lname;

                        firebaseFirestore.collection("Users").document(owner_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                if (task.isSuccessful()){
                                    if (task.getResult().exists()){
                                        String university=task.getResult().getString("university");

                                        mTextView.setText(fullname+"\n"+idnumber+"\n"+university);

                                    }
                                }
                            }
                        });
                    }
                }
            }
        });




        mDecline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               new AlertDialog.Builder(MembershipApproval.this)
                       .setTitle("Declining")
                       .setMessage("Are you sure you want to decline?")
                       .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                           @Override
                           public void onClick(DialogInterface dialogInterface, int i) {
                               firebaseFirestore.collection("Membership").document("Members").collection("Requests").document(owner_id).delete().addOnCompleteListener(
                                       new OnCompleteListener<Void>() {
                                           @Override
                                           public void onComplete(@NonNull Task<Void> task) {
                                               if (task.isSuccessful()){
                                                   //sendingsms
                                                   String Message = "DECLINE:\nYour Mhadhiri membership request has been declined";
                                                   checkingPermisionMain(phone,Message);
                                                   onBackPressed();
                                               }
                                           }
                                       }
                               );
                           }
                       }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialogInterface, int i) {
                       dialogInterface.dismiss();
                   }
               }).show();
            }
        });
        mAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new AlertDialog.Builder(MembershipApproval.this)
                        .setTitle("Membership Approval")
                        .setMessage("Are you sure you want to accept the request?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {


                                progressDialog.setMessage("Approving.... ");
                                progressDialog.show();
                                Aproved(owner_id,phone);
                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).show();

            }
        });
    }

    private void Aproved(String Owner_Id, final String PhoneNumber) {
        firebaseFirestore.collection("Membership").document("Members").collection("Requests").document(Owner_Id)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                String othernames = task.getResult().getString("Other_names");
                String passportnumber = task.getResult().getString("Passport_number");
                String dateofbirth = task.getResult().getString("DoB");
                String telephone1 = task.getResult().getString("telephone1");
                String telephone2 = task.getResult().getString("telephone2");
                String postaladdress = task.getResult().getString("postal_address");
                String employer = task.getResult().getString("employer");
                String employercontact = task.getResult().getString("employer_contact");
                String employercontactperson = task.getResult().getString("contact_person");
                String residdentialaddress = task.getResult().getString("residential_address");
                String locationofwork = task.getResult().getString("location_of_work");
                String jobtitle = task.getResult().getString("job_title");
                String dateofretirement = task.getResult().getString("DoR");
                String gender = task.getResult().getString("gender");
                String employmenttype = task.getResult().getString("employmenttype");
                String proposedcontribution = task.getResult().getString("proposedcontribution");
                String  sourcesofincome = task.getResult().getString("sourcesofincome");
                String  method = task.getResult().getString("method");
                String  fname = task.getResult().getString("fname");
                String  lname = task.getResult().getString("lname");
                String email = task.getResult().getString("email");
                String idnumber = task.getResult().getString("id_number");
                String KRA = task.getResult().getString("kra");
                String passportCopy = task.getResult().getString("passportsize");

                Map<String, String> stringMap = new HashMap<>();
                stringMap.put("Other_names", othernames);
                stringMap.put("Passport_number", passportnumber);
                stringMap.put("DoB", dateofbirth);
                stringMap.put("telephone1", telephone1);
                stringMap.put("telephone2", telephone2);
                stringMap.put("postal_address", postaladdress);
                stringMap.put("residential_address", residdentialaddress);
                stringMap.put("employer", employer);
                stringMap.put("employer_contact", employercontact);
                stringMap.put("contact_person", employercontactperson);
                stringMap.put("location_of_work", locationofwork);
                stringMap.put("job_title", jobtitle);
                stringMap.put("DoR", dateofretirement);
                stringMap.put("gender", gender);
                stringMap.put("employmenttype", employmenttype);
                stringMap.put("proposedcontribution", proposedcontribution);
                stringMap.put("sourcesofincome", sourcesofincome);
                stringMap.put("method", method);
                stringMap.put("fname", fname);
                stringMap.put("lname", lname);
                stringMap.put("email", email);
                stringMap.put("id_number", idnumber);
                stringMap.put("passport", passportnumber);
                stringMap.put("kra", KRA);
                stringMap.put("passportsize", passportCopy);

                firebaseFirestore.collection("Membership").document("Members").collection("Approval").document(owner_id).set(stringMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(MembershipApproval.this, "Member successfully Approved", Toast.LENGTH_SHORT).show();
                            firebaseFirestore.collection("Membership").document("Members").collection("Requests").document(owner_id).delete().addOnCompleteListener(
                                    new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            //send acceptance
                                            String Message = "Hi? Your Mhadhiri Sacco Ltd membership request has been accepted";
                                            checkingPermisionMain(PhoneNumber,Message);

                                        }
                                    }
                            );
                        }
                        progressDialog.dismiss();
                    }
                });
            }
        });


    }


    private void checkingPermisionMain(String Phone,String Message) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            int hasSMSPermission = checkSelfPermission(SEND_SMS);
            if (hasSMSPermission != PackageManager.PERMISSION_GRANTED) {
                if (!shouldShowRequestPermissionRationale(SEND_SMS)) {
                    showMessageOKCancel("You need to allow access to Send SMS",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        requestPermissions(new String[]{SEND_SMS},
                                                REQUEST_SMS);
                                    }
                                }
                            });
                    return;
                }
                requestPermissions(new String[]{SEND_SMS},
                        REQUEST_SMS);
                return;
            }
        }
        sendMySMS(Phone,Message);
    }

    private void sendMySMS(String Phone, String Message) {

        String phone = Phone;
        String message = Message;

        //Check if the phoneNumber is empty
        if (phone.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Please Enter a Valid Phone Number", Toast.LENGTH_SHORT).show();
        } else {

            SmsManager sms = SmsManager.getDefault();
            // if message length is too long messages are divided
            List<String> messages = sms.divideMessage(message);
            for (String msg : messages) {

                PendingIntent sentIntent = PendingIntent.getBroadcast(this, 0, new Intent("SMS_SENT"), 0);
                PendingIntent deliveredIntent = PendingIntent.getBroadcast(this, 0, new Intent("SMS_DELIVERED"), 0);
                sms.sendTextMessage(phone, null, msg, sentIntent, deliveredIntent);

            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        sentStatusReceiver=new BroadcastReceiver() {

            @Override
            public void onReceive(Context arg0, Intent arg1) {
                String s = "Unknown Error";
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        s = "Message Sent Successfully !!";
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        s = "Generic Failure Error";
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        s = "Error : No SMS Service Available";
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        s = "Error : Null PDU";
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        s = "Error : Radio is off";
                        break;
                    default:
                        break;
                }
                Toast.makeText(arg0, s, Toast.LENGTH_SHORT).show();

            }
        };
        deliveredStatusReceiver=new BroadcastReceiver() {

            @Override
            public void onReceive(Context arg0, Intent arg1) {
                String s = "Message Not Delivered";
                switch(getResultCode()) {
                    case Activity.RESULT_OK:
                        s = "Delivered Successfully";
                        break;
                    case Activity.RESULT_CANCELED:
                        break;
                }
                Toast.makeText(MembershipApproval.this, s, Toast.LENGTH_SHORT).show();
            }
        };
        registerReceiver(sentStatusReceiver, new IntentFilter("SMS_SENT"));
        registerReceiver(deliveredStatusReceiver, new IntentFilter("SMS_DELIVERED"));
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(sentStatusReceiver);
        unregisterReceiver(deliveredStatusReceiver);
    }

    private boolean checkPermission() {
        return ( ContextCompat.checkSelfPermission(getApplicationContext(), SEND_SMS ) == PackageManager.PERMISSION_GRANTED);
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{SEND_SMS}, REQUEST_SMS);
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_SMS:
                if (grantResults.length > 0 &&  grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(getApplicationContext(), "Permission Granted, Now you can access sms", Toast.LENGTH_SHORT).show();


                }else {
                    Toast.makeText(getApplicationContext(), "Permission Denied, You cannot access and sms", Toast.LENGTH_SHORT).show();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(SEND_SMS)) {
                            showMessageOKCancel("You need to allow access to both the permissions",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermissions(new String[]{SEND_SMS},
                                                        REQUEST_SMS);
                                            }
                                        }
                                    });
                            return;
                        }
                    }
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new android.support.v7.app.AlertDialog.Builder(MembershipApproval.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }
}
