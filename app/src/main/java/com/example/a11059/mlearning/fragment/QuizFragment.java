package com.example.a11059.mlearning.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.a11059.mlearning.R;
import com.example.a11059.mlearning.activity.LoginActivity;
import com.example.a11059.mlearning.activity.StudentMainActivity;
import com.example.a11059.mlearning.adapter.QuizRvAdapter;
import com.example.a11059.mlearning.entity.Problem;
import com.example.a11059.mlearning.entity.User;
import com.example.a11059.mlearning.utils.UtilDatabase;
import com.example.a11059.mlearning.utils.UtilNetwork;
import com.example.a11059.mlearning.widget.FixLinearSnapHelper;
import com.qmuiteam.qmui.widget.QMUIEmptyView;
import com.qmuiteam.qmui.widget.QMUITopBar;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobUser;

/**
 * Created by 11059 on 2018/7/21.
 */

public class QuizFragment extends Fragment implements View.OnClickListener {

    private static final int TIP_TYPE_SUCCESS = 1;

    private static final int TIP_TYPE_FAIL = 0;

    private static final long DEFAULT_TIP_DURATION = 1000;

    private View fragmentView;

    private StudentMainActivity parentActivity;

    private User user;

    private QMUIEmptyView emptyView;

    private QMUITopBar topBar;

    private RecyclerView recyclerView;

    private LinearLayoutManager layoutManager;

    private QMUIDialog showQuizContent;

    private EditText quizContent;

    private QMUITipDialog tipDialog;

    private List<Problem> problemList = new ArrayList<>();

    public MyHandler handler = new MyHandler(this);

    public static class MyHandler extends Handler {

        private final WeakReference<QuizFragment> mFragment;

        private MyHandler(QuizFragment fragment){
            mFragment = new WeakReference<QuizFragment>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            QuizFragment fragment = mFragment.get();
            switch (msg.what){
                case UtilDatabase.PROBLEM_INFO:
                    fragment.problemList = UtilDatabase.problemList;
                    if (fragment.problemList.size() == 0){
                        fragment.emptyView.show("未获取到留言", "有疑问可以点击右上角进行留言哦");
                    }else {
                        fragment.emptyView.hide();
                        fragment.resetData();
                    }
                    break;
                case UtilDatabase.ERROR_PROBLEM:
                    fragment.emptyView.show("未获取到留言", "有疑问可以点击右上角进行留言哦");
                    break;
                case UtilDatabase.ADD_PROBLEM_SUCCESS:
                    fragment.tipDialog.dismiss();
                    fragment.showTip(TIP_TYPE_SUCCESS, "提交成功", DEFAULT_TIP_DURATION);
                    fragment.emptyView.show();
                    UtilDatabase.findAllProblem(fragment);
                    break;
                case UtilDatabase.ADD_FAIL:
                    fragment.tipDialog.dismiss();
                    fragment.showTip(TIP_TYPE_FAIL, "提交失败", DEFAULT_TIP_DURATION);
                    break;
            }
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        parentActivity = (StudentMainActivity) getActivity();
        user = BmobUser.getCurrentUser(User.class);
        if(user == null){ //验证当前登录人是否存在
            LoginActivity.actionStart(getActivity(), false);
            parentActivity.finish();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_quiz, container, false);
        initTopBar(fragmentView);
        initEmptyView(fragmentView);
        initRecyclerView(fragmentView);
        UtilDatabase.findAllProblem(QuizFragment.this);
        return fragmentView;
    }

    private void initEmptyView(View view){
        emptyView = (QMUIEmptyView) view.findViewById(R.id.emptyView);
        emptyView.show();
    }

    private void initTopBar(View view){
        topBar = (QMUITopBar) view.findViewById(R.id.quiz_topbar);
        topBar.setTitle("问答");
        topBar.addRightTextButton("留言", R.id.quiz).setOnClickListener(this);
    }

    private void initRecyclerView(View view){
        recyclerView = (RecyclerView) view.findViewById(R.id.quiz_recyclerview);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
    }

    private void resetData(){
        QuizRvAdapter adapter = new QuizRvAdapter(problemList);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.quiz:
                showQuizContent = new QMUIDialog.CustomDialogBuilder(getContext())
                        .setLayout(R.layout.dialog_quiz_content)
                        .setTitle("提问")
                        .addAction("取消", new QMUIDialogAction.ActionListener() {
                            @Override
                            public void onClick(QMUIDialog dialog, int index) {
                                dialog.dismiss();
                            }
                        })
                        .addAction("提交", new QMUIDialogAction.ActionListener() {
                            @Override
                            public void onClick(QMUIDialog dialog, int index) {
                                submitQuizContent();
                                dialog.dismiss();
                            }
                        })
                        .create();
                quizContent = (EditText)showQuizContent.findViewById(R.id.dialog_quiz_content);
                showQuizContent.show();
                break;
        }
    }

    private void submitQuizContent(){
        showLoadingTip("正在提交，请稍后");
        if(!UtilNetwork.isNetworkAvailable()){
            showTip(TIP_TYPE_FAIL, "网络不可用", DEFAULT_TIP_DURATION);
            return;
        } else{
            String p = quizContent.getText().toString();
            if(p.equals("")){
                showTip(TIP_TYPE_FAIL, "输入信息不能为空", DEFAULT_TIP_DURATION);
                return;
            }else {
                UtilDatabase.addProblem(QuizFragment.this, p);
            }
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
}
