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
import com.example.a11059.mlearning.entity.Unit;

import java.util.List;

/**
 * Created by 11059 on 2018/7/24.
 */

public class UnitSelectorRvAdapter extends RecyclerView.Adapter<UnitSelectorRvAdapter.ViewHolder> {

    private Context mContext;

    private List<Unit> units;

    public UnitSelectorRvAdapter(List<Unit> unitList){
        units = unitList;
    }

    static class ViewHolder extends RecyclerView.ViewHolder{

        LinearLayout unitView;
        TextView unitNum;
        TextView unitName;

        public ViewHolder(View view){
            super(view);
            unitView = (LinearLayout) view.findViewById(R.id.unit_view);
            unitNum = (TextView) view.findViewById(R.id.unit_num);
            unitName = (TextView) view.findViewById(R.id.unit_name);
        }

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(mContext == null){
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.selector_unit_rv, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UnitSelectorRvAdapter.ViewHolder holder, final int position) {
        Unit unit = units.get(position);
        holder.unitNum.setText("第"+(position+1)+"单元");
        holder.unitName.setText(unit.getName());
        holder.unitView.setOnClickListener(new View.OnClickListener() {
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

    public void setOnItemClickListener(UnitSelectorRvAdapter.OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public int getItemCount() {
        return units == null ? 0:units.size();
    }
}
