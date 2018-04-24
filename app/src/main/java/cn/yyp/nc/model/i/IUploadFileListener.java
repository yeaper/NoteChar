package cn.yyp.nc.model.i;

/**
 * 上传文件监听
 */

public interface IUploadFileListener {
    void uploading(int progress);
    void uploadSuccess(String fileName, String fileUrl);
    void uploadError(String errorMsg);
}
