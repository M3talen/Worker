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
import com.metalen.worker.adapters.RecyclerViewAdapterOverhours;
import com.metalen.worker.classes.DataRecord;
import io.codetail.animation.arcanimator.ArcAnimator;
import io.codetail.animation.arcanimator.Side;
import mirko.android.datetimepicker.date.DatePickerDialog;
import mirko.android.datetimepicker.time.RadialPickerLayout;
import mirko.android.datetimepicker.time.TimePickerDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Metalen on 15.2.2015..
 */
public class OverhoursFragment extends FragmentCore {


    private SQLHandler mDB;
    private RecyclerView mRecyclerView;
    private RecyclerViewAdapterOverhours mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private FloatingActionButton mFAB;
    private List<DataRecord> mDataSet = new ArrayList<>();

    private String DataType = DataRecord.Type.OVERHOURS.toString();

    private int ACC = 1;
    private String ACC_USER = "";

    private Drawable ACC_Cover;

    public OverhoursFragment() {
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
        mAdapter = new RecyclerViewAdapterOverhours(mDataSet, OverhoursFragment.this);
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
        final View dialogView = View.inflate(getActivity(), R.layout.dialog_overtime_add, null);

        mFAB.animate().alpha(0).setDuration(500);
        mFAB.animate().rotation(180).setDuration(800).setInterpolator(new AnticipateOvershootInterpolator());

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(dialogView)
                .setCancelable(false);
        final AlertDialog dialog = builder.create();

        final Calendar mCalendar = Calendar.getInstance();


        final EditText dDate = (EditText) dialogView.findViewById(R.id.dialogWHView_et_date);
        final EditText dData1 = (EditText) dialogView.findViewById(R.id.dialogWHView_et_wh1);
        final EditText dData2 = (EditText) dialogView.findViewById(R.id.dialogWHView_et_wh2);
        final EditText dDate2 = (EditText) dialogView.findViewById(R.id.dialogOHView_et_date2);

        final RadioButton rb1 = (RadioButton) dialogView.findViewById(R.id.radioButton1);
        final RadioButton rb2 = (RadioButton) dialogView.findViewById(R.id.radioButton2);
        final RadioButton rb3 = (RadioButton) dialogView.findViewById(R.id.radioButton3);

        rb1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rb1.setChecked(true);
                rb2.setChecked(false);
                rb3.setChecked(false);
                dDate2.setVisibility(View.VISIBLE);
            }
        });
        rb2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rb1.setChecked(false);
                rb2.setChecked(true);
                rb3.setChecked(false);
                dDate2.setVisibility(View.GONE);
            }
        });
        rb3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rb1.setChecked(false);
                rb2.setChecked(false);
                rb3.setChecked(true);
                dDate2.setVisibility(View.VISIBLE);
            }
        });

        final ImageView iCover = (ImageView) dialogView.findViewById(R.id.header);
        iCover.setImageDrawable(ACC_Cover);

        List<DataRecord> Tab = mDB.getRecords(position + "", ACC_USER);
        final DataRecord Recordx = Tab.get(0);

        dDate.setText(Recordx.getDATE().toString());
        dData1.setText(Recordx.getDATA_1());
        dData2.setText(Recordx.getDATA_2());
        dDate2.setText(Recordx.getDATA_4().toString());

        String tOHMode = Recordx.getDATA_3();
        if (tOHMode.equals(DataRecord.OHMode.PAID.toString())) {
            rb1.setChecked(true);
            dDate2.setVisibility(View.VISIBLE);
        } else if (tOHMode.equals(DataRecord.OHMode.UNUSED.toString())) {
            rb2.setChecked(true);
        } else if (tOHMode.equals(DataRecord.OHMode.USED.toString())) {
            rb3.setChecked(true);
            dDate2.setVisibility(View.VISIBLE);
        }

        final String[] Data1 = Recordx.getDATA_1().split(":");
        final String[] Data2 = Recordx.getDATA_2().split(":");

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

        dDate2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(new DatePickerDialog.OnDateSetListener() {

                    public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {
                        dDate2.setText(new StringBuilder().append(pad(day)).append("/").append(pad(month + 1)).append("/").append(year));
                    }

                }, mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH));
                FragmentManager fragmentManager = getActivity().getFragmentManager();

                datePickerDialog.show(fragmentManager, getTag());
            }
        });

        dData1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final TimePickerDialog timePickerDialog24h = TimePickerDialog.newInstance(new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(RadialPickerLayout view, int hourOfDay,
                                          int minute) {

                        dData1.setText(new StringBuilder().append(pad(hourOfDay)).append(":").append(pad(minute)));

                    }
                }, Integer.parseInt(Data1[0]), Integer.parseInt(Data1[1]), true);
                FragmentManager fragmentManager = getActivity().getFragmentManager();
                timePickerDialog24h.show(fragmentManager, getTag());
            }
        });
        dData2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final TimePickerDialog timePickerDialog24h = TimePickerDialog.newInstance(new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(RadialPickerLayout view, int hourOfDay,
                                          int minute) {

                        dData2.setText(new StringBuilder().append(pad(hourOfDay)).append(":").append(pad(minute)));

                    }
                }, Integer.parseInt(Data2[0]), Integer.parseInt(Data2[1]), true);
                FragmentManager fragmentManager = getActivity().getFragmentManager();
                timePickerDialog24h.show(fragmentManager, getTag());
            }
        });
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogs) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                    dialogView.post(new Runnable() {
                        public void run() {
                            revealShow(dialogView, true, dialog, R.id.dialogOHView, R.id.FABDialogWH);
                        }
                    });
                else
                    dialog.findViewById(R.id.dialogOHView).setVisibility(View.VISIBLE);

            }
        });
        dialogView.findViewById(R.id.FABDialogWH).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!dData1.getText().toString().equals("") && !dData2.getText().toString().equals("")) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                        dialogView.post(new Runnable() {
                            public void run() {
                                revealShow(dialogView, false, dialog, R.id.dialogOHView, R.id.FABDialogWH);
                            }
                        });
                    else {
                        dialog.dismiss();
                        dialog.findViewById(R.id.dialogOHView).setVisibility(View.INVISIBLE);
                    }
                    dialogView.findViewById(R.id.FABDialogWH).animate().alpha(0).setDuration(500);
                    dialogView.findViewById(R.id.FABDialogWH).animate().rotation(180).setDuration(800).setInterpolator(new AnticipateOvershootInterpolator());

                    mFAB.animate().alpha(1).setDuration(500);
                    mFAB.animate().rotation(90).setDuration(800).setInterpolator(new AnticipateOvershootInterpolator());

                    String OHMODE = "";
                    if (rb1.isChecked()) OHMODE = DataRecord.OHMode.PAID.toString();
                    if (rb2.isChecked()) OHMODE = DataRecord.OHMode.UNUSED.toString();
                    if (rb3.isChecked()) OHMODE = DataRecord.OHMode.USED.toString();

                    mDB.updateRecord(position + "", DataType, dDate.getText().toString(), dData1.getText().toString(), dData2.getText().toString(), OHMODE, dDate2.getText().toString());

                    mDataSet.clear();
                    mAdapter = new RecyclerViewAdapterOverhours(mDataSet, OverhoursFragment.this);
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
                                revealShow(dialogView, false, dialog, R.id.dialogOHView, R.id.FABDialogWH);
                            }
                        });
                    else {
                        dialog.dismiss();
                        dialog.findViewById(R.id.dialogOHView).setVisibility(View.INVISIBLE);
                    }
                    dialogView.findViewById(R.id.FABDialogWH).animate().alpha(0).setDuration(700);
                    dialogView.findViewById(R.id.FABDialogWH).animate().rotation(180).setDuration(800).setInterpolator(new AnticipateOvershootInterpolator());

                    mFAB.animate().alpha(1).setDuration(500);
                    mFAB.animate().rotation(90).setDuration(800).setInterpolator(new AnticipateOvershootInterpolator());
                    return true;
                }
                return false;
            }
        });
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        dialog.show();

        FloatingActionButton editFAB = (FloatingActionButton) dialogView.findViewById(R.id.FABDialogWH);
        editFAB.setImageResource(R.drawable.ic_mode_edit_black_24dp);
        editFAB.animate().alpha(1).setDuration(500);
        editFAB.setRotation(-90);
        editFAB.animate().rotation(0).setDuration(800).setInterpolator(new AnticipateOvershootInterpolator());
    }

    public void ButtonDelete(int pos) {
        final int position = pos;
        new AlertDialog.Builder(getActivity())
                .setTitle(getString(R.string.text_deleting))
                .setMessage(getString(R.string.text_areyousure))
                .setPositiveButton(getString(R.string.text_yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        mDB.removeRecord(mDataSet.get(position).getID());
                        mAdapter.removeItem(position);
                    }
                })
                .setNegativeButton(getString(R.string.text_no), null)
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

        final View dialogView = View.inflate(getActivity(), R.layout.dialog_overtime_add, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(dialogView)
                .setCancelable(false);
        final AlertDialog dialog = builder.create();
//
        final FloatingActionButton nFAB = (FloatingActionButton) dialogView.findViewById(R.id.FABDialogWH);

        final Calendar mCalendar = Calendar.getInstance();

        final EditText dDate = (EditText) dialogView.findViewById(R.id.dialogWHView_et_date);
        final EditText dData1 = (EditText) dialogView.findViewById(R.id.dialogWHView_et_wh1);
        final EditText dData2 = (EditText) dialogView.findViewById(R.id.dialogWHView_et_wh2);
        final EditText dDate2 = (EditText) dialogView.findViewById(R.id.dialogOHView_et_date2);

        final RadioButton rb1 = (RadioButton) dialogView.findViewById(R.id.radioButton1);
        final RadioButton rb2 = (RadioButton) dialogView.findViewById(R.id.radioButton2);
        final RadioButton rb3 = (RadioButton) dialogView.findViewById(R.id.radioButton3);

        rb1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rb1.setChecked(true);
                rb2.setChecked(false);
                rb3.setChecked(false);
                dDate2.setVisibility(View.VISIBLE);
            }
        });
        rb2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rb1.setChecked(false);
                rb2.setChecked(true);
                rb3.setChecked(false);
                dDate2.setVisibility(View.GONE);
            }
        });
        rb3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rb1.setChecked(false);
                rb2.setChecked(false);
                rb3.setChecked(true);
                dDate2.setVisibility(View.VISIBLE);
            }
        });

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

        dDate2.setText(date);

        dDate2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(new DatePickerDialog.OnDateSetListener() {

                    public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {
                        dDate2.setText(new StringBuilder().append(pad(day)).append("/").append(pad(month + 1)).append("/").append(year));
                    }

                }, mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH));
                FragmentManager fragmentManager = getActivity().getFragmentManager();

                datePickerDialog.show(fragmentManager, getTag());
            }
        });

        dData1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final TimePickerDialog timePickerDialog24h = TimePickerDialog.newInstance(new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(RadialPickerLayout view, int hourOfDay,
                                          int minute) {

                        dData1.setText(new StringBuilder().append(pad(hourOfDay)).append(":").append(pad(minute)));

                    }
                }, mCalendar.get(Calendar.HOUR_OF_DAY), 0, true);
                FragmentManager fragmentManager = getActivity().getFragmentManager();
                timePickerDialog24h.show(fragmentManager, getTag());
            }
        });
        dData2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final TimePickerDialog timePickerDialog24h = TimePickerDialog.newInstance(new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(RadialPickerLayout view, int hourOfDay,
                                          int minute) {

                        dData2.setText(new StringBuilder().append(pad(hourOfDay)).append(":").append(pad(minute)));

                    }
                }, mCalendar.get(Calendar.HOUR_OF_DAY), 0, true);
                FragmentManager fragmentManager = getActivity().getFragmentManager();
                timePickerDialog24h.show(fragmentManager, getTag());
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
                            revealShow(dialogView, true, dialog, R.id.dialogOHView, R.id.FABDialogWH);
                        }
                    }, 200);
                } else
                    dialog.findViewById(R.id.dialogOHView).setVisibility(View.VISIBLE);

            }
        });
        dialogView.findViewById(R.id.FABDialogWH).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!dData1.getText().toString().equals("") && !dData2.getText().toString().equals("")) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                        dialogView.post(new Runnable() {
                            public void run() {
                                revealShow(dialogView, false, dialog, R.id.dialogOHView, R.id.FABDialogWH, true);

                                int[] locations = new int[2];
                                nFAB.getLocationOnScreen(locations);
                                mFAB.setX(locations[0] - getPixels(56));
                                mFAB.setY(locations[1] - getPixels(77));

                                dialogView.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        final ArcAnimator animatorArc = ArcAnimator.createArcAnimator(mFAB, orgX+getPixels(28), orgY+getPixels(28), 0, Side.LEFT);
                                        animatorArc.setDuration(300);
                                        animatorArc.start();
                                    }
                                }, 330);
                            }
                        });
                    else {
                        dialog.dismiss();
                        dialog.findViewById(R.id.dialogOHView).setVisibility(View.INVISIBLE);
                    }
                    dialogView.findViewById(R.id.FABDialogWH).animate().alpha(0).setDuration(500);
                    dialogView.findViewById(R.id.FABDialogWH).animate().rotation(180).setDuration(800).setInterpolator(new AnticipateOvershootInterpolator());

                    mFAB.animate().alpha(1).setDuration(500);
                    mFAB.animate().rotation(90).setDuration(800).setInterpolator(new AnticipateOvershootInterpolator());

                    String OHMODE = "";
                    if (rb1.isChecked()) OHMODE = DataRecord.OHMode.PAID.toString();
                    if (rb2.isChecked()) OHMODE = DataRecord.OHMode.UNUSED.toString();
                    if (rb3.isChecked()) OHMODE = DataRecord.OHMode.USED.toString();

                    Log.d("SetChecked", OHMODE);

                    DataRecord data = new DataRecord(0, ACC_USER.toString(), DataType, dDate.getText().toString(), dData1.getText().toString(), dData2.getText().toString(), OHMODE, dDate2.getText().toString());
                    mDB.addRecord(data);
                    mAdapter.addItem(data, RecyclerViewAdapterOverhours.LAST_POSITION);

                    mDataSet.clear();
                    mAdapter = new RecyclerViewAdapterOverhours(mDataSet, OverhoursFragment.this);
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
                                revealShow(dialogView, false, dialog, R.id.dialogOHView, R.id.FABDialogWH, true);

                                int[] locations = new int[2];
                                nFAB.getLocationOnScreen(locations);
                                mFAB.setX(locations[0] - getPixels(56));
                                mFAB.setY(locations[1] - getPixels(77));

                                dialogView.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        final ArcAnimator animatorArc = ArcAnimator.createArcAnimator(mFAB, orgX+getPixels(28), orgY+getPixels(28), 0, Side.LEFT);
                                        animatorArc.setDuration(300);
                                        animatorArc.start();
                                    }
                                }, 330);
                            }
                        });
                    else {
                        dialog.dismiss();
                        dialog.findViewById(R.id.dialogOHView).setVisibility(View.INVISIBLE);
                    }
                    dialogView.findViewById(R.id.FABDialogWH).animate().alpha(0).setDuration(700);
                    dialogView.findViewById(R.id.FABDialogWH).animate().rotation(180).setDuration(800).setInterpolator(new AnticipateOvershootInterpolator());

                    mFAB.animate().alpha(1).setDuration(500);
                    mFAB.animate().rotation(90).setDuration(800).setInterpolator(new AnticipateOvershootInterpolator());
                    return true;
                }
                return false;
            }
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        dialog.show();
        dialogView.findViewById(R.id.FABDialogWH).animate().alpha(1).setDuration(500);
        dialogView.findViewById(R.id.FABDialogWH).animate().rotation(90).setDuration(800).setInterpolator(new AnticipateOvershootInterpolator());
    }

    public void LoadDataFromDatabase() {
        final ArrayList<DataRecord> _WorkHours = new ArrayList<>();
        try {
            List<DataRecord> Tab;
            if(_DisableFilter)
                Tab = mDB.getRecordsByType(DataType, "DESC", ACC_USER);
            else
                Tab = mDB.getRecordsFiltered(DataType, _SortingType, ACC_USER ,_MonthFilterEnabled , _MonthFilterValue, _YearFilterEnabled, _YearFilterValue);
            for (int i = 0; i < Tab.size(); ++i) {
                DataRecord x = Tab.get(i);
                _WorkHours.add(x);
            }
        } catch (SQLException ex) {
            Log.d("A", "" + ex.getMessage());
        }
        for (int i = 0; i < _WorkHours.size(); ++i) {
            mAdapter.addItem(_WorkHours.get(i), RecyclerViewAdapterOverhours.LAST_POSITION);
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
                editor.putBoolean("FILTER_" + DataType + "_" + ACC_USER +"_AutoFilter", _AutoFilter);
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
                mAdapter = new RecyclerViewAdapterOverhours(mDataSet, OverhoursFragment.this);
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


}
