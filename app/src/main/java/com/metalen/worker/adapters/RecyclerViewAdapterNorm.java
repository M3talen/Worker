package com.metalen.worker.adapters;

import android.content.res.Resources;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.CardView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.metalen.worker.R;
import com.metalen.worker.classes.DataRecord;
import com.metalen.worker.fragments.NormFragment;

import java.util.ArrayList;
import java.util.List;

public class RecyclerViewAdapterNorm extends RecyclerViewAdapterCore{

    private Button mButton1,mButton2,mButton3;
    private NormFragment mFragment;

    public RecyclerViewAdapterNorm(List<DataRecord> dataSet, NormFragment fragment) {
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
        ((TextView) holder.mView.findViewById(R.id.txt3)).setText(mDataset.get(position).getDATA_1());
        ((ViewPager) holder.mView).setCurrentItem(mItemSwipedStates.get(position).ordinal());

        ImageView imageView = (ImageView) holder.mView.findViewById(R.id.norm_icon);
        if( Float.parseFloat(mDataset.get(position).getDATA_1()) > mSum)
            imageView.setBackgroundResource(R.drawable.ic_expand_less_grey600_24dp);
        else if (Float.parseFloat(mDataset.get(position).getDATA_1()) < mSum)
            imageView.setBackgroundResource(R.drawable.ic_expand_more_grey600_24dp);
        else
            imageView.setBackgroundResource(R.drawable.ic_remove_grey600_24dp);

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
        ((CardView) holder.mView.findViewById(R.id.primaryContentCardView)).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                ((ViewPager) holder.mView).setCurrentItem(1, true);
                return false;
            }
        });
    }

}