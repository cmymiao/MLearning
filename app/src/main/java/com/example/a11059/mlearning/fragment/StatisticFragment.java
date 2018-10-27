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

import com.example.a11059.mlearning.R;
import com.example.a11059.mlearning.activity.StatisticActivity;
import com.example.a11059.mlearning.activity.TeacherMainActivity;
import com.example.a11059.mlearning.adapter.ClassSelectorRvAdapter;
import com.example.a11059.mlearning.adapter.CourseUnitRvAdapter;
import com.example.a11059.mlearning.adapter.UnitSelectorRvAdapter;
import com.example.a11059.mlearning.entity.Statistic;
import com.example.a11059.mlearning.entity.User;
import com.example.a11059.mlearning.utils.UtilDatabase;
import com.example.a11059.mlearning.utils.UtilNetwork;
import com.qmuiteam.qmui.layout.QMUILinearLayout;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.qmuiteam.qmui.widget.QMUIEmptyView;
import com.qmuiteam.qmui.widget.QMUITopBar;
import com.qmuiteam.qmui.widget.dialog.QMUIBottomSheet;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;

import java.lang.ref.WeakReference;

import cn.bmob.v3.BmobUser;

import static com.example.a11059.mlearning.utils.UtilDatabase.UNITS_FIND;

public class StatisticFragment extends Fragment implements View.OnClickListener {

    private static final long DEFAULT_TIP_DURATION = 1000;

    private View fragmentView;

    private TeacherMainActivity parentActivity;

    private QMUITipDialog tipDialog;

    private QMUIBottomSheet c_unitSelectorBs;

    private RecyclerView c_unitSelectorRv;

    private QMUIEmptyView c_unitSelectorEv;

    private QMUIBottomSheet classSelectorBs;

    private RecyclerView classSelectorRv;

    private QMUIEmptyView classSelectorEv;

    private CourseUnitRvAdapter adapter;

    private String selectClassId = "";

    private int selectType = -1;

    public MyHandler handler = new MyHandler(this);

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.statis_entry_one:
                selectClassId = "";
                selectType = StatisticActivity.STATIS_FOR_QUESTION;
                sEntryOneClickAction();
                break;
            case R.id.statis_entry_two:
                selectType = StatisticActivity.STATIS_FOR_STUDENT;
                sEntryTwoClickAction();
                break;
            case R.id.statis_entry_three:
                selectType = StatisticActivity.STATIS_FOR_STUNUM;
                sEntryThreeClickAction();
                break;
            default:
                break;
        }
    }

    public static class MyHandler extends Handler {

        private WeakReference<StatisticFragment> mFragment;

        public MyHandler(StatisticFragment fragment){
            mFragment = new WeakReference<StatisticFragment>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            StatisticFragment fragment = mFragment.get();
            switch (msg.what){
                case UtilDatabase.UNITS_FIND:
                    if(UtilDatabase.courseList.size() == 0){
                        fragment.showUnitLoadedFail();
                        return;
                    }
                    fragment.initCourseUnitSelectorRv();
                    break;
                case UtilDatabase.ERROR_UNIT_LOADED:
                    fragment.showUnitLoadedFail();
                    break;
                case UtilDatabase.CLASS_INFO:
                    if(UtilDatabase.classList.size() == 0){
                        fragment.showUnitLoadedFail();
                        return;
                    }
                    fragment.initClassSelectorRv();
                    break;
                case UtilDatabase.ERROR:
                    break;
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
        fragmentView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_statistic, container, false);
        initTopBar(fragmentView);
        initStatisEnties(fragmentView);
        initCourseUnitBottomSheet();
        initClassUnitBottomSheet();
        return fragmentView;
    }

    private void initTopBar(View view){
        QMUITopBar topBar = (QMUITopBar) view.findViewById(R.id.statis_topbar);
        topBar.setTitle("统计中心");
    }

    private void initStatisEnties(View view){
        float mShadowAlpha = 0.4f;
        int mShadowElevation = QMUIDisplayHelper.dp2px(parentActivity, 5);
        int mRadius = QMUIDisplayHelper.dp2px(parentActivity, 5);
        QMUILinearLayout statisEntryOne = (QMUILinearLayout) view.findViewById(R.id.statis_entry_one);
        QMUILinearLayout statisEntryTwo = (QMUILinearLayout) view.findViewById(R.id.statis_entry_two);
        QMUILinearLayout statisEntryThree = (QMUILinearLayout) view.findViewById(R.id.statis_entry_three);
        statisEntryOne.setRadiusAndShadow(mRadius, mShadowElevation, mShadowAlpha);
        statisEntryTwo.setRadiusAndShadow(mRadius, mShadowElevation, mShadowAlpha);
        statisEntryThree.setRadiusAndShadow(mRadius, mShadowElevation, mShadowAlpha);
        statisEntryOne.setOnClickListener(this);
        statisEntryTwo.setOnClickListener(this);
        statisEntryThree.setOnClickListener(this);
    }

    private void initCourseUnitBottomSheet(){
        c_unitSelectorBs = new QMUIBottomSheet(parentActivity);
        c_unitSelectorBs.setContentView(R.layout.bs_select_unit);
        View view = c_unitSelectorBs.getContentView();
        c_unitSelectorRv = view.findViewById(R.id.unit_selector_rv);
        c_unitSelectorEv = view.findViewById(R.id.unit_selector_ev);
    }

    private void initCourseUnitSelectorRv(){
        if(adapter == null){
            adapter = new CourseUnitRvAdapter(UtilDatabase.courseList, UtilDatabase.courseUnitList);
            adapter.setOnItemClickListener(new CourseUnitRvAdapter.OnItemClickListener() {
                @Override
                public void onClick(int courseIndex, int unitIndex) {
                    int courseId = UtilDatabase.courseList.get(courseIndex).getId();
                    String courseName = UtilDatabase.courseList.get(courseIndex).getName();
                    int unitId = UtilDatabase.courseUnitList.get(courseIndex).get(unitIndex).getId();
                    c_unitSelectorBs.dismiss();
                    StatisticActivity.actionStart(parentActivity, selectType, selectClassId, courseId, courseName, unitId, "");
                }
            });
        } else {
            adapter.notifyDataSetChanged();
            adapter.collapseAllGroup();
        }
        LinearLayoutManager layoutManager = new LinearLayoutManager(parentActivity);
        c_unitSelectorRv.setLayoutManager(layoutManager);
        c_unitSelectorRv.setAdapter(adapter);
        c_unitSelectorEv.hide();
    }

    private void initClassUnitBottomSheet(){
        classSelectorBs = new QMUIBottomSheet(parentActivity);
        classSelectorBs.setContentView(R.layout.bs_select_class);
        View view = classSelectorBs.getContentView();
        classSelectorRv = view.findViewById(R.id.class_selector_rv);
        classSelectorEv = view.findViewById(R.id.class_selector_ev);
    }

    private void initClassSelectorRv(){
        ClassSelectorRvAdapter adapter = new ClassSelectorRvAdapter(UtilDatabase.classList);
        adapter.setOnItemClickListener(new ClassSelectorRvAdapter.OnItemClickListener() {
            @Override
            public void onClick(int classIndex) {
                String classId = UtilDatabase.classList.get(classIndex).getId();
                selectClassId = classId;
                classSelectorBs.dismiss();
                UtilDatabase.findCourse(StatisticFragment.this);
                c_unitSelectorBs.show();
                c_unitSelectorEv.show(true);
            }
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(parentActivity);
        classSelectorRv.setLayoutManager(layoutManager);
        classSelectorRv.setAdapter(adapter);
        classSelectorEv.hide();
    }

    private void sEntryOneClickAction(){
        if(!UtilNetwork.isNetworkAvailable()){
            showBadNetworkTip();
            return;
        }
        UtilDatabase.findCourse(this);
        c_unitSelectorBs.show();
        c_unitSelectorEv.show(true);

    }

    private void sEntryTwoClickAction(){
        if(!UtilNetwork.isNetworkAvailable()){
            showBadNetworkTip();
            return;
        }
        UtilDatabase.findClasses(this);
        classSelectorBs.show();
        classSelectorEv.show(true);
    }

    private void sEntryThreeClickAction(){
        if(!UtilNetwork.isNetworkAvailable()){
            showBadNetworkTip();
            return;
        }
        UtilDatabase.findClasses(this);
        classSelectorBs.show();
        classSelectorEv.show(true);
    }

    private void showBadNetworkTip(){
        QMUITipDialog.Builder tipBuilder = new QMUITipDialog.Builder(parentActivity);
        tipBuilder = tipBuilder.setIconType(QMUITipDialog.Builder.ICON_TYPE_FAIL);
        tipBuilder = tipBuilder.setTipWord("网络不可用");
        tipDialog = tipBuilder.create();
        tipDialog.show();
        fragmentView.postDelayed(new Runnable() {
            @Override
            public void run() {
                tipDialog.dismiss();
            }
        }, DEFAULT_TIP_DURATION);
    }

    private void showUnitLoadedFail(){
        if(!UtilNetwork.isNetworkAvailable()){
            c_unitSelectorEv.show("网络异常", "请确认网络畅通后重试");
        } else {
            c_unitSelectorEv.show("单元离家出走了", "休息一下吧~");
        }
    }
}
