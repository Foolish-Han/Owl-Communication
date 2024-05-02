package com.example.symplerecorder.utils;

import android.content.Context;
import android.widget.Toast;

public class ToastUtil {
    public static Toast mToast;
    public static void showMessage(Context context, String msg){
        if (mToast == null){
            mToast = Toast.makeText(context,msg,Toast.LENGTH_SHORT);
        }else {
            mToast.setText(msg);
        }
        mToast.show();
    }
}
