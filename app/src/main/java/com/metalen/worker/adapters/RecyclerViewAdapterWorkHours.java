package com.metalen.worker.adapters;

import android.content.res.Resources;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.metalen.worker.R;
import com.metalen.worker.classes.DataRecord;
import com.metalen.worker.fragments.WorkHoursFragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Metalen on 9.2.2015..
 */
public class RecyclerViewAdapterWorkHours extends RecyclerViewAdapterCore {

    private Button mButton1,mButton2,mButton3;
    private WorkHoursFragment mFragment;

    protected int mMinSum = 0;
    protected int mHourSum = 0;

    public void addItem(DataRecord s, int position) {
        position = ((position == LAST_POSITION) ? getItemCount() : position);
        mDataset.add(position,s);
        mItemSwipedStates.add(position, SwipedState.SHOWING_PRIMARY_CONTENT);
        notifyItemInserted(position);
    }

    public void removeItem(int position){
        if (position == LAST_POSITION && getItemCount()>0)
            position = getItemCount();
        if (position > LAST_POSITION && position < getItemCount()) {
            mDataset.remove(position);
            mItemSwipedStates.remove(position);
            //notifyItemRemoved(position); //Error ??
        }
        notifyDataSetChanged();
    }

    public RecyclerViewAdapterWorkHours(List<DataRecord> dataSet, WorkHoursFragment fragment) {
        mFragment = fragment;
        mItemSwipedStates = new ArrayList<>();
        mDataset = dataSet;
        for (int i = 0; i < dataSet.size(); i++) {
            mItemSwipedStates.add(i, SwipedState.SHOWING_PRIMARY_CONTENT);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,
                                         int viewType) {
        ViewPager v = (ViewPager) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_norm, parent, false);
        ViewPagerAdapter adapter = new ViewPagerAdapter();

        ((ViewPager) v.findViewById(R.id.viewPager)).setAdapter(adapter);

        DisplayMetrics displayMetrics = Resources.getSystem().getDisplayMetrics();
        v.getLayoutParams().width = displayMetrics.widthPixels;
        v.requestLayout();

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        ((TextView) holder.mView.findViewById(R.id.txt)).setText(mDataset.get(position).getDATE());
        ((TextView) holder.mView.findViewById(R.id.txt3)).setText(mDataset.get(position).getDATA_1() + " - " + mDataset.get(position).getDATA_2());
        ((TextView) holder.mView.findViewById(R.id.txt2)).setText(getTimeDiference(mDataset.get(position).getDATA_1(), mDataset.get(position).getDATA_2()));
        ((ViewPager) holder.mView).setCurrentItem(mItemSwipedStates.get(position).ordinal());

        ((ViewPager) holder.mView).setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            int previousPagePosition = 0;

            @Override
            public void onPageScrolled(int pagePosition, float positionOffset, int positionOffsetPixels) {
                if (pagePosition == previousPagePosition)
                    return;

                switch (pagePosition) {
                    case 0:
                        mItemSwipedStates.set(position, SwipedState.SHOWING_PRIMARY_CONTENT);
                        break;
                    case 1:
                        mItemSwipedStates.set(position, SwipedState.SHOWING_SECONDARY_CONTENT);

                        mButton2 = (Button) holder.mView.findViewById(R.id.btn2);
                        mButton2.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                mFragment.ButtonEdit(position);
                            }
                        });

                        mButton3= (Button) holder.mView.findViewById(R.id.btn3);
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
    }

    private String getTimeDiference(String time1, String time2) {
        int tHours = 0;
        int tMins = 0;
        Date date1 = null;
        Date date2 = null;
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        try {
            date1 = format.parse(time1);
            date2 = format.parse(time2);
        } catch (ParseException e) {
        }
        long difference = date2.getTime() - date1.getTime();
        int Hours = (int) (difference / (1000 * 60 * 60));
        int Mins = (int) (difference / (1000 * 60)) % 60;
        if (Hours < 0) Hours = Hours + 24;
        if (Mins < 0) Mins = Mins + 60;

        return pad(Hours) + ":" + pad(Mins);
    }

    protected static String pad(int c) {
        if (c >= 10)
            return String.valueOf(c);
        else
            return "0" + String.valueOf(c);
    }

    public int getWorkHours_Minutes() {
        return mMinSum;
    }
    public int getWorkHours_Hours() {
        return mHourSum;
    }
}
