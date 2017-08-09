package com.app.qootho.Adapters;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.app.qootho.Model.TripHistory;
import com.app.qootho.R;
import com.app.qootho.Utilities.DirectionJSONParser;
import com.app.qootho.iServices.iTripHistory;
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


public class PreviousTripAdapter extends RecyclerView.Adapter<PreviousTripAdapter.ViewHolder> {

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private static final String LOG_TAG = "MyActivity";
    private final List<TripHistory> mValues;
    private final int[] MAP_TYPES = {GoogleMap.MAP_TYPE_SATELLITE,
            GoogleMap.MAP_TYPE_NORMAL,
            GoogleMap.MAP_TYPE_HYBRID,
            GoogleMap.MAP_TYPE_TERRAIN,
            GoogleMap.MAP_TYPE_NONE};
    public TripHistory mItem;
    protected LatLng start;
    protected LatLng end;
    protected GoogleApiClient mGoogleApiClient;
    Bundle b;
    Location mLastLocation;
    MapView mMapView;
    GoogleMap mGoogleMap;
    String[] permissionsRequired = new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION};
    Marker mCurrLocationMarker;
    iTripHistory listner;
    //private final OnListFragmentInteractionListener mListener;
    private Activity context;
    private GoogleMap mMap;
    private LocationRequest mLocationRequest;

    public PreviousTripAdapter(List<TripHistory> items, Activity context, Bundle b, iTripHistory listner) {
        mValues = items;
        this.context = context;
        this.b = b;
        this.listner = listner;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.previous_trip, parent, false);


        //mMapView.onCreate(savedInstanceState);

        return new ViewHolder(view, b);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        //holder.mItem = mValues.get(position);
        //  holder.mIdView.setText(mValues.get(position).id);
        // holder.mContentView.setText(mValues.get(position).content);
        Log.i("position :: ", position + "");
        holder.txtTripAmount.setText(mValues.get(position).getBaseFare() + "");
        holder.txtTripCarModel.setText(mValues.get(position).getModelName());
        holder.txtTripDate.setText(mValues.get(position).getTripStartTime());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toast.makeText(context, position, Toast.LENGTH_SHORT).show();

                listner.onClick(mValues.get(position));

//                Intent intent  =  new Intent(context, PreviousTripDetail.class);
//                Bundle args = new Bundle();
//                args.putSerializable("help",(Serializable)mValues);
//                args.putInt("position",position);
//                intent.putExtra("BUNDLE",args);
//
//                context.startActivity(intent);

            }
        });


        holder.mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mGoogleMap = googleMap;
                mMap = googleMap;

                double destLatPoint = mValues.get(position).getActualDestinationLat() != null ? mValues.get(position).getActualDestinationLat() : 0.0;
                double destLongPoint = mValues.get(position).getActualDestinationLat() != null ? mValues.get(position).getActualDestinationLat() : 0.0;
                double pickUpLatPoint = mValues.get(position).getActualPickUpLat() != null ? mValues.get(position).getActualPickUpLat() : 0.0;
                double pickUpLongPoint = mValues.get(position).getActualPickUpLong() != null ? mValues.get(position).getActualPickUpLong() : 0.0;

                // Add a marker in Sydney and move the camera
                LatLng startDirection = new LatLng(pickUpLatPoint, pickUpLongPoint);
                LatLng endDirection = new LatLng(destLatPoint, destLongPoint);

                mMap.addMarker(new MarkerOptions().position(startDirection).title(mValues.get(position).getPickUpAddress()).icon(BitmapDescriptorFactory.fromResource(R.drawable.add_marker)));
                mMap.addMarker(new MarkerOptions().position(endDirection).title(mValues.get(position).getDropOffAddress()).icon(BitmapDescriptorFactory.fromResource(R.drawable.add_marker)));
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
                mMap.getUiSettings();

                mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {

                        // listner.onClick(mValues.get(position));
                    }
                });
            }
        });

    }

    @Override
    public int getItemCount() {
        return mValues.size();
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


        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();


            if (result.size() < 1) {
                //Toast.makeText(context, "No Points", Toast.LENGTH_SHORT).show();
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


    public class ViewHolder extends RecyclerView.ViewHolder

    {
        View mView;
        TextView txtTripDate;
        TextView txtTripAmount;
        TextView txtTripCarModel;
        MapView mMapView;


        public ViewHolder(View view, Bundle b) {
            super(view);
            mView = view;
            //  mIdView = (TextView) view.findViewById(R.id.id);
            // mContentView = (TextView) view.findViewById(R.id.content);
            txtTripDate = (TextView) view.findViewById(R.id.txtTripDate);
            txtTripAmount = (TextView) view.findViewById(R.id.txtTripAmount);
            txtTripCarModel = (TextView) view.findViewById(R.id.txtTripCarModel);
            mMapView = (MapView) view.findViewById(R.id.mapView);


            mMapView.onCreate(b);
            mMapView.onResume(); // needed to get the map to display immediately
            try {
                MapsInitializer.initialize(context.getApplicationContext());
            } catch (Exception e) {
                e.printStackTrace();
            }

//            txtContentText = (TextView) view.findViewById(R.id.txtFeedContent);
//            feedPix = (ImageView) view.findViewById(R.id.feedPix);
//            ImageBanner = (ImageView) view.findViewById(R.id.ImageContent);
//            txtDescription = (TextView) view.findViewById(R.id.txtDescription);
//


        }

        private void checkLocationPermission() {
            if (ContextCompat.checkSelfPermission(context, permissionsRequired[0])
                    != PackageManager.PERMISSION_GRANTED) {

                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(context, permissionsRequired[0])) {

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
                                    ActivityCompat.requestPermissions(context,
                                            new String[]{permissionsRequired[0]},
                                            MY_PERMISSIONS_REQUEST_LOCATION);
                                }
                            })
                            .create()
                            .show();


                } else {
                    // No explanation needed, we can request the permission.
                    ActivityCompat.requestPermissions(context,
                            new String[]{permissionsRequired[0]},
                            MY_PERMISSIONS_REQUEST_LOCATION);
                }
            }
        }

        private void initCamera(Location location) {

            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            MarkerOptions markerOptions = new MarkerOptions();
            CameraPosition position = CameraPosition.builder()
                    .target(latLng)
                    .zoom(10f)
                    .bearing(45)
                    .tilt(0.0f)
                    .build();


            mGoogleMap.animateCamera(CameraUpdateFactory
                    .newCameraPosition(position), null);

            markerOptions.position(latLng);
            markerOptions.title("Pick Up Point");
            markerOptions.draggable(true).visible(true);
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
            // mCurrLocationMarker = mGoogleMap.addMarker(markerOptions);
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
            mGoogleMap.setMapType(MAP_TYPES[1]);
            mGoogleMap.getUiSettings().setZoomControlsEnabled(false);
        }


    }


}
