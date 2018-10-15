package com.example.a11059.mlearning.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Message;
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
import com.example.a11059.mlearning.entity.CollectionL;
import com.example.a11059.mlearning.entity.HistoryL;
import com.example.a11059.mlearning.entity.User;
import com.qmuiteam.qmui.span.QMUIAlignMiddleImageSpan;
import com.qmuiteam.qmui.util.QMUIDrawableHelper;

import org.litepal.crud.DataSupport;

import java.util.List;

import cn.bmob.v3.BmobUser;

/**
 * Created by 11059 on 2018/7/24.
 */

public class CollectionRvAdapter extends RecyclerView.Adapter<CollectionRvAdapter.ViewHolder>{

    private static final int OPTION_A = 1;

    private static final int OPTION_B = 2;

    private static final int OPTION_C = 3;

    private static final int OPTION_D = 4;

    private static final int OPTION_RIGHT = 5;

    private static final int OPTION_WRONG = 6;

    private QuestionLocalActivity parentActivity;

    private Context mContext;

    private User currentUser;

    private List<CollectionL> mCollectionList;

    private SparseBooleanArray mSelectStates = new SparseBooleanArray();

    public CollectionRvAdapter(List<CollectionL> collectionList, QuestionLocalActivity activity){
        parentActivity = activity;
        mCollectionList = collectionList;
        currentUser = BmobUser.getCurrentUser(User.class);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(mContext == null){
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.question_local_rv, parent, false);
        return new CollectionRvAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final int lPosition = holder.getLayoutPosition();
        int questionNo = lPosition + 1;
        final CollectionL question = mCollectionList.get(lPosition);
        String content = questionNo + "." + question.getQuestion();
        holder.questionContent.setText(content);
        holder.optionA.setText(getOptionIconString(question, OPTION_A));
        holder.optionB.setText(getOptionIconString(question, OPTION_B));
        holder.optionC.setText(getOptionIconString(question, OPTION_C));
        holder.optionD.setText(getOptionIconString(question, OPTION_D));
        String username = currentUser.getUsername();
        final String questionId = question.getQuestionId();
        HistoryL history = findHistoryById(username, questionId);
        if(!mSelectStates.get(lPosition, false)){ //如果没有点击过
            holder.optionA.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mSelectStates.put(lPosition, true);
                    String option = question.getA();
                    checkOption(option, holder, true);
                    saveHistory(questionId, option);
                }
            });
            holder.optionB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mSelectStates.put(lPosition, true);
                    String option = question.getB();
                    checkOption(option, holder, true);
                    saveHistory(questionId, option);
                }
            });
            holder.optionC.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mSelectStates.put(lPosition, true);
                    String option = question.getC();
                    checkOption(option, holder, true);
                    saveHistory(questionId, option);
                }
            });
            holder.optionD.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mSelectStates.put(lPosition, true);
                    String option = question.getD();
                    checkOption(option, holder, true);
                    saveHistory(questionId, option);
                }
            });
        } else { //点击过
            assert history != null;
            checkOption(history.getOption(), holder, false);
        }

        holder.delButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delQuestionFromCollection(lPosition);
            }
        });
    }

    private void checkOption(String option, ViewHolder holder, boolean needSaveError){
        setHolderClickableFalse(holder); //禁用点击事件
        int position = holder.getAdapterPosition();
        CollectionL question = mCollectionList.get(position);
        char answer, rightAnswer;
        if(option.equals(question.getA())){
            answer = 'A';
        } else if(option.equals(question.getB())){
            answer = 'B';
        } else if(option.equals(question.getC())){
            answer = 'C';
        } else {
            answer = 'D';
        }
        if(question.getAnswer().equals(question.getA())){
            rightAnswer = 'A';
        } else if(question.getAnswer().equals(question.getB())){
            rightAnswer = 'B';
        } else if(question.getAnswer().equals(question.getC())){
            rightAnswer = 'C';
        } else {
            rightAnswer = 'D';
        }
        if(option.equals(question.getAnswer())){ //做对了
            //刷新显示状态
            switch (answer){
                case 'A':
                    holder.optionA.setText(getCheckOptionIconString(OPTION_RIGHT, question.getA()));
                    break;
                case 'B':
                    holder.optionB.setText(getCheckOptionIconString(OPTION_RIGHT, question.getB()));
                    break;
                case 'C':
                    holder.optionC.setText(getCheckOptionIconString(OPTION_RIGHT, question.getC()));
                    break;
                case 'D':
                    holder.optionD.setText(getCheckOptionIconString(OPTION_RIGHT, question.getD()));
                    break;
                default:
                    break;
            }
        } else {//没做对
            //刷新显示状态
            switch (answer){
                case 'A':
                    holder.optionA.setText(getCheckOptionIconString(OPTION_WRONG, question.getA()));
                    break;
                case 'B':
                    holder.optionB.setText(getCheckOptionIconString(OPTION_WRONG, question.getB()));
                    break;
                case 'C':
                    holder.optionC.setText(getCheckOptionIconString(OPTION_WRONG, question.getC()));
                    break;
                case 'D':
                    holder.optionD.setText(getCheckOptionIconString(OPTION_WRONG, question.getD()));
                    break;
                default:
                    break;
            }

            switch (rightAnswer){
                case 'A':
                    holder.optionA.setText(getCheckOptionIconString(OPTION_RIGHT, question.getA()));
                    break;
                case 'B':
                    holder.optionB.setText(getCheckOptionIconString(OPTION_RIGHT, question.getB()));
                    break;
                case 'C':
                    holder.optionC.setText(getCheckOptionIconString(OPTION_RIGHT, question.getC()));
                    break;
                case 'D':
                    holder.optionD.setText(getCheckOptionIconString(OPTION_RIGHT, question.getD()));
                    break;
                default:
                    break;
            }

        }
    }

    private void saveHistory(String questionId, String option){
        String username = currentUser.getUsername();
        HistoryL history = findHistoryById(username, questionId);
        if(history == null){
            history = new HistoryL();
            history.setUsername(username);
            history.setQuestonId(questionId);
            history.setOption(option);
            history.save();
        } else {
            //已经存在这条数据，刷新记录
            history.delete();
            history = new HistoryL();
            history.setUsername(username);
            history.setQuestonId(questionId);
            history.setOption(option);
            history.save();
        }
    }

    private HistoryL findHistoryById(String username, String questionId){
        List<HistoryL> historys = DataSupport
                .where("username = ? and questionid = ?", username,  questionId)
                .find(HistoryL.class);
        if(historys == null){
            return null;
        } else {
            if(historys.size() == 0){
                return null;
            } else {
                return historys.get(0);
            }
        }
    }

    private void delQuestionFromCollection(int pos){
        CollectionL error = mCollectionList.get(pos);
        error.delete();
        mCollectionList.remove(pos);
        notifyItemRemoved(pos);
        notifyItemRangeChanged(pos, mCollectionList.size() - pos);
        if(mCollectionList.size() == 0){
            Message message = new Message();
            message.what = QuestionLocalActivity.MSG_LIST_EMPTY;
            parentActivity.handler.sendMessage(message);
        }
    }

    private void setHolderClickableFalse(ViewHolder holder){
        holder.optionA.setClickable(false);
        holder.optionB.setClickable(false);
        holder.optionC.setClickable(false);
        holder.optionD.setClickable(false);
    }

    private SpannableString getOptionIconString(CollectionL question, int option){
        final float spanWidthCharacterCount = 1.5f;
        String optionString;
        Drawable optionIcon;
        switch (option){
            default:
            case OPTION_A:
                optionString = question.getA();
                optionIcon = QMUIDrawableHelper.getVectorDrawable(mContext, R.drawable.ic_options_a);
                break;
            case OPTION_B:
                optionString = question.getB();
                optionIcon = QMUIDrawableHelper.getVectorDrawable(mContext, R.drawable.ic_options_b);
                break;
            case OPTION_C:
                optionString = question.getC();
                optionIcon = QMUIDrawableHelper.getVectorDrawable(mContext, R.drawable.ic_options_c);
                break;
            case OPTION_D:
                optionString = question.getD();
                optionIcon = QMUIDrawableHelper.getVectorDrawable(mContext, R.drawable.ic_options_d);
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

    private SpannableString getCheckOptionIconString(int type, String content){
        final float spanWidthCharacterCount = 1.5f;
        Drawable optionIcon;
        if(type == OPTION_RIGHT){
            optionIcon = QMUIDrawableHelper.getVectorDrawable(mContext, R.drawable.ic_options_right);
        } else {
            optionIcon = QMUIDrawableHelper.getVectorDrawable(mContext, R.drawable.ic_options_wrong);
        }
        SpannableString spannable = new SpannableString("[icon]" + content);
        if(optionIcon != null){
            optionIcon.setBounds(0, 0, optionIcon.getIntrinsicWidth(), optionIcon.getIntrinsicHeight());
        }
        ImageSpan alignMiddleImageSpan = new QMUIAlignMiddleImageSpan(optionIcon, QMUIAlignMiddleImageSpan.ALIGN_MIDDLE, spanWidthCharacterCount);
        spannable.setSpan(alignMiddleImageSpan, 0, "[icon]".length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        return spannable;
    }

    @Override
    public int getItemCount() {
        return mCollectionList == null ? 0 : mCollectionList.size();
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

}
