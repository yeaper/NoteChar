package cn.yyp.nc.ui.publish_note;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.File;

import butterknife.Bind;
import butterknife.OnClick;
import cn.jzvd.JZVideoPlayer;
import cn.jzvd.JZVideoPlayerStandard;
import cn.yyp.nc.R;
import cn.yyp.nc.base.ParentWithNaviActivity;
import cn.yyp.nc.model.global.C;
import cn.yyp.nc.util.FileUtil;
import cn.yyp.nc.util.TimeUtil;
import cn.yyp.nc.util.video.VideoManager;

public class CreateNoteVideoActivity extends ParentWithNaviActivity {

    @Bind(R.id.et_video_title)
    EditText video_title;
    @Bind(R.id.surface_view)
    SurfaceView surfaceView;
    @Bind(R.id.video_player)
    JZVideoPlayerStandard videoPlayer;
    @Bind(R.id.video_duration)
    TextView video_duration;
    @Bind(R.id.btn_record_play_stop)
    Button record_play_stop;
    @Bind(R.id.video_name)
    TextView video_name;
    @Bind(R.id.video_play_rl)
    RelativeLayout video_play_rl;

    private VideoManager videoManager;
    int record_state = C.RecordState.STOP;

    @Override
    protected String title() {
        return "视频笔记";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_note_video);
        initNaviView();

        videoManager = VideoManager.getInstance();
        videoManager.initRecorder(surfaceView);
        videoManager.initCamera(this);
        videoManager.setVideoRecorderListener(new VideoManager.IVideoRecorderListener() {
            @Override
            public void start() {
                record_state = C.RecordState.START;
                runnable.run();
                record_play_stop.setEnabled(true);
                record_play_stop.setText("停止录制");
            }

            @Override
            public void stop(String filePath, String fileName) {
                record_state = C.RecordState.STOP;
                record_play_stop.setEnabled(false);
                record_play_stop.setText("录制结束");
                video_play_rl.setVisibility(View.VISIBLE);
                video_name.setText(fileName);
                // 显示播放器
                surfaceView.setVisibility(View.GONE);
                videoPlayer.setVisibility(View.VISIBLE);
                videoPlayer.setUp("file://"+filePath+"/"+fileName, JZVideoPlayerStandard.SCREEN_WINDOW_NORMAL, "");
            }
        });
    }

    @OnClick({R.id.btn_record_play_stop,R.id.btn_save})
    public void click(View v){
        switch (v.getId()){
            case R.id.btn_record_play_stop:
                if(record_state == C.RecordState.STOP){//开始录制
                    try {
                        videoManager.startRecord(FileUtil.getDiscFileDir(this)+"/", System.currentTimeMillis() + ".mp4");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else if(record_state == C.RecordState.START) { //停止录制
                    videoManager.stopRecord();
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
                video_duration.setText("时长："+ TimeUtil.showTimeCount(count)); //刷新time视图显示计时
                handler.postDelayed(this, 1000);//每一秒刷新一次
            }
        }
    };

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

    // 销毁、回收资源
    @Override
    protected void onDestroy() {
        super.onDestroy();
        videoManager.releaseAll();
    }
}
