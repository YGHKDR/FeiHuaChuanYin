package com.example.feihuachuanyindemo1;

import android.Manifest;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class SettingActivity extends AppCompatActivity{
    private App_Data app_data;
    private CheckBox speakall;
    private CheckBox repeat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        app_data=(App_Data)getApplication();
        initView();
        initData();
    }
    //点击复选框时写入数据
    void initView(){
        speakall = (CheckBox) findViewById(R.id.SpeakAll);
        repeat=(CheckBox)findViewById(R.id.Repeat);
        speakall.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(speakall.isChecked()){
                    if(app_data.WriteStateSpeakallCheckbox(true)){
                        Log.d("SettingActivity","读出所有内容");
                    }

                }else{
                    if(app_data.WriteStateSpeakallCheckbox(false)){
                        Log.d("SettingActivity","不读出所有内容");
                    }
                }
            }
        });
        repeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(repeat.isChecked()){
                    if(app_data.WriteStateRepeatCheckbox(true)){
                        Log.d("SettingActivity","重复一次");
                    }
                }else{
                    if(app_data.WriteStateRepeatCheckbox(false)){
                        Log.d("SettingActivity","不重复一次");
                    }
                }
            }
        });
    }
    //读出数据，改写复选框
    void initData(){
        if(app_data.ReadStateSpeakallCheckbox()){
            speakall.setChecked(true);
            Log.d("SettingActivity","已开启：读出所有内容");
        }else{
            speakall.setChecked(false);
            Log.d("SettingActivity","未开启：读出所有内容");
        }

        if(app_data.ReadStateRepeatCheckbox()){
            repeat.setChecked(true);
            Log.d("SettingActivity","已开启：重复读一次");
        }else{
            repeat.setChecked(false);
            Log.d("SettingActivity","未开启：重复读一次");
        }
    }
}
