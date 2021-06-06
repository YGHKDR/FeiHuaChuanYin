package com.example.feihuachuanyindemo1;

import android.app.Application;
import android.content.SharedPreferences;

public class App_Data extends Application {
    private SharedPreferences.Editor checkEditor;
    private SharedPreferences sharedPreferences;
    private boolean isChecked_speakall,isChecked_repeat;

    @Override
    public void onCreate() {
        super.onCreate();
        initData();
    }

    private void initData(){
        sharedPreferences=getSharedPreferences("checkBox",MODE_PRIVATE);
        checkEditor=getSharedPreferences("checkBox",MODE_PRIVATE).edit();
    }

    public boolean ReadStateSpeakallCheckbox(){
        isChecked_speakall=sharedPreferences.getBoolean("isCheckedSpeakAll",true);
        return isChecked_speakall;
    }

    public boolean WriteStateSpeakallCheckbox(boolean bool){
        checkEditor.putBoolean("isCheckedSpeakAll",bool);
        return checkEditor.commit();
    }

    public boolean ReadStateRepeatCheckbox(){
        isChecked_repeat=sharedPreferences.getBoolean("isCheckedRepeat",true);
        return isChecked_repeat;
    }

    public boolean WriteStateRepeatCheckbox(boolean bool){
        checkEditor.putBoolean("isCheckedRepeat",bool);
        return checkEditor.commit();
    }
}
