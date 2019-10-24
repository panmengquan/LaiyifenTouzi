package com.laiyifen.capital;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;


public class SetPopView extends PopupWindow implements View.OnClickListener {

    private Context context;
    private View.OnClickListener listener;



    public SetPopView(Context context, View.OnClickListener listener){
        this.context = context;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.popup_set_view, null);
        this.setContentView(view);
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        this.setWidth(RelativeLayout.LayoutParams.WRAP_CONTENT);
        this.setHeight(RelativeLayout.LayoutParams.WRAP_CONTENT);
        this.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00000000")));


//        int h = context.getWindowManager().getDefaultDisplay().getHeight();
//        int w = context.getWindowManager().getDefaultDisplay().getWidth();
//        // 设置SelectPicPopupWindow的View
//        this.setContentView(conentView);
//        // 设置SelectPicPopupWindow弹出窗体的宽
//        this.setWidth(2 * w / 5);
//        // 设置SelectPicPopupWindow弹出窗体的高
//        this.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
//        // 设置SelectPicPopupWindow弹出窗体可点击
//        this.setFocusable(true);
//        this.setOutsideTouchable(true);
//        // 刷新状态
//        this.update();
//        // 实例化一个ColorDrawable颜色为半透明
//        ColorDrawable dw = new ColorDrawable(0000000000);
//        // 点back键和其他地方使其消失,设置了这个才能触发OnDismisslistener ，设置其他控件变化等操作
//        this.setBackgroundDrawable(dw);

        initView(view);
        this.listener = listener;
    }

    private void initView(View v) {
        LinearLayout llRelash = v.findViewById(R.id.ll_reflash);
        LinearLayout llLogout = v.findViewById(R.id.ll_logout);
        LinearLayout llShare = v.findViewById(R.id.ll_share);
        llRelash.setOnClickListener(this);
        llShare.setOnClickListener(this);
        llLogout.setOnClickListener(this);
    }

    public void show(View v,int y){
        this.showAsDropDown(v,0,y);
    }

    @Override
    public void onClick(View view) {
        listener.onClick(view);

        this.dismiss();
    }

    public interface onClickListener {
        void onClick(View view);
    }
}
