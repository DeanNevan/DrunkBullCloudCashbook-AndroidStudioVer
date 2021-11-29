package com.drunkbull.drunkbullcloudcashbook.ui.accounts;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.drunkbull.drunkbullcloudcashbook.R;


public class FragmentAccounts extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("callback", "FragmentAccounts.onCreateView");
        View view = inflater.inflate(R.layout.fragment_accounts, container);
        return view;
    }


}
