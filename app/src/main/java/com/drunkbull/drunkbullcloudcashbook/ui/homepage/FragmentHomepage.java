package com.drunkbull.drunkbullcloudcashbook.ui.homepage;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.drunkbull.drunkbullcloudcashbook.R;
import com.drunkbull.drunkbullcloudcashbook.network.ServerConnection;
import com.drunkbull.drunkbullcloudcashbook.protobuf.CBMessage;
import com.drunkbull.drunkbullcloudcashbook.singleton.Auth;
import com.drunkbull.drunkbullcloudcashbook.singleton.GSignalManager;
import com.drunkbull.drunkbullcloudcashbook.singleton.NoSuchGSignalException;


public class FragmentHomepage extends Fragment {

    public int pageIDX = 0;
    private Context context;
    private boolean current = false;

    Button buttonCreateGroup;
    Button buttonLoginGroup;
    Button buttonGroupDashboard;

    public FragmentHomepage(){
        GSignalManager.getSingleton().addGSignal(this, "switch_to_create_group");
        GSignalManager.getSingleton().addGSignal(this, "switch_to_login_group");
        GSignalManager.getSingleton().addGSignal(this, "switch_to_group_homepage");
        GSignalManager.getSingleton().addGSignal(this, "notify_login_first");
    }

    ImageView imageViewTest;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("callback", "FragmentHomepage.onCreateView");
        View view = inflater.inflate(R.layout.fragment_homepage, container);

        try {
            GSignalManager.getSingleton().connect(getActivity(), "create_group", this, "onCreateGroup", new Class[]{Intent.class});
            GSignalManager.getSingleton().connect(getActivity(), "login_group", this, "onLoginGroup", new Class[]{Intent.class});
            GSignalManager.getSingleton().connect(ServerConnection.getSingleton(), "responsed", this, "onResponsed", new Class[]{CBMessage.Response.class});
        } catch (NoSuchGSignalException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        buttonCreateGroup = view.findViewById(R.id.button_create_group);
        buttonLoginGroup = view.findViewById(R.id.button_login_group);
        buttonGroupDashboard = view.findViewById(R.id.button_group_dashboard);

        imageViewTest = view.findViewById(R.id.image_view_test);


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
                try {
                    GSignalManager.getSingleton().emitGSignal(FragmentHomepage.this, "switch_to_login_group");
                } catch (NoSuchGSignalException e) {
                    e.printStackTrace();
                }
                Toast.makeText(getContext(), "buttonLoginGroup", Toast.LENGTH_SHORT).show();
            }
        });

        buttonGroupDashboard.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {

                @SuppressLint("ResourceAsColor") ColorStateList colorStateList = ColorStateList.valueOf(R.color.green);

                imageViewTest.setForegroundTintList(colorStateList);

                try {
                    GSignalManager.getSingleton().emitGSignal(FragmentHomepage.this, "switch_to_group_homepage");
                } catch (NoSuchGSignalException e) {
                    e.printStackTrace();
                }



                Toast.makeText(getContext(), "buttonGroupDashboard", Toast.LENGTH_SHORT).show();
            }
        });



        return view;
    }

    public void onPageChanged(Integer pos){
        int position = pos;
        if (position == pageIDX){
            current = true;
            if (!Auth.getSingleton().authenticated){
                try {
                    GSignalManager.getSingleton().emitGSignal(this, "notify_login_first");
                } catch (NoSuchGSignalException e) {
                    e.printStackTrace();
                }
            }
        }
        else{
            current = false;
        }
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

    private void requestLoginGroup(
            String groupName,
            String adminName,
            String adminPassword
    ){
        CBMessage.Request.Builder builder = CBMessage.Request.newBuilder();
        CBMessage.RequestEnterGroup.Builder requestEnterGroupBuilder = CBMessage.RequestEnterGroup.newBuilder();
        requestEnterGroupBuilder
                .setGroupname(groupName)
                .setUsername(adminName)
                .setPassword(adminPassword);
        builder
                .setType(CBMessage.Type.ENTER_GROUP)
                .setRequestEnterGroup(requestEnterGroupBuilder.build());
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

    private void onLoginGroup(Intent intent){
        String groupName = intent.getStringExtra("group_name");
        String adminName = intent.getStringExtra("admin_name");
        String adminPassword = intent.getStringExtra("admin_password");

        requestLoginGroup(
                groupName,
                adminName,
                adminPassword
        );
    }

    private void onResponsed(CBMessage.Response response){
        switch (response.getType()){
            case CREATE_GROUP:
                CBMessage.ResponseCreateGroup responseCreateGroup = response.getResponseCreateGroup();
                if (responseCreateGroup.getResult()){
                    Toast.makeText(getActivity(), "创建组织成功！您可以尝试登入！", Toast.LENGTH_SHORT).show();
                }
                else{
                    //Looper.prepare();
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle(R.string.err);
                    builder.setMessage(getString(R.string.err_create_group) + String.format(":%s", responseCreateGroup.getWords()));
                    builder.setCancelable(true);
                    builder.show();
                    Toast.makeText(getActivity(), String.format("创建组织失败！code:%s", responseCreateGroup.getWords()), Toast.LENGTH_SHORT).show();
                    //Looper.loop();
                }
                break;
            case ENTER_GROUP:
                CBMessage.ResponseEnterGroup responseEnterGroup = response.getResponseEnterGroup();
                if (responseEnterGroup.getResult()){

                    CBMessage.User user = responseEnterGroup.getUser();
                    Auth.getSingleton().cbGroupMember.groupName = user.getGroupname();
                    Auth.getSingleton().cbGroupMember.username = user.getUsername();
                    Auth.getSingleton().cbGroupMember.nickname = user.getNickname();
                    Auth.getSingleton().cbGroupMember.admin = user.getAdmin();
                    Auth.getSingleton().cbGroupMember.read = user.getRead();
                    Auth.getSingleton().cbGroupMember.write = user.getWrite();
                    Auth.getSingleton().authenticated = true;
                    Auth.getSingleton().cbGroup.groupName = Auth.getSingleton().cbGroupMember.groupName;
                    if (Auth.getSingleton().cbGroupMember.admin){
                        Auth.getSingleton().cbGroup.admin = Auth.getSingleton().cbGroupMember;
                    }
                    try {
                        GSignalManager.getSingleton().emitGSignal(Auth.getSingleton(), "authenticated");
                    } catch (NoSuchGSignalException e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(getActivity(), "登入组织成功！", Toast.LENGTH_SHORT).show();
                }
                else{
                    //Looper.prepare();
                    AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());
                    builder.setTitle(R.string.err);
                    builder.setMessage(getString(R.string.err_login_group) + String.format(":%s", responseEnterGroup.getWords()));
                    builder.setCancelable(true);
                    builder.show();
                    Toast.makeText(getActivity(), String.format("登入组织失败！code:%s", responseEnterGroup.getWords()), Toast.LENGTH_SHORT).show();
                    //Looper.loop();
                }
                break;
            default:
                break;
        }
    }


}
