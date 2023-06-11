package com.example.greenhillhotel.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.example.greenhillhotel.ui.registration.RegistrationFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginFragment extends Fragment {

    FirebaseAuth mAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            NavController navController = Navigation.findNavController(requireView());
            navController.navigate(R.id.nav_home);
            Toast.makeText(getActivity(), "You are already logged in!", Toast.LENGTH_SHORT).show();
        }
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        mAuth = FirebaseAuth.getInstance();
        EditText emailEditText = view.findViewById(R.id.email);
        EditText passwordEditText = view.findViewById(R.id.password);
        Button loginButton = view.findViewById(R.id.login);
        ProgressBar progressBar = view.findViewById(R.id.progressBar);
        TextView textView = view.findViewById(R.id.registerNow);
        FirebaseUser currentUser = mAuth.getCurrentUser();

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavController navController = Navigation.findNavController(v);
                navController.navigate(R.id.nav_register);
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                String email, password;
                email = String.valueOf(emailEditText.getText());
                password = String.valueOf(passwordEditText.getText());

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getActivity(), "Wprowadz email", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getActivity(), "Wprowadz haslo", Toast.LENGTH_SHORT).show();
                    return;
                }
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);
                                if (task.isSuccessful()) {
                                    Toast.makeText(getContext(), "Login succesful", Toast.LENGTH_SHORT).show();
                                    FirebaseUser currentUser = mAuth.getCurrentUser();
                                    if (currentUser != null) {
                                        String uid = currentUser.getUid();
                                        DocumentReference userRef = db.collection("users").document(uid);
                                        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    DocumentSnapshot document = task.getResult();
                                                    if (document != null && document.exists()) {
                                                        boolean isAdmin = document.getBoolean("isAdmin");
                                                        MainActivity mainActivity = (MainActivity) getActivity();
                                                        mainActivity.updateNavigationView(currentUser.getEmail(), isAdmin);
                                                    }
                                                }
                                            }
                                        });
                                    }

                                    NavController navController = Navigation.findNavController(v);
                                    navController.navigate(R.id.nav_home);
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Toast.makeText(getActivity(), "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        return view;
    }
}
