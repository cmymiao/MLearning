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
import android.widget.Button;
import android.widget.Toast;

import com.example.a11059.mlearning.R;
import com.example.a11059.mlearning.activity.PdfViewActivity;
import com.example.a11059.mlearning.activity.QuestionActivity;
import com.example.a11059.mlearning.activity.QuestionLocalActivity;
import com.example.a11059.mlearning.activity.ResourceActivity;
import com.example.a11059.mlearning.activity.StudentMainActivity;
import com.example.a11059.mlearning.activity.StudentStatisticActivity;
import com.example.a11059.mlearning.adapter.ExamSelectorRvAdapter;
import com.example.a11059.mlearning.adapter.UnitSelectorRvAdapter;
import com.example.a11059.mlearning.adapter.Unit_KnowledgeRvAdapter;
import com.example.a11059.mlearning.entity.User;
import com.example.a11059.mlearning.utils.UtilDatabase;
import com.example.a11059.mlearning.utils.UtilNetwork;
import com.qmuiteam.qmui.widget.QMUIEmptyView;
import com.qmuiteam.qmui.widget.QMUITopBar;
import com.qmuiteam.qmui.widget.dialog.QMUIBottomSheet;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;

import java.lang.ref.WeakReference;

import cn.bmob.v3.BmobUser;

/**
 * Created by 11059 on 2018/7/21.
 */

public class LearnFragment extends Fragment implements View.OnClickListener {

    private View fragmentView;

    private QMUITipDialog tipDialog;

    private StudentMainActivity parentActivity;

    private QMUIBottomSheet unitSelectorBs;

    private RecyclerView unitSelectorRv;

    private QMUIEmptyView unitSelectorEv;

    private QMUIBottomSheet examSelectorBs;

    private RecyclerView examSelectorRv;

    private QMUIEmptyView examSelectorEv;

    private QMUIBottomSheet u_kSelectorBs;

    private RecyclerView u_kSelectorRv;

    private QMUIEmptyView u_kSelectorEv;

    private Unit_KnowledgeRvAdapter adapter;

    private String courseName = "";

    private String courseInfo = "";

    public MyHandler handler = new MyHandler(this);

    public static class MyHandler extends Handler {

        private WeakReference<LearnFragment> mFragment;

        public MyHandler(LearnFragment fragment){
            mFragment = new WeakReference<>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            LearnFragment fragment = mFragment.get();
            switch (msg.what){
                case UtilDatabase.UNIT_INFO:
                    if(UtilDatabase.unitList.size() == 0){
                        fragment.showUnitLoadedFail();
                        return;
                    }
                    fragment.initUnitSelectorRv();
                    break;
                case UtilDatabase.ERRO_UNIT:
                    fragment.showUnitLoadedFail();
                    break;
                case UtilDatabase.EXAM_INFO:
                    if(UtilDatabase.examList.size() == 0){
                        fragment.showExamLoadedFail();
                        return;
                    }
                    fragment.initExamSelectorRv();
                    break;
                case UtilDatabase.ERROR_EXAM:
                    fragment.showExamLoadedFail();
                    break;
                case UtilDatabase.COURSE_NAME:
                    fragment.courseName = UtilDatabase.courseName;
                    fragment.initTopBar(fragment.fragmentView);
                    break;
                case UtilDatabase.UNITS_FIND:
                    if(UtilDatabase.unitsList.size() == 0){
                        fragment.showUnitLoadedFail();
                        return;
                    }
                    fragment.initUnitKnowledgeSelectorRv();
                    break;
                case UtilDatabase.ERROR_UNIT_LOADED:
                    fragment.showUnitLoadedFail();
                    break;
                case UtilDatabase.ERROR:
                    fragment.tipDialog.dismiss();
                    Toast.makeText(fragment.getActivity(), "获取失败", Toast.LENGTH_SHORT).show();
                    break;
                case UtilDatabase.GET_SCHEDULE:
                    fragment.courseInfo = UtilDatabase.schedule;
                    String[] schedule = fragment.courseInfo.split(";");
                    StringBuilder info = new StringBuilder();
                    for(int i = 0; i < schedule.length; i++){
                        info.append(i + 1).append(". 周").append(schedule[i].substring(0, 1)).append(", ").append(schedule[i].substring(1, 3)).append("节, ");
                        switch (schedule[i].substring(3, 4)){
                            case "1":
                                info.append("单周, 第");
                                break;
                            case "2":
                                info.append("双周, 第");
                                break;
                        }
                        info.append(Integer.parseInt(schedule[i].substring(4, 6))).append("周 - 第").append(Integer.parseInt(schedule[i].substring(6, 8))).append("周，上课地点：").append(schedule[i].substring(8)).append("\n");
                    }
                    fragment.courseInfo = info.toString();
                    fragment.tipDialog.dismiss();
                    fragment.showCourseInfo();
                    break;
                case UtilDatabase.GET_PROGRAM:
                    fragment.tipDialog.dismiss();
                    PdfViewActivity.actionStart(fragment.parentActivity, UtilDatabase.bmobFile.getUrl(), "课程大纲");
                    break;
                case UtilDatabase.GET_EXPERIMENT:
                    fragment.tipDialog.dismiss();
                    PdfViewActivity.actionStart(fragment.parentActivity, UtilDatabase.bmobFile.getUrl(), "实验大纲");
                    break;
                case UtilDatabase.GET_TIME:
                    fragment.tipDialog.dismiss();
                    PdfViewActivity.actionStart(fragment.parentActivity, UtilDatabase.bmobFile.getUrl(), "教学日历");
                    break;
            }
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        parentActivity = (StudentMainActivity) getActivity();
        User user = BmobUser.getCurrentUser(User.class);
        UtilDatabase.findCourseNameById(LearnFragment.this, user.getCourseId());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_learn, container, false);
        //initTopBar(fragmentView);
        initTrainButtons(fragmentView);
        initUnitBottomSheet();
        initExamBottomSheet();
        initUnitKnowledgeBottomSheet();
        return fragmentView;
    }

    private void initTopBar(View view) {
        QMUITopBar mTopBar = view.findViewById(R.id.train_topbar);
        mTopBar.addRightImageButton(R.drawable.submit_exam, R.id.showCourseInfo).setOnClickListener(this);
        mTopBar.setTitle(courseName);
    }

    private void initTrainButtons(View view){
        Button sequenceTarin = view.findViewById(R.id.sequence_learning);
        Button unitTrain = view.findViewById(R.id.unit_learning);
        Button randomTrain = view.findViewById(R.id.random_learning);
        Button simulateExam = view.findViewById(R.id.simulate_exam);
        Button mistake = view.findViewById(R.id.mistake);
        Button collection = view.findViewById(R.id.collection);
        Button statistic = view.findViewById(R.id.statistics);
        Button material = view.findViewById(R.id.material);

        sequenceTarin.setOnClickListener(this);
        unitTrain.setOnClickListener(this);
        randomTrain.setOnClickListener(this);
        simulateExam.setOnClickListener(this);
        mistake.setOnClickListener(this);
        collection.setOnClickListener(this);
        statistic.setOnClickListener(this);
        material.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.sequence_learning:
                sequenceTarinClickAction();
                break;
            case R.id.unit_learning:
                unitTrainClickAction();
                break;
            case R.id.random_learning:
                randomExamClickAction();
                break;
            case R.id.simulate_exam:
                simulateExamClickAction();
                break;
            case R.id.mistake:
                mistakeClickAction();
                break;
            case R.id.collection:
                collectionClickAction();
                break;
            case R.id.statistics:
                showStatisticBottomSheet();
                break;
            case R.id.material:
                resourceClickAction();
                break;
            case R.id.showCourseInfo:
                showCourseInfoBottomSheet();
                break;
            default:
                break;
        }
    }

    private void sequenceTarinClickAction(){
        QuestionActivity.actionStart(parentActivity, QuestionActivity.TRAIN_MODE_SEQUENCE, -1, -1);
    }

    private void unitTrainClickAction(){
        UtilDatabase.findUnit(this);
        unitSelectorBs.show();
        unitSelectorEv.show(true);
    }

    private void randomExamClickAction(){
        QuestionActivity.actionStart(parentActivity, QuestionActivity.TRAIN_MODE_RANDOM, -1, -1);
    }

    private void simulateExamClickAction(){
        UtilDatabase.findExam(this);
        examSelectorBs.show();
        examSelectorEv.show(true);
    }

    private void mistakeClickAction(){
        QuestionLocalActivity.actionStart(parentActivity, QuestionLocalActivity.START_MODE_MISTAKE);
    }

    private void collectionClickAction(){
        QuestionLocalActivity.actionStart(parentActivity, QuestionLocalActivity.START_MODE_COLLECTION);
    }

    private void showStatisticBottomSheet(){
        new QMUIBottomSheet.BottomListSheetBuilder(getActivity())
                .addItem("查看每单元答题情况")
                .addItem("查看本班同学答题情况")
                .setOnSheetItemClickListener(new QMUIBottomSheet.BottomListSheetBuilder.OnSheetItemClickListener() {
                    @Override
                    public void onClick(QMUIBottomSheet dialog, View itemView, int position, String tag) {
                        dialog.dismiss();
                        switch (position){
                            case 0:
                                StudentStatisticActivity.actionStart(getContext(), "单元答题情况", StudentStatisticActivity.STATISTIC_UNIT);
                                break;
                            case 1:
                                StudentStatisticActivity.actionStart(getContext(), "本班准确率最高同学", StudentStatisticActivity.STATISTIC_CLASS);
                                break;
                            default:
                                break;
                        }
                    }
                })
                .build()
                .show();
    }

    private void resourceClickAction(){
        UtilDatabase.findUnits(LearnFragment.this);
        u_kSelectorBs.show();
        u_kSelectorEv.show(true);
    }

    private void initUnitBottomSheet(){
        unitSelectorBs = new QMUIBottomSheet(parentActivity);
        unitSelectorBs.setContentView(R.layout.bs_select_unit);
        View view = unitSelectorBs.getContentView();
        unitSelectorRv = view.findViewById(R.id.unit_selector_rv);
        unitSelectorEv = view.findViewById(R.id.unit_selector_ev);
    }

    private void initUnitSelectorRv(){
        UnitSelectorRvAdapter adapter = new UnitSelectorRvAdapter(UtilDatabase.unitList);
        adapter.setOnItemClickListener(new UnitSelectorRvAdapter.OnItemClickListener() {
            @Override
            public void onClick(int unitIndex) {
                int unitId = UtilDatabase.unitList.get(unitIndex).getId();
                QuestionActivity.actionStart(parentActivity, QuestionActivity.TRAIN_MODE_UNIT, unitId, -1);
                unitSelectorBs.dismiss();
            }
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(parentActivity);
        unitSelectorRv.setLayoutManager(layoutManager);
        unitSelectorRv.setAdapter(adapter);
        unitSelectorEv.hide();
    }

    private void initExamBottomSheet(){
        examSelectorBs = new QMUIBottomSheet(parentActivity);
        examSelectorBs.setContentView(R.layout.bs_selector_exam);
        View view = examSelectorBs.getContentView();
        examSelectorRv = view.findViewById(R.id.exam_selector_rv);
        examSelectorEv = view.findViewById(R.id.exam_selector_ev);
    }

    private void initExamSelectorRv(){
        ExamSelectorRvAdapter adapter = new ExamSelectorRvAdapter(UtilDatabase.examList);
        adapter.setOnItemClickListener(new ExamSelectorRvAdapter.OnItemClickListener() {
            @Override
            public void onClick(int examIndex) {
                int examId = UtilDatabase.examList.get(examIndex).getId();
                examSelectorBs.dismiss();
                QuestionActivity.actionStart(parentActivity, QuestionActivity.TRAIN_MODE_SIMULATEEXAM, -1, examId);

            }
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(parentActivity);
        examSelectorRv.setLayoutManager(layoutManager);
        examSelectorRv.setAdapter(adapter);
        examSelectorEv.hide();
    }

    private void initUnitKnowledgeBottomSheet(){
        u_kSelectorBs = new QMUIBottomSheet(parentActivity);
        u_kSelectorBs.setContentView(R.layout.bs_select_unit_knowledge);
        View view = u_kSelectorBs.getContentView();
        u_kSelectorRv = view.findViewById(R.id.u_k_selector_rv);
        u_kSelectorEv = view.findViewById(R.id.u_k_selector_ev);
    }

    private void initUnitKnowledgeSelectorRv(){
        if(adapter == null){
            adapter = new Unit_KnowledgeRvAdapter(UtilDatabase.unitsList, UtilDatabase.unitKnowledgeList);
            adapter.setOnItemClickListener(new Unit_KnowledgeRvAdapter.OnItemClickListener() {
                @Override
                public void onClick(int unitIndex, int knowledgeIndex) {
                    int unitId = UtilDatabase.unitsList.get(unitIndex).getId();
                    int knowledgeId = UtilDatabase.unitKnowledgeList.get(unitIndex).get(knowledgeIndex).getId();
                    u_kSelectorBs.dismiss();
                    ResourceActivity.actionStart(parentActivity, unitId, knowledgeId);

                }
            });
        } else {
            adapter.notifyDataSetChanged();
            adapter.collapseAllGroup();
        }
        LinearLayoutManager layoutManager = new LinearLayoutManager(parentActivity);
        u_kSelectorRv.setLayoutManager(layoutManager);
        u_kSelectorRv.setAdapter(adapter);
        u_kSelectorEv.hide();
    }

    private void showUnitLoadedFail(){
        if(!UtilNetwork.isNetworkAvailable()){
            unitSelectorEv.show("网络异常", "请确认网络畅通后重试");
        } else {
            unitSelectorEv.show("单元离家出走了", "休息一下吧~");
        }
    }

    private void showExamLoadedFail(){
        if(!UtilNetwork.isNetworkAvailable()){
            unitSelectorEv.show("网络异常", "请确认网络畅通后重试");
        } else {
            unitSelectorEv.show("试卷离家出走了", "休息一下吧~");
        }
    }

    private void showCourseInfoBottomSheet() {
        new QMUIBottomSheet.BottomListSheetBuilder(getActivity())
                .addItem("课程表")
                .addItem("课程大纲")
                .addItem("实验大纲")
                .addItem("教学日历")
                .setOnSheetItemClickListener(new QMUIBottomSheet.BottomListSheetBuilder.OnSheetItemClickListener() {
                    @Override
                    public void onClick(QMUIBottomSheet dialog, View itemView, int position, String tag) {
                        dialog.dismiss();
                        switch (position){
                            case 0:
                                showLoadingTip("加载中");
                                UtilDatabase.findSchedule(LearnFragment.this);
                                break;
                            case 1:
                                showLoadingTip("加载中");
                                UtilDatabase.findCourseInfo(LearnFragment.this, "program");
                                break;
                            case 2:
                                showLoadingTip("加载中");
                                UtilDatabase.findCourseInfo(LearnFragment.this, "experiment");
                                break;
                            case 3:
                                showLoadingTip("加载中");
                                UtilDatabase.findCourseInfo(LearnFragment.this, "time");
                                break;
                                default:
                                    break;
                        }
                    }
                })
                .build()
                .show();
    }

    private void showCourseInfo(){
        if(courseInfo.equals("")){
            courseInfo = "未获取到相关信息，请重试。";
        }
        new QMUIDialog.MessageDialogBuilder(getActivity())
                .setTitle("课程表")
                .setMessage(courseInfo)
                .addAction("确认", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        courseInfo = "";
                        dialog.dismiss();
                    }
                })
                .setCanceledOnTouchOutside(false)
                .create()
                .show();
    }

    private void showLoadingTip(String tip){
        tipDialog = new QMUITipDialog.Builder(getActivity())
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                .setTipWord(tip)
                .create(false);
        tipDialog.show();
    }


}
