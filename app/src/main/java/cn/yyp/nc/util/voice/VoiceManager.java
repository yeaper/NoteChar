package cn.yyp.nc.util.voice;

import android.media.MediaPlayer;
import android.media.MediaRecorder;

import java.io.File;
import java.io.IOException;


/**
 * 音频管理
 */
public class VoiceManager {

    private static VoiceManager instance;

    // 播放器
    private MediaPlayer mMediaPlayer;
    // 录音机
    private MediaRecorder mMediaRecorder;

    private VoiceManager(){}

    public static VoiceManager getInstance(){
        if(instance == null){
            instance = new VoiceManager();
        }
        return instance;
    }

    public void initRecorder(){
        // 录音器
        mMediaRecorder = new MediaRecorder();
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        // 输出格式
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
        // 编码格式
        mMediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.DEFAULT);
    }

    public void initMediaPlayer(){
        // 播放器
        mMediaPlayer = new MediaPlayer();
    }

    // 开始录音
    public void startRecord(File audioFile) throws Exception {
        try {
            if(!audioFile.exists()) audioFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 设置录制音频的输出存放文件
        mMediaRecorder.setOutputFile(audioFile.getAbsolutePath());
        // 预备！
        mMediaRecorder.prepare();
        // 开始录音！
        mMediaRecorder.start();
        if(voiceRecorderListener!=null){
            voiceRecorderListener.start();
        }
    }

    /**
     * 停止录音
     */
    public void stopRecord() {
        if(mMediaRecorder!=null){
            // 停止录音
            mMediaRecorder.stop();
            mMediaRecorder.release();
            mMediaRecorder = null;
        }
        if(voiceRecorderListener!=null){
            voiceRecorderListener.stop();
        }
    }

    /**
     * 开始播放声音音频
     *
     * @throws Exception
     */
    public void startPlay(File audioFile) throws Exception {
        // 重置
        mMediaPlayer.reset();
        // 设置播放器的声音源
        mMediaPlayer.setDataSource(audioFile.getAbsolutePath());

        if (!mMediaPlayer.isPlaying()) {
            mMediaPlayer.prepare();
            mMediaPlayer.start();
            if(voicePlayerListener!=null){
                voicePlayerListener.start();
            }
        }

        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) { //播放完成
                mp.stop();
                if(voicePlayerListener!=null){
                    voicePlayerListener.complete();
                }
            }
        });
    }

    // 停止播放
    public void stopPlay() {
        if(mMediaPlayer!=null){
            // 如果播放器在播放声音，停止
            mMediaPlayer.stop();
        }
        if(voicePlayerListener!=null){
            voicePlayerListener.stop();
        }
    }

    public void releaseAll(){
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            // 释放资源
            mMediaPlayer.release();
        }
        if(mMediaRecorder!=null){
            mMediaRecorder.release();
        }
    }

    private IVoiceRecorderListener voiceRecorderListener;
    private IVoicePlayerListener voicePlayerListener;

    public void setVoiceRecorderListener(IVoiceRecorderListener voiceRecorderListener) {
        this.voiceRecorderListener = voiceRecorderListener;
    }

    public void setVoicePlayerListener(IVoicePlayerListener voicePlayerListener) {
        this.voicePlayerListener = voicePlayerListener;
    }

    public interface IVoiceRecorderListener{
        void start();
        void stop();
    }

    public interface IVoicePlayerListener{
        void start();
        void stop();
        void complete();
    }
}
