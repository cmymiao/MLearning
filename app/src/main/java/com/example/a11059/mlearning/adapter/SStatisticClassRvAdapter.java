package com.example.a11059.mlearning.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.a11059.mlearning.R;
import com.example.a11059.mlearning.activity.StudentStatisticActivity;
import com.example.a11059.mlearning.entity.Statistic;
import com.example.a11059.mlearning.entity.User;

import java.util.List;

import cn.bmob.v3.BmobUser;

/**
 * Created by 11059 on 2018/9/7.
 */

public class SStatisticClassRvAdapter extends RecyclerView.Adapter<SStatisticClassRvAdapter.ViewHolder> {

    private static final int VIEW_TYPE_COMMON = 1;

    private static final int VIEW_TYPE_HEADER = 2;

    private User currentUser;

    private Context mContext;

    private StudentStatisticActivity parentActivity;

    private List<Statistic> mStatisticList;

    public SStatisticClassRvAdapter(List<Statistic> statisticList){
        mStatisticList = statisticList;
        currentUser = BmobUser.getCurrentUser(User.class);
    }

    static class ViewHolder extends RecyclerView.ViewHolder{

        TextView classNumber;
        TextView userNickname;
        TextView totalNum;
        TextView userAccuracy;

        public ViewHolder(View view, int viewType){
            super(view);
            if(viewType == VIEW_TYPE_HEADER){
                return;
            }
            classNumber = (TextView) view.findViewById(R.id.class_number);
            userNickname = (TextView) view.findViewById(R.id.user_nickname);
            totalNum = (TextView) view.findViewById(R.id.total_num);
            userAccuracy = (TextView) view.findViewById(R.id.user_accuracy);
        }
    }



    @Override
    public int getItemCount() {
        return mStatisticList == null ? 0 : mStatisticList.size() + 1;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(mContext == null){
            mContext = parent.getContext();
        }
        View view;
        if(viewType == VIEW_TYPE_COMMON){ //普通item加载item布局
            view = LayoutInflater.from(mContext).inflate(R.layout.statistic_class_rv_item, parent, false);
            return new ViewHolder(view, VIEW_TYPE_COMMON);
        } else { //header加载header布局
            view = LayoutInflater.from(mContext).inflate(R.layout.statistic_class_rv_header, parent, false);
            return new ViewHolder(view, VIEW_TYPE_HEADER);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if(getItemViewType(position) == VIEW_TYPE_HEADER){ //如果是header则无需绑定数据
            return;
        }
        final int lPosition = holder.getLayoutPosition();
        holder.classNumber.setText(lPosition + "");
        holder.userNickname.setText(mStatisticList.get(lPosition-1).getNickname() + "");
        holder.totalNum.setText(mStatisticList.get(lPosition-1).getTotalNum() + "");
        holder.userAccuracy.setText(mStatisticList.get(lPosition-1).getAccuracy() + "%");
    }

    @Override
    public int getItemViewType(int position) {
        if(position == 0){
            return VIEW_TYPE_HEADER;
        }
        return VIEW_TYPE_COMMON;
    }

}
