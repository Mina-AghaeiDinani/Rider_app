package com.example.deliveryman;


public class LocationOfPlaces {
    //Property name must be the same as what we defined in real time database
    private Double lat,lng;


    public LocationOfPlaces() {
        //Constructor , it is needed
    }

    public LocationOfPlaces(Double lat, Double lng) {

        this.lat = lat;
        this.lng = lng;


    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }
}