package com.example.a11059.mlearning.adapter;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.a11059.mlearning.R;
import com.example.a11059.mlearning.entity.Course;
import com.example.a11059.mlearning.entity.Unit;
import com.wanjian.view.ExpandableAdapter;

import java.util.List;

public class CourseUnitRvAdapter extends ExpandableAdapter<CourseUnitRvAdapter.GroupVH, CourseUnitRvAdapter.ChildVH> {

    private Context mContext;

    private List<Course> mGroups;

    private List<List<Unit>> mChildss;

    public CourseUnitRvAdapter(List<Course> groups, List<List<Unit>> childss){
        super();
        mGroups = groups;
        mChildss = childss;
        collapseAllGroup();//关闭所有组
    }

    static class GroupVH extends RecyclerView.ViewHolder{

        LinearLayout courseView;
        ImageView courseOpen;
        TextView courseNum;
        TextView courseName;

        public GroupVH(View view){
            super(view);
            courseView = (LinearLayout) view.findViewById(R.id.unit_view_item);
            courseOpen = (ImageView) view.findViewById(R.id.unit_open);
            courseNum = (TextView) view.findViewById(R.id.unit_num_item);
            courseName = (TextView) view.findViewById(R.id.unit_name_item);
        }

    }

    static class ChildVH extends RecyclerView.ViewHolder{

        LinearLayout unitView;
        TextView unitNum;
        TextView unitName;

        public ChildVH(View view){
            super(view);
            unitView = (LinearLayout) view.findViewById(R.id.knowledge_view);
            unitNum = (TextView) view.findViewById(R.id.knowledge_num);
            unitName = (TextView) view.findViewById(R.id.knowledge_name);
        }

    }

    @Override
    protected int getGroupCount() {
        return mGroups == null ? 0 : mGroups.size();
    }

    @Override
    protected int getChildCount(int groupIndex) {
        if(mChildss == null){
            return 0;
        } else {
            return mChildss.get(groupIndex) == null ? 0 : mChildss.get(groupIndex).size();
        }
    }

    @Override
    protected GroupVH onCreateGroupViewHolder(ViewGroup parent, int viewType) {
        if(mContext == null){
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.selector_unit_rv_item, parent, false);
        return new GroupVH(view);
    }

    @Override
    protected void onBindGroupViewHolder(final GroupVH gHolder, final int groupIndex) {
        gHolder.courseView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ObjectAnimator animator;
                DecelerateInterpolator interpolator = new DecelerateInterpolator();
                if(isExpanded(groupIndex)){
                    //关闭组列表
                    collapseGroup(groupIndex);
                    animator = ObjectAnimator.ofFloat(gHolder.courseOpen, "rotation", 90, 0);
                    animator.setDuration(400);
                    animator.setInterpolator(interpolator);
                    animator.start();
                } else {
                    //展开组列表
                    expandGroup(groupIndex);
                    animator = ObjectAnimator.ofFloat(gHolder.courseOpen, "rotation", 0, 90);
                    animator.setDuration(400);
                    animator.setInterpolator(interpolator);
                    animator.start();
                }
            }
        });
        Course gItem = mGroups.get(groupIndex);
        gHolder.courseNum.setText("课程名： " + gItem.getName());

        if(isExpanded(groupIndex)){
            gHolder.courseOpen.setRotation(90);
        } else {
            gHolder.courseOpen.setRotation(0);
        }
    }

    @Override
    protected CourseUnitRvAdapter.ChildVH onCreateChildViewHolder(ViewGroup parent, int viewType) {
        if(mContext == null){
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.selector_knowledge_rv_item, parent, false);
        return new ChildVH(view);
    }

    @Override
    protected void onBindChildViewHolder(ChildVH cHolder, final int groupIndex, final int childIndex) {
        final Unit cItem = mChildss.get(groupIndex).get(childIndex);
        cHolder.unitView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onItemClickListener != null){
                    onItemClickListener.onClick(groupIndex, childIndex);
                }
            }
        });
        cHolder.unitNum.setText("第" + cItem.getId() + "单元 ");
        cHolder.unitName.setText(cItem.getName());
    }

    public interface OnItemClickListener {
        void onClick(int moduleIndex, int chapterIndex);
    }

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(CourseUnitRvAdapter.OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }
}
