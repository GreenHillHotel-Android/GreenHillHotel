package com.example.greenhillhotel;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.greenhillhotel.ui.slideshow.MapFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.navigation.NavigationView;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class MainActivityUnitTest {
    @Test
    public void onCreate_NullUser_UpdatesNavigationView() {
        // Tworzenie scenariusza aktywności bez zalogowanego użytkownika
        ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class);
        scenario.onActivity(activity -> {
            // Sprawdzenie, czy widok nawigacji jest poprawnie zaktualizowany
            TextView emailTextView = activity.findViewById(R.id.textView);
            assertEquals("", emailTextView.getText().toString());

            NavigationView navigationView = activity.findViewById(R.id.nav_view);
            Menu menu = navigationView.getMenu();
            // Sprawdzenie, czy odpowiednie elementy menu są widoczne
            assertTrue(menu.findItem(R.id.nav_home).isVisible());
            assertFalse(menu.findItem(R.id.nav_gallery).isVisible());
            assertTrue(menu.findItem(R.id.nav_login).isVisible());
            assertTrue(menu.findItem(R.id.nav_register).isVisible());
            assertFalse(menu.findItem(R.id.nav_admin_panel).isVisible());
            assertFalse(menu.findItem(R.id.nav_book).isVisible());
        });
    }


    @Test
    public void testHomePage_ImageViewAndTextViewDisplayed() {
        ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class);
        scenario.onActivity(activity -> {
            // Sprawdzenie, czy obrazek i napis są widoczne na stronie głównej
            ImageView imageView = activity.findViewById(R.id.image);
            assertTrue(imageView.isShown());

            TextView textView = activity.findViewById(R.id.textHome);
            assertEquals("Green Hill Hotel", textView.getText().toString());
            assertTrue(textView.isShown());
        });
    }



}
