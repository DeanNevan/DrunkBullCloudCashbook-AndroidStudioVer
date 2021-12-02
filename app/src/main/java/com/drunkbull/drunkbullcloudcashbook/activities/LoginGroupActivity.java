package com.drunkbull.drunkbullcloudcashbook.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.drunkbull.drunkbullcloudcashbook.MainActivity;
import com.drunkbull.drunkbullcloudcashbook.R;

public class LoginGroupActivity extends AppCompatActivity {

    Button buttonConfirm;
    Button buttonCancel;

    EditText editTextGroupName;
    EditText editTextAdminName;
    EditText editTextAdminPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_group);

        buttonConfirm = findViewById(R.id.button_confirm_login_group);
        buttonCancel = findViewById(R.id.button_cancel_login_group);

        editTextGroupName = findViewById(R.id.edit_text_login_group_group_name);
        editTextAdminName = findViewById(R.id.edit_text_login_group_admin_name);
        editTextAdminPassword = findViewById(R.id.edit_text_login_group_admin_password);

        buttonConfirm.setOnClickListener(v -> {

            if (!checkAndAssertInvalid()){
                return;
            }

            Intent intent = new Intent(this, MainActivity.class);
            intent
                    .putExtra("group_name", editTextGroupName.getText().toString())
                    .putExtra("admin_name", editTextAdminName.getText().toString())
                    .putExtra("admin_password", editTextAdminPassword.getText().toString());
            setResult(RESULT_OK, intent);
            finish();
        });

        buttonCancel.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            setResult(RESULT_CANCELED);
            finish();
        });

    }

    private boolean checkAndAssertInvalid(){

        if (editTextGroupName.getText().toString().equals("")){
            Toast.makeText(getApplicationContext(), "请输入组织名称！", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (editTextAdminName.getText().toString().equals("")){
            Toast.makeText(getApplicationContext(), "请输入管理员账号！", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (editTextAdminPassword.getText().toString().equals("")){
            Toast.makeText(getApplicationContext(), "请输入管理员密码！", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
}
