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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.greenhillhotel.R;
import com.example.greenhillhotel.ui.book.ConfigureFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Fragment that is responsible for displaying all of users reservations
 * and cancellation of the reservations.
 *
 */
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
    SimpleDateFormat todayFormat;
    DocumentSnapshot roomData;
    int reservationIndex;
    ArrayAdapter<String> adapter;


    /**
     * Method to implement users reservations panel and its functionality.
     *
     * It is responsible for displaying all of the users reservations,
     * formatting the data and canceling the reservations.
     */
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
        dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        todayFormat = new SimpleDateFormat("yyyy-MM-dd");

        db.collection("users").document(user.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                db.collection("reservations")
                        .whereEqualTo("user", task.getResult().getReference())
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

                                adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, items);
                                spinner2.setAdapter(adapter);

                                spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                        reservationIndex = position;
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
                                                Date today = todayFormat.parse(LocalDate.now().toString());
                                                Date arrival = dateFormat.parse(dateFormat.format(reservations.get(position).getDate("arrival")));
                                                long diffInMillies = Math.abs(arrival.getTime() - today.getTime());
                                                long diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
                                                Log.d("tag", String.valueOf(diff));
                                                boolean cancelable = diff >= 7;
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
            }
        });


        cancelButton.setOnClickListener(new View.OnClickListener() {
            /**
             * Listener which is invoked when you click the cancel button
             */
            @Override
            public void onClick(View v) {
                DocumentSnapshot reservationSnapshot = reservations.get(reservationIndex);
                DocumentReference reservationReference = reservationSnapshot.getReference();
                reservationReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    /**
                     * Listener which is responsible for all of the actions
                     * after successful cancellation of the reservation.
                     * It sends a toast with information and manages List objects.
                     */
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(getActivity(), "Success.", Toast.LENGTH_SHORT).show();
                        reservations.remove(reservationSnapshot);
                        items.remove(reservationIndex);
                        adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, items);
                        spinner2.setAdapter(adapter);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    /**
                     * Listener which is responsible for all of the actions
                     * after unsuccessful cancellation of the reservation.
                     * It sends a toast with information and returns to home page.
                     */
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "Failed to cancel a room.", Toast.LENGTH_SHORT).show();
                        NavController navController = Navigation.findNavController(v);
                        navController.navigate(R.id.nav_home);
                    }
                });
            }
        });

        return view;
    }

}