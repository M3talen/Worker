package com.metalen.worker.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.*;

import java.util.Calendar;

/**
 * Created by Metalen on 24.1.2015..
 */
public class FragmentCore extends Fragment {

    //FILTER
    protected boolean _AutoFilter;
    protected String _SortingType;
    protected boolean _YearFilterEnabled;
    protected boolean _MonthFilterEnabled;
    protected int _YearFilterValue;
    protected int _MonthFilterValue;
    protected boolean _DisableFilter;

    protected static String pad(int c) {
        if (c >= 10)
            return String.valueOf(c);
        else
            return "0" + String.valueOf(c);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    protected void revealShow(View rootView, boolean reveal, final AlertDialog dialog, int dialogViewID, int FABViewID, boolean a) {
        final View view = rootView.findViewById(dialogViewID);
        int w = view.getWidth();
        int h = view.getHeight();
        int tx = (int) view.getX();
        int ty = (int) view.getY();
        int ix = (int) view.findViewById(FABViewID).getX();
        int iy = (int) view.findViewById(FABViewID).getY();
        int ih = view.findViewById(FABViewID).getHeight();
        int iw = view.findViewById(FABViewID).getWidth();

        // float maxRadius = (float) Math.sqrt(w * 0.8*w + h * 0.8*h);
        float maxRadius = (float) Math.sqrt(w * w + h * h) + 10;

        if (reveal) {
            //Animator revealAnimator = ViewAnimationUtils.createCircularReveal(view, w-(w/2+ix), h-(h/2+iy), 0, maxRadius);
            Animator revealAnimator = ViewAnimationUtils.createCircularReveal(view, w / 2, h / 2, 0, maxRadius);

            view.setVisibility(View.VISIBLE);
            revealAnimator.start();

        } else {
            Animator anim = ViewAnimationUtils.createCircularReveal(view, ix + (ih / 2), iy + (iw / 2), maxRadius, ih).setDuration(300);
            //Animator anim = ViewAnimationUtils.createCircularReveal(view, w / 2, h / 2, maxRadius, 0).setDuration(300);
            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    dialog.dismiss();
                    view.setVisibility(View.INVISIBLE);
                }
            });

            anim.start();
        }

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    protected void revealShow(View rootView, boolean reveal, final AlertDialog dialog, int dialogViewID, int FABViewID) {
        final View view = rootView.findViewById(dialogViewID);
        int w = view.getWidth();
        int h = view.getHeight();
        int tx = (int) view.getX();
        int ty = (int) view.getY();
        int ix = (int) view.findViewById(FABViewID).getX();
        int iy = (int) view.findViewById(FABViewID).getY();
        int ih = view.findViewById(FABViewID).getHeight();
        int iw = view.findViewById(FABViewID).getWidth();

        // float maxRadius = (float) Math.sqrt(w * 0.8*w + h * 0.8*h);
        float maxRadius = (float) Math.sqrt(w * w + h * h) + 10;

        if (reveal) {
            //Animator revealAnimator = ViewAnimationUtils.createCircularReveal(view, w-(w/2+ix), h-(h/2+iy), 0, maxRadius);
            Animator revealAnimator = ViewAnimationUtils.createCircularReveal(view, w / 2, h / 2, 0, maxRadius);

            view.setVisibility(View.VISIBLE);
            revealAnimator.start();

        } else {
            Animator anim = ViewAnimationUtils.createCircularReveal(view, ix + (ih / 2), iy + (iw / 2), maxRadius, 0).setDuration(300);
            //Animator anim = ViewAnimationUtils.createCircularReveal(view, w / 2, h / 2, maxRadius, 0).setDuration(300);
            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    dialog.dismiss();
                    view.setVisibility(View.INVISIBLE);
                }
            });

            anim.start();
        }

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    protected void revealShow(View rootView, boolean reveal, final AlertDialog dialog, int dialogViewID, int FABViewID, int x, int y) {
        final View view = rootView.findViewById(dialogViewID);
        int w = view.getWidth();
        int h = view.getHeight();
        int dx = (int) view.getX();
        int dy = (int) view.getY();
        int tx = x;
        int ty = y;
        int ix = (int) view.findViewById(FABViewID).getX();
        int iy = (int) view.findViewById(FABViewID).getY();
        int ih = view.findViewById(FABViewID).getHeight();
        int iw = view.findViewById(FABViewID).getWidth();

        float maxRadius = (float) Math.sqrt(w * 0.8 * w + h * 0.8 * h);

        if (reveal) {
            Animator revealAnimator = ViewAnimationUtils.createCircularReveal(view, x, y, 0, maxRadius).setDuration(500);

            view.setVisibility(View.VISIBLE);
            revealAnimator.start();

        } else {
            Animator anim = ViewAnimationUtils.createCircularReveal(view, ix + (ih / 2), iy + (iw / 2), maxRadius, 0).setDuration(500);
            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    dialog.dismiss();
                    view.setVisibility(View.INVISIBLE);
                }
            });

            anim.start();
        }

    }

    protected int getPixels(int dipValue) {
        Resources r = getResources();
        int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, r.getDisplayMetrics());
        return px;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    protected void setEnabledLayoutsAuto(CheckBox mAutoFilter, CheckBox mDisableFilter, RelativeLayout mLayout1, RelativeLayout mLayout2, RelativeLayout mLayout3) {

        if (_DisableFilter && _AutoFilter) {
            _AutoFilter = true;
            _DisableFilter = false;
            mAutoFilter.setChecked(true);
            mDisableFilter.setChecked(false);
        }

        if (_AutoFilter == true) {
            mLayout1.setVisibility(View.GONE);
            mLayout2.setVisibility(View.GONE);
            mLayout3.setVisibility(View.GONE);
            mDisableFilter.setEnabled(false);
            mAutoFilter.setEnabled(true);
        } else {
            mLayout1.setVisibility(View.VISIBLE);
            mLayout2.setVisibility(View.VISIBLE);
            mLayout3.setVisibility(View.VISIBLE);
            mAutoFilter.setEnabled(true);
            mDisableFilter.setEnabled(true);
        }
    }

    protected void setEnabledLayoutsDisabler(CheckBox mAutoFilter, CheckBox mDisableFilter, RelativeLayout mLayout1, RelativeLayout mLayout2, RelativeLayout mLayout3) {

        if (_DisableFilter && _AutoFilter) {
            _AutoFilter = true;
            _DisableFilter = false;
            mAutoFilter.setChecked(true);
            mDisableFilter.setChecked(false);
        }
        if (_DisableFilter == true) {
            mLayout1.setVisibility(View.GONE);
            mLayout2.setVisibility(View.GONE);
            mLayout3.setVisibility(View.GONE);
            mDisableFilter.setEnabled(true);
            mAutoFilter.setEnabled(false);
        } else {
            mLayout1.setVisibility(View.VISIBLE);
            mLayout2.setVisibility(View.VISIBLE);
            mLayout3.setVisibility(View.VISIBLE);
            mAutoFilter.setEnabled(true);
            mDisableFilter.setEnabled(true);
        }
    }

    protected void getSettingsForFilter(String DataType, String ACC_USER) {
        final SharedPreferences prefs = getActivity().getSharedPreferences("Worker", Context.MODE_PRIVATE);
        _AutoFilter = prefs.getBoolean("FILTER_" + DataType + "_" + ACC_USER + "_AutoFilter", true);
        _SortingType = prefs.getString("FILTER_" + DataType + "_" + ACC_USER + "_SortingType", "DESC");
        _YearFilterEnabled = prefs.getBoolean("FILTER_" + DataType + "_" + ACC_USER + "_YearFilterEnabled", false);
        _MonthFilterEnabled = prefs.getBoolean("FILTER_" + DataType + "_" + ACC_USER + "_MonthFilterEnabled", false);
        _YearFilterValue = prefs.getInt("FILTER_" + DataType + "_" + ACC_USER + "_YearFilterValue", 0);
        _MonthFilterValue = prefs.getInt("FILTER_" + DataType + "_" + ACC_USER + "_MonthFilterValue", 0);
        _DisableFilter = prefs.getBoolean("FILTER_" + DataType + "_" + ACC_USER + "_DisableFilter", false);

        if (_AutoFilter) setupAutoFilter();

    }

    protected void setupAutoFilter() {
        Calendar c = Calendar.getInstance();
        _SortingType = "DESC";
        _DisableFilter = false;
        _YearFilterEnabled = true;
        _YearFilterValue = c.get(Calendar.YEAR);
        _MonthFilterEnabled = true;
        _MonthFilterValue = c.get(Calendar.MONTH) + 1;
    }

    protected void setupFilterParameters(CheckBox mAutoFilter, CheckBox mYearFilter, CheckBox mMonthFilter, CheckBox mDisableFilter, RadioButton mSortingTypeASC, RadioButton mSortingTypeDESC, EditText mYearFilterValue, EditText mMonthFilterValue, RelativeLayout mLayout1, RelativeLayout mLayout2, RelativeLayout mLayout3) {
        if (_DisableFilter)
            setEnabledLayoutsDisabler(mAutoFilter, mDisableFilter, mLayout1, mLayout2, mLayout3);
        if (_AutoFilter)
            setEnabledLayoutsAuto(mAutoFilter, mDisableFilter, mLayout1, mLayout2, mLayout3);
        //
        mAutoFilter.setChecked(_AutoFilter);
        mDisableFilter.setChecked(_DisableFilter);
        mYearFilter.setChecked(_YearFilterEnabled);
        mMonthFilter.setChecked(_MonthFilterEnabled);
        if (_SortingType.equals("DESC")) {
            mSortingTypeDESC.setChecked(true);
            mSortingTypeASC.setChecked(false);
        } else {
            mSortingTypeDESC.setChecked(false);
            mSortingTypeASC.setChecked(true);
        }
        mYearFilterValue.setText(_YearFilterValue + "");
        mMonthFilterValue.setText(_MonthFilterValue + "");
    }

    protected void setupFilterWorking(final CheckBox mAutoFilter, CheckBox mYearFilter, final CheckBox mMonthFilter, final CheckBox mDisableFilter, RadioButton mSortingTypeASC, RadioButton mSortingTypeDESC, final EditText mYearFilterValue, final EditText mMonthFilterValue, final RelativeLayout mLayout1, final RelativeLayout mLayout2, final RelativeLayout mLayout3) {
        mAutoFilter.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                _AutoFilter = !_AutoFilter;
                setEnabledLayoutsAuto(mAutoFilter, mDisableFilter, mLayout1, mLayout2, mLayout3);
            }
        });
        mDisableFilter.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                _DisableFilter = !_DisableFilter;
                setEnabledLayoutsDisabler(mAutoFilter, mDisableFilter, mLayout1, mLayout2, mLayout3);
            }
        });
        mSortingTypeDESC.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                _SortingType = "ASC";
            }
        });
        mSortingTypeASC.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                _SortingType = "DESC";
            }
        });
        mYearFilter.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                _YearFilterEnabled = !_YearFilterEnabled;
            }
        });
        mMonthFilter.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                _MonthFilterEnabled = !_MonthFilterEnabled;
            }
        });
        mYearFilterValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!mYearFilterValue.getText().toString().isEmpty())
                    _YearFilterValue = Integer.parseInt(mYearFilterValue.getText().toString());
            }
        });
        mMonthFilterValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!mMonthFilterValue.getText().toString().isEmpty())
                    _MonthFilterValue = Integer.parseInt(mMonthFilterValue.getText().toString());
            }
        });
    }


}
