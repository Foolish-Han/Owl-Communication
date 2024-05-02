package com.example.symplerecorder;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;

import java.lang.reflect.AccessibleObject;

//跳转系统的相关界面
public class StartSystemPageUtils {

    //跳转到应用的设置界面
    public static void goToAppSetting(Activity context){
        Intent intent=new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri=Uri.fromParts("package",context.getPackageName(),null);
        intent.setData(uri);
        context.startActivity(intent);
    }
}
