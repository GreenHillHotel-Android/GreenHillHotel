package com.example.greenhillhotel.ui.book;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.greenhillhotel.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


public class ConfigureFragment extends Fragment {

    private AlarmManager alarmManager;
    private PendingIntent alarmIntent;
    private Spinner spinner ;
    private ArrayAdapter<String> adapter ;
    TextView roomNumber;
    TextView arrival;
    TextView departure;
    TextView people;
    TextView balcony;
    Switch tv;
    String[] items;
    Button confirmButton;
    SearchBean searchData;
    SimpleDateFormat dateFormat;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_configure, container, false);
        Spinner spinner = view.findViewById(R.id.spinner);
        roomNumber = view.findViewById(R.id.room_number);
        arrival = view.findViewById(R.id.arrival1);
        departure = view.findViewById(R.id.departure1);
        people = view.findViewById(R.id.people1);
        balcony = view.findViewById(R.id.balcony);
        tv = view.findViewById(R.id.switch2);
        confirmButton = view.findViewById(R.id.buttonConfirm);
        alarmManager = (AlarmManager) requireContext().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(requireContext(), AlarmReceiver.class);
        alarmIntent = PendingIntent.getBroadcast(requireContext(), 0, intent, 0);

        getParentFragmentManager().setFragmentResultListener("requestSearch", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                searchData = (SearchBean) result.getSerializable("searchData");
                roomNumber.append(Long.toString((Long) searchData.room.get("id")));
                arrival.append(dateFormat.format(searchData.arrival));
                departure.append(dateFormat.format(searchData.departure));
                people.append(Long.toString(searchData.people));
                balcony.append(Boolean.toString(searchData.hasBalcony));
                if (searchData.people == 1) {
                    items = new String[]{"1"};
                }
                else if (searchData.people == 2) {
                    items = new String[]{"1+1", "2"};
                }
                else {
                    items = new String[]{"1+1+1", "2+1"};
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, items);
                spinner.setAdapter(adapter);
            }
        });

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                FirebaseUser user = mAuth.getCurrentUser();
                String bedConfig = spinner.getSelectedItem().toString();
                boolean isTv = tv.isChecked();
                Map<String, Object> reservationData = new HashMap<>();
                reservationData.put("arrival", searchData.arrival);
                reservationData.put("departure", searchData.departure);
                reservationData.put("bedConfig", bedConfig);
                reservationData.put("room", searchData.room.getReference());
                reservationData.put("tv", isTv);
                db.collection("users").document(user.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        reservationData.put("user", task.getResult().getReference());
                        db.collection("reservations").document().set(reservationData).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(getActivity(), "Success.", Toast.LENGTH_SHORT).show();
                                NavController navController = Navigation.findNavController(v);
                                navController.navigate(R.id.nav_home);
                                setNotification();
                                showAlarmSetNotification();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getActivity(), "Failed to book a room.", Toast.LENGTH_SHORT).show();
                                NavController navController = Navigation.findNavController(v);
                                navController.navigate(R.id.nav_home);
                            }
                        });
                    }
                });

            }
        });


        return view;
    }
    private void setNotification() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(searchData.arrival);
        calendar.add(Calendar.DAY_OF_MONTH, -1);

        long notificationTime = calendar.getTimeInMillis();

        alarmManager.set(AlarmManager.RTC_WAKEUP, notificationTime, alarmIntent);
    }

    private void showAlarmSetNotification() {
        String notificationText = "Alarm set for " + searchData.arrival.toString(); // Określ treść powiadomienia

        NotificationManager notificationManager = (NotificationManager) requireContext().getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Utwórz kanał powiadomień dla Androida 8.0 (API 26) i nowszych wersji
            NotificationChannel channel = new NotificationChannel("AlarmSetChannel", "Alarm Set Channel", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(requireContext(), "AlarmSetChannel")
                .setSmallIcon(R.drawable.ic_menu_gallery)
                .setContentTitle("Alarm Set")
                .setContentText(notificationText)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        notificationManager.notify(0, builder.build());
    }
}
