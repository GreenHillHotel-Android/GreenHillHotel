package com.example.greenhillhotel.ui.book;

import com.google.firebase.firestore.DocumentSnapshot;

import java.io.Serializable;
import java.util.Date;

public class SearchBean implements Serializable {
    DocumentSnapshot room;
    Date arrival;
    Date departure;
    int people;
    boolean hasBalcony;

    public SearchBean(DocumentSnapshot room, Date arrival, Date departure, int people, boolean hasBalcony) {
        this.room = room;
        this.arrival = arrival;
        this.departure = departure;
        this.people = people;
        this.hasBalcony = hasBalcony;
    }
}
