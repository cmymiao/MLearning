package com.example.a11059.mlearning.activity;

import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.a11059.mlearning.R;
import com.example.a11059.mlearning.adapter.QuestionRvAdapter;
import com.example.a11059.mlearning.entity.Examination;
import com.example.a11059.mlearning.entity.Question;
import com.example.a11059.mlearning.entity.RecordL;
import com.example.a11059.mlearning.entity.SequenceGroupL;
import com.example.a11059.mlearning.entity.Unit;
import com.example.a11059.mlearning.entity.User;
import com.example.a11059.mlearning.utils.UtilDatabase;
import com.example.a11059.mlearning.utils.UtilNetwork;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.qmuiteam.qmui.widget.QMUIEmptyView;
import com.qmuiteam.qmui.widget.QMUITopBar;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
import com.qmuiteam.qmui.widget.popup.QMUIPopup;

import org.litepal.crud.DataSupport;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import cn.bmob.v3.BmobUser;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class QuestionActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int TIP_TYPE_SUCCESS = 1;

    private static final int TIP_TYPE_FAIL = 0;

    private static final int TIP_TYPE_INFO = 2;

    private static final long DEFAULT_TIP_DURATION = 1000;

    public static final int TRAIN_MODE_SEQUENCE = 1;

    public static final int TRAIN_MODE_UNIT = 2;

    public static final int TRAIN_MODE_RANDOM = 3;

    public static final int TRAIN_MODE_SIMULATEEXAM = 4;

    private static final int DEFAULT_GROUP_NUM = 10;

    public static int currentTrainMode = TRAIN_MODE_SEQUENCE;

    private static int currentUnitId = 1;

    private static int currentExamId = 1;

    private int maxGroupNo = 1;

    private int groupNo = 0;

    private int randomGroup = 0;

    private int left = 0;

    private int questionNum = 0;

    private SequenceGroupL currentUserSG;

    private QMUITopBar topBar;

    private QMUIEmptyView emptyView;

    private boolean isQuestionLoaded = false;

    private RecyclerView recyclerView;

    private LinearLayoutManager layoutManager;

    private QMUIPopup mNormalPopup;

    private Button btnPrev;

    private Button btnAnalyze;

    private Button btnNext;

    private QMUITipDialog tipDialog;

    private int countDownTime = 60;//按秒计

    private int[] randNum;

    public MyHandler handler = new MyHandler(this);

    private List<Question> questionList = new ArrayList<>();
    private CountDownTimer myTimer;

    public static class MyHandler extends Handler {

        private final WeakReference<QuestionActivity> mActivity;

        public MyHandler(QuestionActivity activity){
            mActivity = new WeakReference<QuestionActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            final QuestionActivity activity = mActivity.get();
            switch (msg.what){
                case UtilDatabase.ERROR:
                    activity.showEmptyViewTip();
                    break;
                case UtilDatabase.QUESTION_NUM_INFO:
                    activity.questionNum = UtilDatabase.questionNum;
                    int yu = UtilDatabase.questionNum % QuestionActivity.DEFAULT_GROUP_NUM;
                    int no = UtilDatabase.questionNum / QuestionActivity.DEFAULT_GROUP_NUM;
                    activity.maxGroupNo = yu == 0 ? no - 1 : no;
                    activity.randNum = activity.setRandomData();
                    activity.requestQuestionData();
                    break;
                case UtilDatabase.QUESTION_INFO:
                    activity.questionList = UtilDatabase.questionList;
                    if(activity.questionList.size() == 0){
                        activity.showEmptyViewTip();
                        return;
                    }
                    activity.resetAdapter();
                    activity.isQuestionLoaded = true;
                    activity.emptyView.hide();
                    if(activity.currentTrainMode == activity.TRAIN_MODE_SIMULATEEXAM){
                        activity.setTimer(activity.countDownTime);
                    }
                    break;
            }
        }
    }

    public static void actionStart(Context context, int trainMode, int unitId, int examId){
        Intent intent = new Intent(context, QuestionActivity.class);
        currentTrainMode = trainMode;
        currentUnitId = unitId;
        currentExamId = examId;
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);
        QMUIStatusBarHelper.translucent(this);
        initLocalData();
        initEmptyView();
        initTopBar();
        initBottomBar();
        initRecyclerView();
        if(currentTrainMode == TRAIN_MODE_SEQUENCE || currentTrainMode == TRAIN_MODE_RANDOM){ //顺序学习先获取一下总数目
            UtilDatabase.findQuestionNum(this);
        } else {
            requestQuestionData();
        }
    }

    private void initLocalData(){
        maxGroupNo = 0;
        String username = BmobUser.getCurrentUser(User.class).getUsername();
        List<SequenceGroupL> groups = DataSupport.where("username = ?", username).find(SequenceGroupL.class);
        if(groups.size() == 1){
            currentUserSG = groups.get(0);
            groupNo = currentUserSG.getGroupNo();
        } else {
            currentUserSG = new SequenceGroupL();
            currentUserSG.setUsername(username);
            currentUserSG.setGroupNo(0);
            currentUserSG.save();
        }
    }

    private void initEmptyView(){
        emptyView = (QMUIEmptyView) findViewById(R.id.emptyView);
        emptyView.show(true);
    }

    private void initTopBar(){
        topBar = (QMUITopBar) findViewById(R.id.question_topbar);
        if(currentTrainMode == TRAIN_MODE_RANDOM){
            topBar.setTitle(getTopBarTitle());
        } else{
            topBar.setTitle(getTopBarTitle());
            topBar.setSubTitle(" ");
        }
        topBar.addLeftBackImageButton().setOnClickListener(this);
        if(currentTrainMode == TRAIN_MODE_SIMULATEEXAM){
            topBar.addRightTextButton("提交", R.drawable.submit_exam).setOnClickListener(this);
        }
    }

    private String getTopBarTitle(){
        String title;
        switch (currentTrainMode){
            default:
            case TRAIN_MODE_SEQUENCE:
                title = "顺序学习";
                break;
            case TRAIN_MODE_UNIT:
                title = "单元训练";
                break;
            case TRAIN_MODE_RANDOM:
                title = "随机练习";
                break;
            case TRAIN_MODE_SIMULATEEXAM:
                title = "模拟考试";
                break;
        }
        return title;
    }

    private void initBottomBar(){
        RelativeLayout bottomBar = (RelativeLayout) findViewById(R.id.question_bottombar);
        if(currentTrainMode == TRAIN_MODE_SIMULATEEXAM){
            bottomBar.setVisibility(View.GONE);
        } else {
            btnPrev = (Button) findViewById(R.id.btn_prev);
            btnAnalyze = (Button) findViewById(R.id.btn_analyze);
            btnNext = (Button) findViewById(R.id.btn_next);
            btnPrev.setOnClickListener(this);
            btnAnalyze.setOnClickListener(this);
            btnNext.setOnClickListener(this);
            if(currentTrainMode == TRAIN_MODE_UNIT){
                btnPrev.setText("上一单元");
                btnNext.setText("下一单元");
            } else if(currentTrainMode == TRAIN_MODE_RANDOM){
                //btnPrev.setVisibility(View.GONE);
                btnPrev.setText("禁用");
                btnNext.setText("下一组");
            }
        }
    }

    private void initRecyclerView(){
        recyclerView = (RecyclerView) findViewById(R.id.question_recyclerview);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(QuestionActivity.this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);
    }

    private void resetAdapter(){
        QuestionRvAdapter adapter = new QuestionRvAdapter(this, questionList);
        recyclerView.setAdapter(adapter);
    }

    private void requestQuestionData(){
        emptyView.show(true);
        switch (currentTrainMode){
            case TRAIN_MODE_SEQUENCE:
                topBar.setSubTitle("(第" + (groupNo + 1) + "组/共" + (maxGroupNo + 1) +"组)");
                UtilDatabase.findQuestionSequence(this, DEFAULT_GROUP_NUM * groupNo, DEFAULT_GROUP_NUM);
                break;
            case TRAIN_MODE_UNIT:
                topBar.setSubTitle(getUnitNameById(currentUnitId));
                UtilDatabase.findQuestionByUnitId(this, currentUnitId);
                break;
            case TRAIN_MODE_RANDOM:
                int[] questions = questionRows();
                UtilDatabase.findQuestionByRandom(this, questions, questionNum);
                break;
            case TRAIN_MODE_SIMULATEEXAM:
                topBar.setTitle(getExamNameById(currentExamId));
                UtilDatabase.findQuestionById(this, getquestionId(currentExamId));
                break;
        }
    }

    private String getUnitNameById(int unitId){
        String unitName = "未知单元";
        for (Unit unit : UtilDatabase.unitList){
            if(unit.getId().equals(unitId)){
                unitName = unit.getName();
            }
        }
        return unitName;
    }

    private String getExamNameById(int examId){
        String examName = "未知试卷";
        for (Examination examination : UtilDatabase.examList){
            if(examination.getId().equals(examId)){
                examName = examination.getName();
            }
        }
        return examName;
    }

    private String[] getquestionId(int examId){
        String id = "";
        for (Examination examination : UtilDatabase.examList){
            if(examination.getId().equals(examId)){
                id = examination.getQuestionList();
            }
        }
        String[] questionId = id.split(";");
        return questionId;

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.qmui_topbar_item_left_back:
                finish();
                break;
            case R.id.btn_prev:
                btnPrevClickAction();
                break;
            case R.id.btn_analyze:
                btnAnalyzeClickAction(v);
                break;
            case R.id.btn_next:
                btnNextClickAction();
                break;
            case R.drawable.submit_exam:
                btnSubmitClickAction();
                break;
            default:
                break;
        }
    }

    private void showLoadingTip(String tip){
        tipDialog = new QMUITipDialog.Builder(QuestionActivity.this)
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                .setTipWord(tip)
                .create(false);
        tipDialog.show();
    }

    private void showTip(int type, String tipWord, long duration){
        QMUITipDialog.Builder tipBuilder = new QMUITipDialog.Builder(QuestionActivity.this);
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

    private void showEmptyViewTip(){
        if(!UtilNetwork.isNetworkAvailable()){
            emptyView.show("网络异常", "请确认网络畅通后重试");
        } else {
            emptyView.show("题目好像飞走了", "休息一下吧~");
        }
        if(currentTrainMode == TRAIN_MODE_SEQUENCE || currentTrainMode == TRAIN_MODE_UNIT || currentTrainMode == TRAIN_MODE_RANDOM){
            btnPrev.setClickable(false);
            btnAnalyze.setClickable(false);
            btnNext.setClickable(false);
        }
    }

    private void btnPrevClickAction(){
        if(!isQuestionLoaded){
            showTip(TIP_TYPE_INFO, "加载中，请稍候", DEFAULT_TIP_DURATION);
            return;
        }
        if(currentTrainMode == TRAIN_MODE_SEQUENCE){
            if(groupNo == 0){
                showTip(TIP_TYPE_INFO, "没有上一组了", DEFAULT_TIP_DURATION);
                return;
            } else {
                emptyView.show(true);
                groupNo--;
                currentUserSG.setGroupNo(groupNo);
                currentUserSG.save();
                requestQuestionData();
            }
        }
        if(currentTrainMode == TRAIN_MODE_UNIT){
            if(currentUnitId == 1){
                showTip(TIP_TYPE_INFO, "没有上一单元了", DEFAULT_TIP_DURATION);
                return;
            } else {
                emptyView.show(true);
                currentUnitId--;
                requestQuestionData();
            }
        }
        if(currentTrainMode == TRAIN_MODE_RANDOM){
            showTip(TIP_TYPE_INFO, "该按钮已禁用", DEFAULT_TIP_DURATION);
            return;
        }
    }

    private void btnAnalyzeClickAction(View v){
        if(!isQuestionLoaded){
            showTip(TIP_TYPE_INFO, "加载中，请稍候", DEFAULT_TIP_DURATION);
            return;
        }
        int positon = layoutManager.findFirstVisibleItemPosition();
        setPopupText(questionList.get(positon).getAnalysis());
        mNormalPopup.setAnimStyle(QMUIPopup.ANIM_GROW_FROM_CENTER);
        mNormalPopup.setPreferredDirection(QMUIPopup.DIRECTION_TOP);
        mNormalPopup.show(v);
    }

    private void btnNextClickAction(){
        if(!isQuestionLoaded){
            showTip(TIP_TYPE_INFO, "加载中，请稍候", DEFAULT_TIP_DURATION);
            return;
        }
        if(currentTrainMode == TRAIN_MODE_SEQUENCE){
            if(groupNo == maxGroupNo){
                showTip(TIP_TYPE_INFO, "没有下一组了", DEFAULT_TIP_DURATION);
                return;
            } else {
                emptyView.show(true);
                groupNo++;
                currentUserSG.setGroupNo(groupNo);
                currentUserSG.save();
                requestQuestionData();
            }
        }
        if(currentTrainMode == TRAIN_MODE_UNIT){
            if(currentUnitId == UtilDatabase.unitList.size()){
                showTip(TIP_TYPE_INFO, "没有下一单元了", DEFAULT_TIP_DURATION);
                return;
            } else {
                emptyView.show(true);
                currentUnitId++;
                requestQuestionData();
            }
        }
        if(currentTrainMode == TRAIN_MODE_RANDOM){
            emptyView.show(true);
            requestQuestionData();
        }
    }

    private void btnSubmitClickAction(){
        new QMUIDialog.MessageDialogBuilder(QuestionActivity.this)
                .setTitle("提示")
                .setMessage("确认提交试卷？")
                .addAction("确定", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        dialog.dismiss();
                        showLoadingTip("提交中");
                        submitExam();
                    }
                })
                .addAction("取消", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        dialog.dismiss();
                    }
                })
                .setCanceledOnTouchOutside(false)
                .create()
                .show();
    }

    private void setPopupText(String text) {
        if (mNormalPopup == null) {
            mNormalPopup = new QMUIPopup(QuestionActivity.this, QMUIPopup.DIRECTION_NONE);
        }
        mNormalPopup.setNeedCacheSize(false);
        TextView textView = new TextView(QuestionActivity.this);
        textView.setLayoutParams(mNormalPopup.generateLayoutParam(
                QMUIDisplayHelper.dp2px(QuestionActivity.this, 250),
                WRAP_CONTENT
        ));
        textView.setLineSpacing(QMUIDisplayHelper.dp2px(QuestionActivity.this, 4), 1.0f);
        int padding = QMUIDisplayHelper.dp2px(QuestionActivity.this, 20);
        textView.setPadding(padding, padding, padding, padding);
        textView.setText(text);
        textView.setTextColor(ContextCompat.getColor(QuestionActivity.this, R.color.app_color_description));
        mNormalPopup.setContentView(textView);
    }

    public void setTimer(int time){
        myTimer = new CountDownTimer(time*1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int time = (int)millisUntilFinished / 1000;
                int hour = time / 3600;
                int minute = (time % 3600) / 60;
                int second = (time % 3600) % 60;
                String h, m, s;
                h = hour >= 10 ? hour+"":"0"+hour;
                m = minute >= 10 ? minute+"":"0"+minute;
                s = second >= 10 ? second+"":"0"+second;
                topBar.setSubTitle(h + ":" + m + ":" + s);
            }

            @Override
            public void onFinish() {
                showLoadingTip("时间到，提交中");
                submitExam();
            }
        }.start();
    }

    public void submitExam(){
        List<RecordL> recordLList = new ArrayList<>();
        recordLList = DataSupport.findAll(RecordL.class);
        List<RecordL> errorList = new ArrayList<>();
        errorList = DataSupport.where("result = ?", "0").find(RecordL.class);
        tipDialog.dismiss();
        new QMUIDialog.MessageDialogBuilder(QuestionActivity.this)
                .setTitle("答题结果")
                .setMessage("本次试卷共有"+ questionList.size()+"道题目，总计答题"+recordLList.size()+"道，答错"+errorList.size()+"道。")
                .addAction("查看答题详情", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        dialog.dismiss();
                        QuestionLocalActivity.actionStart(QuestionActivity.this, QuestionLocalActivity.START_MODE_EXAMDETAIL);
                    }
                })
                .addAction("返回主界面", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        DataSupport.deleteAll(RecordL.class);
                        dialog.dismiss();
                        StudentMainActivity.actionStart(QuestionActivity.this);
                    }
                })
                .setCanceledOnTouchOutside(false)
                .create()
                .show();

    }

    public int[] setRandomData(){
        final int[] randNum = new int[questionNum];
        int[] numberBox = new int[questionNum];
        for (int i = 0; i < questionNum; i++){
            numberBox[i] = i;     //先把N个数放入容器A中
        }
        int end = questionNum - 1;
        for (int j = 0; j < questionNum; j++){
            int n = new Random().nextInt(end + 1);  //取随机数
            randNum[j] = numberBox[n];            //把随机数放入容器B
            numberBox[n] = numberBox[end];          //把容器A中最后一个数覆盖所取的随机数
            end--;                                    //缩小随机数所取范围
        }
        return randNum;
    }

    public int[] questionRows(){
        int[] questions = new int[DEFAULT_GROUP_NUM];
        int skip = left + randomGroup * DEFAULT_GROUP_NUM;
        if(skip + DEFAULT_GROUP_NUM < questionNum){
            for(int i = 0; i < DEFAULT_GROUP_NUM; i++){
                questions[i] = randNum[skip+i];
            }
            randomGroup++;
        } else if(skip + DEFAULT_GROUP_NUM == questionNum){
            for(int i = 0; i < DEFAULT_GROUP_NUM; i++){
                questions[i] = randNum[skip+i];
            }
            randomGroup = 0;
            left = 0;
        } else{
            left = skip + DEFAULT_GROUP_NUM - questionNum;
            randomGroup = 0;
            for(int i = 0; i < questionNum - skip; i++){
                questions[i] = randNum[skip+i];
            }
            randNum = setRandomData();
            for(int i = 0; i < left; i++){
                questions[questionNum - skip + i] = randNum[i];
            }
        }
        return questions;
    }

    public boolean isNetworkAvailable(){
        if(UtilNetwork.isNetworkAvailable()){
            return true;
        } else {
            showTip(TIP_TYPE_FAIL, "请连接网络后答题", DEFAULT_TIP_DURATION);
            return false;
        }
    }

    @Override
    public void onBackPressed() {
        if(currentTrainMode == TRAIN_MODE_SIMULATEEXAM){
            DataSupport.deleteAll(RecordL.class);
        }
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(myTimer != null){
            myTimer.cancel();
        }
    }
}
