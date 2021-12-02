package com.drunkbull.drunkbullcloudcashbook.ui.accounts;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.drunkbull.drunkbullcloudcashbook.R;

public class AddAccountDialog extends Dialog implements View.OnClickListener {
    //声明xml文件里的组件
    private TextView textViewTitle, textViewMessage;
    private Button buttonCancel, buttonConfirm;

    private EditText editTextUsername;
    private EditText editTextNickname;
    private EditText editTextPassword;
    private EditText editTextPasswordConfirm;

    //声明两个点击事件，等会一定要为取消和确定这两个按钮也点击事件
    private IOnCancelListener cancelListener;
    private IOnConfirmListener confirmListener;

    public void setCancel(IOnCancelListener cancelListener) {
        this.cancelListener=cancelListener;
    }
    public void setConfirm(IOnConfirmListener confirmListener){
        this.confirmListener=confirmListener;
    }

    //CustomDialog类的构造方法
    public AddAccountDialog(@NonNull Context context) {
        super(context);
    }
    public AddAccountDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    //在app上以对象的形式把xml里面的东西呈现出来的方法！
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //为了锁定app界面的东西是来自哪个xml文件
        setContentView(R.layout.dialog_add_account);

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

        editTextUsername = findViewById(R.id.edit_text_add_account_username);
        editTextNickname = findViewById(R.id.edit_text_add_account_nickname);
        editTextPassword = findViewById(R.id.edit_text_add_account_password);
        editTextPasswordConfirm = findViewById(R.id.edit_text_add_account_password_confirm);

        textViewMessage.setText("");

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

                    if (getUsername().equals("")){
                        textViewMessage.setText(R.string.notification_username_no_null);
                    }
                    else if(getPassword().equals("")){
                        textViewMessage.setText(R.string.notification_password_no_null);
                    }
                    else if(!getPassword().equals(getPasswordConfirm())){
                        textViewMessage.setText(R.string.notification_password_confirm_incorrect);
                    }
                    else{
                        confirmListener.onConfirm(this);
                        dismiss();//按钮按之后会消失
                    }
                }
                break;
        }
    }

    public String getUsername(){
        return editTextUsername.getText().toString();
    }

    public String getNickname(){
        return editTextNickname.getText().toString();
    }

    public String getPassword(){
        return editTextPassword.getText().toString();
    }

    public String getPasswordConfirm(){
        return editTextPasswordConfirm.getText().toString();
    }

    //写两个接口，当要创建一个CustomDialog对象的时候，必须要实现这两个接口
    //也就是说，当要弹出一个自定义dialog的时候，取消和确定这两个按钮的点击事件，一定要重写！
    public interface IOnCancelListener{
        void onCancel(AddAccountDialog dialog);
    }
    public interface IOnConfirmListener{
        void onConfirm(AddAccountDialog dialog);
    }

}
