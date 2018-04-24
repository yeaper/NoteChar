package cn.yyp.nc.util;

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
     * 获取笔记类型
     * @param which
     * @return
     */
    public static int getNoteType(int which){
        switch (which){
            case 0:
                return C.NoteType.Img_Txt;
            case 1:
                return C.NoteType.Voice;
            case 2:
                return C.NoteType.Video;
            default:
                return C.NoteType.Img_Txt;
        }
    }

    /**
     * 获取笔记类型名
     * @param which
     * @return
     */
    public static String getNoteTypeName(int which){
        switch (which){
            case 1:
                return C.note_type[0];
            case 2:
                return C.note_type[1];
            case 3:
                return C.note_type[2];
            default:
                return C.note_type[0];
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
