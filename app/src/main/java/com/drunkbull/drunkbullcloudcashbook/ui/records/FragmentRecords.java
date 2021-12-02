package com.drunkbull.drunkbullcloudcashbook.ui.records;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.drunkbull.drunkbullcloudcashbook.R;
import com.drunkbull.drunkbullcloudcashbook.singleton.Auth;
import com.drunkbull.drunkbullcloudcashbook.singleton.GSignalManager;
import com.drunkbull.drunkbullcloudcashbook.singleton.NoSuchGSignalException;
import com.drunkbull.drunkbullcloudcashbook.ui.homepage.FragmentHomepage;


public class FragmentRecords extends Fragment {

    TextView textViewBlank;

    public int pageIDX = -1;

    public FragmentRecords(){
        GSignalManager.getSingleton().addGSignal(this, "notify_login_first");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("callback", "FragmentRecords.onCreateView");
        View view = inflater.inflate(R.layout.fragment_records, container);

        textViewBlank = view.findViewById(R.id.text_view_blank_records);
        textViewBlank.setText(R.string.notification_login_first);

        try {
            GSignalManager.getSingleton().connect(Auth.getSingleton(), "authenticated", this, "onAuthenticated");
        } catch (NoSuchGSignalException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        return view;
    }

    private void onAuthenticated(){
        textViewBlank.setText("");
    }

    private void onPageChanged(Integer pos){
        int position = pos;
        if (position == pageIDX){
            if (!Auth.getSingleton().authenticated){
                try {
                    GSignalManager.getSingleton().emitGSignal(this, "notify_login_first");
                } catch (NoSuchGSignalException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
