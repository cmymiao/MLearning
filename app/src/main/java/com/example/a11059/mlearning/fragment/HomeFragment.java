package com.example.a11059.mlearning.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.example.a11059.mlearning.R;
import com.example.a11059.mlearning.activity.StudentInfoActivity;
import com.example.a11059.mlearning.activity.TeacherAllTestQuestionInfoActivity;
import com.example.a11059.mlearning.activity.TeacherMainActivity;
import com.example.a11059.mlearning.activity.TeacherResourceActivity;
import com.example.a11059.mlearning.adapter.ClassSelectorRvAdapter;
import com.example.a11059.mlearning.adapter.CourseAllSelectorRvAdapter;
import com.example.a11059.mlearning.adapter.CourseUnitRvAdapter;
import com.example.a11059.mlearning.adapter.TeacherQuizRvAdapter;
import com.example.a11059.mlearning.adapter.Unit_KnowledgeRvAdapter;
import com.example.a11059.mlearning.entity.Class;
import com.example.a11059.mlearning.entity.Course;
import com.example.a11059.mlearning.entity.Problem;
import com.example.a11059.mlearning.entity.Unit;
import com.example.a11059.mlearning.entity.User;
import com.example.a11059.mlearning.utils.UtilDatabase;
import com.example.a11059.mlearning.utils.UtilNetwork;
import com.qmuiteam.qmui.widget.QMUIEmptyView;
import com.qmuiteam.qmui.widget.QMUITopBar;
import com.qmuiteam.qmui.widget.dialog.QMUIBottomSheet;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
import com.qmuiteam.qmui.widget.grouplist.QMUICommonListItemView;
import com.qmuiteam.qmui.widget.grouplist.QMUIGroupListView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobUser;

public class HomeFragment extends Fragment implements View.OnClickListener{

    private View fragmentView;

    private TeacherMainActivity parentActivity;

    private QMUIEmptyView emptyView;

    private RecyclerView recyclerView;

    private QMUIBottomSheet classSelectorBs;

    private RecyclerView classSelectorRv;

    private QMUIEmptyView classSelectorEv;

    private QMUIBottomSheet courseAllSelectorBs;

    private RecyclerView courseAllSelectorRv;

    private QMUIEmptyView courseAllSlectorEV;

    private LinearLayoutManager layoutManager;

    private QMUIDialog showQuizContent;

    private EditText replyContent;

    private QMUITipDialog tipDialog;

    private List<Problem> problemList = new ArrayList<>();

    private List<Class> classesList = new ArrayList<>();

    private List<Course> coursesAllList = new ArrayList<>();

    private List<Unit> unitsList = new ArrayList<>();

    public MyHandler handler = new MyHandler(this);

    private QMUIBottomSheet u_kSelectorBs;

    private RecyclerView u_kSelectorRv;

    private QMUIEmptyView u_kSelectorEv;

    private QMUIBottomSheet c_unitSelectorBs;

    private RecyclerView c_unitSelectorRv;

    private QMUIEmptyView c_unitSelectorEv;

    private Unit_KnowledgeRvAdapter unit_knowledgeRvAdapter;

    private CourseUnitRvAdapter courseUnitRvAdapter;
    //TeacherResourceActivity的课程id
    private int courseId ;

    private static final int TIP_TYPE_SUCCESS = 1;

    private static final int TIP_TYPE_FAIL = 0;

    private static final long DEFAULT_TIP_DURATION = 1000;

    public static class MyHandler extends Handler {

        private final WeakReference<HomeFragment> mFragment;

        private MyHandler(HomeFragment fragment){
            mFragment = new WeakReference<>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            HomeFragment fragment = mFragment.get();
            switch (msg.what){
                case UtilDatabase.PROBLEM_INFO:
                    fragment.problemList = UtilDatabase.problemList;
                    if (fragment.problemList.size() == 0){
                        fragment.emptyView.show("未获取到留言", "没有未回复的留言");
                    }else {
                        fragment.emptyView.hide();
                        fragment.resetData();
                    }
                    break;
                case UtilDatabase.ERROR_PROBLEM:
                    fragment.emptyView.show("未获取到留言", "没有未回复的留言");
                    break;
                case UtilDatabase.CLASSES_FIND:
                    fragment.classesList = UtilDatabase.classesList;
                    if (fragment.classesList.size() == 0){
                        fragment.showClassesLoadedFail();
                        return;
                    }
                    fragment.initClassSelectorRv();
                    break;
                case UtilDatabase.ERROR_CLASSES_LOADED:
                    fragment.showClassesLoadedFail();
                    break;
                case UtilDatabase.COURSE_ALL_INFO:
                    fragment.coursesAllList = UtilDatabase.courseAllList;
                    if (fragment.coursesAllList.size() == 0){
                        fragment.showAllCourseLoadedFail();
                        return;
                    }
                    fragment.initAllCourseSelectorRv();
                    break;
                case UtilDatabase.ERROR_ALL_COURSE_LOADED:
                    fragment.showAllCourseLoadedFail();
                    break;
                case UtilDatabase.UNITS_FIND:
                    fragment.unitsList = UtilDatabase.unitsList;
                    if (UtilDatabase.unitsList.size() == 0){
                        fragment.showAllUnitsdFail();
                        return;
                    }
                    fragment.initUnitKnowledgeSelectorRv();
                    break;
                case UtilDatabase.ERROR_UNIT_LOADED:
                    fragment.showAllUnitsdFail();
                case UtilDatabase.UNIT_INFO:
                    if (UtilDatabase.courseUnitList.size() == 0){
                        fragment.showUnitLoadedFail();
                        return;
                    }
                    fragment.initCourseUnitSelectorRv();
                    break;
                case UtilDatabase.ERROR_COURSE:
                    fragment.showUnitLoadedFail();
                    break;
                case UtilDatabase.ADD_REPLY_SUCCESS:
                    fragment.tipDialog.dismiss();
                    fragment.showTip(TIP_TYPE_SUCCESS, "提交成功", DEFAULT_TIP_DURATION);
                    fragment.emptyView.show();
                    UtilDatabase.findProblemInfo(fragment);
                    break;
                case UtilDatabase.ADD_REPLY_FAIL:
                    fragment.tipDialog.dismiss();
                    fragment.showTip(TIP_TYPE_FAIL, "提交失败", DEFAULT_TIP_DURATION);
            }
        }
    }

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
        initEmptyView(fragmentView);
        initGroupList(fragmentView);
        initRecyclerView(fragmentView);
        initClassBottomSheet();
        initAllCourseBottomSheet();
        initUnitKnowledgeBottomSheet();
        initCourseUnitBottomSheet();
        return fragmentView;
    }

    private void initTopBar(View view) {
        QMUITopBar mTopBar = view.findViewById(R.id.home_topbar);
        mTopBar.setTitle("主页");
    }

    private void initEmptyView(View view){
        emptyView = (QMUIEmptyView) view.findViewById(R.id.emptyView);
        emptyView.hide();
    }

    private void initGroupList(View view){
        QMUIGroupListView groupList = (QMUIGroupListView) view.findViewById(R.id.info_group_list);

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
        problemsView.getSwitch().setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b == true){
                    emptyView.show(true);
                    UtilDatabase.findProblemInfo(HomeFragment.this);
                }else {
                    emptyView.show(false);
                }

            }
        });
        QMUIGroupListView.newSection(getContext())
                .setTitle("问答")
                .addItemView(problemsView, null)
                .addTo(groupList);
    }



    private void initClassBottomSheet(){
        classSelectorBs = new QMUIBottomSheet(parentActivity);
        classSelectorBs.setContentView(R.layout.bs_select_class);
        View view = classSelectorBs.getContentView();
        classSelectorRv = view.findViewById(R.id.class_selector_rv);
        classSelectorEv = view.findViewById(R.id.class_selector_ev);
    }

    private void initAllCourseBottomSheet(){
        courseAllSelectorBs = new QMUIBottomSheet(parentActivity);
        courseAllSelectorBs.setContentView(R.layout.bs_selector_all_course);
        View view = courseAllSelectorBs.getContentView();
        courseAllSelectorRv = view.findViewById(R.id.course_selector_rv);
        courseAllSlectorEV = view.findViewById(R.id.course_selector_ev);
    }

    private void initUnitKnowledgeBottomSheet(){
        u_kSelectorBs = new QMUIBottomSheet(parentActivity);
        u_kSelectorBs.setContentView(R.layout.bs_select_unit_knowledge);
        View view = u_kSelectorBs.getContentView();
        u_kSelectorRv = view.findViewById(R.id.u_k_selector_rv);
        u_kSelectorEv = view.findViewById(R.id.u_k_selector_ev);
    }

    private void initCourseUnitBottomSheet(){
        c_unitSelectorBs = new QMUIBottomSheet(parentActivity);
        c_unitSelectorBs.setContentView(R.layout.bs_select_unit);
        View view = c_unitSelectorBs.getContentView();
        c_unitSelectorRv = view.findViewById(R.id.unit_selector_rv);
        c_unitSelectorEv = view.findViewById(R.id.unit_selector_ev);
    }

    private void initClassSelectorRv(){
        ClassSelectorRvAdapter adapter = new ClassSelectorRvAdapter(UtilDatabase.classesList);
        adapter.setOnItemClickListener(new ClassSelectorRvAdapter.OnItemClickListener() {
            @Override
            public void onClick(int classIndex) {
                String classId = UtilDatabase.classesList.get(classIndex).getId();
                //显示班级的活动,活动需要重写
                StudentInfoActivity.actionStart(parentActivity, classId);
                classSelectorBs.dismiss();
            }
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(parentActivity);
        classSelectorRv.setLayoutManager(layoutManager);
        classSelectorRv.setAdapter(adapter);
        classSelectorEv.hide();
    }
    //初始化所有课程资源RecycleView
    private void initAllCourseSelectorRv(){
        CourseAllSelectorRvAdapter adapter = new CourseAllSelectorRvAdapter(UtilDatabase.courseAllList);
        adapter.setOnItemClickListener(new CourseAllSelectorRvAdapter.OnItemClickListener() {
            @Override
            public void onClick(int courseIndex) {
                //向TeacherResourceActivity传递课程Id
                courseId = UtilDatabase.courseAllList.get(courseIndex).getId();
                UtilDatabase.findUnits(HomeFragment.this,courseId);
                u_kSelectorBs.show();
                u_kSelectorEv.show(true);
                courseAllSelectorBs.dismiss();
            }
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(parentActivity);
        courseAllSelectorRv.setLayoutManager(layoutManager);
        courseAllSelectorRv.setAdapter(adapter);
        courseAllSlectorEV.hide();
    }

    private void initUnitKnowledgeSelectorRv(){
        if(unit_knowledgeRvAdapter == null){
            unit_knowledgeRvAdapter = new Unit_KnowledgeRvAdapter(UtilDatabase.unitsList, UtilDatabase.unitKnowledgeList);
            unit_knowledgeRvAdapter.setOnItemClickListener(new Unit_KnowledgeRvAdapter.OnItemClickListener() {
                @Override
                public void onClick(int unitIndex, int knowledgeIndex) {
                    int unitId = UtilDatabase.unitsList.get(unitIndex).getId();
                    int knowledgeId = UtilDatabase.unitKnowledgeList.get(unitIndex).get(knowledgeIndex).getId();
                    u_kSelectorBs.dismiss();
                    TeacherResourceActivity.actionStart(parentActivity, unitId, knowledgeId,courseId);
                }
            });
        } else {
            unit_knowledgeRvAdapter.notifyDataSetChanged();
            unit_knowledgeRvAdapter.collapseAllGroup();
        }
        LinearLayoutManager layoutManager = new LinearLayoutManager(parentActivity);
        u_kSelectorRv.setLayoutManager(layoutManager);
        u_kSelectorRv.setAdapter(unit_knowledgeRvAdapter);
        u_kSelectorEv.hide();
    }

    private void initCourseUnitSelectorRv(){
        if(courseUnitRvAdapter == null){
            courseUnitRvAdapter = new CourseUnitRvAdapter(UtilDatabase.courseList,UtilDatabase.courseUnitList);
            courseUnitRvAdapter.setOnItemClickListener(new CourseUnitRvAdapter.OnItemClickListener() {
                @Override
                public void onClick(int courseIndex, int unitIndex) {
                    int courseId = UtilDatabase.courseList.get(courseIndex).getId();
                    int unitId = UtilDatabase.courseUnitList.get(courseIndex).get(unitIndex).getId();
                    c_unitSelectorBs.dismiss();
                    TeacherAllTestQuestionInfoActivity.actionStart(parentActivity, courseIndex, unitIndex, courseId, unitId);
                }
            });
        } else {
            courseUnitRvAdapter.notifyDataSetChanged();
            courseUnitRvAdapter.collapseAllGroup();
        }
        LinearLayoutManager layoutManager = new LinearLayoutManager(parentActivity);
        c_unitSelectorRv.setLayoutManager(layoutManager);
        c_unitSelectorRv.setAdapter(courseUnitRvAdapter);
        c_unitSelectorEv.hide();
    }

    private View.OnClickListener studentInfoViewListener(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UtilDatabase.findClassInfo(HomeFragment.this);
                classSelectorBs.show();
                classSelectorEv.show(true);
            }
        };
    }
    //查询所有课程名
    private View.OnClickListener resourceViewListener(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UtilDatabase.findAllCourse(HomeFragment.this);
                courseAllSelectorBs.show();
                courseAllSlectorEV.show(true);
            }
        };
    }

    private View.OnClickListener questionViewListener(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UtilDatabase.findCourse(HomeFragment.this);
                c_unitSelectorBs.show();
                c_unitSelectorEv.show(true);

            }
        };
    }

    private void initRecyclerView(View view){
        recyclerView = (RecyclerView) view.findViewById(R.id.problem_recyclerview);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
    }

    private void resetData(){
        final TeacherQuizRvAdapter adapter = new TeacherQuizRvAdapter(problemList);
        adapter.setOnItemClickListener(new TeacherQuizRvAdapter.OnReplyQuizClickListener() {
            @Override
            public void onClick(int position) {
                final String problem = UtilDatabase.problemList.get(position+1).getProblem();
                showQuizContent = new QMUIDialog.CustomDialogBuilder(getContext())
                        .setLayout(R.layout.dialog_reply_quiz_content)
                        .setTitle("回复")
                        .addAction("取消", new QMUIDialogAction.ActionListener() {
                            @Override
                            public void onClick(QMUIDialog dialog, int index) {
                                dialog.dismiss();
                            }
                        })
                        .addAction("提交", new QMUIDialogAction.ActionListener() {
                            @Override
                            public void onClick(QMUIDialog dialog, int index) {
                                submitQuizContent(problem);
                                dialog.dismiss();

                            }
                        })
                        .create();
                replyContent = (EditText)showQuizContent.findViewById(R.id.dialog_reply_quiz_content);
                showQuizContent.show();
            }
        });
        recyclerView.setAdapter(adapter);
    }
    private void submitQuizContent(String problem){
        showLoadingTip("正在提交，请稍后");
        if(!UtilNetwork.isNetworkAvailable()){
            showTip(TIP_TYPE_FAIL, "网络不可用", DEFAULT_TIP_DURATION);
            return;
        } else{
            String p = replyContent.getText().toString();
            if(p.equals("")){
                showTip(TIP_TYPE_FAIL, "输入信息不能为空", DEFAULT_TIP_DURATION);
                return;
            }else {
                UtilDatabase.addReply(this, problem, p);
            }
        }
    }

    private void showClassesLoadedFail(){
        if(!UtilNetwork.isNetworkAvailable()){
            classSelectorEv.show("网络异常", "请确认网络畅通后重试");
        } else {
            classSelectorEv.show("班级离家出走了", "休息一下吧~");
        }
    }
    private void showAllCourseLoadedFail(){
        if(!UtilNetwork.isNetworkAvailable()){
            courseAllSlectorEV.show("网络异常", "请确认网络畅通后重试");
        } else {
            courseAllSlectorEV.show("课程资源离家出走了", "休息一下吧~");
        }
    }

    private void showAllUnitsdFail(){
        if(!UtilNetwork.isNetworkAvailable()){
            u_kSelectorEv.show("网络异常", "请确认网络畅通后重试");
        } else {
            u_kSelectorEv.show("课程单元离家出走了", "休息一下吧~");
        }
    }
    private void showUnitLoadedFail(){
        if(!UtilNetwork.isNetworkAvailable()){
            c_unitSelectorEv.show("网络异常", "请确认网络畅通后重试");
        } else {
            c_unitSelectorEv.show("单元离家出走了", "休息一下吧~");
        }
    }
    private void showTip(int type, String tipWord, long duration){
        QMUITipDialog.Builder tipBuilder = new QMUITipDialog.Builder(parentActivity);
        if(type == TIP_TYPE_SUCCESS){
            tipBuilder = tipBuilder.setIconType(QMUITipDialog.Builder.ICON_TYPE_SUCCESS);
        } else if(type == TIP_TYPE_FAIL){
            tipBuilder = tipBuilder.setIconType(QMUITipDialog.Builder.ICON_TYPE_FAIL);
        }
        tipBuilder = tipBuilder.setTipWord(tipWord);
        tipDialog = tipBuilder.create();
        tipDialog.show();
        fragmentView.postDelayed(new Runnable() {
            @Override
            public void run() {
                tipDialog.dismiss();
            }
        }, duration);
    }

    private void showLoadingTip(String tip){
        tipDialog = new QMUITipDialog.Builder(getContext())
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                .setTipWord(tip)
                .create(false);
        tipDialog.show();
    }
    @Override
    public void onClick(View view) {

    }
}
