package com.example.greenhillhotel.ui.registration;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.greenhillhotel.MainActivity;
import com.example.greenhillhotel.R;
import com.example.greenhillhotel.databinding.FragmentHomeBinding;
import com.example.greenhillhotel.ui.login.LoginFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class RegistrationFragment extends Fragment {

    FirebaseAuth mAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_registration, container, false);
        mAuth = FirebaseAuth.getInstance();
        EditText emailEditText = view.findViewById(R.id.email);
        EditText passwordEditText = view.findViewById(R.id.passwd);
        EditText nameEditText = view.findViewById(R.id.name);
        EditText surnameEditText = view.findViewById(R.id.surname);
        Button registerButton = view.findViewById(R.id.btnregister);
        ProgressBar progressBar = view.findViewById(R.id.progressbar);
        TextView textView = view.findViewById(R.id.loginNow);
        FirebaseUser currentUser = mAuth.getCurrentUser();

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavController navController = Navigation.findNavController(v);
                navController.navigate(R.id.nav_login);
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                String email, password, name, surname;
                boolean isAdmin = false;
                email = String.valueOf(emailEditText.getText());
                password = String.valueOf(passwordEditText.getText());
                name = String.valueOf(nameEditText.getText());
                surname = String.valueOf(surnameEditText.getText());

                if (TextUtils.isEmpty(email)){
                    Toast.makeText(getActivity(), "Wprowadz email", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)){
                    Toast.makeText(getActivity(), "Wprowadz haslo", Toast.LENGTH_SHORT).show();
                    return;
                }

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);
                                if (task.isSuccessful()) {
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    String uid = user.getUid();
                                    Toast.makeText(getActivity(), "Konto zostalo stworzone", Toast.LENGTH_SHORT).show();

                                    Map<String, Object> userData = new HashMap<>();
                                    userData.put("name", name);
                                    userData.put("surname", surname);
                                    userData.put("isAdmin", isAdmin);
                                    FirebaseUser currentUser = mAuth.getCurrentUser();
                                    if (currentUser != null) {
                                        MainActivity mainActivity = (MainActivity) getActivity();
                                        mainActivity.updateNavigationView(currentUser.getEmail(),false);
                                    }

                                    db.collection("users").document(uid)
                                            .set(userData)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Log.d(TAG, "DocumentSnapshot added with ID: " + uid);
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.w(TAG, "Error adding document", e);
                                                }
                                            });
                                    NavController navController = Navigation.findNavController(v);
                                    navController.navigate(R.id.nav_home);
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Toast.makeText(getActivity(), "Authentication failed.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });



        return view;
    }

}