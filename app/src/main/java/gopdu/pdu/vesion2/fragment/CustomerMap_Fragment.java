package gopdu.pdu.vesion2.fragment;


import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.compat.AutocompleteFilter;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import gopdu.pdu.vesion2.Common;
import gopdu.pdu.vesion2.GoPDUApplication;
import gopdu.pdu.vesion2.IOnBackPressed;
import gopdu.pdu.vesion2.R;
import gopdu.pdu.vesion2.adapter.ItemLocationAdapter;
import gopdu.pdu.vesion2.adapter.ItemServiceAdapter;
import gopdu.pdu.vesion2.databinding.FragmentCustomerMapBinding;
import gopdu.pdu.vesion2.object.Service;
import gopdu.pdu.vesion2.service.APIService;
import gopdu.pdu.vesion2.service.DataService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomerMap_Fragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnCameraMoveStartedListener, GoogleMap.OnCameraIdleListener, LocationListener, GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks, IOnBackPressed {

    private GoogleMap mMap;
    private FragmentCustomerMapBinding binding;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private LocationRequest mLocationRequest;
    private ItemLocationAdapter locationAdapter;
    private LatLng destinationLng;
    private LatLng pickUpLng;
    private gopdu.pdu.vesion2.object.Location locationDes;
    private gopdu.pdu.vesion2.object.Location locationPickUp;
    private boolean customPickup;
    private View bottomSheet;
    private BottomSheetBehavior<View> behavior;
    private  LatLngBounds LAT_LNG_BOUNDS;
    private AutocompleteFilter autocompleteFilte;
    private DataService dataService;
    private ArrayList<Service> services;
    private ItemServiceAdapter serviceAdapter;
    private float distance = 0;

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

        binding.getRoot().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                Log.d("BBB", "onKey: BBB");
                return false;
            }
        });
        return binding.getRoot();
    }

    private void setUpServiceView() {

        services = new ArrayList<>();
        serviceAdapter = new ItemServiceAdapter(services);
        binding.viewPagerService.setAdapter(serviceAdapter);
        retrofit2.Call<ArrayList<Service>> getService = dataService.getservice();
        getService.enqueue(new Callback<ArrayList<Service>>() {
            @Override
            public void onResponse(Call<ArrayList<Service>> call, Response<ArrayList<Service>> response) {
                if(response.body() != null && response.body().size()>0){
                    services.addAll(response.body());
                    serviceAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Service>> call, Throwable t) {
                Log.d("BBB", "onFailure: "+t.getMessage());
                Common.ShowToastShort(getString(R.string.checkConnect));
            }
        });
    }

    private void init() {

        dataService = APIService.getService();
        //setup list search adapter
        LAT_LNG_BOUNDS = new LatLngBounds(
                new LatLng(-40, -168), new LatLng(71, 136));
        autocompleteFilte = new AutocompleteFilter.Builder()
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_NONE)
                .setCountry("VN")
                .build();

        //setup botomsheet
        bottomSheet = binding.getRoot().findViewById(binding.bottomSheetDes.getId());
        behavior = BottomSheetBehavior.from(bottomSheet);
        binding.cdlPickupme.setVisibility(View.GONE);
        customPickup = false;

        //Google api set up
        buildGoogleAPiClient();
        if (!mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
        }
        locationPickUp = new gopdu.pdu.vesion2.object.Location();

    }

    private void setUpOnClick() {
        binding.etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE
                        || actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId ==EditorInfo.IME_ACTION_NEXT
                       ) {

                    if (mGoogleApiClient.isConnected()) {
                        searchLocation(binding.etSearch.getText().toString().trim());
                        Common.hideSoftInput(getActivity());
                    }

                }
                return false;
            }
        });

        binding.etSearch.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                setStateBottomSheet("up");
            }
        });

        binding.etSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setStateBottomSheet("up");
            }
        });

        binding.btnSearchPickup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setUpSearchPickup();
                customPickup = true;
                binding.cdlPickupme.setVisibility(View.GONE);
            }
        });

        binding.btnAcvite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.cdlPickupme.setVisibility(View.GONE);
                binding.cdlInfomation.setVisibility(View.VISIBLE);
                binding.tvNamePickupInfo.setText(locationPickUp.getNameLocation());
                binding.tvNameDestinationInfo.setText(locationDes.getNameLocation());
                destinationLng = new LatLng(locationDes.getLat(), locationDes.getLogt());
                pickUpLng = new LatLng(locationPickUp.getLat(), locationDes.getLogt());
                Log.d("BBB", "onClick: "+destinationLng+"/"+pickUpLng);
                distance = Common.getDistance(destinationLng, pickUpLng)/1000;
                binding.tvDistanceInfo.setText(getString(R.string.distance, distance));

            }
        });


    }

    //If wan't custom pickup location clear history search
    private void setUpSearchPickup() {

        binding.tvTitle.setText(getString(R.string.wherePickUp));
        binding.etSearch.setText(locationPickUp.getNameLocation());
        binding.etSearch.setFocusable(true);
        setStateBottomSheet("up");
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
                LAT_LNG_BOUNDS, autocompleteFilte,new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()));

        binding.rclDes.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        binding.rclDes.setAdapter(locationAdapter);

        //Item onclick
        locationAdapter.setOnItemClickedListener(new ItemLocationAdapter.OnItemClickedListener() {
            @Override
            public void onItemClick(int postion, View v) {
                if(customPickup==false){
                    setStateBottomSheet2("down");
                    binding.cdlPickupme.setVisibility(View.VISIBLE);
                    locationDes = locationAdapter.getItem(postion);
                    getAddress(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                }else {

                    setStateBottomSheet2("down");
                    binding.cdlPickupme.setVisibility(View.VISIBLE);
                    locationPickUp = locationAdapter.getItem(postion);
                    binding.tvNamePickup.setText(locationPickUp.getNameLocation());
                    binding.tvNamePickupDetail.setText(locationPickUp.getNameDetailLocation());
                    zoomTarget(locationPickUp.getLat(), locationPickUp.getLogt());


                }

            }
        });


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
        if (getActivity() != null && customPickup == false) {
            mLastLocation = location;
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            zoomTarget(latLng.latitude, latLng.longitude);
            zoomTarget(location.getLatitude(), location.getLongitude());
            addMarker(location.getLatitude(), location.getLongitude());
            if(locationAdapter == null){
                setUpListLocation();
            }
        }
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
        mLocationRequest.setInterval(3000);
        mLocationRequest.setFastestInterval(3000);
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

    private void addMarker(double lat, double lng) {
        try {
            mMap.clear();
           /* googleMap.addMarker(new MarkerOptions().position(new LatLng(lat, lng))
                    .icon(BitmapDescriptorFactory
                            .fromResource(R.drawable.ic_location_on_blue_24dp)));*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Setup state bottomsheet
    private void setStateBottomSheet(String state) {
        behavior.setPeekHeight(GoPDUApplication.getInstance().getResources().getDimensionPixelSize(R.dimen.peekHeightMenu));
        if (state == "up") {
            behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        } else if (state == "down") {
            behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
    }

    private void setStateBottomSheet2(String state){
        behavior.setPeekHeight(0);
        if (state == "up" && customPickup) {
            behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        } else if (state == "down") {
            behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
    }

    //All take name locationDes from lat and logt
    public void getAddress(double lat, double lng) {
        Geocoder geocoder = new Geocoder(
                getContext(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            Address obj = addresses.get(0);
            String nameDetail = obj.getAddressLine(0);
            String[] names = nameDetail.split(",");

            locationPickUp.setLogt(mLastLocation.getLongitude());
            locationPickUp.setLat(mLastLocation.getLatitude());

            locationPickUp.setNameLocation(names[0]+","+names[1]);
            locationPickUp.setNameDetailLocation(nameDetail);

            binding.tvNamePickup.setText(locationPickUp.getNameLocation());
            binding.tvNamePickupDetail.setText(locationPickUp.getNameDetailLocation());

            // Toast.makeText(this, "Address=>" + add,
            // Toast.LENGTH_SHORT).show();

            // TennisAppActivity.showDialog(add);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Toast.makeText(GoPDUApplication.getInstance(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onBackPressed() {
        if(locationDes.getNameLocation() != null && customPickup == true){
            setStateBottomSheet2("down");
            binding.cdlPickupme.setVisibility(View.VISIBLE);
            customPickup = false;
        }else if(locationDes.getNameLocation() != null && distance != 0){
            distance = 0;
            setStateBottomSheet2("down");
            binding.cdlPickupme.setVisibility(View.VISIBLE);
            binding.cdlInfomation.setVisibility(View.GONE);
            customPickup = false;
        } else  if(locationDes.getNameLocation() != null ){
            setStateBottomSheet("down");
            binding.etSearch.setText("");
            binding.cdlPickupme.setVisibility(View.GONE);
            locationAdapter.clearList();
            locationAdapter.notifyDataSetChanged();
        }
        Log.d("BBB", "onBackPressed: "+distance);
        return true;
    }
}
