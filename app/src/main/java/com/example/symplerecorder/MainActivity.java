package com.example.symplerecorder;

import android.Manifest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

import com.example.symplerecorder.databinding.ActivityMainBinding;
import com.example.symplerecorder.utils.Contants;
import com.example.symplerecorder.utils.IFileInter;
import com.example.symplerecorder.utils.PermissionUtils;
import com.example.symplerecorder.utils.SDCardUtils;

import java.io.File;
import java.util.List;
import com.example.symplerecorder.github.florent37.viewanimator.ViewAnimator;

public class MainActivity extends AppCompatActivity {
    private TextView myTextView;
    private ActivityMainBinding binding;
    private int time=3; //倒计时时间
    String [] permissions ={ Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_EXTERNAL_STORAGE,
           Manifest.permission.WRITE_EXTERNAL_STORAGE
//            Manifest.permission.INTERNET,
//            Manifest.permission.ACCESS_NETWORK_STATE,
//            Manifest.permission.ACCESS_WIFI_STATE,
//            Manifest.permission.CHANGE_NETWORK_STATE,
//            Manifest.permission.READ_PHONE_STATE,
//            Manifest.permission.WRITE_SETTINGS
    };

    Handler handler=new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            if(msg.what==1){
                time--;
                if (time==0){
                    startActivity(new Intent(MainActivity.this, RecordActivity.class));
                    finish();
                }else {
                    binding.mainTv.setText(time+"");
                    handler.sendEmptyMessageDelayed(1,1000);
                }
            }

            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //setContentView(R.layout.activity_main);
        myTextView=findViewById(R.id.myTextView);
        ViewAnimator.animate(myTextView)
                .scale(0, 2,1)
                .duration(3000)
                .decelerate()
                .start();
        binding.mainTv.setText(time+"");
        PermissionUtils.getInstance().onRequestPermission(this,permissions,listener);


    }

    PermissionUtils.OnPermissionCallbackListener listener=new PermissionUtils.OnPermissionCallbackListener() {
        @Override
        public void onGranted() {

            //判断是否有应用文件夹，没有则创建
            createAppDir();
            //倒计时进入播放录音页面
            handler.sendEmptyMessageDelayed(1,1000);
        }

        @Override
        public void onDenied(List<String> deniedPermissions) {
            PermissionUtils.getInstance().showDialogTipUserGotoAppSetting(MainActivity.this);
        }
    };

    private void createAppDir() {
        File recordDir=SDCardUtils.getInstance().createAppFetchDir(IFileInter.FETCH_DIR_AUDIO);
        Contants.PATH_FETCH_DIR_RECORD=recordDir.getAbsolutePath();
    }

    public void onRequestPermissionsResult(Activity context, int requestCode, @NonNull String[] permissions, @NonNull int [] grantResults){
        super.onRequestPermissionsResult(requestCode,permissions,grantResults);
        PermissionUtils.getInstance().onRequestPermissionsResult(this,requestCode,permissions,grantResults);
    }
}