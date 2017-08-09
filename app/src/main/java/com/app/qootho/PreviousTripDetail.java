package com.app.qootho;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.qootho.Model.TokenObject;
import com.app.qootho.Model.TripHistory;
import com.app.qootho.Model.UserAccount;
import com.app.qootho.Utilities.DirectionJSONParser;
import com.app.qootho.Utilities.QoothoDB;
import com.app.qootho.Utilities.SessionManager;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

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

public class PreviousTripDetail extends AppCompatActivity implements OnMapReadyCallback {

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private static final String LOG_TAG = "MyActivity";
    private final int[] MAP_TYPES = {GoogleMap.MAP_TYPE_SATELLITE,
            GoogleMap.MAP_TYPE_NORMAL,
            GoogleMap.MAP_TYPE_HYBRID,
            GoogleMap.MAP_TYPE_TERRAIN,
            GoogleMap.MAP_TYPE_NONE};
    public TripHistory mItem;
    protected LatLng start;
    protected LatLng end;
    protected GoogleApiClient mGoogleApiClient;
    QoothoDB db = null;
    SessionManager session = null;
    SharedPreferences installPref;
    String username;
    UserAccount user;
    TokenObject token;
    TextView txtBack;
    TextView txtNext;
    TextView title;
    TripHistory history;
    TextView txtCarModel, txtTripDate, txtTripAmount, txtTapHereToReport, txtDriverName,
            txtBaseFare, txtDistance, txtTime, txtSubTotal, txtRoundingDown, txtTotal;
    ImageView img;
    double destLat;
    double destLong;
    double pickUpLat;
    double pickUpLong;
    Location mLastLocation;
    MapView mMapView;
    GoogleMap mGoogleMap;
    String[] permissionsRequired = new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION};
    Marker mCurrLocationMarker;
    private GoogleMap mMap;
    private LocationRequest mLocationRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_previous_trip_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        history = new TripHistory();
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
        title.setText("Trip Details");
        txtBack = (TextView) toolbar.findViewById(R.id.txtBack);
        txtNext = (TextView) toolbar.findViewById(R.id.txtNext);
        txtNext.setVisibility(View.GONE);
        txtBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        Intent intent = getIntent();
        Bundle args = intent.getBundleExtra("BUNDLE");
        ArrayList<TripHistory> object = (ArrayList<TripHistory>) args.getSerializable("help");
        int position = args.getInt("position");
        history = object.get(position);


        Toast.makeText(PreviousTripDetail.this, position, Toast.LENGTH_SHORT).show();

        txtTripDate = (TextView) findViewById(R.id.txtTripDate);
        txtTripAmount = (TextView) findViewById(R.id.txtTripAmount);
        txtCarModel = (TextView) findViewById(R.id.txtCarModel);
        txtTapHereToReport = (TextView) findViewById(R.id.txtTapHereToPay);
        txtDriverName = (TextView) findViewById(R.id.txtDriversName);
        txtBaseFare = (TextView) findViewById(R.id.txtBaseType);
        txtDistance = (TextView) findViewById(R.id.txtDistance);
        txtTime = (TextView) findViewById(R.id.txtTime);
        txtSubTotal = (TextView) findViewById(R.id.txtSubTotal);
        txtRoundingDown = (TextView) findViewById(R.id.txtRoundingDown);
        txtTotal = (TextView) findViewById(R.id.txtTotal);


        txtTotal.setText(history.getTotalFare() + "");
        txtSubTotal.setText(history.getSubTotal() + "");
        txtTime.setText(history.getTimeCost() + "");
        txtDriverName.setText("You rated " + history.getRideTypeName());
        txtTripDate.setText(history.getTripStartTime());
        txtTripAmount.setText(history.getBaseFare() + "");
        txtCarModel.setText(history.getBrandName() + "" + history.getModelName());
        txtRoundingDown.setText(history.getRoundDownValue() + "");
        txtBaseFare.setText(history.getBaseFare() + "");
        txtDistance.setText(history.getDistanceCost() + "");

        destLat = history.getActualDestinationLat();
        destLong = history.getActualDestinationLong();
        pickUpLat = history.getActualPickUpLat();
        pickUpLong = history.getActualDestinationLong();


        mMapView = (MapView) findViewById(R.id.mapView);


        mMapView.onCreate(savedInstanceState);
        mMapView.onResume(); // needed to get the map to display immediately
        try {
            MapsInitializer.initialize(getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

//            txtContentText = (TextView) view.findViewById(R.id.txtFeedContent);
//            feedPix = (ImageView) view.findViewById(R.id.feedPix);
//            ImageBanner = (ImageView) view.findViewById(R.id.ImageContent);
//            txtDescription = (TextView) view.findViewById(R.id.txtDescription);
//
        mMapView.getMapAsync(this);


    }


    private void checkLocationPermission(Context context) {
        if (ContextCompat.checkSelfPermission(PreviousTripDetail.this, permissionsRequired[0])
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(PreviousTripDetail.this, permissionsRequired[0])) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(context)
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(PreviousTripDetail.this,
                                        new String[]{permissionsRequired[0]},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(PreviousTripDetail.this,
                        new String[]{permissionsRequired[0]},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
        }
    }

//
//    protected synchronized void buildGoogleApiClient() {
//        mGoogleApiClient = new GoogleApiClient.Builder(context)
//                .addConnectionCallbacks(this)
//                .addOnConnectionFailedListener(this)
//                .addApi(LocationServices.API)
//                .build();
//        mGoogleApiClient.connect();
//    }
//    @Override
//    public void onConnected(Bundle bundle) {
//        mLocationRequest = new LocationRequest();
//        mLocationRequest.setInterval(100);
//        mLocationRequest.setFastestInterval(1000);
//        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//        try {
//            if (ContextCompat.checkSelfPermission(context, permissionsRequired[0])
//                    == PackageManager.PERMISSION_GRANTED) {
//                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
//
//            }
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//    }
//
//    @Override
//    public void onConnectionSuspended(int i) {
//    }
//
//    @Override
//    public void onConnectionFailed(ConnectionResult connectionResult) {
//    }
//
//    @Override
//    public void onLocationChanged(Location location) {
//        mLastLocation = location;
//        if (mCurrLocationMarker != null) {
//            mCurrLocationMarker.remove();
//        }
//        //mLastLocation.setBearing();
//        initCamera(mLastLocation);
//
//    }
//
//    private void initCamera(Location location) {
//
//        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
//        MarkerOptions markerOptions = new MarkerOptions();
//        CameraPosition position = CameraPosition.builder()
//                .target(latLng)
//                .zoom(10f)
//                .bearing(45)
//                .tilt(0.0f)
//                .build();
//
//
//        mGoogleMap.animateCamera(CameraUpdateFactory
//                .newCameraPosition(position), null);
//
//        markerOptions.position(latLng);
//        markerOptions.title("Pick Up Point");
//        markerOptions.draggable(true).visible(true);
//        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
//        // mCurrLocationMarker = mGoogleMap.addMarker(markerOptions);
//        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
//        mGoogleMap.setMapType(MAP_TYPES[1]);
//        mGoogleMap.getUiSettings().setZoomControlsEnabled(false);
//    }


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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng startDirection = new LatLng(pickUpLat, pickUpLong);
        LatLng endDirection = new LatLng(destLat, destLong);
        mMap.addMarker(new MarkerOptions().position(startDirection).title(history.getPickUpAddress()).icon(BitmapDescriptorFactory.fromResource(R.drawable.add_marker)));
        mMap.addMarker(new MarkerOptions().position(endDirection).title(history.getDropOffAddress()).icon(BitmapDescriptorFactory.fromResource(R.drawable.add_marker)));
        mMap.setMapType(MAP_TYPES[1]);
        mMap.setTrafficEnabled(false);
        // Getting URL to the Google Directions API
        String url = getUrl(startDirection, endDirection);
        Log.d("onMapClick", url.toString());
        FetchUrl FetchUrl = new FetchUrl();
        FetchUrl.execute(url);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(startDirection));
        CameraPosition position = CameraPosition.builder()
                .target(startDirection)
                .zoom(10f)
                .bearing(45)
                .tilt(0.0f)
                .build();
        mMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(position), null);

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
//        @Override
//        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
//            ArrayList<LatLng> points;
//            PolylineOptions lineOptions = null;
//
//            // Traversing through all the routes
//            for (int i = 0; i < result.size(); i++) {
//                points = new ArrayList<>();
//                lineOptions = new PolylineOptions();
//
//                // Fetching i-th route
//                List<HashMap<String, String>> path = result.get(i);
//
//                // Fetching all the points in i-th route
//                for (int j = 0; j < path.size(); j++) {
//                    HashMap<String, String> point = path.get(j);
//
//                    double lat = Double.parseDouble(point.get("lat"));
//                    double lng = Double.parseDouble(point.get("lng"));
//                    LatLng position = new LatLng(lat, lng);
//
//                    points.add(position);
//                }
//
//                // Adding all the points in the route to LineOptions
//                lineOptions.addAll(points);
//                lineOptions.width(10);
//                lineOptions.color(Color.BLACK);
//
//                Log.d("onPostExecute", "onPostExecute lineoptions decoded");
//
//            }
//
//            // Drawing polyline in the Google Map for the i-th route
//            if (lineOptions != null) {
//                mMap.addPolyline(lineOptions);
//            } else {
//                Log.d("onPostExecute", "without Polylines drawn");
//            }
//        }

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
                    System.out.println("Distance:" + j + ", Duration:" + j);

                    if (j == 0) {    // Get distance from the list
                        // distance = point.get("distance");
                        continue;
                    } else if (j == 1) { // Get duration from the list
                        // duration = point.get("duration");
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

//            System.out.println("Distance:" + distance + ", Duration:" + duration);
//            txtDistanceEstimate.setText(distance);
//            txtTimeEstimate.setText(duration);
            if (lineOptions != null) {
                mMap.addPolyline(lineOptions);
            } else {
                Log.d("onPostExecute", "without Polylines drawn");
            }
        }
    }

}
