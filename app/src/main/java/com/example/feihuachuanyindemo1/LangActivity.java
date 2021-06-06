package com.example.feihuachuanyindemo1;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.huawei.hms.mlplugin.asr.MLAsrCaptureActivity;
import com.huawei.hms.mlplugin.asr.MLAsrCaptureConstants;
import com.huawei.hms.mlsdk.asr.MLAsrConstants;
import com.huawei.hms.mlsdk.common.MLApplication;
import com.huawei.hms.mlsdk.tts.MLTtsAudioFragment;
import com.huawei.hms.mlsdk.tts.MLTtsCallback;
import com.huawei.hms.mlsdk.tts.MLTtsConfig;
import com.huawei.hms.mlsdk.tts.MLTtsConstants;
import com.huawei.hms.mlsdk.tts.MLTtsEngine;
import com.huawei.hms.mlsdk.tts.MLTtsError;
import com.huawei.hms.mlsdk.tts.MLTtsWarn;

import java.io.IOException;
import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LangActivity extends AppCompatActivity {
    // 当前使用语音编码为中文，发音人编码为女声。
    private static final String TTS_ZH_HANS = "zh-Hans";
    private static final String TTS_SPEAKER_MALE_ZH = "zh-Hans-st-1";
    private App_Data app_data;
    private MLTtsConfig mlTtsConfig;
    private MLTtsEngine mlTtsEngine;
    private MLTtsCallback callback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MLApplication.getInstance().setApiKey("CgB6e3x9mayWRR3ygKmcyllptWyQxQ7SEo7k6Jaf/yqPxWYMDr4Z11FuMu+b+BEKQ1ILYIONwXActM0Ozm0YHXnM");
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, 1);
        app_data=(App_Data)getApplication();

        // 使用自定义参数配置创建语音合成引擎。
        mlTtsConfig = new MLTtsConfig().setLanguage(TTS_ZH_HANS).setPerson(MLTtsConstants.TTS_SPEAKER_FEMALE_ZH).setSpeed(1.0f).setVolume(1.0f);
        mlTtsEngine = new MLTtsEngine(mlTtsConfig);
        // 设置内置播放器音量，取值范围：[0,100] dB(分贝)
        mlTtsEngine.setPlayerVolume(20);
        // 引擎运行中更新配置。
        mlTtsEngine.updateConfig(mlTtsConfig);
        callback = new MLTtsCallback() {
            @Override
            public void onError(String taskId, MLTtsError err) {
                // 语音合成失败处理。
                Toast.makeText(LangActivity.this,"语音合成失败",Toast.LENGTH_LONG).show();
            }
            @Override
            public void onWarn(String taskId, MLTtsWarn warn) {
                // 告警处理（不影响业务逻辑）。
                Log.d("LangActivity", taskId );
            }
            @Override
            // 返回当前播放分片和文本对应关系。start表示音频分片在输入文本中的起始位置，end表示音频分片在输入文本中的结束位置（不包含）。
            public void onRangeStart(String taskId, int start, int end) {
                // 当前播放分片和文本对应关系处理。
            }
            @Override
            // taskId 该音频对应的语音合成任务Id。
            // audioFragment 音频数据。
            // offset 一个语音合成任务会对应一个音频合成数据队列，该字段表示本次传输的音频分片在该队列中的偏移量。
            // range 本次传输的音频分片所在的文本区域，range.first为起始位置(包含的)，range.second为结束位置（不包含的）。
            public void onAudioAvailable(String taskId, MLTtsAudioFragment audioFragment, int offset, Pair<Integer, Integer> range,
                                         Bundle bundle){
                // Tts合成音频流回调接口，通过此接口将音频合成数据返回给App。
            }
            @Override
            public void onEvent(String taskId, int eventId, Bundle bundle) {
                // 合成事件回调方法。eventId为事件名称。
                switch (eventId) {
                    case MLTtsConstants.EVENT_PLAY_START:
                        // 播放开始回调。
                        break;
                    case MLTtsConstants.EVENT_PLAY_STOP:
                        // 播放停止回调。
                        boolean isInterrupted = bundle.getBoolean(MLTtsConstants.EVENT_PLAY_STOP_INTERRUPTED);
                        break;
                    case MLTtsConstants.EVENT_PLAY_RESUME:
                        // 播放恢复回调。
                        break;
                    case MLTtsConstants.EVENT_PLAY_PAUSE:
                        // 播放暂停回调。
                        break;


                    //以下回调事件类型是在不使用内部播放器播放，只关注合成音频数据时，需要关注的回调接口。
                    case MLTtsConstants.EVENT_SYNTHESIS_START:
                        // 语音合成开始的回调。
                        break;
                    case MLTtsConstants.EVENT_SYNTHESIS_END:
                        // 语音合成结束的回调。
                        break;
                    case MLTtsConstants.EVENT_SYNTHESIS_COMPLETE:
                        // 语音合成完成，同时合成的语音流全部传给App了。
                        isInterrupted = bundle.getBoolean(MLTtsConstants.EVENT_SYNTHESIS_INTERRUPTED);
                        break;
                    default:
                        break;
                }
            }
        };
        mlTtsEngine.setTtsCallback(callback);
    }

    public void go(View view) {
        // 通过intent进行识别设置。
        Intent intent = new Intent(this, MLAsrCaptureActivity.class)
                .putExtra(MLAsrCaptureConstants.LANGUAGE, "zh-CN")
                .putExtra(MLAsrCaptureConstants.FEATURE, MLAsrCaptureConstants.FEATURE_WORDFLUX)
                .putExtra(MLAsrConstants.SCENES, MLAsrConstants.SCENES_SHOPPING);
        startActivityForResult(intent, 100);

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
                            TextView textView1=findViewById(R.id.text_lang1);
                            textView1.setText(text);
                            String text1=text+"。";
                            //接收用户的句子，并判断
                            sendRequestWithHttpClientForReq(text1);
                            //后端给出句子，并合成语音
                            sendRequestWithHttpClientForRes();

                        }
                    }
                    break;
                case MLAsrCaptureConstants.ASR_FAILURE:
                    if(data != null) {
                        String msg="";
                        Bundle bundle = data.getExtras();
                        if(bundle != null && bundle.containsKey(MLAsrCaptureConstants.ASR_ERROR_CODE)) {
                            int errorCode = bundle.getInt(MLAsrCaptureConstants.ASR_ERROR_CODE);
                            Log.d("LangActivity", String.valueOf(errorCode));
                        }
                        if(bundle != null && bundle.containsKey(MLAsrCaptureConstants.ASR_ERROR_MESSAGE)){
                            String errorMsg = bundle.getString(MLAsrCaptureConstants.ASR_ERROR_MESSAGE);
                            msg=errorMsg;
                            Log.d("LangActivity", errorMsg);
                        }
                        if(bundle != null && bundle.containsKey(MLAsrCaptureConstants.ASR_SUB_ERROR_CODE)) {
                            int subErrorCode = bundle.getInt(MLAsrCaptureConstants.ASR_SUB_ERROR_CODE);
                            Log.d("LangActivity", String.valueOf(subErrorCode));
                        }
                        Toast.makeText(LangActivity.this,msg,Toast.LENGTH_LONG).show();
                    }
                default:
                    break;
            }
        }
    }

    private void sendRequestWithHttpClientForReq(String sentence){
        OkHttpClient okHttpClient=new OkHttpClient();
        String token=Common.getToken();
        Request request=new Request.Builder().url("http://192.168.43.102:8000/ebrose/asr?sentence="+sentence+"&token="+token).method("GET",null).build();
        Call call=okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Looper.prepare();
                Toast.makeText(LangActivity.this,"请求服务2失败",Toast.LENGTH_LONG).show();
                Looper.loop();
                return;
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseData=response.body().string();
                Log.d("LangActivityReq",responseData);
                Gson gson=new Gson();
                ResponseObject responseobject=gson.fromJson(responseData,ResponseObject.class);
                boolean status=responseobject.getStatus();
                if(status){   //如果服务端成功接收
                    Log.d("LangActivity","服务端成功接收诗句");

                    String author=responseobject.getAuthor();
                    String title=responseobject.getTitle();
                    String text=responseobject.getText();
                    tt1(author,title,text);

                    if(app_data.ReadStateSpeakallCheckbox()){
                        String detail=title+","+author+","+text;
                        if(app_data.ReadStateRepeatCheckbox()){
                            mlTtsEngine.speak(detail, MLTtsEngine.QUEUE_APPEND);
                            mlTtsEngine.speak(detail, MLTtsEngine.QUEUE_APPEND);
                        }else{
                            mlTtsEngine.speak(detail, MLTtsEngine.QUEUE_APPEND);
                        }
                    }else{
                        //不读
                    }
                }else{
                    String msg=responseobject.getMsg();
                    tt2(msg);
                }
            }
        });
    }

    public void tt1(final String author, final String title, final String text){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //对UI进行操作
                TextView textView2=findViewById(R.id.text_lang2);
                textView2.setText("作者："+author+"  标题："+title+"  诗句："+text);
            }
        });
    }

    public void tt2(final String msg){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //对UI进行操作
                TextView textView2=findViewById(R.id.text_lang2);
                textView2.setText(msg);
            }
        });
    }

    public void tt3(final String msg,final String author, final String title, final String text){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //对UI进行操作
                TextView textView3=findViewById(R.id.text_back);
                textView3.setText(msg+"作者："+author+"  标题："+title+"  诗句："+text);
            }
        });
    }

    public void tt4(final String msg){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //对UI进行操作
                TextView textView3=findViewById(R.id.text_back);
                textView3.setText(msg);
            }
        });
    }

    private void sendRequestWithHttpClientForRes(){
        OkHttpClient okHttpClient=new OkHttpClient();
        String token=Common.getToken();
        Request request=new Request.Builder().url("http://192.168.43.102:8000/ebrose/tts?token="+token).method("GET",null).build();
        Call call=okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Looper.prepare();
                Toast.makeText(LangActivity.this,"请求服务3失败",Toast.LENGTH_LONG).show();
                Log.d("LangActivity",e.getMessage());
                Looper.loop();
                return;
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseData=response.body().string();
                Log.d("LangActivityRes",responseData);
                Gson gson=new Gson();
                ResponseObject responseobject=gson.fromJson(responseData,ResponseObject.class);
                boolean status=responseobject.getStatus();
                if(status){   //如果服务端成功接收
                    Log.d("LangActivity","服务端成功发送诗句");
                    String author=responseobject.getAuthor();
                    String title=responseobject.getTitle();
                    String text=responseobject.getText();
                    String msg=responseobject.getMsg();
                    tt3(msg,author,title,text);
                    // 使用自定义参数配置创建语音合成引擎。
                    try
                    {
                        Thread.sleep(1000);//单位：毫秒
                    } catch (Exception e) {
                    }
                    if(app_data.ReadStateSpeakallCheckbox()){
                        String detail=msg+","+title+","+author+","+text;
                        if(app_data.ReadStateRepeatCheckbox()){
                            mlTtsEngine.speak(detail, MLTtsEngine.QUEUE_APPEND);
                            mlTtsEngine.speak(detail, MLTtsEngine.QUEUE_APPEND);
                        }else{
                            mlTtsEngine.speak(detail, MLTtsEngine.QUEUE_APPEND);
                        }
                    }else{
                        mlTtsEngine.speak(msg, MLTtsEngine.QUEUE_APPEND);
                    }
                }else{
                    String msg=responseobject.getMsg();
                    tt4(msg);
                }
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {  //按下返回键销毁该活动
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.ACTION_DOWN){
            LangActivity.this.finish();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }
}

