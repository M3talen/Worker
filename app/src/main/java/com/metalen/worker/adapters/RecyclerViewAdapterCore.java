package com.metalen.worker.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.metalen.worker.classes.DataRecord;

import java.util.List;

/**
 * Created by Metalen on 6.2.2015..
 */
public class RecyclerViewAdapterCore extends RecyclerView.Adapter<RecyclerViewAdapterNorm.ViewHolder>{

    protected List<DataRecord> mDataset;
    protected List<SwipedState> mItemSwipedStates;
    protected float mSum = 0;

    public static final int LAST_POSITION = -1 ;

    protected enum SwipedState {
        SHOWING_PRIMARY_CONTENT,
        SHOWING_SECONDARY_CONTENT
    }

    public void addItem(DataRecord s, int position) {
        position = ((position == LAST_POSITION) ? getItemCount() : position);
        mDataset.add(position,s);
        mItemSwipedStates.add(position, SwipedState.SHOWING_PRIMARY_CONTENT);
        notifyItemInserted(position);

        mSum = 0;
        for(int i = 0; i < mDataset.size(); i++)
            mSum = mSum + Float.parseFloat(mDataset.get(i).getDATA_1());
        mSum = mSum / (mDataset.size());
    }

    public void removeItem(int position){
        if (position == LAST_POSITION && getItemCount()>0)
            position = getItemCount();
        if (position > LAST_POSITION && position < getItemCount()) {
            mDataset.remove(position);
            mItemSwipedStates.remove(position);
            //notifyItemRemoved(position); //Error ??
        }
        mSum = 0;
        for(int i = 0; i < mDataset.size(); i++)
            mSum = mSum +  Float.parseFloat(mDataset.get(i).getDATA_1());
        mSum = mSum / (mDataset.size());
        notifyDataSetChanged();
    }



    public static class ViewHolder extends RecyclerView.ViewHolder {
        public View mView;

        public ViewHolder(View v) {
            super(v);
            mView = v;
        }
    }

    @Override
    public RecyclerViewAdapterNorm.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerViewAdapterNorm.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }


}
