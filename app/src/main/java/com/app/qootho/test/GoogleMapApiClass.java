package com.app.qootho.test;

import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

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

/**
 * Created by macbookpro on 14/06/2017.
 */


public class GoogleMapApiClass extends SupportMapFragment
        implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private final int[] MAP_TYPES = {GoogleMap.MAP_TYPE_SATELLITE,
            GoogleMap.MAP_TYPE_NORMAL,
            GoogleMap.MAP_TYPE_HYBRID,
            GoogleMap.MAP_TYPE_TERRAIN,
            GoogleMap.MAP_TYPE_NONE};
    GoogleMap mGoogleMap;
    SupportMapFragment mapFrag;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    //private Location mCurrentLocation;
    Marker mCurrLocationMarker;
    String[] permissionsRequired = new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION};

    @Override
    public void onResume() {
        super.onResume();

        setUpMapIfNeeded();
    }

    private void setUpMapIfNeeded() {

        if (mGoogleMap == null) {
            getMapAsync(this);
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        //stop location updates when Activity is no longer active
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        //mGoogleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        //Initialize Google Play Services
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getActivity(),
                    permissionsRequired[0])
                    == PackageManager.PERMISSION_GRANTED) {
                //Location Permission already granted
                buildGoogleApiClient();
                mGoogleMap.setMyLocationEnabled(true);
                mGoogleMap.setMapType(MAP_TYPES[1]);
                // mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(startDirection));
                mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(11));

                //initCamera(mLastLocation);
            } else {
                //Request Location Permission
                checkLocationPermission();
            }
        } else {
            buildGoogleApiClient();
            mGoogleMap.setMyLocationEnabled(true);
            mGoogleMap.setMapType(MAP_TYPES[1]);
            mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(11));
            //initCamera(mLastLocation);
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }


//    @Override
//    public void onConnected(Bundle bundle) {
//        mCurrentLocation = LocationServices
//                .FusedLocationApi
//                .getLastLocation( mGoogleApiClient );
//
//        if(mCurrentLocation != null) {
//
//            initCamera(mCurrentLocation);
//        }
//    }

    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        try {
            if (ContextCompat.checkSelfPermission(getActivity(), permissionsRequired[0])
                    == PackageManager.PERMISSION_GRANTED) {
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
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
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }
        //mLastLocation.setBearing(Float.MAX_VALUE);
        initCamera(mLastLocation);

    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(getActivity(), permissionsRequired[0])
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), permissionsRequired[0])) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(getActivity())
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(getActivity(),
                                        new String[]{permissionsRequired[0]},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(getActivity(),
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
                    if (ContextCompat.checkSelfPermission(getActivity(), permissionsRequired[0])
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mGoogleMap.setMyLocationEnabled(true);
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(getActivity(), "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private void initCamera(Location location) {

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
//        CameraPosition position = CameraPosition.builder()
//                .target( latLng )
//                .zoom( 10f )
//               // .bearing( 45 )
//                .tilt( 0.0f )
//                .build();

        CameraPosition position =
                new CameraPosition.Builder()
                        .target(latLng)
                        .bearing(45)
                        .tilt(90)
                        .zoom(mGoogleMap.getCameraPosition().zoom)
                        .build();


        mGoogleMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(position), null);

        markerOptions.position(latLng);
        markerOptions.title("Pick Up Point");
        markerOptions.draggable(true).visible(true);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        mCurrLocationMarker = mGoogleMap.addMarker(markerOptions);
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14));
        mGoogleMap.setMapType(MAP_TYPES[1]);
        // mGoogleMap.setTrafficEnabled( true );
        // mGoogleMap.setMyLocationEnabled( true );
//        mGoogleMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
//            @Override
//            public void onMyLocationChange(Location location) {
//                CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 18);
//                mGoogleMap.animateCamera(cu);
//            }
//        });
        //mGoogleMap.setBearing(mLastHeading);
        mGoogleMap.getUiSettings().setZoomControlsEnabled(false);
    }


//    GoogleMap.CancelableCallback simpleAnimationCancelableCallback =
//            new GoogleMap.CancelableCallback(){
//
//                @Override
//                public void onCancel() {
//                }
//
//                @Override
//                public void onFinish() {
//
//                    if(++currentPt < markers.size()){
//
//                        CameraPosition cameraPosition =
//                                new CameraPosition.Builder()
//                                        .target(targetLatLng)
//                                        .tilt(currentPt<markers.size()-1 ? 90 : 0)
//                                        //.bearing((float)heading)
//                                        .zoom(mGoogleMap.getCameraPosition().zoom)
//                                        .build();
//
//
//                        mGoogleMap.animateCamera(
//                                CameraUpdateFactory.newCameraPosition(cameraPosition),
//                                3000,
//                                simpleAnimationCancelableCallback);
//
//                        highLightMarker(currentPt);
//
//                    }
//                }
//            };


//    public  class LOGINUSER extends AsyncTask<Void, Void, String> {
//
//        String  email;
//        String   password;
//
//
//        public LOGINUSER(String fName,String  surname){
//            this. email = fName ;
//            this.password = surname;
//
//        }
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            //pDialog = ProgressDialog.show(RegistrationActivity.this, "", "Loading. Please wait...", true);
//        }
//
//        @Override
//        protected String doInBackground(Void... params) {
//            String result  =  null;
//            Log.i(Constants.LOG_TAG, "<<<<STARTING LOGIN>>>>");
//            try{
//
//                //result  = setPreregister(this.MobileNumber,this.Countryname,this.Email); //  this.MobileNumber,this.Email);
//
//                HttpClient client = new DefaultHttpClient();
//                HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000); //Timeout Limit
//                HttpResponse response = null;
//                JSONObject json = new JSONObject();
//
//
//                HttpPost post = new HttpPost("http://www.qothooservice.com/token");
//                json.put("username", this.email.trim());//"08028187457");
//                json.put("password", this.password.trim());
//                json.put("grant_type", "password");
//
//                StringEntity se = new StringEntity( json.toString());
//
//                se.setContentType("application/json");
//                post.addHeader("Content-Type", "application/x-www-form-urlencoded") ;
//                //post.addHeader("Authorization",  GetAPIKeys(this.mobile).trim());  //"Bearer YjYwZDIxZjRmMGE5YzA0NDExOTIyYmE0YTAxYjI4OWU=") ;
//
//                Log.i("json.toString():: ","Bearer YjYwZDIxZjRmMGE5YzA0NDExOTIyYmE0YTAxYjI4OWU=");
//
//                post.setEntity(se);
//                response = client.execute(post);
//
//            /*Checking response */
//                if(response!=null){
//                    // HttpEntity httpEntity = response.getEntity(); //Get the data in the entity
//                    result = EntityUtils.toString(response.getEntity());
//                    Log.i("result++:",result);
//                }
//
//            }catch(Exception exp){
//                exp.printStackTrace();
//                result =  Util.errorCode(Constants.ERROR_CODE, Constants.DOING_BACKGROUND_SERVICE_RESULT);
//            }
//            System.out.println("background " + result);
//            return result;
//        }
//        @Override
//        protected void onPostExecute(final String resp) {
//            //{"message":"Failed app validation"}{"message":"ok"}
//            //pDialog.dismiss();
//            String respond = null;
//            try {
//                JSONObject result = new JSONObject(resp);
//                respond = result.getString("message");
//                if(respond.contains("Ok")){
//
//
//
//                }else{
//                    Toast.makeText(getActivity(),respond,Toast.LENGTH_LONG).show();
//                    //Dialogs.shoeMessage (getApplicationContext(),respond,"Qootho");
//
//                }
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//
//        }
//        @Override
//        protected void onCancelled() {
//
//        }
//    }


}


