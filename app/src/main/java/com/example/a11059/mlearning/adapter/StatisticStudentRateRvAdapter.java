package com.example.a11059.mlearning.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.a11059.mlearning.R;
import com.example.a11059.mlearning.entity.Statistic;

import java.text.DecimalFormat;
import java.util.List;

public class StatisticStudentRateRvAdapter extends RecyclerView.Adapter<StatisticStudentRateRvAdapter.ViewHolder>{

    private static final int VIEW_TYPE_COMMON = 1;

    private static final int VIEW_TYPE_HEADER = 2;

    public static final int HIGH_RATE_FLAG = 0;

    public static final int LOW_RATE_FLAG = 1;

    public static final int NULL_RATE_FLAG = 2;

    private Context mContext;

    private List<Statistic> mSStatisInfos;

    public StatisticStudentRateRvAdapter(List<Statistic> sStatisInfos){
        mSStatisInfos = sStatisInfos;
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
            view = LayoutInflater.from(mContext).inflate(R.layout.tsta_stu_rate_rv_item, parent, false);
            return new ViewHolder(view, VIEW_TYPE_COMMON);
        } else { //header加载header布局
            view = LayoutInflater.from(mContext).inflate(R.layout.tsta_stu_rate_rv_header, parent, false);
            return new ViewHolder(view, VIEW_TYPE_HEADER);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if(getItemViewType(position) == VIEW_TYPE_HEADER){
            return;
        }
        Statistic sStatisInfo = mSStatisInfos.get(position - 1);
        holder.sUsername.setText(sStatisInfo.getUsername());
        holder.sName.setText(sStatisInfo.getName());
        String sTNum = sStatisInfo.getTotalNum() + "";
        holder.sTNum.setText(sTNum);
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        String sERate = decimalFormat.format(sStatisInfo.getAccuracy()) + "%";
        holder.sERate.setText(sERate);
        /*
        if(sStatisInfo.getHighRate() == HIGH_RATE_FLAG){
            holder.sERate.setTextColor(ContextCompat.getColor(mContext, R.color.red));
        }
        else if(sStatisInfo.getHighRate() == LOW_RATE_FLAG){
            holder.sERate.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
        }else if(sStatisInfo.getHighRate() == NULL_RATE_FLAG){
            holder.sERate.setTextColor(ContextCompat.getColor(mContext, R.color.grayText));
        }
        */
    }

    @Override
    public int getItemCount() {
        return mSStatisInfos == null ? 0 : mSStatisInfos.size() + 1;
    }

    static class ViewHolder extends RecyclerView.ViewHolder{

        LinearLayout studentView;
        TextView sUsername;
        TextView sName;
        TextView sTNum;
        TextView sERate;

        public ViewHolder(View view, int viewType){
            super(view);
            if(viewType == VIEW_TYPE_HEADER){
                return;
            }
            studentView = (LinearLayout) view.findViewById(R.id.s_view);
            sUsername = (TextView) view.findViewById(R.id.s_username);
            sName = (TextView) view.findViewById(R.id.s_name);
            sTNum = (TextView) view.findViewById(R.id.s_t_num);
            sERate = (TextView) view.findViewById(R.id.s_e_rate);
        }

    }
}
