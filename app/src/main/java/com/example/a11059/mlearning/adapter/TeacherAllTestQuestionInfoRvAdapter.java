package com.example.a11059.mlearning.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.a11059.mlearning.R;
import com.example.a11059.mlearning.activity.TeacherAllTestQuestionInfoActivity;
import com.example.a11059.mlearning.entity.Question;
import com.qmuiteam.qmui.span.QMUIAlignMiddleImageSpan;
import com.qmuiteam.qmui.util.QMUIDrawableHelper;

import java.util.List;

public class TeacherAllTestQuestionInfoRvAdapter extends RecyclerView.Adapter<TeacherAllTestQuestionInfoRvAdapter.ViewHolder>{

    private static final int OPTION_A = 1;

    private static final int OPTION_B = 2;

    private static final int OPTION_C = 3;

    private static final int OPTION_D = 4;

    private static final int OPTION_RIGHT = 5;

    private Context mContext;

    private TeacherAllTestQuestionInfoActivity parentActivity;

    private List<Question> mQuestionList;


    public TeacherAllTestQuestionInfoRvAdapter(TeacherAllTestQuestionInfoActivity activity, List<Question> questionUnitList){
        parentActivity = activity;
        mQuestionList = questionUnitList;
    }
    //自定义一个viewHolder 方便绑定数据和扩展
    static class ViewHolder extends RecyclerView.ViewHolder{

        TextView questionContent;
        TextView optionA;
        TextView optionB;
        TextView optionC;
        TextView optionD;
        TextView questionAnalysisContent;

        public ViewHolder(View view){
            super(view);
            questionContent = (TextView) view.findViewById(R.id.question_content);
            optionA = (TextView) view.findViewById(R.id.option_a);
            optionB = (TextView) view.findViewById(R.id.option_b);
            optionC = (TextView) view.findViewById(R.id.option_c);
            optionD = (TextView) view.findViewById(R.id.option_d);
            questionAnalysisContent = (TextView) view.findViewById(R.id.question_analysis_content);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        /**
         * 负责绑定每个子项的布局
         */
        if(mContext == null){
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.question_all_recycleview, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final TeacherAllTestQuestionInfoRvAdapter.ViewHolder holder, int position) {
        /**
         * 负责将每个子项holder绑定数据
         */
        final int lPosition = holder.getLayoutPosition(); //position位置不一定准确，layoutpotion为实际位置
        int questionNo = lPosition + 1;
        final Question question = mQuestionList.get(lPosition);
        String questionContent = questionNo + "." + question.getQuestion();
        String questionAnalysisContent = "解析：" + question.getAnalysis();
        holder.questionContent.setText(questionContent);
        holder.optionA.setText(getOptionIconString(question, OPTION_A));
        holder.optionB.setText(getOptionIconString(question, OPTION_B));
        holder.optionC.setText(getOptionIconString(question, OPTION_C));
        holder.optionD.setText(getOptionIconString(question, OPTION_D));
        holder.questionAnalysisContent.setText(getAnalysisContentIconString(questionAnalysisContent));


    }
    @Override
    public int getItemCount() {
        return mQuestionList == null ? 0 : mQuestionList.size();
    }

    private SpannableString getOptionIconString(Question question, int option){
        final float spanWidthCharacterCount = 1.5f;
        String optionString;
        Drawable optionIcon;
        switch (option){
            default:
            case OPTION_A:
                optionString = question.getA();
                if (optionString.equals(question.getAnswer())){
                    optionIcon = QMUIDrawableHelper.getVectorDrawable(mContext, R.drawable.ic_options_right);
                }else {
                    optionIcon = QMUIDrawableHelper.getVectorDrawable(mContext, R.drawable.ic_options_a);
                }
                break;
            case OPTION_B:
                optionString = question.getB();
                if (optionString.equals(question.getAnswer())){
                    optionIcon = QMUIDrawableHelper.getVectorDrawable(mContext, R.drawable.ic_options_right);
                }else {
                    optionIcon = QMUIDrawableHelper.getVectorDrawable(mContext, R.drawable.ic_options_b);
                }
                break;
            case OPTION_C:
                optionString = question.getC();
                if (optionString.equals(question.getAnswer())){
                    optionIcon = QMUIDrawableHelper.getVectorDrawable(mContext, R.drawable.ic_options_right);
                }else {
                    optionIcon = QMUIDrawableHelper.getVectorDrawable(mContext, R.drawable.ic_options_c);
                }
                break;
            case OPTION_D:
                optionString = question.getD();
                if (optionString.equals(question.getAnswer())){
                    optionIcon = QMUIDrawableHelper.getVectorDrawable(mContext, R.drawable.ic_options_right);
                }else {
                    optionIcon = QMUIDrawableHelper.getVectorDrawable(mContext, R.drawable.ic_options_d);
                }
                break;
        }
        SpannableString spannable = new SpannableString("[icon]" + optionString);
        if(optionIcon != null){
            optionIcon.setBounds(0, 0, optionIcon.getIntrinsicWidth(), optionIcon.getIntrinsicHeight());
        }
        ImageSpan alignMiddleImageSpan = new QMUIAlignMiddleImageSpan(optionIcon, QMUIAlignMiddleImageSpan.ALIGN_MIDDLE, spanWidthCharacterCount);
        spannable.setSpan(alignMiddleImageSpan, 0, "[icon]".length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        return spannable;
    }

    private SpannableString getAnalysisContentIconString(String sContent){
        /***
         * 通过SpannableString在字符串中插入图片
         * 解析
         */
        final float spanWidthCharacterCount = 1.5f;
        Drawable icon = QMUIDrawableHelper.getVectorDrawable(mContext, R.drawable.ic_bottombar_explain);
        StringBuilder stringBuilder = new StringBuilder(sContent);
        SpannableString spannable = new SpannableString("[icon]" + stringBuilder);
        if(icon != null){
            icon.setBounds(0, 0, icon.getIntrinsicWidth(), icon.getIntrinsicHeight());
        }

        ImageSpan alignMiddleImageSpan_mobile = new QMUIAlignMiddleImageSpan(icon, QMUIAlignMiddleImageSpan.ALIGN_MIDDLE, spanWidthCharacterCount);;
        spannable.setSpan(alignMiddleImageSpan_mobile, 0, "[icon]".length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        return spannable;
    }
}

