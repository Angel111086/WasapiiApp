package com.wasapii.adisoftin.model;

/**
 * Created by root on 31/5/17.
 */

public class Chat {

    String id="",name="",image="",timestamp="";
    int type;

    public Chat(){}

    public Chat(String id, String name, String image, String timestamp, int type) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.timestamp = timestamp;
        this.type = type;
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
