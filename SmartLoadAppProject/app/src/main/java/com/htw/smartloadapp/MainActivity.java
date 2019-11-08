package com.htw.smartloadapp;

import android.content.Context;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.htw.smartloadapp.webservice.RestWebserviceSettings;
import com.htw.smartloadapp.webservice.download.data.DownloadProgressResponseBody;
import com.htw.smartloadapp.webservice.login.EnumUserRole;
import com.htw.smartloadapp.webservice.login.Login;
import com.htw.smartloadapp.webservice.management.order.SelectedFreightOrder;

import io.paperdb.Paper;



public class MainActivity<name> extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private  EditText etpassforgot;
    private EditText etName;
    private EditText etPassword;
    private Button btnLogin;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    private CheckBox cbLogin;
    private EditText etWebserverLink;
    public String userPassword;
    public String userID;
    Context context;
    private Menu menu;
    private Boolean saveLogin  ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //getPreferencesData();

        etName =(EditText)findViewById(R.id.etName);
        etPassword =(EditText)findViewById(R.id.etPassword);
        btnLogin =(Button)findViewById(R.id.btnLogin);
        etpassforgot =(EditText)findViewById(R.id.etforgotpassword);
        //Save Login
        cbLogin=(CheckBox) findViewById(R.id.cbLogin);
        etpassforgot.setInputType(InputType.TYPE_NULL);
        etWebserverLink = (EditText)findViewById(R.id.etWebserverLink);
        //etWebserverLink.setText("");
        Menu MnEnglish = findViewById(R.id.language_en);
        // calling the actual values of the Sharedpreferences
        sharedPreferences=getSharedPreferences(this.getResources().getString(R.string.Pref_login_preference),MODE_PRIVATE);
        editor=sharedPreferences.edit();
        saveLogin=sharedPreferences.getBoolean("saveLogin",false);
        if(saveLogin==true){
            etName.setText(sharedPreferences.getString("userID",null));
            etPassword.setText(sharedPreferences.getString("userPassword",null));
            cbLogin.setChecked(true);
        }
        else if (saveLogin==false){
            saveLogin=sharedPreferences.getBoolean("saveLogin",false);
            etName.setText("",null);
            etPassword.setText("",null);
        }
        etpassforgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displaymessage("Please contact your Employer!");
            }
        });
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Implementation of Login and Role Management with WebServer

                userID = etName.getText().toString();
                userPassword = etPassword.getText().toString();
                String webserverLink = etWebserverLink.getText().toString();
                RestWebserviceSettings.setBaseUrl(webserverLink);

                final Login myLogin = new Login(userID, userPassword);
                if (cbLogin.isChecked()){
                    // put the values on the Sharedpreferences values
                    editor.putString("userID",userID);
                    editor.putString("userPassword",userPassword);
                    editor.putBoolean("saveLogin",true);
                    editor.commit();
                }
                myLogin.validateCredentials(new DownloadProgressResponseBody.DownloadCallbacks() {
                    @Override
                    public void onProgressUpdate(long bytesRead, long contentLength, boolean done) {
                        Log.d(TAG, "onProgressUpdate");
                    }

                    @Override
                    public void onError() {
                        displaymessage("authentication with server failed, try again");

                    }

                    @Override
                    public void onFinish() {
                        EnumUserRole myEnumUserRole = myLogin.getUserRole();
                        if( myEnumUserRole == EnumUserRole.DRIVER){
                            // Intent MainActivity to Landing Page of Driver
                            Intent intent_main_lpDriver = new Intent(MainActivity.this, DriverActivityLandingPage.class);
                            // Bind userId to upcoming freightorder selection
                            SelectedFreightOrder.getInstance().setUserIdOfCurrentEditor(userID);
                            SelectedFreightOrder.getInstance().setUserRoleOfCurrentEditor(myEnumUserRole);
                            startActivity(intent_main_lpDriver);
                        }
                        else if(myEnumUserRole == EnumUserRole.PACKER) {
                            // Intent MainActivity to Landing Page of Packer
                            Intent intent_main_lpPacker = new Intent(MainActivity.this, PackerActivityLandingPage.class);

                            SelectedFreightOrder.getInstance().setUserIdOfCurrentEditor(userID);
                            SelectedFreightOrder.getInstance().setUserRoleOfCurrentEditor(myEnumUserRole);

                            startActivity(intent_main_lpPacker);
                        }else if(myEnumUserRole == EnumUserRole.NONE){
                            displaymessage("Please enter a valid ID or password!");
                        }else{
                            displaymessage("authentication with server failed, try again");
                        }
                    }
                });
            }
        });

    }
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase, "en"));
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        Paper.init(this);
        String language =Paper.book().read("language");
        if (language ==null)
            Paper.book().write("language","en");
        updateView((String)Paper.book().read("language"));
        MenuInflater inflater=getMenuInflater();
        getMenuInflater().inflate(R.menu.menumain_activity,menu);
        this.menu =menu;
        return true;

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.language_en){
            Paper.book().write("language","en");
            updateView((String)Paper.book().read("language"));
        }
        else if(item.getItemId()==R.id.language_de){
            Paper.book().write("language","de");

            updateView((String)Paper.book().read("language"));
        }
        return true;
    }
    @SuppressLint("ResourceType")//Resource for the MainActivity
    public void updateView(String lang) {
        Context context =LocaleHelper.setLocale(this,lang);
        Resources resources = context.getResources();
        btnLogin.setText(resources.getString(R.string.btnLogin));
        etPassword.setHint(resources.getString(R.string.etPassword));
        etpassforgot.setText(resources.getString(R.string.etforgotpassword));
        cbLogin.setText(resources.getString(R.string.cbLogin));
        etName.setHint(resources.getString(R.string.etName)); // still to think about
       /* menu.findItem(R.id.language_en).setTitle(resources.getString(R.string.language_en));
        menu.findItem(R.id.language_de).setTitle(resources.getString(R.string.language_de));*/
    }

    public static final int TIME_DELAY=2000;
    public static long back_pressed;
    @Override
    public void onBackPressed() {

        if(back_pressed+TIME_DELAY>System.currentTimeMillis()){
            Intent intent=new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }

        else {
            Toast.makeText(this,"Press once again to exit",Toast.LENGTH_SHORT).show();
        }

        back_pressed=System.currentTimeMillis();
    }

    public void displaymessage(String message){
        Toast.makeText(MainActivity.this,message, Toast.LENGTH_LONG).show();
    }



}
