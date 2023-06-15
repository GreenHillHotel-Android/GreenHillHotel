package com.example.greenhillhotel.ui.book;

public class SearchBean {
    long roomid;
    String arrival;
    String departure;
    int people;
    boolean hasBalcony;

    public SearchBean(long roomid, String arrival, String departure, int people, boolean hasBalcony) {
        this.roomid = roomid;
        this.arrival = arrival;
        this.departure = departure;
        this.people = people;
        this.hasBalcony = hasBalcony;
    }
}
