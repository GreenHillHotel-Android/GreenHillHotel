package com.example.greenhillhotel.ui.admin;

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

import org.w3c.dom.Document;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Fragment that is responsible for management of all the reservations.
 */
public class AdminFragment extends Fragment {
    Spinner spinner3;
    View view;
    List<DocumentSnapshot> reservations = new ArrayList<>();
    TextView userID;
    TextView name;
    TextView surname;
    TextView noReservations;
    TextView arrival;
    TextView departure;
    TextView tv;
    TextView balcony;
    TextView bedConfig;
    TextView roomNumber;
    Button cancelButton;
    List<String> items = new ArrayList<>();
    ArrayAdapter<String> adapter;
    SimpleDateFormat dateFormat;
    int reservationIndex;
    DocumentSnapshot roomData;
    DocumentSnapshot userData;

    /**
     * Method to implement admin panel and its functionality.
     *
     * It is responsible for displaying all of the users reservations,
     * formatting the data and canceling the reservations.
     */
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_admin, container, false);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        name = view.findViewById(R.id.name);
        surname = view.findViewById(R.id.surname);
        userID = view.findViewById(R.id.userID);
        spinner3 = view.findViewById(R.id.spinner3);
        roomNumber = view.findViewById(R.id.roomNumber2);
        noReservations = view.findViewById(R.id.noReservation2);
        cancelButton = view.findViewById(R.id.buttonCancel2);
        arrival = view.findViewById(R.id.arrivalRes2);
        departure = view.findViewById(R.id.departureRes2);
        tv = view.findViewById(R.id.doesTv2);
        balcony = view.findViewById(R.id.doesBalcony2);
        bedConfig = view.findViewById(R.id.configuration2);
        db.collection("reservations")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        reservations = task.getResult().getDocuments();
                        if (reservations.size() == 0) {
                            name.setVisibility(View.GONE);
                            surname.setVisibility(View.GONE);
                            userID.setVisibility(View.GONE);
                            roomNumber.setVisibility(View.GONE);
                            noReservations.setVisibility(View.VISIBLE);
                            spinner3.setVisibility(View.GONE);
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
                        spinner3.setAdapter(adapter);

                        spinner3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
                                cancelButton.setVisibility(View.VISIBLE);
                                DocumentReference userDataReference = (DocumentReference) reservations.get(position).get("user");
                                userDataReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        userData = task.getResult();
                                        userID.setText("ID: " + userData.getId());
                                        Log.d("tag", userData.getId());
                                        name.setText("Name: " + userData.get("name"));
                                        surname.setText("Surname: " + userData.get("surname"));
                                    }
                                });
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {
                                return;
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
                        spinner3.setAdapter(adapter);
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
