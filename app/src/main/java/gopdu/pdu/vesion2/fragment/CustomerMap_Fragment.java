package gopdu.pdu.vesion2.fragment;


import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewpager.widget.ViewPager;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
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
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.compat.AutocompleteFilter;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import gopdu.pdu.vesion2.Common;
import gopdu.pdu.vesion2.GoPDUApplication;
import gopdu.pdu.vesion2.IOnBackPressed;
import gopdu.pdu.vesion2.R;
import gopdu.pdu.vesion2.adapter.ItemLocationAdapter;
import gopdu.pdu.vesion2.adapter.ItemServiceAdapter;
import gopdu.pdu.vesion2.databinding.FragmentCustomerMapBinding;
import gopdu.pdu.vesion2.network.ListServiceRespon;
import gopdu.pdu.vesion2.object.Service;
import gopdu.pdu.vesion2.presenter.PresenterCustomerMapFragment;
import gopdu.pdu.vesion2.service.APIService;
import gopdu.pdu.vesion2.service.DataService;
import gopdu.pdu.vesion2.view.ViewCustomerMapListener;
import gopdu.pdu.vesion2.viewmodel.ListServiceViewModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomerMap_Fragment extends Fragment implements ViewCustomerMapListener, OnMapReadyCallback, GoogleMap.OnCameraMoveStartedListener, GoogleMap.OnCameraIdleListener, LocationListener, GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks, IOnBackPressed {

    private GoogleMap mMap;
    private FragmentCustomerMapBinding binding;
    private GoogleApiClient mGoogleApiClient;
    //request old location
    private Location mLastLocation;
    private LocationRequest mLocationRequest;

    //Adapter location search view
    private ItemLocationAdapter locationAdapter;

    //Latlng destination and pickup location
    private LatLng destinationLng;
    private LatLng pickUpLng;

    //Object location
    private gopdu.pdu.vesion2.object.Location locationDes;
    private gopdu.pdu.vesion2.object.Location locationPickUp;

    //Check custom pickup location
    private boolean customPickup = false;

    //View search location
    private View bottomSheet;
    private BottomSheetBehavior<View> behavior;

    //Set up adapter location search
    private AutocompleteFilter autocompleteFilte;

    //Service
    private DataService dataService;
    private ArrayList<Service> services;
    private ItemServiceAdapter serviceAdapter;
    private float distance = 0;
    private int price;
    private ListServiceViewModel serviceModel;

    //Marker
    private Marker pickUpMarker;
    private Marker destinationMarker;

    //Firebase Account
    private FirebaseAuth mAuth;
    private String userID;

    private boolean requestBoolena = false;
    private String statusTrip ;
    private boolean driverFound = false;

    //Presenter
    PresenterCustomerMapFragment presenter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_customer_map, container, false);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map));
        mapFragment.getMapAsync(this);
        setRetainInstance(true);
        init();
        setUpOnClick();
        setUpServiceView();

        return binding.getRoot();
    }

    private void setUpServiceView() {

        services = new ArrayList<>();
        serviceAdapter = new ItemServiceAdapter(services);
        binding.contentInfomation.viewPagerService.setAdapter(serviceAdapter);
        serviceModel.getService().observe(this, new Observer<ListServiceRespon>() {
            @Override
            public void onChanged(ListServiceRespon listServiceRespon) {
                if (listServiceRespon.getData() != null && listServiceRespon.getData().size() > 0) {
                    services.addAll(listServiceRespon.getData());
                    serviceAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    private void init() {

        locationDes = new gopdu.pdu.vesion2.object.Location();
        locationPickUp = new gopdu.pdu.vesion2.object.Location();
        presenter = new PresenterCustomerMapFragment(this);
        serviceModel = ViewModelProviders.of(this).get(ListServiceViewModel.class);
        dataService = APIService.getService();
        //setup list search adapter=
        autocompleteFilte = new AutocompleteFilter.Builder()
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_NONE)
                .setCountry(getString(R.string.country))
                .build();

        //setup botomsheet
        bottomSheet = binding.getRoot().findViewById(binding.contentSearch.bottomSheetDes.getId());
        behavior = BottomSheetBehavior.from(bottomSheet);
        binding.contentPickup.cdlPickupme.setVisibility(View.GONE);
        customPickup = false;

        //Google api set up
        buildGoogleAPiClient();
        if (!mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
        }
        locationPickUp = new gopdu.pdu.vesion2.object.Location();

        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getUid();

    }

    private void setUpOnClick() {
        binding.contentSearch.etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                presenter.reciverSearchOnClick(actionId, mGoogleApiClient);
                return false;
            }
        });

        binding.contentSearch.etSearch.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                setStateBottomSheet(getString(R.string.up));
            }
        });

        binding.contentSearch.etSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setStateBottomSheet(getString(R.string.up));
            }
        });

        binding.contentPickup.btnSearchPickup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setUpSearchPickup();
                customPickup = true;
                binding.contentPickup.cdlPickupme.setVisibility(View.GONE);
            }
        });

        binding.contentPickup.btnAcvite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickUpAdress();

            }
        });

        binding.contentInfomation.viewPagerService.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Service service = services.get(position);
                getPrice(service.getPrice());

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        //Call go travel
        binding.contentInfomation.btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endRide();
            }
        });


    }

    private void pickUpAdress() {
        binding.contentPickup.cdlPickupme.setVisibility(View.GONE);
        binding.contentInfomation.cdlInfomation.setVisibility(View.VISIBLE);
        binding.contentInfomation.tvNamePickupInfo.setText(locationPickUp.getNameLocation());
        binding.contentInfomation.tvNameDestinationInfo.setText(locationDes.getNameLocation());
        destinationLng = new LatLng(locationDes.getLat(), locationDes.getLogt());
        pickUpLng = new LatLng(locationPickUp.getLat(), locationPickUp.getLogt());
        distance = Common.getDistance(destinationLng, pickUpLng) / 1000;
        binding.contentInfomation.tvDistanceInfo.setText(getString(R.string.distance, distance));
        getPrice(services.get(0).getPrice());
        customPickup = false;
    }

    //show price for travel
    private void getPrice(int price) {
        int cash = Math.round((distance*price)/1000) *1000;
        if(cash==0){
            this.price =price;
        }else {
            this.price = cash;
        }
        binding.contentInfomation.tvPriceInfo.setText(getString(R.string.cash, this.price));
    }

    //If wan't custom pickup location clear history search
    private void setUpSearchPickup() {
        binding.contentSearch.tvTitle.setText(getString(R.string.wherePickUp));
        binding.contentSearch.etSearch.setText(locationPickUp.getNameLocation());
        binding.contentSearch.etSearch.setFocusable(true);
        setStateBottomSheet(getString(R.string.up));
        locationAdapter.setPickupLng(new LatLng(locationDes.getLat(), locationDes.getLogt()));
        locationAdapter.clearList();
        locationAdapter.notifyDataSetChanged();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        mMap.setOnCameraMoveStartedListener(this);
        mMap.setOnCameraIdleListener(this);
        mMap.getUiSettings().setRotateGesturesEnabled(false);
        mMap.setMyLocationEnabled(true);
    }

    // Create google API client
    protected synchronized void buildGoogleAPiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                .addOnConnectionFailedListener(this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();
    }


    //Set up adapter search locationDes
    private void setUpListLocation() {

        locationAdapter = new ItemLocationAdapter(mGoogleApiClient,
                Common.LAT_LNG_BOUNDS, autocompleteFilte, new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()));

        binding.contentSearch.rclDes.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        binding.contentSearch.rclDes.setAdapter(locationAdapter);

        //Item onclick
        locationAdapter.setOnItemClickedListener(new ItemLocationAdapter.OnItemClickedListener() {
            @Override
            public void onItemClick(int postion, View v) {
                presenter.reciverLocationOnclick(customPickup, postion);
                searchOnClick(postion);
            }
        });


    }

    private void searchOnClick(int postion) {

    }

    private void searchLocation(String keySearch) {
        locationAdapter.getFilter().filter(keySearch);
    }


    @Override
    public void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onLocationChanged(Location location) {
        presenter.reciverGetDefaultMyloction(getActivity(), customPickup, locationDes, location);


    }

    @Override
    public void onCameraIdle() {

    }

    @Override
    public void onCameraMoveStarted(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(R.integer.requestLocation);
        mLocationRequest.setFastestInterval(R.integer.requestLocation);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    //Zoom map
    private void zoomTarget(double lat, double lng) {
        try {
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(lat, lng)).zoom(15).build();
            mMap.animateCamera(CameraUpdateFactory
                    .newCameraPosition(cameraPosition));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Marker addMarker(double lat, double lng, int idIcon, String title) {
        return mMap.addMarker(new MarkerOptions().position(new LatLng(lat, lng))
                .icon(BitmapDescriptorFactory
                        .fromResource(idIcon)).title(title));
    }

    //Setup state bottomsheet
    private void setStateBottomSheet(String state) {
        behavior.setPeekHeight(GoPDUApplication.getInstance().getResources().getDimensionPixelSize(R.dimen.peekHeightMenu));
        if (state == getString(R.string.up) ) {
            behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        } else if (state == getString(R.string.down)) {
            behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
    }

    private void setStateBottomSheet2(String state) {
        behavior.setPeekHeight(0);
        if (state == getString(R.string.up) && customPickup) {
            behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        } else if (state == getString(R.string.down)) {
            behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
    }


    // back press
    @Override
    public boolean onBackPressed() {
        presenter.reciverBackOnClick(customPickup, locationDes, distance);
        return true;
    }



    //Push data to firebase
    private void endRide() {
            requestBoolena = true;
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("customerRequest");
            GeoFire geoFire = new GeoFire(ref);
            geoFire.setLocation(userID, new GeoLocation(pickUpLng.latitude, pickUpLng.longitude));

    }


    @Override
    public void searchOnClick() {
        searchLocation(binding.contentSearch.etSearch.getText().toString().trim());
        Common.hideSoftInput(getActivity());
    }

    //get address from name address
    @Override
    public void getAddress(Address obj) {
        String nameDetail = obj.getAddressLine(0);
        String[] names = nameDetail.split(",");

        locationPickUp.setLogt(mLastLocation.getLongitude());
        locationPickUp.setLat(mLastLocation.getLatitude());

        locationPickUp.setNameLocation(names[0] + "," + names[1]);
        locationPickUp.setNameDetailLocation(nameDetail);

        binding.contentPickup.tvNamePickup.setText(locationPickUp.getNameLocation());
        binding.contentPickup.tvNamePickupDetail.setText(locationPickUp.getNameDetailLocation());
    }


    //On pickup location
    @Override
    public void onPickUp(int postion) {
        if (pickUpMarker != null) {
            pickUpMarker.remove();
        }
        setStateBottomSheet2(getString(R.string.down));
        binding.contentPickup.cdlPickupme.setVisibility(View.VISIBLE);
        locationPickUp = locationAdapter.getItem(postion);
        binding.contentPickup.tvNamePickup.setText(locationPickUp.getNameLocation());
        binding.contentPickup.tvNamePickupDetail.setText(locationPickUp.getNameDetailLocation());
        zoomTarget(locationPickUp.getLat(), locationPickUp.getLogt());
        pickUpMarker = addMarker(locationPickUp.getLat(), locationPickUp.getLogt(), R.mipmap.ic_pickup, getString(R.string.pickupHere));
        zoomTarget(locationPickUp.getLat(), locationPickUp.getLogt());
    }

    @Override
    public void onDestination(int postion) {
        if (destinationMarker != null) {
            destinationMarker.remove();
        }
        setStateBottomSheet2(getString(R.string.down));
        binding.contentPickup.cdlPickupme.setVisibility(View.VISIBLE);
        locationDes = locationAdapter.getItem(postion);
        presenter.reciverGetAddress(mLastLocation.getLatitude(), mLastLocation.getLongitude());
        destinationMarker = addMarker(locationDes.getLat(), locationDes.getLogt(), R.mipmap.ic_pickup, getString(R.string.destination));
        zoomTarget(locationDes.getLat(), locationDes.getLogt());
    }

    @Override
    public void getDefaultMylocation(Location location) {
        zoomTarget(location.getLatitude(), location.getLongitude());
        zoomTarget(location.getLatitude(), location.getLongitude());
        pickUpMarker = addMarker(location.getLatitude(), location.getLongitude(), R.mipmap.ic_pickup, getString(R.string.pickupHere));
        mLastLocation = location;
        if(locationAdapter==null){
            setUpListLocation();
        }

    }

    @Override
    public void backUpFinishApp() {
        getActivity().finish();
    }

    @Override
    public void backToPickUpFromInfomation() {
        distance = 0;
        setStateBottomSheet2(getString(R.string.down));
        binding.contentPickup.cdlPickupme.setVisibility(View.VISIBLE);
        binding.contentInfomation.cdlInfomation.setVisibility(View.GONE);
        customPickup = false;
    }

    @Override
    public void backToPickUpFromCustomPickUp() {
        setStateBottomSheet2(getString(R.string.down));
        binding.contentPickup.cdlPickupme.setVisibility(View.VISIBLE);
        customPickup = false;
    }

    @Override
    public void backToDestination() {
        setStateBottomSheet(getString(R.string.down));
        locationDes = null;
        binding.contentSearch.etSearch.setText("");
        binding.contentPickup.cdlPickupme.setVisibility(View.GONE);
        locationAdapter.clearList();
        locationAdapter.notifyDataSetChanged();
    }


}
