package com.drunkbull.drunkbullcloudcashbook;

import android.os.Bundle;
import android.util.Log;

import com.drunkbull.drunkbullcloudcashbook.singleton.Auth;
import com.drunkbull.drunkbullcloudcashbook.singleton.GSignalManager;
import com.drunkbull.drunkbullcloudcashbook.singleton.NoSuchGSignalException;
import com.drunkbull.drunkbullcloudcashbook.ui.accounts.FragmentAccounts;
import com.drunkbull.drunkbullcloudcashbook.ui.homepage.FragmentHomepage;
import com.drunkbull.drunkbullcloudcashbook.ui.records.FragmentRecords;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.drunkbull.drunkbullcloudcashbook.utils.Reflex;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    List<Fragment> fragments = new ArrayList<>();
    private int[] tabTitlesIDs;
    private int[] tabIconIDs;
    private TabLayout tabLayout = null;
    private ViewPager2 viewPager2 = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initData();

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
                Log.d("callback", "ViewPager2.OnPageChangeCallback");
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

        try {
            GSignalManager.getSingleton().connect(Auth.getSingleton(), "已验证", this, "testFunc");
        } catch (NoSuchGSignalException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        try {
            GSignalManager.getSingleton().emitGSignal(Auth.getSingleton(), "已验证");
        } catch (NoSuchGSignalException e) {
            e.printStackTrace();
        }


    }

    private void testFunc(){
        Log.d("test", "!!!!!!!!!!!!!!!!!!!!!!");
    }


    private void initData(){
        tabTitlesIDs = new int[]{R.string.tab_item_homepage, R.string.tab_item_records, R.string.tab_item_accounts};
        tabIconIDs = new int[]{R.drawable.home, R.drawable.menu_list, R.drawable.massive_multiplayer};
        FragmentHomepage fragmentHomepage = new FragmentHomepage();
        FragmentRecords fragmentRecords = new FragmentRecords();
        FragmentAccounts fragmentAccounts = new FragmentAccounts();
        fragments.add(fragmentHomepage);
        fragments.add(fragmentRecords);
        fragments.add(fragmentAccounts);
    }

}