package cn.yyp.nc.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.view.ViewGroup;

import com.yanzhenjie.recyclerview.swipe.SwipeItemClickListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenu;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuBridge;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItem;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItemClickListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import cn.yyp.nc.R;
import cn.yyp.nc.adapter.NoteAdapter;
import cn.yyp.nc.base.ParentWithNaviActivity;
import cn.yyp.nc.event.UpdateNoteListEvent;
import cn.yyp.nc.greendao.Note;
import cn.yyp.nc.greendao.NoteManager;
import cn.yyp.nc.model.global.C;
import cn.yyp.nc.ui.show_note.ShowNoteImgTxtActivity;
import cn.yyp.nc.ui.show_note.ShowNoteVideoActivity;
import cn.yyp.nc.ui.show_note.ShowNoteVoiceActivity;

public class MyStarNoteActivity extends ParentWithNaviActivity {

    @Bind(R.id.star_note_list)
    SwipeMenuRecyclerView recyclerView;
    NoteAdapter noteAdapter;
    List<Note> datas = new ArrayList<>();

    private NoteManager noteManager;

    @Override
    protected String title() {
        return "加星笔记";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_star_note);
        initNaviView();
        noteManager = NoteManager.getInstance(this);

        noteAdapter = new NoteAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setSwipeItemClickListener(new SwipeItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Bundle bundle = new Bundle(); //携带笔记对象
                bundle.putSerializable("note", noteAdapter.getDatas().get(position));
                switch (noteAdapter.getDatas().get(position).getNoteType()){
                    case C.NoteType.Img_Txt:
                        Intent goImgTxt = new Intent(MyStarNoteActivity.this, ShowNoteImgTxtActivity.class);
                        goImgTxt.putExtras(bundle);
                        startActivity(goImgTxt);
                        break;
                    case C.NoteType.Voice:
                        Intent goVoice = new Intent(MyStarNoteActivity.this, ShowNoteVoiceActivity.class);
                        goVoice.putExtras(bundle);
                        startActivity(goVoice);
                        break;
                    case C.NoteType.Video:
                        Intent goVideo = new Intent(MyStarNoteActivity.this, ShowNoteVideoActivity.class);
                        goVideo.putExtras(bundle);
                        startActivity(goVideo);
                        break;

                }
            }
        });
        // 设置菜单
        recyclerView.setSwipeMenuCreator(mSwipeMenuCreator);
        // 菜单点击监听
        recyclerView.setSwipeMenuItemClickListener(mMenuItemClickListener);
        // 必须在setSwipeItemClickListener方法之后设置
        recyclerView.setAdapter(noteAdapter);

        initData();
    }

    // 创建菜单
    SwipeMenuCreator mSwipeMenuCreator = new SwipeMenuCreator() {
        @Override
        public void onCreateMenu(SwipeMenu leftMenu, SwipeMenu rightMenu, int viewType) {
            int width = getResources().getDimensionPixelSize(R.dimen.menu_width);

            SwipeMenuItem deleteItem = new SwipeMenuItem(MyStarNoteActivity.this);
            deleteItem.setText("删除");
            deleteItem.setTextSize(18);
            deleteItem.setBackgroundColor(getResources().getColor(R.color.dialog_color_title));
            deleteItem.setWidth(width);
            deleteItem.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);

             // 在Item右侧添加一个菜单
            rightMenu.addMenuItem(deleteItem);
        }
    };

    SwipeMenuItemClickListener mMenuItemClickListener = new SwipeMenuItemClickListener() {
        @Override
        public void onItemClick(SwipeMenuBridge menuBridge) {
            // 任何操作必须先关闭菜单，否则可能出现Item菜单打开状态错乱。
            menuBridge.closeMenu();

            int position = menuBridge.getAdapterPosition();
            Note note = noteAdapter.getDatas().get(position);//一定要获取排序后的列表
            switch (menuBridge.getPosition()){
                case 0: //删除
                    try{
                        noteManager.deleteNote(note.getId());
                        datas.remove(note);
                        noteAdapter.setDatas(datas);
                        EventBus.getDefault().post(new UpdateNoteListEvent());
                    }catch (Exception e){
                        showToast("删除失败");
                    }
                    break;
            }
        }
    };

    /**
     * 初始化数据
     */
    private void initData(){
        try{
            noteAdapter.clear();
            datas = noteManager.queryStarNote();
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
