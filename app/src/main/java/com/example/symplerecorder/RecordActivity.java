package com.example.symplerecorder;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.animation.ObjectAnimator;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.symplerecorder.utils.CustomToast;
import com.example.symplerecorder.utils.UdpUtil;

import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Optional;

import com.example.symplerecorder.utils.ToastUtil;

public class RecordActivity extends AppCompatActivity{
    private AudioRecord audioRecord;
    private boolean isRunning = false;
    private Thread recordingThread;
    private Thread receiveThread;

    private String successConnect="114514";

    private String failConnect="连接失败！";
    private int sampleRateInHz = 16000;
    private int channelConfigIn = AudioFormat.CHANNEL_IN_MONO;
    private int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
    private int bufferSizeInBytes = 1024;
    // "10.192.63.58"
    //"10.193.41.222"
    private String serverAddress = "10.192.12.146"; // 替换为实际的服务器IP地址
    private String defaultIP = "10.192.12.146"; // 替换为实际的服务器IP地址

    private int serverPort = 5555; // 替换为实际的服务器端口号
    private Button startBtn,endBtn;
    private TextView mResultText;
    private static final int SAMPLE_RATE = 16000;
    private static final int CHANNEL_CONFIG = AudioFormat.CHANNEL_OUT_MONO;
    private static final int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;
    int bufferSize = AudioTrack.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT);
    private AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT, bufferSize, AudioTrack.MODE_STREAM);
    private UdpUtil udpUtil;

    private Button buttonShowDialog;
    private SharedPreferences sharedPreferences;

    {
        try {
            udpUtil = new UdpUtil(serverAddress,serverPort);
        } catch (SocketException e) {
            throw new RuntimeException(e);
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        startBtn=findViewById(R.id.start_btn);
        endBtn=findViewById(R.id.end_btn);
        mResultText = findViewById(R.id.result);

        buttonShowDialog = findViewById(R.id.setting_btn);
        sharedPreferences = getSharedPreferences("app_settings", MODE_PRIVATE);

        // 从 SharedPreferences 中获取上次保存的 IP 地址
        serverAddress = sharedPreferences.getString("ip", defaultIP);

        startBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    // 触发动画，例如改变透明度
                    ObjectAnimator fadeAnim = ObjectAnimator.ofFloat(v, "alpha", 1f, 0.5f);
                    fadeAnim.setDuration(100); // 设置动画持续时间
                    fadeAnim.start();
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    // 按钮抬起时的动画，恢复透明度
                    ObjectAnimator fadeAnim = ObjectAnimator.ofFloat(v, "alpha", 0.5f, 1f);
                    fadeAnim.setDuration(100);
                    fadeAnim.start();
                }
                return false; // 返回false以确保按钮的默认点击事件也被触发
            }
        });
        endBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    // 触发动画，例如改变透明度
                    ObjectAnimator fadeAnim = ObjectAnimator.ofFloat(v, "alpha", 1f, 0.5f);
                    fadeAnim.setDuration(100); // 设置动画持续时间
                    fadeAnim.start();
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    // 按钮抬起时的动画，恢复透明度
                    ObjectAnimator fadeAnim = ObjectAnimator.ofFloat(v, "alpha", 0.5f, 1f);
                    fadeAnim.setDuration(100);
                    fadeAnim.start();
                }
                return false; // 返回false以确保按钮的默认点击事件也被触发
            }
        });

        // 在Activity或Fragment中调用此方法显示自定义Toast
        CustomToast.showCustomToast(this, this.getResources().getColor(R.color.purple_700), 20, Gravity.TOP, "鹰鹰来啦！");
        //updateUDPHelper();
        setListener();

    }
    private void settingIP() {
        EditText editTextIp = new EditText(this);
        editTextIp.setText(serverAddress);
        AlertDialog.Builder builder = new AlertDialog.Builder(RecordActivity.this);
        builder.setTitle("设置IP地址");
        builder.setView(editTextIp);
        builder.setPositiveButton("确定", (dialog, which) -> {
            String newIp = editTextIp.getText().toString();
            if (!newIp.isEmpty()) {
                // 保存新的 IP 地址到 SharedPreferences
                sharedPreferences.edit().putString("ip", newIp).apply();
                Toast.makeText(RecordActivity.this, "IP 地址已保存", Toast.LENGTH_SHORT).show();
                serverAddress=newIp;
                updateIPInBackground(newIp);
            } else {
                Toast.makeText(RecordActivity.this, "请输入有效的 IP 地址", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("取消", null);
        builder.show();
    }
    private void setListener(){
        RecordActivity.OnClick onClick=new RecordActivity.OnClick();
       startBtn.setOnClickListener(onClick);
       endBtn.setOnClickListener(onClick);
       buttonShowDialog.setOnClickListener(onClick);
    }

    private class OnClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            int vId = v.getId();
            if (vId == R.id.start_btn) {
                isRunning = true;
                startRecording();
                startPlaying();
            } else if(vId == R.id.end_btn) {
                isRunning=false;
            } else if(vId == R.id.setting_btn) {
                settingIP();
            }
         }
    }
    public void startRecording() {
        if (!isRunning) {
            return;
        }
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, sampleRateInHz, channelConfigIn, audioFormat, bufferSizeInBytes);
        if (audioRecord.getState() != AudioRecord.STATE_INITIALIZED) {
            Log.e("AudioRecorder", "AudioRecord initialization failed");
            return;
        }
        ToastUtil.showMessage(RecordActivity.this,"正在录音");
        audioRecord.startRecording();
        recordingThread = new Thread(new Runnable() {
            @Override
            public void run() {
                byte[] buffer = new byte[bufferSizeInBytes];
                while (isRunning) {
                    int readSize = audioRecord.read(buffer, 0, bufferSizeInBytes);
                    if (readSize > 0) {
                        try {
                            udpUtil.send(buffer);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        },"send");
        recordingThread.start();
    }
    private void startPlaying() {
       receiveThread= new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    audioTrack.play();
                    while (isRunning){
                        Optional<byte[]> receivedData = udpUtil.receive();
                        if (receivedData.isPresent()) {
                            byte[] buffer=receivedData.get();
                                if(buffer[0]==-128&&buffer[1]==-128&&buffer[2]==-128&&buffer[3]==-128){
                                    mResultText.setTextColor(RecordActivity.this.getResources().getColor(R.color.red));
                                    mResultText.setText("鹰鹰拒绝了你的请求！");
                                    Thread.sleep(1000);
                                    mResultText.setText("");
                                    return;
                                }
                            mResultText.setTextColor(RecordActivity.this.getResources().getColor(R.color.red));
                            audioTrack.write(buffer,0,buffer.length);
                        } else {
                            mResultText.setTextColor(RecordActivity.this.getResources().getColor(R.color.red));
                            mResultText.setText("鹰鹰断联了www！");
                            Thread.sleep(3000);
                            mResultText.setText("");
                            audioTrack.stop();
                            isRunning=false;
                            return;
                        }

                    }
                    audioTrack.stop();
                    mResultText.setText("");
                }catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        },"receive");
       receiveThread.start();
    }

    private void updateIPInBackground(String ipAddress) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                // 在这里执行网络操作，比如更新IP
                try {
                    UdpUtil.updateIP(ipAddress);
                } catch (UnknownHostException e) {
                    throw new RuntimeException(e);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                super.onPostExecute(result);
                // 在这里更新UI，如果需要的话
            }
        }.execute();
    }
}
