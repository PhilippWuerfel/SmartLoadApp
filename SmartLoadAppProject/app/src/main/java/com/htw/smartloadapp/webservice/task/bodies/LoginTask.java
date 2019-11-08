package com.htw.smartloadapp.webservice.task.bodies;

public class LoginTask {
    private String userID;
    private String userPassword;

    public LoginTask(String userID, String userPassword) {
        this.userID = userID;
        this.userPassword = userPassword;
    }
}
