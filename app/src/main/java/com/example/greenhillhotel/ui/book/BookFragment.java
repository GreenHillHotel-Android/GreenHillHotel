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

import com.example.greenhillhotel.MainActivity;
import com.example.greenhillhotel.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;

public class BookFragment extends Fragment {

    EditText date;
    EditText date2;
    DatePickerDialog datePickerDialog;
    DatePickerDialog datePickerDialog2;
    private NumberPicker picker1;
    private String[] pickerVals;
    Button btnSearch;
    Switch hasBalconySwitch;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_book, container, false);
        date = (EditText) view.findViewById(R.id.date);
        date2 = (EditText) view.findViewById(R.id.date2);
        btnSearch = view.findViewById(R.id.btnSearch);
        hasBalconySwitch = view.findViewById(R.id.switch1);

        // perform click event on edit text
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR); // current year
                int mMonth = c.get(Calendar.MONTH); // current month
                int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
                // date picker dialog
                datePickerDialog = new DatePickerDialog(getActivity(),
                        new DatePickerDialog.OnDateSetListener() {

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
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR); // current year
                int mMonth = c.get(Calendar.MONTH); // current month
                int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
                // date picker dialog
                datePickerDialog2 = new DatePickerDialog(getActivity(),
                        new DatePickerDialog.OnDateSetListener() {

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
        picker1.setMaxValue(2);
        picker1.setMinValue(0);
        pickerVals  = new String[] {"1", "2", "3"};
        picker1.setDisplayedValues(pickerVals);
        picker1.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                int valuePicker1 = picker1.getValue();
                Log.d("picker value", pickerVals[valuePicker1]);
            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/mm/yyyy");
                try {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        String todayString = dateFormat.format(new Date());
                        Date today = dateFormat.parse(todayString);
                        Date accommodationDate = dateFormat.parse(String.valueOf(date.getText()));
                        Date departureDate = dateFormat.parse(String.valueOf(date2.getText()));

                        if (accommodationDate.compareTo(today) < 0 || departureDate.compareTo(accommodationDate) <= 0) {
                            Toast.makeText(getActivity(), "Wrong date entered!", Toast.LENGTH_SHORT).show();
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
