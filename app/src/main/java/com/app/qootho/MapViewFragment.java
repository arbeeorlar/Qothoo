package com.app.qootho;


import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.provider.Settings;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.qootho.Adapters.PlaceAPIAutocompleteAdapter;
import com.app.qootho.Model.AvailableDriver;
import com.app.qootho.Model.PassengerAccount;
import com.app.qootho.Model.RiderType;
import com.app.qootho.Model.TokenObject;
import com.app.qootho.Model.UserAccount;
import com.app.qootho.Service.FetchAddressIntentService;
import com.app.qootho.Utilities.Connections;
import com.app.qootho.Utilities.Constants;
import com.app.qootho.Utilities.QoothoDB;
import com.app.qootho.Utilities.SessionManager;
import com.app.qootho.Utilities.Util;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

import info.hoang8f.android.segmented.SegmentedGroup;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

//import butterknife.InjectView;


//implements MyDialogFragmentListener
public class MapViewFragment extends Fragment implements OnMapReadyCallback,
        GoogleMap.OnCameraMoveStartedListener,
        GoogleMap.OnCameraMoveListener,
        GoogleMap.OnCameraMoveCanceledListener,
        GoogleMap.OnCameraIdleListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener, RiderAccountBottomDialogFragment.MyDialogFragmentListener {

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String LOG_TAG = "MyActivity";
    private final int[] MAP_TYPES = {GoogleMap.MAP_TYPE_SATELLITE,
            GoogleMap.MAP_TYPE_NORMAL,
            GoogleMap.MAP_TYPE_HYBRID,
            GoogleMap.MAP_TYPE_TERRAIN,
            GoogleMap.MAP_TYPE_NONE};
    public PlaceAPIAutocompleteAdapter mAdapter;
    public PlaceAPIAutocompleteAdapter mAdapter2;
    protected String mAddressOutput;
    protected String mAreaOutput;
    protected String mCityOutput;
    protected String mStateOutput;
    protected LatLng start;
    protected LatLng end;
    protected LatLng mCenterLatLong;
    protected String startAddress;
    protected String endAddress;
    protected GoogleApiClient mGoogleApiClient;
    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 101;
    //    @InjectView(R.id.start)
    TextView starting;
    //    @InjectView(R.id.destination)
    TextView destination;
    //    @InjectView(R.id.send)
    ImageView send;
    //private static final int[] COLORS = new int[]{R.color.primary_dark,R.color.primary,R.color.primary_light,R.color.accent,R.color.primary_dark_material_light};
    int PLACE_AUTOCOMPLETE_REQUEST_CODE_TO = 1;
    int PLACE_AUTOCOMPLETE_REQUEST_CODE_FROM = 2;
    RadioButton rdbClassic;
    RadioButton rdbElegant;
    //    TextView txtScheduledTrip;
//    TextView txtWork;
//    TextView txtHome;
    MapView mMapView;
    GoogleMap mGoogleMap;
    SupportMapFragment mapFrag;
    // LocationRequest mLocationRequest;
    // GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    //private Location mCurrentLocation;
    Marker mCurrLocationMarker;
    RiderAccountBottomDialogFragment myBottomSheet;
    String[] permissionsRequired = new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION};
    int rideType = 0;
    String riderName;
    QoothoDB db = null;
    SessionManager session = null;
    SharedPreferences installPref;
    String username;
    UserAccount user;
    PassengerAccount account;
    ArrayList<PassengerAccount> _account;
    ArrayList<RiderType> _rider;
    RiderType rider;
    TokenObject token;
    ArrayList<AvailableDriver> Driver;
    String accountName = "";
    int accountID = 0;
    ImageButton btnWork, btnHome, btnScheduleTrip;
    private AddressResultReceiver mResultReceiver;
    // private GoogleMap mMap;
    // private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    // private PlaceAutoCompleteAdapter mAdapter;
    private ProgressDialog progressDialog;
    private List<Polyline> polylines;
    private AutoCompleteTextView mAutocompleteView;
    private PolylineOptions currPolylineOptions;
    private boolean isCanceled = false;


//    public void showInputBox(final Activity activity, final ArrayList<PassengerAccount> accounts){
//        final BottomSheetDialog dialog =new BottomSheetDialog(activity);
//        dialog.setContentView(R.layout.bottom_layout);
//
//         ListView mapListView = (ListView) dialog.findViewById(R.id.list_account);
//
//
//        mapListView.setAdapter(new UserAccountListViewCustomAdapter(getActivity(), accounts));
//
//        mapListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                                               @Override
//                                               public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//
//                                                   Toast.makeText(getActivity(),"ABCDEF",Toast.LENGTH_LONG).show();
//                                                   dialog.dismiss();
//                                               }
//                                           }
//        );
////        // This listener's onShow is fired when the dialog is shown
////        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
////            @Override
////            public void onShow(DialogInterface dialog) {
////
////                // In a previous life I used this method to get handles to the positive and negative buttons
////                // of a dialog in order to change their Typeface. Good ol' days.
////
////                BottomSheetDialog d = (BottomSheetDialog) dialog;
////
////                // This is gotten directly from the source of BottomSheetDialog
////                // in the wrapInBottomSheet() method
////                FrameLayout bottomSheet = (FrameLayout) d.findViewById(android.support.design.R.id.design_bottom_sheet);
////
////                // Right here!
////                BottomSheetBehavior.from(bottomSheet)
////                        .setState(BottomSheetBehavior.STATE_EXPANDED);
////            }
////        });
//
////        mapListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
////            @Override
////            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
////
////                RadioButton txtRd = (RadioButton) view.findViewById(R.id.txtRadioButton);
////
////                txtRd.setOnClickListener(new View.OnClickListener() {
////                    @Override
////                    public void onClick(View v) {
////                        accounts.get(position).setChecked(true);
////                        System.out.println("i am here  ---- - ");
////                        dialog.cancel();
////                    }
////                });
////
////
////
////            }
////        });
//
//                dialog.show();
//    }
    /**
     * Callback for results from a Places Geo Data API query that shows the first place result in
     * the details view on screen.
     */
    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback
            = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                // Request did not complete successfully
                Log.e("Place", "Place query did not complete. Error: " + places.getStatus().toString());
                places.release();
                return;
            }
            // Get the Place object from the buffer.
            final Place place = places.get(0);
            // Format details of the place for display and show it in a TextView.
            // starting.setText(place.getName());

//            // Display the third party attributions if set.
//            final CharSequence thirdPartyAttribution = places.getAttributions();
//            if (thirdPartyAttribution == null) {
//                mPlaceDetailsAttribution.setVisibility(View.GONE);
//            } else {
//                mPlaceDetailsAttribution.setVisibility(View.VISIBLE);
//                mPlaceDetailsAttribution.setText(Html.fromHtml(thirdPartyAttribution.toString()));
//            }

            Log.i("Place::", "Place details received: " + place.getName());

            places.release();
        }
    };
    /**
     * Listener that handles selections from suggestions from the AutoCompleteTextView that
     * displays Place suggestions.
     * Gets the place id of the selected item and issues a request to the Places Geo Data API
     * to retrieve more details about the place.
     *
     * @see com.google.android.gms.location.places.GeoDataApi#getPlaceById(com.google.android.gms.common.api.GoogleApiClient,
     * String...)
     */
    private AdapterView.OnItemClickListener mAutocompleteClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            /*
             Retrieve the place ID of the selected item from the Adapter.
             The adapter stores each Place suggestion in a AutocompletePrediction from which we
             read the place ID and title.
              */
            final AutocompletePrediction item = mAdapter.getItem(position);
            final String placeId = item.getPlaceId();
            final CharSequence primaryText = item.getPrimaryText(null);

            Log.i("TAG", "Autocomplete item selected: " + primaryText);


            /*
             Issue a request to the Places Geo Data API to retrieve a Place object with additional
             details about the place.
              */
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);

            Toast.makeText(getActivity(), "Clicked: " + primaryText,
                    Toast.LENGTH_SHORT).show();
            Log.i("TAG", "Called getPlaceById to get Place details for " + placeId);
        }
    };
    /**
     * Callback for results from a Places Geo Data API query that shows the first place result in
     * the details view on screen.
     */
    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback2
            = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                // Request did not complete successfully
                Log.e("Place", "Place query did not complete. Error: " + places.getStatus().toString());
                places.release();
                return;
            }
            // Get the Place object from the buffer.
            final Place place = places.get(0);

            // Format details of the place for display and show it in a TextView.
            //  destination.setText(place.getName());

//            // Display the third party attributions if set.
//            final CharSequence thirdPartyAttribution = places.getAttributions();
//            if (thirdPartyAttribution == null) {
//                mPlaceDetailsAttribution.setVisibility(View.GONE);
//            } else {
//                mPlaceDetailsAttribution.setVisibility(View.VISIBLE);
//                mPlaceDetailsAttribution.setText(Html.fromHtml(thirdPartyAttribution.toString()));
//            }

            Log.i("Place::", "Place details received: " + place.getName());

            places.release();
        }
    };
    private AdapterView.OnItemClickListener mAutocompleteClickListener2
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            /*
             Retrieve the place ID of the selected item from the Adapter.
             The adapter stores each Place suggestion in a AutocompletePrediction from which we
             read the place ID and title.
              */
            final AutocompletePrediction item = mAdapter.getItem(position);
            final String placeId = item.getPlaceId();
            final CharSequence primaryText = item.getPrimaryText(null);

            Log.i("TAG", "Autocomplete item selected: " + primaryText);

            /*
             Issue a request to the Places Geo Data API to retrieve a Place object with additional
             details about the place.
              */
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback2);

            Toast.makeText(getActivity(), "Clicked: " + primaryText,
                    Toast.LENGTH_SHORT).show();
            Log.i("TAG", "Called getPlaceById to get Place details for " + placeId);
        }
    };

    public CameraPosition setWorkCamera(LatLng latlng, String address) {

        if (latlng != null) {
            MarkerOptions markerOptions = new MarkerOptions();
            CameraPosition camera = new CameraPosition.Builder().target(latlng)
                    .zoom(15.5f)
                    .bearing(0)
                    .tilt(25)
                    .build();

            return camera;
        }
        return null;

    }

    public CameraPosition setHomeCamera(LatLng latlng, String Address) {

        if (latlng != null) {


            CameraPosition camera = new CameraPosition.Builder().target(latlng)
                    .zoom(15.5f)
                    .bearing(0)
                    .tilt(25)
                    .build();

//       mGoogleMap.animateCamera(CameraUpdateFactory
//               .newCameraPosition(camera), null);
//
//       markerOptions.position(latlng);
//       markerOptions.title(Address);
//       markerOptions.draggable(true).visible(true);
//       markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.icons_home_page));
//      // mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 15));
//       mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
//       mGoogleMap.addMarker(markerOptions);


            return camera;

        }

        return null;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_book_ride, container, false);
        setHasOptionsMenu(true);
        mMapView = (MapView) rootView.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume(); // needed to get the map to display immediately


        db = new QoothoDB(getActivity());
        session = new SessionManager(getActivity());
        installPref = getActivity().getSharedPreferences(SessionManager.PREF_NAME, SessionManager.PRIVATE_MODE);
        username = installPref.getString(SessionManager.KEY_USERNAME, null);
        user = new UserAccount();
        account = new PassengerAccount();
        _account = new ArrayList<>();
        _rider = new ArrayList<>();
        rider = new RiderType();
        _account = db.GetPassengerAccount(username);
        _rider = db.GetRiderType(username);
        token = new TokenObject();
        user = db.getUserProfile(username);
        token = db.getTokenByUsername(username);
        Driver = new ArrayList<>();


        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());


            View locationButton = ((View) mMapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
            RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
            // position on right bottom
            rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 0);
            rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);


        } catch (Exception e) {
            e.printStackTrace();
        }
        mMapView.getMapAsync(this);


        System.out.println("Token::: " + token.getToken());
//        txtHome = (TextView) rootView.findViewById(R.id.txtHome);
//        txtWork = (TextView) rootView.findViewById(R.id.txtWork);
//        txtScheduledTrip = (TextView) rootView.findViewById(R.id.txtScheduledTrip);

        btnHome = (ImageButton) rootView.findViewById(R.id.btnHome);
        btnWork = (ImageButton) rootView.findViewById(R.id.btnWork);
        btnScheduleTrip = (ImageButton) rootView.findViewById(R.id.btnScheduleFuture);

        starting = (TextView) rootView.findViewById(R.id.start);
        destination = (TextView) rootView.findViewById(R.id.destination);


        System.out.println("getWorkAddress:: " + user.getWorkAddress() + "getHomeAddress:: " + user.getHomeAddress());

//        txtWork.setText(user.getWorkAddress().substring(0,12) + "...");
//        txtHome.setText(user.getHomeAddress().substring(0,12) + "...");


        SegmentedGroup segmented2 = (SegmentedGroup) rootView.findViewById(R.id.segmented2);
        segmented2.setOrientation(LinearLayout.HORIZONTAL);
        addButton(segmented2, _rider);

        try {
            rideType = _rider.get(0).getId();
            riderName = _rider.get(0).getRideTypeName();
            accountName = _account.get(0).getInstitutionName();
            accountID = _account.get(0).getInstitutionID();
        } catch (Exception ex) {
            ex.printStackTrace();
        }


        segmented2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                // Toast.makeText(getActivity(), group.getChildCount(), Toast.LENGTH_SHORT).show();
                int checkedRadioButtonId = group.getCheckedRadioButtonId();
                RadioButton radioBtn = (RadioButton) group.findViewById(checkedRadioButtonId);
                //System.out.println("checkedRadioButtonId:: " + checkedRadioButtonId);
                //Toast.makeText(getContext(), radioBtn.getText(), Toast.LENGTH_SHORT).show();
                rideType = checkedRadioButtonId;
                riderName = radioBtn.getText().toString();
            }
        });

        starting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    Intent intent =
                            new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                                    .build(getActivity());
                    startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE_TO);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }

            }
        });
        destination.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    Intent intent =
                            new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                                    .build(getActivity());
                    startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE_FROM);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }

            }
        });
        /*
        These text watchers set the start and end points to null because once there's
        * a change after a value has been selected from the dropdown
        * then the value has to reselected from dropdown to get
        * the correct location.
        * */
        starting.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int startNum, int before, int count) {
                if (start != null) {
                    start = null;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        destination.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


                if (end != null) {
                    end = null;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        mResultReceiver = new AddressResultReceiver(new Handler());

        if (checkPlayServices()) {
            // If this check succeeds, proceed with normal processing.
            // Otherwise, prompt user to get valid Play Services APK.
            if (!Util.isLocationEnabled(getActivity())) {
                // notify user
                android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(getActivity());
                dialog.setMessage("Location not enabled!");
                dialog.setPositiveButton("Open location settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(myIntent);
                    }
                });
                dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        // TODO Auto-generated method stub

                    }
                });
                dialog.show();
            }
            buildGoogleApiClient();
        } else {
            Toast.makeText(getActivity(), "Location not supported in this device", Toast.LENGTH_SHORT).show();
        }


        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user.getHomeLat() != null || user.getHomeLong() != null || user.getHomeLong() != 0.0 || user.getHomeLat() != 0.0) {
                    LatLng laty = new LatLng(user.getHomeLat(), user.getHomeLong());
                    CameraPosition camera = setHomeCamera(laty, user.getHomeAddress());
                    changeCamera(CameraUpdateFactory.newCameraPosition(camera), null, laty, user.getHomeAddress(), R.drawable.icons_home);

                } else {
                    Intent setHome = new Intent(getActivity(), HomeActivity.class);
                    startActivity(setHome);

                }
            }
        });


        btnWork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user.getWorkLat() != null || user.getWorkLong() != null || user.getWorkLong() != 0.0 || user.getWorkLat() != 0.0) {
                    LatLng laty = new LatLng(user.getWorkLat(), user.getWorkLong());
                    CameraPosition camera = setWorkCamera(laty, user.getWorkAddress());
                    changeCamera(CameraUpdateFactory.newCameraPosition(camera), null, laty, user.getWorkAddress(), R.drawable.icons_work);
                } else {
                    Intent setHome = new Intent(getActivity(), WorkActivity.class);
                    startActivity(setHome);

                }
            }
        });

        btnScheduleTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final BottomSheetDialogFragment myBottomSheet = ScheduleTrip.newInstance(starting.getText().toString());
                myBottomSheet.show(getActivity().getSupportFragmentManager(), myBottomSheet.getTag());

            }
        });


        return rootView;
    }

    private void changeCamera(CameraUpdate update, GoogleMap.CancelableCallback callback, LatLng latlng, String Address, int drawable) {
        MarkerOptions markerOptions = new MarkerOptions();
        mGoogleMap.animateCamera(update, callback);
        markerOptions.position(latlng);
        markerOptions.title(Address);
        markerOptions.draggable(true).visible(true);
        markerOptions.icon(BitmapDescriptorFactory.fromResource(drawable));
        //  mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 15));
        mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
        mGoogleMap.addMarker(markerOptions);

    }

    private void addButton(SegmentedGroup group, ArrayList<RiderType> riderTypes) {

        for (int i = 0; i < riderTypes.size(); i++) {

            RadioButton radioButton = (RadioButton) getActivity().getLayoutInflater().inflate(R.layout.radio_button_item, null);
            radioButton.setText(riderTypes.get(i).getRideTypeName());
            radioButton.setId(riderTypes.get(i).getId());
            RadioGroup.LayoutParams rprms = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.MATCH_PARENT, RadioGroup.LayoutParams.WRAP_CONTENT);
            rprms.weight = 1;
            rprms.gravity = Gravity.CENTER;
            radioButton.setLayoutParams(rprms);
            group.addView(radioButton, rprms);
            group.updateBackground();
        }


    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        try {
            buildGoogleApiClient();
            Log.i("Test", "Hi have created it  boss");
        } catch (Exception ex) {
            ex.toString();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.landing_page, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.clear();    //remove all items

        getActivity().getMenuInflater().inflate(R.menu.map_page, menu);
        setMenuVisibility(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.search_badge:
                // showInputBox(getActivity(),_account);
                // RiderAccountBottomDialogFragment myBottomSheet = RiderAccountBottomDialogFragment.newInstance("Ride With");
                // myBottomSheet.show(getChildFragmentManager(), myBottomSheet.getTag());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
        mMapView.getMapAsync(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
        //stop location updates when Activity is no longer active
        try {
            if (mGoogleApiClient != null) {
                LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mMapView.onStart();
        mGoogleApiClient.connect();
        //mMapView.getMapAsync(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        //mGoogleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        //  mGoogleMap.setMyLocationEnabled(true);
        // mGoogleMap.setMy
        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getActivity(),
                    permissionsRequired[0])
                    == PackageManager.PERMISSION_GRANTED) {
                //Location Permission already granted
                buildGoogleApiClient();
                mGoogleMap.setMyLocationEnabled(true);
                mGoogleMap.setMapType(MAP_TYPES[1]);
                // mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(startDirection));
                mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(14));
                mGoogleMap.setBuildingsEnabled(true);
                mGoogleMap.setOnCameraIdleListener(this);
                mGoogleMap.setOnCameraMoveStartedListener(this);
                mGoogleMap.setOnCameraMoveListener(this);
                mGoogleMap.setOnCameraMoveCanceledListener(this);

                // We will provide our own zoom controls.
                mGoogleMap.getUiSettings().setZoomControlsEnabled(false);
                mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);


                //initCamera(mLastLocation);
            } else {
                //Request Location Permission
                checkLocationPermission();
            }
        } else {
            buildGoogleApiClient();
            mGoogleMap.setMyLocationEnabled(true);
            mGoogleMap.setMapType(MAP_TYPES[1]);
            mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(14));
            //mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
            //initCamera(mLastLocation);
            mGoogleMap.setBuildingsEnabled(true);
            mGoogleMap.setOnCameraIdleListener(this);
            mGoogleMap.setOnCameraMoveStartedListener(this);
            mGoogleMap.setOnCameraMoveListener(this);
            mGoogleMap.setOnCameraMoveCanceledListener(this);

            // We will provide our own zoom controls.
            mGoogleMap.getUiSettings().setZoomControlsEnabled(false);
            mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
        }

        try {
            if (Util.myClickHandler(getActivity())) {
                new GETAVAILABLEDRIVER(token.getToken(), mLastLocation.getLatitude(), mLastLocation.getLongitude(), rideType + "").execute();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

//        mGoogleMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
//            @Override
//            public void onCameraChange(CameraPosition cameraPosition) {
//                Log.d("Camera postion change" + "", cameraPosition + "");
//                mCenterLatLong = cameraPosition.target;
//
//
//                mGoogleMap.clear();
//
//                try {
//
//                    Location mLocation = new Location("");// Location("");
//                    mLocation.setLatitude(mCenterLatLong.latitude);
//                    mLocation.setLongitude(mCenterLatLong.longitude);
//
//                    startIntentService(mLocation);
//
//                   // Toast.makeText(getActivity(),"Lat : " + mCenterLatLong.latitude + "," + "Long : " + mCenterLatLong.longitude,Toast.LENGTH_LONG).show();
//
//                 //   mLocationMarkerText.setText("Lat : " + start.latitude + "," + "Long : " + start.longitude);
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        });

    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
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
            if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
        try {
            if (Util.myClickHandler(getActivity())) {
                new GETAVAILABLEDRIVER(token.getToken(), mLastLocation.getLatitude(), mLastLocation.getLongitude(), rideType + "").execute();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        startIntentService(location);
    }

    protected Marker createMarker(double latitude, double longitude, String title, String snippet) {

        return mGoogleMap.addMarker(new MarkerOptions()
                .position(new LatLng(latitude, longitude))
                .anchor(0.5f, 0.5f)
                .title(title)
                .snippet(snippet)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.car_top_view_13)));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE_TO) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(getActivity(), data);
                Log.i("WEMAMOBILE", "Place: " + place.getName());

                starting.setText(place.getAddress());
                startAddress = starting.getText().toString();
                // txtSourceTrip.setText(place.getAddress().toString().substring(0,11)+" ...");
                start = place.getLatLng();
                Log.i("getAddress", place.getAddress() + "");

            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(getActivity(), data);
                // TODO: Handle the error.
                Log.i("WEMAMOBILE", status.getStatusMessage());

                Toast.makeText(getActivity(), status.getStatusMessage(), Toast.LENGTH_LONG).show();

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        } else if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE_FROM) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(getActivity(), data);
                Log.i("WEMAMOBILE", "Place: " + place.getName());

                destination.setText(place.getAddress());
                endAddress = destination.getText().toString();
                end = place.getLatLng();
                //  txtDestinationTrip.setText(place.getAddress().toString().substring(0,11)+" ...");
                Log.i("getAddress", place.getAddress() + "");
                if (start != null && end != null) {
                    Intent intent = new Intent(getActivity(), ViewTripActivity.class);
                    Bundle b = new Bundle();
                    System.out.println("_account " + _account.size());

                    b.putString("accountName", accountName);
                    b.putString("accountID", accountID + "");

                    if (!starting.getText().toString().equalsIgnoreCase(destination.getText().toString())) {

                        if (!(starting.getText().toString().isEmpty()) || !(TextUtils.isEmpty(starting.getText().toString()))) {
                            // if(Driver.size() > 0) {
//
                            System.out.println("Driver:: " + Driver.size());
                            b.putDouble("startLatitude", start.latitude);
                            b.putDouble("startLongitude", start.longitude);
                            b.putDouble("endLatitude", end.latitude);
                            b.putDouble("endLongititude", end.longitude);
                            b.putString("endAddress", endAddress);
                            b.putString("startAddress", startAddress);
                            b.putString("rideTypeName", riderName);
                            b.putString("rideTypeID", rideType + "");
                            intent.putExtras(b);

                            starting.setText("");
                            destination.setText("");
                            startActivity(intent);

//                    }else{
//
//                        Toast.makeText(getContext(), "Qothoo is not available in ur area...sorry", Toast.LENGTH_SHORT).show();
//                        return;
//
//                    }
                        } else {

                            Toast.makeText(getContext(), "Kindly select your source location", Toast.LENGTH_SHORT).show();

                        }
                    } else {

                        Toast.makeText(getContext(), "You pick the same destination and source location,kindly select another location.", Toast.LENGTH_SHORT).show();

                    }

                } else {
                    Toast.makeText(getContext(), "Oops, something went wrong", Toast.LENGTH_SHORT).show();
                    return;

                }


            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(getActivity(), data);
                // TODO: Handle the error.
                Log.i("WEMAMOBILE", status.getStatusMessage());
                Toast.makeText(getActivity(), status.getStatusMessage(), Toast.LENGTH_LONG).show();


            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    @Override
    public void onReturnValue(String foo) {
        System.out.println("foo - foo " + foo);
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
    public void onCameraMoveStarted(int reason) {
//        if (!isCanceled) {
//            mGoogleMap.clear();
//        }

        String reasonText = "UNKNOWN_REASON";
        //currPolylineOptions = new PolylineOptions().width(5);
        switch (reason) {
            case GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE:
                //currPolylineOptions.color(Color.BLUE);
                reasonText = "GESTURE";
                break;
            case GoogleMap.OnCameraMoveStartedListener.REASON_API_ANIMATION:
                //currPolylineOptions.color(Color.RED);
                reasonText = "API_ANIMATION";
                break;
            case GoogleMap.OnCameraMoveStartedListener.REASON_DEVELOPER_ANIMATION:
                //currPolylineOptions.color(Color.GREEN);
                reasonText = "DEVELOPER_ANIMATION";
                break;
        }
        Log.i("mGoogleMap", "onCameraMoveStarted(" + reasonText + ")");
        //  addCameraTargetToPath();
    }

    @Override
    public void onCameraMove() {
        // When the camera is moving, add its target to the current path we'll draw on the map.
        // if (currPolylineOptions != null) {
        addCameraTargetToPath();
        // }
        Log.i("mGoogleMap", "onCameraMove");
    }

    @Override
    public void onCameraMoveCanceled() {
        // When the camera stops moving, add its target to the current path, and draw it on the map.
        if (currPolylineOptions != null) {
            // addCameraTargetToPath();
            //  mGoogleMap.addPolyline(currPolylineOptions);
        }
        isCanceled = true;  // Set to clear the map when dragging starts again.
        currPolylineOptions = null;
        Log.i("mGoogleMap", "onCameraMoveCancelled");
    }

    @Override
    public void onCameraIdle() {
        if (currPolylineOptions != null) {

            //  mGoogleMap.addPolyline(currPolylineOptions);
        }
        currPolylineOptions = null;
        isCanceled = false;  // Set to *not* clear the map when dragging starts again.
        Log.i("mGoogleMap", "onCameraIdle");
    }

    private void addCameraTargetToPath() {
        mCenterLatLong = mGoogleMap.getCameraPosition().target;
        // currPolylineOptions.add(mCenterLatLong);
        // mGoogleMap.clear();
        try {

            System.out.println("mCenterLatLong:: " + mCenterLatLong.latitude + "mCenterLatLong:: " + mCenterLatLong.longitude);
            Location mLocation = new Location("");// Location("");
            mLocation.setLatitude(mCenterLatLong.latitude);
            mLocation.setLongitude(mCenterLatLong.longitude);
            startIntentService(mLocation);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity());
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, getActivity(),
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                //finish();
            }
            return false;
        }
        return true;
    }

//    private static Spanned formatPlaceDetails(Resources res, CharSequence name, String id,
//                                              CharSequence address, CharSequence phoneNumber, Uri websiteUri) {
//       // Log.e(TAG, res.getString(R.string.place_details, name, id, address, phoneNumber,
//             //   websiteUri));
//        return Html.fromHtml(res.getString(R.string.place_details, name, id, address, phoneNumber,
//                websiteUri));
//
//    }

    private void changeMap(Location location) {

        Log.d("TAG", "Reaching map" + mGoogleMap);


        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        // check if map is created successfully or not
        if (mGoogleMap != null) {
            mGoogleMap.getUiSettings().setZoomControlsEnabled(false);
            LatLng latLong;

            latLong = new LatLng(location.getLatitude(), location.getLongitude());
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(latLong).zoom(19f).tilt(70).build();
            mGoogleMap.setMyLocationEnabled(true);
            mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
            mGoogleMap.animateCamera(CameraUpdateFactory
                    .newCameraPosition(cameraPosition));

            // mLocationMarkerText.setText("Lat : " + location.getLatitude() + "," + "Long : " + location.getLongitude());

            startIntentService(location);


        } else {

            Toast.makeText(getActivity(),
                    "Sorry! unable to create maps", Toast.LENGTH_SHORT)
                    .show();
        }

    }

    /**
     * Updates the address in the UI.
     */
    protected void displayAddressOutput(double latPoint, double longPoint) {
        //  mLocationAddressTextView.setText(mAddressOutput);
        try {
            if (mAreaOutput != null)
                // mLocationText.setText(mAreaOutput+ "");
                // System.out.println("Lat : " + mAreaOutput + "," + "Long : " + mStateOutput);
                //  starting.setText(mStateOutput);
                startAddress = mStateOutput;
            start = new LatLng(latPoint, longPoint);
            // Toast.makeText(getActivity(),"Lat : " + mAreaOutput + "," + "Long : " + mStateOutput,Toast.LENGTH_LONG).show();
            //mLocationText.setText(mAreaOutput);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates an intent, adds location data to it as an extra, and starts the intent service for
     * fetching an address.
     */
    protected void startIntentService(Location mLocation) {
        // Create an intent for passing to the intent service responsible for fetching the address.
        Intent intent = new Intent(getActivity(), FetchAddressIntentService.class);
        // Pass the result receiver as an extra to the service.
        intent.putExtra(Util.LocationConstants.RECEIVER, mResultReceiver);
        // Pass the location data as an extra to the service.
        intent.putExtra(Util.LocationConstants.LOCATION_DATA_EXTRA, mLocation);
        // Start the service. If the service isn't already running, it is instantiated and started
        // (creating a process for it if needed); if it is running then it remains running. The
        // service kills itself automatically once all intents are processed.
        getActivity().startService(intent);
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
                //if(Util.myClickHandler(getContext())) {
                result = Connections.GETAVAILABLECAR(longPoint, latPoint, Token, rideType);
//                }else{
//
//                    result = Util.errorCode(Constants.INTERNET_CONNECTION_ERROR, "Internet coonection is not available, check your connection");
//                }

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


                }


            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        @Override
        protected void onCancelled() {

        }
    }

    class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        /**
         * Receives data sent from FetchAddressIntentService and updates the UI in MainActivity.
         */
        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            // Display the address string or an error message sent from the intent service.
            mAddressOutput = resultData.getString(Util.LocationConstants.RESULT_DATA_KEY);

            mAreaOutput = resultData.getString(Util.LocationConstants.LOCATION_DATA_AREA);

            mCityOutput = resultData.getString(Util.LocationConstants.LOCATION_DATA_CITY);
            mStateOutput = resultData.getString(Util.LocationConstants.LOCATION_DATA_STREET);
            // start = ;


            // Show a toast message if an address was found.
            if (resultCode == Util.LocationConstants.SUCCESS_RESULT) {
                //  showToast(getString(R.string.address_found));

                displayAddressOutput(resultData.getDouble("Latitude"), resultData.getDouble("Longitude"));

            }
        }
    }
}