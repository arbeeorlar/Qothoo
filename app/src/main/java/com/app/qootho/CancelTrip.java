package com.app.qootho;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.app.qootho.Model.TokenObject;
import com.app.qootho.Model.TripDetail;
import com.app.qootho.Model.UserAccount;
import com.app.qootho.Utilities.Connections;
import com.app.qootho.Utilities.Constants;
import com.app.qootho.Utilities.QoothoDB;
import com.app.qootho.Utilities.SessionManager;
import com.app.qootho.Utilities.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CancelTrip extends AppCompatActivity {

    Spinner txtSpinnerReason;
    EditText txtNote;
    Button btnSend;


    QoothoDB db = null;
    SessionManager session = null;
    SharedPreferences installPref;
    String username;
    UserAccount user;
    TokenObject token;
    ArrayList<TripDetail> reasons;

    String reasonID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cancel_trip);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        db = new QoothoDB(getApplicationContext());
        session = new SessionManager(getApplicationContext());
        installPref = getSharedPreferences(SessionManager.PREF_NAME, SessionManager.PRIVATE_MODE);
        username = installPref.getString(SessionManager.KEY_USERNAME, null);
        user = new UserAccount();
        token = new TokenObject();
        user = db.getUserProfile(username);
        token = db.getTokenByUsername(username);
        reasons = new ArrayList<>();

        txtSpinnerReason = (Spinner) findViewById(R.id.txtSpinnerReason);
        txtNote = (EditText) findViewById(R.id.txtNote);
        btnSend = (Button) findViewById(R.id.btnSendCancel);

        txtNote.setVisibility(View.GONE);

        TextView title = (TextView) toolbar.findViewById(R.id.txtTitle);
        title.setTextSize((float) 16.0);
        title.setText("Edit Account");
        TextView txtBack = (TextView) toolbar.findViewById(R.id.txtBack);
        TextView txtNext = (TextView) toolbar.findViewById(R.id.txtNext);
        txtNext.setVisibility(View.INVISIBLE);
        txtBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        new GetCancelReason(token.getToken()).execute();

        txtSpinnerReason.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                reasonID = reasons.get(position).getPayerID() + "";

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tripID = "16";

                String lat = "6.54";
                String longit = "3.56";


                if (Util.myClickHandler(CancelTrip.this)) {
                    new CancelPassengerTrip(token.getToken(), tripID, longit, lat, reasonID).execute();
                } else {
                    Toast.makeText(CancelTrip.this, "No Internet connection,try again later.", Toast.LENGTH_LONG).show();
                }
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

    public void SetSpinner(Spinner spinner, ArrayList<String> reasons) {
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, reasons);
        // set the view for the Drop down list
        dataAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        // set the ArrayAdapter to the spinner
        spinner.setAdapter(dataAdapter);
    }

    public ArrayList<TripDetail> getCancelTrip(String response) {
        ArrayList<TripDetail> tripDetail = new ArrayList<>();

        try {

            if (response != null) {
                JSONArray jsonarray = new JSONArray(response);
                for (int i = 0; i < jsonarray.length(); i++) {
                    TripDetail detail = new TripDetail();
                    JSONObject jsonobject = jsonarray.getJSONObject(i);
                    detail.setPayerID(Integer.parseInt(jsonobject.getString("id")));
                    detail.setExpenseCode(jsonobject.getString("cancelReason"));
                    tripDetail.add(detail);
                }
            } else {
                tripDetail = null;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            tripDetail = null;
        }
        return tripDetail;
    }

    public class CancelPassengerTrip extends AsyncTask<Void, Void, String> {

        String token;
        String TripID;
        String cancelLongitude;
        String cancelLatitude;
        String cancelReasonId;
        ProgressDialog pDialog;


        public CancelPassengerTrip(String token, String TripID, String cancelLongitude, String cancelLatitude, String cancelReasonId) {
            this.TripID = TripID;
            this.token = token;
            this.cancelLatitude = cancelLatitude;
            this.cancelLongitude = cancelLongitude;
            this.cancelReasonId = cancelReasonId;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = ProgressDialog.show(CancelTrip.this, "", "Loading. Please wait...", true);
        }

        @Override
        protected String doInBackground(Void... params) {
            String result = null;
            String resp = "";
            Log.i(Constants.LOG_TAG, "<<<<STARTING LOGIN>>>>");
            try {

                result = Connections.CANCELTRIP(token, TripID, cancelReasonId, cancelLongitude, cancelLatitude);

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
                Toast.makeText(getApplicationContext(), resp, Toast.LENGTH_LONG).show();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void onCancelled() {

        }
    }

    public class GetCancelReason extends AsyncTask<Void, Void, String> {

        String token;
        ProgressDialog pDialog;
        ArrayList<String> string;


        public GetCancelReason(String token) {

            this.token = token;
            string = new ArrayList<>();

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = ProgressDialog.show(com.app.qootho.CancelTrip.this, "", "Loading. Please wait...", true);
        }

        @Override
        protected String doInBackground(Void... params) {
            String result = null;
            String resp = "";
            Log.i(Constants.LOG_TAG, "<<<<STARTING LOGIN>>>>");
            try {

                result = Connections.GETCANCELREASON(token);
            } catch (Exception exp) {
                exp.printStackTrace();
                result = Util.errorCode(Constants.ERROR_CODE, Constants.DOING_BACKGROUND_SERVICE_RESULT);
            }
            System.out.println("RESULT " + result);
            return result;
        }

        @Override
        protected void onPostExecute(final String resp) {
            //{"error":"invalid_grant","error_description":"The user name or password is incorrect."}
            pDialog.dismiss();
            System.out.println("Response::: " + resp);

            try {
                if (resp != null || resp.contains("{\"message\":")) {
                    reasons = getCancelTrip(resp);
                    for (int i = 0; i < reasons.size(); i++) {

                        string.add(reasons.get(i).getExpenseCode());
                    }

                    SetSpinner(txtSpinnerReason, string);
                } else {
                    Toast.makeText(CancelTrip.this, "Reasons Cannot be fetched at the moment, try again later", Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(CancelTrip.this, "Reasons Cannot be fetched at the moment, try again later", Toast.LENGTH_LONG).show();

            }
        }

        @Override
        protected void onCancelled() {

        }
    }


}
