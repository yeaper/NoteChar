package cn.yyp.nc.ui.show_note;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bm.library.PhotoView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import cn.yyp.nc.R;
import cn.yyp.nc.base.ParentWithNaviActivity;
import cn.yyp.nc.greendao.Note;
import cn.yyp.nc.model.global.C;
import cn.yyp.nc.util.ImageLoadUtil;

public class ShowNoteImgTxtActivity extends ParentWithNaviActivity {

    @Bind(R.id.note_type_img)
    public ImageView type_img;
    @Bind(R.id.note_title)
    public TextView title;
    @Bind(R.id.note_create_time)
    public TextView create_time;
    @Bind(R.id.note_star)
    public ImageView star;
    @Bind(R.id.note_content)
    public TextView content;

    @Bind(R.id.note_img_zone)
    public LinearLayout img_zone;
    @Bind(R.id.note_img_one)
    public PhotoView img_one;
    @Bind(R.id.note_img_two)
    public PhotoView img_two;
    @Bind(R.id.note_img_three)
    public PhotoView img_three;

    private List<String> imageList = new ArrayList<>();

    private Note note;

    @Override
    protected String title() {
        return "笔记详情";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_note_img_txt);
        initNaviView();

        if(getIntent()!=null){
            note = (Note) getIntent().getSerializableExtra("note");
        }

        initUI();
    }

    private void initUI(){
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
        content.setText(note.getContent());

        initImg();
    }

    private void initImg(){
        imageList = note.getImageList();
        if(imageList==null || imageList.size()<=0){
            img_zone.setVisibility(View.GONE);
            return;
        }
        img_zone.setVisibility(View.VISIBLE);
        setImageVisible(imageList.size());
        switch (imageList.size()){
            case 1:
                ImageLoadUtil.loadLocalImage(imageList.get(0), img_one);
                break;
            case 2:
                ImageLoadUtil.loadLocalImage(imageList.get(0), img_one);
                ImageLoadUtil.loadLocalImage(imageList.get(1), img_two);
                break;
            case 3:
                ImageLoadUtil.loadLocalImage(imageList.get(0), img_one);
                ImageLoadUtil.loadLocalImage(imageList.get(1), img_two);
                ImageLoadUtil.loadLocalImage(imageList.get(2), img_three);
                break;
        }
    }

    private void setImageVisible(int count){
        img_one.setVisibility(View.GONE);
        img_two.setVisibility(View.GONE);
        img_three.setVisibility(View.GONE);
        switch (count){
            case 1:
                img_one.setVisibility(View.VISIBLE);
                img_two.setVisibility(View.GONE);
                img_three.setVisibility(View.GONE);
                img_one.enable(); //开启缩放
                break;
            case 2:
                img_one.setVisibility(View.VISIBLE);
                img_two.setVisibility(View.VISIBLE);
                img_three.setVisibility(View.GONE);
                img_one.enable(); //开启缩放
                img_two.enable();
                break;
            case 3:
                img_one.setVisibility(View.VISIBLE);
                img_two.setVisibility(View.VISIBLE);
                img_three.setVisibility(View.VISIBLE);
                img_one.enable(); //开启缩放
                img_two.enable();
                img_three.enable();
                break;
        }
    }
}
