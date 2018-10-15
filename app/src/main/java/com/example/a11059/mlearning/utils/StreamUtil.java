package com.example.a11059.mlearning.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by 11059 on 2018/9/1.
 */

public class StreamUtil extends Thread {

    private static final int SUCCESS =1 ;
    String url;
    InputStream is=null;

    public void setUrl(String url) {
        this.url = url;
    }

    public void setIs(InputStream is) {
        this.is = is;
    }

    public  StreamUtil(String url){
        this.url=url;
    }

    public InputStream getIs() {
        return is;
    }

    public void run(){

        try {
            URL url1=new URL(url);
            HttpURLConnection connection = (HttpURLConnection) url1.openConnection();
            connection.setRequestMethod("GET");//试过POST 可能报错
            connection.setDoInput(true);
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);
            //实现连接
            connection.connect();

            System.out.println("connection.getResponseCode()=" + connection.getResponseCode());
            if (connection.getResponseCode() == 200) {
                is = connection.getInputStream();

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
