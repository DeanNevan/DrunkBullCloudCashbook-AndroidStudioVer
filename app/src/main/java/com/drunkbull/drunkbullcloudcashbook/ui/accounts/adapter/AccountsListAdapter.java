package com.drunkbull.drunkbullcloudcashbook.ui.accounts.adapter;

import android.content.Context;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.drunkbull.drunkbullcloudcashbook.R;
import com.drunkbull.drunkbullcloudcashbook.pojo.CBGroup;
import com.drunkbull.drunkbullcloudcashbook.singleton.GSignalManager;
import com.drunkbull.drunkbullcloudcashbook.singleton.NoSuchGSignalException;
import com.drunkbull.drunkbullcloudcashbook.utils.Para2TextFormatter;

import java.util.List;

public class AccountsListAdapter extends RecyclerView.Adapter<AccountsListAdapter.MyViewHolder> {
    private List<CBGroup.CBGroupMember> accountsList;
    private Context context;
    private LayoutInflater inflater;

    private int contextMenuPosition = -1;

    public int getContextMenuPosition() {
        return contextMenuPosition;
    }

    public void setContextMenuPosition(int contextMenuPosition) {
        this.contextMenuPosition = contextMenuPosition;
    }

    public AccountsListAdapter(Context context, List<CBGroup.CBGroupMember> data){
        GSignalManager.getSingleton().addGSignal(this, "create_account_context_menu");
        this.context = context;
        this.accountsList = data;
        inflater = LayoutInflater.from(context);

    }

    public CBGroup.CBGroupMember getAccountViaPosition(int pos){
        return accountsList.get(pos);
    }

    @Override
    public int getItemCount() {
        return accountsList.size();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_account, parent, false);
        MyViewHolder holder= new MyViewHolder(view);
        return holder;

    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        CBGroup.CBGroupMember member = accountsList.get(position);
        Log.d("test", String.valueOf(position));

        holder.textViewID.setText((position + 1) + ".");
        holder.textViewUsername.setText(member.username);
        holder.textViewNickname.setText(member.nickname);
        holder.textViewAuthority.setText(Para2TextFormatter.getCBGroupMemberAuthorityStringRID(member));
        holder.textViewCounter.setText(R.string.text_space);

        holder.itemView.setLongClickable(true);
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                setContextMenuPosition(holder.getLayoutPosition());
                return false;
            }
        });


//        if(position%2==0){
//            //holder.tv.setBackgroundColor(Color.BLUE);
//            holder.v.setBackgroundColor(Color.GRAY);
//
//        }
//        holder.tv.setText(book.getName());
//        holder.msg.setText(book.getZt());
    }

    @Override
    public void onViewRecycled(MyViewHolder holder) {
        super.onViewRecycled(holder);
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        TextView textViewID;
        TextView textViewUsername;
        TextView textViewNickname;
        TextView textViewAuthority;
        TextView textViewCounter;
        public MyViewHolder(View view) {
            super(view);
            textViewID = (TextView) view.findViewById(R.id.text_view_account_id);
            textViewUsername = (TextView) view.findViewById(R.id.text_view_account_username);
            textViewNickname = (TextView) view.findViewById(R.id.text_view_account_nickname);
            textViewAuthority = (TextView) view.findViewById(R.id.text_view_account_authority);
            textViewCounter = (TextView) view.findViewById(R.id.text_view_account_counter);

            view.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            //注意传入的menuInfo为null
            int pos = getContextMenuPosition();
            if (pos == -1){
                return;
            }
            CBGroup.CBGroupMember member = accountsList.get(getContextMenuPosition());
            Log.i("Adapter", "onCreateContextMenu: " + getContextMenuPosition());
            menu.setHeaderTitle(member.username);
            try {
                GSignalManager.getSingleton().emitGSignal(AccountsListAdapter.this, "create_account_context_menu", new Class[]{ContextMenu.class}, new Object[]{menu});
            } catch (NoSuchGSignalException e) {
                e.printStackTrace();
            }
        }

    }


}
