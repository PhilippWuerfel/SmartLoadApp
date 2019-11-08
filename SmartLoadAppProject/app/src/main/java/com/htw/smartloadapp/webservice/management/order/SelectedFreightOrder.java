package com.htw.smartloadapp.webservice.management.order;

import android.graphics.Bitmap;
import android.util.Log;

import com.htw.smartloadapp.webservice.login.EnumUserRole;
import com.htw.smartloadapp.webservice.restservice.model.FreightOrderModel;

import java.io.File;
import java.util.ArrayList;

public class SelectedFreightOrder {
    private static final String TAG = "SelectedFreightOrder";

    private static SelectedFreightOrder selectedFreightOrder_instance = null;

    private FreightOrderModel selectedFreightOrder;
    private String userIdOfCurrentEditor;
    private EnumUserRole userRoleOfCurrentEditor;
    private ArrayList<File> uploadPhotoList;
    private ArrayList<File> photoListBeforeDelivery;
    private ArrayList<File> photoListAfterDelivery;
    private File uploadSignature;

    private Bitmap tempSignatureBitmap;
    private String tempSignatureCustomerName;

    private SelectedFreightOrder() {
    }

    public static SelectedFreightOrder getInstance(){
        if(selectedFreightOrder_instance == null){
            selectedFreightOrder_instance = new SelectedFreightOrder();
        }
        return selectedFreightOrder_instance;
    }

    public void setSelectedFreightOrder(FreightOrderModel selectedFreightOrder){
        this.selectedFreightOrder = selectedFreightOrder;
        // Reset uploadPhotoList and uploadSignature if already exists
        uploadPhotoList = null;
        photoListBeforeDelivery = null;
        photoListAfterDelivery = null;

        uploadSignature = null;
        // Reset temps for Signature
        tempSignatureBitmap = null;
        tempSignatureCustomerName = null;
    }

    public FreightOrderModel getSelectedFreightOrder() {
        return selectedFreightOrder;
    }

    public String getUserIdOfCurrentEditor() {
        return userIdOfCurrentEditor;
    }

    public void setUserIdOfCurrentEditor(String userIdOfCurrentEditor) {
        this.userIdOfCurrentEditor = userIdOfCurrentEditor;
    }

    public EnumUserRole getUserRoleOfCurrentEditor() {
        return userRoleOfCurrentEditor;
    }

    public void setUserRoleOfCurrentEditor(EnumUserRole userRoleOfCurrentEditor) {
        this.userRoleOfCurrentEditor = userRoleOfCurrentEditor;
    }
    public void addPhotoToUploadPhotoList(File file){
            if(uploadPhotoList == null){
                uploadPhotoList = new ArrayList<>();
            }
            uploadPhotoList.add(file);
        Log.d(TAG, "addPhotoToUploadPhotoList: " + uploadPhotoList);
    }
    public void deletePhotoFromUploadPhotoList(File file){
        if(uploadPhotoList.remove(file)){
            Log.d(TAG, "deletePhotoFromUploadPhotoList: " + file + "removed");
            // check if also file in other lists
            if(photoListBeforeDelivery != null){
                if(photoListBeforeDelivery.remove(file)){
                    Log.d(TAG, "deletePhotoFromUploadPhotoList: " + file + " also removed in photoListBeforeDelivery");
                }
            }
            if(photoListAfterDelivery != null){
                if(photoListAfterDelivery.remove(file)){
                    Log.d(TAG, "deletePhotoFromUploadPhotoList: " + file + " also removed in photoListAfterDelivery");
                }
            }

        }else{
            Log.d(TAG, "deletePhotoFromUploadPhotoList: couldn't find " + file);
        }
        Log.d(TAG, "deletePhotoFromUploadPhotoList: " + uploadPhotoList);
    }
    public ArrayList<File> getUploadPhotoList() {
        return uploadPhotoList;
    }

    // PhotoListBeforeDelivery

    public void addPhotoToPhotoListBeforeDelivery(File file){
        if(photoListBeforeDelivery == null){
            photoListBeforeDelivery = new ArrayList<>();
        }
        photoListBeforeDelivery.add(file);
    }
    public void deletePhotoFromPhotoListBeforeDelivery(File file){
        if(photoListBeforeDelivery.remove(file)){
            Log.d(TAG, "deletePhotoFromPhotoListBeforeDelivery: " + file + "removed");
        }else{
            Log.d(TAG, "deletePhotoFromPhotoListBeforeDelivery: couldn't find " + file);
        }
    }
    public ArrayList<File> getUploadPhotoListBeforeDelivery() {
        return photoListBeforeDelivery;
    }

    // PhotoListAfterDelivery

    public void addPhotoToPhotoListAfterDelivery(File file){
        if(photoListAfterDelivery == null){
            photoListAfterDelivery = new ArrayList<>();
        }
        photoListAfterDelivery.add(file);
    }
    public void deletePhotoFromPhotoListAfterDelivery(File file){
        if(photoListAfterDelivery.remove(file)){
            Log.d(TAG, "deletePhotoFromPhotoListAfterDelivery: " + file + "removed");
        }else{
            Log.d(TAG, "deletePhotoFromPhotoListAfterDelivery: couldn't find " + file);
        }
    }
    public ArrayList<File> getUploadPhotoListAfterDelivery() {
        return photoListAfterDelivery;
    }

    public File getUploadSignature() {
        if(uploadSignature == null){
            return null;
        }else{
            return uploadSignature;
        }
    }
    public void setUploadSignature(File uploadSignature) {
        Log.d(TAG, "setUploadSignature: adding Signature:" + uploadSignature);
        this.uploadSignature = uploadSignature;
    }

    public Bitmap getTempSignatureBitmap() {
        return tempSignatureBitmap;
    }

    public void setTempSignatureBitmap(Bitmap tempSignatureBitmap) {
        this.tempSignatureBitmap = tempSignatureBitmap;
    }

    public String getTempSignatureCustomerName() {
        return tempSignatureCustomerName;
    }

    public void setTempSignatureCustomerName(String tempSignatureCustomerName) {
        this.tempSignatureCustomerName = tempSignatureCustomerName;
    }
}
