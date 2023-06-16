package com.example.greenhillhotel;


import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.concurrent.Executor;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class DatabaseConnectionTest {

    @Mock
    private FirebaseFirestore mockDb;

    @Mock
    private CollectionReference mockCollectionReference;

    @Mock
    private DocumentReference mockDocumentReference;

    @Mock
    private Task<DocumentSnapshot> mockTask;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        when(mockDb.collection("users")).thenReturn(mockCollectionReference);
        when(mockCollectionReference.document(any())).thenReturn(mockDocumentReference);
        when(mockDocumentReference.get()).thenReturn(mockTask);
    }

    @Test
    public void checkUserName() {
        String userId = "mgzPjFFK4yeJptBQTVV9e9yXiCd2";
        String expectedName = "chuj";

        DocumentSnapshot mockDocumentSnapshot = mock(DocumentSnapshot.class);
        when(mockTask.addOnSuccessListener(any())).thenAnswer(invocation -> {
            OnSuccessListener<DocumentSnapshot> listener = invocation.getArgument(0);
            listener.onSuccess(mockDocumentSnapshot);
            return mockTask;
        });

        when(mockDocumentSnapshot.exists()).thenReturn(true);
        when(mockDocumentSnapshot.getString("name")).thenReturn(expectedName);

        DatabaseConnection databaseConnection = new DatabaseConnection(mockDb);
        databaseConnection.checkUserName(userId);

        // Asercje na podstawie oczekiwanej nazwy użytkownika
        assertEquals(expectedName, databaseConnection.getUserName());
    }
}
