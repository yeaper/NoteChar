package cn.yyp.nc.model.i;

/**
 * 重置密码回调接口
 */

public interface IResetPwdCallback {
    void setSuccess();
    void setError(String msg);
}
