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
import com.example.a11059.mlearning.entity.Knowledge;
import com.example.a11059.mlearning.entity.Unit;
import com.wanjian.view.ExpandableAdapter;

import java.util.List;

/**
 * Created by 11059 on 2018/8/2.
 */

public class Unit_KnowledgeRvAdapter extends ExpandableAdapter<Unit_KnowledgeRvAdapter.GroupVH, Unit_KnowledgeRvAdapter.ChildVH> {

    private Context mContext;

    private List<Unit> mGroups;

    private List<List<Knowledge>> mChildss;

    public Unit_KnowledgeRvAdapter(List<Unit> groups, List<List<Knowledge>> childss){
        super();
        mGroups = groups;
        mChildss = childss;
        collapseAllGroup();//关闭所有组
    }

    static class GroupVH extends RecyclerView.ViewHolder{

        LinearLayout unitView;
        ImageView unitOpen;
        TextView unitNum;
        TextView unitName;

        public GroupVH(View view){
            super(view);
            unitView = (LinearLayout) view.findViewById(R.id.unit_view_item);
            unitOpen = (ImageView) view.findViewById(R.id.unit_open);
            unitNum = (TextView) view.findViewById(R.id.unit_num_item);
            unitName = (TextView) view.findViewById(R.id.unit_name_item);
        }

    }

    static class ChildVH extends RecyclerView.ViewHolder{

        LinearLayout knowledgeView;
        TextView knowledgeNum;
        TextView knowledgeName;

        public ChildVH(View view){
            super(view);
            knowledgeView = (LinearLayout) view.findViewById(R.id.knowledge_view);
            knowledgeNum = (TextView) view.findViewById(R.id.knowledge_num);
            knowledgeName = (TextView) view.findViewById(R.id.knowledge_name);
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
        gHolder.unitView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ObjectAnimator animator;
                DecelerateInterpolator interpolator = new DecelerateInterpolator();
                if(isExpanded(groupIndex)){
                    //关闭组列表
                    collapseGroup(groupIndex);
                    animator = ObjectAnimator.ofFloat(gHolder.unitOpen, "rotation", 90, 0);
                    animator.setDuration(400);
                    animator.setInterpolator(interpolator);
                    animator.start();
                } else {
                    //展开组列表
                    expandGroup(groupIndex);
                    animator = ObjectAnimator.ofFloat(gHolder.unitOpen, "rotation", 0, 90);
                    animator.setDuration(400);
                    animator.setInterpolator(interpolator);
                    animator.start();
                }
            }
        });
        Unit gItem = mGroups.get(groupIndex);
        gHolder.unitNum.setText("第" + gItem.getId() + "单元");
        gHolder.unitName.setText(gItem.getName());

        if(isExpanded(groupIndex)){
            gHolder.unitOpen.setRotation(90);
        } else {
            gHolder.unitOpen.setRotation(0);
        }
    }

    @Override
    protected ChildVH onCreateChildViewHolder(ViewGroup parent, int viewType) {
        if(mContext == null){
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.selector_knowledge_rv_item, parent, false);
        return new ChildVH(view);
    }

    @Override
    protected void onBindChildViewHolder(ChildVH cHolder, final int groupIndex, final int childIndex) {
        final Knowledge cItem = mChildss.get(groupIndex).get(childIndex);
        cHolder.knowledgeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onItemClickListener != null){
                    onItemClickListener.onClick(groupIndex, childIndex);
                }
            }
        });
        cHolder.knowledgeNum.setText("知识点" + cItem.getId() + "：");
        cHolder.knowledgeName.setText(cItem.getName());
    }

    public interface OnItemClickListener {
        void onClick(int moduleIndex, int chapterIndex);
    }

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(Unit_KnowledgeRvAdapter.OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
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



}
