package com.metalen.worker.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.alamkanak.weekview.MonthLoader;
import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewEvent;
import com.metalen.worker.R;
import com.metalen.worker.SQL.SQLHandler;
import com.metalen.worker.classes.DataRecord;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by M3talen on 27.12.2015..
 */
public class FragmentCalendar extends FragmentCore {
    private WeekView mWeekView;
    private SQLHandler mDB;
    private String ACC_USER;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_calendar, container, false);

        mWeekView = (WeekView) fragmentView.findViewById(R.id.weekView);

        mDB = new SQLHandler(getActivity());

        SharedPreferences prefs = getActivity().getSharedPreferences("Worker", Context.MODE_PRIVATE);
        int ACC = prefs.getInt("ACC", 1);
        if (ACC == 1)
            ACC_USER = DataRecord.Account.ACC1.toString();
        else
            ACC_USER = DataRecord.Account.ACC2.toString();

        buildWeekView();

        return fragmentView;
    }

    private void buildWeekView() {
        mWeekView.setOnEventClickListener(new WeekView.EventClickListener() {
            @Override
            public void onEventClick(WeekViewEvent event, RectF eventRect) {
                Log.d("Event", event.getName());
            }
        });
        mWeekView.setMonthChangeListener(new MonthLoader.MonthChangeListener() {
            @Override
            public List<WeekViewEvent> onMonthChange(int newYear, int newMonth) {
                List<WeekViewEvent> events = getEvents(newYear, newMonth);
                return events;
            }
        });

        mWeekView.setNumberOfVisibleDays(3);

        mWeekView.setColumnGap((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics()));
        mWeekView.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
        mWeekView.setEventTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
    }

    private List<WeekViewEvent> getEvents(int newYear, int newMonth) {
        List<WeekViewEvent> events = new ArrayList<>();

        ArrayList<DataRecord> _Holidays = new ArrayList<>();
        ArrayList<DataRecord> _WorkHours = new ArrayList<>();

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

        for (int i = 0; i < _WorkHours.size(); i++) {
            String[] tData1 = _WorkHours.get(i).getDATA_1().split(":");
            String[] tData2 = _WorkHours.get(i).getDATA_1().split(":");
            String tDate = _WorkHours.get(i).getDATE();

            Calendar startTime = Calendar.getInstance();
            Calendar endTime;

            SimpleDateFormat format2 = new SimpleDateFormat("dd/MM/yyyy");
            Date date0 = null;
            try {
                date0 = format2.parse(tDate);
            } catch (ParseException e) {
            }
            startTime = Calendar.getInstance();
            startTime.set(Calendar.HOUR_OF_DAY, Integer.parseInt(tData1[0]));
            startTime.set(Calendar.MINUTE, Integer.parseInt(tData1[1]));
            startTime.set(Calendar.MONTH, date0.getMonth()-1);
            startTime.set(Calendar.YEAR, date0.getYear());
            endTime = (Calendar) startTime.clone();
            endTime.set(Calendar.HOUR_OF_DAY, Integer.parseInt(tData2[0]));
            endTime.set(Calendar.MINUTE, Integer.parseInt(tData2[1]));
            endTime.set(Calendar.MONTH, date0.getMonth()-1);
            WeekViewEvent event = new WeekViewEvent(_WorkHours.get(i).getID(), "Work hours", startTime, endTime);
            event.setColor(R.color.red_500);
            events.add(event);
        }

        return events;
    }
}
