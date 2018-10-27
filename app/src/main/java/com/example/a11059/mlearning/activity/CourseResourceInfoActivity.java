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
import com.example.a11059.mlearning.entity.Course;
import com.example.a11059.mlearning.utils.UtilDatabase;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.qmuiteam.qmui.widget.QMUIEmptyView;
import com.qmuiteam.qmui.widget.QMUITopBar;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class CourseResourceInfoActivity extends AppCompatActivity implements View.OnClickListener{

    private static final int TIP_TYPE_SUCCESS = 1;

    private static final int TIP_TYPE_FAIL = 0;

    private static final int TIP_TYPE_INFO = 2;

    private static final long DEFAULT_TIP_DURATION = 1000;

    private QMUITopBar topBar;

    private QMUIEmptyView emptyView;

    private RecyclerView recyclerView;

    private LinearLayoutManager layoutManager;

    private List<Course> courseResourceList = new ArrayList<>();

    public MyHandler handler = new MyHandler(this);

    private static int currentCourseId;

    public static class MyHandler extends Handler {

        private final WeakReference<CourseResourceInfoActivity> mActivity;

        public MyHandler(CourseResourceInfoActivity activity){
            mActivity = new WeakReference<CourseResourceInfoActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            final CourseResourceInfoActivity activity = mActivity.get();
            switch (msg.what){
                case UtilDatabase.COURSE_FIND:
                    if(UtilDatabase.courseResourceList.size() == 0){
                        activity.emptyView.show("未找到该课程资源信息", "该课程还没有资源");
                    }else {
                        activity.courseResourceList = UtilDatabase.courseResourceList;
                        activity.emptyView.hide();
                        activity.setData();
                    }
                    break;
                case UtilDatabase.ERROR_COURSE:
                    activity.emptyView.show("查询失败", "无查询结果");
                    break;
            }
        }
    }

    public static void actionStart(Context context, int courseId){
        Intent intent = new Intent(context, CourseResourceInfoActivity.class);
        currentCourseId = courseId;
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_resource);
        QMUIStatusBarHelper.translucent(this);
        initEmptyView();
        initTopBar();
        initRecyclerView();
        UtilDatabase.findCourseResourceInfo(this, currentCourseId);

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
        for (Course course : UtilDatabase.courseAllList){
            if (course.getId() != null){
                if (course.getId().equals(currentCourseId)){
                    title = course.getName();
                }
            }

        }
        return title;
    }

    private void initRecyclerView(){
        recyclerView = (RecyclerView) findViewById(R.id.course_recyclerview);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(CourseResourceInfoActivity.this, LinearLayoutManager.VERTICAL, false);
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
