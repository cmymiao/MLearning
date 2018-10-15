package com.example.a11059.mlearning.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.a11059.mlearning.R;
import com.example.a11059.mlearning.adapter.CollectionRvAdapter;
import com.example.a11059.mlearning.adapter.ExamDetailRvAdapter;
import com.example.a11059.mlearning.adapter.MistakeRvAdapter;
import com.example.a11059.mlearning.entity.CollectionL;
import com.example.a11059.mlearning.entity.MistakeL;
import com.example.a11059.mlearning.entity.RecordL;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.qmuiteam.qmui.widget.QMUIEmptyView;
import com.qmuiteam.qmui.widget.QMUITopBar;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
import com.qmuiteam.qmui.widget.popup.QMUIPopup;

import org.litepal.crud.DataSupport;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobUser;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class QuestionLocalActivity extends AppCompatActivity implements View.OnClickListener {

    public static final int MSG_LIST_EMPTY = 111;

    private static final int TIP_TYPE_SUCCESS = 1;

    private static final int TIP_TYPE_FAIL = 0;

    private static final int TIP_TYPE_INFO = 2;

    private static final long DEFAULT_TIP_DURATION = 1000;

    public static final int START_MODE_MISTAKE= 1;

    public static final int START_MODE_COLLECTION = 2;

    public static final int START_MODE_EXAMDETAIL = 3;

    private static int currentMode = START_MODE_MISTAKE;

    private QMUIEmptyView emptyView;

    private QMUIPopup mNormalPopup;

    private QMUITipDialog tipDialog;

    private LinearLayoutManager layoutManager;

    private boolean isQuestionLoaded = false;

    private Button btnAnalyze;

    private List<MistakeL> mErrorList = new ArrayList<>();

    private List<CollectionL> mCollectionList = new ArrayList<>();

    private List<RecordL> mRecordList = new ArrayList<>();

    public MyHandler handler = new MyHandler(this);

    public static class MyHandler extends Handler {

        private final WeakReference<QuestionLocalActivity> mActivity;

        public MyHandler(QuestionLocalActivity activity){
            mActivity = new WeakReference<QuestionLocalActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            QuestionLocalActivity activity = mActivity.get();
            switch (msg.what){
                case MSG_LIST_EMPTY:
                    activity.showEmptyViewTip();
                    break;
            }
        }
    }

    public static void actionStart(Context context, int startMode){
        Intent intent = new Intent(context, QuestionLocalActivity.class);
        currentMode = startMode;
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_local);
        QMUIStatusBarHelper.translucent(this);
        initTopBar();
        initEmptyView();
        initBottomBar();
        loadQuestionData();
    }

    private void initTopBar(){
        String title;
        if(currentMode == START_MODE_MISTAKE){
            title = "错题集";
        } else if(currentMode == START_MODE_COLLECTION){
            title = "收藏夹";
        } else{
            title = "答题详情";
        }
        QMUITopBar topBar = (QMUITopBar) findViewById(R.id.questionl_topbar);
        topBar.setTitle(title);
        topBar.addLeftBackImageButton().setOnClickListener(this);
    }

    private void initEmptyView(){
        emptyView = (QMUIEmptyView) findViewById(R.id.questionl_emptyView);
        emptyView.show(true);
    }

    private void initBottomBar(){
        btnAnalyze = (Button) findViewById(R.id.btn_analyzel);
        btnAnalyze.setOnClickListener(this);
    }

    private void loadQuestionData(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    Thread.sleep(1000);
                } catch (InterruptedException e){
                    e.printStackTrace();
                }
                if(currentMode == START_MODE_MISTAKE){
                    mErrorList = DataSupport.where("username = ?", BmobUser.getCurrentUser().getUsername()).find(MistakeL.class);
                } else if(currentMode == START_MODE_COLLECTION){
                    mCollectionList = DataSupport.where("username = ?", BmobUser.getCurrentUser().getUsername()).find(CollectionL.class);
                } else{
                    mRecordList = DataSupport.findAll(RecordL.class);
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        isQuestionLoaded = true;
                        initRecyclerView();
                        if(currentMode == START_MODE_MISTAKE){
                            if(mErrorList.size() == 0){
                                showEmptyViewTip();
                            } else {
                                emptyView.hide();
                            }
                        } else if(currentMode == START_MODE_COLLECTION){
                            if(mCollectionList.size() == 0){
                                showEmptyViewTip();
                            } else {
                                emptyView.hide();
                            }
                        } else{
                            if(mRecordList.size() == 0){
                                showEmptyViewTip();
                            } else {
                                emptyView.hide();
                            }
                        }

                    }
                });
            }
        }).start();

    }

    private void showEmptyViewTip(){
        btnAnalyze.setClickable(false);
        if(currentMode == START_MODE_MISTAKE){
            emptyView.show("错题集什么都没有", "难道每次都偷偷看了解析？");
        } else if(currentMode == START_MODE_COLLECTION){
            emptyView.show("收藏夹是空的", "快去收集题目充实自己吧！");
        } else{
            emptyView.show("您没有答本试卷中的题目", "需要答题后再提交哦");
        }
    }

    private void initRecyclerView(){
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.questionl_recyclerview);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(QuestionLocalActivity.this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);
        if(currentMode == START_MODE_MISTAKE){
            MistakeRvAdapter adapter = new MistakeRvAdapter(mErrorList, this);
            recyclerView.setAdapter(adapter);
        } else if(currentMode == START_MODE_COLLECTION){
            CollectionRvAdapter adapter = new CollectionRvAdapter(mCollectionList, this);
            recyclerView.setAdapter(adapter);
        } else{
            ExamDetailRvAdapter adapter = new ExamDetailRvAdapter(mRecordList, this);
            recyclerView.setAdapter(adapter);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.qmui_topbar_item_left_back:
                if(currentMode == START_MODE_EXAMDETAIL){
                    DataSupport.deleteAll(RecordL.class);
                    StudentMainActivity.actionStart(QuestionLocalActivity.this);
                }else{
                    finish();
                }
                break;
            case R.id.btn_analyzel:
                btnAnalyzeClickAction(v);
                break;
            default:
                break;
        }
    }

    private void btnAnalyzeClickAction(View v){
        if(!isQuestionLoaded){
            showTip(TIP_TYPE_INFO, "加载中，请稍候", DEFAULT_TIP_DURATION);
            return;
        }
        int positon = layoutManager.findFirstVisibleItemPosition();
        if(currentMode == START_MODE_MISTAKE){
            setPopupText(mErrorList.get(positon).getAnalyze());
        } else if(currentMode == START_MODE_COLLECTION){
            setPopupText(mCollectionList.get(positon).getAnalyze());
        } else{
            setPopupText(mRecordList.get(positon).getAnalysis());
        }

        mNormalPopup.setAnimStyle(QMUIPopup.ANIM_GROW_FROM_CENTER);
        mNormalPopup.setPreferredDirection(QMUIPopup.DIRECTION_TOP);
        mNormalPopup.show(v);
    }

    private void setPopupText(String text) {
        if (mNormalPopup == null) {
            mNormalPopup = new QMUIPopup(QuestionLocalActivity.this, QMUIPopup.DIRECTION_NONE);
        }
        mNormalPopup.setNeedCacheSize(false);
        TextView textView = new TextView(QuestionLocalActivity.this);
        textView.setLayoutParams(mNormalPopup.generateLayoutParam(
                QMUIDisplayHelper.dp2px(QuestionLocalActivity.this, 250),
                WRAP_CONTENT
        ));
        textView.setLineSpacing(QMUIDisplayHelper.dp2px(QuestionLocalActivity.this, 4), 1.0f);
        int padding = QMUIDisplayHelper.dp2px(QuestionLocalActivity.this, 20);
        textView.setPadding(padding, padding, padding, padding);
        textView.setText(text);
        textView.setTextColor(ContextCompat.getColor(QuestionLocalActivity.this, R.color.app_color_description));
        mNormalPopup.setContentView(textView);
    }

    private void showTip(int type, String tipWord, long duration){
        QMUITipDialog.Builder tipBuilder = new QMUITipDialog.Builder(QuestionLocalActivity.this);
        if(type == TIP_TYPE_SUCCESS){
            tipBuilder = tipBuilder.setIconType(QMUITipDialog.Builder.ICON_TYPE_SUCCESS);
        } else if(type == TIP_TYPE_FAIL){
            tipBuilder = tipBuilder.setIconType(QMUITipDialog.Builder.ICON_TYPE_FAIL);
        } else if(type == TIP_TYPE_INFO){
            tipBuilder = tipBuilder.setIconType(QMUITipDialog.Builder.ICON_TYPE_INFO);
        }
        tipBuilder = tipBuilder.setTipWord(tipWord);
        tipDialog = tipBuilder.create();
        tipDialog.show();
        emptyView.postDelayed(new Runnable() {
            @Override
            public void run() {
                tipDialog.dismiss();
            }
        }, duration);

    }

    @Override
    public void onBackPressed() {
        if(currentMode == START_MODE_EXAMDETAIL){
            DataSupport.deleteAll(RecordL.class);
            StudentMainActivity.actionStart(QuestionLocalActivity.this);
        }else{
            super.onBackPressed();
            finish();
        }

    }
}
