package com.htw.smartloadapp.webservice;

public class RestWebserviceSettings {
    // all necessary settings will be listed here

    static String BASE_URL = ""; //"https://rs.bss-bln.de/";

    public static String getBaseUrl() {
        return BASE_URL;
    }

    public static void setBaseUrl(String baseUrl) {
        BASE_URL = baseUrl;
    }
}

