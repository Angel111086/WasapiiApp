package com.wasapii.adisoftin.model;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;



/**
 * Created by root on 14/6/17.
 */

public class MapClusterItem implements ClusterItem {

    String user_id="",name="",password="",contact_no="",gender="",dob="",
            user_interests="",login_status="",email="",signup_type="",
            profile_img="",device_type="",user_timestamp="",venu_name="",venu_lat="",venu_long="",
            distance="";
    double user_lat,user_long;
    private final LatLng mPosition;
    //public final int marker_icon;
    BitmapDescriptor marker_icon;




    public MapClusterItem(BitmapDescriptor marker_icon, String user_id, String name, String password, String contact_no, String gender, String dob, String user_interests, String login_status, String email, String signup_type, String profile_img, String device_type, double user_lat, double user_long, String user_timestamp, String venu_name, String venu_lat, String venu_long, String distance) {
        mPosition = new LatLng(user_lat, user_long);
        this.marker_icon = marker_icon;
        this.user_id = user_id;
        this.name = name;
        this.password = password;
        this.contact_no = contact_no;
        this.gender = gender;
        this.dob = dob;
        this.user_interests = user_interests;
        this.login_status = login_status;
        this.email = email;
        this.signup_type = signup_type;
        this.profile_img = profile_img;
        this.device_type = device_type;
        this.user_lat = user_lat;
        this.user_long = user_long;
        this.user_timestamp = user_timestamp;
        this.venu_name = venu_name;
        this.venu_lat = venu_lat;
        this.venu_long = venu_long;
        this.distance = distance;
    }
    @Override
    public LatLng getPosition() {
        return mPosition;
    }


    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getContact_no() {
        return contact_no;
    }

    public void setContact_no(String contact_no) {
        this.contact_no = contact_no;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getUser_interests() {
        return user_interests;
    }

    public void setUser_interests(String user_interests) {
        this.user_interests = user_interests;
    }

    public String getLogin_status() {
        return login_status;
    }

    public void setLogin_status(String login_status) {
        this.login_status = login_status;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSignup_type() {
        return signup_type;
    }

    public void setSignup_type(String signup_type) {
        this.signup_type = signup_type;
    }

    public String getProfile_img() {
        return profile_img;
    }

    public void setProfile_img(String profile_img) {
        this.profile_img = profile_img;
    }

    public String getDevice_type() {
        return device_type;
    }

    public void setDevice_type(String device_type) {
        this.device_type = device_type;
    }

    public double getUser_lat() {
        return user_lat;
    }

    public void setUser_lat(double user_lat) {
        this.user_lat = user_lat;
    }

    public double getUser_long() {
        return user_long;
    }

    public void setUser_long(double user_long) {
        this.user_long = user_long;
    }

    public String getUser_timestamp() {
        return user_timestamp;
    }

    public void setUser_timestamp(String user_timestamp) {
        this.user_timestamp = user_timestamp;
    }

    public String getVenu_name() {
        return venu_name;
    }

    public void setVenu_name(String venu_name) {
        this.venu_name = venu_name;
    }

    public String getVenu_lat() {
        return venu_lat;
    }

    public void setVenu_lat(String venu_lat) {
        this.venu_lat = venu_lat;
    }

    public String getVenu_long() {
        return venu_long;
    }

    public void setVenu_long(String venu_long) {
        this.venu_long = venu_long;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }




   /* public LatLng getmPosition() {
        return mPosition;
    }*/

    public BitmapDescriptor getMarker_icon() {
        return marker_icon;
    }
}
