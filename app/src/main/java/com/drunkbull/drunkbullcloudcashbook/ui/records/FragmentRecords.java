package com.drunkbull.drunkbullcloudcashbook.ui.records;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.drunkbull.drunkbullcloudcashbook.R;
import com.drunkbull.drunkbullcloudcashbook.network.ServerConnection;
import com.drunkbull.drunkbullcloudcashbook.pojo.CBRecord;
import com.drunkbull.drunkbullcloudcashbook.protobuf.CBMessage;
import com.drunkbull.drunkbullcloudcashbook.singleton.Auth;
import com.drunkbull.drunkbullcloudcashbook.singleton.GSignalManager;
import com.drunkbull.drunkbullcloudcashbook.singleton.NoSuchGSignalException;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.scwang.smart.refresh.footer.ClassicsFooter;
import com.scwang.smart.refresh.header.MaterialHeader;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;


public class FragmentRecords extends Fragment {

    public int pageIDX = 0;
    private Context context;
    private boolean current = false;

    TextView textViewBlank;

    private FloatingActionButton floatButtonAddRecord;
    private FloatingActionButton floatButtonManageSearchContents;
    private FloatingActionButton floatButtonSortType;

    private CBMessage.SortType sortType = CBMessage.SortType.SORT_ID;//bad
    private boolean ascending = false;

    private int searchPageSize = 20;
    private int searchPageIDX = 0;

    private RecordsListAdapter recordsListAdapter;
    private RecyclerView recordsRecyclerView;

    private int selectedAscendButtonID = 0;
    private int selectedSortTypeButtonID = 0;

    public FragmentRecords(){
        GSignalManager.getSingleton().addGSignal(this, "notify_login_first");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("callback", "FragmentRecords.onCreateView");
        View view = inflater.inflate(R.layout.fragment_records, container);

        floatButtonAddRecord = view.findViewById(R.id.float_button_add_record);
        floatButtonManageSearchContents = view.findViewById(R.id.float_button_manage_search_contents);
        floatButtonSortType = view.findViewById(R.id.float_button_sort_type);

        floatButtonAddRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupDialogAddRecord();
            }
        });
        floatButtonManageSearchContents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle(R.string.text_todo);
                builder.setMessage(R.string.text_todo);
                builder.setNegativeButton(R.string.text_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                builder.show();
            }
        });
        floatButtonSortType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupDialogSortType();
            }
        });

        textViewBlank = view.findViewById(R.id.text_view_blank_records);
        textViewBlank.setText(R.string.notification_login_first);


        recordsListAdapter = new RecordsListAdapter(getContext(), Auth.getSingleton().cbGroup.records);
        recordsRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_records);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recordsRecyclerView.setLayoutManager(layoutManager);
        //如果可以确定每个item的高度是固定的，设置这个选项可以提高性能
        recordsRecyclerView.setHasFixedSize(true);
        //创建并设置Adapter
        recordsRecyclerView.setAdapter(recordsListAdapter);
        recordsRecyclerView.setItemAnimator(new DefaultItemAnimator());
        recordsRecyclerView.setVisibility(View.INVISIBLE);


        try {
            GSignalManager.getSingleton().connect(Auth.getSingleton(), "authenticated", this, "onAuthenticated");
            GSignalManager.getSingleton().connect(ServerConnection.getSingleton(), "responsed", this, "onResponsed", new Class[]{CBMessage.Response.class});
            GSignalManager.getSingleton().connect(recordsListAdapter, "create_record_context_menu", this, "onCreateRecordContextMenu", new Class[]{ContextMenu.class});
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
                requestGetRecords();
                Toast.makeText(context, getText(R.string.notification_refresh_success), Toast.LENGTH_SHORT).show();
                refreshlayout.finishRefresh(500/*,false*/);//传入false表示刷新失败
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshlayout) {
                requestGetRecords();
                Toast.makeText(context, getText(R.string.notification_refresh_success), Toast.LENGTH_SHORT).show();
                refreshlayout.finishLoadMore(500/*,false*/);//传入false表示加载失败
            }
        });

        return view;
    }

    private void popupDialogAddRecord(){
        AddRecordDialog addRecordDialog = new AddRecordDialog(getContext());
        addRecordDialog.setCancel(new AddRecordDialog.IOnCancelListener() {
            @Override
            public void onCancel(AddRecordDialog dialog) {
                Log.d(this.getClass().getSimpleName(), "popupDialogAddRecord cancel");
            }
        });
        addRecordDialog.setConfirm(new AddRecordDialog.IOnConfirmListener(){
            @Override
            public void onConfirm(AddRecordDialog dialog) {
                String title = dialog.getTitle();
                String comment = dialog.getComment();
                long dateStamp = dialog.getDateTimestamp();
                double money = dialog.getMoney();

                CBRecord cbRecord = new CBRecord();
                cbRecord.title = title;
                cbRecord.comment = comment;
                cbRecord.money = money;
                cbRecord.dateTime = dateStamp;

                requestAddRecord(cbRecord);

                Toast.makeText(getContext(), getString(R.string.text_add_record),Toast.LENGTH_SHORT).show();
            }
        });
        addRecordDialog.show();
    }

    private void popupDialogViewRecord(CBRecord viewRecord){
        AddRecordDialog addRecordDialog = new AddRecordDialog(getContext(), true, viewRecord);
        addRecordDialog.setCancel(new AddRecordDialog.IOnCancelListener() {
            @Override
            public void onCancel(AddRecordDialog dialog) {
                Log.d(this.getClass().getSimpleName(), "popupDialogAddRecord cancel");
            }
        });
        addRecordDialog.setConfirm(new AddRecordDialog.IOnConfirmListener(){
            @Override
            public void onConfirm(AddRecordDialog dialog) {
            }
        });
        addRecordDialog.show();
    }

    private void popupDialogSortType(){
        RecordSortTypeDialog recordSortTypeDialog = new RecordSortTypeDialog(getContext(), selectedAscendButtonID, selectedSortTypeButtonID);
        recordSortTypeDialog.setCancel(new RecordSortTypeDialog.IOnCancelListener() {
            @Override
            public void onCancel(RecordSortTypeDialog dialog) {
                Log.d(this.getClass().getSimpleName(), "popupDialogSortType cancel");
            }
        });
        recordSortTypeDialog.setConfirm(new RecordSortTypeDialog.IOnConfirmListener(){
            @Override
            public void onConfirm(RecordSortTypeDialog dialog) {
                selectedAscendButtonID = dialog.selectedAscendButtonID;
                selectedSortTypeButtonID = dialog.selectedSortTypeButtonID;
                String selectedAscendString = dialog.selectedAscendString;

                String selectedSortTypeString = dialog.selectedSortTypeString;

                if (!selectedAscendString.equals("")){
                    if (selectedAscendString.equals(getString(R.string.text_ascend))){
                        ascending = true;
                    }
                    else if (selectedAscendString.equals(getString(R.string.text_descend))){
                        ascending = false;
                    }
                }

                if (!selectedSortTypeString.equals("")){
                    if (selectedSortTypeString.equals(getString(R.string.text_id))){
                        sortType = CBMessage.SortType.SORT_ID;
                    }
                    if (selectedSortTypeString.equals(getString(R.string.text_title))){
                        sortType = CBMessage.SortType.SORT_TITLE;
                    }
                    if (selectedSortTypeString.equals(getString(R.string.text_username))){
                        sortType = CBMessage.SortType.SORT_USERNAME;
                    }
                    if (selectedSortTypeString.equals(getString(R.string.text_money))){
                        sortType = CBMessage.SortType.SORT_MONEY;
                    }
                    if (selectedSortTypeString.equals(getString(R.string.text_date))){
                        sortType = CBMessage.SortType.SORT_DATE;
                    }
                    if (selectedSortTypeString.equals(getString(R.string.text_id))){
                        sortType = CBMessage.SortType.SORT_ID;
                    }
                }

                requestGetRecords();

                Toast.makeText(getContext(), getString(R.string.text_sort_type),Toast.LENGTH_SHORT).show();
            }
        });
        recordSortTypeDialog.show();
    }

    private void onCreateRecordContextMenu(ContextMenu menu){

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
                    menu.add(groupID, itemID[i], order, getString(R.string.text_view_detail) + " " + getString(R.string.text_todo));
                    break;
                case 3:
                    menu.add(groupID, itemID[i], order, getString(R.string.text_edit) + " " + getString(R.string.text_todo));
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getGroupId() != 0){
            return super.onContextItemSelected(item);
        }
        int pos = -1;
        CBRecord record;
        switch (item.getItemId()) {
            case 1:
                pos = recordsListAdapter.getContextMenuPosition();
                Log.d(getClass().getSimpleName(), String.format("%s:%d", "删除", pos));
                record = Auth.getSingleton().cbGroup.records.get(pos);
                if (record != null){
                    requestRemoveRecord(record);
                }
                break;
            case 2:
                pos = recordsListAdapter.getContextMenuPosition();
                record = Auth.getSingleton().cbGroup.records.get(pos);
                if (record != null){
                    popupDialogViewRecord(record);
                }
                break;
            case 3:
                break;
            default:
                break;
        }
        return super.onContextItemSelected(item);
    }

    private void onAuthenticated(){
        textViewBlank.setText("");
    }

    private void onResponsed(CBMessage.Response response){
        switch (response.getType()){
            case GET_RECORDS:
                CBMessage.ResponseGetRecords responseGetRecords = response.getResponseGetRecords();
                if (responseGetRecords.getResult()){
                    Auth.getSingleton().cbGroup.recordsTotalCount = responseGetRecords.getRecordsCount();
                    Auth.getSingleton().cbGroup.records.clear();
                    for (CBMessage.Record record : responseGetRecords.getRecordsList()){
                        CBRecord cbRecord = new CBRecord();
                        cbRecord.groupName = record.getGroupname();
                        cbRecord.username = record.getUsername();
                        cbRecord.id = record.getId();
                        cbRecord.comment = record.getComment();
                        cbRecord.title = record.getTitle();
                        cbRecord.money = record.getMoney();
                        cbRecord.imagesData = record.getImagesDataList();
                        cbRecord.dateTime = record.getDate();
                        Auth.getSingleton().cbGroup.records.add(cbRecord);
                    }
                }
                updateUIRecordsList();
                break;
            case ADD_RECORD:
                CBMessage.ResponseAddRecord responseAddRecord = response.getResponseAddRecord();
                assert responseAddRecord.getResult();
                requestGetRecords();
                break;
            case REMOVE_RECORD:
                CBMessage.ResponseRemoveRecord responseRemoveRecord = response.getResponseRemoveRecord();
                assert responseRemoveRecord.getResult();
                requestGetRecords();
                break;
            default:
                break;
        }
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
            else{
                updateUIRecordsList();
                requestGetRecords();
            }
        }
        else{
            current = false;
        }
    }

    private void updateUIRecordsList(){
        //accountsRecyclerView.setAdapter(accountsListAdapter);
        recordsListAdapter.notifyDataSetChanged();
        recordsRecyclerView.refreshDrawableState();
        if (Auth.getSingleton().authenticated){
            recordsRecyclerView.setVisibility(View.VISIBLE);
        }
        else{
            recordsRecyclerView.setVisibility(View.INVISIBLE);
        }
        //accountsRecyclerView.setAdapter(accountsListAdapter);
    }

    private void requestAddRecord(CBRecord cbRecord){
        CBMessage.Request.Builder builder = CBMessage.Request.newBuilder();

        CBMessage.RequestAddRecord.Builder requestAddRecordBuilder = CBMessage.RequestAddRecord.newBuilder();

        CBMessage.Record.Builder recordBuilder = CBMessage.Record.newBuilder();
        recordBuilder
                .setGroupname(Auth.getSingleton().cbGroup.groupName)
                .setUsername(Auth.getSingleton().cbGroupMember.username)
                .setTitle(cbRecord.title)
                .setComment(cbRecord.comment)
                .setMoney(cbRecord.money)
                .setDate(cbRecord.dateTime);

        requestAddRecordBuilder
                .setRecord(recordBuilder.build())
                .setGroupname(Auth.getSingleton().cbGroup.groupName);

        builder
                .setRequestAddRecord(requestAddRecordBuilder.build())
                .setType(CBMessage.Type.ADD_RECORD);

        ServerConnection.getSingleton().sendRequest(builder);
    }

    private void requestRemoveRecord(CBRecord cbRecord){
        CBMessage.Request.Builder builder = CBMessage.Request.newBuilder();

        CBMessage.RequestRemoveRecord.Builder requestRemoveRecordBuilder = CBMessage.RequestRemoveRecord.newBuilder();

        requestRemoveRecordBuilder
                .setRecordId(cbRecord.id)
                .setGroupname(Auth.getSingleton().cbGroup.groupName);

        builder
                .setRequestRemoveRecord(requestRemoveRecordBuilder.build())
                .setType(CBMessage.Type.REMOVE_RECORD);

        ServerConnection.getSingleton().sendRequest(builder);
    }

    public void requestGetRecords(){
        CBMessage.Request.Builder builder = CBMessage.Request.newBuilder();

        CBMessage.RequestGetRecords.Builder requestGetRecordsBuilder = CBMessage.RequestGetRecords.newBuilder();

        requestGetRecordsBuilder
                .setAscending(ascending)
                .setSortType(sortType)
                .setPageIdx(searchPageIDX)
                .setPageSize(searchPageSize)
                .setUsername(Auth.getSingleton().cbGroupMember.username)
                .setOnlyCount(false)
                .setGroupname(Auth.getSingleton().cbGroup.groupName);

        builder
                .setRequestGetRecords(requestGetRecordsBuilder.build())
                .setType(CBMessage.Type.GET_RECORDS);

        ServerConnection.getSingleton().sendRequest(builder);
    }

}
