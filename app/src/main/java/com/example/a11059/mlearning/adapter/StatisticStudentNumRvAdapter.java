package com.example.a11059.mlearning.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.a11059.mlearning.R;
import com.example.a11059.mlearning.entity.Statistic;
import com.example.a11059.mlearning.entity.User;

import java.util.List;

public class StatisticStudentNumRvAdapter extends RecyclerView.Adapter<StatisticStudentNumRvAdapter.ViewHolder>{

    private static final int VIEW_TYPE_COMMON = 1;

    private static final int VIEW_TYPE_HEADER = 2;

    private Context mContext;

    private List<Statistic> mNFSudents;

    public StatisticStudentNumRvAdapter(List<Statistic> nfStudents){
        mNFSudents = nfStudents;
    }

    @Override
    public int getItemViewType(int position) {
        if(position == 0){
            return VIEW_TYPE_HEADER;
        }
        return VIEW_TYPE_COMMON;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(mContext == null){
            mContext = parent.getContext();
        }
        View view;
        if(viewType == VIEW_TYPE_COMMON){
            view = LayoutInflater.from(mContext).inflate(R.layout.tsta_stu_num_rv_item, parent, false);
            return new ViewHolder(view, VIEW_TYPE_COMMON);
        } else {
            view = LayoutInflater.from(mContext).inflate(R.layout.tsta_stu_num_rv_header, parent, false);
            return new ViewHolder(view, VIEW_TYPE_HEADER);
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if(getItemViewType(position) == VIEW_TYPE_HEADER){ //如果是header直接返回
            return;
        }
        Statistic nfStudent = mNFSudents.get(position - 1);
        String nfsNum = (position) + "";
        holder.nfsNum.setText(nfsNum);
        holder.nfsUsername.setText(nfStudent.getUsername());
        holder.nfsName.setText(nfStudent.getName());
        holder.totalNum.setText(nfStudent.getTotalNum()+"");
    }

    @Override
    public int getItemCount() {
        return mNFSudents == null ? 0 : mNFSudents.size() + 1;
    }

    static class ViewHolder extends RecyclerView.ViewHolder{

        TextView nfsNum;
        TextView nfsUsername;
        TextView nfsName;
        TextView totalNum;

        public ViewHolder(View view, int viewType){
            super(view);
            if(viewType == VIEW_TYPE_HEADER){
                return;
            }
            nfsNum = (TextView) view.findViewById(R.id.nfs_num);
            nfsUsername = (TextView) view.findViewById(R.id.nfs_username);
            nfsName = (TextView) view.findViewById(R.id.nfs_name);
            totalNum = (TextView) view.findViewById(R.id.nfs_number);
        }

    }
}
