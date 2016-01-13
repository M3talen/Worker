package com.metalen.worker.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.CardView;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.gms.common.SignInButton;
import com.metalen.worker.MainActivity;
import com.metalen.worker.R;
import com.metalen.worker.SQL.SQLHandler;
import com.metalen.worker.animator.WorkerPathAnim;
import com.metalen.worker.classes.DataRecord;
import it.neokree.materialnavigationdrawer.elements.MaterialAccount;
import net.steamcrafted.loadtoast.LoadToast;
import oak.svg.AnimatedSvgView;

import java.io.*;
import java.util.ArrayList;

/**
 * Created by Metalen on 30.11.2014..
 */
public class FragmentSettings extends FragmentCore {

    private Handler mHandler = new Handler();

    public static final int LOAD_IMAGE_RESULTS = 1;
    SignInButton gSignInButton;
    TextView mTextUser1, mTextJob1, mTextUser2, mTextJob2, mTextNorma1, mTextNorma2;
    ImageView mIcon1, mIcon2;
    Button mEdit1, mEdit2, mBtNorma1;
    MaterialAccount account, account1, tAccount;
    AlertDialog dialog;
    ImageView dIcon, dCover;
    CardView normaCard;
    SQLHandler mainDB;
    String AccountNo = DataRecord.Account.ACC1.toString();

    private AnimatedSvgView mAnimatedSvgView;
    private String dFileName = null;
    LoadToast mLoadToast;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_settings, container, false);
        mTextUser1 = (TextView) fragmentView.findViewById(R.id.settings_textView1);
        mTextJob1 = (TextView) fragmentView.findViewById(R.id.settings_textView2);
        mTextUser2 = (TextView) fragmentView.findViewById(R.id.settings_textView3);
        mTextJob2 = (TextView) fragmentView.findViewById(R.id.settings_textView4);
        mIcon1 = (ImageView) fragmentView.findViewById(R.id.settings_imageView1);
        mIcon2 = (ImageView) fragmentView.findViewById(R.id.settings_imageView2);
        mEdit1 = (Button) fragmentView.findViewById(R.id.settings_btn_edit1);
        mEdit2 = (Button) fragmentView.findViewById(R.id.settings_btn_edit2);
        mAnimatedSvgView = (AnimatedSvgView) fragmentView.findViewById(R.id.animated_svg_view);

        normaCard = (CardView) fragmentView.findViewById(R.id.settings_CardView3);
        mTextNorma1 = (TextView) fragmentView.findViewById(R.id.settings_norma_tv1);
        mTextNorma2 = (TextView) fragmentView.findViewById(R.id.settings_norma_tv2);
        mBtNorma1 = (Button) fragmentView.findViewById(R.id.settings_norma_bt1);

        mainDB = new SQLHandler(getActivity());
        mLoadToast = new LoadToast(getActivity());
        getAccountData();

        boolean dbFound = false;

        if (isPackageInstalled("com.metalen.norm", getActivity())) {
            normaCard.setVisibility(View.VISIBLE);
            mTextNorma1.setText(Html.fromHtml(this.getString(R.string.settings_norm_found)));
            File dbfile = null;
            try {
                dbfile = new File(Environment.getExternalStorageDirectory().getPath() + "/Norm/SQLNorma");
                if (dbfile.exists()) {
                    mTextNorma2.setText(Html.fromHtml(this.getString(R.string.settings_db_bk_1)));
                    dbFound = true;
                } else {
                    mTextNorma2.setText(Html.fromHtml(this.getString(R.string.settings_db_bk_2)));
                    dbFound = false;
                }
            } catch (Exception e) {e.printStackTrace();}
        }

        final boolean finalDbFound = dbFound;
        mBtNorma1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Handler mnHandler = new Handler();
                mnHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (finalDbFound) {
                            mLoadToast.setText(getString(R.string.settings_importing));
                            DisplayMetrics displayMetrics = getActivity().getResources().getDisplayMetrics();
                            mLoadToast.setTranslationY(Math.round(100 * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT)));
                            mLoadToast.show();
                            new AlertDialog.Builder(getActivity())
                                    .setTitle(getString(R.string.settings_import))
                                    .setMessage(getString(R.string.settings_import_text))
                                    .setPositiveButton(getString(R.string.text_yes), new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            new AlertDialog.Builder(getActivity())
                                                    .setTitle(getString(R.string.text_account))
                                                    .setMessage(getString(R.string.text_choseAccount))
                                                    .setPositiveButton(getString(R.string.account_no2), new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            AccountNo = DataRecord.Account.ACC2.toString();
                                                            importDatabaseNorm();
                                                        }
                                                    })
                                                    .setNegativeButton(getString(R.string.account_no1), new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            AccountNo = DataRecord.Account.ACC1.toString();
                                                            importDatabaseNorm();
                                                        }
                                                    })
                                                    .show();
                                        }
                                    })
                                    .setNegativeButton(getString(R.string.text_no), new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            mLoadToast.error();
                                        }
                                    })
                                    .show();
                        } else {
                            new AlertDialog.Builder(getActivity())
                                    .setTitle(getString(R.string.text_error))
                                    .setMessage(getString(R.string.text_needtocratebackup))
                                    .setPositiveButton(getString(R.string.text_ok), new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    })
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .show();
                        }
                    }
                });
            }
        });

        mAnimatedSvgView.setGlyphStrings(WorkerPathAnim.WORKER_PATH);
        mAnimatedSvgView.setFillPaints(
                new int[]{255, 255},
                new int[]{254, 40},
                new int[]{254, 40},
                new int[]{113, 40});
        int traceColor = Color.argb(255, 0, 0, 0);
        int[] traceColors = new int[2];
        int residueColor = Color.argb(50, 0, 0, 0);
        int[] residueColors = new int[2];
        for (int i = 0; i < traceColors.length; i++) {
            traceColors[i] = traceColor;
            residueColors[i] = residueColor;
        }
        mAnimatedSvgView.setTraceColors(traceColors);
        mAnimatedSvgView.setTraceResidueColors(residueColors);

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mAnimatedSvgView.start();
            }
        }, 1000);

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mAnimatedSvgView.animate().alpha(0.0F).setDuration(2000).setInterpolator(new AccelerateDecelerateInterpolator());
            }
        }, 5000);

        mEdit1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editDialog(account, mIcon1);
            }
        });
        mEdit2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editDialog(account1, mIcon2);
            }
        });


        return fragmentView;
    }

    protected void importDatabaseNorm() {
        try {
            Log.d("Record", "Starting");
            File dbfile = new File(Environment.getExternalStorageDirectory().getPath() + "/Norm/SQLNorma");
            Log.d("Record", "File : " + dbfile.exists());
            SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(dbfile, null);
            Log.d("Record", "DB : " + db.isOpen());

            ArrayList<DataRecord> mDataRecords = new ArrayList<>();

            String query = "SELECT * FROM Norma";
            Cursor cursor = db.rawQuery(query, null);
            Log.d("Record", "Starting read");
            if (cursor.moveToFirst()) {
                do {
                    Log.d("Record", " Year: " + cursor.getString(1) +
                            " Month: " + cursor.getString(2) +
                            " Day: " + cursor.getString(3) +
                            " Mode: " + cursor.getString(4) +
                            " Norma: " + cursor.getString(5) +
                            " Data1: " + cursor.getString(6) +
                            " Data2: " + cursor.getString(7));
                    DataRecord mDataRecord = new DataRecord();
                    mDataRecord.setACC(AccountNo);
                    mDataRecord.setDATE(new StringBuilder()
                            .append(pad(Integer.parseInt(cursor.getString(3))))
                            .append("/").append(pad(Integer.parseInt(cursor.getString(2))))
                            .append("/").append(cursor.getString(1)).toString());
                    if (cursor.getString(4).equals("MODE_NORMA")) {
                        mDataRecord.setTYPE(DataRecord.Type.NORMA.toString());
                        mDataRecord.setDATA_1(cursor.getString(5));
                    }
                    if (cursor.getString(4).equals("MODE_DOPUST")) {
                        mDataRecord.setTYPE(DataRecord.Type.HOLIDAYS.toString());
                        String s = cursor.getString(5);
                        mDataRecord.setDATA_1(s.indexOf(".") < 0 ? s : s.replaceAll("0*$", "").replaceAll("\\.$", ""));
                    }
                    if (cursor.getString(4).equals("MODE_WORKING_HOURS")) {
                        mDataRecord.setTYPE(DataRecord.Type.WORK_HOURS.toString());
                        mDataRecord.setDATA_1(cursor.getString(6));
                        mDataRecord.setDATA_2(cursor.getString(7));
                    }
                    if (cursor.getString(4).equals("MODE_OVER_PAID")) {
                        mDataRecord.setTYPE(DataRecord.Type.OVERHOURS.toString());
                        String[] Data1 = cursor.getString(6).split(" - ");
                        mDataRecord.setDATA_1(Data1[0]);
                        mDataRecord.setDATA_2(Data1[1]);
                        mDataRecord.setDATA_3(DataRecord.OHMode.PAID.toString());
                        String[] Data2 = cursor.getString(7).replace(" ", "").split("/");
                        if (!Data2[0].equals(""))
                            mDataRecord.setDATA_4(new StringBuilder()
                                    .append(pad(Integer.parseInt(Data2[0])))
                                    .append("/").append(pad(Integer.parseInt(Data2[1])))
                                    .append("/").append(Data2[2]).toString());
                    }
                    if (cursor.getString(4).equals("MODE_OVER_USED")) {
                        mDataRecord.setTYPE(DataRecord.Type.OVERHOURS.toString());
                        String[] Data1 = cursor.getString(6).split(" - ");
                        mDataRecord.setDATA_1(Data1[0]);
                        mDataRecord.setDATA_2(Data1[1]);
                        mDataRecord.setDATA_3(DataRecord.OHMode.USED.toString());
                        String[] Data2 = cursor.getString(7).replace(" ", "").split("/");
                        if (!Data2[0].equals(""))
                            mDataRecord.setDATA_4(new StringBuilder()
                                    .append(pad(Integer.parseInt(Data2[0])))
                                    .append("/").append(pad(Integer.parseInt(Data2[1])))
                                    .append("/").append(Data2[2]).toString());
                    }
                    if (cursor.getString(4).equals("MODE_OVER_UNUSED")) {
                        mDataRecord.setTYPE(DataRecord.Type.OVERHOURS.toString());
                        String[] Data1 = cursor.getString(6).split(" - ");
                        mDataRecord.setDATA_1(Data1[0]);
                        mDataRecord.setDATA_2(Data1[1]);
                        mDataRecord.setDATA_3(DataRecord.OHMode.UNUSED.toString());
                    }
                    mDataRecords.add(mDataRecord);
                } while (cursor.moveToNext());

                for (int i = 0; i < mDataRecords.size(); i++)
                    mainDB.addRecord(mDataRecords.get(i));

                mLoadToast.success();
            }
        } catch (Exception e) {
            e.printStackTrace();
            mLoadToast.error();
        }
    }

    protected void getAccountData() {
        account = ((MainActivity) getActivity()).getUser(0);
        account1 = ((MainActivity) getActivity()).getUser(1);

        mTextUser1.setText(account.getTitle());
        mTextJob1.setText(account.getSubTitle());
        try {
            mIcon1.setImageBitmap(((BitmapDrawable) account.getCircularPhoto()).getBitmap());
        } catch (Exception e) {e.printStackTrace();}

        mTextUser2.setText(account1.getTitle());
        mTextJob2.setText(account1.getSubTitle());
        try {
            mIcon2.setImageBitmap(((BitmapDrawable) account1.getCircularPhoto()).getBitmap());
        } catch (Exception e) {e.printStackTrace();}
    }

    private boolean isPackageInstalled(String packagename, Context context) {
        PackageManager pm = context.getPackageManager();
        try {
            pm.getPackageInfo(packagename, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    protected void editDialog(final MaterialAccount accountx, final ImageView imx) {
        final View dialogView = View.inflate(getActivity(), R.layout.dialog_settings_useredit, null);

        tAccount = accountx;

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(dialogView)
                .setCancelable(false);
        dialog = builder.create();

        final EditText dUser = (EditText) dialogView.findViewById(R.id.dialog_settings_edit_et_user);
        final EditText dJob = (EditText) dialogView.findViewById(R.id.dialog_settings_edit_et_job);
        dIcon = (ImageView) dialogView.findViewById(R.id.dialog_settings_edit_iv_icon);
        dCover = (ImageView) dialogView.findViewById(R.id.dialog_settings_edit_header);

        dUser.setText(accountx.getTitle());
        dJob.setText(accountx.getSubTitle());
        dIcon.setImageBitmap(((BitmapDrawable) accountx.getCircularPhoto()).getBitmap());
        dCover.setBackground(new BitmapDrawable(getActivity().getResources(), ((BitmapDrawable) accountx.getBackground()).getBitmap()));

        dIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (accountx.getAccountNumber() == 0)
                    dFileName = "user";
                else
                    dFileName = "user2";
                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, LOAD_IMAGE_RESULTS);
                dIcon.setImageBitmap(((BitmapDrawable) accountx.getCircularPhoto()).getBitmap());
                dCover.setBackground(new BitmapDrawable(getActivity().getResources(), ((BitmapDrawable) accountx.getBackground()).getBitmap()));
            }
        });
        dCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (accountx.getAccountNumber() == 0)
                    dFileName = "cover";
                else
                    dFileName = "cover2";
                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, LOAD_IMAGE_RESULTS);
                dIcon.setImageBitmap(((BitmapDrawable) accountx.getCircularPhoto()).getBitmap());
                dCover.setBackground(new BitmapDrawable(getActivity().getResources(), ((BitmapDrawable) accountx.getBackground()).getBitmap()));
            }
        });
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogs) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                    dialogView.post(new Runnable() {
                        public void run() {
                            revealShow(dialogView, true, dialog, R.id.dialogSettingsEdit, R.id.dialog_settings_edit_FAB, (imx.getLeft() + imx.getRight()) / 2, (imx.getTop() + imx.getBottom()) / 2);
                        }
                    });
                else
                    dialog.findViewById(R.id.dialogSettingsEdit).setVisibility(View.VISIBLE);

                dIcon.setImageBitmap(((BitmapDrawable) accountx.getCircularPhoto()).getBitmap());
                dCover.setBackground(new BitmapDrawable(getActivity().getResources(), ((BitmapDrawable) accountx.getBackground()).getBitmap()));
            }
        });
        dialogView.findViewById(R.id.dialog_settings_edit_FAB).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                    dialogView.post(new Runnable() {
                        public void run() {
                            revealShow(dialogView, false, dialog, R.id.dialogSettingsEdit, R.id.dialog_settings_edit_FAB, (imx.getLeft() + imx.getRight()) / 2, (imx.getTop() + imx.getBottom()) / 2);
                        }
                    });
                else {
                    dialog.dismiss();
                    dialog.findViewById(R.id.dialogSettingsEdit).setVisibility(View.INVISIBLE);
                }
                dialogView.findViewById(R.id.dialog_settings_edit_FAB).animate().alpha(0).setDuration(500);
                dialogView.findViewById(R.id.dialog_settings_edit_FAB).animate().rotation(180).setDuration(800).setInterpolator(new AnticipateOvershootInterpolator());

                accountx.setTitle(dUser.getText().toString());
                accountx.setSubTitle(dJob.getText().toString());

                SharedPreferences.Editor editor = getActivity().getSharedPreferences("Worker", getActivity().MODE_PRIVATE).edit();
                if (accountx.getAccountNumber() == 0) {
                    editor.putString("User0", dUser.getText().toString());
                    editor.putString("Job0", dJob.getText().toString());
                } else {
                    editor.putString("User1", dUser.getText().toString());
                    editor.putString("Job1", dJob.getText().toString());
                }
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
                                revealShow(dialogView, false, dialog, R.id.dialogSettingsEdit, R.id.dialog_settings_edit_FAB, (int) imx.getX(), (int) imx.getY());
                            }
                        });
                    else {
                        dialog.dismiss();
                        dialog.findViewById(R.id.dialogSettingsEdit).setVisibility(View.INVISIBLE);
                    }
                    dialogView.findViewById(R.id.dialog_settings_edit_FAB).animate().alpha(0).setDuration(700);
                    dialogView.findViewById(R.id.dialog_settings_edit_FAB).animate().rotation(180).setDuration(800).setInterpolator(new AnticipateOvershootInterpolator());

                    ((MainActivity) getActivity()).forceUpdateFragment();
                    return true;
                }
                return false;
            }
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        dialog.show();

        FloatingActionButton editFAB = (FloatingActionButton) dialogView.findViewById(R.id.dialog_settings_edit_FAB);
        editFAB.animate().alpha(1).setDuration(500);
        editFAB.setRotation(-90);
        editFAB.animate().rotation(0).setDuration(800).setInterpolator(new AnticipateOvershootInterpolator());

        ((MainActivity) getActivity()).forceNotifyAccountChange();
    }

    public int GCD(int a, int b) {
        return b == 0 ? a : GCD(b, a % b);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        InputStream ImageStream = null;

        if (requestCode == LOAD_IMAGE_RESULTS && resultCode == Activity.RESULT_OK && data != null) {
            try {

                Uri pickedImage = data.getData();
                ImageStream = getActivity().getContentResolver().openInputStream(pickedImage);

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 5;

                Bitmap decodedImage = BitmapFactory.decodeStream(ImageStream, null, options);

                FileOutputStream out = null;

                final File direct = new File(Environment.getExternalStorageDirectory()
                        + "/Worker");

                final File mFile = new File(direct + "/" + dFileName + ".jpg");

                try {
                    if (!direct.exists()) direct.mkdir();
                    out = new FileOutputStream(mFile);
                    decodedImage.compress(Bitmap.CompressFormat.JPEG, 90, out);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (out != null) {
                            out.close();

                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (ImageStream != null) {
                    try {
                        ImageStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        }
        ((MainActivity) getActivity()).forceNotifyAccountChangeWithChange(dFileName);
        updateDisplayData();
        dIcon.setImageBitmap(((BitmapDrawable) tAccount.getCircularPhoto()).getBitmap());
        dCover.setBackground(new BitmapDrawable(getActivity().getResources(), ((BitmapDrawable) tAccount.getBackground()).getBitmap()));
    }


    private void updateDisplayData() {
        final File direct = new File(Environment.getExternalStorageDirectory()
                + "/Worker");
        if (dFileName == "user" || dFileName == "cover") {
            account.setPhoto(BitmapFactory.decodeFile(new File(direct + "/user.jpg").toString()));
            account.setBackground(BitmapFactory.decodeFile(new File(direct + "/cover.jpg").toString()));
            dIcon.setImageBitmap(((BitmapDrawable) account.getCircularPhoto()).getBitmap());
            dCover.setBackground(new BitmapDrawable(getActivity().getResources(), ((BitmapDrawable) account.getBackground()).getBitmap()));
            ((MainActivity) getActivity()).forceNotifyAccountChange();
        } else {
            account1.setPhoto(BitmapFactory.decodeFile(new File(direct + "/user2.jpg").toString()));
            account1.setBackground(BitmapFactory.decodeFile(new File(direct + "/cover2.jpg").toString()));
            dIcon.setImageBitmap(((BitmapDrawable) account1.getCircularPhoto()).getBitmap());
            dCover.setBackground(new BitmapDrawable(getActivity().getResources(), ((BitmapDrawable) account1.getBackground()).getBitmap()));
            ((MainActivity) getActivity()).forceNotifyAccountChange();
        }
        ((MainActivity) getActivity()).forceNotifyAccountChange();
        ((MainActivity) getActivity()).forceUpdateFragment();
        dialog.dismiss();
        dialog.show();

    }

}
