package com.example.a11059.mlearning.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.a11059.mlearning.R;
import com.example.a11059.mlearning.activity.QuestionLocalActivity;
import com.example.a11059.mlearning.entity.RecordL;
import com.example.a11059.mlearning.entity.User;
import com.qmuiteam.qmui.span.QMUIAlignMiddleImageSpan;
import com.qmuiteam.qmui.util.QMUIDrawableHelper;

import java.util.List;

/**
 * Created by 11059 on 2018/7/30.
 */

public class ExamDetailRvAdapter extends RecyclerView.Adapter<ExamDetailRvAdapter.ViewHolder> {

    private static final int OPTION_A = 1;

    private static final int OPTION_B = 2;

    private static final int OPTION_C = 3;

    private static final int OPTION_D = 4;

    private static final int OPTION_RIGHT = 5;

    private static final int OPTION_WRONG = 6;

    private QuestionLocalActivity parentActivity;

    private Context mContext;

    private List<RecordL> mRecordList;

    private SparseBooleanArray mSelectStates = new SparseBooleanArray();

    public ExamDetailRvAdapter(List<RecordL> collectionList, QuestionLocalActivity activity){
        parentActivity = activity;
        mRecordList = collectionList;
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        ImageButton delButton;
        TextView questionContent;
        TextView optionA;
        TextView optionB;
        TextView optionC;
        TextView optionD;

        public ViewHolder(View view){
            super(view);
            delButton = (ImageButton) view.findViewById(R.id.delete_button);
            questionContent = (TextView) view.findViewById(R.id.question_content);
            optionA = (TextView) view.findViewById(R.id.option_a);
            optionB = (TextView) view.findViewById(R.id.option_b);
            optionC = (TextView) view.findViewById(R.id.option_c);
            optionD = (TextView) view.findViewById(R.id.option_d);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(mContext == null){
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.question_local_rv, parent, false);
        return new ExamDetailRvAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExamDetailRvAdapter.ViewHolder holder, int position) {
        final int lPosition = holder.getLayoutPosition();
        int questionNo = lPosition + 1;
        final RecordL question = mRecordList.get(lPosition);
        String content = questionNo + "." + question.getQuestion();
        holder.questionContent.setText(content);
        holder.optionA.setText(getOptionIconString(question, OPTION_A));
        holder.optionB.setText(getOptionIconString(question, OPTION_B));
        holder.optionC.setText(getOptionIconString(question, OPTION_C));
        holder.optionD.setText(getOptionIconString(question, OPTION_D));
        holder.optionA.setClickable(false);
        holder.optionB.setClickable(false);
        holder.optionC.setClickable(false);
        holder.optionD.setClickable(false);
        holder.delButton.setVisibility(View.GONE);
    }

    private SpannableString getOptionIconString(RecordL question, int option){
        final float spanWidthCharacterCount = 1.5f;
        String optionString;
        String userOption = question.getOption();
        String answer = question.getAnswer();
        int result = question.getResult();
        Drawable optionIcon;
        switch (option){
            default:
            case OPTION_A:
                optionString = question.getA();
                if((userOption.equals(optionString) && result == 1) || (answer.equals(optionString))){
                    optionIcon = QMUIDrawableHelper.getVectorDrawable(mContext, R.drawable.ic_options_right);
                }else if(userOption.equals(optionString) && result == 0){
                    optionIcon = QMUIDrawableHelper.getVectorDrawable(mContext, R.drawable.ic_options_wrong);
                }else{
                    optionIcon = QMUIDrawableHelper.getVectorDrawable(mContext, R.drawable.ic_options_a);
                }
                break;
            case OPTION_B:
                optionString = question.getB();
                if((userOption.equals(optionString) && result == 1) || (answer.equals(optionString))){
                    optionIcon = QMUIDrawableHelper.getVectorDrawable(mContext, R.drawable.ic_options_right);
                }else if(userOption.equals(optionString) && result == 0){
                    optionIcon = QMUIDrawableHelper.getVectorDrawable(mContext, R.drawable.ic_options_wrong);
                }else{
                    optionIcon = QMUIDrawableHelper.getVectorDrawable(mContext, R.drawable.ic_options_b);
                }
                break;
            case OPTION_C:
                optionString = question.getC();
                if((userOption.equals(optionString) && result == 1) || (answer.equals(optionString))){
                    optionIcon = QMUIDrawableHelper.getVectorDrawable(mContext, R.drawable.ic_options_right);
                }else if(userOption.equals(optionString) && result == 0){
                    optionIcon = QMUIDrawableHelper.getVectorDrawable(mContext, R.drawable.ic_options_wrong);
                }else{
                    optionIcon = QMUIDrawableHelper.getVectorDrawable(mContext, R.drawable.ic_options_c);
                }
                break;
            case OPTION_D:
                optionString = question.getD();
                if((userOption.equals(optionString) && result == 1) || (answer.equals(optionString))){
                    optionIcon = QMUIDrawableHelper.getVectorDrawable(mContext, R.drawable.ic_options_right);
                }else if(userOption.equals(optionString) && result == 0){
                    optionIcon = QMUIDrawableHelper.getVectorDrawable(mContext, R.drawable.ic_options_wrong);
                }else{
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

    @Override
    public int getItemCount() {
        return mRecordList == null ? 0 : mRecordList.size();
    }
}

