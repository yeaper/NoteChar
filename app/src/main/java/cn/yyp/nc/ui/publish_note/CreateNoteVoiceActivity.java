package cn.yyp.nc.ui.publish_note;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.BoolRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.BrokenBarrierException;

import butterknife.Bind;
import butterknife.OnClick;
import cn.yyp.nc.R;
import cn.yyp.nc.base.ParentWithNaviActivity;
import cn.yyp.nc.model.global.C;
import cn.yyp.nc.util.FileUtil;
import cn.yyp.nc.util.TimeUtil;
import cn.yyp.nc.util.voice.VoiceManager;

public class CreateNoteVoiceActivity extends ParentWithNaviActivity {

    @Bind(R.id.voice_title)
    EditText voice_title;
    @Bind(R.id.voice_duration)
    TextView voice_duration;
    @Bind(R.id.voice_name)
    TextView voice_name;
    @Bind(R.id.voice_play_rl)
    RelativeLayout voice_play_rl;

    @Bind(R.id.btn_record_play_stop)
    Button record_play_stop;
    @Bind(R.id.btn_play_stop)
    ImageView play_stop;

    private VoiceManager voiceManager;
    int record_state = C.RecordState.STOP;
    int play_state = C.PlayState.STOP;

    File audioFile;

    @Override
    protected String title() {
        return "音频笔记";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_note_voice);
        initNaviView();
        initVoiceManager();
    }

    private void initVoiceManager(){
        voiceManager = VoiceManager.getInstance();
        voiceManager.initRecorder();
        voiceManager.initMediaPlayer();
        voiceManager.setVoiceRecorderListener(new VoiceManager.IVoiceRecorderListener() {
            @Override
            public void start() {
                record_state = C.RecordState.START;
                runnable.run();
                record_play_stop.setEnabled(true);
                record_play_stop.setText("停止录制");
            }

            @Override
            public void stop() {
                record_state = C.RecordState.STOP;
                record_play_stop.setEnabled(false);
                record_play_stop.setText("录制结束");
                voice_play_rl.setVisibility(View.VISIBLE);
                voice_name.setText(audioFile.getName());
            }
        });
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
    }

    @OnClick({R.id.btn_record_play_stop,R.id.btn_play_stop,R.id.btn_save})
    public void click(View v){
        switch (v.getId()){
            case R.id.btn_record_play_stop:
                if(record_state == C.RecordState.STOP){//开始录制
                    audioFile = new File(FileUtil.getDiscFileDir(this), String.valueOf(System.currentTimeMillis())+".amr");
                    try {
                        voiceManager.startRecord(audioFile);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else if(record_state == C.RecordState.START) { //停止录制
                    voiceManager.stopRecord();
                }
                break;
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
            case R.id.btn_save:
                break;
        }
    }

    final Handler handler = new Handler();
    int count = 0;

    final Runnable runnable = new Runnable(){
        @Override
        public void run() {
            if(record_state != C.RecordState.STOP){
                count++;
                voice_duration.setText("时长："+ TimeUtil.showTimeCount(count)); //刷新time视图显示计时
                handler.postDelayed(this, 1000);//每一秒刷新一次
            }
        }
    };

    // 销毁、回收资源
    @Override
    protected void onDestroy() {
        super.onDestroy();
        voiceManager.releaseAll();
    }
}
