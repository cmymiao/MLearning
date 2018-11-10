package com.example.a11059.mlearning.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.a11059.mlearning.R;
import com.example.a11059.mlearning.entity.Problem;
import com.example.a11059.mlearning.fragment.HomeFragment;

import java.util.List;

public class TeacherQuizRvAdapter extends RecyclerView.Adapter<TeacherQuizRvAdapter.ViewHolder>{

    private HomeFragment homeFragment;



    private Context mContext;

    private List<Problem> mProblemList;


    public TeacherQuizRvAdapter(List<Problem> problemList){
        mProblemList = problemList;

    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView quizContent;
        TextView quizReply;
        TextView createdAt;
        TextView replyTime;
        ImageButton replyButton;

        public ViewHolder(View view){
            super(view);
            quizContent = (TextView) view.findViewById(R.id.quiz_content);
            quizReply = (TextView) view.findViewById(R.id.quiz_reply);
            createdAt = (TextView) view.findViewById(R.id.create_time);
            replyTime = (TextView) view.findViewById(R.id.reply_time);
            replyButton = (ImageButton) view.findViewById(R.id.reply_submit);
        }
    }

    @NonNull
    @Override
    public TeacherQuizRvAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(mContext == null){
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.quiz_teacher_recycleview, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        final int lPosition = holder.getLayoutPosition();
        String quizContent = (lPosition + 1) + ". " + mProblemList.get(lPosition).getStudentName()
                + ":" + mProblemList.get(lPosition).getProblem();
        String replayContent = "回复：" + mProblemList.get(lPosition).getReply();
        holder.quizContent.setText(quizContent);
        holder.createdAt.setText(mProblemList.get(lPosition).getCreatedAt());
        if(mProblemList.get(lPosition).getReply() == null){
            holder.quizReply.setText("暂未回复");
            holder.replyTime.setVisibility(View.GONE);
            holder.replyButton.setVisibility(View.VISIBLE);
        }else {
            holder.replyTime.setVisibility(View.VISIBLE);
            holder.quizReply.setText(replayContent);
            holder.replyTime.setText(mProblemList.get(lPosition).getUpdatedAt());
            holder.replyButton.setVisibility(View.GONE);
        }
        holder.replyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(OnReplyQuizClickListener != null){
                    OnReplyQuizClickListener.onClick(position-1);
                }
            }
        });
    }
    public interface OnReplyQuizClickListener {
        void onClick(int position);
    }
    private OnReplyQuizClickListener OnReplyQuizClickListener;

    public void setOnItemClickListener(TeacherQuizRvAdapter.OnReplyQuizClickListener OnReplyQuizClickListener){
        this.OnReplyQuizClickListener = OnReplyQuizClickListener;
    }
    @Override
    public int getItemCount() {
        return mProblemList == null ? 0 : mProblemList.size();
    }
}
