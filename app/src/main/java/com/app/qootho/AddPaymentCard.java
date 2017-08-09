package com.app.qootho;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.qootho.Model.TokenObject;
import com.app.qootho.Model.UserAccount;
import com.app.qootho.Utilities.Connections;
import com.app.qootho.Utilities.Constants;
import com.app.qootho.Utilities.DatePickerFragment;
import com.app.qootho.Utilities.QoothoDB;
import com.app.qootho.Utilities.SessionManager;
import com.app.qootho.Utilities.Util;

import org.json.JSONObject;

import co.paystack.android.PaystackSdk;
import co.paystack.android.model.Card;
import co.paystack.android.model.Token;

public class AddPaymentCard extends AppCompatActivity {

    public static final long FiveYearsInMillis = 157766400000L;
    Token tokenNo;
    Card card;
    QoothoDB db = null;
    SessionManager session = null;
    SharedPreferences installPref;
    String username;
    UserAccount user;
    TokenObject token;
    TextView txtBack;
    TextView txtNext;
    TextView title;
    EditText txtCardPan;
    EditText txtCVV;
    //EditText txtPIN;
    EditText txtExpiryDate;
    Button btnSend;
    View waitIconLayout;
    View addCardView;
    ViewGroup form;
    View focusView = null;
    LinearLayout wait_icon;
    private DatePickerFragment expiryDateFragment;
    private int expiryMonth;
    private int expiryYear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_payment_card);
        PaystackSdk.initialize(getApplicationContext());
        PaystackSdk.setPublicKey(getResources().getString(R.string.paystack_public_key));

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        db = new QoothoDB(getApplicationContext());
        session = new SessionManager(getApplicationContext());
        installPref = getSharedPreferences(SessionManager.PREF_NAME, SessionManager.PRIVATE_MODE);
        username = installPref.getString(SessionManager.KEY_USERNAME, null);
        user = new UserAccount();
        token = new TokenObject();
        // paymnet = new PendingPayment();
        user = db.getUserProfile(username);
        token = db.getTokenByUsername(username);
        // accountList =  new ArrayList<>();

        title = (TextView) toolbar.findViewById(R.id.txtTitle);
        title.setTextSize((float) 16.0);
        title.setText("Add Payment Card");
        txtBack = (TextView) toolbar.findViewById(R.id.txtBack);
        txtNext = (TextView) toolbar.findViewById(R.id.txtNext);
        txtNext.setVisibility(View.GONE);
        txtBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        txtCardPan = (EditText) findViewById(R.id.txtCardPAN);
        txtCVV = (EditText) findViewById(R.id.txtCVV);
        // txtPIN = (EditText) findViewById(R.id.txtPIN);
        txtExpiryDate = (EditText) findViewById(R.id.txtExpDate);
        btnSend = (Button) findViewById(R.id.btnAddCard);

        wait_icon = (LinearLayout) findViewById(R.id.wait_icon);
        txtExpiryDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (expiryDateFragment == null) {
                    expiryDateFragment = DatePickerFragment.newInstance(new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            expiryMonth = monthOfYear + 1; //values start from 0, so add 1 to levelup
                            expiryYear = year;
                            txtExpiryDate.setText(getString(R.string._card_expiry_format, expiryMonth, expiryYear));
                        }
                    }, null, null, null);

                    expiryDateFragment.setCallback(new DatePickerFragment.DatePickerCallback() {
                        @Override
                        public void onCreateDialog(DatePickerDialog dialog) {
                            DatePicker picker = dialog.getDatePicker();
                            //minimum date should be today
                            picker.setMinDate(System.currentTimeMillis() - 10000); //subtract seconds to be safe
                            //max date shouldn't be more than 3-5 years from now. let's set five to be safe
                            picker.setMaxDate(System.currentTimeMillis() + FiveYearsInMillis);
                            dialog.setTitle(null);
                            expiryDateFragment.attemptDayVisibilityChangeInPicker(View.GONE);
                        }
                    });
                    expiryDateFragment.show(getSupportFragmentManager(), "expiryDatePicker");
                } else {
                    try {
                        DatePickerDialog dialog = expiryDateFragment.getDatePickerDialog();
                        dialog.updateDate(expiryYear, expiryMonth, 0);
                        //do this to clear the title
                        dialog.setTitle(null);
                    } catch (Exception e) {

                    }
                    expiryDateFragment.getDatePickerDialog().show();
                }
            }
        });

        txtExpiryDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (expiryDateFragment == null) {
                    expiryDateFragment = DatePickerFragment.newInstance(new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            expiryMonth = monthOfYear + 1; //values start from 0, so add 1 to levelup
                            expiryYear = year;
                            txtExpiryDate.setText(getString(R.string._card_expiry_format, expiryMonth, expiryYear));
                        }
                    }, null, null, null);

                    expiryDateFragment.setCallback(new DatePickerFragment.DatePickerCallback() {
                        @Override
                        public void onCreateDialog(DatePickerDialog dialog) {
                            DatePicker picker = dialog.getDatePicker();
                            //minimum date should be today
                            picker.setMinDate(System.currentTimeMillis() - 10000); //subtract seconds to be safe
                            //max date shouldn't be more than 3-5 years from now. let's set five to be safe
                            picker.setMaxDate(System.currentTimeMillis() + FiveYearsInMillis);
                            dialog.setTitle(null);
                            expiryDateFragment.attemptDayVisibilityChangeInPicker(View.GONE);
                        }
                    });
                    expiryDateFragment.show(getSupportFragmentManager(), "expiryDatePicker");
                } else {
                    try {
                        DatePickerDialog dialog = expiryDateFragment.getDatePickerDialog();
                        dialog.updateDate(expiryYear, expiryMonth, 0);
                        //do this to clear the title
                        dialog.setTitle(null);
                    } catch (Exception e) {

                    }
                    expiryDateFragment.getDatePickerDialog().show();
                }
            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validateSecondPage()) {
                    //Begin  process of charge card
//              StringTokenizer tokens = new StringTokenizer(txtExpiryDate.getText().toString(), "/");
//              String first = tokens.nextToken();// this will contain "Fruit"
//              expiryMonth = getMonthFromString(tokens.nextToken());
//              expiryYear = Integer.parseInt(tokens.nextToken());

                    Log.i("Date:: ", expiryMonth + "" + expiryYear);
                    card = new Card.Builder(txtCardPan.getText().toString(), expiryMonth, expiryYear, txtCVV.getText().toString()).build();
                    if (!card.validNumber()) {
                        //  Dialogs.showMessage2(context, "Card Number is invalid", Constants.TITLE);
                        txtCardPan.setError("Card Pan is invalid.");
                        focusView = txtCardPan;
                        return;
                    } else if (!card.validCVC()) {
                        txtCVV.setError("CVV is invalid.");
                        focusView = txtCVV;
                        return;
                    } else if (!card.validExpiryDate()) {
                        txtExpiryDate.setError("Expiry Date is invalid.");
                        focusView = txtExpiryDate;
                        return;
                    } else {
                        if (card.isValid()) {

                            new ADDCard(token.getToken(), txtCardPan.getText().toString(), txtCVV.getText().toString(), expiryMonth, expiryYear).execute();
                        }
                    }

                }
            }
        });


    }


//    private void createToken(Card card) {
//        //then create token using PaystackSdk class
//
//        PaystackSdk.setPublicKey("pk_test_e771cb7ce71746ea6a21034ed54cfa4cfc550c12");
//       // PaystackSdk.chargeCard(getApplicationContext(),);
////        PaystackSdk..createToken(card, new Paystack.TokenCallback() {
////            @Override
////            public void onCreate(Token token) {
////
////                tokenNo  =  token ;
////                System.out.println(tokenNo.last4 + "\n" + tokenNo.token);
////                // Dialogs.showMessage(getActivity(),tokenNo.last4 + "\n" + tokenNo.token,Constants.TITLE );
////                if(tokenNo.last4.length() == 4 && tokenNo.token.length() > 1){
////
////                    txtMaskedPan.setText("xxxx-xxxx-xxxx-" +tokenNo.last4 );
////                    //txtMaskedCardName.setText(txtCardName.getText().toString());
////                    txtMaskedExpiryDate.setText(txtExpiryDate.getText().toString());
////                    txtMaskedCVV.setText("xxx");
////                    pDialog.dismiss();
////                    setLayoutVisible(false, true,false);
////
////
////                }
////            }
////
////            @Override
////            public void onError(Exception error) {
////                pDialog.dismiss();
////                Dialogs.showMessage2(context,error.toString(),Constants.TITLE);
////            }
////        });
//    }


    public int getMonthFromString(String expMonth) {
        int response = 0;
        if ("Jan".equalsIgnoreCase(expMonth)) {
            response = 1;
        } else if ("Feb".equalsIgnoreCase(expMonth)) {
            response = 2;
        } else if ("Mar".equalsIgnoreCase(expMonth)) {
            response = 3;
        } else if ("Apr".equalsIgnoreCase(expMonth)) {
            response = 4;
        } else if ("May".equalsIgnoreCase(expMonth)) {
            response = 5;
        } else if ("Jun".equalsIgnoreCase(expMonth)) {
            response = 6;
        } else if ("Jul".equalsIgnoreCase(expMonth)) {
            response = 7;
        } else if ("Aug".equalsIgnoreCase(expMonth)) {
            response = 8;
        } else if ("Sep".equalsIgnoreCase(expMonth)) {
            response = 9;
        } else if ("Oct".equalsIgnoreCase(expMonth)) {
            response = 10;
        } else if ("Nov".equalsIgnoreCase(expMonth)) {
            response = 11;
        } else if ("Dec".equalsIgnoreCase(expMonth)) {
            response = 12;
        } else {
            response = 0;
        }
        return response;
    }

    private Boolean validateSecondPage() {
        View focusView = null;
        boolean cancel = false;
       /* if (TextUtils.isEmpty(txtCardName.getText().toString())) {
            txtCardName.setError("Required.");
            focusView = txtCardName;
            cancel = true;
        }else*/
        if (TextUtils.isEmpty(txtCardPan.getText().toString())) {
            txtCardPan.setError("Required.");
            focusView = txtCardPan;
            cancel = true;
        } else if (TextUtils.isEmpty(txtExpiryDate.getText().toString())) {
            txtExpiryDate.setError("Required.");
            focusView = txtExpiryDate;
            cancel = true;
        } else if (TextUtils.isEmpty(txtCVV.getText().toString())) {
            txtCVV.setError("Required.");
            focusView = txtCVV;
            cancel = true;
//        }else if(TextUtils.isEmpty(txtPIN.getText().toString())) {
//            txtPIN.setError("Required.");
//            focusView = txtPIN;
//            cancel = true;
        }
        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            //Crouton.makeText(this, getString(R.string.login_progress_signing_in),Style.INFO).show();
            //focusView.clearFocus();
            //focusView = null;
        }

        return cancel;
    }


    public class ADDCard extends AsyncTask<Void, Void, String> {

        String token;
        String cardPan;
        String cvv;
        int expMonth;
        int expYear;


        public ADDCard(String token, String cardpan, String cvv, int expryMonth, int expYear) {
            this.token = token;
            this.cardPan = cardpan;
            this.cvv = cvv;
            this.expMonth = expryMonth;
            this.expYear = expYear;


        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            wait_icon.setVisibility(View.VISIBLE);
            btnSend.setVisibility(View.GONE);
        }

        @Override
        protected String doInBackground(Void... params) {
            String result = null;
            String resp = "";
            Log.i(Constants.LOG_TAG, "<<<<ADDCard>>>>");
            try {

                if (Util.myClickHandler(getApplicationContext())) {
                    result = Connections.ADDPAYMENTCARD(token, cardPan, cvv, expMonth, expiryYear);
                } else {
                    result = Util.errorCode(Constants.ERROR_CODE, Constants.DOING_BACKGROUND_SERVICE_RESULT);

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
            wait_icon.setVisibility(View.GONE);
            btnSend.setVisibility(View.VISIBLE);
            String respond;
            try {
                JSONObject result = new JSONObject(resp);
                respond = result.getString("message");
                if (respond.contains("Ok")) {


//                startActivity(new Intent(RegistrationActivity.this,MainActivity.class));
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    finish();

                } else {
                    Toast.makeText(getApplicationContext(), respond, Toast.LENGTH_LONG).show();
                    //Dialogs.shoeMessage (getApplicationContext(),respond,"Qootho");

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
