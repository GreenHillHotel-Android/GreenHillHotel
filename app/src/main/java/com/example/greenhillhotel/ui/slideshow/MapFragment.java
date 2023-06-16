package com.example.greenhillhotel.ui.slideshow;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.greenhillhotel.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Fragment responsible for displaying a map.
 */
public class MapFragment extends Fragment {

    /**
     * Method to display a map with localisation of the hotel.
     *
     * It uses marker to pin a localisation based on provided
     * latitude and longitude.
     */
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        SupportMapFragment supportMapFragment = (SupportMapFragment)
                getChildFragmentManager().findFragmentById(R.id.google_map);

        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            /**
             * Method to display the marker with hotels localisation
             */
            @Override
            public void onMapReady(@NonNull GoogleMap googleMap) {
                LatLng hotelLocation = new LatLng(51.941262, 15.52969);

                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(hotelLocation, 15));

                MarkerOptions markerOptions = new MarkerOptions()
                        .position(hotelLocation)
                        .title("GreenHillHotel");
                googleMap.addMarker(markerOptions);


            }
        });


        return view;
    }


}