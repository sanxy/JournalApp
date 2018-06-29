package com.sanxynet.journalapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;


public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.JournalViewHolder> {
    private Context mContext;
    private ArrayList<Journal> mJournalArrayList;
    private ItemClickListener mListener;
    private static View mItemView;


    public RecyclerAdapter(Context mContext, ArrayList<Journal> mJournalArrayList, ItemClickListener listener) {
        this.mContext = mContext;
        this.mJournalArrayList = mJournalArrayList;
        this.mListener = listener;
    }

    @NonNull
    @Override
    public JournalViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.layout_journal_item,parent,false);
        return new JournalViewHolder(itemView,mListener);
    }

    
    @Override
    public void onBindViewHolder(JournalViewHolder holder, int position) {
        Journal journal = mJournalArrayList.get(position);
        holder.mJournalIconTextView.setText(journal.getIcon());
        holder.mJournalNameTextView.setText(journal.getJournalName());
        holder.mJournalCategoryTextView.setText(journal.getJournalCategory().getCategory());
        holder.mJournalCardView.setBackgroundColor(setItemBgColor(journal.getJournalPriority()));

    }

    private int setItemBgColor(int journalPriority) {
        switch (journalPriority){
            case 0: return (ContextCompat.getColor(mContext,R.color.priority0));
            case 1: return (ContextCompat.getColor(mContext,R.color.priority1));
            case 2: return (ContextCompat.getColor(mContext,R.color.priority2));
            case 3: return (ContextCompat.getColor(mContext,R.color.priority3));
            case 4: return (ContextCompat.getColor(mContext,R.color.priority4));
            case 5: return (ContextCompat.getColor(mContext,R.color.priority5));
            default: return (ContextCompat.getColor(mContext,R.color.priority0));
        }
    }

    @Override
    public int getItemCount() {
        return mJournalArrayList.size();
    }

    public static class JournalViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        LinearLayout layout;
        TextView mJournalIconTextView;
        TextView mJournalNameTextView;
        TextView mJournalCategoryTextView;
        ItemClickListener itemClickListener;
        CardView mJournalCardView;

        public JournalViewHolder(View itemView,ItemClickListener itemClickListener) {
            super(itemView);
            RecyclerAdapter.mItemView = itemView;
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            this.itemClickListener = itemClickListener;

            layout = itemView.findViewById(R.id.journal_layout);
            layout.getLayoutParams().width = (int) (Utils.getScreenWidth(itemView.getContext()) / 3.15);
            mJournalIconTextView = itemView.findViewById(R.id.journal_icon_textview);
            mJournalNameTextView = itemView.findViewById(R.id.journal_name_textview);
            mJournalCategoryTextView = itemView.findViewById(R.id.journal_category_textview);
            mJournalCardView = itemView.findViewById(R.id.journal_item_card);
        }

        @Override
        public void onClick(View view) {
            int id = view.getId();
            int position = getAdapterPosition();

            if(position!=RecyclerView.NO_POSITION){

                if(id == R.id.journal_layout){
                    itemClickListener.onItemClick(view,position);
                }
            }
        }

        @Override
        public boolean onLongClick(View v) {
            int id = v.getId();
            int position = getAdapterPosition();

            if(position!=RecyclerView.NO_POSITION){

                if(id == R.id.journal_layout){
                    itemClickListener.onItemLongClick(v,position);
                }
            }
            return true;
        }
    }
}

interface ItemClickListener{
    void onItemClick(View view, int position);
    void onItemLongClick(View view, int position);
}
