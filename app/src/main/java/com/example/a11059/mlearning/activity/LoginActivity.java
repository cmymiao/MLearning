package com.example.a11059.mlearning.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.a11059.mlearning.R;
import com.example.a11059.mlearning.entity.User;
import com.example.a11059.mlearning.utils.UtilDatabase;
import com.example.a11059.mlearning.utils.UtilNetwork;
import com.example.a11059.mlearning.utils.UtilString;
import com.qmuiteam.qmui.layout.QMUILinearLayout;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
import com.qmuiteam.qmui.widget.grouplist.QMUICommonListItemView;
import com.qmuiteam.qmui.widget.grouplist.QMUIGroupListView;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;

import java.lang.ref.WeakReference;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

import static com.example.a11059.mlearning.application.MyApplication.getContext;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String IDENTITY_TEACHER = "teacher";

    private static final String IDENTITY_STUDENT = "student";

    private static final int TIP_TYPE_SUCCESS = 1;

    private static final int TIP_TYPE_FAIL = 0;

    private static final long DEFAULT_TIP_DURATION = 1000;

    private QMUILinearLayout loginWindow;

    private QMUITipDialog tipDialog;

    private EditText username;

    private EditText password;

    private EditText resetUserPwd;

    User user = new User();

    private static boolean isNeedShowTip = false;

    private QMUIDialog resetDialog;

    public MyHandler handler = new MyHandler(this);

    public static class MyHandler extends Handler {

        private final WeakReference<LoginActivity> mActivity;

        private MyHandler(LoginActivity activity){
            mActivity = new WeakReference<LoginActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            LoginActivity activity = mActivity.get();
            switch (msg.what){
                case UtilDatabase.RESET_PASSWORD_SUCCESS:
                    activity.tipDialog.dismiss();
                    activity.showTip(TIP_TYPE_SUCCESS, "邮件已发送，请前往邮箱重置密码", DEFAULT_TIP_DURATION);
                    break;
                case UtilDatabase.RESET_PASSWORD_FAIL:
                    activity.tipDialog.dismiss();
                    activity.showTip(TIP_TYPE_FAIL, "邮件发送失败，请重试", DEFAULT_TIP_DURATION);
                    break;
                default:
                    break;
            }
        }
    }

    public static void actionStart(Context context, boolean needShowTip){
        Intent intent = new Intent(context, LoginActivity.class);
        isNeedShowTip = needShowTip;
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        QMUIStatusBarHelper.translucent(this);
        initLoginWindow();
        if(isNeedShowTip){
            showPasswdModifiedTip();
        }
    }

    private void initLoginWindow(){
        loginWindow = (QMUILinearLayout) findViewById(R.id.login_window);
        float mShadowAlpha = 0.45f;
        int mShadowElevation = QMUIDisplayHelper.dp2px(LoginActivity.this, 14);
        int mRadius = QMUIDisplayHelper.dp2px(LoginActivity.this, 5);
        loginWindow.setRadiusAndShadow(mRadius, mShadowElevation, mShadowAlpha);
        initEditText();
        QMUIRoundButton login = (QMUIRoundButton) findViewById(R.id.btn_login);
        QMUIRoundButton reset = (QMUIRoundButton) findViewById(R.id.btn_resetPasswd);
        login.setOnClickListener(this);
        reset.setOnClickListener(this);
    }

    private void showPasswdModifiedTip(){
        new QMUIDialog.MessageDialogBuilder(LoginActivity.this)
                .setTitle("提示")
                .setMessage("密码已被修改，请重新登录。")
                .addAction("确定", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        dialog.dismiss();
                    }
                })
                .create()
                .show();
    }

    private void initEditText(){
        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        ImageView clrUsername = (ImageView) findViewById(R.id.clr_username);
        ImageView clrPassword = (ImageView) findViewById(R.id.clr_password);
        clrUsername.setOnClickListener(this);
        clrPassword.setOnClickListener(this);
        initClrEditText(username, clrUsername);
        initClrEditText(password, clrPassword);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.clr_username:
                clrUsernameClickAction();
                break;
            case R.id.clr_password:
                clrPasswordClickAction();
                break;
            case R.id.btn_login:
                loginClickAction();
                break;
            case R.id.btn_resetPasswd:
                resetPasswordClickAction();
                break;
            default:
                break;
        }
    }

    private void clrUsernameClickAction(){
        username.setText("");
    }

    private void clrPasswordClickAction(){
        password.setText("");
    }

    private void loginClickAction(){
        if(!UtilNetwork.isNetworkAvailable()){
            showTip(TIP_TYPE_FAIL, "网络不可用", DEFAULT_TIP_DURATION);
            return;
        }
        if(!isUsernameCheck()){
            showTip(TIP_TYPE_FAIL, "用户名不可为空", DEFAULT_TIP_DURATION);
            return;
        }
        if(!isPasswordCheck()){
            showTip(TIP_TYPE_FAIL, "密码不可为空", DEFAULT_TIP_DURATION);
            return;
        }

        showLoadingTip("登录中");
        //这里写登录的具体逻辑
        loginAction();
    }

    private void resetPasswordClickAction(){
        if(!UtilNetwork.isNetworkAvailable()){
            showTip(TIP_TYPE_FAIL, "网络不可用", DEFAULT_TIP_DURATION);
            return;
        }
        resetDialog = new QMUIDialog.CustomDialogBuilder(LoginActivity.this)
                .setLayout(R.layout.dialog_modify_userinfo)
                .setTitle("提示:需使用邮箱重置密码")
                .addAction("取消", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        dialog.dismiss();
                    }
                })
                .addAction("确定", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        resetDialog.dismiss();
                        showLoadingTip("正在向您的邮箱中发送邮件");
                        UtilDatabase.resetPasswordBeforeLogin(LoginActivity.this, resetUserPwd.getText().toString());
                    }
                }).create();

        resetUserPwd = (EditText) resetDialog.findViewById(R.id.user_info);
        resetUserPwd.setHint("请输入您个人资料中的邮箱");
        resetDialog.show();;
    }

    private void loginAction(){
        user.setUsername(username.getText().toString());
        user.setPassword(password.getText().toString());
        user.login(new SaveListener<User>() {
            @Override
            public void done(User user, final BmobException e) {
                //做个延迟，否则可能登陆太快，导致登录提示窗口的体验不好
                loginWindow.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(e == null){
                            loginSuccessAction();
                        } else {
                            loginFailAction(e.getErrorCode());
                        }
                    }
                }, DEFAULT_TIP_DURATION);

            }
        });
    }

    private void startStudentMainActivity(){
        StudentMainActivity.actionStart(LoginActivity.this);
        finish();
    }

    private void startTeacherMainActivity(){
        TeacherMainActivity.actionStart(LoginActivity.this);
        finish();
    }

    private void startInitialActivity(){
        InitialActivity.actionStart(LoginActivity.this);
        finish();
    }

    private void loginSuccessAction(){
        tipDialog.dismiss();
        showTip(TIP_TYPE_SUCCESS, "登录成功", DEFAULT_TIP_DURATION);
       // SharedPreferences.Editor editor = getSharedPreferences("loginData",MODE_PRIVATE).edit();
        //editor.putBoolean("loginState",true);
        //editor.apply();
        user = BmobUser.getCurrentUser(User.class);
        String identity = user.getIdentity();
        int firstTime = user.getFirstTime();
        if(firstTime == 0){
            startInitialActivity();
        }
        else{
            if(identity.equals(IDENTITY_STUDENT)){
                startStudentMainActivity();
            } else if(identity.equals(IDENTITY_TEACHER)){
                startTeacherMainActivity();
            }
        }
    }

    private void loginFailAction(int errorCode){
        String errorTipString = "登录失败";
        if(errorCode == 101){
            errorTipString = "用户名或密码错误";
        }
        tipDialog.dismiss();
        showTip(TIP_TYPE_FAIL, errorTipString, DEFAULT_TIP_DURATION);
    }

    private void showLoadingTip(String tip){
        tipDialog = new QMUITipDialog.Builder(LoginActivity.this)
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                .setTipWord(tip)
                .create(false);
        tipDialog.show();
    }

    private void showTip(int type, String tipWord, long duration){
        QMUITipDialog.Builder tipBuilder = new QMUITipDialog.Builder(LoginActivity.this);
        if(type == TIP_TYPE_SUCCESS){
            tipBuilder = tipBuilder.setIconType(QMUITipDialog.Builder.ICON_TYPE_SUCCESS);
        } else if(type == TIP_TYPE_FAIL){
            tipBuilder = tipBuilder.setIconType(QMUITipDialog.Builder.ICON_TYPE_FAIL);
        }
        tipBuilder = tipBuilder.setTipWord(tipWord);
        tipDialog = tipBuilder.create();
        tipDialog.show();
        loginWindow.postDelayed(new Runnable() {
            @Override
            public void run() {
                tipDialog.dismiss();
            }
        }, duration);

    }

    private void initClrEditText(final EditText e, final ImageView i){
        i.setVisibility(View.GONE);
        e.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b){
                    i.setVisibility(View.VISIBLE);
                }
                else{
                    i.setVisibility(View.GONE);
                }
            }
        });
    }

    private boolean isUsernameCheck(){
        if(username == null)
            return false;
        return UtilString.isNotBlankString(username.getText());
    }

    private boolean isPasswordCheck(){
        if(password == null)
            return false;
        return UtilString.isNotBlankString(password.getText());
    }

    @Override
    protected void onDestroy() {
        tipDialog.dismiss();
        super.onDestroy();
    }
}
