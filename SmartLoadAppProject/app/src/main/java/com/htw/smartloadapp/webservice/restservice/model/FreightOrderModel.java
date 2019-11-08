package com.htw.smartloadapp.webservice.restservice.model;

import android.util.Log;

import com.htw.smartloadapp.webservice.management.order.EnumFreightOrderStatus;

public class FreightOrderModel {

    // Class representing Webserver call: https://rs.bss-bln.de/

    private static final String TAG = "FreightOrderModel";
    
    // As string, to prevent type conversion errors
    private String freightOrderId;
    private String userId;
    private String companyName;
    private String description;
    private EnumFreightOrderStatus freightOrderStatus;
    private String timestamp;
    private String postalCode;
    private String country;
    private String city;
    private String streetName;
    private String streetNumber;

    private String signatureTagPacker;
    private String feedbackPacker;
    private String photoTagsPacker;

    private String signatureTagDriver;
    private String feedbackDriver;
    private String photoTagsDriver;


    public FreightOrderModel(String freightOrderId, String userId, String companyName, String description, EnumFreightOrderStatus freightOrderStatus, String timestamp, String postalCode, String country, String city, String streetName, String streetNumber, String signatureTagPacker, String feedbackPacker, String photoTagsPacker, String signatureTagDriver, String feedbackDriver, String photoTagsDriver) {
        this.freightOrderId = freightOrderId;
        this.userId = userId;
        this.companyName = companyName;
        this.description = description;
        this.freightOrderStatus = freightOrderStatus;
        this.timestamp = timestamp;
        this.postalCode = postalCode;
        this.country = country;
        this.city = city;
        this.streetName = streetName;
        this.streetNumber = streetNumber;
        this.signatureTagPacker = signatureTagPacker;
        this.feedbackPacker = feedbackPacker;
        this.photoTagsPacker = photoTagsPacker;
        this.signatureTagDriver = signatureTagDriver;
        this.feedbackDriver = feedbackDriver;
        this.photoTagsDriver = photoTagsDriver;
    }

    public String getFreightOrderId() {
        return freightOrderId;
    }

    public void setFreightOrderId(String freightOrderId) {
        this.freightOrderId = freightOrderId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public EnumFreightOrderStatus getFreightOrderStatus() {
        return freightOrderStatus;
    }

    public void setFreightOrderStatus(EnumFreightOrderStatus freightOrderStatus) {
        this.freightOrderStatus = freightOrderStatus;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStreetName() {
        return streetName;
    }

    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    public String getStreetNumber() {
        return streetNumber;
    }

    public void setStreetNumber(String streetNumber) {
        this.streetNumber = streetNumber;
    }

    public String getSignatureTagPacker() {
        return signatureTagPacker;
    }

    public void setSignatureTagPacker(String signatureTagPacker) {
        this.signatureTagPacker = signatureTagPacker;
    }

    public String getFeedbackPacker() {
        return feedbackPacker;
    }

    public void setFeedbackPacker(String feedbackPacker) {
        this.feedbackPacker = feedbackPacker;
    }

    public String getPhotoTagsPacker() {
        return photoTagsPacker;
    }

    public void setPhotoTagsPacker(String photoTagsPacker) {
        this.photoTagsPacker = photoTagsPacker;
    }

    public String getSignatureTagDriver() {
        return signatureTagDriver;
    }

    public void setSignatureTagDriver(String signatureTagDriver) {
        this.signatureTagDriver = signatureTagDriver;
    }

    public String getFeedbackDriver() {
        return feedbackDriver;
    }

    public void setFeedbackDriver(String feedbackDriver) {
        this.feedbackDriver = feedbackDriver;
    }

    public String getPhotoTagsDriver() {
        return photoTagsDriver;
    }

    public void setPhotoTagsDriver(String photoTagsDriver) {
        this.photoTagsDriver = photoTagsDriver;
    }

    public void addPhotoTagPacker(String photoTagPacker) {
        if(this.photoTagsPacker.equals("")){
            this.photoTagsPacker = photoTagPacker;
        }else{
            this.photoTagsPacker = this.photoTagsDriver + "," + photoTagPacker;
        }
    }
    public void deletePhotoTagPacker(String photoTagPacker){
        if(this.photoTagsPacker.contains(","+photoTagPacker)){
            this.photoTagsPacker = this.photoTagsPacker.replace(","+photoTagPacker, "");
        }else if(this.photoTagsPacker.contains(photoTagPacker)){
            this.photoTagsPacker = this.photoTagsPacker.replace(photoTagPacker, "");
        }else if(this.photoTagsPacker.contains(photoTagPacker+",")){
            this.photoTagsPacker = this.photoTagsPacker.replace(photoTagPacker+",", "");
        }
    }
    public void addPhotoTagDriver(String photoTagDriver) {
        if (this.photoTagsDriver.equals("")){
            this.photoTagsDriver = photoTagDriver;
        }else{
            this.photoTagsDriver = (String)(this.photoTagsDriver + ", "+ photoTagDriver);
        }
        Log.d(TAG, "ADD_TEST addPhotoTagDriver: " + this.photoTagsDriver);
    }
    public void deletePhotoTagDriver(String photoTagDriver){
        if(this.photoTagsDriver.contains(","+photoTagDriver)){
            this.photoTagsDriver = this.photoTagsDriver.replace(","+photoTagDriver, "");
        }else if(this.photoTagsDriver.contains(photoTagDriver)){
            this.photoTagsDriver = this.photoTagsDriver.replace(photoTagDriver, "");
        }else if(this.photoTagsDriver.contains(photoTagDriver+",")){
            this.photoTagsDriver = this.photoTagsDriver.replace(photoTagDriver+",", "");
        }
        Log.d(TAG, "DELETE_TEST addPhotoTagDriver: " + this.photoTagsDriver);
    }

    @Override
    public String toString() {
        return "\nFreight order freightOrderId: " + freightOrderId +
                "\nCompany name: " + companyName + "\n";
    }
}
