package cn.yyp.nc.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.Bind;
import cn.yyp.nc.R;
import cn.yyp.nc.greendao.Note;
import cn.yyp.nc.model.global.C;

/**
 * 笔记适配
 */

public class NoteAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Note> notes = new ArrayList<>();

    public void setDatas(List<Note> list) {

        notes.clear();
        if (null != list) {
            notes.addAll(list);
            sort();
            notifyDataSetChanged();
        }
    }

    public void clear() {
        notes.clear();
        notifyDataSetChanged();
    }

    /**
     * 处理置顶的排序
     */
    public void sort(){
        Collections.sort(notes, new Comparator<Note>() {

            @Override
            public int compare(Note o1, Note o2) {
                if(o1.isTop() && o2.isTop()){
                    return 0;
                }else if(o1.isTop() && !o2.isTop()){
                    return -1;
                }else{
                    return 1;
                }
            }
        });
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new NoteAdapter.MyHolder(parent.getContext(), parent, onRecyclerViewListener);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((BaseViewHolder) holder).bindData(notes.get(position));
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    public class MyHolder extends BaseViewHolder<Note> {

        @Bind(R.id.note_item)
        public LinearLayout item;
        @Bind(R.id.note_type_img)
        public ImageView type_img;
        @Bind(R.id.note_title)
        public TextView title;
        @Bind(R.id.note_create_time)
        public TextView create_time;
        @Bind(R.id.note_star)
        public ImageView star;

        public MyHolder(Context context, ViewGroup root, OnRecyclerViewListener onRecyclerViewListener) {
            super(context, root, R.layout.item_note, onRecyclerViewListener);
        }

        @Override
        public void bindData(final Note note) {
            //置顶
            if(note.isTop()){
                item.setBackgroundColor(context.getResources().getColor(R.color.top_bg_color));
            }else{
                item.setBackgroundColor(context.getResources().getColor(R.color.white));
            }
            //加星
            if(note.isStar()){
                star.setVisibility(View.VISIBLE);
            }else{
                star.setVisibility(View.GONE);
            }
            //笔记类型
            switch (note.getNoteType()){
                case C.NoteType.Img_Txt:
                    type_img.setImageResource(R.drawable.note_img_txt);
                    break;
                case C.NoteType.Voice:
                    type_img.setImageResource(R.drawable.note_voice);
                    break;
                case C.NoteType.Video:
                    type_img.setImageResource(R.drawable.note_video);
                    break;
            }
            title.setText(note.getTitle());
            create_time.setText(note.getCreateTime());
        }
    }

    private OnRecyclerViewListener onRecyclerViewListener;

    public void setOnRecyclerViewListener(OnRecyclerViewListener onRecyclerViewListener) {
        this.onRecyclerViewListener = onRecyclerViewListener;
    }
}