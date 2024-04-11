package com.wasapii.adisoftin.model;

/**
 * Created by adisfot5 on 5/4/17.
 */
public class User {

    String name="",dob="",gender="",interests="",profileImgUrl="",img1="",img2="",img3="";

    public User(String name , String dob, String gender,String interests,String imageUrl,String img1 , String img2 , String img3){

        this.name = name;
        this.gender = gender;
        this.interests = interests;
        this.dob = dob;
        profileImgUrl = imageUrl;
        this.img1 = img1;
        this.img2 = img2;
        this.img3 = img3;
    }

    public String getImg1() {
        return img1;
    }

    public void setImg1(String img1) {
        this.img1 = img1;
    }

    public String getImg2() {
        return img2;
    }

    public void setImg2(String img2) {
        this.img2 = img2;
    }

    public String getImg3() {
        return img3;
    }

    public void setImg3(String img3) {
        this.img3 = img3;
    }

    public String getProfileImgUrl() {
        return profileImgUrl;
    }

    public void setProfileImgUrl(String profileImgUrl) {
        this.profileImgUrl = profileImgUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getInterests() {
        return interests;
    }

    public void setInterests(String interests) {
        this.interests = interests;
    }
}
