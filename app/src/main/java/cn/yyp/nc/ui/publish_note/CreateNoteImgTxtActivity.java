package cn.yyp.nc.ui.publish_note;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.imnjh.imagepicker.SImagePicker;
import com.imnjh.imagepicker.activity.PhotoPickerActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import cn.yyp.nc.R;
import cn.yyp.nc.base.ParentWithNaviActivity;
import cn.yyp.nc.model.global.C;
import cn.yyp.nc.util.ImageLoadUtil;

/**
 * 创建图文笔记
 */
public class CreateNoteImgTxtActivity extends ParentWithNaviActivity {

    @Bind(R.id.et_note_title)
    EditText note_title;
    @Bind(R.id.et_note_content)
    EditText note_content;
    @Bind(R.id.et_note_content_tip)
    TextView note_content_tip;
    @Bind(R.id.et_note_image_one)
    ImageView note_image_one;
    @Bind(R.id.et_note_image_two)
    ImageView note_image_two;
    @Bind(R.id.et_note_image_three)
    ImageView note_image_three;

    private List<String> imageList = new ArrayList<>();


    @Override
    protected String title() {
        return "图文笔记";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_note_img_txt);
        initNaviView();

        note_content.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                note_content_tip.setText(s.length()+"/400字");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @OnClick({R.id.et_note_image_one,R.id.et_note_image_two,R.id.et_note_image_three,R.id.btn_save})
    public void click(View v){
        switch (v.getId()){
            case R.id.et_note_image_one:
            case R.id.et_note_image_two:
            case R.id.et_note_image_three:
                selectImage();
                break;
            case R.id.btn_save:

                break;
        }
    }

    /**
     * 选择图片
     */
    public void selectImage(){
        SImagePicker
                .from(this)
                .maxCount(3)
                .rowCount(3)
                .pickMode(SImagePicker.MODE_IMAGE)
                .showCamera(true)
                .pickText(R.string.finish)
                .forResult(C.REQUEST_CODE_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 图片选择结果回调
        if (resultCode == Activity.RESULT_OK && requestCode == C.REQUEST_CODE_IMAGE) {
            imageList = data.getStringArrayListExtra(PhotoPickerActivity.EXTRA_RESULT_SELECTION);
            if(imageList != null && imageList.size()>0) {
                showImages();
            }
        }
    }

    /**
     * 显示选中的图片
     */
    private void showImages(){
        setImageVisible(imageList.size());
        switch (imageList.size()){
            case 1:
                ImageLoadUtil.loadLocalImage(imageList.get(0), note_image_one);
                ImageLoadUtil.loadResImage(R.drawable.add, note_image_two);
                break;
            case 2:
                ImageLoadUtil.loadLocalImage(imageList.get(0), note_image_one);
                ImageLoadUtil.loadLocalImage(imageList.get(1), note_image_two);
                ImageLoadUtil.loadResImage(R.drawable.add, note_image_three);
                break;
            case 3:
                ImageLoadUtil.loadLocalImage(imageList.get(0), note_image_one);
                ImageLoadUtil.loadLocalImage(imageList.get(1), note_image_two);
                ImageLoadUtil.loadLocalImage(imageList.get(2), note_image_three);
                break;
        }
    }

    private void setImageVisible(int count){
        note_image_two.setVisibility(View.GONE);
        note_image_three.setVisibility(View.GONE);
        switch (count){
            case 1:
                note_image_two.setVisibility(View.VISIBLE);
                note_image_three.setVisibility(View.GONE);
                break;
            case 2:
            case 3:
                note_image_two.setVisibility(View.VISIBLE);
                note_image_three.setVisibility(View.VISIBLE);
                break;
        }
    }
}
