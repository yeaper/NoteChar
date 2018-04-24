package cn.yyp.nc.ui.fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListPopupWindow;

import com.leon.lfilepickerlibrary.LFilePicker;
import com.leon.lfilepickerlibrary.utils.Constant;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;
import cn.yyp.nc.R;
import cn.yyp.nc.adapter.SearchResFileAdapter;
import cn.yyp.nc.base.ParentWithNaviFragment;
import cn.yyp.nc.model.ResFile;
import cn.yyp.nc.model.global.C;
import cn.yyp.nc.util.FileUtil;
import cn.yyp.nc.util.Util;

import static android.app.Activity.RESULT_OK;

/**
 * 资源页面
 */
public class NoteFragment extends ParentWithNaviFragment {

    @Bind(R.id.res_root)
    FrameLayout res_root;
    @Bind(R.id.et_file_name)
    EditText et_file_name;
    @Bind(R.id.fab_upload_res)
    FloatingActionButton upload_res;
    @Bind(R.id.sw_refresh)
    SwipeRefreshLayout refreshLayout;
    @Bind(R.id.rc_view)
    RecyclerView recyclerView;
    SearchResFileAdapter adapter;
    List<ResFile> datas = new ArrayList<>();

    int res_type = C.NoteType.Img_Txt;
    private ListPopupWindow mPopup;

    ProgressDialog progressDialog;


    @Override
    protected String title() {
        return "笔记";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_res, container, false);
        initNaviView();
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mPopup = new ListPopupWindow(getActivity());
        adapter = new SearchResFileAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshLayout.setRefreshing(false);
            }
        });
    }

    @OnClick({R.id.btn_search, R.id.fab_upload_res})
    public void click(View v){
        switch (v.getId()){
            case R.id.btn_search:
                if(et_file_name.getText().toString().trim().length() > 0){
                    Util.HideKeyboard(recyclerView);
                    refreshLayout.setRefreshing(true);
                }else{
                    showToast("请输入搜索内容");
                }
                break;
            case R.id.fab_upload_res:
                showUploadType();
                break;
        }
    }

    @OnTouch({R.id.res_root, R.id.rc_view})
    public boolean touch(View v){
        switch (v.getId()){
            case R.id.res_root:
            case R.id.rc_view:
                Util.HideKeyboard(recyclerView);
                break;
        }
        return false;
    }

    private static final int SELECT_FILE_CODE = 0x01;

    /**
     * 选择上传资源类型
     */
    public void showUploadType(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), 3);
        builder.setTitle("笔记类型");
        // 设置列表显示，注意设置了列表显示就不要设置builder.setMessage()了，否则列表不起作用。
        builder.setItems(C.note_type, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                res_type = FileUtil.getNoteType(which);
                // 选择文件
                new LFilePicker().withSupportFragment(NoteFragment.this)
                        .withRequestCode(SELECT_FILE_CODE)
                        .withIconStyle(Constant.ICON_STYLE_BLUE)
                        .withTitle("选择文件")
                        .withMaxNum(1)
                        .start();

            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_FILE_CODE) {
                List<String> list = data.getStringArrayListExtra(Constant.RESULT_INFO);
                if(list.size() > 0){
                    showPD();
                }
            }
        }
    }

    private void showPD() {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);//转盘
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("正在上传，请稍后……");
        progressDialog.show();
    }
}
