package cn.yyp.nc.ui.show_note;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;

import butterknife.Bind;
import butterknife.OnClick;
import cn.yyp.nc.R;
import cn.yyp.nc.base.ParentWithNaviActivity;
import cn.yyp.nc.greendao.Note;
import cn.yyp.nc.model.global.C;
import cn.yyp.nc.util.FileUtil;
import cn.yyp.nc.util.voice.VoiceManager;

public class ShowNoteVoiceActivity extends ParentWithNaviActivity {

    @Bind(R.id.note_type_img)
    public ImageView type_img;
    @Bind(R.id.note_title)
    public TextView title;
    @Bind(R.id.note_create_time)
    public TextView create_time;
    @Bind(R.id.note_star)
    public ImageView star;

    @Bind(R.id.voice_name)
    public TextView voice_name;
    @Bind(R.id.btn_play_stop)
    public ImageView play_stop;

    private Note note;

    private VoiceManager voiceManager;
    int play_state = C.PlayState.STOP;
    File audioFile;

    @Override
    protected String title() {
        return "笔记详情";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_note_voice);
        initNaviView();

        if(getIntent()!=null){
            note = (Note) getIntent().getSerializableExtra("note");
        }

        audioFile = new File(note.getVoiceUrl());
        voiceManager = VoiceManager.getInstance();
        voiceManager.initMediaPlayer();
        voiceManager.setVoicePlayerListener(new VoiceManager.IVoicePlayerListener() {
            @Override
            public void start() {
                play_state = C.PlayState.START;
                play_stop.setImageResource(R.drawable.stop);
            }

            @Override
            public void stop() {
                play_state = C.PlayState.STOP;
                play_stop.setImageResource(R.drawable.play);
            }

            @Override
            public void complete() {
                play_state = C.PlayState.STOP;
                play_stop.setImageResource(R.drawable.play);
            }
        });

        initUI();
    }

    /**
     * 初始化数据
     */
    protected void initUI() {
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

        voice_name.setText(FileUtil.getFileName(note.getVoiceUrl()));
    }

    @OnClick({R.id.btn_play_stop})
    public void click(View v){
        switch (v.getId()){
            case R.id.btn_play_stop:
                if(play_state == C.PlayState.STOP){//开始播放
                    try {
                        voiceManager.startPlay(audioFile);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else if(play_state == C.PlayState.START) { //停止播放
                    voiceManager.stopPlay();
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        voiceManager.releaseAll();
    }
}
