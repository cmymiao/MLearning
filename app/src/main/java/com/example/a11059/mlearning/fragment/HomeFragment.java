package com.example.a11059.mlearning.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.a11059.mlearning.R;
import com.example.a11059.mlearning.activity.TeacherMainActivity;
import com.example.a11059.mlearning.entity.User;
import com.example.a11059.mlearning.utils.UtilDatabase;
import com.qmuiteam.qmui.widget.QMUITopBar;
import com.qmuiteam.qmui.widget.grouplist.QMUICommonListItemView;
import com.qmuiteam.qmui.widget.grouplist.QMUIGroupListView;

import cn.bmob.v3.BmobUser;

public class HomeFragment extends Fragment implements View.OnClickListener{

    private View fragmentView;

    private TeacherMainActivity parentActivity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        parentActivity = (TeacherMainActivity) getActivity();
        User user = BmobUser.getCurrentUser(User.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_home, container, false);
        initTopBar(fragmentView);
        initGroupList(fragmentView);
        return fragmentView;
    }

    private void initTopBar(View view) {
        QMUITopBar mTopBar = view.findViewById(R.id.home_topbar);
        mTopBar.setTitle("主页");
    }

    private void initGroupList(View view){
        QMUIGroupListView groupList = (QMUIGroupListView) view.findViewById(R.id.problem_group_list);

        //section1
        QMUICommonListItemView studentInfoView = groupList.createItemView("浏览学生信息");
        studentInfoView.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);
        QMUICommonListItemView resourceView = groupList.createItemView("浏览课程资源");
        resourceView.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);
        QMUICommonListItemView questionView = groupList.createItemView("浏览题目信息");
        questionView.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);
        QMUIGroupListView.newSection(getContext())
                .setTitle("信息浏览")
                .addItemView(studentInfoView, studentInfoViewListener())
                .addItemView(resourceView, resourceViewListener())
                .addItemView(questionView, questionViewListener())
                .addTo(groupList);

        //section2
        QMUICommonListItemView problemsView = groupList.createItemView("查看问题并解答");
        problemsView.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_SWITCH);
        QMUIGroupListView.newSection(getContext())
                .setTitle("问答")
                .addItemView(problemsView, problemsViewListener())
                .addTo(groupList);
    }

    private View.OnClickListener studentInfoViewListener(){
        return null;
    }

    private View.OnClickListener resourceViewListener(){
        return null;
    }

    private View.OnClickListener questionViewListener(){
        return null;
    }

    private View.OnClickListener problemsViewListener(){
        return null;
    }



    @Override
    public void onClick(View view) {

    }
}
