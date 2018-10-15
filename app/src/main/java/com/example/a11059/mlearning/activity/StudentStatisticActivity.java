package com.example.a11059.mlearning.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.a11059.mlearning.R;
import com.example.a11059.mlearning.adapter.SStatisticClassRvAdapter;
import com.example.a11059.mlearning.adapter.SStatisticUnitRvAdapter;
import com.example.a11059.mlearning.entity.Statistic;
import com.example.a11059.mlearning.utils.UtilDatabase;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.qmuiteam.qmui.widget.QMUIEmptyView;
import com.qmuiteam.qmui.widget.QMUITopBar;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class StudentStatisticActivity extends AppCompatActivity implements View.OnClickListener {

    public static int STATISTIC_UNIT = 1;

    public static int STATISTIC_CLASS = 2;

    private QMUITopBar topBar;

    private QMUIEmptyView emptyView;

    private RecyclerView recyclerView;

    private LinearLayoutManager layoutManager;

    public static String currentTitle;

    public static int currentType;

    private List<Statistic> mStatisticList = new ArrayList<>();

    public static void actionStart(Context context, String title, int type){
        Intent intent = new Intent(context, StudentStatisticActivity.class);
        currentTitle = title;
        currentType = type;
        context.startActivity(intent);
    }

    public MyHandler handler = new MyHandler(this);

    public static class MyHandler extends Handler {

        private final WeakReference<StudentStatisticActivity> mActivity;

        public MyHandler(StudentStatisticActivity activity){
            mActivity = new WeakReference<StudentStatisticActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            final StudentStatisticActivity activity = mActivity.get();
            switch (msg.what){
                case UtilDatabase.STATISTIC_ERROR:
                    activity.emptyView.show("查询失败", "请重试");
                    break;
                case UtilDatabase.STATISTIC_UNIT:
                    activity.mStatisticList = UtilDatabase.statisticList;
                    if(activity.mStatisticList.size() == 0){
                        activity.emptyView.show("查询失败", "无查询结果");
                        return;
                    }else {
                        activity.emptyView.hide();
                        activity.setData();
                    }
                    break;
                case UtilDatabase.STATISTIC_CLASS:
                    activity.mStatisticList = UtilDatabase.statisticList;
                    if(activity.mStatisticList.size() == 0){
                        activity.emptyView.show("查询失败", "无查询结果");
                        return;
                    }else {
                        activity.emptyView.hide();
                        activity.setData();
                    }
                    break;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_statistic);
        QMUIStatusBarHelper.translucent(this);
        initTopBar();
        initEmptyView();
        initRecyclerView();
        requestData();
    }

    private void initTopBar(){
        topBar = (QMUITopBar) findViewById(R.id.statistics_topbar);
        topBar.setTitle("答题情况统计");
        topBar.addLeftBackImageButton().setOnClickListener(this);
    }

    private void initEmptyView(){
        emptyView = (QMUIEmptyView) findViewById(R.id.emptyView);
        emptyView.show(true);
    }

    private void requestData(){
        if(currentType == STATISTIC_UNIT){
            UtilDatabase.findStatisticUnit(this);
        }else{
            UtilDatabase.findStatisticClass(this);
        }
    }

    private void initRecyclerView(){
        recyclerView = (RecyclerView)findViewById(R.id.statistic_recyclerview);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(StudentStatisticActivity.this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
    }

    private void setData(){
        if(currentType == STATISTIC_UNIT){
            SStatisticUnitRvAdapter adapter = new SStatisticUnitRvAdapter(mStatisticList);
            recyclerView.setAdapter(adapter);
        }else{
            SStatisticClassRvAdapter adapter = new SStatisticClassRvAdapter(mStatisticList);
            recyclerView.setAdapter(adapter);
        }

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
