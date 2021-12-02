package com.drunkbull.drunkbullcloudcashbook.ui.accounts;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.drunkbull.drunkbullcloudcashbook.R;
import com.drunkbull.drunkbullcloudcashbook.network.ServerConnection;
import com.drunkbull.drunkbullcloudcashbook.pojo.CBGroup;
import com.drunkbull.drunkbullcloudcashbook.protobuf.CBMessage;
import com.drunkbull.drunkbullcloudcashbook.singleton.Auth;
import com.drunkbull.drunkbullcloudcashbook.singleton.GSignalManager;
import com.drunkbull.drunkbullcloudcashbook.singleton.NoSuchGSignalException;
import com.drunkbull.drunkbullcloudcashbook.ui.accounts.adapter.AccountsListAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.scwang.smart.refresh.footer.ClassicsFooter;
import com.scwang.smart.refresh.header.MaterialHeader;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;


public class FragmentAccounts extends Fragment {

    private Context context;
    public int pageIDX = -1;

    private AccountsListAdapter accountsListAdapter;
    private RecyclerView accountsRecyclerView;

    private FloatingActionButton floatButtonAddAccount;

    int selectedAccountPosition = -1;

    public FragmentAccounts(){
        GSignalManager.getSingleton().addGSignal(this, "notify_login_first");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("callback", "FragmentAccounts.onCreateView");
        View view = inflater.inflate(R.layout.fragment_accounts, container);

        floatButtonAddAccount = view.findViewById(R.id.float_button_add_account);
        floatButtonAddAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupDialogAddAccount();
            }
        });

        accountsListAdapter = new AccountsListAdapter(getContext(), Auth.getSingleton().cbGroup.members);
        accountsRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_accounts);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        accountsRecyclerView.setLayoutManager(layoutManager);
        //如果可以确定每个item的高度是固定的，设置这个选项可以提高性能
        accountsRecyclerView.setHasFixedSize(true);
        //创建并设置Adapter
        accountsRecyclerView.setAdapter(accountsListAdapter);
        accountsRecyclerView.setItemAnimator(new DefaultItemAnimator());
        accountsRecyclerView.setVisibility(View.INVISIBLE);

        try {
            GSignalManager.getSingleton().connect(accountsListAdapter, "create_account_context_menu", this, "onCreateAccountContextMenu", new Class[]{ContextMenu.class});
        } catch (NoSuchGSignalException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        this.context = getActivity();

        RefreshLayout refreshLayout = (RefreshLayout) view.findViewById(R.id.refresh_layout);
        refreshLayout.setRefreshHeader(new MaterialHeader(getContext()));
        refreshLayout.setRefreshFooter(new ClassicsFooter(getContext()));
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                requestGetAccounts();

                Toast.makeText(context, getText(R.string.notification_refresh_success), Toast.LENGTH_SHORT).show();

                refreshlayout.finishRefresh(500/*,false*/);//传入false表示刷新失败
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshlayout) {
                Toast.makeText(context, getText(R.string.notification_refresh_success), Toast.LENGTH_SHORT).show();

                refreshlayout.finishLoadMore(500/*,false*/);//传入false表示加载失败
            }
        });

        try {
            GSignalManager.getSingleton().connect(Auth.getSingleton(), "authenticated", this, "onAuthenticated");
            GSignalManager.getSingleton().connect(ServerConnection.getSingleton(), "responsed", this, "onResponsed", new Class[]{CBMessage.Response.class});
        } catch (NoSuchGSignalException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        return view;
    }

    private void popupDialogAddAccount(){
        AddAccountDialog customDialog = new AddAccountDialog(FragmentAccounts.this.getContext());
        customDialog.setCancel(new AddAccountDialog.IOnCancelListener() {
            @Override
            public void onCancel(AddAccountDialog dialog) {
                Log.d(this.getClass().getSimpleName(), "popupDialogAddAccount cancel");
            }
        });
        customDialog.setConfirm(new AddAccountDialog.IOnConfirmListener(){
            @Override
            public void onConfirm(AddAccountDialog dialog) {
                String username = dialog.getUsername();
                String nickname = dialog.getNickname();
                String password = dialog.getPassword();
                if (nickname.equals("")){
                    nickname = username;
                }
                requestAddAccount(
                        username,
                        nickname,
                        password
                );
                requestGetAccounts();

                Toast.makeText(getContext(), getString(R.string.text_add_account),Toast.LENGTH_SHORT).show();
            }
        });
        customDialog.show();
    }

    private void updateUIAccountsList(){
        accountsListAdapter.notifyDataSetChanged();
        if (Auth.getSingleton().authenticated){
            accountsRecyclerView.setVisibility(View.VISIBLE);
        }
        else{
            accountsRecyclerView.setVisibility(View.INVISIBLE);
        }
    }

    private void onCreateAccountContextMenu(ContextMenu menu){
        int groupID = 0;
        int order = 0;
        int[] itemID = {1, 2, 3};

        for(int i=0; i < itemID.length; i++)
        {
            switch(itemID[i])
            {
                case 1:
                    menu.add(groupID, itemID[i], order, getString(R.string.text_remove));
                    break;
                case 2:
                    menu.add(groupID, itemID[i], order, getString(R.string.text_edit) + " " + getString(R.string.text_todo));
                    break;
                case 3:
                    menu.add(groupID, itemID[i], order, getString(R.string.text_view_detail) + " " + getString(R.string.text_todo));
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 1:
                int pos = accountsListAdapter.getContextMenuPosition();
                Log.d("BookList", String.format("%s:%d", "删除", pos));
                CBGroup.CBGroupMember member = Auth.getSingleton().cbGroup.members.get(pos);
                if (member != null){
                    requestRemoveAccount(
                            member.username
                    );
                    requestGetAccounts();
                }
                break;
            case 2:
                break;
            case 3:
                break;
            default:
                break;
        }
        return super.onContextItemSelected(item);
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
            else{
                updateUIAccountsList();
                requestGetAccounts();
            }
        }
    }

    private void onResponsed(CBMessage.Response response){
        switch (response.getType()){
            case GET_ACCOUNTS:
                CBMessage.ResponseGetAccounts responseGetAccounts = response.getResponseGetAccounts();
                if (responseGetAccounts.getResult()){
                    Auth.getSingleton().cbGroup.members.clear();
                    for (CBMessage.User user : responseGetAccounts.getAccountsList()){
                        CBGroup.CBGroupMember cbGroupMember = new CBGroup.CBGroupMember();
                        cbGroupMember.username = user.getUsername();
                        cbGroupMember.nickname = user.getNickname();
                        cbGroupMember.admin = user.getAdmin();
                        cbGroupMember.read = user.getRead();
                        cbGroupMember.write = user.getWrite();
                        cbGroupMember.groupName = user.getGroupname();
                        Auth.getSingleton().cbGroup.members.add(cbGroupMember);
                    }
                }
                updateUIAccountsList();
                break;
            case ADD_ACCOUNT:
                break;
            case REMOVE_ACCOUNT:
                break;
            default:
                break;
        }
    }

    private void onAuthenticated(){
    }

    private void requestAddAccount(
            String username,
            String nickname,
            String password
    ){
        CBMessage.Request.Builder builder = CBMessage.Request.newBuilder();

        CBMessage.RequestAddAccount.Builder requestAddAccountBuilder = CBMessage.RequestAddAccount.newBuilder();

        CBMessage.User.Builder userBuilder = CBMessage.User.newBuilder();
        userBuilder
                .setGroupname(Auth.getSingleton().cbGroup.groupName)
                .setAdmin(false)
                .setRead(true)
                .setWrite(true)
                .setUsername(username)
                .setNickname(nickname)
                .setPassword(password);

        requestAddAccountBuilder
                .setUser(userBuilder.build())
                .setGroupname(Auth.getSingleton().cbGroup.groupName);

        builder
                .setRequestAddAccount(requestAddAccountBuilder.build())
                .setType(CBMessage.Type.ADD_ACCOUNT);

        ServerConnection.getSingleton().sendRequest(builder);
    }

    private void requestRemoveAccount(
            String username
    ){
        CBMessage.Request.Builder builder = CBMessage.Request.newBuilder();

        CBMessage.RequestRemoveAccount.Builder requestRemoveAccountBuilder = CBMessage.RequestRemoveAccount.newBuilder();


        requestRemoveAccountBuilder
                .setUsername(username)
                .setGroupname(Auth.getSingleton().cbGroup.groupName);

        builder
                .setRequestRemoveAccount(requestRemoveAccountBuilder.build())
                .setType(CBMessage.Type.REMOVE_ACCOUNT);

        ServerConnection.getSingleton().sendRequest(builder);
    }

    private void requestGetAccounts(){
        CBMessage.Request.Builder builder = CBMessage.Request.newBuilder();

        CBMessage.RequestGetAccounts.Builder requestGetAccountsBuilder = CBMessage.RequestGetAccounts.newBuilder();


        requestGetAccountsBuilder
                .setGroupname(Auth.getSingleton().cbGroup.groupName);

        builder
                .setRequestGetAccounts(requestGetAccountsBuilder.build())
                .setType(CBMessage.Type.GET_ACCOUNTS);

        ServerConnection.getSingleton().sendRequest(builder);
    }



}
