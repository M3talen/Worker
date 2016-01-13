package com.metalen.worker.adapters;

import android.content.res.Resources;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.CardView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.metalen.worker.R;
import com.metalen.worker.classes.DataRecord;
import com.metalen.worker.fragments.OverhoursFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Metalen on 15.2.2015..
 */
public class RecyclerViewAdapterOverhours extends RecyclerViewAdapterCore {

    protected int mMinSum = 0;
    protected int mHourSum = 0;
    private Button mButton1, mButton2, mButton3;
    private OverhoursFragment mFragment;

    public RecyclerViewAdapterOverhours(List<DataRecord> dataSet, OverhoursFragment fragment) {
        mFragment = fragment;
        mItemSwipedStates = new ArrayList<>();
        mDataset = dataSet;
        for (int i = 0; i < dataSet.size(); i++) {
            mItemSwipedStates.add(i, RecyclerViewAdapterCore.SwipedState.SHOWING_PRIMARY_CONTENT);
        }
    }

    public void addItem(DataRecord s, int position) {
        position = ((position == LAST_POSITION) ? getItemCount() : position);
        mDataset.add(position, s);
        mItemSwipedStates.add(position, RecyclerViewAdapterCore.SwipedState.SHOWING_PRIMARY_CONTENT);
        notifyItemInserted(position);
    }

    public void removeItem(int position) {
        if (position == LAST_POSITION && getItemCount() > 0)
            position = getItemCount();
        if (position > LAST_POSITION && position < getItemCount()) {
            mDataset.remove(position);
            mItemSwipedStates.remove(position);
            //notifyItemRemoved(position); //Error ??
        }
        notifyDataSetChanged();
    }

    @Override
    public RecyclerViewAdapterCore.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                 int viewType) {
        ViewPager v = (ViewPager) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_norm, parent, false);
        ViewPagerAdapter adapter = new ViewPagerAdapter();

        ((ViewPager) v.findViewById(R.id.viewPager)).setAdapter(adapter);

        DisplayMetrics displayMetrics = Resources.getSystem().getDisplayMetrics();
        v.getLayoutParams().width = displayMetrics.widthPixels;
        v.requestLayout();

        return new RecyclerViewAdapterCore.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final RecyclerViewAdapterCore.ViewHolder holder, final int position) {
        ((TextView) holder.mView.findViewById(R.id.txt)).setText(mDataset.get(position).getDATE());
        ((TextView) holder.mView.findViewById(R.id.txt3)).setText(mDataset.get(position).getDATA_1() + " - " + mDataset.get(position).getDATA_2());
        ((ViewPager) holder.mView).setCurrentItem(mItemSwipedStates.get(position).ordinal());

        TextView tvType = (TextView) holder.mView.findViewById(R.id.txt2);
        TextView tvDate = (TextView) holder.mView.findViewById(R.id.txt4);
        if (mDataset.get(position).getDATA_3().equals(DataRecord.OHMode.USED.toString())) {
            tvType.setText(R.string.text_used);
            tvDate.setText(mDataset.get(position).getDATA_4());
        } else if (mDataset.get(position).getDATA_3().equals(DataRecord.OHMode.UNUSED.toString())) {
            tvType.setText(R.string.text_unused);
            tvDate.setText("");
        } else if (mDataset.get(position).getDATA_3().equals(DataRecord.OHMode.PAID.toString())) {
            tvType.setText(R.string.text_paid);
            tvDate.setText(mDataset.get(position).getDATA_4());
        }

        ((ViewPager) holder.mView).setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            int previousPagePosition = 0;

            @Override
            public void onPageScrolled(int pagePosition, float positionOffset, int positionOffsetPixels) {
                if (pagePosition == previousPagePosition)
                    return;

                switch (pagePosition) {
                    case 0:
                        mItemSwipedStates.set(position, RecyclerViewAdapterCore.SwipedState.SHOWING_PRIMARY_CONTENT);
                        break;
                    case 1:
                        mItemSwipedStates.set(position, RecyclerViewAdapterCore.SwipedState.SHOWING_SECONDARY_CONTENT);

                        mButton2 = (Button) holder.mView.findViewById(R.id.btn2);
                        mButton2.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                mFragment.ButtonEdit(position);
                            }
                        });

                        mButton3 = (Button) holder.mView.findViewById(R.id.btn3);
                        mButton3.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                mFragment.ButtonDelete(position);
                            }
                        });
                        break;

                }
                previousPagePosition = pagePosition;
            }

            @Override
            public void onPageSelected(int pagePosition) {//ne dela
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        ((CardView) holder.mView.findViewById(R.id.primaryContentCardView)).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                ((ViewPager) holder.mView).setCurrentItem(1, true);
                return false;
            }
        });
    }

    public int getWorkHours_Minutes() {
        return mMinSum;
    }

    public int getWorkHours_Hours() {
        return mHourSum;
    }
}
