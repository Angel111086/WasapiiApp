package com.wasapii.adisoftin.model;

/**
 * Created by adisfot5 on 29/3/17.
 */
public class SearchUser {

    String id="",name="",image="";

    public SearchUser(String id, String name , String image){
        this.id=id;
        this.name=name;
        this.image=image;
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
}
