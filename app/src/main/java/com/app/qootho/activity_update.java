package com.app.qootho;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.app.qootho.Model.TokenObject;
import com.app.qootho.Model.UserAccount;
import com.app.qootho.Utilities.Connections;
import com.app.qootho.Utilities.Constants;
import com.app.qootho.Utilities.QoothoDB;
import com.app.qootho.Utilities.SessionManager;
import com.app.qootho.Utilities.Util;

import org.json.JSONException;
import org.json.JSONObject;

public class activity_update extends AppCompatActivity {


    TextView txtBack;
    TextView txtNext;
    TextView title;

    EditText txtBox;
    Button  btnSend;

    String Category;

    QoothoDB db = null;
    SessionManager  session = null;
    SharedPreferences installPref;
    String username;
    UserAccount user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        db = new QoothoDB(getApplicationContext());
        session = new SessionManager(getApplicationContext());
        installPref = getSharedPreferences(SessionManager.PREF_NAME,SessionManager.PRIVATE_MODE);
        username = installPref.getString(SessionManager.KEY_USERNAME, null);
        user =  new UserAccount();
        user = db.getUserProfile(username);


        title = (TextView)toolbar.findViewById(R.id.txtTitle);
        title.setText("Update Account");
        txtBack = (TextView)toolbar.findViewById(R.id.txtBack);
        txtNext = (TextView) toolbar.findViewById(R.id.txtNext);
        txtNext.setVisibility(View.GONE);

        txtBox = (EditText) findViewById(R.id.txtMBileNo);
        btnSend = (Button) findViewById(R.id.btnSend);

        if("FNAME".equalsIgnoreCase(getStringData("Category"))){

            txtBox.setHint("First Name");
            btnSend.setText("Update");
        }else if("LNAME".equalsIgnoreCase(getStringData("Category"))){
            txtBox.setHint("Last Name");
            btnSend.setText("Update");

        }else if("EMAIL".equalsIgnoreCase(getStringData("Category"))){

            txtBox.setHint("Email");
            btnSend.setText("Update");
        }else if("MobileNo".equalsIgnoreCase(getStringData("Category"))){
            txtBox.setHint("Mobile Number");
            btnSend.setText("Update");

        }else if("PWORD".equalsIgnoreCase(getStringData("Category"))) {

            txtBox.setHint("Password");
            btnSend.setText("Update");

        }

        txtBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


    }

    private String getStringData(String name) {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String value = extras.getString(name);
            return value;
        }
        return null;
    }

    public  class LOGINUSER extends AsyncTask<Void, Void, String> {

        String  email;
        String   password;
        String countryCode;
        ProgressDialog pDialog;


        public LOGINUSER(String fName,String  surname,String countryCode){
            this. email = fName ;
            this.password = surname;
            this.countryCode = countryCode;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = ProgressDialog.show(activity_update.this, "", "Loading. Please wait...", true);
        }

        @Override
        protected String doInBackground(Void... params) {
            String result  =  null;
            String resp = "";
            Log.i(Constants.LOG_TAG, "<<<<STARTING LOGIN>>>>");
            try{
                email = "234" + email ;
                result = Connections.LOGIN(email,password,password);
                if( result.contains("{\"access_token\"") && result != ""){

                    JSONObject jsonObj = new JSONObject(result);



                }
            }catch(Exception exp){
                exp.printStackTrace();
                result =  Util.errorCode(Constants.ERROR_CODE, Constants.DOING_BACKGROUND_SERVICE_RESULT);
            }
            System.out.println("background " + result);
            return result;
        }
        @Override
        protected void onPostExecute(final String resp) {
            //{"error":"invalid_grant","error_description":"The user name or password is incorrect."}
            pDialog.dismiss();
            try {
                if( resp.contains("{\"access_token\"") && resp != ""){
                    TokenObject token  =  new TokenObject();

                    JSONObject result = new JSONObject(resp);
                    token.setToken(result.getString("access_token"));
                    token.setToken_type(result.getString("token_type"));
                    token.setExpiresIn(result.getString("expires_in"));
                    token.setIssued(result.getString(".issued"));
                    token.setExpire(result.getString(".expires"));
                    token.setUserName(result.getString("userName"));
                    System.out.println("result.getString(\".expires\") :: " +result.getString(".expires"));
                    System.out.println("result.getString(\".expires_in\") :: " +result.getString("expires_in"));


                    if(db.registerToken(getApplicationContext(),token)){
                        //   getVisibility(false,false,false,true);
                        startActivity(new Intent(activity_update.this,MainActivity.class));
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                    }else{

                        System.out.println("++++++++++++++++++++++++++");
                        Toast.makeText(getApplicationContext(),"Oops something went wrong",Toast.LENGTH_LONG).show();
                        //Dialogs.shoeMessage (getApplicationContext(),respond,"Qootho")

                    }
                }else if(resp.contains("{\"error\"") && resp != "") {


                    JSONObject result = new JSONObject(resp);
                    String description = result.getString("error_description");
                    Toast.makeText(getApplicationContext(),description,Toast.LENGTH_LONG).show();

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        @Override
        protected void onCancelled() {

        }
    }
}
