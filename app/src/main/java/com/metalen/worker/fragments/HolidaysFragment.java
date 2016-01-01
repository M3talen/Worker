package com.metalen.worker.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.SQLException;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.*;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.*;
import com.metalen.worker.MainActivity;
import com.metalen.worker.R;
import com.metalen.worker.SQL.SQLHandler;
import com.metalen.worker.adapters.RecyclerViewAdapterHolidays;
import com.metalen.worker.classes.DataRecord;
import io.codetail.animation.arcanimator.ArcAnimator;
import io.codetail.animation.arcanimator.Side;
import mirko.android.datetimepicker.date.DatePickerDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Metalen on 24.1.2015..
 */
public class HolidaysFragment extends FragmentCore {

    private SQLHandler mDB;
    private RecyclerView mRecyclerView;
    private RecyclerViewAdapterHolidays mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private FloatingActionButton mFAB;
    private List<DataRecord> mDataSet = new ArrayList<>();

    private String DataType = DataRecord.Type.HOLIDAYS.toString();

    private int ACC = 1;
    private String ACC_USER = "";
    private Drawable ACC_Cover;

    public HolidaysFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_norm, container, false);

        mFAB = (FloatingActionButton) fragmentView.findViewById(R.id.FABNorma);
        mRecyclerView = (RecyclerView) fragmentView.findViewById(R.id.recyclerView);

        mDB = new SQLHandler(getActivity());
        //mDemoDataSet.add(new Norms(0," NULL ", " NULL ", "NULL"));
//
        mRecyclerView.setHasFixedSize(false);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mDataSet.clear();
        mAdapter = new RecyclerViewAdapterHolidays(mDataSet, HolidaysFragment.this);
       // mRecyclerView.setItemAnimator(new ItemAnimator(mRecyclerView));
        mRecyclerView.setAdapter(mAdapter);
//

        mFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ButtonAdd();
            }
        });
        mFAB.animate().rotation(90).setDuration(500).setInterpolator(new AnticipateOvershootInterpolator());

        mRecyclerView.postDelayed(new Runnable() {
            public void run() {
                LoadDataFromDatabase();
            }
        }, 100);

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

        return fragmentView;
    }


    public void ButtonEdit(final int pos) {
        final int position = (mDataSet.get(pos).getID());
        final View dialogView = View.inflate(getActivity(), R.layout.dialog_norm_add, null);

        mFAB.animate().alpha(0).setDuration(500);
        mFAB.animate().rotation(180).setDuration(800).setInterpolator(new AnticipateOvershootInterpolator());

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(dialogView)
                .setCancelable(false);
        final AlertDialog dialog = builder.create();

        final Calendar mCalendar = Calendar.getInstance();

        final EditText dDate = (EditText) dialogView.findViewById(R.id.dialogNormView_et_date);
        final EditText dData = (EditText) dialogView.findViewById(R.id.dialogNormView_et_norm);

        final ImageView iCover = (ImageView) dialogView.findViewById(R.id.header);
        iCover.setImageDrawable(ACC_Cover);

        List<DataRecord> Tab = mDB.getRecords(position + "", ACC_USER);
        DataRecord Recordx = Tab.get(0);

        dDate.setText(Recordx.getDATE().toString());
        dData.setText(Recordx.getDATA_1());

        dDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(new DatePickerDialog.OnDateSetListener() {

                    public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {
                        dDate.setText(new StringBuilder().append(pad(day)).append("/").append(pad(month + 1)).append("/").append(year));
                    }

                }, mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH));
                FragmentManager fragmentManager = getActivity().getFragmentManager();

                datePickerDialog.show(fragmentManager, getTag());
            }
        });
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogs) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                    dialogView.post(new Runnable() {
                        public void run() {
                            revealShow(dialogView, true, dialog, R.id.dialogNormView, R.id.FABDialogNorma);
                        }
                    });
                else
                    dialog.findViewById(R.id.dialogNormView).setVisibility(View.VISIBLE);

            }
        });
        dialogView.findViewById(R.id.FABDialogNorma).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!dData.getText().toString().equals("")) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                        dialogView.post(new Runnable() {
                            public void run() {
                                revealShow(dialogView, false, dialog, R.id.dialogNormView, R.id.FABDialogNorma);
                            }
                        });
                    else {
                        dialog.dismiss();
                        dialog.findViewById(R.id.dialogNormView).setVisibility(View.INVISIBLE);
                    }
                    dialogView.findViewById(R.id.FABDialogNorma).animate().alpha(0).setDuration(500);
                    dialogView.findViewById(R.id.FABDialogNorma).animate().rotation(180).setDuration(800).setInterpolator(new AnticipateOvershootInterpolator());

                    mFAB.animate().alpha(1).setDuration(500);
                    mFAB.animate().rotation(90).setDuration(800).setInterpolator(new AnticipateOvershootInterpolator());

                    mDB.updateRecord(position + "", DataType, dDate.getText().toString(), dData.getText().toString(), "", "", "");

                    mDataSet.clear();
                    mAdapter = new RecyclerViewAdapterHolidays(mDataSet, HolidaysFragment.this);
                    LoadDataFromDatabase();
                    mRecyclerView.swapAdapter(mAdapter, true);
                }
            }
        });
        dialog.setOnKeyListener(new Dialog.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface idialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                        dialogView.post(new Runnable() {
                            public void run() {
                                revealShow(dialogView, false, dialog, R.id.dialogNormView, R.id.FABDialogNorma);
                            }
                        });
                    else {
                        dialog.dismiss();
                        dialog.findViewById(R.id.dialogNormView).setVisibility(View.INVISIBLE);
                    }
                    dialogView.findViewById(R.id.FABDialogNorma).animate().alpha(0).setDuration(700);
                    dialogView.findViewById(R.id.FABDialogNorma).animate().rotation(180).setDuration(800).setInterpolator(new AnticipateOvershootInterpolator());

                    mFAB.animate().alpha(1).setDuration(500);
                    mFAB.animate().rotation(90).setDuration(800).setInterpolator(new AnticipateOvershootInterpolator());
                    return true;
                }
                return false;
            }
        });
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        dialog.show();

        FloatingActionButton editFAB = (FloatingActionButton) dialogView.findViewById(R.id.FABDialogNorma);
        editFAB.setImageResource(R.drawable.ic_mode_edit_black_24dp);
        editFAB.animate().alpha(1).setDuration(500);
        editFAB.setRotation(-90);
        editFAB.animate().rotation(0).setDuration(800).setInterpolator(new AnticipateOvershootInterpolator());
    }

    public void ButtonDelete(int pos) {
        final int position = pos;
        new AlertDialog.Builder(getActivity())
                .setTitle("Deleting")
                .setMessage("Are you sure you want to delete the record ?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        mDB.removeRecord(mDataSet.get(position).getID());
                        mAdapter.removeItem(position);
                    }
                })
                .setNegativeButton("No", null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    public void ButtonAdd() {

        final float orgX = mFAB.getX();
        final float orgY = mFAB.getY();

        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        final int width = size.x;
        final int height = size.y;

        mFAB.animate().rotation(180).setDuration(800).setInterpolator(new AnticipateOvershootInterpolator());

        final View dialogView = View.inflate(getActivity(), R.layout.dialog_norm_add, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(dialogView)
                .setCancelable(false);
        final AlertDialog dialog = builder.create();
//
        final FloatingActionButton nFAB = (FloatingActionButton) dialogView.findViewById(R.id.FABDialogNorma);

        final Calendar mCalendar = Calendar.getInstance();

        final EditText dDate = (EditText) dialogView.findViewById(R.id.dialogNormView_et_date);
        final EditText dData = (EditText) dialogView.findViewById(R.id.dialogNormView_et_norm);

        final ImageView iCover = (ImageView) dialogView.findViewById(R.id.header);
        iCover.setImageDrawable(ACC_Cover);

        Date today = new Date();
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        String date = format.format(today);

        dDate.setText(date);

        dDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(new DatePickerDialog.OnDateSetListener() {

                    public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {
                        dDate.setText(new StringBuilder().append(pad(day)).append("/").append(pad(month + 1)).append("/").append(year));
                    }

                }, mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH));
                FragmentManager fragmentManager = getActivity().getFragmentManager();

                datePickerDialog.show(fragmentManager, getTag());
            }
        });
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogs) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    final ArcAnimator animarc = ArcAnimator.createArcAnimator(mFAB, width / 2, height / 2 - mFAB.getHeight(), 30, Side.LEFT);
                    animarc.setDuration(200);
                    animarc.start();
                    dialogView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            revealShow(dialogView, true, dialog, R.id.dialogNormView, R.id.FABDialogNorma);
                        }
                    }, 200);
                } else
                    dialog.findViewById(R.id.dialogNormView).setVisibility(View.VISIBLE);

            }
        });
        dialogView.findViewById(R.id.FABDialogNorma).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!dData.getText().toString().equals("")) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                        dialogView.post(new Runnable() {
                            public void run() {
                                revealShow(dialogView, false, dialog, R.id.dialogNormView, R.id.FABDialogNorma, true);

                                int[] locations = new int[2];
                                nFAB.getLocationOnScreen(locations);
                                mFAB.setX(locations[0] - getPixels(56));
                                mFAB.setY(locations[1] - getPixels(77));

                                dialogView.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        final ArcAnimator animatorArc = ArcAnimator.createArcAnimator(mFAB, orgX + getPixels(28), orgY + getPixels(28), 0, Side.LEFT);
                                        animatorArc.setDuration(300);
                                        animatorArc.start();
                                    }
                                }, 330);
                            }
                        });
                    else {
                        dialog.dismiss();
                        dialog.findViewById(R.id.dialogNormView).setVisibility(View.INVISIBLE);
                    }
                    dialogView.findViewById(R.id.FABDialogNorma).animate().alpha(0).setDuration(500);
                    dialogView.findViewById(R.id.FABDialogNorma).animate().rotation(180).setDuration(800).setInterpolator(new AnticipateOvershootInterpolator());

                    mFAB.animate().alpha(1).setDuration(500);
                    mFAB.animate().rotation(90).setDuration(800).setInterpolator(new AnticipateOvershootInterpolator());

                    DataRecord data = new DataRecord(0, ACC_USER.toString(), DataType, dDate.getText().toString(), dData.getText().toString(), "", "", "");
                    mDB.addRecord(data);
                    mAdapter.addItem(data, RecyclerViewAdapterHolidays.LAST_POSITION);

                    mDataSet.clear();
                    mAdapter = new RecyclerViewAdapterHolidays(mDataSet, HolidaysFragment.this);
                    LoadDataFromDatabase();
                    mRecyclerView.swapAdapter(mAdapter, true);
                }
            }
        });
        dialog.setOnKeyListener(new Dialog.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface idialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                        dialogView.post(new Runnable() {
                            public void run() {
                                revealShow(dialogView, false, dialog, R.id.dialogNormView, R.id.FABDialogNorma, true);


                                int[] locations = new int[2];
                                nFAB.getLocationOnScreen(locations);
                                mFAB.setX(locations[0] - getPixels(56));
                                mFAB.setY(locations[1] - getPixels(77));

                                dialogView.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        final ArcAnimator animatorArc = ArcAnimator.createArcAnimator(mFAB, orgX + getPixels(28), orgY + getPixels(28), 0, Side.LEFT);
                                        animatorArc.setDuration(300);
                                        animatorArc.start();
                                    }
                                }, 330);
                            }
                        });
                    else {
                        dialog.dismiss();
                        dialog.findViewById(R.id.dialogNormView).setVisibility(View.INVISIBLE);
                    }
                    dialogView.findViewById(R.id.FABDialogNorma).animate().alpha(0).setDuration(700);
                    dialogView.findViewById(R.id.FABDialogNorma).animate().rotation(180).setDuration(800).setInterpolator(new AnticipateOvershootInterpolator());

                    mFAB.animate().alpha(1).setDuration(500);
                    mFAB.animate().rotation(90).setDuration(800).setInterpolator(new AnticipateOvershootInterpolator());
                    return true;
                }
                return false;
            }
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        dialog.show();
        dialogView.findViewById(R.id.FABDialogNorma).animate().alpha(1).setDuration(500);
        dialogView.findViewById(R.id.FABDialogNorma).animate().rotation(90).setDuration(800).setInterpolator(new AnticipateOvershootInterpolator());
    }

    public void LoadDataFromDatabase() {
        final ArrayList<DataRecord> _Holidays = new ArrayList<>();
        try {
            List<DataRecord> Tab;
            if(_DisableFilter)
                Tab = mDB.getRecordsByType(DataType, "DESC", ACC_USER);
            else
                Tab = mDB.getRecordsFiltered(DataType, _SortingType, ACC_USER ,_MonthFilterEnabled , _MonthFilterValue, _YearFilterEnabled, _YearFilterValue);
            for (int i = 0; i < Tab.size(); ++i) {
                DataRecord x = Tab.get(i);
                _Holidays.add(x);
            }
        } catch (SQLException ex) {
            Log.d("A", "" + ex.getMessage());
        }
        for (int i = 0; i < _Holidays.size(); ++i) {
            mAdapter.addItem(_Holidays.get(i), RecyclerViewAdapterHolidays.LAST_POSITION);
        }
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

                if(_AutoFilter)
                    setupAutoFilter();
                //APPLY FILTER
                SharedPreferences.Editor editor = getActivity().getSharedPreferences("Worker", getActivity().MODE_PRIVATE).edit();
                editor.putBoolean("FILTER_" + DataType +  "_" + ACC_USER +"_AutoFilter", _AutoFilter);
                editor.putBoolean("FILTER_" + DataType + "_" + ACC_USER +"_YearFilterEnabled", _YearFilterEnabled);
                editor.putBoolean("FILTER_" + DataType + "_" + ACC_USER +"_MonthFilterEnabled", _MonthFilterEnabled);
                editor.putBoolean("FILTER_" + DataType + "_" + ACC_USER +"_DisableFilter", _DisableFilter);
                editor.putInt("FILTER_" + DataType + "_" + ACC_USER +"_YearFilterValue", _YearFilterValue);
                editor.putInt("FILTER_" + DataType + "_" + ACC_USER +"_MonthFilterValue", _MonthFilterValue);
                if (_SortingType.equals("DESC"))
                    editor.putString("FILTER_" + DataType + "_" + ACC_USER +"_SortingType", "DESC");
                else
                    editor.putString("FILTER_" + DataType + "_" + ACC_USER +"_SortingType", "ASC");
                editor.commit();

                mDataSet.clear();
                mAdapter = new RecyclerViewAdapterHolidays(mDataSet, HolidaysFragment.this);
                LoadDataFromDatabase();
                mRecyclerView.swapAdapter(mAdapter, true);

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

    protected void setDataType(String tType)
    {
        DataType = tType;
    }

}
