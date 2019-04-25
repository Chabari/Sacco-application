package com.george.mhadhirisaccoltd.MainIssues.Activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.george.mhadhirisaccoltd.Account.AccountSettings;
import com.george.mhadhirisaccoltd.Account.RandomMembershipNumber;
import com.george.mhadhirisaccoltd.MainIssues.Fragments.Home;
import com.george.mhadhirisaccoltd.MainIssues.Loans.FileUtils;
import com.george.mhadhirisaccoltd.MainPanel;
import com.george.mhadhirisaccoltd.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Membeship extends AppCompatActivity {

    private EditText mOthernames,mPassportnumber,mDateofBirt,mTelephone1,mTelephone2,mPostalAddress,mResiddentialAdress,mEmployer,
            mEmployerContactsPerson,mEmployerContact,mLocationofWork,mJobTitle,mDateofRetirement,mlname,mfname,idNumber,mEmail;
    private RadioButton mmale,mfemale,mPermanentandPensionable,mContract,mTemporary,mMonthlyContribution,mShareCapital,mIagree,mDecline,mOtherSource,mPension,mSalary,motherproposed;
    private CheckBox mCheckoff,mStandingorder,mDirectDeposit,mOthersCheck,mPassportcopy,mKraPin,mStaffid,midCopy;
    private Button mSubmit;
    private FirebaseAuth auth;
    private String user_id;
    private ProgressDialog progressDialog;
    private FirebaseFirestore firebaseFirestore;
    private TextToSpeech t1;
    private static final int PICKFILE_RESULT_CODE = 0;
    private Uri uri;
    String path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_membeship);

        firebaseFirestore=FirebaseFirestore.getInstance();
        auth=FirebaseAuth.getInstance();
        user_id=auth.getCurrentUser().getUid();
        progressDialog=new ProgressDialog(this);

        //Edittexts

        mOthernames=(EditText)findViewById(R.id.edtOtherNames);
        mPassportnumber=(EditText)findViewById(R.id.edtPassportNumber);
        mDateofBirt=(EditText)findViewById(R.id.edt_date_of_birth);
        mTelephone1=(EditText)findViewById(R.id.edt_telephone1);
        mTelephone2=(EditText)findViewById(R.id.edt_telephone2);
        mPostalAddress=(EditText)findViewById(R.id.edtPostAdress);
        mResiddentialAdress=(EditText)findViewById(R.id.edtResidential);
        mEmployer=(EditText)findViewById(R.id.edtemployer);
        mEmployerContact=(EditText)findViewById(R.id.edt_Employer_contacts);
        mEmployerContactsPerson=(EditText)findViewById(R.id.edt_employercontactPerson);
        mLocationofWork=(EditText)findViewById(R.id.edt_location_of_workstation);
        mJobTitle=(EditText)findViewById(R.id.edt_job_title);
        mDateofRetirement=(EditText)findViewById(R.id.edt_expecte_date);
        mlname=(EditText)findViewById(R.id.edtlnameMembership);
        mfname=(EditText)findViewById(R.id.edtfnameMembership);
        mEmail=(EditText)findViewById(R.id.edtemailMembership);
        idNumber=(EditText)findViewById(R.id.edtNationalidMembership);


        //Radiobuttons
        mmale=(RadioButton)findViewById(R.id.radio_male);
        mfemale=(RadioButton)findViewById(R.id.radio_female);
        mPermanentandPensionable=(RadioButton)findViewById(R.id.radPermanent_and_pensionable);
        mContract=(RadioButton)findViewById(R.id.rad_contract);
        mTemporary=(RadioButton)findViewById(R.id.rad_temporary);
        mMonthlyContribution=(RadioButton)findViewById(R.id.rad_contributions);
        mShareCapital=(RadioButton)findViewById(R.id.rad_Share_capital);
        mIagree=(RadioButton)findViewById(R.id.rad_iagree);
        mDecline=(RadioButton)findViewById(R.id.rad_decline);
        mSalary=(RadioButton) findViewById(R.id.checksalary);
        mPension=(RadioButton)findViewById(R.id.check_pension);
        mOtherSource=(RadioButton)findViewById(R.id.check_other_source);
        motherproposed=(RadioButton)findViewById(R.id.radothers);

        //Checkboxs
        mCheckoff=(CheckBox)findViewById(R.id.check_checkoff);
        mStandingorder=(CheckBox)findViewById(R.id.check_standingorder);
        mDirectDeposit=(CheckBox)findViewById(R.id.check_deposit);
        mOthersCheck=(CheckBox)findViewById(R.id.check_others);
        mPassportcopy=(CheckBox)findViewById(R.id.check_passport_photograph);
        mStaffid=(CheckBox)findViewById(R.id.check_staff_id);
        mKraPin=(CheckBox)findViewById(R.id.check_kra_pin);
        midCopy=(CheckBox)findViewById(R.id.check_idcopies);


        mSubmit=(Button)findViewById(R.id.Submitmembership);
        t1=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    t1.setLanguage(Locale.UK);
                }
            }
        });

        //picking files
        mPassportcopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                intent.addCategory(Intent.CATEGORY_OPENABLE);

                try{

                    startActivityForResult(
                            Intent.createChooser(intent,"Select a file to upload"),PICKFILE_RESULT_CODE
                    );
                }catch (android.content.ActivityNotFoundException e){
                    Toast.makeText(Membeship.this, "Please install a file manager..", Toast.LENGTH_SHORT).show();
                }

            }
        });
        mKraPin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                intent.addCategory(Intent.CATEGORY_OPENABLE);

                try{

                    startActivityForResult(
                            Intent.createChooser(intent,"Select a file to upload"),PICKFILE_RESULT_CODE
                    );
                }catch (android.content.ActivityNotFoundException e){
                    Toast.makeText(Membeship.this, "Please install a file manager..", Toast.LENGTH_SHORT).show();
                }

            }
        });
        mStaffid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                intent.addCategory(Intent.CATEGORY_OPENABLE);

                try{

                    startActivityForResult(
                            Intent.createChooser(intent,"Select a file to upload"),PICKFILE_RESULT_CODE
                    );
                }catch (android.content.ActivityNotFoundException e){
                    Toast.makeText(Membeship.this, "Please install a file manager..", Toast.LENGTH_SHORT).show();
                }

            }
        });
        midCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                intent.addCategory(Intent.CATEGORY_OPENABLE);

                try{

                    startActivityForResult(
                            Intent.createChooser(intent,"Select a file to upload"),PICKFILE_RESULT_CODE
                    );
                }catch (android.content.ActivityNotFoundException e){
                    Toast.makeText(Membeship.this, "Please install a file manager..", Toast.LENGTH_SHORT).show();
                }

            }
        });

        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email=mEmail.getText().toString().trim();
                String lname=mlname.getText().toString().trim();
                String fname=mfname.getText().toString().trim();
                String idnumber=idNumber.getText().toString().trim();
                String othernames=mOthernames.getText().toString().trim();
                String passportnumber=mPassportnumber.getText().toString().trim();
                String dateofbirth=mDateofBirt.getText().toString().trim();
                String telephone1=mTelephone1.getText().toString().trim();
                String telephone2=mTelephone2.getText().toString().trim();
                String postaladdress=mPostalAddress.getText().toString().trim();
                String residdentialaddress=mResiddentialAdress.getText().toString().trim();
                String employer=mEmployer.getText().toString().trim();
                String employercontact=mEmployerContact.getText().toString().trim();
                String employercontactperson=mEmployerContactsPerson.getText().toString().trim();
                String locationofwork=mLocationofWork.getText().toString().trim();
                String jobtitle=mJobTitle.getText().toString().trim();
                String dateofretirement=mDateofRetirement.getText().toString().trim();

                //radios

                String gender=null;
                if (mmale.isChecked()){
                    gender="male";
                }
                if (mfemale.isChecked()){
                    gender="female";
                }

                String employmenttype=null;
                if (mPermanentandPensionable.isChecked()){
                    employmenttype="Permanent and pensionable";
                }if (mContract.isChecked()){
                    employmenttype="Contract";
                }if (mTemporary.isChecked()){
                    employmenttype="Temporary";
                }

                String proposedcontribution=null;
                if (mMonthlyContribution.isChecked()){
                    proposedcontribution="Monthly Contribution";
                }if (mShareCapital.isChecked()){
                    proposedcontribution="Shared Capital";
                }if (motherproposed.isChecked()){
                    proposedcontribution="Others";
                }

                String sourcesofincome=null;
                if (mSalary.isChecked()){
                    sourcesofincome="Salary";
                }if (mPension.isChecked()){
                    sourcesofincome="Pension";
                }if (mOtherSource.isChecked()){
                    sourcesofincome="Other";
                }

                String method=null;
                if (mCheckoff.isChecked()){
                    method="Checkoff";
                }if (mStandingorder.isChecked()){
                    method="Standingorder";
                }if (mDirectDeposit.isChecked()){
                    method="DirectDeposit";
                }if (mOthersCheck.isChecked()){
                    method="OthersCheck";
                }

                String passportCopy=null;
                String KRA=null;
                String StaffId=null;
                String IDCopy=null;
                if (mPassportcopy.isChecked()){
                    passportCopy="PassportCopy";
                }
                if (mKraPin.isChecked()){
                    KRA="KRAPin";
                }if (mStaffid.isChecked()){
                    StaffId="StaffID";
                }if (midCopy.isChecked()){
                    IDCopy="IDcopy";
                }

                //ope

                if (TextUtils.isEmpty(othernames)){
                    Toast.makeText(Membeship.this, "Enter your other names", Toast.LENGTH_SHORT).show();

                }else if (TextUtils.isEmpty(idnumber)){
                    Toast.makeText(Membeship.this, "Enter your ID Number", Toast.LENGTH_SHORT).show();
                }else if (TextUtils.isEmpty(fname)){
                    Toast.makeText(Membeship.this, "Enter your First Name", Toast.LENGTH_SHORT).show();
                }else if (TextUtils.isEmpty(lname)){
                    Toast.makeText(Membeship.this, "Enter your Last Name", Toast.LENGTH_SHORT).show();
                }else if (TextUtils.isEmpty(email)){
                    Toast.makeText(Membeship.this, "Enter your email", Toast.LENGTH_SHORT).show();
                }else if (TextUtils.isEmpty(passportnumber)){
                    Toast.makeText(Membeship.this, "Enter your passport", Toast.LENGTH_SHORT).show();
                }else if (TextUtils.isEmpty(dateofbirth)){
                    Toast.makeText(Membeship.this, "Enter your date of birth", Toast.LENGTH_SHORT).show();
                }else if (TextUtils.isEmpty(telephone1)){
                    Toast.makeText(Membeship.this, "Enter your telephone number", Toast.LENGTH_SHORT).show();
                }else if (TextUtils.isEmpty(telephone2)){
                    mTelephone2.setError("Enter Second telephone number..");
                }else if (TextUtils.isEmpty(postaladdress)){
                    mPostalAddress.setError("Enter your postal Address..");
                }else if (TextUtils.isEmpty(residdentialaddress)){
                    mResiddentialAdress.setError("Enter residential address..");
                }else if (TextUtils.isEmpty(employer)){
                    mEmployer.setError("Enter your employer..");
                } else if (TextUtils.isEmpty(employercontact)) {
                    mEmployerContact.setError("Write Employer contacts..");
                }else if (TextUtils.isEmpty(employercontactperson)){
                    mEmployerContactsPerson.setError("Enter employer contact person...");
                }else if (TextUtils.isEmpty(locationofwork)){
                    mLocationofWork.setError("Enter location of work..");
                }else if (TextUtils.isEmpty(jobtitle)){
                    mJobTitle.setError("Enter your job Title..");
                }else if (TextUtils.isEmpty(dateofretirement)){
                    Toast.makeText(Membeship.this, "Enter date of retirement.", Toast.LENGTH_SHORT).show();
                }else {
                    if(mIagree.isChecked()){
                        progressDialog.setMessage("Submitting your registration details..");
                        progressDialog.show();
                        fireupload(null,othernames,passportnumber,dateofbirth,telephone1,telephone2,postaladdress,residdentialaddress,employer,employercontact,employercontactperson,locationofwork,
                                jobtitle,dateofretirement,sourcesofincome,method,gender,employmenttype,proposedcontribution,fname,lname,idnumber,email,passportCopy,KRA,IDCopy);
                    }else {
                        Toast.makeText(Membeship.this, "Agree to continue", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });

    }

    private void fireupload(@NonNull Task<UploadTask.TaskSnapshot> task ,String othernames, String passportnumber, String dateofbirth, String telephone1, String telephone2, String postaladdress,
                            String residdentialaddress, String employer, String employercontact, String employercontactperson,
                            String locationofwork, String jobtitle, String dateofretirement,
                            String gender,String employmenttype,String proposedcontribution,String sourcesofincome,String method,String FName,String LName,String Id,String Email,String PassPort,String KRA,String PassportSize) {

        Map<String, Object> stringMap = new HashMap<>();
        stringMap.put("Other_names", othernames);
        stringMap.put("Passport_number",passportnumber);
        stringMap.put("DoB",dateofbirth);
        stringMap.put("telephone1",telephone1);
        stringMap.put("telephone2",telephone2);
        stringMap.put("postal_address",postaladdress);
        stringMap.put("residential_address",residdentialaddress);
        stringMap.put("employer",employer);
        stringMap.put("employer_contact",employercontact);
        stringMap.put("contact_person",employercontactperson);
        stringMap.put("location_of_work",locationofwork);
        stringMap.put("job_title",jobtitle);
        stringMap.put("DoR",dateofretirement);
        stringMap.put("gender",gender);
        stringMap.put("employmenttype",employmenttype);
        stringMap.put("proposedcontribution",proposedcontribution);
        stringMap.put("sourcesofincome",sourcesofincome);
        stringMap.put("method",method);
        stringMap.put("fname",FName);
        stringMap.put("lname",LName);
        stringMap.put("email",Email);
        stringMap.put("id_number",Id);
        stringMap.put("passport",PassPort);
        stringMap.put("kra",KRA);
        stringMap.put("passportsize",PassportSize);
        stringMap.put("user_id",user_id);
        stringMap.put("timeStamp", FieldValue.serverTimestamp());


        firebaseFirestore.collection("Membership").document("Members").collection("Requests").document(user_id).set(stringMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()){
                    Toast.makeText(Membeship.this, "Details submitted successfully", Toast.LENGTH_SHORT).show();
                    String membershipNumber = RandomMembershipNumber.generateNumber();
                    String toSpeak = "Your membership number is: "+membershipNumber;
                    String newPassword = RandomMembershipNumber.generateNumber();
                    membership(membershipNumber,newPassword);
                    Toast.makeText(getApplicationContext(), toSpeak,Toast.LENGTH_SHORT).show();
                    t1.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
                    new android.support.v7.app.AlertDialog.Builder(Membeship.this)
                            .setTitle("Membership Number")
                            .setMessage("Your membership number is:\n"+membershipNumber+"\nPlease right it down for future use")
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setCancelable(false)
                            .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    onBackPressed();
                                }
                            }).show();
                }else {
                    String errorMessage = task.getException().getMessage();
                    Toast.makeText(getApplicationContext(),"Server error!..\n"+errorMessage,Toast.LENGTH_LONG);
                }
                progressDialog.dismiss();
            }
        });

    }

    public void onPause(){
        if(t1 !=null){
            t1.stop();
            t1.shutdown();
        }
        super.onPause();
    }



    private void membership(String memberNumber,String password){
        Map<String, String> stringStringMap  = new HashMap<>();
        stringStringMap.put("user_id",user_id);
        stringStringMap.put("number",memberNumber);
        stringStringMap.put("password",password);


        firebaseFirestore.collection("MembershipNumbers").document(user_id).set(stringStringMap);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode){
            case PICKFILE_RESULT_CODE:
                if (resultCode == RESULT_OK && requestCode==PICKFILE_RESULT_CODE && data!=null && data.getData() !=null){
                    uri = data.getData();
                    try {
                        path = FileUtils.getPath(this, uri);
                        mPassportcopy.setText(path);
                        mKraPin.setText(path);
                        midCopy.setText(path);
                        mStaffid.setText(path);

                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }

                }
                break;
        }

        super.onActivityResult(requestCode,resultCode,data);
    }


}
