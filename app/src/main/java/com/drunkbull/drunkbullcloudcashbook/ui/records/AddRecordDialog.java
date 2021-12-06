package com.drunkbull.drunkbullcloudcashbook.ui.records;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.drunkbull.drunkbullcloudcashbook.R;
import com.drunkbull.drunkbullcloudcashbook.pojo.CBRecord;
import com.drunkbull.drunkbullcloudcashbook.utils.data.DateUtil;
import com.drunkbull.drunkbullcloudcashbook.utils.data.TimeUtil;

import java.sql.Time;
import java.util.Date;

public class AddRecordDialog extends Dialog implements View.OnClickListener {
    //声明xml文件里的组件
    private TextView textViewTitle, textViewMessage;
    private Button buttonCancel, buttonConfirm;

    private EditText editTextTitle;
    private EditText editTextComment;
    private EditText editTextMoney;
    private EditText editTextDate;

    //声明两个点击事件，等会一定要为取消和确定这两个按钮也点击事件
    private IOnCancelListener cancelListener;
    private IOnConfirmListener confirmListener;

    private boolean viewMode = false;
    private CBRecord viewRecord;

    public void setCancel(IOnCancelListener cancelListener) {
        this.cancelListener=cancelListener;
    }
    public void setConfirm(IOnConfirmListener confirmListener){
        this.confirmListener=confirmListener;
    }

    //CustomDialog类的构造方法
    public AddRecordDialog(@NonNull Context context) {
        super(context);
    }
    public AddRecordDialog(@NonNull Context context, boolean viewMode, CBRecord viewRecord) {
        super(context);
        this.viewMode = viewMode;
        this.viewRecord = viewRecord;
    }
    public AddRecordDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    //在app上以对象的形式把xml里面的东西呈现出来的方法！
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //为了锁定app界面的东西是来自哪个xml文件
        setContentView(R.layout.dialog_add_record);

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
        textViewMessage = findViewById(R.id.text_view_message);
        buttonCancel = findViewById(R.id.button_cancel);
        buttonConfirm = findViewById(R.id.button_confirm);

        editTextTitle = findViewById(R.id.edit_text_add_record_title);
        editTextComment = findViewById(R.id.edit_text_add_record_comment);
        editTextMoney = findViewById(R.id.edit_text_add_record_money);
        editTextDate = findViewById(R.id.edit_text_add_record_date);

        if (viewMode){
            textViewTitle.setText(getContext().getText(R.string.text_view_detail) + "(" + viewRecord.id + ")");

            editTextTitle.setText(viewRecord.title);
            editTextComment.setText(viewRecord.comment);
            editTextMoney.setText(String.valueOf(viewRecord.money));

            if (viewRecord.money < 0){
                editTextMoney.setTextColor(getContext().getResources().getColor(R.color.green));
            }
            else if (viewRecord.money > 0){
                editTextMoney.setTextColor(getContext().getResources().getColor(R.color.red));
            }

            long timestamp = viewRecord.dateTime;
            String timeString = TimeUtil.stampToDate(timestamp);
            editTextDate.setText(timeString);

            editTextTitle.setClickable(false);
            editTextComment.setClickable(false);
            editTextMoney.setClickable(false);
            editTextDate.setClickable(false);
            buttonCancel.setVisibility(View.INVISIBLE);
        }

        textViewMessage.setText("");

        Date date = DateUtil.getCurrentDate();
        long timestamp = DateUtil.getMillisecondTimestamp(date);
        String string = TimeUtil.stampToDate(timestamp);
        editTextDate.setText(string);

        //为两个按钮添加点击事件
        buttonConfirm.setOnClickListener(this);
        buttonCancel.setOnClickListener(this);
    }

    //重写onClick方法
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.button_cancel:
                textViewMessage.setText("");

                if(cancelListener!=null){
                    cancelListener.onCancel(this);
                }
                dismiss();
                break;
            case R.id.button_confirm:
                if(confirmListener!=null){
                    textViewMessage.setText("");

                    if (getTitle().equals("")){
                        textViewMessage.setText(R.string.notification_title_no_null);
                    }
                    else if(getDateString().equals("")){
                        textViewMessage.setText(R.string.notification_date_no_null);
                    }
                    else{
                        String dateString = getDateString();
                        String temp = TimeUtil.formatStringToValidDate(dateString);

                        if (dateString.equals(temp)){
                            confirmListener.onConfirm(this);
                            dismiss();//按钮按之后会消失
                        }
                        else{
                            editTextDate.setText(temp);
                            textViewMessage.setText(R.string.notification_invalid_date_and_updated);
                        }
                    }
                }
                break;
        }
    }

    public String getTitle(){
        return editTextTitle.getText().toString();
    }

    public String getComment(){
        return editTextComment.getText().toString();
    }

    public double getMoney(){
        if (editTextMoney.getText().toString().equals("")){
            return 0;
        }
        return Double.parseDouble(editTextMoney.getText().toString());
    }

    public String getDateString(){
        return editTextDate.getText().toString();
    }

    public long getDateTimestamp(){
        String dateString = getDateString();
        String temp = TimeUtil.formatStringToValidDate(dateString);
        if (temp.equals("")){
            return -1;
        }
        else{
            return TimeUtil.dateToSecondStamp(temp);
        }
    }

    //写两个接口，当要创建一个CustomDialog对象的时候，必须要实现这两个接口
    //也就是说，当要弹出一个自定义dialog的时候，取消和确定这两个按钮的点击事件，一定要重写！
    public interface IOnCancelListener{
        void onCancel(AddRecordDialog dialog);
    }
    public interface IOnConfirmListener{
        void onConfirm(AddRecordDialog dialog);
    }

}
