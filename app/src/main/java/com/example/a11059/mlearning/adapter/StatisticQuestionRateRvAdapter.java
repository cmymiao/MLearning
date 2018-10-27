package com.example.a11059.mlearning.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.a11059.mlearning.R;
import com.example.a11059.mlearning.entity.Statistic;

import java.text.DecimalFormat;
import java.util.List;

public class StatisticQuestionRateRvAdapter extends RecyclerView.Adapter<StatisticQuestionRateRvAdapter.ViewHolder>{
    private static final int VIEW_TYPE_COMMON = 1;

    private static final int VIEW_TYPE_HEADER = 2;

    private Context mContext;

    private List<Statistic> statisticListt;

    public StatisticQuestionRateRvAdapter(List<Statistic> mStatisticList){
        statisticListt = mStatisticList;
    }

    @Override
    public int getItemViewType(int position) {
        if(position == 0){
            return VIEW_TYPE_HEADER;
        }
        return VIEW_TYPE_COMMON;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(mContext == null){
            mContext = parent.getContext();
        }
        View view;
        if(viewType == VIEW_TYPE_COMMON){ //普通item加载item布局
            view = LayoutInflater.from(mContext).inflate(R.layout.tsta_ques_rv_item, parent, false);
            return new ViewHolder(view, VIEW_TYPE_COMMON);
        } else { //header加载header布局
            view = LayoutInflater.from(mContext).inflate(R.layout.tsta_ques_rv_header, parent, false);
            return new ViewHolder(view, VIEW_TYPE_HEADER);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        if(getItemViewType(position) == VIEW_TYPE_HEADER){ //如果是header则无需绑定数据
            return;
        }
        final Statistic qStatisInfo = statisticListt.get(position - 1);
        String qId = qStatisInfo.getQuestionId() + "";
        switch (qId.length()){
            case 1:
                qId = "000" + qId;
                break;
            case 2:
                qId = "00" + qId;
                break;
            case 3:
                qId = "0" + qId;
                break;
            case 4:
            default:
                break;
        }
        holder.questionId.setText(qId);
        final String qTNum = qStatisInfo.getTotalNum() + "";
        holder.questionTNum.setText(qTNum);
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        String qERate = decimalFormat.format(qStatisInfo.getAccuracy()) + "%";
        holder.questionERate.setText(qERate);
        holder.showQContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onShowQContentClickListener != null){
                    onShowQContentClickListener.onClick(position - 1);
                }
            }
        });
    }

    public interface OnShowQContentClickListener {
        void onClick(int position);
    }

    private OnShowQContentClickListener onShowQContentClickListener;

    public void setOnItemClickListener(StatisticQuestionRateRvAdapter.OnShowQContentClickListener onShowQContentClickListener){
        this.onShowQContentClickListener = onShowQContentClickListener;
    }

    @Override
    public int getItemCount() {
        return statisticListt == null ? 0 : statisticListt.size() + 1;
    }

    static class ViewHolder extends RecyclerView.ViewHolder{

        TextView questionId;
        TextView questionTNum;
        TextView questionERate;
        ImageView showQContent;

        public ViewHolder(View view, int viewType){
            super(view);
            if(viewType == VIEW_TYPE_HEADER){
                return;
            }
            questionId = (TextView) view.findViewById(R.id.question_id);
            questionTNum = (TextView) view.findViewById(R.id.question_t_num);
            questionERate = (TextView) view.findViewById(R.id.question_e_rate);
            showQContent = (ImageView) view.findViewById(R.id.show_q_content);
        }

    }
}
