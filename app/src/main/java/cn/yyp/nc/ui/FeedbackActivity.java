package cn.yyp.nc.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.yyp.nc.R;
import cn.yyp.nc.base.ParentWithNaviActivity;
import cn.yyp.nc.model.Feedback;
import cn.yyp.nc.model.UserModel;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

/**
 * 意见反馈
 */
public class FeedbackActivity extends ParentWithNaviActivity {

    @Bind(R.id.feedback_content)
    EditText content;

    @Override
    protected String title() {
        return "意见反馈";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        initNaviView();
    }

    @OnClick(R.id.btn_feedback)
    public void click(View v){
        if(content.getText().toString().trim().length()>0){
            // 提交意见
            Feedback feedback = new Feedback();
            feedback.setAuthorId(UserModel.getInstance().getCurrentUser().getObjectId());
            feedback.setContent(content.getText().toString().trim());
            feedback.save(new SaveListener<String>() {
                @Override
                public void done(String s, BmobException e) {
                    if(e == null){
                        showToast("反馈成功");
                        finish();
                    }else{
                        showToast("反馈失败："+e.getMessage());
                    }
                }
            });
        }else{
            showToast("请输入内容");
        }

    }
}
