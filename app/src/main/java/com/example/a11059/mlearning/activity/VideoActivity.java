package com.example.a11059.mlearning.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.a11059.mlearning.R;
import com.example.a11059.mlearning.utils.UtilUI;
import com.example.a11059.mlearning.widget.CustomMediaController;

import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.MediaPlayer.OnBufferingUpdateListener;
import io.vov.vitamio.MediaPlayer.OnInfoListener;
import io.vov.vitamio.Vitamio;
import io.vov.vitamio.widget.VideoView;

public class VideoActivity extends Activity implements OnInfoListener, OnBufferingUpdateListener{

    /**
     * TODO: Set the path variable to a streaming video URL or a local media file
     * path.
     */
    private static String currentUrl;
    private static String currentTitle;


    private Uri uri;
    private VideoView mVideoView;
    private ProgressBar pb;
    private TextView downloadRateView, loadRateView;
    private CustomMediaController mCustomMediaController;

    private long position = 0;


    public static void actionStart(Context context, String url, String title){
        Intent intent = new Intent(context, VideoActivity.class);
        currentUrl = url;
        currentTitle = title;
        context.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        Vitamio.isInitialized(getApplicationContext());
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        setContentView(R.layout.activity_video);
        mVideoView = (VideoView) findViewById(R.id.buffer);
        pb = (ProgressBar) findViewById(R.id.probar);

        downloadRateView = (TextView) findViewById(R.id.download_rate);
//        loadRateView = (TextView) findViewById(R.id.load_rate);
        if (currentUrl == "") {
            // Tell the user to provide a media file URL/path.
            UtilUI.shortToast("视频不存在请重试");
            return;
        } else {
            /*
             * Alternatively,for streaming media you can use
             * mVideoView.setVideoURI(Uri.parse(URLstring));
             */
            uri = Uri.parse(currentUrl);
            mVideoView.setVideoURI(uri);
//      mVideoView.setMediaController(new MediaController(this));
            mCustomMediaController = new CustomMediaController(this,mVideoView,this);
            mCustomMediaController.setVideoName(currentTitle);

            mCustomMediaController.show(5000); //5s隐藏
            mVideoView.setMediaController(mCustomMediaController);

            mVideoView.setBufferSize(2048); //设置视频缓冲大小
            mVideoView.requestFocus();
            mVideoView.setOnInfoListener(this);
            mVideoView.setOnBufferingUpdateListener(this);
            mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    // optional need Vitamio 4.0
                    mediaPlayer.setPlaybackSpeed(1.0f);
                }
            });
        }

    }
    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        switch (what) {
            case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                if (mVideoView.isPlaying()) {
                    mVideoView.pause();
                    pb.setVisibility(View.VISIBLE);
                    downloadRateView.setText("");
//                    loadRateView.setText("");
                    downloadRateView.setVisibility(View.VISIBLE);
//                    loadRateView.setVisibility(View.VISIBLE);

                }
                break;
            case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                mVideoView.start();
                pb.setVisibility(View.GONE);
                downloadRateView.setVisibility(View.GONE);
//                loadRateView.setVisibility(View.GONE);
                break;
            case MediaPlayer.MEDIA_INFO_DOWNLOAD_RATE_CHANGED:
                downloadRateView.setText("" + extra + "kb/s" + "  ");
                break;
        }
        return true;
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
//        loadRateView.setText(percent + "%");
    }

    @Override
    protected void onPause() {
        super.onPause();
        position = mVideoView.getCurrentPosition();
        mVideoView.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (position>0){
            mVideoView.seekTo(position);
            mVideoView.start();
        }
    }
}
