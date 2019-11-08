package com.htw.smartloadapp.webservice.restservice.model;

import com.google.gson.annotations.SerializedName;

public class SmartLoadModel {

    // Class representing Webserver call: https://rs.bss-bln.de/api/SmartLoad
    // currently unused

    private String userId;

    private String passwrd;

    private String role;

    public String getUserId() {
        return userId;
    }

    public String getPasswrd() {
        return passwrd;
    }

    public String getRole() {
        return role;
    }
}
