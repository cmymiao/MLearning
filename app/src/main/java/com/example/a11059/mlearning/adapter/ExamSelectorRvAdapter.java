package com.example.a11059.mlearning.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.a11059.mlearning.R;
import com.example.a11059.mlearning.entity.Examination;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 11059 on 2018/7/24.
 */

public class ExamSelectorRvAdapter extends RecyclerView.Adapter<ExamSelectorRvAdapter.ViewHolder> {

    private Context mContext;

    private List<Examination> examinations = new ArrayList<>();

    public ExamSelectorRvAdapter(List<Examination> examinationList){examinations = examinationList;}

    static class ViewHolder extends RecyclerView.ViewHolder{

        LinearLayout examView;
        TextView examNum;
        TextView examName;
        TextView examDifficulty;

        public ViewHolder(View view){
            super(view);
            examView = (LinearLayout) view.findViewById(R.id.exam_view);
            examNum = (TextView) view.findViewById(R.id.exam_num);
            examName = (TextView) view.findViewById(R.id.exam_name);
            examDifficulty = (TextView) view.findViewById(R.id.exam_difficulty);
        }

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(mContext == null){
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.selector_exam_rv, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExamSelectorRvAdapter.ViewHolder holder, final int position) {
        Examination examination = examinations.get(position);
        holder.examNum.setText("试卷" + (position+1));
        holder.examName.setText(examination.getName());
        holder.examDifficulty.setText("难度系数：" + examination.getDifficulty());
        holder.examView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onItemClickListener != null){
                    onItemClickListener.onClick(position);
                }
            }
        });
    }

    public interface OnItemClickListener{
        void onClick(int examIndex);
    }

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(ExamSelectorRvAdapter.OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public int getItemCount() {
        return examinations == null ? 0:examinations.size();
    }
}
