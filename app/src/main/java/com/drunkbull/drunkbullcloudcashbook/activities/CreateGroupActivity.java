package com.drunkbull.drunkbullcloudcashbook.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.drunkbull.drunkbullcloudcashbook.MainActivity;
import com.drunkbull.drunkbullcloudcashbook.R;

public class CreateGroupActivity extends AppCompatActivity {

    Button buttonConfirm;
    Button buttonCancel;

    EditText editTextGroupName;
    EditText editTextAdminName;
    EditText editTextAdminPassword;
    EditText editTextAdminConfirmPassword;
    EditText editTextAdminNickname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        buttonConfirm = findViewById(R.id.button_confirm_create_group);
        buttonCancel = findViewById(R.id.button_cancel_create_group);

        editTextGroupName = findViewById(R.id.edit_text_create_group_group_name);
        editTextAdminName = findViewById(R.id.edit_text_create_group_admin_name);
        editTextAdminPassword = findViewById(R.id.edit_text_create_group_admin_password);
        editTextAdminConfirmPassword = findViewById(R.id.edit_text_create_group_admin_password_confirm);
        editTextAdminNickname = findViewById(R.id.edit_text_create_group_admin_nickname);

        buttonConfirm.setOnClickListener(v -> {

            if (!checkAndAssertInvalid()){
                return;
            }

            Intent intent = new Intent(this, MainActivity.class);
            intent
                    .putExtra("group_name", editTextGroupName.getText().toString())
                    .putExtra("admin_name", editTextAdminName.getText().toString())
                    .putExtra("admin_password", editTextAdminPassword.getText().toString())
                    .putExtra("admin_password_confirm", editTextAdminConfirmPassword.getText().toString())
                    .putExtra("admin_nickname", editTextAdminNickname.getText().toString());
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
        if (editTextAdminConfirmPassword.getText().toString().equals("")){
            Toast.makeText(getApplicationContext(), "请输入二次确认密码！", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!editTextAdminPassword.getText().toString().equals(editTextAdminConfirmPassword.getText().toString())){
            Toast.makeText(getApplicationContext(), "请输入相同的密码！", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
}
