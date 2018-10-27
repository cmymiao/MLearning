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
import com.example.a11059.mlearning.entity.Course;

import java.util.List;

public class CourseAllSelectorRvAdapter extends RecyclerView.Adapter<CourseAllSelectorRvAdapter.ViewHolder>{

    private Context mContext;

    private List<Course> courses;

    public CourseAllSelectorRvAdapter(List<Course> courseList){
        courses = courseList;
    }

    static class ViewHolder extends RecyclerView.ViewHolder{

        LinearLayout courseView;
        //        TextView classNum;
        TextView courseName;

        public ViewHolder(View view){
            super(view);
            courseView = (LinearLayout) view.findViewById(R.id.course_view);
//            classNum = (TextView) view.findViewById(R.id.class_num);
            courseName = (TextView) view.findViewById(R.id.course_name);
        }

    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(mContext == null){
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.selector_all_course_rv, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseAllSelectorRvAdapter.ViewHolder holder, final int position) {
        Course course = courses.get(position);
//        holder.classNum.setText("第"+(position+1)+"单元");
        holder.courseName.setText(course.getName());
        holder.courseView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onItemClickListener != null){
                    onItemClickListener.onClick(position);
                }
            }
        });
    }

    public interface OnItemClickListener{
        void onClick(int unitIndex);
    }

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(CourseAllSelectorRvAdapter.OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public int getItemCount() {
        return courses == null ? 0:courses.size();
    }
}
