package cn.yyp.nc.model.global;


/**
 *
 */
public class C {

    public static final String[] note_type = new String[]{"图文", "音频", "视频"};

    public static class NoteType{
        public static final int Img_Txt = 1;
        public static final int Voice = 2;
        public static final int Video = 3;
    }

    public static final int REQUEST_CODE_IMAGE = 0x01;
    public static final int REQUEST_CODE_AVATAR = 0x02;
    public static final int REQUEST_CODE_VOICE = 0x03;
    public static final int REQUEST_CODE_VIDEO = 0x04;

    public static class RecordState{
        public static final int START = 1;
        public static final int STOP = 2;
    }

    public static class PlayState{
        public static final int START = 1;
        public static final int STOP = 2;
    }
}
