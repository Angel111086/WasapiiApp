package com.wasapii.adisoftin.model;

/**
 * Created by root on 15/5/17.
 */

public class ChangePassword {

    String mobile,old_password,new_password,confirm_password;

    public ChangePassword(String mobile, String old_password, String new_password, String confirm_password) {
        this.mobile = mobile;
        this.old_password = old_password;
        this.new_password = new_password;
        this.confirm_password = confirm_password;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getOld_password() {
        return old_password;
    }

    public void setOld_password(String old_password) {
        this.old_password = old_password;
    }

    public String getNew_password() {
        return new_password;
    }

    public void setNew_password(String new_password) {
        this.new_password = new_password;
    }

    public String getConfirm_password() {
        return confirm_password;
    }

    public void setConfirm_password(String confirm_password) {
        this.confirm_password = confirm_password;
    }
}
