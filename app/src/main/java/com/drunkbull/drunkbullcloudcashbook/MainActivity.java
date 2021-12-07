package com.drunkbull.drunkbullcloudcashbook;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.drunkbull.drunkbullcloudcashbook.activities.CreateGroupActivity;
import com.drunkbull.drunkbullcloudcashbook.activities.GroupDashboardActivity;
import com.drunkbull.drunkbullcloudcashbook.activities.LoginGroupActivity;
import com.drunkbull.drunkbullcloudcashbook.network.HeartBeatManager;
import com.drunkbull.drunkbullcloudcashbook.network.ServerConnection;
import com.drunkbull.drunkbullcloudcashbook.protobuf.CBMessage;
import com.drunkbull.drunkbullcloudcashbook.singleton.Auth;
import com.drunkbull.drunkbullcloudcashbook.singleton.GSignalManager;
import com.drunkbull.drunkbullcloudcashbook.singleton.NoSuchGSignalException;
import com.drunkbull.drunkbullcloudcashbook.ui.accounts.FragmentAccounts;
import com.drunkbull.drunkbullcloudcashbook.ui.homepage.FragmentHomepage;
import com.drunkbull.drunkbullcloudcashbook.ui.records.FragmentRecords;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.drunkbull.drunkbullcloudcashbook.utils.data.DateUtil;
import com.drunkbull.drunkbullcloudcashbook.utils.data.TimeUtil;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    List<Fragment> fragments = new ArrayList<>();
    private int[] tabTitlesIDs;
    private int[] tabIconIDs;
    private TabLayout tabLayout = null;
    private ViewPager2 viewPager2 = null;

    private TextView textViewHeadline;
    private TextView textViewStatus;

    FragmentHomepage fragmentHomepage = new FragmentHomepage();
    FragmentRecords fragmentRecords = new FragmentRecords();
    FragmentAccounts fragmentAccounts = new FragmentAccounts();

    ActivityResultLauncher<Intent> createGroupActivityLauncher;
    ActivityResultLauncher<Intent> loginGroupActivityLauncher;
    ActivityResultLauncher<Intent> groupDashboardActivityLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        initData();

        try {
            GSignalManager.getSingleton().addGSignal(this, "create_group");
            GSignalManager.getSingleton().addGSignal(this, "login_group");
            GSignalManager.getSingleton().addGSignal(this, "page_changed");

            GSignalManager.getSingleton().connect(Auth.getSingleton(), "authenticated", this, "onAuthenticated");
            GSignalManager.getSingleton().connect(ServerConnection.getSingleton(), "responsed", this, "onResponsed", new Class[]{CBMessage.Response.class});
            GSignalManager.getSingleton().connect(ServerConnection.getSingleton(), "connection_connected", this, "onConnectionConnected");
            GSignalManager.getSingleton().connect(ServerConnection.getSingleton(), "connection_disconnected", this, "onConnectionDisconnected");
            GSignalManager.getSingleton().connect(ServerConnection.getSingleton(), "connection_reconnecting", this, "onConnectionReconnecting");

            GSignalManager.getSingleton().connect(fragmentHomepage, "switch_to_create_group", this, "switchToCreateGroup");
            GSignalManager.getSingleton().connect(fragmentHomepage, "switch_to_login_group", this, "switchToLoginGroup");
            GSignalManager.getSingleton().connect(fragmentHomepage, "switch_to_group_homepage", this, "switchToGroupHomepage");

            GSignalManager.getSingleton().connect(HeartBeatManager.getSingleton(), "timeout", this, "onConnectionDisconnected");

            for (Fragment fragment : fragments){
                GSignalManager.getSingleton().connect(this, "page_changed", fragment, "onPageChanged", new Class[]{Integer.class});
                GSignalManager.getSingleton().connect(fragment, "notify_login_first", this, "onNotifyLoginFirst");
            }

        } catch (NoSuchGSignalException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ServerConnection.getSingleton().initChannel();

        textViewHeadline = findViewById(R.id.text_view_headline);
        textViewStatus = findViewById(R.id.text_view_status);
        updateTextViewStatus();

        //找到控件
        tabLayout = findViewById(R.id.tab_layout);
        viewPager2 = findViewById(R.id.view_page2);
        //创建适配器
        MainViewPageAdapter adapter = new MainViewPageAdapter(this, fragments);
        viewPager2.setAdapter(adapter);

        viewPager2.setOffscreenPageLimit(3);

        //TabLayout与ViewPage2联动关键代码
        new TabLayoutMediator(tabLayout, viewPager2, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                Log.d("callback", "TabLayoutMediator.TabConfigurationStrategy.onConfigureTab");
                tab.setIcon(tabIconIDs[position]);
                tab.setText(tabTitlesIDs[position]);
            }
        }).attach();

        //ViewPage2选中改变监听
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                try {
                    GSignalManager.getSingleton().emitGSignal(MainActivity.this, "page_changed", new Class[]{Integer.class}, new Object[]{position});
                } catch (NoSuchGSignalException e) {
                    e.printStackTrace();
                }
                Log.d("callback", "ViewPager2.OnPageChangeCallback");
                textViewHeadline.setText(tabTitlesIDs[position]);
            }

            @Override
            public void onPageScrollStateChanged(int position){
                super.onPageScrollStateChanged(position);
                Log.d("callback", "ViewPager2.onPageScrollStateChanged");
            }

            @Override
            public void onPageScrolled(
                    int position,
                    float positionOffset,
                    int positionOffsetPixels
            ){
                super.onPageScrolled(
                        position,
                        positionOffset,
                        positionOffsetPixels
                );
                Log.d("callback", "ViewPager2.onPageScrolled");
            }
        });
        //TabLayout的选中改变监听
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Log.d("callback", "TabLayout.OnTabSelectedListener.onTabSelected");

            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                Log.d("callback", "TabLayout.OnTabSelectedListener.onTabUnselected");
            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                Log.d("callback", "TabLayout.OnTabSelectedListener.onTabReselected");
            }
        });


        createGroupActivityLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == RESULT_OK) {
                    assert result.getData() != null;

                    try {
                        GSignalManager.getSingleton().emitGSignal(MainActivity.this, "create_group", new Class[]{Intent.class}, new Object[]{result.getData()});
                    } catch (NoSuchGSignalException e) {
                        e.printStackTrace();
                    }

                }
                else if (result.getResultCode() == RESULT_CANCELED){
                    //nothing
                }
            }
        });

        loginGroupActivityLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == RESULT_OK) {
                    assert result.getData() != null;

                    try {
                        GSignalManager.getSingleton().emitGSignal(MainActivity.this, "login_group", new Class[]{Intent.class}, new Object[]{result.getData()});
                    } catch (NoSuchGSignalException e) {
                        e.printStackTrace();
                    }

                }
                else if (result.getResultCode() == RESULT_CANCELED){
                    //nothing
                }
            }
        });

        groupDashboardActivityLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == RESULT_OK) {
                    assert result.getData() != null;
                }
                else if (result.getResultCode() == RESULT_CANCELED){
                    //nothing
                }
            }
        });

        HeartBeatManager.getSingleton().start();
        Toast.makeText(MainActivity.this, "已启动！", Toast.LENGTH_SHORT).show();
    }


    private void onResponsed(CBMessage.Response response){
        if (response.getType() != CBMessage.Type.HEARTBEAT){
            //Toast.makeText(this, getText(R.string.notification_got_data) + ":" + response.getType().toString(), Toast.LENGTH_SHORT).show();
        }
    }

    private void updateTextViewStatus(){
        textViewStatus.setText(
                String.format(
                        "%s好，%s（%s）",
                        getString(DateUtil.getDayTimePeriodStringID()),
                        Auth.getSingleton().cbGroupMember.nickname,
                        Auth.getSingleton().cbGroupMember.username
                )
        );
    }

    private void onNotifyLoginFirst(){
        Toast.makeText(this, getText(R.string.notification_login_first), Toast.LENGTH_SHORT).show();
        Log.i("", "");
    }

    private void onAuthenticated(){
        updateTextViewStatus();
    }

    private void onConnectionConnected(){
        ServerConnection.getSingleton().sendRequestConnect();

        Toast.makeText(getApplicationContext(), "已连接！", Toast.LENGTH_SHORT).show();
    }

    private void onConnectionDisconnected(){
        HeartBeatManager.getSingleton().inactivate();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.notification_disconnected);
        //builder.setIcon(R.drawable.class_delteacher);
        builder.setCancelable(true);
        builder.setNegativeButton(getString(R.string.text_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        builder.show();
    }

    private void onConnectionReconnecting(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.notification_reconnecting);
        //builder.setIcon(R.drawable.class_delteacher);
        builder.setCancelable(true);
        builder.setNegativeButton(getString(R.string.text_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        builder.show();

        //Toast.makeText(getApplicationContext(), "重连中！", Toast.LENGTH_LONG).show();
    }


    private void initData(){
        tabTitlesIDs = new int[]{R.string.title_homepage, R.string.title_records, R.string.title_accounts};
        tabIconIDs = new int[]{R.drawable.home, R.drawable.menu_list, R.drawable.massive_multiplayer};
        fragmentHomepage.pageIDX = 0;
        fragmentRecords.pageIDX = 1;
        fragmentAccounts.pageIDX = 2;
        fragments.add(fragmentHomepage);
        fragments.add(fragmentRecords);
        fragments.add(fragmentAccounts);
    }

    public void switchToCreateGroup(){
        Intent intent = new Intent(this, CreateGroupActivity.class);
        createGroupActivityLauncher.launch(intent);
    }

    public void switchToLoginGroup(){
        if (Auth.getSingleton().authenticated){
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle(R.string.text_please_confirm);
            builder.setMessage(R.string.text_replace_authentication);
            builder.setPositiveButton(R.string.text_confirm, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Auth.getSingleton().authenticated = false;
                    Intent intent = new Intent(MainActivity.this, LoginGroupActivity.class);
                    loginGroupActivityLauncher.launch(intent);
                }
            });
            builder.setNegativeButton(R.string.text_cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            builder.show();
        }
        else{
            Intent intent = new Intent(this, LoginGroupActivity.class);
            loginGroupActivityLauncher.launch(intent);
        }
    }

    public void switchToGroupHomepage(){
        if (Auth.getSingleton().authenticated){
            Intent intent = new Intent(this, GroupDashboardActivity.class);
            groupDashboardActivityLauncher.launch(intent);
        }
        else{
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle(R.string.notification_login_first);
            builder.setMessage(R.string.notification_login_first);
            builder.setNegativeButton(R.string.text_cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            builder.show();
        }

    }



}