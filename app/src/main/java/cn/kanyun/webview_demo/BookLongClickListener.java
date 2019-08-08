package cn.kanyun.webview_demo;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;

import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.snappydb.DB;
import com.snappydb.SnappydbException;

/**
 * 书签长按删除
 */
public class BookLongClickListener implements BaseQuickAdapter.OnItemLongClickListener {

    private Context context;

    public BookLongClickListener(Context context) {
        this.context = context;
    }

    @Override
    public boolean onItemLongClick(BaseQuickAdapter adapter, View view, int position) {
        Book book = (Book) adapter.getItem(position);
        String title = book.getTitle();
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("警告！")
                .setMessage("是否删除书签：" + title)
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ToastUtils.showShort("已取消");
                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DB db = MyApplication.getDb();
                        try {
//                          从KV数据库中删除
                            db.del(title);
//                          从RecycleView中删除(这是利用BaseQuickAdapter的功能)
                            adapter.remove(position);
                            ToastUtils.showShort("删除成功");
                        } catch (SnappydbException e) {
                            ToastUtils.showShort("删除失败");
                            e.printStackTrace();
                        }

                    }
                });
        builder.show();
        return false;
    }
}
