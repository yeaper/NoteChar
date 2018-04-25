package cn.yyp.nc.ui.show_note;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.leon.lfilepickerlibrary.LFilePicker;

import java.io.File;

import butterknife.Bind;
import butterknife.OnClick;
import cn.jzvd.JZVideoPlayer;
import cn.jzvd.JZVideoPlayerStandard;
import cn.yyp.nc.R;
import cn.yyp.nc.base.ParentWithNaviActivity;
import cn.yyp.nc.greendao.Note;
import cn.yyp.nc.model.global.C;
import cn.yyp.nc.util.FileUtil;
import cn.yyp.nc.util.video.VideoManager;
import cn.yyp.nc.util.voice.VoiceManager;

public class ShowNoteVideoActivity extends ParentWithNaviActivity {

    @Bind(R.id.note_type_img)
    public ImageView type_img;
    @Bind(R.id.note_title)
    public TextView title;
    @Bind(R.id.note_create_time)
    public TextView create_time;
    @Bind(R.id.note_star)
    public ImageView star;

    @Bind(R.id.video_player)
    public JZVideoPlayer videoPlayer;
    @Bind(R.id.video_name)
    public TextView video_name;

    private Note note;

    @Override
    protected String title() {
        return "笔记详情";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_note_video);
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

        video_name.setText(FileUtil.getFileName(note.getVideoUrl()));

        // 播放器
        videoPlayer.setUp("file://"+note.getVideoUrl(), JZVideoPlayerStandard.SCREEN_WINDOW_NORMAL, "");
    }

    @Override
    public void onBackPressed() {
        if (JZVideoPlayer.backPress()) {
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        JZVideoPlayer.releaseAllVideos();
    }
}
