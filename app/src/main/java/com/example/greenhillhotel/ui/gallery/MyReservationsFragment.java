package com.example.greenhillhotel.ui.gallery;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.greenhillhotel.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MyReservationsFragment extends Fragment {

    private Spinner spinner2 ;
    List<DocumentSnapshot> reservations = new ArrayList<>();
    TextView noReservations;
    TextView arrival;
    TextView departure;
    TextView tv;
    TextView balcony;
    TextView bedConfig;
    TextView roomNumber;
    Button cancelButton;
    List<String> items = new ArrayList<>();
    SimpleDateFormat dateFormat;
    DocumentSnapshot roomData;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reservation, container, false);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        roomNumber = view.findViewById(R.id.roomNumber);
        noReservations = view.findViewById(R.id.noReservation);
        cancelButton = view.findViewById(R.id.buttonCancel);
        arrival = view.findViewById(R.id.arrivalRes);
        departure = view.findViewById(R.id.departureRes);
        tv = view.findViewById(R.id.doesTv);
        balcony = view.findViewById(R.id.doesBalcony);
        bedConfig = view.findViewById(R.id.configuration);
        spinner2 = view.findViewById(R.id.spinner1);
        dateFormat = new SimpleDateFormat("dd/mm/yyyy");

        db.collection("reservations")
                .whereEqualTo("uid", user.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        reservations = task.getResult().getDocuments();
                        if (reservations.size() == 0) {
                            roomNumber.setVisibility(View.GONE);
                            noReservations.setVisibility(View.VISIBLE);
                            spinner2.setVisibility(View.GONE);
                            cancelButton.setVisibility(View.GONE);
                            arrival.setVisibility(View.GONE);
                            departure.setVisibility(View.GONE);
                            tv.setVisibility(View.GONE);
                            balcony.setVisibility(View.GONE);
                            bedConfig.setVisibility(View.GONE);
                        }

                        for (DocumentSnapshot document : reservations) {
                            items.add(String.format("%s -> %s",
                                    dateFormat.format(document.getDate("arrival")),
                                    dateFormat.format(document.getDate("departure"))));
                        }
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, items);
                        spinner2.setAdapter(adapter);

                        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                arrival.setText("Arrival: " + dateFormat.format(reservations.get(position).getDate("arrival")));
                                departure.setText("Departure: " + dateFormat.format(reservations.get(position).getDate("departure")));
                                tv.setText("Television: " + (boolean) reservations.get(position).get("tv"));
                                DocumentReference roomDataReference = (DocumentReference) reservations.get(position).get("room");
                                roomDataReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        roomData = task.getResult();
                                        balcony.setText("Balcony: " + (boolean) roomData.get("isBalcony"));
                                        roomNumber.setText("Room number: " + roomData.get("id"));
                                    }
                                });
                                bedConfig.setText("Bed config: " + reservations.get(position).get("bedConfig").toString());
                                try {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                        Date today = dateFormat.parse(dateFormat.format(new Date()));
                                        Calendar c = Calendar.getInstance();
                                        c.setTime(today);
                                        c.add(Calendar.DATE, 7);
                                        Date plusWeek = c.getTime();
                                        boolean cancelable = plusWeek.compareTo(reservations.get(position).getDate("arrival")) <= 0;
                                        Log.d("tag", String.valueOf(cancelable));
                                        if (!cancelable) {
                                            cancelButton.setVisibility(View.GONE);
                                        }
                                        else {
                                            cancelButton.setVisibility(View.VISIBLE);
                                        }
                                    }
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }


                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {
                                return;
                            }
                        });
                    }
                });

        return view;
    }

}