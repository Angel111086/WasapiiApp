package com.wasapii.adisoftin.model;

/**
 * Created by adisfot5 on 1/3/17.
 */
public class Venue {

    String name="";
    String address="";
    String contact="";
    String distance="";
    String photoUrl;
    String latitude = "";

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    String longitude = "";



    public Venue(String name , String address , String contact, String distance,String photoUrl, String latitude, String longitude){

        this.name=name;
        this.address=address;
        this.contact=contact;
        this.distance=distance;
        this.photoUrl=photoUrl;
        this.latitude=latitude;
        this.longitude=longitude;

    }

    public String getAddress() {
        return address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
}
