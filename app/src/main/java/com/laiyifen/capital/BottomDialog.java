package com.laiyifen.capital;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import butterknife.ButterKnife;


public class BottomDialog extends Dialog implements View.OnClickListener {
    private Context context;
    private int layoutRes;
    private View view;
    private int[] clickIds;   //需要设置点击事件的ID.需要其他ID,在dialog实例化后在dialog上fbc.
    private TextView phoneTitle;
    private boolean isPhone = false;

    public BottomDialog(Context context, int layoutRes, int[] clickIds) {
        super(context, R.style.dialogFullscreen);    //设置主题
        this.context = context;
        this.layoutRes = layoutRes;
        this.clickIds = clickIds;
    }

    public BottomDialog(Context context, View view, int[] clickIds) {
        super(context, R.style.dialog_full);    //设置主题
        this.context = context;
        this.view = view;
        this.clickIds = clickIds;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        Window window = getWindow();
        //底部弹出的Dialog
        window.setGravity(Gravity.BOTTOM);
        //底部弹出的动画
        window.setWindowAnimations(R.style.main_menu_animstyle);
        if(view != null) {
            setContentView(view);
        }else{
            setContentView(layoutRes);
        }


        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        //点击Dialog外部消失
        setCanceledOnTouchOutside(false);
        //禁用返回键
        setCancelable(true);
        //设置点击事件
        if (clickIds != null) {
            for (int id : clickIds) {
                findViewById(id).setOnClickListener(this);
            }
        }
    }

    public View getView() {
        if (view == null) {
            return getLayoutInflater().inflate(layoutRes, null);
        }
        return view;
    }

    private OnBottomItemClickListener listener;


    public interface OnBottomItemClickListener {
        void onBottomItemClick(BottomDialog dialog, View view);
    }

    public void setOnBottomItemClickListener(OnBottomItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onClick(View v) {
        listener.onBottomItemClick(this, v);
    }
//    public void setPhoneFirstName(boolean isPhone,String name){
//      phoneTitle.setText(name);
//      this.isPhone = isPhone;
//    }
}
