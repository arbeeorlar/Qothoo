package com.app.qootho;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
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
import android.widget.ImageView;
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
import com.app.qootho.Model.UserAccount;
import com.app.qootho.Utilities.Connections;
import com.app.qootho.Utilities.Constants;
import com.app.qootho.Utilities.DirectionJSONParser;
import com.app.qootho.Utilities.QoothoDB;
import com.app.qootho.Utilities.SessionManager;
import com.app.qootho.Utilities.Util;
import com.app.qootho.iServices.iPaymentMethod;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
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

import org.json.JSONArray;
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

import javax.net.ssl.HttpsURLConnection;

public class InterStateTripActivity extends AppCompatActivity implements GoogleMap.OnCameraMoveStartedListener,
        GoogleMap.OnCameraMoveListener,
        GoogleMap.OnCameraMoveCanceledListener,
        GoogleMap.OnCameraIdleListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener, OnMapReadyCallback, OnFragmentInteractionListener, iPaymentMethod {

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String TAG = MainActivity.class.getSimpleName();
    private final int[] MAP_TYPES = {GoogleMap.MAP_TYPE_SATELLITE,
            GoogleMap.MAP_TYPE_NORMAL,
            GoogleMap.MAP_TYPE_HYBRID,
            GoogleMap.MAP_TYPE_TERRAIN,
            GoogleMap.MAP_TYPE_NONE};
    protected LatLng mCenterLatLong;
    protected GoogleApiClient mGoogleApiClient;
    String[] permissionsRequired = new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION};
    GoogleMap mGoogleMap;
    Location mLastLocation;
    String pickUpId;
    String dropOffId;
    String TotalFare;
    String DropOffState;
    String PickUpState;

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
    TextView txtFairType, txtPaymentType, txtPickUp, txtDropOff, txtDistanceEstimate, txtTimeEstimate, txtFairEstimate, txtRideType;
    String distance = "";
    String duration = "";
    Button btnSendRequest;
    ArrayList<AvailableDriver> Driver;
    QoothoDB db = null;
    SessionManager session = null;
    SharedPreferences installPref;
    String username;
    UserAccount user;
    TokenObject token;
    EditText txtEditText;
    PassengerAccount account;
    ArrayList<PassengerAccount> _account;
    ImageView txtBack;
    TextView txtNext;
    TextView title;
    LinearLayout mLinearPassengerAccount;
    LinearLayout mLinearPaymentMethod;
    LinearLayout mLinearRequestTrip;
    ListView mListPassenger;
    ListView mListPaymnet;
    TextView txtStatus;
    String regID = "";
    ArrayList<PaymentMethod> payment;
    PaymentMethod _pay;
    String paymentType;
    LatLng startDirection = null;
    LatLng endDirection = null;
    private LocationRequest mLocationRequest;
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
        // getSupportActionBar().setDisplayShowCustomEnabled(true);
        // getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        //getSupportActionBar().setHomeButtonEnabled(true);

        // Status bar :: Transparent
        Window window = this.getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }

        account = new PassengerAccount();
        _account = new ArrayList<>();
        payment = new ArrayList<>();
        _pay = new PaymentMethod();


//        toolbar.setNavigationIcon(R.drawable.ic_action_keyboard_backspace);
//       // toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });
        title = (TextView) toolbar.findViewById(R.id.txtTitle);
        title.setTextSize((float) 16.0);
        title.setText("Inter State");
        txtBack = (ImageView) toolbar.findViewById(R.id.txtBack);
        txtNext = (TextView) toolbar.findViewById(R.id.txtNext);
        txtNext.setVisibility(View.INVISIBLE);
        txtBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mLinearPassengerAccount = (LinearLayout) findViewById(R.id.ViewPassengerAccount);
        mLinearPaymentMethod = (LinearLayout) findViewById(R.id.ViewPaymentMethod);
        mLinearRequestTrip = (LinearLayout) findViewById(R.id.ViewCooperateSummary);
        txtStatus = (TextView) findViewById(R.id.txtStatus);

        getVisibility(false, false, true);

        txtFairType = (TextView) findViewById(R.id.txtRideType);
        txtPaymentType = (TextView) findViewById(R.id.txtPaymentType);
        txtPickUp = (TextView) findViewById(R.id.txtPickUp);
        txtDropOff = (TextView) findViewById(R.id.txtDropOff);
        txtDistanceEstimate = (TextView) findViewById(R.id.txtDistanceEstimate);
        txtTimeEstimate = (TextView) findViewById(R.id.txtTimeEstimate);
        txtFairEstimate = (TextView) findViewById(R.id.txtFareEstimate);
        txtRideType = (TextView) findViewById(R.id.txtRideType);
        btnSendRequest = (Button) findViewById(R.id.btnRequestTrip);
        txtEditText = (EditText) findViewById(R.id.txtSearchText);

        mListPassenger = (ListView) findViewById(R.id.list_account);
        mListPaymnet = (ListView) findViewById(R.id.listOfPayment);
        txtStatus = (TextView) findViewById(R.id.txtStatus);

        db = new QoothoDB(getApplicationContext());
        session = new SessionManager(getApplicationContext());
        installPref = getSharedPreferences(SessionManager.PREF_NAME, SessionManager.PRIVATE_MODE);
        username = installPref.getString(SessionManager.KEY_USERNAME, null);
        user = new UserAccount();
        _account = db.GetPassengerAccount(username);
        token = new TokenObject();
        user = db.getUserProfile(username);
        token = db.getTokenByUsername(username);

        Driver = new ArrayList<>();

        txtStatus.setVisibility(View.GONE);

        PickUpState = getStringData("PickUpState");
        DropOffState = getStringData("DropOffState");
        pickUpId = getStringData("pickUpId");
        dropOffId = getStringData("dropOffId");
        TotalFare = getStringData("TotalFare");

        distance = getStringData("distance");
        duration = getStringData("duration");

        //getVisibility(false,false,true);

        // getVisibility(true,false,false);

        new startLatLongFinder(PickUpState).execute();
        new endLatLongFinder(DropOffState).execute();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        setPayMentMethod();

        mListPassenger.setAdapter(new UserAccountListViewCustomAdapter(this, _account, this));

        mListPaymnet.setAdapter(new PaymentMethodAdapter(this, payment, this));


        mListPassenger.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(InterStateTripActivity.this, position, Toast.LENGTH_SHORT).show();
                getVisibility(false, true, false);
            }
        });

        mListPaymnet.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                getVisibility(true, false, false);
            }
        });

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

    private void displayFirebaseRegId() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Constants.SHARED_PREF, 0);
        String regId = pref.getString("regId", null);
        Log.e("Firebase", "Firebase reg id: " + regId);
        if (!TextUtils.isEmpty(regId))
            regID = regId;

        // Toast.makeText(this, regID, Toast.LENGTH_LONG).show();

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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Add a marker in Sydney and move the camera

        final Date d = new Date();

        // Getting URL to the Google Directions API
        String url = getUrl(PickUpState, DropOffState);
        //String url = getUrl(new LatLng(user.getHomeLat(),user.getHomeLong()), new LatLng(user.getWorkLat(),user.getWorkLong()));
        Log.d("onMapClick", url.toString());
        FetchUrl FetchUrl = new FetchUrl();
        FetchUrl.execute(url);


        //mMap.addMarker(new MarkerOptions().position(new LatLng(user.getHomeLat(),user.getHomeLong())).title(PickUpState));
        //mMap.addMarker(new MarkerOptions().position(new LatLng(user.getWorkLat(),user.getWorkLat())).title(DropOffState));
        mMap.setMapType(MAP_TYPES[1]);
        mMap.setTrafficEnabled(false);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(user.getHomeLat(), user.getHomeLong())));

        // new GETAVAILABLEDRIVER(token.getToken(), startLatitude, startLongitude,rideType + "").execute();

        mMap.animateCamera(CameraUpdateFactory.zoomTo(9));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(user.getWorkLat(), user.getWorkLat()), 9));
        CameraPosition position = CameraPosition.builder()
                .target(new LatLng(user.getWorkLat(), user.getWorkLat()))
                .zoom(15f)
                .bearing(45)
                .tilt(0.0f)
                .build();

        mMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(position), null);
        mMap.getUiSettings().setZoomControlsEnabled(true);

        btnSendRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Util.myClickHandler(getApplicationContext())) {

                    System.out.println("pickUpId:: " + pickUpId);
                    System.out.println("dropOffId::: " + dropOffId);
                    new REQUEST_TRIP(token.getToken(), Util.CheckDateTime(d), pickUpId, dropOffId, TotalFare, regID).execute();

                } else {
                    Toast.makeText(getApplicationContext(), "Internet  connection is not available,try again later", Toast.LENGTH_LONG).show();
                }
            }
        });

    }


//    private String getUrl(LatLng origin, LatLng dest) {
//        // Origin of route
//        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
//        // Destination of route
//        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
//        // Sensor enabled
//        String sensor = "sensor=false";
//        String mode = "&mode=DRIVING";
//        // Building the parameters to the web service
//        String parameters = str_origin + "&" + str_dest + "&" + sensor + mode;
//        // Output format
//        String output = "json";
//        // Building the url to the web service
//        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;
//        System.out.println("URL by Lat n Long:: " + url);
//        return url;
//    }

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

    private String getUrl(String origin, String dest) {
        // Origin of route
        String str_origin = "origin=" + origin; // + "," + origin.longitude;
        // Destination of route
        String str_dest = "destination=" + dest;//.latitude + "," + dest.longitude;
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

    private String getStringData(String name) {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String value = extras.getString(name);
            return value;
        }
        return null;
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {
//        mLocationRequest = new LocationRequest();
//        mLocationRequest.setInterval(1000);
//        mLocationRequest.setFastestInterval(5000);
//        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//        try {
//            if (ContextCompat.checkSelfPermission(getActivity(), permissionsRequired[0])
//                    == PackageManager.PERMISSION_GRANTED) {
//                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
//
//            }
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
        try {
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }

            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
            if (mLastLocation != null) {
                // changeMap(mLastLocation);
                initCamera(mLastLocation, rideType + "");
                Log.d("TAG", "ON connected");

            } else
                try {
                    LocationServices.FusedLocationApi.removeLocationUpdates(
                            mGoogleApiClient, this);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            try {
                mLocationRequest = new LocationRequest();
                mLocationRequest.setInterval(10000);
                mLocationRequest.setFastestInterval(5000);
                mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                LocationServices.FusedLocationApi.requestLocationUpdates(
                        mGoogleApiClient, mLocationRequest, this);

            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception ex) {
            ex.printStackTrace();

        }

    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
//        if (mCurrLocationMarker != null) {
//            mCurrLocationMarker.remove();
//        }
//        //mLastLocation.setBearing();
//        initCamera(mLastLocation,rideType +"");


    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), permissionsRequired[0])
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(InterStateTripActivity.this, permissionsRequired[0])) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(getApplicationContext())
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(InterStateTripActivity.this,
                                        new String[]{permissionsRequired[0]},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(InterStateTripActivity.this,
                        new String[]{permissionsRequired[0]},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(getApplicationContext(), permissionsRequired[0])
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mGoogleMap.setMyLocationEnabled(true);
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(getApplicationContext(), "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

        }
    }

    private void initCamera(Location location, String rideType) {

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        CameraPosition position = CameraPosition.builder()
                .target(latLng)
                .zoom(15.5f)
                .bearing(300)
                .tilt(50)
                .build();

        mGoogleMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(position), null);

        markerOptions.position(latLng);
        markerOptions.title("Pick Up Point");
        markerOptions.draggable(true).visible(true);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        // mCurrLocationMarker = mGoogleMap.addMarker(markerOptions);
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
        mGoogleMap.setMapType(MAP_TYPES[1]);
        mGoogleMap.getUiSettings().setZoomControlsEnabled(true);

        // System.out.println("getLatitude:: " +mLastLocation.getLatitude() + " getLongitude ::  " + mLastLocation.getLongitude());
//        try {
//            new GETAVAILABLEDRIVER(token.getToken(), mLastLocation.getLatitude(), mLastLocation.getLongitude(),rideType +"").execute();
//
//        }catch (Exception ex){
//            ex.printStackTrace();
//        }

        //startIntentService(location);
    }

    @Override
    public void onCameraIdle() {

    }

    @Override
    public void onCameraMoveCanceled() {

    }

    @Override
    public void onCameraMove() {

    }

    @Override
    public void onCameraMoveStarted(int i) {

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

    protected Marker createMarker(double latitude, double longitude, String title, String snippet) {

        return mMap.addMarker(new MarkerOptions()
                .position(new LatLng(latitude, longitude))
                .anchor(0.5f, 0.5f)
                .title(title)
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
        menu.clear();

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

    public String getLatLongByURL(String requestURL) {
        URL url;
        String response = "";
        try {
            url = new URL(requestURL);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");
            conn.setDoOutput(true);
            int responseCode = conn.getResponseCode();

            if (responseCode == HttpsURLConnection.HTTP_OK) {
                String line;
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line = br.readLine()) != null) {
                    response += line;
                }
            } else {
                response = "";
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("response LatLongPoint::: " + response);
        return response;
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

                if (routes != null || routes.size() <= 0) {


                }

            } catch (Exception e) {
                Log.d("ParserTask", e.toString());
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();


            if (result.size() < 1) {
                //Toast.makeText(getBaseContext(), "No Points", Toast.LENGTH_SHORT).show();
                btnSendRequest.setVisibility(View.GONE);
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
                    //System.out.println("Distance:" + j + ", Duration:" + j);

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

            // System.out.println("Distance:" + distance + ", Duration:" + duration);
            txtDistanceEstimate.setText(distance);
            txtTimeEstimate.setText(duration);
            txtRideType.setText(accountName);//riderTypeName);  //rideType==1?"Classic Ride":"Elegant Ride");
            txtPaymentType.setText(paymentType);
            txtPickUp.setText(PickUpState);
            txtDropOff.setText(DropOffState);
            txtFairEstimate.setText(TotalFare);

            if (lineOptions != null) {
                mMap.addPolyline(lineOptions);
            } else {
                Log.d("onPostExecute", "without Polylines drawn");
            }
        }
    }

    public class REQUEST_TRIP extends AsyncTask<Void, Void, String> {

        String token;
        String regID;
        String tripDate;
        String takeOffStateID;
        String destinationStateID;
        String totalFare;
        String regId;
        ProgressDialog pDialog;

        public REQUEST_TRIP(String token, String tripDate, String takeOffStateID, String destinationStateID, String totalFare, String regId) {

            this.token = token;
            this.tripDate = tripDate;
            this.takeOffStateID = takeOffStateID;
            this.destinationStateID = destinationStateID;
            this.totalFare = totalFare;
            this.regID = regId;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = ProgressDialog.show(InterStateTripActivity.this, "", "Loading. Please wait...", true);
        }

        @Override
        protected String doInBackground(Void... params) {
            String result = null;
            String resp = "";
            Log.i(Constants.LOG_TAG, "<<<<STARTING LOGIN>>>>");
            try {
                if (Util.myClickHandler(getApplicationContext())) {

                    result = Connections.REQUEST_INTERSTATE_TRIP(token, tripDate, takeOffStateID, destinationStateID, totalFare, regID);

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


                    Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();

                Toast.makeText(getApplicationContext(), "Oops, something went wrong", Toast.LENGTH_LONG).show();

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

                        createMarker(_driver.getPointLat(), _driver.getPointLong(), _driver.getLogTime() + "", "Qothoo");
                    }
                } else {
                    Toast.makeText(InterStateTripActivity.this, "Internet connection not available", Toast.LENGTH_SHORT).show();

                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        @Override
        protected void onCancelled() {

        }
    }

    private class startLatLongFinder extends AsyncTask<String, Void, String[]> {
        //ProgressDialog dialog = new ProgressDialog(getApplicationContext());
        String address;

        public startLatLongFinder(String address) {
            this.address = address;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //dialog.setMessage("Please wait...");
            //dialog.setCanceledOnTouchOutside(false);
            //dialog.show();
        }

        @Override
        protected String[] doInBackground(String... params) {
            String response;
            try {
                response = getLatLongByURL("http://maps.google.com/maps/api/geocode/json?address=" + address + "&sensor=false");
                // Log.d("response",""+response);
                return new String[]{response};
            } catch (Exception e) {
                return new String[]{"error"};
            }
        }

        @Override
        protected void onPostExecute(String... result) {
            try {
                JSONObject jsonObject = new JSONObject(result[0]);

                startLongitude = ((JSONArray) jsonObject.get("results")).getJSONObject(0)
                        .getJSONObject("geometry").getJSONObject("location")
                        .getDouble("lng");

                startLatitude = ((JSONArray) jsonObject.get("results")).getJSONObject(0)
                        .getJSONObject("geometry").getJSONObject("location")
                        .getDouble("lat");

                startDirection = new LatLng(startLatitude, startLongitude);

                System.out.println("startLatitude " + startLatitude + " startLongitude:: " + startLongitude);

            } catch (JSONException e) {
                e.printStackTrace();
            }
//            if (dialog.isShowing()) {
//                dialog.dismiss();
//            }
        }
    }

    private class endLatLongFinder extends AsyncTask<String, Void, String[]> {
        //ProgressDialog dialog = new ProgressDialog(getApplicationContext());
        String address;

        public endLatLongFinder(String address) {
            this.address = address;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            dialog.setMessage("Please wait...");
//            dialog.setCanceledOnTouchOutside(false);
//            dialog.show();
        }

        @Override
        protected String[] doInBackground(String... params) {
            String response;
            try {
                response = getLatLongByURL("http://maps.google.com/maps/api/geocode/json?address=" + address + "&sensor=false");
                // Log.d("response",""+response);
                return new String[]{response};
            } catch (Exception e) {
                return new String[]{"error"};
            }
        }

        @Override
        protected void onPostExecute(String... result) {
            try {
                JSONObject jsonObject = new JSONObject(result[0]);

                endLongititude = ((JSONArray) jsonObject.get("results")).getJSONObject(0)
                        .getJSONObject("geometry").getJSONObject("location")
                        .getDouble("lng");

                endLatitude = ((JSONArray) jsonObject.get("results")).getJSONObject(0)
                        .getJSONObject("geometry").getJSONObject("location")
                        .getDouble("lat");

                endDirection = new LatLng(endLatitude, endLongititude);
                // System.out.println("endLatitude " +endLatitude + " endLongititude:: " + endLongititude);

            } catch (JSONException e) {
                e.printStackTrace();
            }
//            if (dialog.isShowing()) {
//                dialog.dismiss();
//            }
        }
    }


}
