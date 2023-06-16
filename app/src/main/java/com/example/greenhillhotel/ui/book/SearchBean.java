package com.example.greenhillhotel.ui.book;

import com.google.firebase.firestore.DocumentSnapshot;

import java.io.Serializable;
import java.util.Date;

/**
 * Data to pass from BookFragment to ConfigureFragment
 *
 * @see BookFragment
 * @see ConfigureFragment
 */
public class SearchBean implements Serializable {
    DocumentSnapshot room;
    Date arrival;
    Date departure;
    int people;
    boolean hasBalcony;

    /**
     *
     * @param room room reference from firebase
     * @param arrival date of arrival
     * @param departure date of departure
     * @param people number of people
     * @param hasBalcony if room should have a balcony
     */
    public SearchBean(DocumentSnapshot room, Date arrival, Date departure, int people, boolean hasBalcony) {
        this.room = room;
        this.arrival = arrival;
        this.departure = departure;
        this.people = people;
        this.hasBalcony = hasBalcony;
    }
}
