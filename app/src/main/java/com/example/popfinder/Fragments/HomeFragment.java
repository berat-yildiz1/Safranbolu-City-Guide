package com.example.popfinder.Fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.example.popfinder.Adapter.GooglePlaceAdapter;
import com.example.popfinder.Adapter.TouristicAdapter;
import com.example.popfinder.Constant.AllConstant;
import com.example.popfinder.GooglePlaceModel;
import com.example.popfinder.Model.GoogleResponseModel;
import com.example.popfinder.Permissions.AppPermissions;
import com.example.popfinder.PlaceModel;
import com.example.popfinder.R;
import com.example.popfinder.TouristicPlaceModelClass;
import com.example.popfinder.WebServices.RetrofitAPI;
import com.example.popfinder.WebServices.RetrofitClient;
import com.example.popfinder.databinding.FragmentHomeBinding;
import com.example.popfinder.Utility.LoadingDialog;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class HomeFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private FragmentHomeBinding binding;
    private GoogleMap mGoogleMap;
    private AppPermissions appPermissions;
    private boolean isLocationPermissionOk, isTrafficEnable;
    private com.google.android.gms.location.LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Location currentLocation;
    private Marker currentMarker;
    private LoadingDialog loadingDialog;
    private int radius = 5000;
    private RetrofitAPI retrofitAPI;
    private List<GooglePlaceModel> googlePlaceModelList;
    private PlaceModel selectedPlaceModel;
    private GooglePlaceAdapter googlePlaceAdapter;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager1;
    private List<TouristicPlaceModelClass>touristicPlaceModelClassList;
    private TouristicAdapter touristicAdapter;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate ( inflater,container,false );
        appPermissions = new AppPermissions ();
        loadingDialog = new LoadingDialog(requireActivity());
        retrofitAPI = RetrofitClient.getRetrofitClient().create(RetrofitAPI.class);
        googlePlaceModelList = new ArrayList<>();

        binding.btnMapType.setOnClickListener(view -> {
            PopupMenu popupMenu = new PopupMenu(requireContext(), view);
            popupMenu.getMenuInflater().inflate(R.menu.map_type_menu, popupMenu.getMenu());


            popupMenu.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.btnNormal:
                        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                        break;

                    case R.id.btnSatellite:
                        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                        break;

                    case R.id.btnTerrain:
                        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                        break;
                }
                return true;
            });
            
            popupMenu.show();
        });

        binding.enableTraffic.setOnClickListener(view -> {

            if (isTrafficEnable) {
                if (mGoogleMap != null) {
                    mGoogleMap.setTrafficEnabled(false);
                    isTrafficEnable = false;
                }
            } else {
                if (mGoogleMap != null) {
                    mGoogleMap.setTrafficEnabled(true);
                    isTrafficEnable = true;
                }
            }

        });


        binding.currentLocation.setOnClickListener(currentLocation -> getCurrentLocation());

        binding.placesGroup.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(ChipGroup group, int checkedId) {

                if (checkedId != -1) {
                    PlaceModel placeModel = AllConstant.placesName.get(checkedId - 1);
                    binding.edtPlaceName.setText(placeModel.getName());
                    selectedPlaceModel = placeModel;
                    getPlaces(placeModel.getPlaceType());
                }
            }
        });

        initdata();
        initRecylerView();



        return binding.getRoot ();

    }




    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager ().findFragmentById ( R.id.homeMap );


        assert mapFragment != null;
        mapFragment.getMapAsync ( this   );



        for (PlaceModel placeModel : AllConstant.placesName) {

            Chip chip = new Chip(requireContext());
            chip.setText(placeModel.getName());
            chip.setId(placeModel.getId());
            chip.setPadding(8, 8, 8, 8);
            chip.setTextColor(getResources().getColor( R.color.white,null ));
            chip.setChipBackgroundColor(getResources().getColorStateList(R.color.primaryColor, null));
            chip.setChipIcon( ResourcesCompat.getDrawable(getResources(), placeModel.getDrawableId(), null));
            chip.setCheckable(true);
            chip.setCheckedIconVisible(false);

            binding.placesGroup.addView(chip);


        }

        setUpRecyclerView ();

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

        mGoogleMap = googleMap;

        if(appPermissions.isLocationOk ( requireContext () )){
            isLocationPermissionOk=true;

            setUpGoogleMap();
        }else if (ActivityCompat.shouldShowRequestPermissionRationale ( requireActivity (), Manifest.permission.ACCESS_FINE_LOCATION )){

            new AlertDialog.Builder ( requireContext () )
                    .setTitle ( "Location Permission" )
                    .setMessage ( "PopFinder required location permission to show you near by places" )
                    .setPositiveButton ( "Ok", new DialogInterface.OnClickListener () {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                           requestLocation();
                        }
                    } )
                .create().show();
        }else{
            requestLocation();
        }

    }
    private void  requestLocation(){
        requestPermissions ( new String []{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION
        ,Manifest.permission.ACCESS_BACKGROUND_LOCATION},AllConstant.LOCATION_REQUEST_CODE );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == AllConstant.LOCATION_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                isLocationPermissionOk = true;
                setUpGoogleMap();
            } else {
                isLocationPermissionOk = false;
                Toast.makeText(requireContext(), "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setUpGoogleMap() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            isLocationPermissionOk = false;
            return;
        }
        mGoogleMap.setMyLocationEnabled(true);
        mGoogleMap.getUiSettings().setTiltGesturesEnabled(true);


        setUpLocationUpdate();
    }

    private void setUpLocationUpdate() {

        locationRequest = com.google.android.gms.location.LocationRequest.create ();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority( LocationRequest.PRIORITY_HIGH_ACCURACY );

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult != null) {
                    for (Location location : locationResult.getLocations()) {
                        Log.d("TAG", "onLocationResult: " + location.getLongitude() + " " + location.getLatitude());
                    }
                }
                super.onLocationResult(locationResult);
            }
        };
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext());

        startLocationUpdates();


    }

    private void startLocationUpdates() {

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            isLocationPermissionOk = false;
            return;
        }

        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
                .addOnCompleteListener(new OnCompleteListener<Void> () {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(requireContext(), "Location updated started", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

        getCurrentLocation();
    }

    private void getCurrentLocation() {

        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext());

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            isLocationPermissionOk = false;
            return;
        }
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener ( new OnSuccessListener<Location> () {
            @Override
            public void onSuccess(Location location) {
                currentLocation = location;

                moveCameraToLocation(location);
            }

        });
    }
    private void moveCameraToLocation(Location location) {

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new
                LatLng (location.getLatitude(), location.getLongitude()), 17);

        MarkerOptions markerOptions = new MarkerOptions()
                .position(new LatLng(location.getLatitude(), location.getLongitude()))
                .title("Current Location")
                .icon( BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));


        if (currentMarker != null) {
            currentMarker.remove();
        }

        currentMarker = mGoogleMap.addMarker(markerOptions);
        currentMarker.setTag(703);
        mGoogleMap.animateCamera(cameraUpdate);

    }
    private void stopLocationUpdate() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
        Log.d("TAG", "stopLocationUpdate: Location Update stop");
    }

    @Override
    public void onPause() {
        super.onPause();

        if (fusedLocationProviderClient != null)
            stopLocationUpdate();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (fusedLocationProviderClient != null) {

            startLocationUpdates();
            if (currentMarker != null) {
                currentMarker.remove();
            }
        }
    }

    private void getPlaces(String placeName) {

        if (isLocationPermissionOk) {


            loadingDialog.startLoading();
            String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="
                    + currentLocation.getLatitude() + "," + currentLocation.getLongitude()
                    + "&radius=" + radius + "&type=" + placeName + "&key=" +
                    getResources().getString(R.string.API_KEY);

            if (currentLocation != null) {


                retrofitAPI.getNearByPlaces(url).enqueue(new Callback<GoogleResponseModel> () {

                    @Override
                    public void onResponse(@NonNull Call<GoogleResponseModel> call, @NonNull Response<GoogleResponseModel> response) {
                        Gson gson = new Gson();
                        String res = gson.toJson(response.body());
                        Log.d("TAG", "onResponse: " + res);
                        if (response.errorBody() == null) {
                            if (response.body() != null) {
                                if (response.body().getGooglePlaceModelList() != null && response.body().getGooglePlaceModelList().size() > 0) {

                                    googlePlaceModelList.clear();
                                    mGoogleMap.clear();
                                    for (int i = 0; i < response.body().getGooglePlaceModelList().size(); i++) {

                                        googlePlaceModelList.add(response.body().getGooglePlaceModelList().get(i));
                                        addMarker(response.body().getGooglePlaceModelList().get ( i ),i);


                                    }

                                    googlePlaceAdapter.setGooglePlaceModels(googlePlaceModelList);

                                }else if (response.body().getError() != null) {
                                    Snackbar.make(binding.getRoot(),
                                            response.body().getError(),
                                            Snackbar.LENGTH_LONG).show();
                                }  else {

                                    mGoogleMap.clear();
                                    googlePlaceModelList.clear();
                                    googlePlaceAdapter.setGooglePlaceModels(googlePlaceModelList);
                                    radius += 1000;
                                    getPlaces(placeName);

                                }
                            }

                        } else {
                            Log.d("TAG", "onResponse: " + response.errorBody());
                            Toast.makeText(requireContext(), "Error : " + response.errorBody(), Toast.LENGTH_SHORT).show();
                        }

                        loadingDialog.stopLoading();

                    }

                    @Override
                    public void onFailure(Call<GoogleResponseModel> call, Throwable t) {

                        Log.d("TAG", "onFailure: " + t);
                        loadingDialog.stopLoading();

                    }
                });
            }
        }

    }


    private void addMarker(GooglePlaceModel googlePlaceModel, int position) {

        MarkerOptions markerOptions = new MarkerOptions()
                .position(new LatLng(googlePlaceModel.getGeometry().getLocation().getLat(),
                        googlePlaceModel.getGeometry().getLocation().getLng()))
                .title(googlePlaceModel.getName())
                .snippet(googlePlaceModel.getVicinity());
        markerOptions.icon(getCustomIcon());
        mGoogleMap.addMarker(markerOptions).setTag(position);

    }

    private BitmapDescriptor getCustomIcon() {

        Drawable background = ContextCompat.getDrawable(requireContext(), R.drawable.ic_location);
        background.setTint(getResources().getColor(R.color.quantum_googred900, null));
        background.setBounds(0, 0, background.getIntrinsicWidth(), background.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(background.getIntrinsicWidth(), background.getIntrinsicHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        background.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private void setUpRecyclerView() {

        binding.placesRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.placesRecyclerView.setHasFixedSize(false);
        googlePlaceAdapter = new GooglePlaceAdapter();
        binding.placesRecyclerView.setAdapter(googlePlaceAdapter);

        SnapHelper snapHelper = new PagerSnapHelper();

        snapHelper.attachToRecyclerView(binding.placesRecyclerView);

        binding.placesRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

                int position = linearLayoutManager.findFirstCompletelyVisibleItemPosition();
                if (position > -1) {
                    GooglePlaceModel googlePlaceModel = googlePlaceModelList.get(position);
                    mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(googlePlaceModel.getGeometry().getLocation().getLat(),
                            googlePlaceModel.getGeometry().getLocation().getLng()), 20));
                }
            }
        });

    }


    @Override
    public boolean onMarkerClick(Marker marker) {

        int markerTag = (int) marker.getTag();
        Log.d("TAG", "onMarkerClick: " + markerTag);

        binding.placesRecyclerView.scrollToPosition(markerTag);
        return false;
    }

    private void addMarker2(int position,double lat, double longs,String name, String info) {

        MarkerOptions markerOptions = new MarkerOptions()
                .position(new LatLng(lat,
                        longs))
                .title(name)
                .snippet(info);
        markerOptions.icon(getCustomIcon());
        mGoogleMap.addMarker(markerOptions).setTag(position);

    }





    private void initdata() {

        touristicPlaceModelClassList= new ArrayList<> ();

        touristicPlaceModelClassList.add (new TouristicPlaceModelClass ( R.drawable.cincihamami,"Cinci Hamamı","Cinci Hamamı","41.244869459283265","32.69334272415161" ));
        touristicPlaceModelClassList.add (new TouristicPlaceModelClass ( R.drawable.eskicarsi,"Safranbolu Eski Çarşı","Safranbolu Eski Çarşı","41.244124462650284","32.6932306109375" ));

    }
    private void initRecylerView() {

        recyclerView = binding.placesRecyclerView2;
        linearLayoutManager1 = new LinearLayoutManager (requireContext ());
        linearLayoutManager1.setOrientation ( RecyclerView.HORIZONTAL );
        recyclerView.setLayoutManager ( linearLayoutManager1 );
        touristicAdapter = new TouristicAdapter ( touristicPlaceModelClassList );
        recyclerView.setAdapter ( touristicAdapter );
        touristicAdapter.notifyDataSetChanged ();

        binding.placesRecyclerView2.addOnScrollListener ( new RecyclerView.OnScrollListener () {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled ( recyclerView, dx, dy );
                LinearLayoutManager linearLayoutManager2 = (LinearLayoutManager) recyclerView.getLayoutManager ();
                int position = linearLayoutManager2.findFirstCompletelyVisibleItemPosition();
                if(position>0){
                    TouristicPlaceModelClass touristicPlaceModelClass = touristicPlaceModelClassList.get ( position );
                    double lnttut1 = Double.valueOf ( touristicPlaceModelClass.getLats () );
                    double longtut = Double.valueOf ( touristicPlaceModelClass.getLongs () );
                    System.out.println ("lnt:"+lnttut1+"long"+longtut);
                    String nametut = touristicPlaceModelClass.getTextview1 ();
                    String infotut = touristicPlaceModelClass.getTextview2 ();
                    mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lnttut1,
                            longtut), 50));
                    addMarker2 ( position,lnttut1,longtut,nametut,infotut );


                }
            }
        } );
    }


}