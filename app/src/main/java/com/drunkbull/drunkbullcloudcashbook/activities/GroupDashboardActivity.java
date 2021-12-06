package com.drunkbull.drunkbullcloudcashbook.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.drunkbull.drunkbullcloudcashbook.MainActivity;
import com.drunkbull.drunkbullcloudcashbook.R;
import com.drunkbull.drunkbullcloudcashbook.singleton.Auth;
import com.drunkbull.drunkbullcloudcashbook.utils.RecordsTool;

public class GroupDashboardActivity extends AppCompatActivity {

    Button buttonReturn;

    TextView textViewGroupName;
    TextView textViewAdminName;
    TextView textViewMembersCount;
    TextView textViewRecordsCount;
    TextView textViewTotalMoney;

    @SuppressLint("DefaultLocale")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_dashboard);

        buttonReturn = findViewById(R.id.button_return);
        textViewGroupName = findViewById(R.id.text_view_group_name);
        textViewAdminName = findViewById(R.id.text_view_admin_name);
        textViewMembersCount = findViewById(R.id.text_view_members_count);
        textViewRecordsCount = findViewById(R.id.text_view_records_count);
        textViewTotalMoney = findViewById(R.id.text_view_total_money);

        textViewGroupName.setText(Auth.getSingleton().cbGroup.groupName);
        textViewAdminName.setText(String.format("%s(%s)", Auth.getSingleton().cbGroup.admin.nickname, Auth.getSingleton().cbGroup.admin.username));
        textViewMembersCount.setText(String.format("%d", Auth.getSingleton().cbGroup.members.size()));
        textViewRecordsCount.setText(String.format("%d", Auth.getSingleton().cbGroup.records.size()));
        double totalMoney = RecordsTool.getTotalMoney();
        textViewTotalMoney.setText(String.format("%.2f", totalMoney));

        if (totalMoney < 0){
            textViewTotalMoney.setTextColor(getResources().getColor(R.color.green));
        }
        else if (totalMoney > 0){
            textViewTotalMoney.setTextColor(getResources().getColor(R.color.red));
        }

        buttonReturn.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            setResult(RESULT_CANCELED);
            finish();
        });

    }

}
