package cn.yyp.nc.util;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

import cn.bmob.v3.listener.UploadFileListener;
import cn.yyp.nc.model.global.C;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.yyp.nc.model.i.IUploadFileListener;

/**
 * 文件工具
 */

public class FileUtil {

    /**
     * 获取app文件缓存路径
     *
     * @param context
     * @return
     */
    public static String getDiscFileDir(Context context) {
        String filePath = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            filePath = context.getExternalFilesDir(null).getAbsolutePath() + "/temp";
        } else {
            filePath = context.getFilesDir().getAbsolutePath() + "/temp";
        }
        File file = new File(filePath);
        if(!file.exists()){
            file.mkdirs();
        }
        return filePath;
    }

    /**
     * 获取图片文件缓存路径
     *
     * @param context
     * @return
     */
    public static String getImgFileDir(Context context) {
        String filePath;
        filePath = getDiscFileDir(context)+"/image";
        File file = new File(filePath);
        if(!file.exists()){
            file.mkdirs();
        }
        return filePath+"/";
    }

    /**
     * 获取音频文件缓存路径
     *
     * @param context
     * @return
     */
    public static String getVoiceFileDir(Context context) {
        String filePath;
        filePath = getDiscFileDir(context)+"/voice";
        File file = new File(filePath);
        if(!file.exists()){
            file.mkdirs();
        }
        return filePath+"/";
    }

    /**
     * 获取视频文件缓存路径
     *
     * @param context
     * @return
     */
    public static String getVideoFileDir(Context context) {
        String filePath;
        filePath = getDiscFileDir(context)+"/video";
        File file = new File(filePath);
        if(!file.exists()){
            file.mkdirs();
        }
        return filePath+"/";
    }

    /**
     * 获取文件父路径
     * @param path
     * @return eg 222/a/c/
     */
    public static String getFilePath(String path){
        int end = path.lastIndexOf("/");
        if(end!=-1){
            return path.substring(0, end)+"/";
        }else{
            return "";
        }
    }

    /**
     * 获取文件名
     * @param path
     * @return eg aaa.mp4
     */
    public static String getFileName(String path){
        int start = path.lastIndexOf("/");
        if(start!=-1){
            return path.substring(start+1);
        }else{
            return "";
        }
    }

    /**
     * 获取文件后缀
     * @param path
     * @return eg .mp4
     */
    public static String getFileSuffix(String path){
        int start = path.lastIndexOf(".");
        if(start!=-1){
            return path.substring(start);
        }else{
            return "";
        }
    }

    /**
     * 复制文件
     * @param oldPath
     * @param newPath
     * @return 复制成功 失败
     */
    public static boolean copyFile(String oldPath, String newPath) {
        try {
            int byteRead;
            File oldFile = new File(oldPath);
            if (oldFile.exists()) { //文件存在时
                InputStream inStream = new FileInputStream(oldPath); //读入原文件
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1024];
                while ( (byteRead = inStream.read(buffer)) != -1) {
                    fs.write(buffer, 0, byteRead);
                }
                inStream.close();
            }
        } catch (Exception e) {
            System.out.println("复制单个文件操作出错");
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 上传文件
     * @param path
     */
    public static void uploadFile(String path, final IUploadFileListener listener){
        final BmobFile bmobFile = new BmobFile(new File(path));
        bmobFile.uploadblock(new UploadFileListener() {

            @Override
            public void done(BmobException e) {
                if(e == null){
                    if(listener != null){
                        listener.uploadSuccess(bmobFile.getFilename(), bmobFile.getFileUrl());
                    }
                }else{
                    if(listener != null){
                        listener.uploadError(e.getMessage());
                    }
                }
            }

            @Override
            public void onProgress(Integer value) {
                if(listener != null){
                    listener.uploading(value);
                }
            }
        });
    }
}
