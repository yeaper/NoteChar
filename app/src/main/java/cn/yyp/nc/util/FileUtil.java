package cn.yyp.nc.util;

import android.content.Context;
import android.os.Environment;

import java.io.File;

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
     * 获取文件名
     * @param path
     * @return
     */
    public static String getFileName(String path, String suffix){
        int start = path.lastIndexOf("/");
        int end = path.lastIndexOf(".");
        if(start!=-1 && end!=-1){
            return path.substring(start+1,end)+suffix;
        }else{
            return "";
        }
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
