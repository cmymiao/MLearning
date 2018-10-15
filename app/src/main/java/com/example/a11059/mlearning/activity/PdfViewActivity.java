package com.example.a11059.mlearning.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.a11059.mlearning.R;
import com.example.a11059.mlearning.utils.StreamUtil;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnDrawListener;
import com.github.barteksc.pdfviewer.listener.OnErrorListener;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.listener.OnPageScrollListener;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.qmuiteam.qmui.widget.QMUIEmptyView;
import com.qmuiteam.qmui.widget.QMUITopBar;

import java.io.File;
import java.io.InputStream;

public class PdfViewActivity extends AppCompatActivity implements OnPageChangeListener,OnLoadCompleteListener, View.OnClickListener {

    public PDFView pdfView ;

    public QMUITopBar topBar;

    private QMUIEmptyView emptyView;

    public static String currentUrl;

    public static String currentTitle;

    public static void actionStart(Context context, String url, String title){
        Intent intent = new Intent(context, PdfViewActivity.class);
        currentTitle = title;
        currentUrl = url;
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_view);
        QMUIStatusBarHelper.translucent(this);
        iniTopBar();
        initEmptyView();
        initPdfView();
    }

    private void iniTopBar(){
        topBar = findViewById(R.id.pdfview_topbar);
        topBar.setTitle(currentTitle);
        topBar.addLeftBackImageButton().setOnClickListener(this);
    }

    private void initEmptyView(){
        emptyView = findViewById(R.id.emptyView);
        emptyView.show(true);
    }

    private void initPdfView(){
        if(currentUrl.indexOf("http")==0) {
            StreamUtil st = new StreamUtil(currentUrl);
            st.start();
            InputStream is = null;
            while (is == null) {
                is = st.getIs();
            }
            pdfView = findViewById(R.id.pdf_view);
            pdfView.fromStream(is)

                    .enableSwipe(true)
                    .swipeHorizontal(true)
                    .enableDoubletap(true)
                    .defaultPage(0)
                    .onDraw(new OnDrawListener() {
                        @Override
                        public void onLayerDrawn(Canvas canvas, float pageWidth, float pageHeight, int displayedPage) {

                        }
                    })
                    .onLoad(new OnLoadCompleteListener() {
                        @Override
                        public void loadComplete(int nbPages) {
                            emptyView.hide();
                            //Toast.makeText(getApplicationContext(), "loadComplete", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .onPageChange(new OnPageChangeListener() {
                        @Override
                        public void onPageChanged(int page, int pageCount) {

                        }
                    })
                    .onPageScroll(new OnPageScrollListener() {
                        @Override
                        public void onPageScrolled(int page, float positionOffset) {

                        }
                    })
                    .onError(new OnErrorListener() {
                        @Override
                        public void onError(Throwable t) {
                            emptyView.show("加载失败", "请重试");
                        }
                    })
                    .enableAnnotationRendering(false)
                    .password(null)
                    .scrollHandle(null)
                    .load();
        }else{
            pdfView = findViewById( R.id.pdf_view );
            File file=new File(currentUrl);
            Uri uri = Uri.fromFile(file);
            pdfView.fromUri(uri)
                    .enableDoubletap(true)
                    .onPageChange(this)
                    .enableAnnotationRendering(true)
                    .onLoad(this)
                    .enableDoubletap(true)
                    .load();
        }
    }

    @Override
    public void loadComplete(int nbPages) {

    }

    @Override
    public void onPageChanged(int page, int pageCount) {

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
