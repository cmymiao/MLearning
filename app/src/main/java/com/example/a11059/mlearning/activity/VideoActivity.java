package com.example.a11059.mlearning.activity;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.os.Handler;
import android.widget.Toast;

import java.io.IOException;
import java.util.logging.Logger;

import com.example.a11059.mlearning.R;
import com.example.a11059.mlearning.widget.BrightnessHelper;
import com.example.a11059.mlearning.widget.ShowChangeLayout;
import com.example.a11059.mlearning.widget.VideoGestureRelativeLayout;

public class VideoActivity extends AppCompatActivity implements VideoGestureRelativeLayout.VideoGestureListener{

    private static String currentUrl;
    private static String currentTitle;

    private static final String TAG1 = "Video_demo";
    private SurfaceView surfaceView;
    private SeekBar seekBar;
    private SurfaceHolder surfaceHolder;
    private MediaPlayer player;
    private ImageButton start,button_back;
    private TextView time,title_name;
    private RelativeLayout layout_top,layout_buttom;
    private ProgressDialog progressDialog;
    private AudioManager audioManager = null;

    private int curIndex = 0;
    public static final int UPDATE_UI = 1;
    private int  current_time = 0, total_time = 0, currentVolume = 0;
    private boolean isSurfaceCreated =false;
    private boolean isVisible = false;
    private String show_time = "00:00:00/00:00:00";
    private boolean mIsVideoSizeKnown;
    private int mVideoHeight,mVideoWidth,mSurfaceViewWidth,mSurfaceViewHeight;
    //手势控制初始化参数
    private final String TAG = "gesturetestm";
    private VideoGestureRelativeLayout ly_VG;
    private ShowChangeLayout scl;
    private AudioManager mAudioManager;
    private int maxVolume = 0;
    private int oldVolume = 0;
    private int newProgress = 0, oldProgress = 0, pos = 0;
    private BrightnessHelper mBrightnessHelper;
    private float brightness = 1;
    private Window mWindow;
    private WindowManager.LayoutParams mLayoutParams;

    public static void actionStart(Context context, String url, String title){
        Intent intent = new Intent(context, VideoActivity.class);
        currentUrl = url;
        currentTitle = title;
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        UIinit();
        setPlayerEvent();
        player = new MediaPlayer();
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        CreateSurface();
        surfaceHolder.setKeepScreenOn(true);
    }

    /**
     * UI布局初始化
     */
    private void UIinit() {

        /*
        Intent intent = getIntent();
        if (intent!=null){
            Bundle data = intent.getBundleExtra("data");
            String path = data.getString("url");
            String Titlename = data.getString("TitleName");
           // url = path;
            //TitleName = Titlename;
        }*/
        //TODO 将屏幕设置为横屏()
//        audioManager = (AudioManager) getSystemService(Service.AUDIO_SERVICE);
//        maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
//        //当前音量
//        currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        surfaceView = ((SurfaceView) findViewById(R.id.surfaceView));
        //进度条
        seekBar = ((SeekBar) findViewById(R.id.seekBar));
        //播放及暂停
        start = (ImageButton) findViewById(R.id.play_bt);
        time = (TextView) findViewById(R.id.current_time);
        title_name = (TextView) findViewById(R.id.title_name);
        //进度条的布局
        layout_buttom = (RelativeLayout) findViewById(R.id.layout_buttom);
        layout_top = (RelativeLayout) findViewById(R.id.layout_top);
        button_back = (ImageButton) findViewById(R.id.Button_return);
        start.setBackgroundResource(R.drawable.video_pasuseer);
        title_name.setText(currentTitle);

        ly_VG = (VideoGestureRelativeLayout) findViewById(R.id.ly_VG);
        ly_VG.setVideoGestureListener(this);

        scl = (ShowChangeLayout) findViewById(R.id.scl);

        //初始化获取音量属性
        mAudioManager = (AudioManager)getSystemService(Service.AUDIO_SERVICE);
        maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

        //初始化亮度调节
        mBrightnessHelper = new BrightnessHelper(this);

        //下面这是设置当前APP亮度的方法配置
        mWindow = getWindow();
        mLayoutParams = mWindow.getAttributes();
        brightness = mLayoutParams.screenBrightness;
    }
    private void setPlayerEvent() {
        /*
         * 暂停播放操作
         */
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (player != null && !player.isPlaying()){
                    start.setBackgroundResource(R.drawable.video_pasuseer);
                    player.start();
                    UIHandler.sendEmptyMessage(UPDATE_UI);
                }
                else{
                    start.setBackgroundResource(R.drawable.video_player);
                    player.pause();
                    UIHandler.removeMessages(UPDATE_UI);
                }
            }
        });
        /**
         * 返回操作
         */
        button_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        /**
         * 快进、后退操作
         */

        /**
         * 进度条操作
         */
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                timeShow();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                start.setBackgroundResource(R.drawable.video_player);
                UIHandler.removeMessages(UPDATE_UI);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                start.setBackgroundResource(R.drawable.video_pasuseer);
                int progress=seekBar.getProgress();
                //令视频播放进度遵循seekBar停止拖动的这一刻的进度
                player.seekTo(progress);
                UIHandler.sendEmptyMessage(UPDATE_UI);

            }
        });
    }
    /**
     *时间格式化
     */
    public void timeShow(){
        int current_time = player.getCurrentPosition()/1000;
        total_time = player.getDuration()/1000;
        System.out.println("当前：" + current_time + "/" + "总共：" + total_time);
        show_time = getFormatTime(current_time,total_time);
        time.setText(show_time);
    }

    public  String formatTimeUnit(int time) {
        return time < 10 ? "0" + time : "" + time;
    }

    public  String formatTimeString(int format_time) {
        String hours=formatTimeUnit(format_time / 3600);  //时
        String minutes=formatTimeUnit((format_time / 60) % 60); //分
        String seconds=formatTimeUnit(format_time % 60); //秒
        return hours + ":" + minutes+ ":" + seconds;
    }

    public  String getFormatTime(int current_time, int total_time) {
        current_time = Math.abs(current_time); // 得到当前播放时间的绝对值
        total_time = Math.abs(total_time); // 得到总播放时间的绝对值
        return formatTimeString(current_time) + "/" + formatTimeString(total_time);
    }

    /**
     * 实现网络监听
     */
//    @Override
//    public void onWifi() {
//        Toast.makeText("当前网络环境是WIFI");
//    }
//
//    @Override
//    public void onMobile() {
//        mToast("当前网络环境是手机网络");
//
//    }
//
//    @Override
//    public void onDisConnect() {
//        mToast("网络链接断开");
//
//    }
//
//    @Override
//    public void onNoAvailable() {
//        mToast("当前无网络链接");
//
//    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

    }

    //    @Override
//    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
//        if (width == 0 || height == 0) {
//            Log.e(TAG, "invalid video width(" + width + ") or height(" + height
//                    + ")");
//            return;
//        }
//        Log.d(TAG, "onVideoSizeChanged width:" + width + " height:" + height);
//
//        int wid = player.getVideoWidth();
//        int hig = player.getVideoHeight();
//        // 根据视频的属性调整其显示的模式
//
//        if (wid > hig) {
//            if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
//                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//            }
//        } else {
//            if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
//                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//            }
//        }
//        DisplayMetrics dm = new DisplayMetrics();
//        getWindowManager().getDefaultDisplay().getMetrics(dm);
//        mSurfaceViewWidth = dm.widthPixels;
//        mSurfaceViewHeight = dm.heightPixels;
//        if (width > height) {
//            // 竖屏录制的视频，调节其上下的空余
//
//            int w = mSurfaceViewHeight * width / height;
//            int margin = (mSurfaceViewWidth - w) / 2;
//            Log.d(TAG, "margin:" + margin);
//            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
//                    RelativeLayout.LayoutParams.MATCH_PARENT,
//                    RelativeLayout.LayoutParams.MATCH_PARENT);
//            lp.setMargins(margin, 0, margin, 0);
//            surfaceView.setLayoutParams(lp);
//        } else {
//            // 横屏录制的视频，调节其左右的空余
//
//            int h = mSurfaceViewWidth * height / width;
//            int margin = (mSurfaceViewHeight - h) / 2;
//            Log.d(TAG, "margin:" + margin);
//            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
//                    RelativeLayout.LayoutParams.MATCH_PARENT,
//                    RelativeLayout.LayoutParams.MATCH_PARENT);
//            lp.setMargins(0, margin, 0, margin);
//            surfaceView.setLayoutParams(lp);
//        }
//    }
//    /**
//     *触屏监控
//     * 5秒无操作隐藏上下布局
//     */
//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        // TODO Auto-generated method stub
//        String action = "";
//        float x = event.getX();
//        float y = event.getY();
//        switch (event.getAction()){
//            case MotionEvent.ACTION_DOWN:
//                downX = x;
//                downY = y;
//                Log.e("Tag","=======按下时X："+x);
//                Log.e("Tag","=======按下时Y："+y);
//
//                break;
//            case MotionEvent.ACTION_UP:
//                Log.e("Tag","=======抬起时X："+x);
//                Log.e("Tag","=======抬起时Y："+y);
//
//                //获取到距离差
//                float dx= x-downX;
//                float dy = y-downY;
//                //防止是按下也判断
//                if (Math.abs(dx)>8&&Math.abs(dy)>8) {
//                    //通过距离差判断方向
//                    int orientation = getOrientation(dx, dy);
//                    switch (orientation) {
//                        case 'r':
//                            action = "右";
//                            int pro1 = seekBar.getProgress();
//                            player.seekTo(pro1+5);
//                            break;
//                        case 'l':
//                            action = "左";
//                            int pro2 = seekBar.getProgress();
//                            player.seekTo(pro2-5);
//                            break;
//                        case 't':
//                            action = "上";
//                            audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,AudioManager.ADJUST_RAISE,AudioManager.FLAG_SHOW_UI);
//                            break;
//                        case 'b':
//                            action = "下";
//                            audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,AudioManager.ADJUST_LOWER,AudioManager.FLAG_SHOW_UI);
//                            break;
//                    }
//                    Toast.makeText(MainActivity.this, "向" + action + "滑动", Toast.LENGTH_SHORT).show();
//                }else {
//                    if(layout_top.getVisibility()==View.INVISIBLE&&layout_buttom.getVisibility()==View.INVISIBLE){
//                        layout_top.setVisibility(View.VISIBLE);
//                        layout_buttom.setVisibility(View.VISIBLE);
//
//                        new Thread(){
//                            public void run() {
//                                try {
//                                    Thread.sleep(5000);
//                                    runOnUiThread(new Runnable() {
//
//                                        @Override
//                                        public void run() {
//                                            if(layout_top.getVisibility()==View.VISIBLE&&layout_buttom.getVisibility()==View.VISIBLE){
//                                                layout_top.setVisibility(View.INVISIBLE);
//                                                layout_buttom.setVisibility(View.INVISIBLE);
//                                            }
//                                        }
//                                    });
//                                } catch (InterruptedException e) {
//                                    // TODO Auto-generated catch block
//                                    e.printStackTrace();
//                                }
//                            };
//                        }.start();
//                    }else if(layout_top.getVisibility()==View.VISIBLE&&layout_buttom.getVisibility()==View.VISIBLE){
//                        layout_top.setVisibility(View.INVISIBLE);
//                        layout_buttom.setVisibility(View.INVISIBLE);}
//                }
//
//
//
//        }
//        return true;
//    }
//    private int getOrientation(float dx, float dy) {
//        Log.e("Tag","========X轴距离差："+dx);
//        Log.e("Tag","========Y轴距离差："+dy);
//        if (Math.abs(dx)>Math.abs(dy)){
//            //X轴移动
//            return dx>0?'r':'l';
//        }else{
//            //Y轴移动
//            return dy>0?'b':'t';
//        }
//    }
    @Override
    public void onDown(MotionEvent e) {
        //每次按下的时候更新当前亮度和音量，还有进度
        if (newProgress == 100){
            oldProgress = 0;

        }else if (newProgress == 0){
            oldProgress = 100;
        }else {
            oldProgress = newProgress;
        }
        oldVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        brightness = mLayoutParams.screenBrightness;
        if (brightness == -1){
            //一开始是默认亮度的时候，获取系统亮度，计算比例值
            brightness = mBrightnessHelper.getBrightness() / 255f;
        }
    }

    @Override
    public void onEndFF_REW(MotionEvent e) {
//        makeToast("设置进度为" + newProgress);
        int step  = (newProgress - oldProgress)*1000;
        if (step>5000&&newProgress==100){
            player.seekTo(pos + 5000);
        }else {
            player.seekTo(pos + step);
        }



    }

    @Override
    public void onVolumeGesture(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {

        Log.d(TAG, "onVolumeGesture: oldVolume " + oldVolume);
        int value = ly_VG.getHeight()/maxVolume ;
        int newVolume = (int) ((e1.getY() - e2.getY())/value + oldVolume);

        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,newVolume,AudioManager.FLAG_PLAY_SOUND);


//        int newVolume = oldVolume;

        Log.d(TAG, "onVolumeGesture: value" + value);

        //另外一种调音量的方法，感觉体验不好，就没采用
//        if (distanceY > value){
//            newVolume = 1 + oldVolume;
//            mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,AudioManager.ADJUST_RAISE, AudioManager.FLAG_PLAY_SOUND);
//        }else if (distanceY < -value){
//            newVolume = oldVolume - 1;
//            mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,AudioManager.ADJUST_LOWER, AudioManager.FLAG_PLAY_SOUND);
//        }
        Log.d(TAG, "onVolumeGesture: newVolume "+ newVolume);

        //要强行转Float类型才能算出小数点，不然结果一直为0
        int volumeProgress = (int) (newVolume/Float.valueOf(maxVolume) *100);
        if (volumeProgress >= 50){
            scl.setImageResource(R.drawable.volume_higher_w);
        }else if (volumeProgress > 0){
            scl.setImageResource(R.drawable.volume_lower_w);
        }else {
            scl.setImageResource(R.drawable.volume_off_w);
        }
        scl.setProgress(volumeProgress);
        scl.show();
    }

    @Override
    public void onBrightnessGesture(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        //这是直接设置系统亮度的方法
//        if (Math.abs(distanceY) > ly_VG.getHeight()/255){
//            if (distanceY > 0){
//                setBrightness(4);
//            }else {
//                setBrightness(-4);
//            }
//        }

        //下面这是设置当前APP亮度的方法
        Log.d(TAG, "onBrightnessGesture: old" + brightness);
        float newBrightness = (e1.getY() - e2.getY()) / ly_VG.getHeight() ;
        newBrightness += brightness;

        Log.d(TAG, "onBrightnessGesture: new" + newBrightness);
        if (newBrightness < 0){
            newBrightness = 0;
        }else if (newBrightness > 1){
            newBrightness = 1;
        }
        mLayoutParams.screenBrightness = newBrightness;
        mWindow.setAttributes(mLayoutParams);
        scl.setProgress((int) (newBrightness * 100));
        scl.setImageResource(R.drawable.brightness_w);
        scl.show();
    }

    //这是直接设置系统亮度的方法
    private void setBrightness(int brightness) {
        //要是有自动调节亮度，把它关掉
        mBrightnessHelper.offAutoBrightness();
        int oldBrightness = mBrightnessHelper.getBrightness();
        Log.d(TAG, "onBrightnessGesture: oldBrightness: " + oldBrightness);
        int newBrightness = oldBrightness + brightness;
        Log.d(TAG, "onBrightnessGesture: newBrightness: " + newBrightness);
        //设置亮度
        mBrightnessHelper.setSystemBrightness(newBrightness);
        //设置显示
        scl.setProgress((int) (Float.valueOf(newBrightness)/mBrightnessHelper.getMaxBrightness() * 100));
        scl.setImageResource(R.drawable.brightness_w);
        scl.show();

    }


    @Override
    public void onFF_REWGesture(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        float offset = e2.getX() - e1.getX();
        pos = player.getCurrentPosition();
        Log.d(TAG, "onFF_REWGesture: offset " + offset);
        Log.d(TAG, "onFF_REWGesture: ly_VG.getWidth()" + ly_VG.getWidth());
        //根据移动的正负决定快进还是快退
        if (offset > 0) {
            scl.setImageResource(R.drawable.ff);
            newProgress = (int) (oldProgress + offset/ly_VG.getWidth() * 100);
            if (newProgress > 100){
                newProgress = 100;
            }
        }else {
            scl.setImageResource(R.drawable.fr);
            newProgress = (int) (oldProgress + offset/ly_VG.getWidth() * 100);
            if (newProgress < 0){
                newProgress = 0;
            }
        }

        scl.setProgress(newProgress);
        scl.show();
    }

    @Override
    public void onSingleTapGesture(MotionEvent e) {
        Log.d(TAG, "onSingleTapGesture: ");
        if(layout_top.getVisibility()==View.INVISIBLE&&layout_buttom.getVisibility()==View.INVISIBLE){
            layout_top.setVisibility(View.VISIBLE);
            layout_buttom.setVisibility(View.VISIBLE);

            new Thread(){
                public void run() {
                    try {
                        Thread.sleep(5000);
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                if(layout_top.getVisibility()==View.VISIBLE&&layout_buttom.getVisibility()==View.VISIBLE){
                                    layout_top.setVisibility(View.INVISIBLE);
                                    layout_buttom.setVisibility(View.INVISIBLE);
                                }
                            }
                        });
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                };
            }.start();
        }else if(layout_top.getVisibility()==View.VISIBLE&&layout_buttom.getVisibility()==View.VISIBLE){
            layout_top.setVisibility(View.INVISIBLE);
            layout_buttom.setVisibility(View.INVISIBLE);}
    }


    @Override
    public void onDoubleTapGesture(MotionEvent e) {
        Log.d(TAG1, "onDoubleTapGesture: ");

        if (player != null && !player.isPlaying()){
            start.setBackgroundResource(R.drawable.video_pasuseer);
            player.start();
            UIHandler.sendEmptyMessage(UPDATE_UI);
            makeToast("播放");
        }
        else{
            start.setBackgroundResource(R.drawable.video_player);
            player.pause();
            UIHandler.removeMessages(UPDATE_UI);
            makeToast("暂停");
        }
    }

    private void makeToast(String str){
        Toast.makeText(this,str,Toast.LENGTH_SHORT).show();
    }
//    /**
//     * 系统音量按键监听
//     */
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        switch (keyCode) {
//            case KeyEvent.KEYCODE_VOLUME_DOWN:
//                /**
//                 * 调低音量
//                 * 第一个参数：声音类型
//                 * 第二个参数：调整音量的方向
//                 * 第三个参数：可选的标志位
//                 */
//                audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,AudioManager.ADJUST_LOWER,0);
//                layout_Volume.setVisibility(View.VISIBLE);
//                currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
//                current_volume = (float) currentVolume/maxVolume*100;
//                int volume = Math.round(current_volume);
//                volume_text.setText("当前音量：" + volume + "%");
////                volume_text.setText("-----------------"+count);
////                count--;
//                return true;
//            case KeyEvent.KEYCODE_VOLUME_UP:
//                audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,AudioManager.ADJUST_RAISE,0);
//                layout_Volume.setVisibility(View.VISIBLE);
//                currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
//                volume_text.setText("当前音量：" + current_volume  + "%");
////                volume_text.setText("++++++++++++++++"+ count);
////                count++;
//                return true;
//            case KeyEvent.KEYCODE_VOLUME_MUTE:
//                volume_text.setText("MUTE");
//                return true;
//        }
//        return super.onKeyDown(keyCode, event);
//    }
    /**
     * 消息机制
     */
    private Handler UIHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what==UPDATE_UI){
                //获取视频当前播放的时间
                int currentPosition = player.getCurrentPosition();
                //获取视频播放的总时间
                int totalPosition = player.getDuration();
                //格式化视频播放时间
                timeShow();
                //同步进度条
                seekBar.setMax(totalPosition);
                seekBar.setProgress(currentPosition);
//                new Thread(new MyThread()).start();
                UIHandler.sendEmptyMessageDelayed(UPDATE_UI,500);

            }
        }
    };

    /**
     * 创建surfaceview对象
     */
    private void CreateSurface()
    {
        surfaceHolder = surfaceView.getHolder();
        //SURFACE_TYPE_PUSH_BUFFERS表明该Surface不包含原生数据，
        // Surface用到的数据由其他对象提供，在Camera图像预览中就使用该类型的Surface，
        // 有Camera负责提供给预览Surface数据，这样图像预览会比较流畅。
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        surfaceHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                Log.i(TAG1, "SurfaceHolder 被创建");
                isSurfaceCreated = true;
                player.setDisplay(surfaceHolder);
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                Log.i(TAG1, "SurfaceHolder 大小被改变");

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                Log.i(TAG1, "SurfaceHolder 被销毁");
                isSurfaceCreated = false;
                if (player !=null && player.isPlaying()){
                    curIndex = player.getCurrentPosition();
                    player.stop();
                }

            }
        });
    }


    /**
     * 暂停
     */
    private void Pause()
    {
        if (player != null && player.isPlaying())
        {
            curIndex = player.getCurrentPosition();
            player.pause();

        }

    }
    private void Play(final int currentPosition)
    {
        try
        {
            player.reset();
            player.setDataSource(VideoActivity.this, Uri.parse(currentUrl));
            player.setDisplay(surfaceHolder);
            //异步准备 准备工作在子线程中进行 当播放网络视频时候一般采用此方法
            player.prepareAsync();
            showProgressDialog("提示", "正在加载......");
            player.setOnPreparedListener(new MediaPlayer.OnPreparedListener()
            {

                @Override
                public void onPrepared(MediaPlayer mp)
                {

                    int width = player.getVideoWidth();
                    int height = player.getVideoHeight();
                    DisplayMetrics dm = new DisplayMetrics();
                    getWindowManager().getDefaultDisplay().getMetrics(dm);
                    mSurfaceViewWidth = dm.widthPixels;
                    mSurfaceViewHeight = dm.heightPixels;
                    if (width > height) {
                        // 竖屏录制的视频，调节其上下的空余

                        int w = mSurfaceViewHeight * width / height;
                        int margin = (mSurfaceViewWidth - w) / 2;
                        Log.d(TAG1, "margin:" + margin);
                        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                                RelativeLayout.LayoutParams.MATCH_PARENT,
                                RelativeLayout.LayoutParams.MATCH_PARENT);
                        lp.setMargins(margin, 0, margin, 0);
                        surfaceView.setLayoutParams(lp);}

                    player.seekTo(currentPosition);
//                    player.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
//
//                        @Override
//                        public void onBufferingUpdate(MediaPlayer player,
//                                                      int bufferingProgress) {
//                            try {
//                                skbProgress.setSecondaryProgress(bufferingProgress);//更新缓冲进度
//                                VideoPlyer.this.bufferingProgress = bufferingProgress;
//                                int currentProgress = skbProgress.getMax()
//                                        * player.getCurrentPosition()
//                                        / player.getDuration();
//                                Log.e("onBufferingUpdate", "load:" + bufferingProgress
//                                        + "%__" + "play:" + currentProgress + "%");
//                                // 同步刷新播放进度
//                                Message msg = mHandler.obtainMessage(
//                                        player.getCurrentPosition(), currentProgress,
//                                        bufferingProgress);
//                                mHandler.sendMessage(msg);
//                            } catch (Exception e) {
//                                Log.e("VideoPlayer", "error msg:" + e.getMessage());
//                            }
//                        }
//                    });
                    player.start();
                    timeShow();
                    hideProgressDialog();
                }
            });
            // 缓冲监听
            player.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
                @Override
                public void onBufferingUpdate(MediaPlayer mp, int percent) {
                    seekBar.setSecondaryProgress(percent);
                    int currentProgress=seekBar.getMax()*player.getCurrentPosition()/player.getDuration();
                    Log.e("播放进度"+currentProgress+"% play", "缓冲进度"+percent + "% buffer");
                }
            });
            // 播放完成监听
            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    Log.e(TAG1,"视频播放完毕");
                }
            });
            player.setOnErrorListener(new MediaPlayer.OnErrorListener() {

                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {

                    return false;
                }
            });
        }
        catch (Exception e)
        {
            Log.d(TAG1,"line:210--Video_demo--Play--error");
        }
    }

    /**
     * 创建完毕页面后需要将播放操作延迟10ms防止因surface创建不及时导致播放失败
     */
    @Override
    protected void onResume()
    {
        super.onResume();
        Log.e(TAG1,"onResume");
        new Handler().postDelayed(new Runnable()
        {
            public void run()
            {
                if(isSurfaceCreated)
                {
                    if (curIndex==0){
                        Play(curIndex);
                    }else
                    {
                        player.seekTo(curIndex);
                        player.start();
                    }

                }
            }
        }, 10);
        UIHandler.sendEmptyMessage(UPDATE_UI);
    }
    /**
     * 页面从前台到后台会执行 onPause ->onStop 此时Surface会被销毁，
     * 再一次从后台 到前台时需要 重新创建Surface
     */
    @Override
    protected void onRestart()
    {
        super.onRestart();
        Log.e(TAG1,"onRestart");
        if(!isSurfaceCreated)
        {
            CreateSurface();
        }
        UIHandler.sendEmptyMessage(UPDATE_UI);
    }


    @Override
    protected void onPause()
    {
        super.onPause();
        Log.e(TAG1,"onPause");
        Pause();
        UIHandler.removeMessages(UPDATE_UI);
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        Log.e(TAG1,"onStop");
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(TAG1, "start onDestroy");
        if (player != null) {
            player.release();
            player = null;
        }
    }
    /***
     * 显示正在加载中
     */
    public void showProgressDialog(String title, String message) {
        if (progressDialog == null) {

            progressDialog = ProgressDialog.show(VideoActivity.this, title,
                    message, true, false);
        } else if (progressDialog.isShowing()) {
            progressDialog.setTitle(title);
            progressDialog.setMessage(message);
        }
        progressDialog.show();

    }

    /**
     * 隐藏加载
     */
    public void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }

    }
}
