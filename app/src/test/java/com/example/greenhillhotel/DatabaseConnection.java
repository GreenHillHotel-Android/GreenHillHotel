package com.example.greenhillhotel;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class DatabaseConnection {
    private FirebaseFirestore db;
    private String userName;

    public DatabaseConnection(FirebaseFirestore db) {
        this.db = db;
    }

    public void checkUserName(String userId) {
        db.collection("users").document(userId).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            userName = documentSnapshot.getString("name");
                        }
                    }
                });
    }

    public String getUserName() {
        return userName;
    }
}