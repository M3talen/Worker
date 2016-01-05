package com.metalen.worker.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.SQLException;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import com.metalen.worker.R;
import com.metalen.worker.SQL.SQLHandler;
import com.metalen.worker.classes.DataRecord;
import com.percolate.foam.FoamEvent;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Metalen on 13.2.2015..
 */
public class SalaryFragment extends FragmentCore {

    SQLHandler mDB;
    Boolean isADV = false;
    private EditText mET1, mET2, mET_OH1, mET_OH2, mET_TAX1, mET_TAX2, mET_TAX3_1, mET_TAX3_2, mET_TAX3_3, mET_ADD1, mET_ADD2;
    private String ACC_USER = "";

    public SalaryFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_salary, container, false);

        CheckBox mCheckBox = (CheckBox) fragmentView.findViewById(R.id.salary_cb_adv);
        final CardView mOvertimeCard = (CardView) fragmentView.findViewById(R.id.salary_card_overtime);
        final CardView mTaxCard = (CardView) fragmentView.findViewById(R.id.salary_card_tax);
        final CardView mAddCard = (CardView) fragmentView.findViewById(R.id.salary_card_additions);

        FloatingActionButton mFAB = (FloatingActionButton) fragmentView.findViewById(R.id.FABSalary);

        mET1 = (EditText) fragmentView.findViewById(R.id.salary_et_1);
        mET2 = (EditText) fragmentView.findViewById(R.id.salary_et_2);
        mET_OH1 = (EditText) fragmentView.findViewById(R.id.salary_et_3);
        mET_OH2 = (EditText) fragmentView.findViewById(R.id.salary_et_4);
        mET_TAX1 = (EditText) fragmentView.findViewById(R.id.salary_et_5);
        mET_TAX2 = (EditText) fragmentView.findViewById(R.id.salary_et_6);
        mET_TAX3_1 = (EditText) fragmentView.findViewById(R.id.salary_et_7_1);
        mET_TAX3_2 = (EditText) fragmentView.findViewById(R.id.salary_et_7_2);
        mET_TAX3_3 = (EditText) fragmentView.findViewById(R.id.salary_et_7_3);

        mET_ADD1 = (EditText) fragmentView.findViewById(R.id.salary_et_9);
        mET_ADD2 = (EditText) fragmentView.findViewById(R.id.salary_et_10);

        mDB = new SQLHandler(getActivity());

        SharedPreferences prefs = getActivity().getSharedPreferences("Worker", Context.MODE_PRIVATE);
        int ACC = prefs.getInt("ACC", 1);
        if (ACC == 1)
            ACC_USER = DataRecord.Account.ACC1.toString();
        else
            ACC_USER = DataRecord.Account.ACC2.toString();

        mOvertimeCard.setVisibility(View.INVISIBLE);
        mTaxCard.setVisibility(View.INVISIBLE);
        mAddCard.setVisibility(View.INVISIBLE);
        mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    isADV = true;
                    mOvertimeCard.setVisibility(View.VISIBLE);
                    mTaxCard.setVisibility(View.VISIBLE);
                    mAddCard.setVisibility(View.VISIBLE);
                } else {
                    isADV = false;
                    mOvertimeCard.setVisibility(View.INVISIBLE);
                    mTaxCard.setVisibility(View.INVISIBLE);
                    mAddCard.setVisibility(View.INVISIBLE);
                }
            }
        });

        mET2.setText(getWorkedHours());
        mET_OH1.setText(getFakeOverhours());

        mFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculateSalary();
            }
        });

        return fragmentView;
    }

    private void calculateSalary() {

        double add_deduction = 0;
        double add_coefficient = 1;
        double tax_relief = 0;
        double tax_1 = 0.12;
        double tax_2 = 0.25;
        double tax_3 = 0.4;
        double tax_health = 0;
        double tax_pension = 0;
        double overtime_pph = 0;
        double overtime_hours = 0;
        double paymet_per_hour = 0;
        double worked_hours = 0;
        try {
            paymet_per_hour = Double.parseDouble(mET1.getText().toString());
            worked_hours = Double.parseDouble(mET2.getText().toString());
            overtime_hours = Double.parseDouble(mET_OH1.getText().toString());
            overtime_pph = Double.parseDouble(mET_OH2.getText().toString());
            tax_pension = Double.parseDouble(mET_TAX1.getText().toString()) / 100;
            tax_health = Double.parseDouble(mET_TAX2.getText().toString()) / 100;
            tax_1 = Double.parseDouble(mET_TAX3_1.getText().toString()) / 100;
            tax_2 = Double.parseDouble(mET_TAX3_2.getText().toString()) / 100;
            tax_3 = Double.parseDouble(mET_TAX3_3.getText().toString()) / 100;
            add_coefficient = Double.parseDouble(mET_ADD1.getText().toString());
            add_deduction = Double.parseDouble(mET_ADD2.getText().toString());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if (isADV) {
            double tSalary = paymet_per_hour * worked_hours;
            if (overtime_hours != 0 && overtime_pph != 0)
                tSalary = tSalary + (overtime_hours * overtime_pph);

            double tBruto1 = tSalary * (1 - tax_pension - tax_health);
            double tDeduction = add_deduction * add_coefficient;
            double tTaxabe;

            double p1 = 0, p2 = 0, p3 = 0;

            if (tBruto1 < tDeduction)
                tTaxabe = 0;
            else
                tTaxabe = tBruto1 - tDeduction;
            if (tTaxabe < tDeduction)
                p1 = tax_1 * tTaxabe;
            if (tTaxabe >= tDeduction && tTaxabe < 6 * tDeduction) {
                p1 = tDeduction * tax_1;
                p2 = (tSalary - tDeduction) * tax_2;
            }
            if (tTaxabe >= 6 * tDeduction) {
                p1 = tDeduction * tax_1;
                p2 = (5 * tDeduction) * tax_2;
                p3 = tSalary - (6 * tDeduction) * tax_3;
            }

            double tNeto = tBruto1 - p1 - p2 - p3;

            new AlertDialog.Builder(getActivity())
                    .setTitle("Salary")
                    .setMessage("Bruto : " + String.format("%.2f", tBruto1) + " €\nNeto : " + String.format("%.2f", tNeto) + " €")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    })
                    .show();

        } else if (!isADV) {
            double tSalary = paymet_per_hour * worked_hours;
            double tBruto = tSalary;
            double tNeto = (tSalary - (tSalary * 0.25));

            new AlertDialog.Builder(getActivity())
                    .setTitle("Salary")
                    .setMessage("Bruto : " + String.format("%.2f", tBruto) + " €\nNeto : " + String.format("%.2f", tNeto) + " €")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    })
                    .show();

        }
        new FoamEvent().track(getActivity(), "Calculating salary");
    }

    public String getWorkedHours() {

        int mMinSum = 0;
        int mHourSum = 0;

        ArrayList<DataRecord> _WorkHours = new ArrayList<>();

        SimpleDateFormat format = new SimpleDateFormat("HH:mm");

        try {
            List<DataRecord> Tab3 = mDB.getRecordsByType(DataRecord.Type.WORK_HOURS.toString(), "ASC", ACC_USER);  //TO-DO add difrent filter
            for (int i = 0; i < Tab3.size(); ++i) {
                DataRecord x = Tab3.get(i);
                if (!x.getDATA_1().equals("")) {
                    _WorkHours.add(x);
                }
            }
        } catch (SQLException ex) {
            Log.d("A", "" + ex.getMessage());
        }

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
            if (mMinSum != 0) {
                float x = (float) ((mMinSum / 60.0) * 100.0);
                return mHourSum + "." + (int) x;
            }
        }

        return mHourSum + "." + mMinSum;
    }

    public String getFakeOverhours() {

        int mMinSum = 0;
        int mHourSum = 0;

        String ACC_FAKE = "";

        SharedPreferences prefs = getActivity().getSharedPreferences("Worker", Context.MODE_PRIVATE);
        int ACC = prefs.getInt("ACC", 1);
        if (ACC == 1)
            ACC_FAKE = DataRecord.Account.ACC2.toString();
        else
            ACC_FAKE = DataRecord.Account.ACC1.toString();

        ArrayList<DataRecord> _WorkHours = new ArrayList<>();

        SimpleDateFormat format = new SimpleDateFormat("HH:mm");

        try {
            List<DataRecord> Tab3 = mDB.getRecordsByType(DataRecord.Type.WORK_HOURS.toString(), "ASC", ACC_FAKE);  //TO-DO add difrent filter
            for (int i = 0; i < Tab3.size(); ++i) {
                DataRecord x = Tab3.get(i);
                if (!x.getDATA_1().equals("")) {
                    _WorkHours.add(x);
                }
            }
        } catch (SQLException ex) {
            Log.d("A", "" + ex.getMessage());
        }

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
            if (mMinSum != 0) {
                float x = (float) ((mMinSum / 60.0) * 100.0);
                return mHourSum + "." + (int) x;
            }
        }

        return mHourSum + "." + mMinSum;
    }
}
