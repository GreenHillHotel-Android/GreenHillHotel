package com.example.greenhillhotel;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.example.greenhillhotel.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.ktx.Firebase;

/**
 * MainActivity of the application.
 *
 * It is responsible for holding user session, creating proper fragments
 * and the whole appearance of the application.
 */
public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    private NavigationView navigationView;
    private InterstitialAd mInterstitialAd;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth auth;
    FirebaseUser user;
    TextView textView;

    /**
     * Method to get current user, show ads and build home page.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        textView = findViewById(R.id.textView);
        navigationView = binding.navView;


        setSupportActionBar(binding.appBarMain.toolbar);
        binding.appBarMain.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser currentUser = auth.getCurrentUser();
                if (currentUser != null) {
                    auth.signOut();
                    Toast.makeText(MainActivity.this, "Logged out", Toast.LENGTH_SHORT).show();
                    updateNavigationView(null, false);
                } else {
                    Toast.makeText(MainActivity.this, "Not logged in", Toast.LENGTH_SHORT).show();
                }
            }
        });
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(this, "ca-app-pub-3940256099942544/1033173712", adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        mInterstitialAd = interstitialAd;
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        mInterstitialAd = null;
                    }
                });


        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow, R.id.nav_login, R.id.nav_register, R.id.nav_admin_panel, R.id.nav_book)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        updateNavigationView(user != null ? user.getEmail() : null, false);

    }


    /**
     * Method invoked after creating options menu.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    /**
     * Method to show proper menu options based on users permissions.
     */
    public void updateNavigationView(String email, boolean isAdmin) {
        View headerView = navigationView.getHeaderView(0);
        TextView emailTextView = headerView.findViewById(R.id.textView);
        emailTextView.setText(email);

        NavigationView navigationView = findViewById(R.id.nav_view);
        Menu menu = navigationView.getMenu();

        // Wyświetl odpowiednie elementy dla zalogowanego użytkownika
        menu.findItem(R.id.nav_home).setVisible(true);
        menu.findItem(R.id.nav_gallery).setVisible(true);

        if (email != null) {
            // Użytkownik jest zalogowany
            menu.findItem(R.id.nav_login).setVisible(false);
            menu.findItem(R.id.nav_register).setVisible(false);

            // Aktualizuj widoczność panelu administratora na podstawie wartości isAdmin
            menu.findItem(R.id.nav_admin_panel).setVisible(isAdmin);
            menu.findItem(R.id.nav_gallery).setVisible(!isAdmin);
            menu.findItem(R.id.nav_book).setVisible(!isAdmin);
        } else {
            // Użytkownik jest wylogowany
            menu.findItem(R.id.nav_login).setVisible(true);
            menu.findItem(R.id.nav_register).setVisible(true);
            menu.findItem(R.id.nav_gallery).setVisible(false);
            menu.findItem(R.id.nav_admin_panel).setVisible(false);
            menu.findItem(R.id.nav_book).setVisible(false);
        }
    }

    /**
     * Method to get current user on application start
     * and check permissions to properly display
     * menu options.
     */
    @Override
    protected void onStart() {
        super.onStart();
        auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
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
                            updateNavigationView(currentUser.getEmail(), isAdmin);
                        }
                    }
                }
            });


        }
    }
}





