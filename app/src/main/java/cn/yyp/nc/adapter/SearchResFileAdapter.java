package cn.yyp.nc.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import cn.yyp.nc.R;
import cn.yyp.nc.model.ResFile;
import cn.yyp.nc.util.FileUtil;
import cn.yyp.nc.util.Util;


/**
 * 资源适配器
 */
public class SearchResFileAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private List<ResFile> files = new ArrayList<>();

    public void setDatas(List<ResFile> list) {
        files.clear();
        if (null != list) {
            files.addAll(list);
            notifyDataSetChanged();
        }
    }

    public void clear(){
        files.clear();
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyHolder(parent.getContext(), parent, onRecyclerViewListener);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((BaseViewHolder) holder).bindData(files.get(position));
    }

    @Override
    public int getItemCount() {
        return files.size();
    }

    public class MyHolder extends BaseViewHolder<ResFile>{

        @Bind(R.id.res_file_name)
        public TextView file_name;
        @Bind(R.id.res_file_upload_time)
        public TextView file_upload_time;
        @Bind(R.id.res_file_download_count)
        public TextView file_download_count;
        @Bind(R.id.res_file_type)
        TextView file_type;
        @Bind(R.id.res_file_download)
        public ImageView btn_file_download;

        public MyHolder(Context context, ViewGroup root, OnRecyclerViewListener onRecyclerViewListener) {
            super(context, root, R.layout.item_res_file, onRecyclerViewListener);
        }

        @Override
        public void bindData(final ResFile resFile) {
            file_name.setText(resFile.getFileName());
            file_upload_time.setText(resFile.getUploadTime());
            if(resFile.getDownloadCount() <= 0){
                file_download_count.setVisibility(View.GONE);
            }else if(resFile.getDownloadCount() < 100){
                file_download_count.setVisibility(View.VISIBLE);
                file_download_count.setText("已下载"+resFile.getDownloadCount()+"次");
            }else{
                file_download_count.setVisibility(View.VISIBLE );
                file_download_count.setText("已下载99+次");
            }
            file_type.setText(Util.getNoteTypeName(resFile.getResType()));
        }
    }

    private OnRecyclerViewListener onRecyclerViewListener;

    public void setOnRecyclerViewListener(OnRecyclerViewListener onRecyclerViewListener) {
        this.onRecyclerViewListener = onRecyclerViewListener;
    }
}
