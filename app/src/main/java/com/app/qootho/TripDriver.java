package com.app.qootho;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.qootho.Model.DirectionModel;
import com.app.qootho.Model.TokenObject;
import com.app.qootho.Model.TripDriverDetail;
import com.app.qootho.Model.UserAccount;
import com.app.qootho.Utilities.Connections;
import com.app.qootho.Utilities.Constants;
import com.app.qootho.Utilities.DirectionJSONParser;
import com.app.qootho.Utilities.QoothoDB;
import com.app.qootho.Utilities.SessionManager;
import com.app.qootho.Utilities.Util;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TripDriver extends AppCompatActivity implements OnMapReadyCallback {


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
    TextView txtFairType, txtPaymentType, txtPickUp, txtDropOff, txtDistanceEstimate, txtTimeEstimate, txtFairEstimate, txtRideType;
    String distance = "";
    String duration = "";
    Button btnSendRequest;
    QoothoDB db = null;
    SessionManager session = null;
    SharedPreferences installPref;
    String username;
    UserAccount user;
    TokenObject token;
    Button btnCallDriver;
    Button btnCancelRequest;
    ImageView DriverImage;
    ImageView CarImage;
    TextView txtPlateNumber, txtTime, txtBrandName, txtNames;
    TripDriverDetail trip;
    TextView txtBack;
    TextView txtNext;
    TextView title;
    String tripId;
    String regID = "";
    private GoogleMap mMap;
    private RecyclerView mapListView;
    private BroadcastReceiver mRegistrationBroadcastReceiver;

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
        }


        setContentView(R.layout.activity_trip_driver);


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
        txtNext.setVisibility(View.INVISIBLE);
        txtBack.setVisibility(View.GONE);
        txtBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        db = new QoothoDB(getApplicationContext());
        session = new SessionManager(getApplicationContext());
        installPref = getSharedPreferences(SessionManager.PREF_NAME, SessionManager.PRIVATE_MODE);
        username = installPref.getString(SessionManager.KEY_USERNAME, null);
        user = new UserAccount();
        token = new TokenObject();
        user = db.getUserProfile(username);
        token = db.getTokenByUsername(username);

        trip = new TripDriverDetail();


        startLatitude = getDoubleData("startLatitude");
        startLongitude = getDoubleData("startLongitude");
        endLatitude = getDoubleData("endLatitude");
        endLongititude = getDoubleData("endLongititude");
        startAddress = getStringData("startAddress");
        endAddress = getStringData("endAddress");
        tripId = getStringData("tripId");

        new GETTRIPDRIVER(token.getToken(), tripId).execute();


        btnCallDriver = (Button) findViewById(R.id.txtCallTheDriver);
        btnCancelRequest = (Button) findViewById(R.id.txtCancelRequest);
        txtPlateNumber = (TextView) findViewById(R.id.txtCarPlateNumber);
        txtTime = (TextView) findViewById(R.id.txtTripDurtaion);
        txtBrandName = (TextView) findViewById(R.id.txtCarModel);
        txtNames = (TextView) findViewById(R.id.txtDriversName);
        DriverImage = (ImageView) findViewById(R.id.txtDriverPicture);
        CarImage = (ImageView) findViewById(R.id.carImage);


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        btnCancelRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TripDriver.this, CancelTrip.class);
                Bundle b = new Bundle();
                b.putString("tripId", tripId);
                intent.putExtras(b);
                startActivity(intent);
            }
        });

        btnCallDriver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (ActivityCompat.checkSelfPermission(TripDriver.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:" + trip.getPhoneNumber()));
                    startActivity(callIntent);
                } catch (Exception ex) {
                    ex.printStackTrace();

                }
            }
        });


        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                if (intent.getAction().equals(Constants.PUSH_NOTIFICATION)) {
                    // new push notification is received
                    String message = intent.getStringExtra("message");
                    Toast.makeText(getApplicationContext(), "Push notification: " + message, Toast.LENGTH_LONG).show();

                }
            }
        };


    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Add a marker in Sydney and move the camera
        LatLng startDirection = new LatLng(startLatitude, startLongitude);
        LatLng endDirection = new LatLng(endLatitude, endLongititude);
//        mMap.addMarker(new MarkerOptions().position(startDirection).title(startAddress));
//        mMap.addMarker(new MarkerOptions().position(endDirection).title(endAddress));
        mMap.addMarker(new MarkerOptions().position(startDirection).title(startAddress).icon(BitmapDescriptorFactory.fromResource(R.drawable.add_marker)));
        mMap.addMarker(new MarkerOptions().position(endDirection).title(endAddress).icon(BitmapDescriptorFactory.fromResource(R.drawable.add_marker)));

        mMap.setMapType(MAP_TYPES[1]);
        mMap.setTrafficEnabled(true);

        // Getting URL to the Google Directions API
        String url = getUrl(startDirection, endDirection);
        Log.d("onMapClick", url.toString());
        FetchUrl FetchUrl = new FetchUrl();
        FetchUrl.execute(url);
        // mMap.moveCamera(CameraUpdateFactory.newLatLng(startDirection));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(startDirection, 17));
        CameraPosition position = CameraPosition.builder()
                .target(startDirection)
                .zoom(15f)
                .bearing(45)
                .tilt(0.0f)
                .build();
        mMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(position), null);

    }

    @Override
    protected void onResume() {
        super.onResume();

        // register GCM registration complete receiver
        //LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
        // new IntentFilter(Config.REGISTRATION_COMPLETE));

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Constants.PUSH_NOTIFICATION));

        // clear the notification area when the app is opened
        ///  NotificationUtils.clearNotifications(getApplicationContext());
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

    private String getStringData(String name) {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String value = extras.getString(name);
            return value;
        }
        return null;
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

    public TripDriverDetail GETDRIVER(String response) {
        TripDriverDetail user = null;
        if (response != null) {
            System.out.println("RESPONSE::: " + response);
            GsonBuilder builder = new GsonBuilder();
            Gson mGson = builder.create();
            user = mGson.fromJson(response, TripDriverDetail.class);

            txtPlateNumber.setText(user.getPlateNumber());
            txtBrandName.setText(user.getBrandName() + " " + user.getModelName());
            txtNames.setText(user.getSurname() + " " + user.getFirstName());
            DriverImage.setImageBitmap(Util.GetImageFromString(user.getRaiderPhoto()));
            CarImage.setImageBitmap(Util.GetImageFromString(user.getVehiclePhoto()));

        }
        return user;
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

            // System.out.println("jsonData  :::: " + jsonData);
            try {
                jObject = new JSONObject(jsonData[0]);
                // Log.d("ParserTask", jsonData[0].toString());
                DirectionJSONParser parser = new DirectionJSONParser();
                //Log.d("ParserTask", parser.toString());

                // Starts parsing data
                routes = parser.parse(jObject);
                //Log.d("ParserTask", "Executing routes");
                //Log.d("ParserTask", routes.toString());

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
                Toast.makeText(getBaseContext(), "No Points", Toast.LENGTH_SHORT).show();
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

            //System.out.println("Distance:" + distance + ", Duration:" + duration);

            if (lineOptions != null) {
                mMap.addPolyline(lineOptions);
            } else {
                Log.d("onPostExecute", "without Polylines drawn");
            }
        }
    }

    public class GETTRIPDRIVER extends AsyncTask<Void, Void, String> {

        String token;
        String TripID;
        ProgressDialog pDialog;


        public GETTRIPDRIVER(String token, String TripID) {
            this.TripID = TripID;
            this.token = token;


        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = ProgressDialog.show(TripDriver.this, "", "Loading. Please wait...", true);
        }

        @Override
        protected String doInBackground(Void... params) {
            String result = null;
            String resp = "";
            Log.i(Constants.LOG_TAG, "<<<<STARTING LOGIN>>>>");
            try {

                result = Connections.GETTRIPDRIVER(token, TripID);
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
            Toast.makeText(getApplicationContext(), resp, Toast.LENGTH_LONG).show();
            pDialog.dismiss();
            String response;
            try {
                if (resp.contains("\"brandName\":") && resp != "") {
                    trip = GETDRIVER(resp);

                    Toast.makeText(getApplicationContext(), trip.getFirstName(), Toast.LENGTH_LONG).show();


                } else {
                    Toast.makeText(getApplicationContext(), resp, Toast.LENGTH_LONG).show();

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
