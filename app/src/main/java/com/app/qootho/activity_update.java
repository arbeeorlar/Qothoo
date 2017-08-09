package com.app.qootho;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

public class activity_update extends AppCompatActivity {


    private static final int SELECT_PICTURE = 1;
    TextView txtBack;
    TextView txtNext;
    TextView title;
    EditText txtBox;
    Button btnSend;
    String Category;
    QoothoDB db = null;
    SessionManager session = null;
    SharedPreferences installPref;
    String username;
    UserAccount user;
    String Key;
    TokenObject token;
    ImageView imgView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
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
        title.setText("Update Account");
        txtBack = (TextView) toolbar.findViewById(R.id.txtBack);
        txtNext = (TextView) toolbar.findViewById(R.id.txtNext);
        txtNext.setVisibility(View.INVISIBLE);

        //imgView = (ImageView) findViewById(R.id.)


        txtBox = (EditText) findViewById(R.id.txtMBileNo);
        btnSend = (Button) findViewById(R.id.btnSend);
        txtBox.setSelection(txtBox.getText().length());


        if ("FNAME".equalsIgnoreCase(getStringData("Category"))) {

            txtBox.setInputType(InputType.TYPE_CLASS_TEXT |
                    InputType.TYPE_CLASS_TEXT);
            txtBox.setSelection(txtBox.getText().length());

            txtBox.setHint("First Name");
            btnSend.setText("Update Firstname");
            Key = "FirstName";
        } else if ("LNAME".equalsIgnoreCase(getStringData("Category"))) {

            txtBox.setInputType(InputType.TYPE_CLASS_TEXT |
                    InputType.TYPE_CLASS_TEXT);
            txtBox.setSelection(txtBox.getText().length());

            btnSend.setText("Update Password");
            txtBox.setHint("Surname");
            btnSend.setText("Update Surname");
            Key = "Surname";

        } else if ("EMAIL".equalsIgnoreCase(getStringData("Category"))) {
            txtBox.setInputType(InputType.TYPE_CLASS_TEXT |
                    InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
            txtBox.setSelection(txtBox.getText().length());
            btnSend.setText("Update Password");
            txtBox.setHint("Email");
            btnSend.setText("Update Email");
            Key = "Email";
        } else if ("MobileNo".equalsIgnoreCase(getStringData("Category"))) {
            txtBox.setHint("Mobile Number");
            btnSend.setText("Update PhoneNumber");
            txtBox.setInputType(InputType.TYPE_CLASS_TEXT |
                    InputType.TYPE_CLASS_PHONE);
            Key = "PhoneNumber";

        } else if ("PWORD".equalsIgnoreCase(getStringData("Category"))) {

            txtBox.setHint("Password");
            txtBox.setInputType(InputType.TYPE_CLASS_TEXT |
                    InputType.TYPE_TEXT_VARIATION_PASSWORD);

            btnSend.setText("Update Password");
            Key = "Password";




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

                new UPDATE(txtBox.getText().toString(), Key, token.getToken()).execute();


            }
        });


    }


//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        try {
//            if (resultCode == Activity.RESULT_OK && requestCode == SELECT_PICTURE && data != null && data.getData() != null) {
//                //release previous
//                if (pictureResult != null) {
//                    Utilities.releaseBitmap(pictureResult.bitmap);
//                    pictureResult.bitmap = null;
//                }
//
//                Uri _uri = data.getData();
//
//                pictureResult = Utilities.processPictureSelection(getActivity(), _uri, Defaults.photoMaxLength, Defaults.photoMaxLength, true, true, Defaults.photoMaxLength);
//                // pictureView.setImageBitmap(pictureResult.bitmap);
//                // pictureChanged = true;
//
//                new BuxMeUploadImageTask(Config.BitMapToString(pictureResult.bitmap), ProfileId, Email).execute();
//
//            }
//
//            super.onActivityResult(requestCode, resultCode, data);
//        }catch (Exception ex){
//            ex.printStackTrace();
//        }
//    }

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

    public class UPDATE extends AsyncTask<Void, Void, String> {

        String Value;
        String Key;
        String token;
        ProgressDialog pDialog;


        public UPDATE(String Value, String Key, String token) {
            this.Value = Value;
            this.Key = Key;
            this.token = token;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = ProgressDialog.show(activity_update.this, "", "Loading. Please wait...", true);
        }

        @Override
        protected String doInBackground(Void... params) {
            String result = null;
            String resp = "";
            Log.i(Constants.LOG_TAG, "<<<<STARTING LOGIN>>>>");
            try {
                if (Util.myClickHandler(activity_update.this)) {
                    result = Connections.UPDATEEMAIL(token, Value, Key);
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
