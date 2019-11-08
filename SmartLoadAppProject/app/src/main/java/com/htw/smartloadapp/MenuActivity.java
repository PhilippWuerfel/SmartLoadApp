package com.htw.smartloadapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.htw.smartloadapp.webservice.download.data.DownloadProgressResponseBody;
import com.htw.smartloadapp.webservice.login.EnumUserRole;
import com.htw.smartloadapp.webservice.management.order.EnumFreightOrderStatus;
import com.htw.smartloadapp.webservice.management.order.SelectedFreightOrder;
import com.htw.smartloadapp.webservice.restservice.model.FreightOrderModel;
import com.htw.smartloadapp.webservice.task.bodies.FreightOrderStatusTask;
import com.htw.smartloadapp.webservice.upload.freightorder.RestFreightOrderStatusUpload;

import java.io.File;
import java.sql.Timestamp;

import io.paperdb.Paper;


//All activities that have menu inherit from this activity
public class MenuActivity extends AppCompatActivity {
    private static final String TAG = "MenuActivity";
    public SharedPreferences sharedPreferences,sharedPreferencesPhoto;
    public SharedPreferences.Editor editor,editorPhotoQuality;
    public Boolean saveLogin;
    private Menu menu;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // the Sharedpreferences has to be initialise
        sharedPreferences=getSharedPreferences(this.getResources().getString(R.string.Pref_login_preference),MODE_PRIVATE);
        editor=sharedPreferences.edit();
        sharedPreferencesPhoto=getSharedPreferences(this.getResources().getString(R.string.Pref_photo_quality),MODE_PRIVATE);
        editorPhotoQuality=sharedPreferencesPhoto.edit();
        Paper.init(this);
        String language =Paper.book().read("language");
        if (language ==null)
            Paper.book().write("language","en");
        updateView((String)Paper.book().read("language"));
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.options_menu,menu);
        this.menu =menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId()==R.id.actionEnglish){
            Paper.book().write("language","en");
            updateView((String)Paper.book().read("language"));
        }
        else if(item.getItemId()==R.id.actionDeutsch){
            Paper.book().write("language","de");
            updateView((String)Paper.book().read("language"));
        }

        else if(item.getItemId()==R.id.actionHigh){
            editorPhotoQuality.putInt("photo",100);
            editorPhotoQuality.commit();
        }
        else if(item.getItemId()==R.id.actionMedium){
            editorPhotoQuality.putInt("photo",50);
            editorPhotoQuality.commit();
        }
        else if(item.getItemId()==R.id.actionLow){
            editorPhotoQuality.putInt("photo",25);
            editorPhotoQuality.commit();
        }
        else if(item.getItemId()==R.id.actionLogOut){
            try{
                switch (SelectedFreightOrder.getInstance().getSelectedFreightOrder().getFreightOrderStatus()){
                    case ON_DELIVERY:
                        // Revert FreightOrderStatus back to READY_FOR_DELIVERY
                        revertUpdateFreightOrderStatus(EnumFreightOrderStatus.READY_FOR_DELIVERY);
                        break;
                    case ON_PACKING:
                        // Revert FreightOrderStatus back to BEFORE_PACKING
                        revertUpdateFreightOrderStatus(EnumFreightOrderStatus.BEFORE_PACKING);
                        break;
                    default:
                        editor.putString("",null);
                        editor.putString("",null);
                        editor.putBoolean("saveLogin",false);
                        editor.commit();
                        deleteAllFile();
                        Intent intent=new Intent(this,MainActivity.class);
                        startActivity(intent);

                }
            }catch(Exception e){
                Log.d(TAG, "onOptionsItemSelected: Cannot revert FreightOrder as it is null");
                editor.putString("",null);
                editor.putString("",null);
                editor.putBoolean("saveLogin",false);
                editor.commit();
                deleteAllFile();
                Intent intent=new Intent(this,MainActivity.class);
                startActivity(intent);
            }
        }
        return true;
    }
      /*  switch (item.getItemId()){
            case R.id.actionLogOut:
            {
                Intent intent=new Intent(this,MainActivity.class);
                startActivity(intent);
            }
            case R.id.actionDeutsch:{
                Paper.book().write("language","de");
                updateView((String)Paper.book().read("language"));
            }
            case R.id.actionEnglish:{
                Paper.book().write("language","en");
                updateView((String)Paper.book().read("language"));
            }
        }
        return true;*/

    private void revertUpdateFreightOrderStatus(EnumFreightOrderStatus enumFreightOrderStatus){
        try{
            FreightOrderModel selectedFreightOrder = SelectedFreightOrder.getInstance().getSelectedFreightOrder();
            // Update EnumFreightOrderStatus back to given enumFreightOrderStatus
            selectedFreightOrder.setFreightOrderStatus(enumFreightOrderStatus);
            selectedFreightOrder.setUserId(SelectedFreightOrder.getInstance().getUserIdOfCurrentEditor());
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            selectedFreightOrder.setTimestamp(String.valueOf(timestamp.getTime()));
            // Call to Webserver: Update EnumFreightOrderStatus
            // uploadFreightOrderStatus(String freightOrderId, EnumFreightOrderStatus freightOrderStatus, String userId, TimeStamp timeStamp)
            // Create Task for Upload
            FreightOrderStatusTask freightOrderStatusTask = new FreightOrderStatusTask(selectedFreightOrder.getFreightOrderId(), selectedFreightOrder.getFreightOrderStatus(), selectedFreightOrder.getUserId(), selectedFreightOrder.getTimestamp());
            // Call to Webserver: Upload FreightOrderStatus
            RestFreightOrderStatusUpload restFreightOrderStatusUpload = new RestFreightOrderStatusUpload(freightOrderStatusTask);
            restFreightOrderStatusUpload.uploadFreightOrderStatus(new DownloadProgressResponseBody.DownloadCallbacks() {
                @Override
                public void onProgressUpdate(long bytesRead, long contentLength, boolean done) {
                    Log.d(TAG, "onProgressUpdate");
                }

                @Override
                public void onError() {
                    Toast.makeText(MenuActivity.this, "Error reverting FreightOrderStatus", Toast.LENGTH_SHORT).show();
                    // whole update should be done again
                }

                @Override
                public void onFinish() {
                    Intent intent=new Intent(MenuActivity.this,MainActivity.class);
                    startActivity(intent);
                }
            });
        }catch(Exception e){
            Log.e(TAG, "revertUpdateFreightOrderStatus: " + e.getMessage() );
        }
    }

    @SuppressLint("ResourceType")
    public void updateView(String lang) {
        Context context =LocaleHelper.setLocale(this,lang);
        Resources resources = context.getResources();
        menu.findItem(R.id.actionDeutsch).setTitle(resources.getString(R.string.MnGerman));
        menu.findItem(R.id.actionEnglish).setTitle(resources.getString(R.string.MnEnglish));
        menu.findItem(R.id.subMenuLanguage).setTitle(resources.getString(R.string.subMenuLanguage));
        menu.findItem(R.id.actionLogOut).setTitle(resources.getString(R.string.actionLogOut));
        menu.findItem(R.id.subMenuPhotoResolution).setTitle(resources.getString(R.string.subMenuPhotoResolution));
        menu.findItem(R.id.actionHigh).setTitle(resources.getString(R.string.actionHigh));
        menu.findItem(R.id.actionMedium).setTitle(resources.getString(R.string.actionMedium));
        menu.findItem(R.id.actionLow).setTitle(resources.getString(R.string.actionLow));
    }
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase, "en"));
    }// To removed all the file  when you Log out that the the Memory could be free
    private void deleteAllFile() {

        File filepath = Environment.getExternalStorageDirectory();
        File folder = new File(filepath.getAbsolutePath() + "/SmartLoad/");
        File[] listOfFiles = folder.listFiles();
        try {
            for (int i = 0; i < listOfFiles.length; i++) {
                if (listOfFiles[i].isFile()) {
                    listOfFiles[i].delete();
                }
            }
        }
        catch (Exception e){
        }

    }
}
