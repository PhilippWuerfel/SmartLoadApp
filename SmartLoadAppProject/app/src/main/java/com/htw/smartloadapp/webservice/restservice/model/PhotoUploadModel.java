
package com.htw.smartloadapp.webservice.restservice.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PhotoUploadModel {

    // Class representing Webserver call: https://rs.bss-bln.de/api/Photoupload/upload

    @SerializedName("length")
    @Expose
    private Integer length;
    @SerializedName("name")
    @Expose
    private String name;

    public Integer getLength() {
        return length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
