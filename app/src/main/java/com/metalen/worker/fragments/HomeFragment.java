package com.metalen.worker.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.SQLException;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Html;
import android.util.Log;
import android.view.*;
import android.widget.*;
import com.metalen.worker.MainActivity;
import com.metalen.worker.R;
import com.metalen.worker.SQL.SQLHandler;
import com.metalen.worker.classes.DataRecord;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.format.ArrayWeekDayFormatter;
import org.eazegraph.lib.charts.ValueLineChart;
import org.eazegraph.lib.models.ValueLinePoint;
import org.eazegraph.lib.models.ValueLineSeries;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


/**
 * Created by Metalen on 27.12.2014..
 */
public class HomeFragment extends FragmentCore {

    SQLHandler mDB;
    ValueLineChart mChartNorm;
    TextView mStat1, mStat2, mNormStat, mHolidayStat, mWHStat, mOHStat, mSLStat ,mInterventionStat;
    ArrayList<DataRecord> _Norme = null;
    ArrayList<DataRecord> _Holidays = null;
    ArrayList<DataRecord> _WorkHours = null;
    ArrayList<DataRecord> _SickLave = null;
    ArrayList<DataRecord> _InterventionHours = null;

    String DataType = "HOME";

    private int ACC = 1;
    private String ACC_USER = "";
    private Drawable ACC_Cover;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_home, container, false);

        mChartNorm = (ValueLineChart) fragmentView.findViewById(R.id.home_chart_norm);
        mStat1 = (TextView) fragmentView.findViewById(R.id.home_textView_1);
        mStat2 = (TextView) fragmentView.findViewById(R.id.home_textView_2);

        mNormStat = (TextView) fragmentView.findViewById(R.id.home_et_1);
        mHolidayStat = (TextView) fragmentView.findViewById(R.id.home_et_2);
        mWHStat = (TextView) fragmentView.findViewById(R.id.home_et_3);
        mOHStat = (TextView) fragmentView.findViewById(R.id.home_et_4);
        mSLStat = (TextView) fragmentView.findViewById(R.id.home_et_5);
        mInterventionStat = (TextView) fragmentView.findViewById(R.id.home_et_6);

        mDB = new SQLHandler(getActivity());

        SharedPreferences prefs = getActivity().getSharedPreferences("Worker", Context.MODE_PRIVATE);
        ACC = prefs.getInt("ACC", 1);
        if (ACC == 1) {
            ACC_USER = DataRecord.Account.ACC1.toString();
            ACC_Cover = ((MainActivity) getActivity()).getUser(0).getBackground();
        } else {
            ACC_USER = DataRecord.Account.ACC2.toString();
            ACC_Cover = ((MainActivity) getActivity()).getUser(1).getBackground();
        }

        this.setHasOptionsMenu(true);
        getSettingsForFilter(DataType, ACC_USER);

        buildChart();

        Calendar nextYear = Calendar.getInstance();
        nextYear.add(Calendar.YEAR, 1);
        Calendar prevYear = Calendar.getInstance();
        prevYear.add(Calendar.YEAR, -1);

        MaterialCalendarView calendar = (MaterialCalendarView) fragmentView.findViewById(R.id.calendar_view);
        calendar.setWeekDayFormatter(new ArrayWeekDayFormatter(getResources().getTextArray(R.array.custom_weekdays)));
        calendar.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                Calendar cal = Calendar.getInstance();
                cal.set(date.getYear(), date.getMonth(), date.getDay());
                getDataForDate(cal.getTime());
            }
        });

        final Date today = new Date();
        calendar.setDateSelected(today, true);
        calendar.postDelayed(new Runnable() {
            @Override
            public void run() {
                getDataForDate(today);
            }
        }, 300);


        String stats1 = String.format(this.getString(R.string.text_home_1),
                String.format("%.2f", (float) getAverageNorm()),
                String.format("%.2f", (float) getUsedHolidays()),
                getWorkedHours(),
                String.format("%.2f", (float) getSickLeaveHours()));

        String stats2 = String.format(this.getString(R.string.text_home_2),
                getOverhours(DataRecord.OHMode.PAID.toString()),
                getOverhours(DataRecord.OHMode.USED.toString()),
                getOverhours(DataRecord.OHMode.UNUSED.toString()),
                getInterventionHours());

        mStat1.setText(Html.fromHtml(stats1));
        mStat2.setText(Html.fromHtml(stats2));
       /* mNormAvg.setText("Average norm is " + getAverageNorm() + System.getProperty("line.separator")
                + "Used holidays " + getUsedHolidays() + System.getProperty("line.separator")
                + "Worked hours " +  getWorkedHours());*/
        return fragmentView;
    }

    private void getDataForDate(Date tdate) {

        SimpleDateFormat form = new SimpleDateFormat("dd/MM/yyyy");
        String final_date = form.format(tdate);

        mOHStat.setVisibility(View.GONE);
        mNormStat.setVisibility(View.GONE);
        mHolidayStat.setVisibility(View.GONE);
        mWHStat.setVisibility(View.GONE);
        mSLStat.setVisibility(View.GONE);
        mInterventionStat.setVisibility(View.GONE);

        try {
            List<DataRecord> Tab3 = mDB.getRecordsByDate(final_date, "ASC", ACC_USER);
            for (int i = 0; i < Tab3.size(); ++i) {
                DataRecord x = Tab3.get(i);
                Log.d("aaaa", x.getTYPE() + "  " + x.getDATA_1());
                if (x.getTYPE().equals(DataRecord.Type.NORMA.toString())) {
                    mNormStat.setVisibility(View.VISIBLE);
                    mNormStat.setText(x.getDATA_1());
                }
                if (x.getTYPE().equals(DataRecord.Type.HOLIDAYS.toString())) {
                    mHolidayStat.setVisibility(View.VISIBLE);
                    mHolidayStat.setText(x.getDATA_1() + " h");
                }
                if (x.getTYPE().equals(DataRecord.Type.WORK_HOURS.toString())) {
                    mWHStat.setVisibility(View.VISIBLE);
                    mWHStat.setText(x.getDATA_1() + " - " + x.getDATA_2());
                }
                if (x.getTYPE().equals(DataRecord.Type.OVERHOURS.toString())) {
                    mOHStat.setVisibility(View.VISIBLE);
                    if (x.getDATA_3().equals(DataRecord.OHMode.PAID.toString()))
                        mOHStat.setText(Html.fromHtml(String.format(getString(R.string.test_home_paid), x.getDATA_4(), x.getDATA_1(), x.getDATA_2())));
                    if (x.getDATA_3().equals(DataRecord.OHMode.USED.toString()))
                        mOHStat.setText(Html.fromHtml(String.format(getString(R.string.text_home_used), x.getDATA_4(), x.getDATA_1(), x.getDATA_2())));
                    if (x.getDATA_3().equals(DataRecord.OHMode.UNUSED.toString()))
                        mOHStat.setText(String.format(getString(R.string.text_home_unused), x.getDATA_1(), x.getDATA_2()));
                }
                if (x.getTYPE().equals(DataRecord.Type.SICKLEAVE.toString())) {
                    mSLStat.setVisibility(View.VISIBLE);
                    mSLStat.setText(x.getDATA_1() + " h");
                }
                if (x.getTYPE().equals(DataRecord.Type.INTERVENCIJE.toString())) {
                    mInterventionStat.setVisibility(View.VISIBLE);
                    mInterventionStat.setText(x.getDATA_1() + " - " + x.getDATA_2());
                }
            }
        } catch (SQLException ex) {
            Log.d("A", "" + ex.getMessage());
        }

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void buildChart() {
        ValueLineSeries series = new ValueLineSeries();
        series.setColor(0xFF56B7F1);
        ValueLineSeries series2 = new ValueLineSeries();
        series2.setColor(0x22000000);
        _Norme = new ArrayList<>();
        _Holidays = new ArrayList<>();
        _WorkHours = new ArrayList<>();
        _SickLave = new ArrayList<>();
        _InterventionHours = new ArrayList<>();

        try {
            List<DataRecord> Tab;
            if (_DisableFilter)
                Tab = mDB.getRecordsByType(DataRecord.Type.NORMA.toString(), "ASC", ACC_USER);
            else
                Tab = mDB.getRecordsFiltered(DataRecord.Type.NORMA.toString(), _SortingType, ACC_USER, _MonthFilterEnabled, _MonthFilterValue, _YearFilterEnabled, _YearFilterValue);
            for (int i = 0; i < Tab.size(); ++i) {
                DataRecord x = Tab.get(i);
                if (!x.getDATA_1().equals("")) {
                    if (i == 0)
                        series.addPoint(new ValueLinePoint(x.getDATE(), Float.parseFloat(x.getDATA_1())));
                    if (i == Tab.size() - 1)
                        series.addPoint(new ValueLinePoint(x.getDATE(), Float.parseFloat(x.getDATA_1())));
                    series.addPoint(new ValueLinePoint(x.getDATE(), Float.parseFloat(x.getDATA_1())));
                    _Norme.add(x);
                }
            }

            List<DataRecord> Tab2;
            if (_DisableFilter)
                Tab2 = mDB.getRecordsByType(DataRecord.Type.HOLIDAYS.toString(), "ASC", ACC_USER);
            else
                Tab2 = mDB.getRecordsFiltered(DataRecord.Type.HOLIDAYS.toString(), _SortingType, ACC_USER, _MonthFilterEnabled, _MonthFilterValue, _YearFilterEnabled, _YearFilterValue);
            for (int i = 0; i < Tab2.size(); ++i) {
                DataRecord x = Tab2.get(i);
                if (!x.getDATA_1().equals("")) {
                    _Holidays.add(x);
                }
            }

            List<DataRecord> Tab3;
            if (_DisableFilter)
                Tab3 = mDB.getRecordsByType(DataRecord.Type.WORK_HOURS.toString(), "ASC", ACC_USER);
            else
                Tab3 = mDB.getRecordsFiltered(DataRecord.Type.WORK_HOURS.toString(), _SortingType, ACC_USER, _MonthFilterEnabled, _MonthFilterValue, _YearFilterEnabled, _YearFilterValue);
            for (int i = 0; i < Tab3.size(); ++i) {
                DataRecord x = Tab3.get(i);
                if (!x.getDATA_1().equals("")) {
                    _WorkHours.add(x);
                }
            }

            List<DataRecord> Tab4;
            if (_DisableFilter)
                Tab4 = mDB.getRecordsByType(DataRecord.Type.SICKLEAVE.toString(), "ASC", ACC_USER);
            else
                Tab4 = mDB.getRecordsFiltered(DataRecord.Type.SICKLEAVE.toString(), _SortingType, ACC_USER, _MonthFilterEnabled, _MonthFilterValue, _YearFilterEnabled, _YearFilterValue);
            for (int i = 0; i < Tab4.size(); ++i) {
                DataRecord x = Tab4.get(i);
                if (!x.getDATA_1().equals("")) {
                    _SickLave.add(x);
                }
            }

            List<DataRecord> Tab5;
            if (_DisableFilter)
                Tab5 = mDB.getRecordsByType(DataRecord.Type.INTERVENCIJE.toString(), "ASC", ACC_USER);
            else
                Tab5 = mDB.getRecordsFiltered(DataRecord.Type.INTERVENCIJE.toString(), _SortingType, ACC_USER, _MonthFilterEnabled, _MonthFilterValue, _YearFilterEnabled, _YearFilterValue);
            for (int i = 0; i < Tab5.size(); ++i) {
                DataRecord x = Tab5.get(i);
                if (!x.getDATA_1().equals("")) {
                    _InterventionHours.add(x);
                }
            }

            if (_Norme.size() != 0) {
                series2.addPoint(new ValueLinePoint(series.getSeries().get(0).getLegendLabel(), getAverageNorm()));
                series2.addPoint(new ValueLinePoint(series.getSeries().get(series.getSeries().size() - 1).getLegendLabel(), getAverageNorm()));
            }
        } catch (SQLException ex) {
            Log.d("A", "" + ex.getMessage());
        }

        mChartNorm.addSeries(series);
        mChartNorm.addSeries(series2);
        mChartNorm.startAnimation();
    }


    public float getAverageNorm() {
        float x, sum = 0;
        if (_Norme.size() != 0) {

            for (int i = 0; i < _Norme.size(); i++) {
                sum = sum + Float.parseFloat(_Norme.get(i).getDATA_1());
            }
            x = sum / _Norme.size();
        } else x = 0;
        return x;
    }

    public float getUsedHolidays() {
        float sum = 0;
        if (_Holidays.size() != 0) {

            for (int i = 0; i < _Holidays.size(); i++) {
                sum = sum + Float.parseFloat(_Holidays.get(i).getDATA_1());
            }
        }
        return sum;
    }

    public float getSickLeaveHours() {
        float sum = 0;
        if (_SickLave.size() != 0) {

            for (int i = 0; i < _SickLave.size(); i++) {
                sum = sum + Float.parseFloat(_SickLave.get(i).getDATA_1());
            }
        }
        return sum;
    }

    public String getWorkedHours() {

        int mMinSum = 0;
        int mHourSum = 0;

        SimpleDateFormat format = new SimpleDateFormat("HH:mm");

        if (_WorkHours.size() != 0) {
            for (int i = 0; i < _WorkHours.size(); i++) {
                String[] Data1 = _WorkHours.get(i).getDATA_1().split(":");
                String[] Data2 = _WorkHours.get(i).getDATA_1().split(":");

                String tData1 = _WorkHours.get(i).getDATA_1();
                String tData2 = _WorkHours.get(i).getDATA_2();

                Date date1 = null;
                Date date2 = null;
                try {
                    date1 = format.parse(tData1);
                    date2 = format.parse(tData2);
                } catch (ParseException e) {
                }
                long difference = date2.getTime() - date1.getTime();
                int Hours = (int) (difference / (1000 * 60 * 60));
                int Mins = (int) (difference / (1000 * 60)) % 60;
                if (Hours < 0) Hours = Hours + 24;
                if (Mins < 0) Mins = Mins + 60;

                mHourSum = mHourSum + Hours;
                mMinSum = mMinSum + Mins;
            }
            while (mMinSum >= 60) {
                mMinSum = mMinSum - 60;
                mHourSum++;
            }
        }

        return pad(mHourSum) + ":" + pad(mMinSum);
    }

    public String getInterventionHours() {

        int mMinSum = 0;
        int mHourSum = 0;

        SimpleDateFormat format = new SimpleDateFormat("HH:mm");

        if (_InterventionHours.size() != 0) {
            for (int i = 0; i < _InterventionHours.size(); i++) {
                String[] Data1 = _InterventionHours.get(i).getDATA_1().split(":");
                String[] Data2 = _InterventionHours.get(i).getDATA_1().split(":");

                String tData1 = _InterventionHours.get(i).getDATA_1();
                String tData2 = _InterventionHours.get(i).getDATA_2();

                Date date1 = null;
                Date date2 = null;
                try {
                    date1 = format.parse(tData1);
                    date2 = format.parse(tData2);
                } catch (ParseException e) {
                }
                long difference = date2.getTime() - date1.getTime();
                int Hours = (int) (difference / (1000 * 60 * 60));
                int Mins = (int) (difference / (1000 * 60)) % 60;
                if (Hours < 0) Hours = Hours + 24;
                if (Mins < 0) Mins = Mins + 60;

                mHourSum = mHourSum + Hours;
                mMinSum = mMinSum + Mins;
            }
            while (mMinSum >= 60) {
                mMinSum = mMinSum - 60;
                mHourSum++;
            }
        }

        return pad(mHourSum) + ":" + pad(mMinSum);
    }


    public String getOverhours(String tMODE) {

        int mMinSum = 0;
        int mHourSum = 0;

        ArrayList<DataRecord> temp_OverHoursFake = new ArrayList<>();
        ArrayList<DataRecord> _OverHoursFake = new ArrayList<>();

        SimpleDateFormat format = new SimpleDateFormat("HH:mm");

        try {
            List<DataRecord> Tab3;
            if (_DisableFilter)
                Tab3 = mDB.getRecordsByType(DataRecord.Type.OVERHOURS.toString(), "DESC", ACC_USER);
            else
                Tab3 = mDB.getRecordsFiltered(DataRecord.Type.OVERHOURS.toString(), _SortingType, ACC_USER, _MonthFilterEnabled, _MonthFilterValue, _YearFilterEnabled, _YearFilterValue);

            for (int i = 0; i < Tab3.size(); ++i) {
                DataRecord x = Tab3.get(i);
                if (!x.getDATA_1().equals("")) {
                    temp_OverHoursFake.add(x);
                }
            }
        } catch (SQLException ex) {
            Log.d("A", "" + ex.getMessage());
        }
        if (temp_OverHoursFake.size() != 0)
            for (int i = 0; i < temp_OverHoursFake.size(); i++) {
                if (temp_OverHoursFake.get(i).getDATA_3().toString().equals(tMODE))
                    _OverHoursFake.add(temp_OverHoursFake.get(i));
            }

        if (_OverHoursFake.size() != 0) {
            for (int i = 0; i < _OverHoursFake.size(); i++) {
                String tData1 = _OverHoursFake.get(i).getDATA_1();
                String tData2 = _OverHoursFake.get(i).getDATA_2();

                Date date1 = null;
                Date date2 = null;
                try {
                    date1 = format.parse(tData1);
                    date2 = format.parse(tData2);
                } catch (ParseException e) {
                }
                long difference = date2.getTime() - date1.getTime();
                int Hours = (int) (difference / (1000 * 60 * 60));
                int Mins = (int) (difference / (1000 * 60)) % 60;
                if (Hours < 0) Hours = Hours + 24;
                if (Mins < 0) Mins = Mins + 60;

                mHourSum = mHourSum + Hours;
                mMinSum = mMinSum + Mins;
            }
            while (mMinSum >= 60) {
                mMinSum = mMinSum - 60;
                mHourSum++;
            }
        } else
            return "00:00";

        return pad(mHourSum) + ":" + pad(mMinSum);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_filter:
                openFilter();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void openFilter() {
        final View dialogView = View.inflate(getActivity(), R.layout.dialog_filter, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(dialogView)
                .setCancelable(false);
        final AlertDialog dialog = builder.create();
//
        final ImageView iCover = (ImageView) dialogView.findViewById(R.id.header);
        iCover.setImageDrawable(ACC_Cover);

        //GET SETTINGS
        getSettingsForFilter(DataType, ACC_USER);
        //getViews
        final CheckBox mAutoFilter = (CheckBox) dialogView.findViewById(R.id.checkBoxAutoFilter);
        final CheckBox mYearFilter = (CheckBox) dialogView.findViewById(R.id.checkBoxYearFilter);
        final CheckBox mMonthFilter = (CheckBox) dialogView.findViewById(R.id.checkBoxMonthFilter);
        final CheckBox mDisableFilter = (CheckBox) dialogView.findViewById(R.id.checkBoxDisableFilter);
        RadioButton mSortingTypeASC = (RadioButton) dialogView.findViewById(R.id.radioButtonAsc);
        RadioButton mSortingTypeDESC = (RadioButton) dialogView.findViewById(R.id.radioButtonDesc);
        final EditText mYearFilterValue = (EditText) dialogView.findViewById(R.id.textYear);
        final EditText mMonthFilterValue = (EditText) dialogView.findViewById(R.id.textMonth);
        final RelativeLayout mLayout1 = (RelativeLayout) dialogView.findViewById(R.id.FilterLayout1);
        final RelativeLayout mLayout2 = (RelativeLayout) dialogView.findViewById(R.id.FilterLayout2);
        final RelativeLayout mLayout3 = (RelativeLayout) dialogView.findViewById(R.id.FilterLayout3);
        //
        setupFilterParameters(mAutoFilter, mYearFilter, mMonthFilter, mDisableFilter, mSortingTypeASC, mSortingTypeDESC, mYearFilterValue, mMonthFilterValue, mLayout1, mLayout2, mLayout3);
        //
        setupFilterWorking(mAutoFilter, mYearFilter, mMonthFilter, mDisableFilter, mSortingTypeASC, mSortingTypeDESC, mYearFilterValue, mMonthFilterValue, mLayout1, mLayout2, mLayout3);

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogs) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                    dialogView.post(new Runnable() {
                        public void run() {
                            dialogView.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    revealShow(dialogView, true, dialog, R.id.dialogFilter, R.id.FABDialogFilter);
                                }
                            }, 200);

                        }
                    });
                else
                    dialog.findViewById(R.id.dialogFilter).setVisibility(View.VISIBLE);

            }
        });
        dialogView.findViewById(R.id.FABDialogFilter).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                    dialogView.post(new Runnable() {
                        public void run() {
                            revealShow(dialogView, false, dialog, R.id.dialogFilter, R.id.FABDialogFilter, true);
                        }
                    });
                else {
                    dialog.dismiss();
                    dialog.findViewById(R.id.dialogFilter).setVisibility(View.INVISIBLE);
                }

                if (_AutoFilter)
                    setupAutoFilter();
                //APPLY FILTER
                SharedPreferences.Editor editor = getActivity().getSharedPreferences("Worker", getActivity().MODE_PRIVATE).edit();
                editor.putBoolean("FILTER_" + DataType + "_" + ACC_USER + "_AutoFilter", _AutoFilter);
                editor.putBoolean("FILTER_" + DataType + "_" + ACC_USER + "_YearFilterEnabled", _YearFilterEnabled);
                editor.putBoolean("FILTER_" + DataType + "_" + ACC_USER + "_MonthFilterEnabled", _MonthFilterEnabled);
                editor.putBoolean("FILTER_" + DataType + "_" + ACC_USER + "_DisableFilter", _DisableFilter);
                editor.putInt("FILTER_" + DataType + "_" + ACC_USER + "_YearFilterValue", _YearFilterValue);
                editor.putInt("FILTER_" + DataType + "_" + ACC_USER + "_MonthFilterValue", _MonthFilterValue);
                if (_SortingType.equals("DESC"))
                    editor.putString("FILTER_" + DataType + "_" + ACC_USER + "_SortingType", "DESC");
                else
                    editor.putString("FILTER_" + DataType + "_" + ACC_USER + "_SortingType", "ASC");
                editor.commit();

                ((MainActivity) getActivity()).forceUpdateFragment();

            }
        });
        dialog.setOnKeyListener(new Dialog.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface idialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                        dialogView.post(new Runnable() {
                            public void run() {
                                revealShow(dialogView, false, dialog, R.id.dialogFilter, R.id.FABDialogFilter, true);
                            }
                        });
                    else {
                        dialog.dismiss();
                        dialog.findViewById(R.id.dialogFilter).setVisibility(View.INVISIBLE);
                    }
                    return true;
                }
                return false;
            }
        });
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        dialog.show();
    }


}

