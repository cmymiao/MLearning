package com.example.a11059.mlearning.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.View;
import android.widget.TextView;

import com.example.a11059.mlearning.R;
import com.example.a11059.mlearning.adapter.StatisticQuestionRateRvAdapter;
import com.example.a11059.mlearning.adapter.StatisticStudentNumRvAdapter;
import com.example.a11059.mlearning.adapter.StatisticStudentRateRvAdapter;
import com.example.a11059.mlearning.entity.Statistic;
import com.example.a11059.mlearning.utils.UtilDatabase;
import com.example.a11059.mlearning.utils.UtilNetwork;
import com.qmuiteam.qmui.span.QMUIAlignMiddleImageSpan;
import com.qmuiteam.qmui.util.QMUIDrawableHelper;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.qmuiteam.qmui.widget.QMUIEmptyView;
import com.qmuiteam.qmui.widget.QMUITopBar;
import com.qmuiteam.qmui.widget.dialog.QMUIBottomSheet;

import java.lang.ref.WeakReference;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class StatisticActivity extends AppCompatActivity {

    public static final int STATIS_FOR_QUESTION = 1;

    public static final int STATIS_FOR_STUDENT = 2;

    public static final int STATIS_FOR_STUNUM = 3;//student not fount

    private static int currentStatisType;

    private static String currentClassId = "";

    private static int currentCourseid = 0;

    private static String currentCourseName = "";

    private static int currentUnitId = 0;

    private static String currentCTitle = "";

    private Map<Integer, String> indexTitleMap = new HashMap<Integer, String>(){
        {
            put(STATIS_FOR_QUESTION, "章节题目错误率");
            put(STATIS_FOR_STUDENT, "学生答题错误率");
            put(STATIS_FOR_STUNUM, "学生答题数统计");
        }
    };

    private QMUIEmptyView emptyView;

    private RecyclerView statisRv;

    private QMUIBottomSheet qStatisDetailBs;

    public MyHandler handler = new MyHandler(this);

    public static class MyHandler extends Handler {

        private WeakReference<StatisticActivity> mActivity;

        public MyHandler(StatisticActivity activity){
            mActivity = new WeakReference<StatisticActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            StatisticActivity activity = mActivity.get();
            switch (msg.what){
                case UtilDatabase.STATISTIC_QUESTION:
                    if(UtilDatabase.statisticList.size() == 0){
                        activity.showEmptyViewTip(false);
                    }else {
                        activity.emptyView.hide();
                        activity.setQStatisRvAdapter();
                    }
                    break;
                case UtilDatabase.ERROR:
                    activity.showEmptyViewTip(true);
                    break;
                case UtilDatabase.STATISTIC_STUDENT:
                    if(UtilDatabase.statisticList.size() == 0){
                        activity.showEmptyViewTip(false);
                    }else {
                        activity.emptyView.hide();
                        activity.setSStatisRvAdapter();
                    }
                    break;
                case UtilDatabase.STATISTIC_STUDENT_NUM:
                    if(UtilDatabase.statisticList.size() == 0){
                        activity.showEmptyViewTip(false);
                    }else {
                        activity.emptyView.hide();
                        activity.setNFSStatisRvAdapter();
                    }
                    break;
            }
        }
    }

    public static void actionStart(Context context, int statisType, String classid, int cid, String courseName, int uid, String cTitle){
        Intent intent = new Intent(context, StatisticActivity.class);
        currentStatisType = statisType;
        currentClassId = classid;
        currentCourseid = cid;
        currentCourseName = courseName;
        currentUnitId = uid;
        currentCTitle = cTitle;
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistic);
        QMUIStatusBarHelper.translucent(this);
        initEmptyView();
        initTopBar();
        initStatisRv();
        if(currentStatisType == STATIS_FOR_QUESTION){ //统计信息是章节题目时需要初始化底部弹出view
            initQStatisBottomSheet();
        }
    }

    private void initEmptyView(){
        emptyView = (QMUIEmptyView) findViewById(R.id.emptyView);
        emptyView.show(true);
    }

    private void initTopBar(){
        QMUITopBar topBar = (QMUITopBar) findViewById(R.id.activity_statis_topbar);
        if(currentClassId.equals("")){
            topBar.setTitle(indexTitleMap.get(currentStatisType));
        }else {
            topBar.setTitle(currentClassId + "班" + indexTitleMap.get(currentStatisType));
        }
        if(currentStatisType == STATIS_FOR_QUESTION || currentStatisType == STATIS_FOR_STUDENT){
            String subTitle =currentCourseName + " 第" + currentUnitId + "单元";
            topBar.setSubTitle(subTitle);
        }
        topBar.addLeftBackImageButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initStatisRv(){
        statisRv = (RecyclerView) findViewById(R.id.activity_statis_recyclerview);
        statisRv.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        statisRv.setLayoutManager(layoutManager);
        if(currentStatisType == STATIS_FOR_QUESTION){
            UtilDatabase.statisticQuestionRate(this, currentCourseid, currentUnitId);
        } else if(currentStatisType == STATIS_FOR_STUDENT){
            UtilDatabase.statisticStudentRate(this, currentClassId, currentCourseid, currentUnitId);
        } else {
            UtilDatabase.statisticStudentNum(this, currentClassId, currentCourseid, currentUnitId);
        }
    }

    private void initQStatisBottomSheet(){
        qStatisDetailBs = new QMUIBottomSheet(this);
        qStatisDetailBs.setContentView(R.layout.bs_question_detail);
    }

    private void setQStatisRvAdapter(){
        StatisticQuestionRateRvAdapter adapter = new StatisticQuestionRateRvAdapter(UtilDatabase.statisticList);
        adapter.setOnItemClickListener(new StatisticQuestionRateRvAdapter.OnShowQContentClickListener() {
            @Override
            public void onClick(int position) {
                Statistic sta = UtilDatabase.statisticList.get(position);
                setqStatisDetailBsContent(sta);
                qStatisDetailBs.show();
            }
        });
        statisRv.setAdapter(adapter);
    }

    private void setSStatisRvAdapter(){
        StatisticStudentRateRvAdapter adapter = new StatisticStudentRateRvAdapter(UtilDatabase.statisticList);
        statisRv.setAdapter(adapter);
    }

    private void setNFSStatisRvAdapter(){
        StatisticStudentNumRvAdapter adapter = new StatisticStudentNumRvAdapter(UtilDatabase.statisticList);
        statisRv.setAdapter(adapter);
    }

    private void setqStatisDetailBsContent(Statistic statistic){
        View view = qStatisDetailBs.getContentView();
        TextView qId = (TextView) view.findViewById(R.id.question_id);
        TextView qTNum = (TextView) view.findViewById(R.id.question_t_num);
        TextView qERate = (TextView) view.findViewById(R.id.question_e_rate);
        TextView qContent = (TextView) view.findViewById(R.id.question_content);

        String qIdS = statistic.getQuestionId() + "";
        switch (qIdS.length()){
            case 1:
                qIdS = "000" + qIdS;
                break;
            case 2:
                qIdS = "00" + qIdS;
                break;
            case 3:
                qIdS = "0" + qIdS;
                break;
            case 4:
            default:
                break;
        }
        qIdS = "编号：" + qIdS;
        qId.setText(qIdS);

        String qTNumS = "答题人数：" +  statistic.getTotalNum();
        qTNum.setText(qTNumS);

        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        String qERateS = "正确率：" +  decimalFormat.format(statistic.getAccuracy()) + "%";
        qERate.setText(qERateS);

        String qContentS = "题目内容：" + statistic.getQuestion();
        qContent.setText(getQContentIconString(qContentS));
    }

    private SpannableString getQContentIconString(String qContent){
        final float spanWidthCharacterCount = 1.5f;
        Drawable icon = QMUIDrawableHelper.getVectorDrawable(StatisticActivity.this, R.drawable.ic_bs_q_content);
        SpannableString spannable = new SpannableString("[icon]" + qContent);
        if(icon != null){
            icon.setBounds(0, 0, icon.getIntrinsicWidth(), icon.getIntrinsicHeight());
        }
        ImageSpan alignMiddleImageSpan = new QMUIAlignMiddleImageSpan(icon, QMUIAlignMiddleImageSpan.ALIGN_MIDDLE, spanWidthCharacterCount);
        spannable.setSpan(alignMiddleImageSpan, 0, "[icon]".length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        return spannable;
    }

    private void showEmptyViewTip(boolean isBadRequest){
        if(!UtilNetwork.isNetworkAvailable()){
            emptyView.show("网络异常", "请确认网络畅通后重试");
        } else if(isBadRequest){
            emptyView.show("加载失败", "请退出重试~");
        } else {
            emptyView.show("暂无统计数据", "");
        }
    }

}
