package com.example.a11059.mlearning.activity;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.a11059.mlearning.R;
import com.example.a11059.mlearning.entity.User;

import org.litepal.LitePal;

import java.lang.ref.WeakReference;

import cn.bmob.v3.BmobUser;

public class SplashActivity extends Activity {

    private static final int START_TO_MAIN_STUDENT = 0;

    private static final int START_TO_LOGIN = 1;

    private static final int START_TO_MAIN_TEACHER = 2;

    private static final String IDENTITY_TEACHER = "teacher";

    private static final String IDENTITY_STUDENT = "student";

    private static final long DELAY_TIME = 1500;

    private User currentUser;

    public MyHandler handler = new MyHandler(this);

    public static class MyHandler extends Handler {

        private final WeakReference<SplashActivity> mActivity;

        public MyHandler(SplashActivity activity){
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            SplashActivity activity = mActivity.get();
            switch (msg.what){
                case START_TO_MAIN_STUDENT:
                    activity.startStudentActivity();
                    break;
                case START_TO_LOGIN:
                    activity.startLoginActivity();
                    break;
                case START_TO_MAIN_TEACHER:
                    activity.startTeacherActivity();
                    break;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        LitePal.getDatabase();
        currentUser = BmobUser.getCurrentUser(User.class);
        initStart();
    }

    private void initStart(){
        Message message = new Message();
        if(currentUser != null){
            int fistTime = currentUser.getFirstTime();
            String identity = currentUser.getIdentity();
            if(fistTime == 0){
                message.what = START_TO_LOGIN;
            }
            else{
                if(identity.equals(IDENTITY_STUDENT)){
                    message.what = START_TO_MAIN_STUDENT;
                } else if(identity.equals(IDENTITY_TEACHER)){
                    message.what = START_TO_MAIN_TEACHER;
                }
            }
        } else {
            message.what = START_TO_LOGIN;
        }
        handler.sendMessageDelayed(message, DELAY_TIME);
    }

    private void startStudentActivity(){
        StudentMainActivity.actionStart(SplashActivity.this);
        finish();
    }

    private void startTeacherActivity(){
        TeacherMainActivity.actionStart(SplashActivity.this);
        finish();
    }

    private void startLoginActivity(){
        LoginActivity.actionStart(SplashActivity.this, false);
        finish();
    }
}
