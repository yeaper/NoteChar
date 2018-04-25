package cn.yyp.nc.ui.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListPopupWindow;

import com.yanzhenjie.recyclerview.swipe.SwipeItemClickListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenu;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuBridge;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItem;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItemClickListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;
import cn.yyp.nc.R;
import cn.yyp.nc.adapter.NoteAdapter;
import cn.yyp.nc.base.ParentWithNaviFragment;
import cn.yyp.nc.greendao.Note;
import cn.yyp.nc.greendao.NoteManager;
import cn.yyp.nc.model.global.C;
import cn.yyp.nc.ui.publish_note.CreateNoteImgTxtActivity;
import cn.yyp.nc.ui.publish_note.CreateNoteVideoActivity;
import cn.yyp.nc.ui.publish_note.CreateNoteVoiceActivity;
import cn.yyp.nc.util.Util;

/**
 * 资源页面
 */
public class NoteFragment extends ParentWithNaviFragment {

    @Bind(R.id.note_root)
    FrameLayout note_root;
    @Bind(R.id.et_note_title)
    EditText et_note_title;
    @Bind(R.id.note_list)
    SwipeMenuRecyclerView recyclerView;
    NoteAdapter noteAdapter;
    List<Note> datas = new ArrayList<>();

    private NoteManager noteManager;

    private ListPopupWindow mPopup;

    @Override
    protected String title() {
        return "笔记";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_note, container, false);
        initNaviView();
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        noteManager = NoteManager.getInstance(getActivity());

        mPopup = new ListPopupWindow(getActivity());
        noteAdapter = new NoteAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setSwipeItemClickListener(new SwipeItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

            }
        });
        // 设置菜单
        recyclerView.setSwipeMenuCreator(mSwipeMenuCreator);
        // 菜单点击监听
        recyclerView.setSwipeMenuItemClickListener(mMenuItemClickListener);
        // 必须在setSwipeItemClickListener方法之后设置
        recyclerView.setAdapter(noteAdapter);

        et_note_title.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length()==0){
                    initData();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        initData();
    }

    // 创建菜单
    SwipeMenuCreator mSwipeMenuCreator = new SwipeMenuCreator() {
        @Override
        public void onCreateMenu(SwipeMenu leftMenu, SwipeMenu rightMenu, int viewType) {
            int width = getResources().getDimensionPixelSize(R.dimen.menu_width);

            SwipeMenuItem topItem = new SwipeMenuItem(getContext());
            topItem.setText("置顶");
            topItem.setTextSize(18);
            topItem.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            topItem.setWidth(width);
            topItem.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);

            SwipeMenuItem starItem = new SwipeMenuItem(getContext());
            starItem.setText("加星");
            starItem.setTextSize(18);
            starItem.setBackgroundColor(getResources().getColor(R.color.star_bg));
            starItem.setWidth(width);
            starItem.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);

            SwipeMenuItem deleteItem = new SwipeMenuItem(getContext());
            deleteItem.setText("删除");
            deleteItem.setTextSize(18);
            deleteItem.setBackgroundColor(getResources().getColor(R.color.dialog_color_title));
            deleteItem.setWidth(width);
            deleteItem.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);

            rightMenu.addMenuItem(topItem); // 在Item右侧添加一个菜单
            rightMenu.addMenuItem(starItem);
            rightMenu.addMenuItem(deleteItem);
        }
    };

    SwipeMenuItemClickListener mMenuItemClickListener = new SwipeMenuItemClickListener() {
        @Override
        public void onItemClick(SwipeMenuBridge menuBridge) {
            // 任何操作必须先关闭菜单，否则可能出现Item菜单打开状态错乱。
            menuBridge.closeMenu();

            switch (menuBridge.getPosition()){
                case 0: //置顶

                    break;
                case 1: //加星

                    break;
                case 2: //删除

                    break;
            }
        }
    };

    @OnClick({R.id.btn_search, R.id.fab_make_note})
    public void click(View v){
        switch (v.getId()){
            case R.id.btn_search:
                if(et_note_title.getText().toString().trim().length() > 0){
                    Util.HideKeyboard(recyclerView);
                    search(et_note_title.getText().toString().trim());
                }else{
                    showToast("请输入搜索内容");
                }
                break;
            case R.id.fab_make_note:
                showNoteType();
                break;
        }
    }

    @OnTouch({R.id.note_root, R.id.note_list})
    public boolean touch(View v){
        switch (v.getId()){
            case R.id.note_root:
            case R.id.note_list:
                Util.HideKeyboard(recyclerView);
                break;
        }
        return false;
    }

    /**
     * 选择笔记类型
     */
    public void showNoteType(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), 3);
        builder.setTitle("笔记类型");
        // 设置列表显示，注意设置了列表显示就不要设置builder.setMessage()了，否则列表不起作用。
        builder.setItems(C.note_type, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                switch (which){
                    case 0:
                        startActivity(CreateNoteImgTxtActivity.class, null);
                        break;
                    case 1:
                        startActivity(CreateNoteVoiceActivity.class, null);
                        break;
                    case 2:
                        startActivity(CreateNoteVideoActivity.class, null);
                        break;
                }
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

    /**
     * 初始化数据
     */
    private void initData(){
        try{
            noteAdapter.clear();
            datas = noteManager.queryAllNote();
            noteAdapter.setDatas(datas);
            if(datas.size()<=0){
                showToast("暂无笔记");
            }
        }catch (Exception e){
            e.printStackTrace();
            showToast("暂无笔记");
        }
    }

    /**
     * 搜索笔记
     */
    private void search(String str){
        try{
            noteAdapter.clear();
            datas = noteManager.queryNote(str);
            noteAdapter.setDatas(datas);
            if(datas.size()<=0){
                showToast("暂无笔记");
            }
        }catch (Exception e){
            e.printStackTrace();
            showToast("暂无笔记");
        }
    }
}
