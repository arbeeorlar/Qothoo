package com.app.qootho;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.qootho.Model.UserAccount;
import com.app.qootho.Utilities.QoothoDB;
import com.app.qootho.Utilities.SessionManager;

public class CustomerDetailActivity extends AppCompatActivity {

    TextView txtBack;
    TextView txtNext;
    TextView title;

    LinearLayout linearFName;
    LinearLayout linearLName;
    LinearLayout  linearPWord;
    LinearLayout  linearEMail;
    LinearLayout  linearMobileNo;

    TextView txtFName,txtLName,txtEMail,txtPWord,txtMobileNumber;

    QoothoDB db = null;
    SessionManager  session = null;
    SharedPreferences installPref;
    String username;
    UserAccount user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_detail_update);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        getSupportActionBar().setDisplayShowCustomEnabled(true);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setDisplayShowTitleEnabled(false);

        db = new QoothoDB(getApplicationContext());
        session = new SessionManager(getApplicationContext());
        installPref = getSharedPreferences(SessionManager.PREF_NAME,SessionManager.PRIVATE_MODE);
        username = installPref.getString(SessionManager.KEY_USERNAME, null);
        user =  new UserAccount();
        user = db.getUserProfile(username);


        title = (TextView)toolbar.findViewById(R.id.txtTitle);
        title.setTextSize((float) 18.0);
        title.setText("Edit Account");
        txtBack = (TextView)toolbar.findViewById(R.id.txtBack);
        txtNext = (TextView) toolbar.findViewById(R.id.txtNext);
        txtNext.setVisibility(View.GONE);


        linearFName = (LinearLayout) findViewById(R.id.linearLayoutFName);
        linearLName = (LinearLayout) findViewById(R.id.linearLayoutLastName);
        linearPWord = (LinearLayout)  findViewById(R.id.linearLayoutPassword);
        linearEMail = (LinearLayout) findViewById(R.id.linearLayoutEmail);
        linearMobileNo = (LinearLayout)  findViewById(R.id.linearLayoutMobileNo);

        txtFName = (TextView) findViewById(R.id.txtFName);
        txtLName = (TextView) findViewById(R.id.txtLastname);
        txtEMail = (TextView) findViewById(R.id.txtEmailAddress);
        txtPWord = (TextView) findViewById(R.id.txtPassWord);
        txtMobileNumber = (TextView) findViewById(R.id.txtMobileNo);

        txtFName.setText(user.getFirstName());
        txtLName.setText(user.getSurname());
        txtMobileNumber.setText(username.substring(3));
        txtEMail.setText(user.getEmail()!=null?user.getEmail():"---------");


        linearFName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 Intent intent  =  new Intent(CustomerDetailActivity.this,activity_update.class);
                 Bundle b = new Bundle();
                 b.putString("Category","FNAME");
                 intent.putExtras(b);
                 startActivity(intent);
            }
        });
        linearLName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent  =  new Intent(CustomerDetailActivity.this,activity_update.class);
                Bundle b = new Bundle();
                b.putString("Category","LNAME");
                intent.putExtras(b);
                startActivity(intent);
            }
        });
        linearEMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent  =  new Intent(CustomerDetailActivity.this,activity_update.class);
                Bundle b = new Bundle();
                b.putString("Category","EMAIL");
                intent.putExtras(b);
                startActivity(intent);
            }
        });
        linearPWord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent  =  new Intent(CustomerDetailActivity.this,activity_update.class);
                Bundle b = new Bundle();
                b.putString("Category","PWORD");
                intent.putExtras(b);
                startActivity(intent);
            }
        });
        linearMobileNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent  =  new Intent(CustomerDetailActivity.this,activity_update.class);
                Bundle b = new Bundle();
                b.putString("Category","MobileNo");
                intent.putExtras(b);
                startActivity(intent);
            }
        });

        txtBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }
}
