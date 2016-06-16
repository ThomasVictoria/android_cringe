package fr.stephenrichard.cringe.fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.HashMap;
import java.util.Map;

import fr.stephenrichard.cringe.CircleTransform;
import fr.stephenrichard.cringe.R;
import fr.stephenrichard.cringe.activity.DetailCringeActivity;
import fr.stephenrichard.cringe.model.Cringe;

public class MapFragment extends Fragment implements OnMapReadyCallback, LocationListener {

    private View rootView;
    GoogleMap googleMap;
    MapView mMapView;
    private DatabaseReference mDatabase;

    private Map<String, String> allMarkersMap = new HashMap<>();

    @Override
    public void onMapReady(final GoogleMap map) {

        mDatabase = FirebaseDatabase.getInstance().getReference();

        if (ContextCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            map.setMyLocationEnabled(true);

            String locationProvider = LocationManager.NETWORK_PROVIDER;

            LocationManager locationManager = (LocationManager)
                    getActivity().getSystemService(Context.LOCATION_SERVICE);

            Location lastKnownLocation = locationManager.getLastKnownLocation(locationProvider);


            LatLng coordinate = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
            CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(coordinate, 13);
            map.animateCamera(yourLocation);


        } else {
            Toast.makeText(this.getContext(), "Geolocation Access Denied",
                    Toast.LENGTH_LONG).show();
        }

        ValueEventListener CringeListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (final DataSnapshot child : dataSnapshot.child("cringes").getChildren()) {
                    final Double lat = Double.parseDouble(child.child("lat").getValue().toString());
                    final Double lng = Double.parseDouble(child.child("lng").getValue().toString());

                    final Boolean isPrivate = Boolean.valueOf(child.child("isPrivate").getValue().toString());
                    String author_pictures = child.child("author_picture").getValue().toString();

                    final String uid = child.child("uid").getValue().toString();

                    Picasso
                            .with(getContext())
                            .load(author_pictures)
                            .transform(new CircleTransform())
                            .into(new Target() {
                                @Override
                                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                    if(!isPrivate) {
                                        Marker marker = map.addMarker(new MarkerOptions()
                                                .position(new LatLng(lng, lat))
                                                .icon(BitmapDescriptorFactory.fromBitmap(bitmap))
                                        );
                                        allMarkersMap.put(marker.getId(), child.getKey());
                                    }



                                }

                                @Override
                                public void onBitmapFailed(Drawable errorDrawable) {

                                }

                                @Override
                                public void onPrepareLoad(Drawable placeHolderDrawable) {

                                }
                            });

                    map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                        @Override
                        public boolean onMarkerClick(Marker marker) {
                            Intent intent1 = new Intent(getContext(), DetailCringeActivity.class);
                            String id = marker.getId();
                            intent1.putExtra(DetailCringeActivity.EXTRA_POST_KEY, allMarkersMap.get(id));
                            startActivity(intent1);
                            return false;
                        }

                    });

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                Log.w("Error", "loadPost:onCancelled", databaseError.toException());

            }
        };
        mDatabase.addValueEventListener(CringeListener);



    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        try {
            rootView = inflater.inflate(R.layout.activity_map, container, false);
            MapsInitializer.initialize(this.getActivity());
            mMapView = (MapView) rootView.findViewById(R.id.map);
            mMapView.onCreate(savedInstanceState);
            mMapView.getMapAsync(this);
        }
        catch (InflateException e){
            Log.e("Error", "Inflate exception");
        }
        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState); mMapView.onSaveInstanceState(outState);
    }
    @Override
    public void onLowMemory()
    {
        super.onLowMemory();
        mMapView.onLowMemory();
    }
    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}