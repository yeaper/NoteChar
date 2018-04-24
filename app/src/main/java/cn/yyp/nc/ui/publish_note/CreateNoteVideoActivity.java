package cn.yyp.nc.ui.publish_note;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.leon.lfilepickerlibrary.LFilePicker;
import com.leon.lfilepickerlibrary.utils.Constant;

import org.w3c.dom.Text;

import java.io.File;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import cn.jzvd.JZVideoPlayer;
import cn.jzvd.JZVideoPlayerStandard;
import cn.yyp.nc.R;
import cn.yyp.nc.base.ParentWithNaviActivity;
import cn.yyp.nc.model.global.C;
import cn.yyp.nc.ui.MainActivity;
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
    @Bind(R.id.btn_lead_in)
    Button lead_in;
    @Bind(R.id.video_name)
    TextView video_name;
    @Bind(R.id.video_play_rl)
    RelativeLayout video_play_rl;

    private VideoManager videoManager;
    int record_state = C.RecordState.STOP;
    String fileUrl,fileName;

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
            public void stop() {
                record_state = C.RecordState.STOP;
                record_play_stop.setEnabled(false);
                lead_in.setEnabled(false);
                record_play_stop.setText("录制结束");
                lead_in.setText("限制导入");
                video_play_rl.setVisibility(View.VISIBLE);
                video_name.setText(fileName);
                // 显示播放器
                surfaceView.setVisibility(View.GONE);
                videoPlayer.setVisibility(View.VISIBLE);
                videoPlayer.setUp("file://"+fileUrl, JZVideoPlayerStandard.SCREEN_WINDOW_NORMAL, "");
            }
        });
    }

    @OnClick({R.id.btn_record_play_stop,R.id.btn_lead_in,R.id.btn_save})
    public void click(View v){
        switch (v.getId()){
            case R.id.btn_record_play_stop:
                if(record_state == C.RecordState.STOP){//开始录制
                    try {
                        fileUrl = FileUtil.getDiscFileDir(this)+"/"+System.currentTimeMillis() + ".mp4";
                        fileName = FileUtil.getFileName(fileUrl, ".mp4");
                        videoManager.startRecord(fileUrl);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else if(record_state == C.RecordState.START) { //停止录制
                    videoManager.stopRecord();
                }
                break;
            case R.id.btn_lead_in:
                new LFilePicker()
                        .withActivity(this)
                        .withRequestCode(C.REQUEST_CODE_VIDEO)
                        .withTitle("选择视频")
                        .withBackgroundColor("#009ad6")
                        .withFileFilter(new String[]{".mp4"})
                        .withMutilyMode(false)
                        .withMaxNum(1)
                        .start();
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == C.REQUEST_CODE_VIDEO) {
                List<String> list = data.getStringArrayListExtra(Constant.RESULT_INFO);
                if(list.size()>0){
                    fileUrl = list.get(0);
                    fileName = FileUtil.getFileName(fileUrl, ".mp4");
                    record_play_stop.setEnabled(false);
                    lead_in.setEnabled(false);
                    record_play_stop.setText("限制录制");
                    lead_in.setText("限制导入");
                    video_play_rl.setVisibility(View.VISIBLE);
                    video_name.setText(fileName);
                    // 显示播放器
                    surfaceView.setVisibility(View.GONE);
                    videoPlayer.setVisibility(View.VISIBLE);
                    videoPlayer.setUp("file://"+fileUrl, JZVideoPlayerStandard.SCREEN_WINDOW_NORMAL, "");
                }
            }
        }
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

    // 销毁、回收资源
    @Override
    protected void onDestroy() {
        super.onDestroy();
        videoManager.releaseAll();
    }
}
