package com.app.qootho;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.qootho.Model.PictureSelectionResult;
import com.app.qootho.Model.TokenObject;
import com.app.qootho.Model.UserAccount;
import com.app.qootho.Utilities.Connections;
import com.app.qootho.Utilities.Constants;
import com.app.qootho.Utilities.QoothoDB;
import com.app.qootho.Utilities.SessionManager;
import com.app.qootho.Utilities.Util;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

public class CustomerDetailActivity extends AppCompatActivity {

    private static final int SELECT_PICTURE = 1;
    TextView txtBack;
    TextView txtNext;
    TextView title;
    LinearLayout linearFName;
    LinearLayout linearLName;
    LinearLayout linearPWord;
    LinearLayout linearEMail;
    LinearLayout linearMobileNo;
    TextView txtFName, txtLName, txtEMail, txtPWord, txtMobileNumber;
    boolean pictureChanged;
    QoothoDB db = null;
    SessionManager session = null;
    SharedPreferences installPref;
    String username;
    UserAccount user;
    ImageView imgView;
    PictureSelectionResult pictureResult;
    TokenObject token;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_detail_update);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        getSupportActionBar().setDisplayShowCustomEnabled(true);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setDisplayShowTitleEnabled(false);
        token = new TokenObject();
        db = new QoothoDB(getApplicationContext());
        session = new SessionManager(getApplicationContext());
        installPref = getSharedPreferences(SessionManager.PREF_NAME, SessionManager.PRIVATE_MODE);
        username = installPref.getString(SessionManager.KEY_USERNAME, null);
        user = new UserAccount();
        user = db.getUserProfile(username);
        token = db.getTokenByUsername(username);


        title = (TextView) toolbar.findViewById(R.id.txtTitle);
        title.setTextSize((float) 16.0);
        title.setText("Edit Account");
        txtBack = (TextView) toolbar.findViewById(R.id.txtBack);
        txtNext = (TextView) toolbar.findViewById(R.id.txtNext);
        txtNext.setVisibility(View.INVISIBLE);

        imgView = (ImageView) findViewById(R.id.update_picture);

        linearFName = (LinearLayout) findViewById(R.id.linearLayoutFName);
        linearLName = (LinearLayout) findViewById(R.id.linearLayoutLastName);
        linearPWord = (LinearLayout) findViewById(R.id.linearLayoutPassword);
        linearEMail = (LinearLayout) findViewById(R.id.linearLayoutEmail);
        linearMobileNo = (LinearLayout) findViewById(R.id.linearLayoutMobileNo);

        txtFName = (TextView) findViewById(R.id.txtFName);
        txtLName = (TextView) findViewById(R.id.txtLastname);
        txtEMail = (TextView) findViewById(R.id.txtEmailAddress);
        txtPWord = (TextView) findViewById(R.id.txtPassWord);
        txtMobileNumber = (TextView) findViewById(R.id.txtMobileNo);

        txtFName.setText(user.getFirstName());
        txtLName.setText(user.getSurname());
        txtMobileNumber.setText(user.getPhoneNumber());
        txtEMail.setText(user.getEmail());
        txtPWord.setText("**********");

        imgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent pickIntent = new Intent();
                pickIntent.setType("image/*");
                pickIntent.setAction(Intent.ACTION_GET_CONTENT);
                Intent chooserIntent = Intent.createChooser(pickIntent, "Select a pix");

                startActivityForResult(chooserIntent, SELECT_PICTURE);

            }
        });
        linearFName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CustomerDetailActivity.this, activity_update.class);
                Bundle b = new Bundle();
                b.putString("Category", "FNAME");
                intent.putExtras(b);
                startActivity(intent);
            }
        });
        linearLName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CustomerDetailActivity.this, activity_update.class);
                Bundle b = new Bundle();
                b.putString("Category", "LNAME");
                intent.putExtras(b);
                startActivity(intent);
            }
        });
        linearEMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CustomerDetailActivity.this, activity_update.class);
                Bundle b = new Bundle();
                b.putString("Category", "EMAIL");
                intent.putExtras(b);
                startActivity(intent);
            }
        });
        linearPWord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CustomerDetailActivity.this, activity_update.class);
                Bundle b = new Bundle();
                b.putString("Category", "PWORD");
                intent.putExtras(b);
                startActivity(intent);
            }
        });
        linearMobileNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CustomerDetailActivity.this, activity_update.class);
                Bundle b = new Bundle();
                b.putString("Category", "MobileNo");
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


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            if (resultCode == Activity.RESULT_OK && requestCode == SELECT_PICTURE && data != null && data.getData() != null) {
                //release previous
                if (pictureResult != null) {
                    Util.releaseBitmap(pictureResult.bitmap);
                    pictureResult.bitmap = null;
                }

                Uri _uri = data.getData();

                pictureResult = Util.processPictureSelection(CustomerDetailActivity.this, _uri, Util.photoMaxLength, Util.photoMaxLength, true, true, Util.photoMaxLength);
                imgView.setImageBitmap(pictureResult.bitmap);
                pictureChanged = true;

                new PHOTOUPDATE("UpdatePhoto", Util.BitMapToString(pictureResult.bitmap), token.getToken()).execute();

            }

            super.onActivityResult(requestCode, resultCode, data);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private String getStringData(String name) {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String value = extras.getString(name);
            return value;
        }
        return null;
    }

    public void GETUSERDEATIAL(String token, String username) {
        QoothoDB db = new QoothoDB(getApplicationContext());
        UserAccount user = null;
        String respons = Connections.GETUSERDETAIL_GET(token);
        if (respons != null) {
            System.out.println("RESPONSE::: " + respons);

            GsonBuilder builder = new GsonBuilder();
            Gson mGson = builder.create();
            user = mGson.fromJson(respons, UserAccount.class);
            //System.out.println("" + user.getFirstName());
            db.saveUserProfile(getApplicationContext(), user, username);


        }

    }

    public class PHOTOUPDATE extends AsyncTask<Void, Void, String> {

        String Value;
        String Key;
        String token;
        ProgressDialog pDialog;


        public PHOTOUPDATE(String Value, String Key, String token) {
            this.Value = Value;
            this.Key = Key;
            this.token = token;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = ProgressDialog.show(CustomerDetailActivity.this, "", "Loading. Please wait...", true);
        }

        @Override
        protected String doInBackground(Void... params) {
            String result = null;
            String resp = "";
            Log.i(Constants.LOG_TAG, "<<<<STARTING LOGIN>>>>");
            try {
                if (Util.myClickHandler(CustomerDetailActivity.this)) {
                    result = Connections.UPDATEPHOTO(token, Value, Key);
//                if( result.contains("{\"access_token\"") && result != ""){
                    JSONObject jsonObj = new JSONObject(result);
                    String msg = jsonObj.getString("message");
                    if (msg.contains("Ok")) {
                        GETUSERDEATIAL(token, username);
                        System.out.println("GETUSERDEATIAL::: " + result);
                    } else {

                        System.out.println("GETUSERDEATIAL:::2 " + result);
                    }
                } else {


                    result = Util.errorCode(Constants.ERROR_CODE, Constants.INTERNET_CONNECTION_ERROR);
                }
            } catch (Exception exp) {
                exp.printStackTrace();
                result = Util.errorCode(Constants.ERROR_CODE, Constants.DOING_BACKGROUND_SERVICE_RESULT);
            }
            System.out.println("background " + result);
            return result;
        }

        @Override
        protected void onPostExecute(final String resp) {
            //{"error":"invalid_grant","error_description":"The user name or password is incorrect."}
            pDialog.dismiss();
            String response;

            try {
                JSONObject result = new JSONObject(resp);
                response = result.getString("message");
                if (response.contains("Ok")) {

                    // initCamera(this.latitude, this.longitude, Address.toString());
                    Toast.makeText(getApplicationContext(), resp, Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();

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
