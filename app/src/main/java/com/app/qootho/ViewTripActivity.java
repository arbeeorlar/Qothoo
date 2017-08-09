package com.app.qootho;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.qootho.Adapters.PaymentMethodAdapter;
import com.app.qootho.Adapters.UserAccountListViewCustomAdapter;
import com.app.qootho.Model.AvailableDriver;
import com.app.qootho.Model.DirectionModel;
import com.app.qootho.Model.PassengerAccount;
import com.app.qootho.Model.PaymentMethod;
import com.app.qootho.Model.TokenObject;
import com.app.qootho.Model.TripDetail;
import com.app.qootho.Model.UserAccount;
import com.app.qootho.Utilities.Connections;
import com.app.qootho.Utilities.Constants;
import com.app.qootho.Utilities.DirectionJSONParser;
import com.app.qootho.Utilities.QoothoDB;
import com.app.qootho.Utilities.SessionManager;
import com.app.qootho.Utilities.Util;
import com.app.qootho.iServices.iPaymentMethod;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class ViewTripActivity extends AppCompatActivity implements OnMapReadyCallback, OnFragmentInteractionListener, iPaymentMethod {

    private static final String TAG = ViewTripActivity.class.getSimpleName();
    private final int[] MAP_TYPES = {GoogleMap.MAP_TYPE_SATELLITE,
            GoogleMap.MAP_TYPE_NORMAL,
            GoogleMap.MAP_TYPE_HYBRID,
            GoogleMap.MAP_TYPE_TERRAIN,
            GoogleMap.MAP_TYPE_NONE};
    double startLatitude;
    double startLongitude;
    double endLatitude;
    double endLongititude;
    String startAddress;
    String endAddress;
    int rideType;
    int accountID;
    String accountName;
    String riderType, riderTypeName;
    //ArrayList<RiderAccount> accounts;
    TextView txtFairType, txtPaymentType, txtPickUp, txtDropOff, txtDistanceEstimate, txtTimeEstimate, txtFairEstimate;//txtRideType;
    String distance = "";
    String duration = "";
    Button btnSendRequest;
    ArrayList<AvailableDriver> Driver;
    ArrayList<PassengerAccount> _account;
    PassengerAccount account;
    QoothoDB db = null;
    SessionManager session = null;
    SharedPreferences installPref;
    String username;
    UserAccount user;
    TokenObject token;
    EditText txtEditText;
    TextView txtBack;
    TextView txtNext;
    TextView title;
    String regID = "";
    LinearLayout mLinearPassengerAccount;
    LinearLayout mLinearPaymentMethod;
    LinearLayout mLinearRequestTrip;
    ListView mListPassenger;
    ListView mListPaymnet;
    TextView txtStatus;
    String paymentType;
    ArrayList<PaymentMethod> payment;
    PaymentMethod _pay;
    double baseFare = 0.0;
    double farePerKm = 2.0;
    double farePerMinute = 3.0;
    double cancellationCharge = 4.0;
    double chargeGracePeriod = 5;
    double TotalEstimatedFare = 0.0;
    private GoogleMap mMap;
    private RecyclerView mapListView;
    private BroadcastReceiver mRegistrationBroadcastReceiver;

    public static boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return false;
        } catch (NullPointerException e) {
            return false;
        }
        // only got here if we didn't return false
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            Window w = getWindow(); // in Activity's onCreate() for instance
//            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
//            getWindow().getDecorView().setSystemUiVisibility(
//                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        }


        setContentView(R.layout.activity_view_trip);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(true);


        // Status bar :: Transparent
        Window window = this.getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }


        toolbar.setNavigationIcon(R.drawable.ic_action_keyboard_backspace);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        title = (TextView) toolbar.findViewById(R.id.txtTitle);
        title.setTextSize((float) 16.0);
        title.setText(getResources().getString(R.string.app_name));
        txtBack = (TextView) toolbar.findViewById(R.id.txtBack);
        txtNext = (TextView) toolbar.findViewById(R.id.txtNext);
        txtNext.setVisibility(View.GONE);
        txtBack.setVisibility(View.GONE);
        txtBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        account = new PassengerAccount();
        _account = new ArrayList<>();
        db = new QoothoDB(getApplicationContext());
        session = new SessionManager(getApplicationContext());
        installPref = getSharedPreferences(SessionManager.PREF_NAME, SessionManager.PRIVATE_MODE);
        username = installPref.getString(SessionManager.KEY_USERNAME, null);
        user = new UserAccount();
        token = new TokenObject();
        user = db.getUserProfile(username);
        token = db.getTokenByUsername(username);
        payment = new ArrayList<>();
        _pay = new PaymentMethod();


        _account = db.GetPassengerAccount(username);


        Driver = new ArrayList<>();
        startLatitude = getDoubleData("startLatitude");
        startLongitude = getDoubleData("startLongitude");
        endLatitude = getDoubleData("endLatitude");
        endLongititude = getDoubleData("endLongititude");
        startAddress = getStringData("startAddress");
        endAddress = getStringData("endAddress");
        rideType = Integer.parseInt(getStringData("rideTypeID"));
        accountID = Integer.parseInt(getStringData("accountID"));
        accountName = getStringData("accountName");
        riderTypeName = getStringData("rideTypeName");


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mLinearPassengerAccount = (LinearLayout) findViewById(R.id.ViewPassengerAccount);
        mLinearPaymentMethod = (LinearLayout) findViewById(R.id.ViewPaymentMethod);
        mLinearRequestTrip = (LinearLayout) findViewById(R.id.ViewCooperateSummary);

        mListPassenger = (ListView) findViewById(R.id.list_account);
        mListPaymnet = (ListView) findViewById(R.id.listOfPayment);
        txtStatus = (TextView) findViewById(R.id.txtStatus);

        // getVisibility(true,false,false);
        getVisibility(false, false, true);
//        mListPassenger.setAdapter(new UserAccountListViewCustomAdapter(ViewTripActivity.this, _account,mLinearPaymentMethod,mLinearPassengerAccount));
//
//        mListPaymnet.setAdapter(new UserAccountListViewCustomAdapter(ViewTripActivity.this, _account,mLinearRequestTrip,mLinearPaymentMethod));

        setPayMentMethod();
        mListPassenger.setAdapter(new UserAccountListViewCustomAdapter(ViewTripActivity.this, _account, this));

        txtStatus.setVisibility(View.GONE);
        mListPaymnet.setAdapter(new PaymentMethodAdapter(ViewTripActivity.this, payment, this));

        mListPassenger.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(ViewTripActivity.this, position, Toast.LENGTH_SHORT).show();

            }
        });

        mListPaymnet.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                getVisibility(true, false, false);

            }
        });


        txtFairType = (TextView) findViewById(R.id.txtRideType);
        txtPaymentType = (TextView) findViewById(R.id.txtPaymentType);
        txtPickUp = (TextView) findViewById(R.id.txtPickUp);
        txtDropOff = (TextView) findViewById(R.id.txtDropOff);
        txtDistanceEstimate = (TextView) findViewById(R.id.txtDistanceEstimate);
        txtTimeEstimate = (TextView) findViewById(R.id.txtTimeEstimate);
        txtFairEstimate = (TextView) findViewById(R.id.txtFareEstimate);
        // txtRideType = (TextView) findViewById(R.id.txtRideType);
        btnSendRequest = (Button) findViewById(R.id.btnRequestTrip);
        txtEditText = (EditText) findViewById(R.id.txtSearchText);

        txtEditText.setText(startAddress);

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // checking for type intent filter
                if (intent.getAction().equals(Constants.REGISTRATION_COMPLETE)) {
                    // gcm successfully registered
                    // now subscribe to `global` topic to receive app wide notifications
                    FirebaseMessaging.getInstance().subscribeToTopic(Constants.TOPIC_GLOBAL);
                    displayFirebaseRegId();
                }
//                } else if (intent.getAction().equals(Constants.PUSH_NOTIFICATION)) {
//                    // new push notification is received
//                    String message = intent.getStringExtra("message");
//                    Toast.makeText(getApplicationContext(), "Push notification: " + message, Toast.LENGTH_LONG).show();
//
//                }
            }
        };
        displayFirebaseRegId();


    }

    private void getVisibility(Boolean Request, Boolean payment, Boolean passenger) {
        if (Request) {
            mLinearRequestTrip.setVisibility(View.VISIBLE);
            mLinearPaymentMethod.setVisibility(View.GONE);
            mLinearPassengerAccount.setVisibility(View.GONE);

            //  Util.performAnimation(mLinearPhoneNumber);

        }
        if (payment) {
            mLinearRequestTrip.setVisibility(View.GONE);
            mLinearPaymentMethod.setVisibility(View.VISIBLE);
            mLinearPassengerAccount.setVisibility(View.GONE);
        }
        if (passenger) {
            mLinearRequestTrip.setVisibility(View.GONE);
            mLinearPaymentMethod.setVisibility(View.GONE);
            mLinearPassengerAccount.setVisibility(View.VISIBLE);
        }


    }

    private void displayFirebaseRegId() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Constants.SHARED_PREF, 0);
        String regId = pref.getString("regId", null);

        Log.e("Firebase", "Firebase reg id: " + regId);

        if (!TextUtils.isEmpty(regId))
            regID = regId;

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng startDirection = new LatLng(startLatitude, startLongitude);
        LatLng endDirection = new LatLng(endLatitude, endLongititude);
        mMap.addMarker(new MarkerOptions().position(startDirection).title(startAddress).icon(BitmapDescriptorFactory.fromResource(R.drawable.add_marker)));
        mMap.addMarker(new MarkerOptions().position(endDirection).title(endAddress).icon(BitmapDescriptorFactory.fromResource(R.drawable.add_marker)));

        mMap.setMapType(MAP_TYPES[1]);
        mMap.setTrafficEnabled(false);

        // Getting URL to the Google Directions API
        String url = getUrl(startDirection, endDirection);
        Log.d("onMapClick", url.toString());
        FetchUrl FetchUrl = new FetchUrl();
        FetchUrl.execute(url);
        // mMap.moveCamera(CameraUpdateFactory.newLatLng(startDirection));

        new GETAVAILABLEDRIVER(token.getToken(), startLatitude, startLongitude, rideType + "").execute();

        mMap.animateCamera(CameraUpdateFactory.zoomTo(17));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(startDirection, 17));
        CameraPosition position = CameraPosition.builder()
                .target(startDirection)
                .zoom(15f)
                .bearing(45)
                .tilt(0.0f)
                .build();

        mMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(position), null);


        btnSendRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TripDetail trip = new TripDetail();
                trip.setDropOffAddress(endAddress);
                trip.setPickUpAddress(startAddress);
                trip.setProposedPickUpLat(startLatitude);
                trip.setProposedPickUpLong(startLongitude);
                trip.setProposedDestinationLat(endLatitude);
                trip.setProposedDestinationLong(endLongititude);
                trip.setFareEstimateLB(2.0);
                trip.setRideTypeID(rideType);
                trip.setFareEstimateUB(1.0);
                trip.setPayerID(1);
                trip.setPaymentTypeID(accountID);

                new REQUEST_TRIP(token.getToken(), trip, regID).execute();

            }
        });

    }

    private void initCamera(Location location) {

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        CameraPosition position = CameraPosition.builder()
                .target(latLng)
                .zoom(16f)
                .bearing(0.0f)
                .tilt(0.0f)
                .build();
        mMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(position), null);
        // markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
//        mCurrLocationMarker = mGoogleMap.addMarker(markerOptions);
//        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));
//        mGoogleMap.setMapType( MAP_TYPES[1] );
//        // mGoogleMap.setTrafficEnabled( true );
//        // mGoogleMap.setMyLocationEnabled( true );
////        mGoogleMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
////            @Override
////            public void onMyLocationChange(Location location) {
////                CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 18);
////                mGoogleMap.animateCamera(cu);
////            }
////        });
        mMap.getUiSettings().setZoomControlsEnabled(true);
    }

    private String getUrl(LatLng origin, LatLng dest) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        // Sensor enabled
        String sensor = "sensor=false";
        String mode = "&mode=DRIVING";
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor + mode;
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;
        System.out.println("URL:: " + url);
        return url;
    }

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);
            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
            StringBuffer sb = new StringBuffer();
            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            data = sb.toString();
            Log.d("downloadUrl", data.toString());
            System.out.println("downloadUrl  ::: " + data.toString());
            br.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    private double getDoubleData(String name) {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            double value = extras.getDouble(name);
            return value;
        }
        return 0.0;
    }

//    @Override
//    public void onClick(PassengerAccount passengerAccount) {
//        getVisibility(false,true,false);
//        accountName = passengerAccount.getInstitutionName();
//        accountID = passengerAccount.getInstitutionID();
//        //Toast.makeText(ViewTripActivity.this,passengerAccount.getInstitutionName(),Toast.LENGTH_LONG).show();
//    }
//
//    @Override
//    public void onClick(PaymentMethod payment) {
//        getVisibility(true,false,false);
//        paymentType = payment.getPaymentName();
//
//    }

    private String getStringData(String name) {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String value = extras.getString(name);
            return value;
        }
        return null;
    }

    @Override
    public void onClick(PassengerAccount passengerAccount) {
        accountName = passengerAccount.getInstitutionName();
        accountID = passengerAccount.getInstitutionID();
        getVisibility(false, true, false);
        txtFairType.setText(accountName);

    }

    @Override
    public void onClick(PaymentMethod payment) {
        paymentType = payment.getPaymentName();
        getVisibility(true, false, false);
        txtPaymentType.setText(paymentType);

    }

    public DirectionModel convertToJSON(String respons) {
        DirectionModel direction = null;
        if (respons != null && respons.contains("\"geocoder_status\" : \"OK\"")) {
            System.out.println("RESPONSE::: " + respons);

            GsonBuilder builder = new GsonBuilder();
            Gson mGson = builder.create();
            direction = mGson.fromJson(respons, DirectionModel.class);

        }
        return direction;
    }

    protected Marker createMarker(double sourceLat, double sourceLong, double latitude, double longitude, String title, String snippet) {

        Location myTargetLocation = new Location("");
        Location sourceLocation = new Location("");
        myTargetLocation.setLatitude(latitude);
        myTargetLocation.setLongitude(longitude);
        sourceLocation.setLatitude(sourceLat);
        sourceLocation.setLongitude(sourceLong);
        float dist = sourceLocation.distanceTo(myTargetLocation);
        dist = dist / 1000;


        return mMap.addMarker(new MarkerOptions()
                .position(new LatLng(latitude, longitude))
                .anchor(0.5f, 0.5f)
                .title(dist + "to pick up point")
                .snippet(snippet)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.car_top_view_13)));
    }

    public ArrayList<AvailableDriver> getTripHistroy(String response) {
        ArrayList<AvailableDriver> trip = new ArrayList<>();
        try {
            AvailableDriver[] driver = null;
            if (response != null && response.contains("[{\"id\"")) {
                System.out.println("RESPONSE::: " + response);
                GsonBuilder builder = new GsonBuilder();
                Gson mGson = builder.create();
                driver = mGson.fromJson(response, AvailableDriver[].class);
                for (AvailableDriver _driver : driver) {
                    trip.add(_driver);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.landing_page, menu);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.landing_page, menu);

        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.search_badge:

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onResume() {
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        super.onResume();
    }

    @Override
    public void onStart() {
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        super.onStart();
    }

    public void setPayMentMethod() {
        PaymentMethod pay = new PaymentMethod();
        pay.setId(1);
        pay.setPaymentName("Cash");
        pay.setPaymentType("Cash");
        payment.add(pay);

        PaymentMethod pay2 = new PaymentMethod();
        pay2.setId(2);
        pay2.setPaymentName("My MasterCard");
        pay2.setPaymentType("Card");
        payment.add(pay2);
    }

    // Fetches data from url passed
    private class FetchUrl extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
                Log.d("Background Task data", data.toString());
                System.out.println("data " + data.toString());
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            ParserTask parserTask = new ParserTask();
            //Invokes the thread for parsing the JSON data
            parserTask.execute(result);

        }
    }

    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {
        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {
            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            System.out.println("jsonData  :::: " + jsonData);
            try {
                jObject = new JSONObject(jsonData[0]);
                Log.d("ParserTask", jsonData[0].toString());
                DirectionJSONParser parser = new DirectionJSONParser();
                Log.d("ParserTask", parser.toString());

                // Starts parsing data
                routes = parser.parse(jObject);
                Log.d("ParserTask", "Executing routes");
                Log.d("ParserTask", routes.toString());

            } catch (Exception e) {
                Log.d("ParserTask", e.toString());
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();
            if (result.size() < 1) {
                //Toast.makeText(getBaseContext(), "No Points", Toast.LENGTH_SHORT).show();
                System.out.println("No Point");
                return;
            }

            // Traversing through all the routes
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);
                    // System.out.println("Distance:" + j + ", Duration:" + j);

                    if (j == 0) {    // Get distance from the list
                        distance = point.get("distance");
                        continue;
                    } else if (j == 1) { // Get duration from the list
                        duration = point.get("duration");
                        continue;
                    }

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(12);
                lineOptions.color(Color.BLACK);
            }


            //String[] splited = str.split("\\s+");
            new GetFareEstimate(token.getToken(), rideType, Double.parseDouble(distance.split("\\s+")[0]), Double.parseDouble(duration.split("\\s+")[0])).execute();

            System.out.println("Distance:" + distance + ", Duration:" + duration);
            txtDistanceEstimate.setText(distance);
            txtTimeEstimate.setText(duration);
            txtFairType.setText(accountName);  //rideType==1?"Classic Ride":"Elegant Ride");
            txtPaymentType.setText(paymentType);
            txtPickUp.setText(startAddress);
            txtDropOff.setText(endAddress);
            //txtFairEstimate.setText("N" +TotalEstimatedFare);


            if (lineOptions != null) {
                mMap.addPolyline(lineOptions);
            } else {
                Log.d("onPostExecute", "without Polylines drawn");
            }
        }
    }

    public class REQUEST_TRIP extends AsyncTask<Void, Void, String> {

        String token;
        TripDetail trip;
        ProgressDialog pDialog;
        String regId;

        Date d = new Date();

        public REQUEST_TRIP(String token, TripDetail trip, String regId) {
            this.trip = trip;
            this.token = token;
            this.regId = regId;


        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = ProgressDialog.show(ViewTripActivity.this, "", "Loading. Please wait...", true);
        }

        @Override
        protected String doInBackground(Void... params) {
            String result = null;
            String resp = "";
            Log.i(Constants.LOG_TAG, "<<<<STARTING LOGIN>>>>");
            try {
                trip.setScheduledPickUpTime(Util.CheckDateTime(d));
                result = Connections.REQUESTTRIP(token, trip, regId);
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
                if (isInteger(response)) {

                    Intent intent = new Intent(ViewTripActivity.this, TripDriver.class);
                    Bundle b = new Bundle();
                    b.putDouble("startLatitude", startLatitude);
                    b.putDouble("startLongitude", startLongitude);
                    b.putDouble("endLatitude", endLatitude);
                    b.putDouble("endLongititude", endLongititude);
                    b.putString("endAddress", endAddress);
                    b.putString("startAddress", startAddress);
                    b.putString("tripId", response);
                    intent.putExtras(b);
                    startActivity(intent);


                    Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();


                } else {
                    Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();

//                    Intent intent = new Intent(ViewTripActivity.this, TripDriver.class);
//                    Bundle b =  new Bundle();
//                    b.putDouble("startLatitude", startLatitude);
//                    b.putDouble("startLongitude", startLongitude);
//                    b.putDouble("endLatitude",endLatitude);
//                    b.putDouble("endLongititude", endLongititude);
//                    b.putString("endAddress", endAddress);
//                    b.putString("startAddress", startAddress);
//                    intent.putExtras(b);
//                    startActivity(intent);

                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Oops, something  went wrong", Toast.LENGTH_LONG).show();
//
//                Intent intent = new Intent(ViewTripActivity.this, TripDriver.class);
//                Bundle b =  new Bundle();
//                b.putString("tripId","89");
//                b.putDouble("startLatitude", startLatitude);
//                b.putDouble("startLongitude", startLongitude);
//                b.putDouble("endLatitude",endLatitude);
//                b.putDouble("endLongititude", endLongititude);
//                b.putString("endAddress", endAddress);
//                b.putString("startAddress", startAddress);
//                intent.putExtras(b);
//                startActivity(intent);


            }

        }

        @Override
        protected void onCancelled() {

        }
    }

    public class GETAVAILABLEDRIVER extends AsyncTask<Void, Void, String> {

        String Token;
        double latPoint;
        double longPoint;
        String rideType;

        public GETAVAILABLEDRIVER(String token, double longPoint, double latPoint, String rideType) {
            this.Token = token;
            this.latPoint = latPoint;
            this.longPoint = longPoint;
            this.rideType = rideType;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            //  pDialog = ProgressDialog.show(getApplicationContext(), "", "Loading. Please wait...", true);
//            wait_icon.setVisibility(View.VISIBLE);

        }

        @Override
        protected String doInBackground(Void... params) {
            String result = null;
            String resp = "";
            Log.i(Constants.LOG_TAG, "<<<<GET ALL DRIVER>>>>");
            try {
                if (Util.myClickHandler(getApplicationContext())) {
                    result = Connections.GETAVAILABLECAR(longPoint, latPoint, Token, rideType);
                } else {

                    result = Util.errorCode(Constants.INTERNET_CONNECTION_ERROR, "Internet coonection is not available, check your connection");
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
            System.out.println("response::: " + resp);
            try {

                if (resp.contains("{\"id\"")) {
                    Driver = getTripHistroy(resp);
                    for (AvailableDriver _driver : Driver) {

                        createMarker(startLatitude, startLongitude, _driver.getPointLat(), _driver.getPointLong(), _driver.getLogTime() + "", "Qothoo");
                    }


                } else {


                }


            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        @Override
        protected void onCancelled() {

        }
    }

    public class GetFareEstimate extends AsyncTask<Void, Void, String> {

        String Token;
        int rideType;
        double estimatedDistance;
        double estimatedTime;

        public GetFareEstimate(String token, int rideType, double estimatedDistance, double estimatedTime) {
            this.Token = token;
            this.rideType = rideType;
            this.estimatedTime = estimatedTime;
            this.estimatedDistance = estimatedDistance;

        }

//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
////            //  pDialog = ProgressDialog.show(getApplicationContext(), "", "Loading. Please wait...", true);
////            wait_icon.setVisibility(View.VISIBLE);
//
//        }

        @Override
        protected String doInBackground(Void... params) {
            String result = null;
            String resp = "";
            Log.i(Constants.LOG_TAG, "<<<<GET ALL DRIVER>>>>");
            try {
                if (Util.myClickHandler(getApplicationContext())) {
                    result = Connections.GETFARETYPE(rideType, Token);

                } else {

                    result = Util.errorCode(Constants.INTERNET_CONNECTION_ERROR, "Internet coonection is not available, check your connection");
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
            System.out.println("response::: " + resp);
            try {

                if (resp != null && resp.length() > 15) {

                    JSONObject result = new JSONObject(resp);
                    baseFare = result.getDouble("baseFare");
                    double farePerKm = result.getDouble("farePerKm");
                    farePerMinute = result.getDouble("farePerMinute");
                    cancellationCharge = result.getDouble("cancellationCharge");
                    chargeGracePeriod = result.getDouble("chargeGracePeriod");
                    TotalEstimatedFare = baseFare + (estimatedDistance * farePerKm) + (estimatedTime * farePerKm);

                    txtFairEstimate.setText("N" + TotalEstimatedFare);


                } else {


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
