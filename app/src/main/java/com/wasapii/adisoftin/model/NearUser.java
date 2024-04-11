package com.wasapii.adisoftin.model;

/**
 * Created by adisfot5 on 29/3/17.
 */
public class NearUser {

    String name="",venueName="", distance="",photoUrl,id;

    public NearUser(){}
    public  NearUser(String name , String venueName , String distance , String photoUrl , String id){

        this.name=name;
        this.venueName = venueName;
        this.distance=distance;
        this.photoUrl = photoUrl;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVenueName() {
        return venueName;
    }

    public void setVenueName(String venueName) {
        this.venueName = venueName;
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
