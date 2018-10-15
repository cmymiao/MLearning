package com.example.a11059.mlearning.utils;

import android.content.Context;
import android.widget.Toast;

import com.example.a11059.mlearning.application.MyApplication;

public class UtilUI {

    private static Context mContext = MyApplication.getContext();

    public static void shortToast(String content){
        Toast.makeText(mContext, content, Toast.LENGTH_SHORT).show();
    }

}
