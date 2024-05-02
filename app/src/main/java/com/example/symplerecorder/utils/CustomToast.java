package com.example.symplerecorder.utils;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

public class CustomToast extends Toast {
    private int fontColor;
    private int fontSize;
    private int position;
    private String textContent;

    public CustomToast(Context context, int fontColor, int fontSize, int position, String textContent) {
        super(context);
        this.fontColor = fontColor;
        this.fontSize = fontSize;
        this.position = position;
        this.textContent = textContent;
    }
    public void show(Context context) {
        TextView toastView = new TextView(context);
        toastView.setText(textContent);
        toastView.setTextColor(fontColor);
        toastView.setTextSize(fontSize);
        toastView.setGravity(position);
        setView(toastView);
        super.show();
    }

    public static void showCustomToast(Context context, int fontColor, int fontSize, int position, String textContent) {
        CustomToast customToast = new CustomToast(context, fontColor, fontSize, position, textContent);
        customToast.show(context);
    }
}
