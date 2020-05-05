package gopdu.pdu.vesion2.fragment;


import android.app.ProgressDialog;
import android.location.Address;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewpager.widget.ViewPager;

import com.arsy.maps_library.MapRadar;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
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
import com.google.android.libraries.places.compat.AutocompleteFilter;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gopdu.pdu.vesion2.Common;
import gopdu.pdu.vesion2.GoPDUApplication;
import gopdu.pdu.vesion2.IOnBackPressed;
import gopdu.pdu.vesion2.R;
import gopdu.pdu.vesion2.adapter.ItemLocationAdapter;
import gopdu.pdu.vesion2.adapter.ItemServiceAdapter;
import gopdu.pdu.vesion2.databinding.FragmentCustomerMapBinding;
import gopdu.pdu.vesion2.network.HistoryDetailRespon;
import gopdu.pdu.vesion2.network.ListServiceRespon;
import gopdu.pdu.vesion2.object.HistoryDetail;
import gopdu.pdu.vesion2.object.ServerResponse;
import gopdu.pdu.vesion2.object.Service;
import gopdu.pdu.vesion2.presenter.PresenterCustomerMapFragment;
import gopdu.pdu.vesion2.service.APIService;
import gopdu.pdu.vesion2.service.DataService;
import gopdu.pdu.vesion2.view.ViewCustomerMapListener;
import gopdu.pdu.vesion2.viewmodel.CheckRattingViewModel;
import gopdu.pdu.vesion2.viewmodel.InsertRatingViewModel;
import gopdu.pdu.vesion2.viewmodel.ListServiceViewModel;

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
    private View bottomSheetDes;
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
    private CheckRattingViewModel checkRattingModel;
    private InsertRatingViewModel insertRatingModel;

    //Marker
    private Marker pickUpMarker;
    private Marker destinationMarker;

    //Firebase Account
    private FirebaseAuth mAuth;
    private String userId;


    private boolean requestBoolena = false;
    //Status trip
    private String statusTrip="";

    //Presenter
    private PresenterCustomerMapFragment presenter;

    //Loadding
    private ProgressDialog progressDialog;

    //Map radar
    private MapRadar searchRadar;

    //id driver rating
    private String historyIdRating;

    //status chip
    private DatabaseReference tripRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if(binding == null){
            //dialog
            progressDialog = new ProgressDialog(getContext());
            progressDialog.setMessage(getString(R.string.waiting));
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_customer_map, container, false);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map));
        mapFragment.getMapAsync(this);
        init();
        setUpOnClick();
        setUpServiceView();
        checkRatting();

        checkStatusTrip();

        return binding.getRoot();
    }

    private void checkStatusTrip() {

        checkPickUpLocation();
        DatabaseReference customerRef = FirebaseDatabase.getInstance().getReference().child("User").child("Customer").child(userId).child("DriverRequest");
        customerRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                presenter.reciverResumeTrip(dataSnapshot);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void checkPickUpLocation() {
        FirebaseDatabase.getInstance().getReference().child(getString(R.string.paramCustomerRequest)).child(userId).child(getString(R.string.paramL)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() ) {
                    List<Object> map = (List<Object>) dataSnapshot.getValue();
                    double locationLat = 0;
                    double locationLongi = 0;
                    if (map.get(0) != null) {
                        locationLat = Double.parseDouble(map.get(0).toString());
                    }
                    if (map.get(1) != null) {
                        locationLongi = Double.parseDouble(map.get(1).toString());
                    }
                    pickUpLng = new LatLng(locationLat, locationLongi);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void checkRatting() {

        HashMap<String, String> param = new HashMap<>();
        param.put(getString(R.string.paramID), userId);
        checkRattingModel.checkRatting(param).observe(this, new Observer<HistoryDetailRespon>() {
            @Override
            public void onChanged(HistoryDetailRespon historyDetailRespon) {
                presenter.reciverCheckRatting(historyDetailRespon);
            }
        });

    }

    private void setUpServiceView() {
        services = new ArrayList<>();
        serviceAdapter = new ItemServiceAdapter(services);
        binding.contentInfomation.viewPagerService.setAdapter(serviceAdapter);
        serviceModel.getService().observe(this, new Observer<ListServiceRespon>() {
            @Override
            public void onChanged(ListServiceRespon listServiceRespon) {
                if (listServiceRespon != null) {
                    services.addAll(listServiceRespon.getData());
                    serviceAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    private void init() {
        binding.contentInfomation.cdlInfomation.setVisibility(View.GONE);

        //set animation
        binding.contentPickup.cdlPickupme.setAnimation(Common.animationVisible());
        binding.contentInfomation.cdlInfomation.setAnimation(Common.animationVisible());
        binding.contentSearch.cdlDestination.setAnimation(Common.animationVisible());
        binding.contentInfomationDriver.cdlInfomationDriver.setAnimation(Common.animationVisible());
        binding.contentRatting.cdlRatting.setAnimation(Common.animationVisible());





        //object location
        locationDes = new gopdu.pdu.vesion2.object.Location();
        locationPickUp = new gopdu.pdu.vesion2.object.Location();

        presenter = new PresenterCustomerMapFragment(this);

        //get serviceMoidel ( dịch vụ )
        serviceModel = ViewModelProviders.of(this).get(ListServiceViewModel.class);
        checkRattingModel = ViewModelProviders.of(this).get(CheckRattingViewModel.class);
        insertRatingModel = ViewModelProviders.of(this).get(InsertRatingViewModel.class);
        dataService = APIService.getService();

        //setup list search adapter=
        autocompleteFilte = new AutocompleteFilter.Builder()
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_NONE)
                .setCountry(getString(R.string.country))
                .build();

        //setup botomsheet
        bottomSheetDes = binding.getRoot().findViewById(binding.contentSearch.bottomSheetDes.getId());
        behavior = BottomSheetBehavior.from(bottomSheetDes);
        binding.contentPickup.cdlPickupme.setVisibility(View.GONE);
        customPickup = false;

        //Google api set up
        buildGoogleAPiClient();
        if (!mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
        }
        locationPickUp = new gopdu.pdu.vesion2.object.Location();

        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getUid();

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
                setStatusBottomSheet(getString(R.string.up));
            }
        });

        binding.contentSearch.etSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setStatusBottomSheet(getString(R.string.up));
            }
        });

        binding.contentPickup.btnSearchPickup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                pickUpMarker = addMarker(mLastLocation.getLatitude(), mLastLocation.getLongitude(), R.mipmap.ic_pickup, getString(R.string.pickupHere));
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
                presenter.reciverEndRide(requestBoolena);
            }
        });

        binding.contentRatting.tvLater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.contentRatting.cdlRatting.setVisibility(View.INVISIBLE);
                binding.contentSearch.cdlDestination.setVisibility(View.VISIBLE);
            }
        });

        binding.contentRatting.tvNever.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                insertRating(historyIdRating, Double.parseDouble(getString(R.string.ratingNever)));
            }
        });

        binding.contentRatting.tvAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                insertRating(historyIdRating, binding.contentRatting.ratting.getRating());
            }
        });

        binding.contentInfomationDriver.btnEndride.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endRide();
            }
        });


    }


    // setup view search location pickup ( Thiết lập thanh tìm kiếm điểm đến )
    private void pickUpAdress() {
        binding.contentPickup.cdlPickupme.setVisibility(View.GONE);

        binding.contentInfomation.cdlInfomation.setVisibility(View.VISIBLE);

        binding.contentInfomation.tvNamePickupInfo.setText(locationPickUp.getNameLocation());
        binding.contentInfomation.tvNameDestinationInfo.setText(locationDes.getNameLocation());

        destinationLng = new LatLng(locationDes.getLat(), locationDes.getLogt());
        pickUpLng = new LatLng(locationPickUp.getLat(), locationPickUp.getLogt());
        distance = Common.getDistance(destinationLng, pickUpLng)/1000;
        Log.d("BBB", "pickUpAdress: "+distance);
        binding.contentInfomation.tvDistanceInfo.setText(getString(R.string.distance, distance));
        getPrice(services.get(0).getPrice());
        customPickup = false;
    }

    //show price for travel
    private void getPrice(int price) {
        int cash = Math.round((distance * price));;
        if (cash == 0) {
            this.price = price;
        } else {
            this.price = cash;
        }
        binding.contentInfomation.tvPriceInfo.setText(getString(R.string.cash, Common.formatVNĐ(this.price)));
    }

    //If wan't custom pickup location clear history search
    private void setUpSearchPickup() {
        binding.contentSearch.tvTitle.setText(getString(R.string.wherePickUp));
        binding.contentSearch.etSearch.setText(locationPickUp.getNameLocation());
        binding.contentSearch.etSearch.setFocusable(true);
        setStatusBottomSheet(getString(R.string.up));
        locationAdapter.setFromLng(new LatLng(locationDes.getLat(), locationDes.getLogt()));
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


    //Set up adapter search locationDes ( Thiết lập dữ liệu cho việc tìm kiếm điểm đến )
    private void setUpListLocation(Location location) {

        locationAdapter = new ItemLocationAdapter(mGoogleApiClient,
                Common.LAT_LNG_BOUNDS, autocompleteFilte, new LatLng(location.getLatitude(), location.getLongitude()));
        locationAdapter.setFromLng(new LatLng(location.getLatitude(), location.getLongitude()));
        binding.contentSearch.rclDes.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        binding.contentSearch.rclDes.setAdapter(locationAdapter);

        //Item onclick
        locationAdapter.setOnItemClickedListener(new ItemLocationAdapter.OnItemClickedListener() {
            @Override
            public void onItemClick(int postion, View v) {
                presenter.reciverLocationOnclick(customPickup, postion);

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
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

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
    private void setStatusBottomSheet(String status) {
        behavior.setPeekHeight(GoPDUApplication.getInstance().getResources().getDimensionPixelSize(R.dimen.peekHeightMenu));
        if (status == getString(R.string.up)) {
            behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        } else if (status == getString(R.string.down)) {
            behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
    }

    private void setStatusBottomSheet2(String status) {
        behavior.setPeekHeight(0);
        if (status == getString(R.string.up) && customPickup) {
            behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        } else if (status == getString(R.string.down)) {
            behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
    }


    private int radius = 0;
    private Boolean driverFound = false;
    private String driverFoundId;
    GeoQuery geoQuery;

    private void pickDriver() {
        DatabaseReference driverLocation = FirebaseDatabase.getInstance().getReference().child("driversAvailable");
        final GeoFire geoFire = new GeoFire(driverLocation);
        geoQuery = geoFire.queryAtLocation(new GeoLocation(pickUpLng.latitude, pickUpLng.longitude), radius);
        geoQuery.removeAllListeners();
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(final String key, final GeoLocation location) {
                if (driverFound == false && requestBoolena && driverFoundId == null) {
                    if (driverFound) {
                        return;
                    }
                    driverFound = true;
                    driverFoundId = key;
                    searchRadar.stopRadarAnimation();
                    presenter.pushInfomationTravel(driverFoundId);
                    takenInfomationDriver(key);
                    getHasRideEnded();
                }
            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {
                if (driverFound == false && requestBoolena) {
                    radius++;
                    Log.d("BBB", "onGeoQueryReady: " + radius);
                    pickDriver();
                }
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });
    }

    //Lấy vị thông tin tài xế
    private void takenInfomationDriver(String key) {
        binding.contentInfomationDriver.cdlInfomationDriver.setVisibility(View.VISIBLE);
        binding.contentSearch.cdlDestination.setVisibility(View.INVISIBLE);
        binding.contentPickup.cdlPickupme.setVisibility(View.INVISIBLE);
        binding.contentInfomation.cdlInfomation.setVisibility(View.INVISIBLE);
        DatabaseReference customerDatabase = FirebaseDatabase.getInstance().getReference().child("User").child("Driver").child(driverFoundId);
        customerDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    Log.d("BBB", "onDataChange: " + map);
                    if (map.get(getString(R.string.paramName)) != null) {
                        binding.contentInfomationDriver.tvDrivername.setText(map.get(getString(R.string.paramName)).toString());
                    }

                    if (map.get(getString(R.string.paramLicenseplate)) != null) {
                        binding.contentInfomationDriver.tvLicensePlate.setText(map.get(getString(R.string.paramLicenseplate)).toString());
                    }

                    if (map.get(getString(R.string.paramImv_Driverface)) != null) {
                        Glide.with(getContext())
                                .load(map.get(getString(R.string.paramImv_Driverface)))
                                .apply(RequestOptions.circleCropTransform())
                                .into(binding.contentInfomationDriver.imvDriverFace);
                    }
                    binding.contentInfomationDriver.tvPrice.setText(Common.formatVNĐ(price));
                    View bottomSheetDes =   binding.getRoot().findViewById(binding.contentInfomationDriver.bottomSheetInfo.getId());
                    BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(bottomSheetDes);
                    behavior.setState(BottomSheetBehavior.STATE_EXPANDED);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("BBB", "onCancelled: " + databaseError.toString());
            }
        });

    }

    // back press
    @Override
    public boolean onBackPressed() {
        presenter.reciverBackOnClick(customPickup, locationDes, distance);
        return true;
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
        if (pickUpMarker.getTitle() != null) {
            pickUpMarker.remove();
        }
        setStatusBottomSheet2(getString(R.string.down));
        binding.contentPickup.cdlPickupme.setVisibility(View.VISIBLE);

        locationPickUp = locationAdapter.getItem(postion);
        binding.contentPickup.tvNamePickup.setText(locationPickUp.getNameLocation());
        binding.contentPickup.tvNamePickupDetail.setText(locationPickUp.getNameDetailLocation());
        zoomTarget(locationPickUp.getLat(), locationPickUp.getLogt());
        pickUpMarker = addMarker(locationPickUp.getLat(), locationPickUp.getLogt(), R.mipmap.ic_pickup, getString(R.string.pickupHere));
    }

    @Override
    public void onDestination(int postion) {
        if (destinationMarker != null) {
            destinationMarker.remove();
        }
        setStatusBottomSheet2(getString(R.string.down));
        binding.contentPickup.cdlPickupme.setVisibility(View.VISIBLE);

        locationDes = locationAdapter.getItem(postion);
        presenter.reciverGetAddress(mLastLocation.getLatitude(), mLastLocation.getLongitude());
        destinationMarker = addMarker(locationDes.getLat(), locationDes.getLogt(), R.mipmap.ic_destination, getString(R.string.destination));
        zoomTarget(locationDes.getLat(), locationDes.getLogt());
    }

    @Override
    public void getDefaultMylocation(Location location) {
        zoomTarget(location.getLatitude(), location.getLongitude());

        mLastLocation = location;

        if (locationAdapter == null && mLastLocation != null && services.size() >0 ) {
            setUpListLocation(mLastLocation);
            progressDialog.dismiss();
        }

    }

    @Override
    public void backUpFinishApp() {
        getActivity().finish();
    }

    @Override
    public void backToPickUpFromInfomation() {
        locationPickUp = new gopdu.pdu.vesion2.object.Location();
        distance = 0;
        setStatusBottomSheet2(getString(R.string.down));
        binding.contentPickup.cdlPickupme.setVisibility(View.VISIBLE);

        binding.contentInfomation.cdlInfomation.setVisibility(View.GONE);
        customPickup = false;
    }

    @Override
    public void backToPickUpFromCustomPickUp() {
        if (pickUpMarker != null) {
            pickUpMarker.remove();
        }

        presenter.getDefaultMylocation(mLastLocation);
        setStatusBottomSheet2(getString(R.string.down));
        binding.contentPickup.cdlPickupme.setVisibility(View.VISIBLE);

        customPickup = false;
    }

    @Override
    public void backToDestination() {
        locationDes = new gopdu.pdu.vesion2.object.Location();

        if (destinationMarker != null) {
            destinationMarker.remove();
        }
        setStatusBottomSheet(getString(R.string.down));
        binding.contentSearch.etSearch.setText("");
        binding.contentPickup.cdlPickupme.setVisibility(View.GONE);
        binding.contentSearch.cdlDestination.setVisibility(View.VISIBLE);
        locationAdapter.clearList();
        locationAdapter.notifyDataSetChanged();
    }

    @Override
    public void pushInfomationTravel(String idDriver) {

        driverFound = true;
        driverFoundId = idDriver;

        DatabaseReference driverRef = FirebaseDatabase.getInstance().getReference().child("User").child("Driver").child(driverFoundId).child("customerRequest");

        HashMap hashMapDriver = new HashMap();
        hashMapDriver.put("customerRideId", userId);
        hashMapDriver.put("destinationLat", locationDes.getLat());
        hashMapDriver.put("destinationLongt", locationDes.getLogt());
        hashMapDriver.put("distance", distance);
        hashMapDriver.put("price", price);
        hashMapDriver.put("status", "waitting");
        driverRef.updateChildren(hashMapDriver);

        DatabaseReference customerRef = FirebaseDatabase.getInstance().getReference().child("User").child("Customer").child(userId).child("DriverRequest");
        HashMap hashMapCustomer = new HashMap();
        hashMapCustomer.put("driverRideId", driverFoundId);
        hashMapCustomer.put("destinationLat", locationDes.getLat());
        hashMapCustomer.put("destinationLongt", locationDes.getLogt());
        hashMapCustomer.put("distance", distance);
        hashMapCustomer.put("price", price);
        hashMapCustomer.put("status", "waitting");

        customerRef.updateChildren(hashMapCustomer);
        binding.contentInfomation.btnCall.setText("Xác định địa điểm của tài xế...");
        Log.d("BBB", "onKeyEntered: " + userId);
    }

    @Override
    public void startRide() {
        if (driverFound) {
            return;
        }
        requestBoolena = true;

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(getString(R.string.paramCustomerRequest));
        GeoFire geoFire = new GeoFire(ref);
        geoFire.setLocation(userId, new GeoLocation(pickUpLng.latitude, pickUpLng.longitude));

        binding.contentInfomation.cdlInfomation.setVisibility(View.GONE);

        if (searchRadar == null) {
            //setUp map radar
            searchRadar = new MapRadar(mMap, pickUpLng, getContext());
            searchRadar.withRadarColors(0x00fccd29, 0xfffccd29);
            searchRadar.withDistance(2000);
            searchRadar.withOuterCircleStrokeColor(0xfccd29);
        } else {
            searchRadar.withLatLng(pickUpLng);
        }

        searchRadar.startRadarAnimation();

        pickDriver();
    }

    private DatabaseReference driverHasEndedRef;
    ValueEventListener driverHasEndedEventListener;

    private void getHasRideEnded() {
        driverHasEndedRef = FirebaseDatabase.getInstance().getReference().child(getString(R.string.paramUser)).child(getString(R.string.paramCustomer)).child(userId).child(getString(R.string.paramDriverRequest)).child(getString(R.string.statusTrip));
        driverHasEndedEventListener = driverHasEndedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Log.d("BBB", "onDataChange: "+dataSnapshot.getValue());
                    requestBoolena = true;
                        statusTrip = dataSnapshot.getValue().toString();

                } else {
                    statusTrip = "";
                    endRide();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("BBB", "onCancelled: "+databaseError.getMessage());
            }
        });
    }

    //end trip
    @Override
    public void endRide() {
        Log.d("BBB", "endRide: "+statusTrip);
            if( !statusTrip.equalsIgnoreCase(getString(R.string.tripPickUp))
                && !statusTrip.equalsIgnoreCase(getString(R.string.tripWaitting))
                && !statusTrip.equalsIgnoreCase("")){
                Common.ShowToastShort(getString(R.string.cancelTrip));
            }else {
                radius = 1;
                requestBoolena = false;
                if (geoQuery != null) {
                    geoQuery.removeAllListeners();
                }

                if (driverFoundId != null) {
                    DatabaseReference driverRef = FirebaseDatabase.getInstance().getReference().child(getString(R.string.paramUser)).child(getString(R.string.paramDriver)).child(driverFoundId).child(getString(R.string.paramCustomerRequest));
                    driverRef.removeValue();

                    if(driverHasEndedRef != null){
                        driverHasEndedRef.removeEventListener(driverHasEndedEventListener);
                    }

                    driverFoundId = null;
                    DatabaseReference customerRef = FirebaseDatabase.getInstance().getReference().child(getString(R.string.paramUser)).child(getString(R.string.paramCustomer)).child(userId).child(getString(R.string.paramDriverRequest));
                    customerRef.removeValue();
                }
                if (destinationMarker != null) {
                    destinationMarker.remove();
//                    destination = null;
                }
                distance = 0;

                destinationLng =null;
                pickUpLng = null;

                LatLng latLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(15));;
                driverFound = false;
                radius = 1;
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("customerRequest");
                GeoFire geoFire = new GeoFire(ref);
                geoFire.removeLocation(userId);

                if (pickUpMarker != null) {
                    pickUpMarker.remove();
                }
                if (destinationMarker != null) {
                    destinationMarker.remove();
                }
                binding.contentInfomationDriver.cdlInfomationDriver.setVisibility(View.GONE);
                backToDestination();
            }
    }

    @Override
    public void showRatingView(HistoryDetail data) {
        Log.d("BBB", "showRatingView: " + data.getDriver().getImvDriverface());
        Glide.with(getContext())
                .load(data.getDriver().getImvDriverface().toString())
                .apply(RequestOptions.circleCropTransform())
                .into(binding.contentRatting.imvDriverFace);

        binding.contentRatting.cdlRatting.setVisibility(View.VISIBLE);
        binding.contentSearch.cdlDestination.setVisibility(View.INVISIBLE);
        binding.contentInfomation.cdlInfomation.setVisibility(View.INVISIBLE);
        binding.contentPickup.cdlPickupme.setVisibility(View.INVISIBLE);
        binding.contentInfomationDriver.cdlInfomationDriver.setVisibility(View.INVISIBLE);
        binding.contentRatting.tvName.setText(data.getDriver().getName());
        binding.contentRatting.tvPrice.setText(getString(R.string.price, Common.formatVNĐ(data.getHistory().getPrice())));
        binding.contentRatting.tvLicencePlate.setText(data.getDriver().getLicenseplates());
        historyIdRating = data.getHistory().getId();

    }

    @Override
    public void insertRatingSuccess(String messenger) {
        binding.contentRatting.cdlRatting.setVisibility(View.INVISIBLE);
        binding.contentSearch.cdlDestination.setVisibility(View.VISIBLE);
        progressDialog.dismiss();
        Common.ShowToastLong(messenger);
    }

    @Override
    public void insertRatingFaild(String messenger) {
        binding.contentRatting.cdlRatting.setVisibility(View.INVISIBLE);
        binding.contentSearch.cdlDestination.setVisibility(View.VISIBLE);
        progressDialog.dismiss();
        Common.ShowToastLong(messenger);
    }

    @Override
    public void resumTrip(final DataSnapshot data) {
        requestBoolena = true;

        Map<String, Object> map = (Map<String, Object>) data.getValue();
        if (map.get(getString(R.string.statusTrip)) != null) {
            statusTrip = map.get(getString(R.string.statusTrip)).toString();
        }
        if (map.get(getString(R.string.paramDriverRideId)) != null) {
            driverFoundId = map.get(getString(R.string.paramDriverRideId)).toString();
            driverFound = true;
            getHasRideEnded();
        }


        Double destinationLat = 0.0;
        Double destinationlongt = 0.0;
        if(map.get(getString(R.string.paramDestinationLat))!= null){
            destinationLat = Double.parseDouble(map.get(getString(R.string.paramDestinationLat)).toString()) ;

        }
        if(map.get(getString(R.string.paramDestinationLogt))!= null){
            destinationlongt = Double.parseDouble(map.get(getString(R.string.paramDestinationLogt)).toString()) ;
        }
        destinationLng = new LatLng(destinationLat, destinationlongt);
        destinationMarker = mMap.addMarker(new MarkerOptions().position(destinationLng).title(getString(R.string.destination)));
        if (map.get(getString(R.string.paramDestination)) != null) {
            locationDes.setNameDetailLocation(map.get(getString(R.string.paramDestination)).toString());
        }
        distance = Common.getDistance(destinationLng, pickUpLng)/1000;
        getPrice(services.get(0).getPrice());
        takenInfomationDriver(driverFoundId);

    }

    private void insertRating(String id, double rating) {
        HashMap param = new HashMap();
        param.put(getString(R.string.paramID), id);
        param.put(getString(R.string.paramRating), rating);
        insertRatingModel.checkRatting(param).observe(this, new Observer<ServerResponse>() {
            @Override
            public void onChanged(ServerResponse serverResponse) {
                presenter.reciverInsertRating(serverResponse);
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
