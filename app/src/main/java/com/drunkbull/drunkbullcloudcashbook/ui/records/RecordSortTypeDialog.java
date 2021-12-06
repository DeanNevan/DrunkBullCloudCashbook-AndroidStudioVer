package com.drunkbull.drunkbullcloudcashbook.ui.records;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.drunkbull.drunkbullcloudcashbook.R;
import com.drunkbull.drunkbullcloudcashbook.utils.data.DateUtil;
import com.drunkbull.drunkbullcloudcashbook.utils.data.TimeUtil;

import java.util.Date;

public class RecordSortTypeDialog extends Dialog implements View.OnClickListener {
    //声明xml文件里的组件
    private TextView textViewTitle;
    private Button buttonCancel, buttonConfirm;

    private RadioGroup radioGroupAscend;
    private RadioGroup radioGroupSortType;

    //声明两个点击事件，等会一定要为取消和确定这两个按钮也点击事件
    private IOnCancelListener cancelListener;
    private IOnConfirmListener confirmListener;

    public String selectedAscendString = "";
    public int selectedAscendButtonID = 0;
    public String selectedSortTypeString = "";
    public int selectedSortTypeButtonID = 0;

    public void setCancel(IOnCancelListener cancelListener) {
        this.cancelListener=cancelListener;
    }
    public void setConfirm(IOnConfirmListener confirmListener){
        this.confirmListener=confirmListener;
    }

    //CustomDialog类的构造方法
    public RecordSortTypeDialog(@NonNull Context context, int selectedAscendButtonID, int selectedSortTypeButtonID) {
        super(context);
        this.selectedAscendButtonID = selectedAscendButtonID;
        this.selectedSortTypeButtonID = selectedSortTypeButtonID;
    }
    public RecordSortTypeDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    //在app上以对象的形式把xml里面的东西呈现出来的方法！
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //为了锁定app界面的东西是来自哪个xml文件
        setContentView(R.layout.dialog_records_sort_type);

        //设置弹窗的宽度
        WindowManager m = getWindow().getWindowManager();
        Display d = m.getDefaultDisplay();
        WindowManager.LayoutParams p =getWindow().getAttributes();
        Point size = new Point();
        d.getSize(size);
        p.width = (int)(size.x * 0.8);//是dialog的宽度为app界面的80%
        getWindow().setAttributes(p);

        //找到组件
        textViewTitle = findViewById(R.id.text_view_title);
        buttonCancel = findViewById(R.id.button_cancel);
        buttonConfirm = findViewById(R.id.button_confirm);

        radioGroupAscend = (RadioGroup) findViewById(R.id.radio_group_ascend);
        radioGroupSortType = (RadioGroup) findViewById(R.id.radio_group_sort_type);

        if (selectedAscendButtonID != 0){
            radioGroupAscend.check(selectedAscendButtonID);
        }

        if (selectedSortTypeButtonID != 0){
            radioGroupSortType.check(selectedSortTypeButtonID);
        }

        radioGroupAscend.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton radioButton = (RadioButton) findViewById(i);//获取被选择的单选按钮
                selectedAscendString = radioButton.getText().toString();
                selectedAscendButtonID = i;
            }
        });

        radioGroupSortType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton radioButton = (RadioButton) findViewById(i);//获取被选择的单选按钮
                selectedSortTypeString = radioButton.getText().toString();
                selectedSortTypeButtonID = i;
            }
        });

        //为两个按钮添加点击事件
        buttonConfirm.setOnClickListener(this);
        buttonCancel.setOnClickListener(this);
    }

    //重写onClick方法
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.button_cancel:
                if(cancelListener!=null){
                    cancelListener.onCancel(this);
                }
                dismiss();
                break;
            case R.id.button_confirm:
                if(confirmListener!=null){
                    confirmListener.onConfirm(this);
                    dismiss();//按钮按之后会消失
                }
                break;
        }
    }

    //写两个接口，当要创建一个CustomDialog对象的时候，必须要实现这两个接口
    //也就是说，当要弹出一个自定义dialog的时候，取消和确定这两个按钮的点击事件，一定要重写！
    public interface IOnCancelListener{
        void onCancel(RecordSortTypeDialog dialog);
    }
    public interface IOnConfirmListener{
        void onConfirm(RecordSortTypeDialog dialog);
    }

}
