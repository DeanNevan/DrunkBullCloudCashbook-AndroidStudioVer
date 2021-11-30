package com.drunkbull.drunkbullcloudcashbook.ui.homepage;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.drunkbull.drunkbullcloudcashbook.MainActivity;
import com.drunkbull.drunkbullcloudcashbook.R;
import com.drunkbull.drunkbullcloudcashbook.activities.CreateGroupActivity;
import com.drunkbull.drunkbullcloudcashbook.network.RequestWriter;
import com.drunkbull.drunkbullcloudcashbook.network.ServerConnection;
import com.drunkbull.drunkbullcloudcashbook.protobuf.CBMessage;
import com.drunkbull.drunkbullcloudcashbook.singleton.GSignalManager;
import com.drunkbull.drunkbullcloudcashbook.singleton.NoSuchGSignalException;
import com.drunkbull.drunkbullcloudcashbook.utils.data.DateUtil;


public class FragmentHomepage extends Fragment {

    Button buttonCreateGroup;
    Button buttonLoginGroup;
    Button buttonGroupDashboard;

    public FragmentHomepage(){
        GSignalManager.getSingleton().addGSignal(this, "switch_to_create_group");
        GSignalManager.getSingleton().addGSignal(this, "switch_to_login_group");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("callback", "FragmentHomepage.onCreateView");
        View view = inflater.inflate(R.layout.fragment_homepage, container);

        try {
            GSignalManager.getSingleton().connect(getActivity(), "create_group", this, "onCreateGroup", new Class[]{Intent.class});
        } catch (NoSuchGSignalException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        buttonCreateGroup = view.findViewById(R.id.button_create_group);
        buttonLoginGroup = view.findViewById(R.id.button_login_group);
        buttonGroupDashboard = view.findViewById(R.id.button_group_dashboard);

        buttonCreateGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    GSignalManager.getSingleton().emitGSignal(FragmentHomepage.this, "switch_to_create_group");
                } catch (NoSuchGSignalException e) {
                    e.printStackTrace();
                }
                Toast.makeText(getContext(), "buttonCreateGroup", Toast.LENGTH_SHORT).show();
            }
        });

        buttonLoginGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "buttonLoginGroup", Toast.LENGTH_SHORT).show();
            }
        });

        buttonGroupDashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "buttonGroupDashboard", Toast.LENGTH_SHORT).show();
            }
        });



        return view;
    }

    private void requestCreateGroup(
            String groupName,
            String adminName,
            String adminPassword,
            String adminNickname
    ){
        CBMessage.Request.Builder builder = CBMessage.Request.newBuilder();
        CBMessage.RequestCreateGroup.Builder requestCreateGroupBuilder = CBMessage.RequestCreateGroup.newBuilder();
        requestCreateGroupBuilder
                .setGroupname(groupName)
                .setAdminUsername(adminName)
                .setAdminNickname(adminNickname)
                .setAdminPassword(adminPassword);
        builder
                .setType(CBMessage.Type.CREATE_GROUP)
                .setRequestCreateGroup(requestCreateGroupBuilder.build());
        ServerConnection.getSingleton().sendRequest(builder);
    }

    private void onCreateGroup(Intent intent){
        String groupName = intent.getStringExtra("group_name");
        String adminName = intent.getStringExtra("admin_name");
        String adminPassword = intent.getStringExtra("admin_password");
        String adminPasswordConfirm = intent.getStringExtra("admin_password_confirm");
        String adminNickname = intent.getStringExtra("admin_nickname");

        if (adminNickname.equals("")) adminNickname = adminName;

        requestCreateGroup(
                groupName,
                adminName,
                adminPassword,
                adminNickname
        );
    }


}
