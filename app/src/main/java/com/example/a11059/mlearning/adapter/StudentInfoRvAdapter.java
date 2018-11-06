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
import com.example.a11059.mlearning.activity.StudentInfoActivity;
import com.example.a11059.mlearning.entity.User;

import java.util.List;

public class StudentInfoRvAdapter extends RecyclerView.Adapter<StudentInfoRvAdapter.ViewHolder> {

    private static final int VIEW_TYPE_COMMON = 1;

    private static final int VIEW_TYPE_HEADER = 2;

    private StudentInfoActivity parentActivity;

    private Context mContext;

    private List<User> mStudentList;


    public StudentInfoRvAdapter(List<User> studentList){
        mStudentList = studentList;
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView studentId;
        TextView studentName;
        TextView studentNickname;
        ImageView studentDetail;


        public ViewHolder(View view, int viewType){
            super(view);
            if(viewType == VIEW_TYPE_HEADER){
                return;
            }
            studentId = (TextView) view.findViewById(R.id.student_id);
            studentName = (TextView) view.findViewById(R.id.studnet_name);
            studentNickname = (TextView) view.findViewById(R.id.student_nickname);
            studentDetail = (ImageView) view.findViewById(R.id.show_detail_information);

        }
    }
    @Override
    public int getItemCount() {
        return mStudentList == null ? 0 : mStudentList.size() + 1;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (mContext == null){
            mContext = parent.getContext();
        }
        View view;
        if (viewType == VIEW_TYPE_COMMON){//普通item加载item布局
            view = LayoutInflater.from(mContext).inflate(R.layout.student_information_rv_item, parent, false);
            return new ViewHolder(view,VIEW_TYPE_COMMON);
        }else {//header加载header布局
            view = LayoutInflater.from(mContext).inflate(R.layout.student_information_rv_header, parent, false);
            return new ViewHolder(view,VIEW_TYPE_HEADER);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        if(getItemViewType(position) == VIEW_TYPE_HEADER){ //如果是header则无需绑定数据
            return;
        }
        //绑定数据
        final int lPosition = holder.getLayoutPosition();
        holder.studentId.setText(mStudentList.get(lPosition-1).getUsername());
        holder.studentName.setText(mStudentList.get(lPosition-1).getName());
        holder.studentNickname.setText(mStudentList.get(lPosition-1).getNickname());
        holder.studentDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(OnShowSDetailClickListener != null){
                    OnShowSDetailClickListener.onClick(position - 1);
                }
            }
        });
    }
    public interface OnShowSDetailClickListener {
        void onClick(int position);
    }
    private OnShowSDetailClickListener OnShowSDetailClickListener;

    public void setOnItemClickListener(StudentInfoRvAdapter.OnShowSDetailClickListener OnShowSDetailClickListener){
        this.OnShowSDetailClickListener = OnShowSDetailClickListener;
    }
    @Override
    public int getItemViewType(int position) {
        if(position == 0){
            return VIEW_TYPE_HEADER;
        }
        return VIEW_TYPE_COMMON;
    }
}
