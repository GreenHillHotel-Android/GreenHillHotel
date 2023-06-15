package com.example.greenhillhotel.ui.book;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.greenhillhotel.MainActivity;
import com.example.greenhillhotel.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
import java.util.Map;

/**
 * Fragment that is responsible for searching for an available room.
 */
public class BookFragment extends Fragment {

    EditText date;
    EditText date2;
    DatePickerDialog datePickerDialog;
    DatePickerDialog datePickerDialog2;
    private NumberPicker picker1;
    private String[] pickerVals;
    Button btnSearch;
    Switch hasBalconySwitch;
    List<DocumentSnapshot> rooms = new ArrayList<>();
    List<DocumentSnapshot> reservedRooms = new ArrayList<>();
    DocumentSnapshot availableRoom;
    SimpleDateFormat dateFormat;
    SimpleDateFormat todayFormat;
    /**
     * Method to implement search panel and its functionality.
     *
     * It is responsible for collecting user input,
     * validation of the input and passing it to the configuration fragment.
     *
     * @see ConfigureFragment
     */
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_book, container, false);
        date = (EditText) view.findViewById(R.id.date);
        date2 = (EditText) view.findViewById(R.id.date2);
        btnSearch = view.findViewById(R.id.btnSearch);
        hasBalconySwitch = view.findViewById(R.id.switch1);

        // perform click event on edit text
        date.setOnClickListener(new View.OnClickListener() {
            /**
             * Method to get the user calendar input.
             *
             * It is responsible for getting the arrival date.
             */
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR); // current year
                int mMonth = c.get(Calendar.MONTH); // current month
                int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
                // date picker dialog
                datePickerDialog = new DatePickerDialog(getActivity(),
                        new DatePickerDialog.OnDateSetListener() {

                            /**
                             * Method to set the text value of the arrival date.
                             */
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                // set day of month , month and year value in the edit text
                                date.setText(dayOfMonth + "/"
                                        + (monthOfYear + 1) + "/" + year);

                            }
                        }, mYear, mMonth, mDay);

                datePickerDialog.show();
                datePickerDialog.getDatePicker().setDescendantFocusability(DatePicker.FOCUS_BLOCK_DESCENDANTS);

            }
        });
        date2.setOnClickListener(new View.OnClickListener() {
            /**
             * Method to get the user calendar input.
             *
             * It is responsible for getting the departure date.
             */
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR); // current year
                int mMonth = c.get(Calendar.MONTH); // current month
                int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
                // date picker dialog
                datePickerDialog2 = new DatePickerDialog(getActivity(),
                        new DatePickerDialog.OnDateSetListener() {
                            /**
                             * Method to set the text value of the departure date.
                             */
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                // set day of month , month and year value in the edit text
                                date2.setText(dayOfMonth + "/"
                                        + (monthOfYear + 1) + "/" + year);

                            }
                        }, mYear, mMonth, mDay);

                datePickerDialog2.show();
                datePickerDialog2.getDatePicker().setDescendantFocusability(DatePicker.FOCUS_BLOCK_DESCENDANTS);

            }
        });

        picker1 = view.findViewById(R.id.picker);
        picker1.setMaxValue(3);
        picker1.setMinValue(1);
        pickerVals  = new String[] {"1", "2", "3"};
        picker1.setDisplayedValues(pickerVals);
        picker1.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            /**
             * Listener responsible for handling the change of
             * people count value.
             */
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                int valuePicker1 = picker1.getValue();
                Log.d("picker value", pickerVals[valuePicker1-1]);
            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            /**
             * Method to search for an available room
             * after pressing the search button.
             *
             * It  is responsible for input validation and getting the room data from firebase.
             */
            @Override
            public void onClick(View v) {
                dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                todayFormat = new SimpleDateFormat(("yyyy-MM-dd"));
                try {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        Date today = todayFormat.parse(LocalDate.now().toString());
                        Log.d("tag", String.valueOf(today));
                        Date accommodationDate = dateFormat.parse(String.valueOf(date.getText()));
                        Date departureDate = dateFormat.parse(String.valueOf(date2.getText()));

                        if (accommodationDate.compareTo(today) < 0 || departureDate.compareTo(accommodationDate) <= 0) {
                            Toast.makeText(getActivity(), "Wrong date entered!", Toast.LENGTH_SHORT).show();
                        } else {
                            boolean hasBalcony = hasBalconySwitch.isChecked();
                            int people = picker1.getValue();


                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            db.collection("rooms")
                                    .whereEqualTo("capacity", people)
                                    .whereEqualTo("isBalcony", hasBalcony)
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            rooms = task.getResult().getDocuments();
                                            db.collection("reservations")
                                                    .whereGreaterThan("departure", accommodationDate)
                                                    .get()
                                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                            QuerySnapshot querySnapshot = task.getResult();
                                                            List<DocumentSnapshot> documents = querySnapshot.getDocuments();
                                                            for (DocumentSnapshot snapshot : documents) {
                                                                if (snapshot.getTimestamp("arrival").toDate().before(departureDate)) {
                                                                    reservedRooms.add(snapshot);
                                                                }
                                                            }
                                                            for (DocumentSnapshot reservedRoom : reservedRooms) {
                                                                rooms.remove(reservedRoom.get("roomid"));
                                                            }
                                                            if (rooms.size() > 0) {
                                                                availableRoom = rooms.get(0);
                                                                SearchBean searchData = new SearchBean(
                                                                        availableRoom,
                                                                        accommodationDate,
                                                                        departureDate,
                                                                        people,
                                                                        hasBalcony
                                                                );

                                                                Bundle result = new Bundle();
                                                                result.putSerializable("searchData", searchData);
                                                                getParentFragmentManager().setFragmentResult("requestSearch", result);
                                                                NavController navController = Navigation.findNavController(v);
                                                                navController.navigate(R.id.nav_search);
                                                            }
                                                        }
                                                    });
                                        }
                                    });
                        }
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });
        return view;
    }
}
