package com.drunkbull.drunkbullcloudcashbook.ui.records;

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
import com.drunkbull.drunkbullcloudcashbook.pojo.CBRecord;
import com.drunkbull.drunkbullcloudcashbook.singleton.GSignalManager;
import com.drunkbull.drunkbullcloudcashbook.singleton.NoSuchGSignalException;
import com.drunkbull.drunkbullcloudcashbook.utils.Para2TextFormatter;
import com.drunkbull.drunkbullcloudcashbook.utils.data.TimeUtil;

import java.util.Date;
import java.util.List;

public class RecordsListAdapter extends RecyclerView.Adapter<RecordsListAdapter.MyViewHolder> {
    private List<CBRecord> recordsList;
    private Context context;
    private LayoutInflater inflater;

    private int contextMenuPosition = -1;

    public int getContextMenuPosition() {
        return contextMenuPosition;
    }

    public void setContextMenuPosition(int contextMenuPosition) {
        this.contextMenuPosition = contextMenuPosition;
    }

    public RecordsListAdapter(Context context, List<CBRecord> data){
        GSignalManager.getSingleton().addGSignal(this, "create_record_context_menu");
        this.context = context;
        this.recordsList = data;
        inflater = LayoutInflater.from(context);

    }

    public CBRecord getRecordViaPosition(int pos){
        return recordsList.get(pos);
    }

    @Override
    public int getItemCount() {
        return recordsList.size();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_record, parent, false);
        MyViewHolder holder= new MyViewHolder(view);
        return holder;

    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        CBRecord record = recordsList.get(position);
        Log.d("test", String.valueOf(position));

        holder.textViewID.setText(record.id + ".");
        holder.textViewTitle.setText(record.title);
        holder.textViewUsername.setText(record.username);
        holder.textViewMoney.setText(String.valueOf(record.money));
        if (record.money < 0){
            holder.textViewMoney.setTextColor(context.getResources().getColor(R.color.green));
        }
        else if (record.money > 0){
            holder.textViewMoney.setTextColor(context.getResources().getColor(R.color.red));
        }

        long timestamp = record.dateTime;
        String timeString = TimeUtil.stampToDate(timestamp);
        holder.textViewDate.setText(timeString);

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
        TextView textViewTitle;
        TextView textViewUsername;
        TextView textViewMoney;
        TextView textViewDate;
        public MyViewHolder(View view) {
            super(view);
            textViewID = (TextView) view.findViewById(R.id.text_view_record_id);
            textViewTitle = (TextView) view.findViewById(R.id.text_view_record_title);
            textViewUsername = (TextView) view.findViewById(R.id.text_view_record_username);
            textViewMoney = (TextView) view.findViewById(R.id.text_view_record_money);
            textViewDate = (TextView) view.findViewById(R.id.text_view_record_datetime);

            view.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            //注意传入的menuInfo为null
            int pos = getContextMenuPosition();
            if (pos == -1){
                return;
            }
            CBRecord record = recordsList.get(getContextMenuPosition());
            Log.i("Adapter", "onCreateContextMenu: " + getContextMenuPosition());
            menu.setHeaderTitle(record.title);
            try {
                GSignalManager.getSingleton().emitGSignal(RecordsListAdapter.this, "create_record_context_menu", new Class[]{ContextMenu.class}, new Object[]{menu});
            } catch (NoSuchGSignalException e) {
                e.printStackTrace();
            }
        }

    }


}
