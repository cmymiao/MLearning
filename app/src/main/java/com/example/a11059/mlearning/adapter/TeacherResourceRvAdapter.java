package com.example.a11059.mlearning.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.a11059.mlearning.R;
import com.example.a11059.mlearning.activity.PdfViewActivity;
import com.example.a11059.mlearning.activity.TeacherResourceActivity;
import com.example.a11059.mlearning.activity.VideoActivity;
import com.example.a11059.mlearning.entity.Resource;
import com.example.a11059.mlearning.entity.User;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;

import java.util.List;

import cn.bmob.v3.BmobUser;

public class TeacherResourceRvAdapter extends RecyclerView.Adapter<TeacherResourceRvAdapter.ViewHolder>{

    private TeacherResourceActivity parentActivity;

    private User currentUser;

    private Context mContext;

    private List<Resource> mResourceList;

    private static final int TIP_TYPE_SUCCESS = 1;

    private static final int TIP_TYPE_FAIL = 0;

    private QMUITipDialog tipDialog;

    public TeacherResourceRvAdapter(TeacherResourceActivity activity, List<Resource> resourceList){
        parentActivity = activity;
        mResourceList = resourceList;
        currentUser = BmobUser.getCurrentUser(User.class);
    }

    static class ViewHolder extends RecyclerView.ViewHolder{

        LinearLayout showResources;
        ImageView materialImage;
        TextView fileName;
        TextView type;

        public ViewHolder(View view){
            super(view);
            showResources = (LinearLayout) view.findViewById(R.id.resource_content);
            materialImage = (ImageView) view.findViewById(R.id.material_image);
            fileName = (TextView) view.findViewById(R.id.filename);
            type = (TextView) view.findViewById(R.id.filetype);

        }
    }

    @NonNull
    @Override
    public TeacherResourceRvAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(mContext == null){
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.resource_teacher_recycleview, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TeacherResourceRvAdapter.ViewHolder holder, int position) {
        final int lPosition = holder.getLayoutPosition();

        if(currentUser.getImage() == null){

        } else{
            final String url = currentUser.getImage().getUrl();
            Glide.with(mContext).load(url).into(holder.materialImage);
        }


        holder.fileName.setText(mResourceList.get(lPosition).getFile().getFilename());
        holder.type.setText(mResourceList.get(lPosition).getType());

        final String urlResource = mResourceList.get(lPosition).getFile().getUrl();
        final String title = mResourceList.get(lPosition).getFile().getFilename();

        holder.showResources.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                switch (mResourceList.get(lPosition).getType()) {
                    case "pdf":
                        PdfViewActivity.actionStart(mContext, urlResource, title);
                        break;
                    case "video":
                        VideoActivity.actionStart(mContext, urlResource, title);
                        break;
                    default:
                        showTip(TIP_TYPE_FAIL, "未知类型文件");
                        break;
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mResourceList == null ? 0 : mResourceList.size();
    }

    private void showTip(int type, String tipWord){
        QMUITipDialog.Builder tipBuilder = new QMUITipDialog.Builder(mContext);
        if(type == TIP_TYPE_SUCCESS){
            tipBuilder = tipBuilder.setIconType(QMUITipDialog.Builder.ICON_TYPE_SUCCESS);
        } else if(type == TIP_TYPE_FAIL){
            tipBuilder = tipBuilder.setIconType(QMUITipDialog.Builder.ICON_TYPE_FAIL);
        }
        tipBuilder = tipBuilder.setTipWord(tipWord);
        tipDialog = tipBuilder.create();
        tipDialog.show();

    }
}
