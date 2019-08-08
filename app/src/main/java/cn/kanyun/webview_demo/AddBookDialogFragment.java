package cn.kanyun.webview_demo;

import android.app.Activity;
import android.app.Dialog;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.blankj.utilcode.util.ToastUtils;
import com.orhanobut.logger.Logger;
import com.snappydb.DB;
import com.snappydb.DBFactory;
import com.snappydb.SnappydbException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * DialogFragment替代Dialog
 * https://blog.csdn.net/u011016373/article/details/79926933
 * DialogFragment的使用方式有两种：
 * a、继承DialogFragment，并实现onCreateDialog方法。（推荐此方式）
 * b、继承DialogFragment，并实现onCreateView方法。
 */
public class AddBookDialogFragment extends DialogFragment {

    @BindView(R.id.url_edit)
    EditText urlEdit;

    @BindView(R.id.title_edit)
    EditText titleEdit;

    private Unbinder unbinder;
    private static volatile AddBookDialogFragment instance;

    View dialogView;
    Context context;

    private AddBookDialogFragment() {
    }

    /**
     * 这是获取单例的方法,但是加了Bundle参数,主要是在Fragment传参时遇到了坑,总结来看
     * Fragment传参有四种方式
     * 1：构造函数,但是如果由于屏幕旋转或其他原因导致Activity销毁时,由于Fragment的重建需要调用无参的构造函数,所以这个时候带参数的构造方法就无效了
     * 2：数据封装到Intent 但是Intent不支持大数据量,因为Intent并不是传递引用的
     * 3：在Fragment中定义一个接口，用过这个接口进行值传递：显然十分麻烦
     * 4：就是这种通过静态方法来实现
     *
     * @param args
     * @return
     */
    public static AddBookDialogFragment getInstance(Bundle args) {
        if (instance == null) {
            synchronized (AddBookDialogFragment.class) {
                if (instance == null) {
                    instance = new AddBookDialogFragment();
                }
            }
        }
        instance.setArguments(args);
        return instance;
    }


    /**
     * 这个方法在onCreateDialog后执行
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        Logger.d("onCreateView方法执行");
////        去掉dialog标题
//        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
////        注意参数获取方法
//        savedInstanceState = getArguments();
//        dialogView = inflater.inflate(R.layout.book_dialog, container, false);
//        unbinder = ButterKnife.bind(this, dialogView);
//        urlEdit.setText(savedInstanceState.getString("url"));
//        titleEdit.setText(savedInstanceState.getString("title"));
//
//        return dialogView;
//    }
    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Logger.d("onCreateDialog方法执行");
//        注意参数的传递方式
        savedInstanceState = getArguments();
        LayoutInflater inflater = LayoutInflater.from(context);
        dialogView = inflater.inflate(R.layout.book_dialog, null);
        unbinder = ButterKnife.bind(this, dialogView);

        String url = savedInstanceState.getString("url");
        String title = savedInstanceState.getString("title");
        urlEdit.setText(url);
        titleEdit.setText(title);

        AlertDialog dialog = new AlertDialog.Builder(context)
                .setView(dialogView)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            DB db = MyApplication.getDb();
                            db.put(title, url);
                            dismiss();
                            ToastUtils.showLong("收藏成功");
                        } catch (SnappydbException e) {
                            ToastUtils.showLong("收藏失败");
                            e.printStackTrace();
                        }
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).create();
        return dialog;

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }
}
