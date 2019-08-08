package cn.kanyun.webview_demo;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.orhanobut.logger.Logger;
import com.snappydb.DB;
import com.snappydb.DBFactory;
import com.snappydb.KeyIterator;
import com.snappydb.SnappydbException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * BaseRecyclerViewAdapterHelper使用
 * https://www.jianshu.com/p/b343fcff51b0/
 */
public class BookAdapter extends BaseQuickAdapter<Book, BaseViewHolder> {

    Context context;

    public BookAdapter() {
        super(R.layout.book_layout, getSampleData());
    }

    public static List<Book> getSampleData() {
        List<Book> list = new ArrayList<>();
        DB db = MyApplication.getDb();
        if (db != null) {
            try {
//                Key的迭代器
                KeyIterator keyIterator = db.allKeysIterator();
//                批量获取迭代器
                Iterable<String[]> iterables = keyIterator.byBatch(1000);
//                跟上面的不一样
                Iterator<String[]> it = iterables.iterator();
                while (it.hasNext()) {
                    String[] keys = it.next();
                    for (int i = 0; i < keys.length; i++) {
                        Book book = new Book(keys[i], db.get(keys[i]));
                        list.add(book);
                    }
                }


            } catch (SnappydbException e) {
                e.printStackTrace();
            }
        }

        return list;
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, Book item) {

        helper.setText(R.id.book_title, item.getTitle());

    }


}



