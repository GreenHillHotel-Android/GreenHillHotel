package com.example.greenhillhotel;

import static android.content.ContentValues.TAG;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Class used only once to generate
 * rooms and put them into firebase.
 */
public class RoomCreator {
    /**
     * Method to generate 80 rooms with random attributes
     * and to put them into firebase with proper
     * fields and values.
     */
    public static void createRooms() {
        System.out.println("Inserting records into the table...");
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        for (int i = 1; i < 81; i++) {
            Integer room_capacity = ThreadLocalRandom.current().nextInt(1, 4);
            Boolean has_balcony = ThreadLocalRandom.current().nextBoolean();
            Map<String, Object> roomData = new HashMap<>();
            roomData.put("id", i);
            roomData.put("capacity", room_capacity);
            roomData.put("isBalcony", has_balcony);
            int finalI = i;
            db.collection("rooms").document(String.format("%02d", i))
                    .set(roomData)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        /**
                         * Method to log successful room creation.
                         */
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "DocumentSnapshot added with ID: " + finalI);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        /**
                         * Method to log unsuccessful room creation.
                         */
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error adding document", e);
                        }
                    });
        }
        System.out.println("Inserted records into the table...");
    }
}
