package com.example.a11059.mlearning.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.a11059.mlearning.R;
import com.example.a11059.mlearning.adapter.StudentInfoRvAdapter;
import com.example.a11059.mlearning.entity.Class;
import com.example.a11059.mlearning.entity.User;
import com.example.a11059.mlearning.utils.UtilDatabase;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.qmuiteam.qmui.widget.QMUIEmptyView;
import com.qmuiteam.qmui.widget.QMUITopBar;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class StudentInfoActivity extends AppCompatActivity implements View.OnClickListener{

    private static final int TIP_TYPE_SUCCESS = 1;

    private static final int TIP_TYPE_FAIL = 0;

    private static final int TIP_TYPE_INFO = 2;

    private static final long DEFAULT_TIP_DURATION = 1000;

    private QMUITopBar topBar;

    private QMUIEmptyView emptyView;

    private RecyclerView recyclerView;

    private LinearLayoutManager layoutManager;

    private List<User> studentList = new ArrayList<>();

    public MyHandler handler = new MyHandler(this);

    private static String currentClassId = "";

    public static class MyHandler extends Handler {

        private final WeakReference<StudentInfoActivity> mActivity;

        public MyHandler(StudentInfoActivity activity){
            mActivity = new WeakReference<StudentInfoActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            final StudentInfoActivity activity = mActivity.get();
            switch (msg.what){
                case UtilDatabase.STUDENT_INFO:
                    if(UtilDatabase.studentList.size() == 0){
                        activity.emptyView.show("未找到学生信息", "该班级还没有学生");
                    }else {
                        activity.studentList = UtilDatabase.studentList;
                        activity.emptyView.hide();
                        activity.setData();
                    }
                    break;
                case UtilDatabase.ERROR_STUDENT:
                    activity.emptyView.show("查询失败", "无查询结果");
                    break;
            }
        }
    }

    public static void actionStart(Context context, String classId){
        Intent intent = new Intent(context, StudentInfoActivity.class);
        currentClassId = classId;
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_students_info);
        QMUIStatusBarHelper.translucent(this);
        initEmptyView();
        initTopBar();
        initRecyclerView();
        UtilDatabase.findStudentInfo(this, currentClassId);

    }
    private void initEmptyView(){
        emptyView = (QMUIEmptyView) findViewById(R.id.emptyView);
        emptyView.show(true);
    }

    private void initTopBar(){
        topBar = (QMUITopBar) findViewById(R.id.studentinfo_topbar);
        topBar.setTitle(getTopBarTitle());
        topBar.addLeftBackImageButton().setOnClickListener(this);
    }
    private String getTopBarTitle(){
        String title = "";
        for (Class aclass : UtilDatabase.classesList){
            if (aclass.getId() != null){
                if(aclass.getId().equals(currentClassId)){
                    title = aclass.getName();
                }
            }
        }
        return title;
    }

    private void initRecyclerView(){
        recyclerView = (RecyclerView) findViewById(R.id.student_recyclerview);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(StudentInfoActivity.this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
    }
    private void setData(){
        StudentInfoRvAdapter adapter = new StudentInfoRvAdapter(UtilDatabase.studentList);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.qmui_topbar_item_left_back:
                finish();
        }
    }
}
