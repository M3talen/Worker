package com.metalen.worker.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.SQLException;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
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
    TextView mStat1, mStat2, mNormStat, mHolidayStat, mWHStat, mOHStat;
    ArrayList<DataRecord> _Norme = null;
    ArrayList<DataRecord> _Holidays = null;
    ArrayList<DataRecord> _WorkHours = null;
    private String ACC_USER = "";

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

        mDB = new SQLHandler(getActivity());

        SharedPreferences prefs = getActivity().getSharedPreferences("Worker", Context.MODE_PRIVATE);
        int ACC = prefs.getInt("ACC", 1);
        if (ACC == 1)
            ACC_USER = DataRecord.Account.ACC1.toString();
        else
            ACC_USER = DataRecord.Account.ACC2.toString();

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
        calendar.setDateSelected(today,true);
        calendar.postDelayed(new Runnable() {
            @Override
            public void run() {
                getDataForDate(today);
            }
        }, 300);


        String stats1 = String.format("Average norm : <b>%s</b><br>Used holidays : <b>%s</b><br>Worked hours : <b>%s</b>", String.format("%.2f", (float) getAverageNorm()), String.format("%.2f", (float) getUsedHolidays()), getWorkedHours());
        String stats2 = String.format("Overhours paid : <b>%s</b><br>Overhours used : <b>%s</b><br>Overhours unused : <b>%s</b>", getFakeOverhours(DataRecord.OHMode.PAID.toString()), getFakeOverhours(DataRecord.OHMode.USED.toString()), getFakeOverhours(DataRecord.OHMode.UNUSED.toString()));
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

        try {
            List<DataRecord> Tab3 = mDB.getRecordsByDate(final_date, "ASC", ACC_USER);  //TO-DO add difrent filter
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
                        mOHStat.setText(Html.fromHtml("Paid (" + x.getDATA_4() + ")<br>" + x.getDATA_1() + " - " + x.getDATA_2()));
                    if (x.getDATA_3().equals(DataRecord.OHMode.UNUSED.toString()))
                        mOHStat.setText(Html.fromHtml("Used (" + x.getDATA_4() + ")<br>" + x.getDATA_1() + " - " + x.getDATA_2()));
                    if (x.getDATA_3().equals(DataRecord.OHMode.USED.toString()))
                        mOHStat.setText("Unused " + x.getDATA_1() + " - " + x.getDATA_2());
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

        try {
            List<DataRecord> Tab = mDB.getRecordsByType(DataRecord.Type.NORMA.toString(), "ASC", ACC_USER);  //TO-DO add difrent filter
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
            List<DataRecord> Tab2 = mDB.getRecordsByType(DataRecord.Type.HOLIDAYS.toString(), "ASC", ACC_USER);  //TO-DO add difrent filter
            for (int i = 0; i < Tab2.size(); ++i) {
                DataRecord x = Tab2.get(i);
                if (!x.getDATA_1().equals("")) {
                    _Holidays.add(x);
                }
            }
            List<DataRecord> Tab3 = mDB.getRecordsByType(DataRecord.Type.WORK_HOURS.toString(), "ASC", ACC_USER);  //TO-DO add difrent filter
            for (int i = 0; i < Tab3.size(); ++i) {
                DataRecord x = Tab3.get(i);
                if (!x.getDATA_1().equals("")) {
                    _WorkHours.add(x);
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

    public String getFakeOverhours(String tMODE) {

        int mMinSum = 0;
        int mHourSum = 0;

        ArrayList<DataRecord> temp_OverHoursFake = new ArrayList<>();
        ArrayList<DataRecord> _OverHoursFake = new ArrayList<>();

        SimpleDateFormat format = new SimpleDateFormat("HH:mm");

        try {
            List<DataRecord> Tab3 = mDB.getRecordsByType(DataRecord.Type.OVERHOURS.toString(), "ASC", ACC_USER);  //TO-DO add difrent filter
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


}

