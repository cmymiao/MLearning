package com.example.a11059.mlearning.adapter;

import android.content.Context;
import android.graphics.Color;
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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.a11059.mlearning.R;
import com.example.a11059.mlearning.activity.QuestionActivity;
import com.example.a11059.mlearning.entity.CollectionL;
import com.example.a11059.mlearning.entity.Feedback;
import com.example.a11059.mlearning.entity.HistoryL;
import com.example.a11059.mlearning.entity.MistakeL;
import com.example.a11059.mlearning.entity.Question;
import com.example.a11059.mlearning.entity.RecordL;
import com.example.a11059.mlearning.entity.User;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.qmuiteam.qmui.span.QMUIAlignMiddleImageSpan;
import com.qmuiteam.qmui.util.QMUIDrawableHelper;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by 11059 on 2018/7/23.
 */

public class QuestionRvAdapter extends RecyclerView.Adapter<QuestionRvAdapter.ViewHolder> {

    private static final int OPTION_A = 1;

    private static final int OPTION_B = 2;

    private static final int OPTION_C = 3;

    private static final int OPTION_D = 4;

    private static final int OPTION_RIGHT = 5;

    private static final int OPTION_WRONG = 6;

    private User currentUser;

    private Context mContext;

    private QuestionActivity parentActivity;

    private List<Question> mQuestionList;

    private SparseBooleanArray mSelectStates = new SparseBooleanArray();

    private int clickChoice = 0;

    public QuestionRvAdapter(QuestionActivity activity, List<Question> questionList){
        parentActivity = activity;
        mQuestionList = questionList;
        currentUser = BmobUser.getCurrentUser(User.class);
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        LikeButton likeButton;
        TextView questionContent;
        TextView optionA;
        TextView optionB;
        TextView optionC;
        TextView optionD;

        public ViewHolder(View view){
            super(view);
            likeButton = (LikeButton) view.findViewById(R.id.like_button);
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
        View view = LayoutInflater.from(mContext).inflate(R.layout.question_recyclerview, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final QuestionRvAdapter.ViewHolder holder, int position) {
        final int lPosition = holder.getLayoutPosition(); //position位置不一定准确，layoutpotion为实际位置
        int questionNo = lPosition + 1;
        final Question question = mQuestionList.get(lPosition);
        String content = questionNo + "." + question.getQuestion();
        holder.questionContent.setText(content);
        holder.optionA.setText(getOptionIconString(question, OPTION_A));
        holder.optionB.setText(getOptionIconString(question, OPTION_B));
        holder.optionC.setText(getOptionIconString(question, OPTION_C));
        holder.optionD.setText(getOptionIconString(question, OPTION_D));

        final String questionId = question.getId()+"";
        String username = currentUser.getUsername();
        final String objectId = question.getObjectId();
        final int unitId = question.getUnitId();

        int mode = QuestionActivity.currentTrainMode;
        HistoryL history = findHistoryById(username, questionId);
        if (mode == QuestionActivity.TRAIN_MODE_SEQUENCE || mode == QuestionActivity.TRAIN_MODE_UNIT || mode == QuestionActivity.TRAIN_MODE_RANDOM){
            if(history == null){ //如果本地记录没有此记录，则绑定点击事件
                clickChoice = 1;
                holder.optionA.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!parentActivity.isNetworkAvailable()){
                            return;
                        }
                        String option = question.getA();
                        saveQuestionTotalNum(objectId);
                        saveStudentTotalNum(unitId);
                        checkOption(option, holder, true);
                        saveHistory(questionId, option);
                    }
                });
                holder.optionB.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!parentActivity.isNetworkAvailable()){
                            return;
                        }
                        String option = question.getB();
                        saveQuestionTotalNum(objectId);
                        saveStudentTotalNum(unitId);
                        checkOption(option, holder, true);
                        saveHistory(questionId, option);
                    }
                });
                holder.optionC.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!parentActivity.isNetworkAvailable()){
                            return;
                        }
                        String option = question.getC();
                        saveQuestionTotalNum(objectId);
                        saveStudentTotalNum(unitId);
                        checkOption(option, holder, true);
                        saveHistory(questionId, option);
                    }
                });
                holder.optionD.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!parentActivity.isNetworkAvailable()){
                            return;
                        }
                        String option = question.getD();
                        saveQuestionTotalNum(objectId);
                        saveStudentTotalNum(unitId);
                        checkOption(option, holder, true);
                        saveHistory(questionId, option);
                    }
                });
            } else { //已经有此记录，则恢复状态
                checkOption(history.getOption(), holder, false);
            }
        } else if(mode == QuestionActivity.TRAIN_MODE_SIMULATEEXAM){
            holder.optionA.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    saveExamRecord(question, question.getA());
                    setOptionState(holder, OPTION_A, question);
                }
            });
            holder.optionB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    saveExamRecord(question, question.getB());
                    setOptionState(holder, OPTION_B, question);
                }
            });
            holder.optionC.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    saveExamRecord(question, question.getC());
                    setOptionState(holder, OPTION_C, question);
                }
            });
            holder.optionD.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    saveExamRecord(question, question.getD());
                    setOptionState(holder, OPTION_D, question);
                }
            });
        } else {
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


        }


        //收藏与取消收藏
        holder.likeButton.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                //向收藏表里添加这道题目
                saveCollection(question);
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                //从收藏表里移除这道题目
                deleteCollection(question);
            }
        });
        //防止likebutton状态错乱，重置正确状态
        CollectionL collection = findCollectionById(username, questionId);
        if(collection != null){
            if(!holder.likeButton.isLiked()){
                holder.likeButton.setLiked(true);
            }
        } else {
            if(holder.likeButton.isLiked()){
                holder.likeButton.setLiked(false);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mQuestionList == null ? 0 : mQuestionList.size();
    }

    private void checkOption(String option, ViewHolder holder, boolean needSaveError){
        setHolderClickableFalse(holder); //禁用点击事件
        int position = holder.getAdapterPosition();
        Question question = mQuestionList.get(position);
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
            //保存记录到云数据库
            //saveFeedback(question, "true");
            //刷新显示状态
            switch (answer){
                case 'A':
                    if(clickChoice == 1){
                        saveQuestionRightNum(question.getObjectId());
                        saveStudentRightNum(question.getUnitId());
                        clickChoice = 0;
                    }else {

                    }
                    holder.optionA.setText(getCheckOptionIconString(OPTION_RIGHT, question.getA()));
                    break;
                case 'B':
                    if(clickChoice == 1){
                        saveQuestionRightNum(question.getObjectId());
                        saveStudentRightNum(question.getUnitId());
                        clickChoice = 0;
                    }else {

                    }
                    holder.optionB.setText(getCheckOptionIconString(OPTION_RIGHT, question.getB()));
                    break;
                case 'C':
                    if(clickChoice == 1){
                        saveQuestionRightNum(question.getObjectId());
                        saveStudentRightNum(question.getUnitId());
                        clickChoice = 0;
                    }else {

                    }
                    holder.optionC.setText(getCheckOptionIconString(OPTION_RIGHT, question.getC()));
                    break;
                case 'D':
                    if(clickChoice == 1){
                        saveQuestionRightNum(question.getObjectId());
                        saveStudentRightNum(question.getUnitId());
                        clickChoice = 0;
                    }else {

                    }
                    holder.optionD.setText(getCheckOptionIconString(OPTION_RIGHT, question.getD()));
                    break;
                default:
                    break;
            }
        } else {//没做对
            //保存记录到云数据库
            //saveFeedback(question, "false");
            //收录错题
            if(needSaveError){
                saveMistake(question);
            }
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

    private void saveCollection(Question question){
        String username = currentUser.getUsername();
        String questionId = question.getId() + "";
        CollectionL collection = findCollectionById(username, questionId);
        if(collection == null){
            collection = new CollectionL();
            collection.setUsername(username);
            collection.setQuestionId(questionId);
            collection.setQuestion(question.getQuestion());
            collection.setA(question.getA());
            collection.setB(question.getB());
            collection.setC(question.getC());
            collection.setD(question.getD());
            collection.setAnswer(question.getAnswer());
            collection.setAnalyze(question.getAnalysis());
            collection.save();
        } else {
            //已经收藏，不操作
        }
    }

    private void deleteCollection(Question question){
        String username = currentUser.getUsername();
        String questionId = question.getId() + "";
        CollectionL collection = findCollectionById(username, questionId);
        if(collection != null){
            collection.delete();
        } else {
            //记录不存在，不操作
        }
    }

    private CollectionL findCollectionById(String username, String questionId){
        List<CollectionL> collections = DataSupport
                .where("username = ? and questionid = ?", username,  questionId)
                .find(CollectionL.class);
        if(collections == null){
            return null;
        } else {
            if(collections.size() == 0){
                return null;
            } else {
                return collections.get(0);
            }
        }
    }

    private void saveMistake(Question question){
        String username = currentUser.getUsername();
        String questionId = question.getId() + "";
        MistakeL mistake = findMistakeById(username, questionId);
        if(mistake == null){
            mistake = new MistakeL();
            mistake.setUsername(username);
            mistake.setQuestionId(questionId);
            mistake.setQuestion(question.getQuestion());
            mistake.setA(question.getA());
            mistake.setB(question.getB());
            mistake.setC(question.getC());
            mistake.setD(question.getD());
            mistake.setAnswer(question.getAnswer());
            mistake.setAnalyze(question.getAnalysis());
            mistake.save();
        } else {
            //已经存在，不操作
        }
    }

    private MistakeL findMistakeById(String username, String questionId){
        List<MistakeL> errors = DataSupport
                .where("username = ? and questionid = ?", username,  questionId)
                .find(MistakeL.class);
        if(errors == null){
            return null;
        } else {
            if(errors.size() == 0){
                return null;
            } else {
                return errors.get(0);
            }
        }
    }

    private void setHolderClickableFalse(ViewHolder holder){
        holder.optionA.setClickable(false);
        holder.optionB.setClickable(false);
        holder.optionC.setClickable(false);
        holder.optionD.setClickable(false);
    }

    private SpannableString getOptionIconString(Question question, int option){
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

    private SpannableString getExamOptionIconString(int type, String content){
        final float spanWidthCharacterCount = 1.5f;
        Drawable optionIcon;
        if(type == OPTION_A){
            optionIcon = QMUIDrawableHelper.getVectorDrawable(mContext, R.drawable.exam_options_a);
        } else if(type == OPTION_B){
            optionIcon = QMUIDrawableHelper.getVectorDrawable(mContext, R.drawable.exam_options_b);
        } else if(type == OPTION_C){
            optionIcon = QMUIDrawableHelper.getVectorDrawable(mContext, R.drawable.exam_options_c);
        } else{
            optionIcon = QMUIDrawableHelper.getVectorDrawable(mContext, R.drawable.exam_options_d);
        }
        SpannableString spannable = new SpannableString("[icon]" + content);
        if(optionIcon != null){
            optionIcon.setBounds(0, 0, optionIcon.getIntrinsicWidth(), optionIcon.getIntrinsicHeight());
        }
        ImageSpan alignMiddleImageSpan = new QMUIAlignMiddleImageSpan(optionIcon, QMUIAlignMiddleImageSpan.ALIGN_MIDDLE, spanWidthCharacterCount);
        spannable.setSpan(alignMiddleImageSpan, 0, "[icon]".length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        return spannable;
    }

    private void saveExamRecord(Question q, String o){
        String questionId = q.getId()+"";
        RecordL recordL = findRecordById(questionId);
        if(recordL == null){
            recordL = new RecordL();
            recordL.setQuestionId(questionId);
            recordL.setQuestion(q.getQuestion());
            recordL.setA(q.getA());
            recordL.setB(q.getB());
            recordL.setC(q.getC());
            recordL.setD(q.getD());
            recordL.setOption(o);
            recordL.setAnswer(q.getAnswer());
            recordL.setAnalysis(q.getAnalysis());
            if(q.getAnswer().equals(o)){
                recordL.setResult(1);
            }else{
                recordL.setResult(0);
            }
            recordL.save();
        }else{
            recordL = new RecordL();
            recordL.setOption(o);
            if(q.getAnswer().equals(o)){
                recordL.setResult(1);
            }else{
                recordL.setResult(0);
            }
            recordL.updateAll("questionId = ?", questionId);
        }
    }

    private RecordL findRecordById(String questionId){
        List<RecordL> recordLS = DataSupport
                .where("questionid = ?", questionId)
                .find(RecordL.class);
        if(recordLS == null){
            return null;
        } else {
            if(recordLS.size() == 0){
                return null;
            } else {
                return recordLS.get(0);
            }
        }
    }

    private void setOptionState(ViewHolder holder, int type, Question question){
        switch (type){
            case OPTION_A:
                holder.optionA.setText(getExamOptionIconString(OPTION_A, question.getA()));
                holder.optionA.setTextColor(Color.parseColor("#00A8E1"));
                holder.optionB.setText(getOptionIconString(question, OPTION_B));
                holder.optionB.setTextColor(Color.parseColor("#858C96"));
                holder.optionC.setText(getOptionIconString(question, OPTION_C));
                holder.optionC.setTextColor(Color.parseColor("#858C96"));
                holder.optionD.setText(getOptionIconString(question, OPTION_D));
                holder.optionD.setTextColor(Color.parseColor("#858C96"));
                break;
            case OPTION_B:
                holder.optionB.setText(getExamOptionIconString(OPTION_B, question.getB()));
                holder.optionB.setTextColor(Color.parseColor("#00A8E1"));
                holder.optionA.setText(getOptionIconString(question, OPTION_A));
                holder.optionA.setTextColor(Color.parseColor("#858C96"));
                holder.optionC.setText(getOptionIconString(question, OPTION_C));
                holder.optionC.setTextColor(Color.parseColor("#858C96"));
                holder.optionD.setText(getOptionIconString(question, OPTION_D));
                holder.optionD.setTextColor(Color.parseColor("#858C96"));
                break;
            case OPTION_C:
                holder.optionC.setText(getExamOptionIconString(OPTION_C, question.getC()));
                holder.optionC.setTextColor(Color.parseColor("#00A8E1"));
                holder.optionA.setText(getOptionIconString(question, OPTION_A));
                holder.optionA.setTextColor(Color.parseColor("#858C96"));
                holder.optionB.setText(getOptionIconString(question, OPTION_B));
                holder.optionB.setTextColor(Color.parseColor("#858C96"));
                holder.optionD.setText(getOptionIconString(question, OPTION_D));
                holder.optionD.setTextColor(Color.parseColor("#858C96"));
                break;
            case OPTION_D:
                holder.optionD.setText(getExamOptionIconString(OPTION_D, question.getD()));
                holder.optionD.setTextColor(Color.parseColor("#00A8E1"));
                holder.optionA.setText(getOptionIconString(question, OPTION_A));
                holder.optionA.setTextColor(Color.parseColor("#858C96"));
                holder.optionB.setText(getOptionIconString(question, OPTION_B));
                holder.optionB.setTextColor(Color.parseColor("#858C96"));
                holder.optionC.setText(getOptionIconString(question, OPTION_C));
                holder.optionC.setTextColor(Color.parseColor("#858C96"));
                break;
                default:
                    break;
        }
    }

    private void saveQuestionTotalNum(String id){
        Question updateQuestion = new Question();
        updateQuestion.increment("totalNum");
        updateQuestion.update(id, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if(e == null){

                }
            }
        });
    }

    private void saveQuestionRightNum(String id){
        Question updateQuestion = new Question();
        updateQuestion.increment("rightNum");
        updateQuestion.update(id, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if(e == null){

                }
            }
        });
    }

    private void saveStudentTotalNum(int unitId){
        BmobQuery<Feedback> query1 = new BmobQuery<>();
        query1.addWhereEqualTo("username", currentUser.getUsername());
        BmobQuery<Feedback> query2 = new BmobQuery<>();
        query2.addWhereEqualTo("unitId", unitId);
        List<BmobQuery<Feedback>> andQuery = new ArrayList<>();
        andQuery.add(query1);
        andQuery.add(query2);
        BmobQuery<Feedback> query = new BmobQuery<>();
        query.and(andQuery);
        query.findObjects(new FindListener<Feedback>() {
            @Override
            public void done(List<Feedback> list, BmobException e) {
                if(e == null){
                    Feedback feedback = new Feedback();
                    feedback.increment("totalNum");
                    feedback.update(list.get(0).getObjectId(), new UpdateListener() {
                        @Override
                        public void done(BmobException e) {

                        }
                    });
                }
            }
        });
    }

    private void saveStudentRightNum(int unitId){
        BmobQuery<Feedback> query1 = new BmobQuery<>();
        query1.addWhereEqualTo("username", currentUser.getUsername());
        BmobQuery<Feedback> query2 = new BmobQuery<>();
        query2.addWhereEqualTo("unitId", unitId);
        List<BmobQuery<Feedback>> andQuery = new ArrayList<>();
        andQuery.add(query1);
        andQuery.add(query2);
        BmobQuery<Feedback> query = new BmobQuery<>();
        query.and(andQuery);
        query.findObjects(new FindListener<Feedback>() {
            @Override
            public void done(List<Feedback> list, BmobException e) {
                if(e == null){
                    Feedback feedback = new Feedback();
                    feedback.increment("rightNum");
                    feedback.update(list.get(0).getObjectId(), new UpdateListener() {
                        @Override
                        public void done(BmobException e) {

                        }
                    });
                }
            }
        });
    }

}
