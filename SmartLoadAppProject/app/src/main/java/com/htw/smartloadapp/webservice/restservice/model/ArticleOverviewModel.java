package com.htw.smartloadapp.webservice.restservice.model;

import android.util.Log;

public class ArticleOverviewModel {

    // Class representing Webserver call: https://rs.bss-bln.de/

    private static final String TAG = "ArticleOverviewDummy";
    private String articleId;
    private String freightOrderId;
    private String articleName;
    private int articleAmount;
    private String articleUnitOfMeasure;
    private String articleLocation;
    private String articleShelfNumber;
    private boolean isChecked;

    public ArticleOverviewModel(String articleId, String freightOrderId, String articleName, int articleAmount, String articleUnitOfMeasure, String articleLocation, String articleShelfNumber) {
        this.articleId = articleId;
        this.freightOrderId = freightOrderId;
        this.articleName = articleName;
        this.articleAmount = articleAmount;
        this.articleUnitOfMeasure = articleUnitOfMeasure;
        this.articleLocation = articleLocation;
        this.articleShelfNumber = articleShelfNumber;
    }

    public String getArticleId() {
        return articleId;
    }

    public String getFreightOrderId() {
        return freightOrderId;
    }

    public void setFreightOrderId(String freightOrderId) {
        this.freightOrderId = freightOrderId;
    }

    public void setArticleId(String articleId) {
        this.articleId = articleId;
    }

    public String getArticleName() {
        return articleName;
    }

    public void setArticleName(String articleName) {
        this.articleName = articleName;
    }

    public int getArticleAmount() {
        return articleAmount;
    }

    public void setArticleAmount(int articleAmount) {
        this.articleAmount = articleAmount;
    }

    public String getArticleUnitOfMeasure() {
        return articleUnitOfMeasure;
    }

    public void setArticleUnitOfMeasure(String articleUnitOfMeasure) {
        this.articleUnitOfMeasure = articleUnitOfMeasure;
    }

    public String getArticleLocation() {
        return articleLocation;
    }

    public void setArticleLocation(String articleLocation) {
        this.articleLocation = articleLocation;
    }

    public String getArticleShelfNumber() {
        return articleShelfNumber;
    }

    public void setArticleShelfNumber(String articleShelfNumber) {
        this.articleShelfNumber = articleShelfNumber;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        Log.d(TAG, "setChecked: switch to:" + checked + "from id: " + articleId);
        isChecked = checked;
    }

    @Override
    public String toString() {
        return "article id = " + articleId +
                "\narticle name = " + articleName +
                "\narticle amount = " + articleAmount +
                "\narticle unit = " + articleUnitOfMeasure +
                "\narticle location = " + articleLocation +
                "\narticle shelf number = " + articleShelfNumber;
    }
}
