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
import com.example.a11059.mlearning.entity.Class;

import java.util.List;


public class ClassSelectorRvAdapter extends RecyclerView.Adapter<ClassSelectorRvAdapter.ViewHolder>{

    private Context mContext;

    private List<Class> classes;

    public ClassSelectorRvAdapter(List<Class> classesList){
        classes = classesList;
    }

    static class ViewHolder extends RecyclerView.ViewHolder{

        LinearLayout classView;
//        TextView classNum;
        TextView className;

        public ViewHolder(View view){
            super(view);
            classView = (LinearLayout) view.findViewById(R.id.class_view);
//            classNum = (TextView) view.findViewById(R.id.class_num);
            className = (TextView) view.findViewById(R.id.class_name);
        }

    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(mContext == null){
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.selector_class_rv, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClassSelectorRvAdapter.ViewHolder holder, final int position) {
        Class aClass = classes.get(position);
//        holder.classNum.setText("第"+(position+1)+"单元");
        holder.className.setText(aClass.getName());
        holder.classView.setOnClickListener(new View.OnClickListener() {
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

    public void setOnItemClickListener(ClassSelectorRvAdapter.OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public int getItemCount() {
        return classes == null ? 0:classes.size();
    }
}
