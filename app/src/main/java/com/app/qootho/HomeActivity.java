package com.app.qootho;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
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
import android.widget.EditText;
import android.widget.Toast;

import com.app.qootho.Model.TokenObject;
import com.app.qootho.Model.UserAccount;
import com.app.qootho.Utilities.Connections;
import com.app.qootho.Utilities.Constants;
import com.app.qootho.Utilities.QoothoDB;
import com.app.qootho.Utilities.SessionManager;
import com.app.qootho.Utilities.Util;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

public class HomeActivity extends AppCompatActivity implements OnMapReadyCallback {

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private final int[] MAP_TYPES = {GoogleMap.MAP_TYPE_SATELLITE,
            GoogleMap.MAP_TYPE_NORMAL,
            GoogleMap.MAP_TYPE_HYBRID,
            GoogleMap.MAP_TYPE_TERRAIN,
            GoogleMap.MAP_TYPE_NONE};
    protected LatLng start;
    int PLACE_AUTOCOMPLETE_REQUEST_CODE_TO = 1;
    int PLACE_AUTOCOMPLETE_REQUEST_CODE_FROM = 2;
    GoogleMap mGoogleMap;
    SupportMapFragment mapFrag;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    //private Location mCurrentLocation;
    Marker mCurrLocationMarker;
    String[] permissionsRequired = new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION};
    QoothoDB db = null;
    SessionManager session = null;
    SharedPreferences installPref;
    String username;
    UserAccount user;
    TokenObject token;
    double workLatitude;
    double workLongitude;
    String workAddress;
    EditText txtSearch;
    // String token ;
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        txtSearch = (EditText) toolbar.findViewById(R.id.start);
        toolbar.setNavigationIcon(R.drawable.ic_action_keyboard_backspace);
        // toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(true);

        txtSearch = (EditText) toolbar.findViewById(R.id.start);
        toolbar.setNavigationIcon(R.drawable.ic_action_keyboard_backspace);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        db = new QoothoDB(getApplicationContext());
        session = new SessionManager(getApplicationContext());
        installPref = getSharedPreferences(SessionManager.PREF_NAME, SessionManager.PRIVATE_MODE);
        username = installPref.getString(SessionManager.KEY_USERNAME, null);
        user = new UserAccount();
        user = db.getUserProfile(username);
        token = new TokenObject();
        token = db.getTokenByUsername(username);

        workLatitude = user.getHomeLat() != null ? user.getHomeLat() : 0.0;
        workLongitude = user.getHomeLong() != null ? user.getHomeLong() : 0.0;
        workAddress = user.getHomeAddress();

        txtSearch.setEnabled(true);

        txtSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent =
                            new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                                    .build(HomeActivity.this);
                    startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE_TO);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }

            }
        });

        mapFragment.getMapAsync(this);
    }

    @Override
    public void onPause() {
        super.onPause();

        //stop location updates when Activity is no longer active
//        if (mGoogleApiClient != null) {
//            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
//        }
    }

//    protected synchronized void buildGoogleApiClient() {
//        mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
//                .addConnectionCallbacks(this)
//                .addOnConnectionFailedListener(this)
//                .addApi(LocationServices.API)
//                .build();
//        mGoogleApiClient.connect();
//    }

//    @Override
//    public void onConnected(Bundle bundle) {
//
//        mLocationRequest = new LocationRequest();
//        mLocationRequest.setInterval(1000);
//        mLocationRequest.setFastestInterval(1000);
//        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//        try {
//            if (ContextCompat.checkSelfPermission(getApplicationContext(), permissionsRequired[0])
//                    == PackageManager.PERMISSION_GRANTED) {
//                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
//            }
//        }catch (Exception ex){
//            ex.printStackTrace();
//        }
//
////        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
////                mGoogleApiClient);
////        if (mLastLocation != null) {
////            // changeMap(mLastLocation);
////            Log.d("connected", "ON connected");
////
////        }else {
////            try {
////                LocationServices.FusedLocationApi.removeLocationUpdates(
////                        mGoogleApiClient, this);
////
////            } catch (Exception e) {
////                e.printStackTrace();
////            }
////
////            mLocationRequest = new LocationRequest();
////            mLocationRequest.setInterval(10000);
////            mLocationRequest.setFastestInterval(5000);
////            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
////            try {
////                if (ContextCompat.checkSelfPermission(getActivity(), permissionsRequired[0])
////                        == PackageManager.PERMISSION_GRANTED) {
////                    LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
////                }
////            } catch (Exception ex) {
////                ex.printStackTrace();
////            }
////        }
//    }
//
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
//        initCamera(mLastLocation.getLatitude(),mLastLocation.getLongitude(),"");
//
//    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng startDirection = new LatLng(workLatitude, workLongitude);
        // LatLng endDirection =  new LatLng(endLatitude,endLongititude);
        mMap.addMarker(new MarkerOptions().position(startDirection).title(workAddress));
        //mMap.addMarker(new MarkerOptions().position(endDirection).title(endAddress));
        mMap.setMapType(MAP_TYPES[1]);
        mMap.setTrafficEnabled(false);

        // Getting URL to the Google Directions API
        //String url = getUrl(startDirection, endDirection);
        //Log.d("onMapClick", url.toString());
        // BookRideActivity.FetchUrl FetchUrl = new BookRideActivity.FetchUrl();
        //FetchUrl.execute(url);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(startDirection));
        CameraPosition position = CameraPosition.builder()
                .target(startDirection)
                .zoom(19f)
                .bearing(0.0f)
                .tilt(0.0f)
                .build();
        mMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(position), null);
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), permissionsRequired[0])
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(HomeActivity.this, permissionsRequired[0])) {

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
                                ActivityCompat.requestPermissions(HomeActivity.this,
                                        new String[]{permissionsRequired[0]},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(HomeActivity.this,
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

//                        if (mGoogleApiClient == null) {
//                            buildGoogleApiClient();
//                        }
//                        mGoogleMap.setMyLocationEnabled(true);
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(getApplicationContext(), "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private void initCamera(double lat, double longitude, String title) {

        mMap.clear();

        LatLng latLng = new LatLng(lat, longitude);
        MarkerOptions markerOptions = new MarkerOptions();
        CameraPosition position = CameraPosition.builder()
                .target(latLng)
                .zoom(19f)
                .bearing(0.0f)
                .tilt(0f)
                .build();

        mMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(position), null);

        markerOptions.position(latLng);
        markerOptions.title(title);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        mCurrLocationMarker = mMap.addMarker(markerOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
        mMap.setMapType(MAP_TYPES[1]);


    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE_TO) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(HomeActivity.this, data);
                Log.i("WEMAMOBILE", "Place: " + place.getName());

                txtSearch.setText(place.getName());

                start = place.getLatLng();
                Log.i("getAddress", place.getAddress() + "");

                new UPDATE(start.longitude, start.latitude, place.getAddress().toString(), token.getToken()).execute();


            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(HomeActivity.this, data);
                // TODO: Handle the error.
                Log.i("WEMAMOBILE", status.getStatusMessage());

                Toast.makeText(HomeActivity.this, status.getStatusMessage(), Toast.LENGTH_LONG).show();

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
            Status status = PlaceAutocomplete.getStatus(HomeActivity.this, data);
            // TODO: Handle the error.
            Log.i("WEMAMOBILE", status.getStatusMessage());
            Toast.makeText(HomeActivity.this, status.getStatusMessage(), Toast.LENGTH_LONG).show();


        } else if (resultCode == RESULT_CANCELED) {
            // The user canceled the operation.
        }
    }

//    @Override
//    public void onMapClick(LatLng latLng) {
//
//    }

//    @Override
//    public void onMapClick(LatLng latLng) {
//        initCamera(latLng.latitude,latLng.longitude,"Set Work Address");
//    }

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

        double longitude;
        double latitude;
        String Address;
        String token;
        ProgressDialog pDialog;


        public UPDATE(double longitude, double latitude, String Address, String token) {
            this.longitude = longitude;
            this.latitude = latitude;
            this.Address = Address;
            this.token = token;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = ProgressDialog.show(HomeActivity.this, "", "Loading. Please wait...", true);
        }

        @Override
        protected String doInBackground(Void... params) {
            String result = null;
            String resp = "";
            Log.i(Constants.LOG_TAG, "<<<<STARTING LOGIN>>>>");
            try {

                System.out.println(token + latitude + longitude + " - " + Address);
                result = Connections.UPDATEHOMEADDRESS(token, latitude, longitude, Address);
                //if( result.contains("{\"access_token\"") && result != ""){
                JSONObject jsonObj = new JSONObject(result);
                String msg = jsonObj.getString("message");
                if (msg.contains("Ok")) {
                    GETUSERDEATIAL(token, username);
                    System.out.println("GETUSERDEATIAL::: " + result);
                }

                //}
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

                    initCamera(this.latitude, this.longitude, Address.toString());
                    Toast.makeText(getApplicationContext(), "Successful Updated", Toast.LENGTH_LONG).show();

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
