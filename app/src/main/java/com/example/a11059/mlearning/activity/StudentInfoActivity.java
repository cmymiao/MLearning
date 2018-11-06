package com.example.a11059.mlearning.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.View;
import android.widget.TextView;

import com.example.a11059.mlearning.R;
import com.example.a11059.mlearning.adapter.StudentInfoRvAdapter;
import com.example.a11059.mlearning.entity.Class;
import com.example.a11059.mlearning.entity.User;
import com.example.a11059.mlearning.utils.UtilDatabase;
import com.qmuiteam.qmui.span.QMUIAlignMiddleImageSpan;
import com.qmuiteam.qmui.util.QMUIDrawableHelper;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.qmuiteam.qmui.widget.QMUIEmptyView;
import com.qmuiteam.qmui.widget.QMUITopBar;
import com.qmuiteam.qmui.widget.dialog.QMUIBottomSheet;

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

    private QMUIBottomSheet stuInfoDetailBs;

    private List<User> studentList = new ArrayList<>();

    public MyHandler handler = new MyHandler(this);

    private static String currentClassId = "";

    private  static int sumStudent ;

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
                        sumStudent = activity.studentList.size();
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
        initStuInfoDetailBottomsheet();
    }
    private void initEmptyView(){
        emptyView = (QMUIEmptyView) findViewById(R.id.emptyView);
        emptyView.show(true);
    }

    private void initTopBar(){
        topBar = (QMUITopBar) findViewById(R.id.studentinfo_topbar);
        topBar.setTitle(getTopBarTitle());
//        topBar.setSubTitle(getTopBarSubTitle());
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
    private String getTopBarSubTitle(){
        String subTitle = " ";
        subTitle = sumStudent + "人";
        return subTitle;
    }

    private void initRecyclerView(){
        recyclerView = (RecyclerView) findViewById(R.id.student_recyclerview);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(StudentInfoActivity.this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
    }
    private void initStuInfoDetailBottomsheet(){
        stuInfoDetailBs = new QMUIBottomSheet(this);
        stuInfoDetailBs.setContentView(R.layout.bs_students_information_detail);
    }
    private void setData(){
        StudentInfoRvAdapter adapter = new StudentInfoRvAdapter(UtilDatabase.studentList);
        adapter.setOnItemClickListener(new StudentInfoRvAdapter.OnShowSDetailClickListener() {
            @Override
            public void onClick(int position) {
                User stuInfo = UtilDatabase.studentList.get(position);
                setStuInforDetailBsContent(stuInfo);
                stuInfoDetailBs.show();
            }
        });
        recyclerView.setAdapter(adapter);
    }
    private void setStuInforDetailBsContent(User stuInfo){
        View view = stuInfoDetailBs.getContentView();
        TextView studentId = (TextView) view.findViewById(R.id.student_id);
        TextView studentName = (TextView) view.findViewById(R.id.student_name);
        TextView studentNickname = (TextView) view.findViewById(R.id.student_nickname);
        TextView studentInfoContent = (TextView) view.findViewById(R.id.student_info_content);

//        String stuId = stuInfo.getUsername() + "";

        String stuId = "学号：" + stuInfo.getUsername();
        studentId.setText(stuId);
        if (stuInfo.getName() != null){
            String stuName = "姓名：" +  stuInfo.getName();
            studentName.setText(stuName);
        }else {
            studentName.setText("无");
        }
        if (stuInfo.getNickname() != null){
            String stuNickname = "昵称：" + stuInfo.getNickname() ;
            studentNickname.setText(stuNickname);
        }else {
            studentNickname.setText("无");
        }

        String stuOtherInfo = "电话：" + stuInfo.getMobilePhoneNumber()
                              + "\n"
                              + "邮箱：" + stuInfo.getEmail();
        studentInfoContent.setText(getQContentIconString(stuOtherInfo));
    }

    private SpannableString getQContentIconString(String sContent){
        /***
         * 通过SpannableString在字符串中插入图片
         */
        final float spanWidthCharacterCount = 1.5f;
        int end = sContent.indexOf("\n");
        Drawable icon_mobile = QMUIDrawableHelper.getVectorDrawable(StudentInfoActivity.this, R.drawable.ic_bs_stu_mobile_info);
        Drawable icon_email = QMUIDrawableHelper.getVectorDrawable(StudentInfoActivity.this, R.drawable.ic_bs_stu_email_info);
        StringBuilder stringBuilder = new StringBuilder(sContent);
        stringBuilder.insert(end + 1 , "[icon_emil]");
        SpannableString spannable = new SpannableString("[icon_mobile]" + stringBuilder);
        if(icon_mobile != null){
            icon_mobile.setBounds(0, 0, icon_mobile.getIntrinsicWidth(), icon_mobile.getIntrinsicHeight());
        }
        if(icon_email != null){
            icon_email.setBounds(0, 0, icon_email.getIntrinsicWidth(), icon_email.getIntrinsicHeight());
        }
        if (end != -1){
            ImageSpan alignMiddleImageSpan_mobile = new QMUIAlignMiddleImageSpan(icon_mobile, QMUIAlignMiddleImageSpan.ALIGN_MIDDLE, spanWidthCharacterCount);
            ImageSpan alignMiddleImageSpan_email = new QMUIAlignMiddleImageSpan(icon_email, QMUIAlignMiddleImageSpan.ALIGN_MIDDLE, spanWidthCharacterCount);
            spannable.setSpan(alignMiddleImageSpan_mobile, 0, "[icon_mobile]".length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            spannable.setSpan(alignMiddleImageSpan_email, end + "[icon_mobile]".length()+1 , end + "[icon_mobile]".length() + "[icon_email]".length() , Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        }

        return spannable;
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.qmui_topbar_item_left_back:
                finish();
        }
    }
}
