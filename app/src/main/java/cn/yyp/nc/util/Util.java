package cn.yyp.nc.util;

import android.content.Context;
import android.os.Environment;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.io.File;

import cn.yyp.nc.model.global.C;

/**
 * 通用工具
 */
public class Util {

    public static boolean checkSdCard() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }

    /**
     * 获取SD卡路径
     * @return
     */
    public static String getSDPath(){
        File sdDir = null;
        if(checkSdCard()) {
            sdDir = Environment.getExternalStorageDirectory();//获取根目录
        }
        if(sdDir != null) {
            return sdDir.toString();
        }
        return "";
    }

    /**
     * 显示键盘
     * @param context
     * @param view
     */
    public static void showInputMethod(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null && !imm.isActive()) {
            imm.showSoftInput(view, 0);
        }
    }

    /**
     * 隐藏虚拟键盘
     */
    public static void HideKeyboard(View v){
        InputMethodManager imm = ( InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null && imm.isActive()) {
            imm.hideSoftInputFromWindow(v.getWindowToken(),0);
        }
    }

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
}
