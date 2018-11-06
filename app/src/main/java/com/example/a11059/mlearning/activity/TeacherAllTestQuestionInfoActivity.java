package com.example.a11059.mlearning.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.view.View;
import android.widget.Button;

import com.example.a11059.mlearning.R;
import com.example.a11059.mlearning.adapter.TeacherAllTestQuestionInfoRvAdapter;
import com.example.a11059.mlearning.entity.Course;
import com.example.a11059.mlearning.entity.Question;
import com.example.a11059.mlearning.utils.UtilDatabase;
import com.example.a11059.mlearning.utils.UtilNetwork;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.qmuiteam.qmui.widget.QMUIEmptyView;
import com.qmuiteam.qmui.widget.QMUITopBar;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class TeacherAllTestQuestionInfoActivity extends AppCompatActivity implements View.OnClickListener{
    private static int currentCourseid;
    private static int currentUnitId;
    private static String currentUnitName;
    private static final int TIP_TYPE_SUCCESS = 1;

    private static final int TIP_TYPE_FAIL = 0;

    private static final int TIP_TYPE_INFO = 2;

    private static final long DEFAULT_TIP_DURATION = 1000;

    private boolean isQuestionLoaded = false;

    private QMUITopBar topBar;

    private QMUIEmptyView emptyView;

    private RecyclerView recyclerView;

    private LinearLayoutManager layoutManager;

    private Button btnPrev;

    private Button btnNext;

    private QMUITipDialog tipDialog;

    private List<Question> questionUnitList = new ArrayList<>();

    public MyHandler handler = new MyHandler(this);

    public static class MyHandler extends Handler {

        private final WeakReference<TeacherAllTestQuestionInfoActivity> mActivity;

        public MyHandler(TeacherAllTestQuestionInfoActivity activity){
            mActivity = new WeakReference<TeacherAllTestQuestionInfoActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            final TeacherAllTestQuestionInfoActivity activity = mActivity.get();
            switch (msg.what){
                case UtilDatabase.QUESTION_FIND:
                    activity.questionUnitList = UtilDatabase.questionUnitList;
                    if(activity.questionUnitList.size() == 0){
                        activity.showEmptyViewTip();
                        return;
                    }else {
                        activity.resetAdapter();
                        activity.emptyView.hide();
                        activity.isQuestionLoaded = true;
                    }
                    break;
                case UtilDatabase.ERROR_QUESTION:
                    activity.showEmptyViewTip();
                    break;
            }
        }
    }

    public static void actionStart(Context context,String unitName, int courseId, int unitId){
        Intent intent = new Intent(context, TeacherAllTestQuestionInfoActivity.class);
        currentCourseid = courseId;
        currentUnitId = unitId;
        currentUnitName = unitName;
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_question_info);
        QMUIStatusBarHelper.translucent(this);
        initEmptyView();
        initTopBar();
        initBottomBar();
        initRecyclerView();
        requestQuestionData();
    }
    private void initEmptyView(){
        emptyView = (QMUIEmptyView) findViewById(R.id.emptyView);
        emptyView.show(true);
    }

    private void initTopBar(){
        topBar = (QMUITopBar) findViewById(R.id.question_topbar);
        topBar.setTitle(getTopBarTitle());
        topBar.addLeftBackImageButton().setOnClickListener(this);
    }

    private String getTopBarTitle(){
        String title = "";
        for (Course course : UtilDatabase.courseList){
            if (course.getId() != null){
                if(course.getId().equals(currentCourseid)){
                    title = course.getName();
                }
            }
        }
        return title;
    }

    private String getTopBarSubTitle(int unitId){
        String subTitle = currentUnitName;

        return subTitle;
    }
    private void initBottomBar(){
        btnPrev = (Button) findViewById(R.id.btn_prev);
        btnNext = (Button) findViewById(R.id.btn_next);
        btnPrev.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        btnPrev.setText("上一单元");
        btnNext.setText("下一单元");
    }

    private void initRecyclerView(){
        recyclerView = (RecyclerView) findViewById(R.id.question_recyclerview);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(TeacherAllTestQuestionInfoActivity.this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);
    }

    private void resetAdapter(){
        TeacherAllTestQuestionInfoRvAdapter adapter = new TeacherAllTestQuestionInfoRvAdapter(this, questionUnitList);
        recyclerView.setAdapter(adapter);
    }
    private void requestQuestionData(){
        topBar.setSubTitle(getTopBarSubTitle(currentUnitId));
        UtilDatabase.findQuestionInfo(TeacherAllTestQuestionInfoActivity.this, currentCourseid, currentUnitId);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.qmui_topbar_item_left_back:
                finish();
            case R.id.btn_prev:
                btnPrevClickAction();
                break;
            case R.id.btn_next:
                btnNextClickAction();
                break;
        }
    }

    private void showTip(int type, String tipWord, long duration){
        QMUITipDialog.Builder tipBuilder = new QMUITipDialog.Builder(TeacherAllTestQuestionInfoActivity.this);
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
    private void btnPrevClickAction(){
        if(!isQuestionLoaded){
            showTip(TIP_TYPE_INFO, "加载中，请稍候", DEFAULT_TIP_DURATION);
            return;
        }
        if(currentUnitId == 1){
            showTip(TIP_TYPE_INFO, "没有上一单元了", DEFAULT_TIP_DURATION);
            return;
        } else {
            emptyView.show(true);
            currentUnitId--;
            requestQuestionData();
        }

    }


    private void btnNextClickAction(){
        if(!isQuestionLoaded){
            showTip(TIP_TYPE_INFO, "加载中，请稍候", DEFAULT_TIP_DURATION);
            return;
        }
        if(currentUnitId == UtilDatabase.unitsList.size()){
            showTip(TIP_TYPE_INFO, "没有下一单元了", DEFAULT_TIP_DURATION);
            return;
        } else {
            emptyView.show(true);
            currentUnitId++;
            requestQuestionData();
        }

    }

    private void showEmptyViewTip(){
        if(!UtilNetwork.isNetworkAvailable()){
            emptyView.show("网络异常", "请确认网络畅通后重试");
        } else {
            emptyView.show("未获取到相关课程题目", "请重试~");
        }
        btnPrev.setClickable(false);
        btnNext.setClickable(false);
    }
}

