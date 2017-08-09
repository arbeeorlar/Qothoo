package com.app.qootho;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.qootho.Adapters.HelpListAdapter;
import com.app.qootho.Model.HelpModel;
import com.app.qootho.Model.TokenObject;
import com.app.qootho.Model.UserAccount;
import com.app.qootho.Utilities.Connections;
import com.app.qootho.Utilities.Constants;
import com.app.qootho.Utilities.QoothoDB;
import com.app.qootho.Utilities.SessionManager;
import com.app.qootho.Utilities.Util;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;

public class HelpManagment extends AppCompatActivity {


    QoothoDB db = null;
    SessionManager session = null;
    SharedPreferences installPref;
    String username;
    UserAccount user;
    TokenObject token;

    TextView txtBack;
    TextView txtNext;
    TextView title;
    Toolbar toolbar;

    LinearLayout wait_icon;
    ListView listHelp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_managment);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        db = new QoothoDB(getApplicationContext());
        session = new SessionManager(getApplicationContext());
        installPref = getSharedPreferences(SessionManager.PREF_NAME, SessionManager.PRIVATE_MODE);
        username = installPref.getString(SessionManager.KEY_USERNAME, null);
        user = new UserAccount();
        token = new TokenObject();
        user = db.getUserProfile(username);
        token = db.getTokenByUsername(username);


        title = (TextView) toolbar.findViewById(R.id.txtTitle);
        title.setTextSize((float) 16.0);
        title.setText("Help");
        txtBack = (TextView) toolbar.findViewById(R.id.txtBack);
        txtNext = (TextView) toolbar.findViewById(R.id.txtNext);
        txtNext.setVisibility(View.INVISIBLE);
        listHelp = (ListView) findViewById(R.id.listHelp);
        wait_icon = (LinearLayout) findViewById(R.id.wait_icon);
        wait_icon.setVisibility(View.VISIBLE);

        txtBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        try {
            if (Util.myClickHandler(getApplicationContext())) {
                new GetAllHelp(token.getToken()).execute();
            } else {

                Toast.makeText(getApplicationContext(), "Internet connection is not available, try again later", Toast.LENGTH_LONG).show();

            }
        } catch (Exception ex) {

            ex.printStackTrace();
        }

    }

    public ArrayList<HelpModel> getTripHistroy(String response) {
        ArrayList<HelpModel> trip = new ArrayList<>();
        try {
            HelpModel[] user = null;
            if (response != null && response.contains("[{\"id\"")) {
                System.out.println("RESPONSE::: " + response);
                GsonBuilder builder = new GsonBuilder();
                Gson mGson = builder.create();
                user = mGson.fromJson(response, HelpModel[].class);
                for (HelpModel history : user) {
                    trip.add(history);
                }
            } else {
                trip = null;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            trip = null;
        }
        return trip;
    }

    public class GetAllHelp extends AsyncTask<Void, Void, String> {

        String token;


        public GetAllHelp(String token) {
            this.token = token;

            Log.i("Token:: ", token);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //  pDialog = ProgressDialog.show(getApplicationContext(), "", "Loading. Please wait...", true);
            wait_icon.setVisibility(View.VISIBLE);

        }

        @Override
        protected String doInBackground(Void... params) {
            String result = null;
            String resp = "";
            Log.i(Constants.LOG_TAG, "<<<<GET ALL TRIP>>>>");
            try {

                result = Connections.GETHELP(token);

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
            //pDialog.dismiss();
            wait_icon.setVisibility(View.GONE);
            System.out.println("response::: " + resp);
            try {
                if (resp.contains("[{\"id\"")) {
                    ArrayList<HelpModel> trip = getTripHistroy(resp);
                    if (trip != null) {

                        listHelp.setAdapter(new HelpListAdapter(HelpManagment.this, trip));
                    }
                }


            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        @Override
        protected void onCancelled() {

        }
    }

}
