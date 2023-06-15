package com.example.greenhillhotel.ui.book;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.greenhillhotel.R;

import java.util.ArrayList;
import java.util.Arrays;


public class ConfigureFragment extends Fragment {

    private Spinner spinner ;
    private ArrayAdapter<String> adapter ;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_configure, container, false);
        Spinner spinner = view.findViewById(R.id.spinner);

        String[] items = new String[]{"1", "2", "3"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, items);
//set the spinners adapter to the previously created one.
        spinner.setAdapter(adapter);


        return view;
    }
}
