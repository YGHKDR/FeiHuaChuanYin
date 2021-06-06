package com.example.feihuachuanyindemo1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.huawei.hms.mlplugin.asr.MLAsrCaptureActivity;
import com.huawei.hms.mlplugin.asr.MLAsrCaptureConstants;
import com.huawei.hms.mlsdk.asr.MLAsrConstants;
import com.huawei.hms.mlsdk.common.MLApplication;

import java.io.IOException;

public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        MLApplication.getInstance().setApiKey("CgB6e3x9mayWRR3ygKmcyllptWyQxQ7SEo7k6Jaf/yqPxWYMDr4Z11FuMu+b+BEKQ1ILYIONwXActM0Ozm0YHXnM");
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, 1);
    }
    public void recognize(View view){
        //语音识别令词
        Intent intent = new Intent(this, MLAsrCaptureActivity.class)
                .putExtra(MLAsrCaptureConstants.LANGUAGE, "zh-CN")
                .putExtra(MLAsrCaptureConstants.FEATURE, MLAsrCaptureConstants.FEATURE_WORDFLUX)
                .putExtra(MLAsrConstants.SCENES, MLAsrConstants.SCENES_SHOPPING);
        startActivityForResult(intent, 100);
    }
    public void setting(View view){
        Intent intent=new Intent(StartActivity.this,SettingActivity.class);
        startActivity(intent);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String text = "";
        if (requestCode == 100) {
            switch (resultCode) {
                case MLAsrCaptureConstants.ASR_SUCCESS:
                    if (data != null) {
                        Bundle bundle = data.getExtras();
                        if (bundle != null && bundle.containsKey(MLAsrCaptureConstants.ASR_RESULT)) {
                            text = bundle.getString(MLAsrCaptureConstants.ASR_RESULT);
                            TextView text1=findViewById(R.id.edit_text);
                            text1.setText(text);
                        }
                    }
                    break;
                case MLAsrCaptureConstants.ASR_FAILURE:
                    if(data != null) {
                        String msg="";
                        Bundle bundle = data.getExtras();
                        if(bundle != null && bundle.containsKey(MLAsrCaptureConstants.ASR_ERROR_CODE)) {
                            int errorCode = bundle.getInt(MLAsrCaptureConstants.ASR_ERROR_CODE);
                            Log.d("StartActivity", String.valueOf(errorCode));
                        }
                        if(bundle != null && bundle.containsKey(MLAsrCaptureConstants.ASR_ERROR_MESSAGE)){
                            String errorMsg = bundle.getString(MLAsrCaptureConstants.ASR_ERROR_MESSAGE);
                            msg=errorMsg;
                            Log.d("StartActivity", errorMsg);
                        }
                        if(bundle != null && bundle.containsKey(MLAsrCaptureConstants.ASR_SUB_ERROR_CODE)) {
                            int subErrorCode = bundle.getInt(MLAsrCaptureConstants.ASR_SUB_ERROR_CODE);
                            Log.d("StartActivity", String.valueOf(subErrorCode));
                        }
                        Toast.makeText(StartActivity.this,msg,Toast.LENGTH_LONG).show();
                    }
                default:
                    break;
            }
        }
    }
    public void input(View view){
        //从词中识别出令词
        TextView text1=findViewById(R.id.edit_text);
        String text=text1.getText().toString();
        int temp=text.indexOf("的");
        String word=Character.toString(text.charAt(temp+1));
        Log.d("StartActivity", word);
        if(word==null||word.equals("")){
            Toast.makeText(StartActivity.this,"请输入令词",Toast.LENGTH_LONG).show();
        }else{
            sendRequestWithHttpClient(word);
            //Common.setPivot(word);
            Intent intent=new Intent(StartActivity.this,LangActivity.class);
            //intent.putExtra("word",word);
            startActivity(intent);
        }
    }
    private void sendRequestWithHttpClient(String pivot){
        OkHttpClient okHttpClient=new OkHttpClient();
        Request request=new Request.Builder().url("http://192.168.43.102:8000/ebrose/pivot?pivot="+pivot).method("GET",null).build();
        Call call=okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Looper.prepare();
                Toast.makeText(StartActivity.this,"请求服务1失败",Toast.LENGTH_LONG).show();
                Log.d("StartActivity",e.getMessage());
                Looper.loop();
                return;
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseData=response.body().string();
                Gson gson=new Gson();
                PivotView pivotView=gson.fromJson(responseData,PivotView.class);
                String token=pivotView.getToken();
                Common.setToken(token);
                //String msg=jo.get("msg").getAsString();
                Log.d("StartActivity","令词传递成功");

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //对UI进行操作
                    }
                });
            }
        });
    }
}