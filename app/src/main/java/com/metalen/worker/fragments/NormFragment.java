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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.*;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.*;
import com.metalen.worker.MainActivity;
import com.metalen.worker.R;
import com.metalen.worker.SQL.SQLHandler;
import com.metalen.worker.adapters.RecyclerViewAdapterNorm;
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
 * Created by Metalen on 4.12.2014..
 */
public class NormFragment extends FragmentCore {

    private SQLHandler mDB;
    private RecyclerView mRecyclerView;
    private RecyclerViewAdapterNorm mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private FloatingActionButton mFAB;
    private List<DataRecord> mDataSet = new ArrayList<>();
    private View fragmentView;
    private String DataType = DataRecord.Type.NORMA.toString();

    private int ACC = 1;
    private String ACC_USER = "";

    private Drawable ACC_Cover;


    public NormFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentView = inflater.inflate(R.layout.fragment_norm, container, false);

        this.setHasOptionsMenu(true);

        mFAB = (FloatingActionButton) fragmentView.findViewById(R.id.FABNorma);
        mRecyclerView = (RecyclerView) fragmentView.findViewById(R.id.recyclerView);
        mDB = new SQLHandler(getActivity());
//
        mRecyclerView.setHasFixedSize(false);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mDataSet.clear();
        mAdapter = new RecyclerViewAdapterNorm(mDataSet, NormFragment.this);
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

        getSettingsForFilter(DataType);

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

        final View iCover = (View) dialogView.findViewById(R.id.header);
        iCover.setBackground(ACC_Cover);

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
                    mAdapter = new RecyclerViewAdapterNorm(mDataSet, NormFragment.this);
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
        //mFAB.animate().alpha(0).setDuration(500);

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
        final Calendar mCalendar = Calendar.getInstance();

        final EditText dDate = (EditText) dialogView.findViewById(R.id.dialogNormView_et_date);
        final EditText dData = (EditText) dialogView.findViewById(R.id.dialogNormView_et_norm);

        final FloatingActionButton nFAB = (FloatingActionButton) dialogView.findViewById(R.id.FABDialogNorma);

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
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                    dialogView.post(new Runnable() {
                        public void run() {

                            final ArcAnimator animarc = ArcAnimator.createArcAnimator(mFAB, width / 2, height / 2 - mFAB.getHeight(), 30, Side.LEFT);
                            animarc.setDuration(200);
                            animarc.start();
                            dialogView.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    revealShow(dialogView, true, dialog, R.id.dialogNormView, R.id.FABDialogNorma);
                                }
                            }, 200);

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
                    nFAB.animate().alpha(0).setDuration(500);
                    nFAB.animate().rotation(180).setDuration(800).setInterpolator(new AnticipateOvershootInterpolator());

                    mFAB.animate().alpha(1).setDuration(500);
                    mFAB.animate().rotation(90).setDuration(800).setInterpolator(new AnticipateOvershootInterpolator());

                    DataRecord data = new DataRecord(0, ACC_USER.toString(), DataType, dDate.getText().toString(), dData.getText().toString(), "", "", "");
                    mDB.addRecord(data);
                    mAdapter.addItem(data, RecyclerViewAdapterNorm.LAST_POSITION);

                    mDataSet.clear();
                    mAdapter = new RecyclerViewAdapterNorm(mDataSet, NormFragment.this);
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

                    //nFAB.animate().alpha(0).setDuration(700);
                    //nFAB.animate().rotation(180).setDuration(800).setInterpolator(new AnticipateOvershootInterpolator());

                    //mFAB.animate().alpha(1).setDuration(500);
                    //mFAB.animate().rotation(90).setDuration(800).setInterpolator(new AnticipateOvershootInterpolator());
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
        final ArrayList<DataRecord> _Norme = new ArrayList<>();
        try {
            List<DataRecord> Tab;
            if(_DisableFilter)
                Tab = mDB.getRecordsByType(DataType, "DESC", ACC_USER);
            else
                Tab = mDB.getRecordsFiltered(DataType, "DESC", ACC_USER, _SortingType,_MonthFilterEnabled , _MonthFilterValue, _YearFilterEnabled, _YearFilterValue);
            for (int i = 0; i < Tab.size(); ++i) {
                DataRecord x = Tab.get(i);
                _Norme.add(x);
            }
        } catch (SQLException ex) {
            Log.d("A", "" + ex.getMessage());
        }
        for (int i = 0; i < _Norme.size(); ++i) {
            mAdapter.addItem(_Norme.get(i), RecyclerViewAdapterNorm.LAST_POSITION);
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
        getSettingsForFilter(DataType);
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
        if(_DisableFilter)
            setEnabledLayoutsDisabler(mAutoFilter, mDisableFilter, mLayout1, mLayout2, mLayout3);
        if(_AutoFilter)
            setEnabledLayoutsAuto(mAutoFilter, mDisableFilter, mLayout1, mLayout2, mLayout3);
        //
        mAutoFilter.setChecked(_AutoFilter);
        mDisableFilter.setChecked(_DisableFilter);
        mYearFilter.setChecked(_YearFilterEnabled);
        mMonthFilter.setChecked(_MonthFilterEnabled);
        if (_SortingType.equals("DESC")) {
            mSortingTypeDESC.setChecked(true);
            mSortingTypeASC.setChecked(false);
        }else
        {
            mSortingTypeDESC.setChecked(false);
            mSortingTypeASC.setChecked(true);
        }
        mYearFilterValue.setText(_YearFilterValue + "");
        mMonthFilterValue.setText(_MonthFilterValue + "");
        //
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
                    dialog.findViewById(R.id.dialogNormView).setVisibility(View.VISIBLE);

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
                    dialog.findViewById(R.id.dialogNormView).setVisibility(View.INVISIBLE);
                }

                //APPLY FILTER
                SharedPreferences.Editor editor = getActivity().getSharedPreferences("Worker", getActivity().MODE_PRIVATE).edit();
                editor.putBoolean("FILTER_" + DataType + "_AutoFilter", _AutoFilter);
                editor.putBoolean("FILTER_" + DataType + "_YearFilterEnabled", _YearFilterEnabled);
                editor.putBoolean("FILTER_" + DataType + "_MonthFilterEnabled", _MonthFilterEnabled);
                editor.putBoolean("FILTER_" + DataType + "_DisableFilter", _DisableFilter);
                editor.putInt("FILTER_" + DataType + "_YearFilterValue", _YearFilterValue);
                editor.putInt("FILTER_" + DataType + "_MonthFilterValue", _MonthFilterValue);
                if (_SortingType.equals("DESC"))
                    editor.putString("FILTER_" + DataType + "_SortingType", "DESC");
                else
                    editor.putString("FILTER_" + DataType + "_SortingType", "ASC");
                editor.commit();

                mDataSet.clear();
                mAdapter = new RecyclerViewAdapterNorm(mDataSet, NormFragment.this);
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
                        dialog.findViewById(R.id.dialogNormView).setVisibility(View.INVISIBLE);
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