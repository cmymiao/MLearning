package com.example.a11059.mlearning.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.a11059.mlearning.R;
import com.example.a11059.mlearning.application.BaseSampleActivity;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.qmuiteam.qmui.widget.QMUIEmptyView;
import com.qmuiteam.qmui.widget.QMUITopBar;

import es.voghdev.pdfviewpager.library.RemotePDFViewPager;
import es.voghdev.pdfviewpager.library.adapter.PDFPagerAdapter;
import es.voghdev.pdfviewpager.library.remote.DownloadFile;
import es.voghdev.pdfviewpager.library.util.FileUtil;

public class RemotePDFActivity extends BaseSampleActivity implements DownloadFile.Listener, View.OnClickListener {

    private static String currentUrl = "";

    private static String currentTitle = "";

    private QMUITopBar topBar;

    private QMUIEmptyView emptyView;

    private LinearLayout root;
    private RemotePDFViewPager remotePDFViewPager;
    private PDFPagerAdapter adapter;

    private String u;

    public static void actionStart(Context context, String url, String title){
        Intent intent = new Intent(context, RemotePDFActivity.class);
        currentUrl = url;
        currentTitle = title;
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remote_pdf);
        QMUIStatusBarHelper.translucent(this);
        initTopbar();
        initEmptyview();
        initLinearLayout();
        setDownloadButtonListener();
    }

    private void initTopbar(){
        topBar = (QMUITopBar) findViewById(R.id.remotepdf_topbar);
        topBar.setTitle(currentTitle);
        topBar.addLeftBackImageButton().setOnClickListener(this);
    }

    private void initEmptyview(){
        emptyView = (QMUIEmptyView) findViewById(R.id.emptyView_pdf);
        emptyView.show(true);
    }

    private void initLinearLayout(){
        root = (LinearLayout)findViewById(R.id.remote_pdf_root);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (adapter != null) {
            adapter.close();
        }
    }

    /***
     * 下载缓存PDF
     */
    protected void setDownloadButtonListener() {
        final Context ctx = this;
        final DownloadFile.Listener listener = this;
        remotePDFViewPager = new RemotePDFViewPager(ctx, currentUrl, listener);
        Log.d("ffffff","Downloading");
    }

    public void updateLayout() {
        root.removeAllViewsInLayout();
        root.addView(remotePDFViewPager,
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onSuccess(String url, String destinationPath) {
        emptyView.hide();
        adapter = new PDFPagerAdapter(this, FileUtil.extractFileNameFromURL(url));
        remotePDFViewPager.setAdapter(adapter);
        Log.d("ffffff","Downloaded");
        updateLayout();
    }

    @Override
    public void onFailure(Exception e) {
        e.printStackTrace();
        Log.d("ffffff", "error");
        //Toast.makeText(getApplicationContext(),"网络错误，请重试", Toast.LENGTH_LONG);
    }

    @Override
    public void onProgressUpdate(int progress, int total) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.qmui_topbar_item_left_back:
                finish();
                break;
        }
    }
}
