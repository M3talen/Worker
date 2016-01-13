package com.metalen.worker.SQL;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.metalen.worker.classes.DataRecord;
import com.percolate.foam.FoamEvent;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Metalen on 6.1.2015..
 */
public class SQLHandler extends SQLiteOpenHelper {

    private static final int VER_LAUNCH = 1;

    private static final int DATABASE_VERSION = VER_LAUNCH;
    private static final String DATABASE_NAME = "WorkerSQL";
    private static final String TABLE_RECORDS = "DataRecords";

    private static final String KEY_ID = "ID";
    private static final String KEY_TYPE = "TYPE";
    private static final String KEY_ACC = "ACC";
    private static final String KEY_DATE = "DATE";
    private static final String KEY_DATA_1 = "DATA_1";
    private static final String KEY_DATA_2 = "DATA_2";
    private static final String KEY_DATA_3 = "DATA_3";
    private static final String KEY_DATA_4 = "DATA_4";
    private Context mContext;

    public SQLHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_BOOK_TABLE = "CREATE TABLE DataRecords ( " + "ID INTEGER PRIMARY KEY AUTOINCREMENT, " + "ACC TEXT, " + "TYPE TEXT, "
                + "DATE TEXT, " + "DATA_1 TEXT, " + "DATA_2 TEXT, " + "DATA_3 TEXT, " + "DATA_4 TEXT )";
        db.execSQL(CREATE_BOOK_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != DATABASE_VERSION) {
            Log.w("SQLHandler", "Destroying old data during upgrade");

            db.execSQL("DROP TABLE IF EXISTS Norma");

            onCreate(db);
        }
    }

    public void addRecord(DataRecord Record) {
        Log.d("Added Record to SQL", Record.toString());
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_ACC, Record.getACC());
        values.put(KEY_TYPE, Record.getTYPE());
        values.put(KEY_DATE, Record.getDATE());
        values.put(KEY_DATA_1, Record.getDATA_1());
        values.put(KEY_DATA_2, Record.getDATA_2());
        values.put(KEY_DATA_3, Record.getDATA_3());
        values.put(KEY_DATA_4, Record.getDATA_4());
        new FoamEvent().track((Activity) mContext, "Adding record for " + Record.getTYPE());
        db.insert(TABLE_RECORDS, null, values);
        db.close();
    }

    public List<DataRecord> getRecords(String tID, String tACC) {
        List<DataRecord> Record = new LinkedList<DataRecord>();

        String query = "SELECT * FROM " + TABLE_RECORDS + " WHERE ID = '" + tID + "' AND ACC = '" + tACC + "'";

        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        DataRecord mList = null;

        if (cursor.moveToFirst()) {
            do {
                mList = new DataRecord();
                mList.setID(Integer.parseInt(cursor.getString(0)));
                mList.setACC(cursor.getString(1));
                mList.setTYPE(cursor.getString(2));
                mList.setDATE(cursor.getString(3));
                mList.setDATA_1(cursor.getString(4));
                mList.setDATA_2(cursor.getString(5));
                mList.setDATA_3(cursor.getString(6));
                mList.setDATA_4(cursor.getString(7));
                Record.add(mList);

            } while (cursor.moveToNext());

        }

        Log.d("Getting all records", Record.toString());
        return Record;
    }

    public List<DataRecord> getRecordsByType(String tType, String tSort, String tACC) {
        List<DataRecord> Record = new LinkedList<DataRecord>();
        // Log.d("aa", tType);
        String query = "";
        if (new String(tSort).equals("ASC")) {       //  ORDER BY CAST(substr(DATE,7) AS INTEGER) ASC, CAST(substr(DATE,4,1) AS INTEGER) ASC, CAST(substr(DATE,1,1) AS INTEGER) ASC
            query = "SELECT * FROM " + TABLE_RECORDS + " WHERE ACC = '" + tACC + "' AND TYPE = '" + tType + "' ORDER BY CAST(substr(DATE,7) AS INTEGER) ASC, CAST(substr(DATE,4,2) AS INTEGER) ASC, CAST(substr(DATE,1,2) AS INTEGER) ASC";
        }
        if (new String(tSort).equals("DESC")) {
            query = "SELECT * FROM " + TABLE_RECORDS + " WHERE ACC = '" + tACC + "' AND TYPE = '" + tType + "' ORDER BY CAST(substr(DATE,7) AS INTEGER) DESC, CAST(substr(DATE,4,2) AS INTEGER) DESC, CAST(substr(DATE,1,2) AS INTEGER) DESC";
        }

        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        DataRecord mList = null;

        if (cursor.moveToFirst()) {
            do {
                mList = new DataRecord();
                mList.setID(Integer.parseInt(cursor.getString(0)));
                mList.setACC(cursor.getString(1));
                mList.setTYPE(cursor.getString(2));
                mList.setDATE(cursor.getString(3));
                mList.setDATA_1(cursor.getString(4));
                mList.setDATA_2(cursor.getString(5));
                mList.setDATA_3(cursor.getString(6));
                mList.setDATA_4(cursor.getString(7));
                Record.add(mList);

            } while (cursor.moveToNext());

        }

        Log.d("Getting all records", Record.toString());
        return Record;
    }

    public List<DataRecord> getRecordsByYear(String tYear, String tACC) {
        List<DataRecord> Record = new LinkedList<DataRecord>();

        String query = "SELECT * FROM " + TABLE_RECORDS + " WHERE ACC = '" + tACC + "' AND substr(DATE,7) = '" + tYear + "'";

        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        DataRecord mList = null;

        if (cursor.moveToFirst()) {
            do {
                mList = new DataRecord();
                mList.setID(Integer.parseInt(cursor.getString(0)));
                mList.setACC(cursor.getString(1));
                mList.setTYPE(cursor.getString(2));
                mList.setDATE(cursor.getString(3));
                mList.setDATA_1(cursor.getString(4));
                mList.setDATA_2(cursor.getString(5));
                mList.setDATA_3(cursor.getString(6));
                mList.setDATA_4(cursor.getString(7));
                Record.add(mList);

            } while (cursor.moveToNext());

        }

        Log.d("Getting all records", Record.toString());
        return Record;
    }

    public List<DataRecord> getRecordsByDate(String tDate, String tSort, String tACC) {
        List<DataRecord> Record = new LinkedList<DataRecord>();
        String query = "";
        if (new String(tSort).equals("ASC")) {
            query = "SELECT * FROM " + TABLE_RECORDS + " WHERE ACC = '" + tACC + "' AND DATE = '" + tDate + "' ORDER BY CAST(substr(DATE,7) AS INTEGER) ASC, CAST(substr(DATE,4,2) AS INTEGER) ASC, CAST(substr(DATE,1,2) AS INTEGER) ASC";
        }
        if (new String(tSort).equals("DESC")) {
            query = "SELECT * FROM " + TABLE_RECORDS + " WHERE ACC = '" + tACC + "' AND DATE = '" + tDate + "' ORDER BY CAST(substr(DATE,7) AS INTEGER) DESC, CAST(substr(DATE,4,2) AS INTEGER) DESC, CAST(substr(DATE,1,2) AS INTEGER) DESC";
        }

        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        DataRecord mList = null;

        if (cursor.moveToFirst()) {
            do {
                mList = new DataRecord();
                mList.setID(Integer.parseInt(cursor.getString(0)));
                mList.setACC(cursor.getString(1));
                mList.setTYPE(cursor.getString(2));
                mList.setDATE(cursor.getString(3));
                mList.setDATA_1(cursor.getString(4));
                mList.setDATA_2(cursor.getString(5));
                mList.setDATA_3(cursor.getString(6));
                mList.setDATA_4(cursor.getString(7));
                Record.add(mList);

            } while (cursor.moveToNext());

        }

        Log.d("Getting all records", Record.toString());
        return Record;
    }

    public List<DataRecord> getRecordsFiltered(String tType, String tSort, String tACC, boolean _MonthFilterEnabled, int monthFilterValue, boolean _YearFilterEnabled, int yearFilterValue) {
        List<DataRecord> Record = new LinkedList<DataRecord>();
        String query = "";
        if (_MonthFilterEnabled)
            query = "SELECT * FROM DataRecords WHERE ACC = '" + tACC + "'" +
                    " AND TYPE = '" + tType + "'" +
                    " AND CAST (substr(DATE, 4, 2) AS INTEGER) = '" + monthFilterValue + "'" +
                    " ORDER BY" +
                    " CAST (substr(DATE, 7) AS INTEGER) " + tSort + "," +
                    " CAST (substr(DATE, 4, 2) AS INTEGER) " + tSort + "," +
                    " CAST (substr(DATE, 1, 2) AS INTEGER) " + tSort + "";
        if (_YearFilterEnabled)
            query = "SELECT" +
                    " *" +
                    " FROM" +
                    " DataRecords" +
                    " WHERE" +
                    " ACC = '" + tACC + "'" +
                    " AND TYPE = '" + tType + "'" +
                    " AND CAST (substr(DATE, 7) AS INTEGER) = '" + yearFilterValue + "'" +
                    " ORDER BY" +
                    " CAST (substr(DATE, 7) AS INTEGER) " + tSort + "," +
                    " CAST (substr(DATE, 4, 2) AS INTEGER) " + tSort + "," +
                    " CAST (substr(DATE, 1, 2) AS INTEGER) " + tSort + "";

        if (_YearFilterEnabled && _MonthFilterEnabled)
            query = "SELECT" +
                    " *" +
                    " FROM" +
                    " DataRecords" +
                    " WHERE" +
                    " ACC = '" + tACC + "'" +
                    " AND TYPE = '" + tType + "'" +
                    " AND CAST (substr(DATE, 7) AS INTEGER) = '" + yearFilterValue + "'" +
                    " AND CAST (substr(DATE, 4, 2) AS INTEGER) = '" + monthFilterValue + "'" +
                    " ORDER BY" +
                    " CAST (substr(DATE, 7) AS INTEGER) " + tSort + "," +
                    " CAST (substr(DATE, 4, 2) AS INTEGER) " + tSort + "," +
                    " CAST (substr(DATE, 1, 2) AS INTEGER) " + tSort + "";

        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        DataRecord mList = null;

        if (cursor.moveToFirst()) {
            do {
                mList = new DataRecord();
                mList.setID(Integer.parseInt(cursor.getString(0)));
                mList.setACC(cursor.getString(1));
                mList.setTYPE(cursor.getString(2));
                mList.setDATE(cursor.getString(3));
                mList.setDATA_1(cursor.getString(4));
                mList.setDATA_2(cursor.getString(5));
                mList.setDATA_3(cursor.getString(6));
                mList.setDATA_4(cursor.getString(7));
                Record.add(mList);

            } while (cursor.moveToNext());

        }

        Log.d("Getting all records", Record.toString());
        return Record;
    }

    public void updateRecord(String ID, String tType, String tDate, String tData_1, String tData_2, String tData_3, String tData_4) {
        String query = " UPDATE " + TABLE_RECORDS + " SET TYPE = '" + tType + "', DATE = '" + tDate + "', DATA_1 = '"
                + tData_1 + "', DATA_2 = '" + tData_2 + "',DATA_3 = '" + tData_3 + "', DATA_4 = '" + tData_4 + "' WHERE ID = '" + ID + "'";
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(query);
        new FoamEvent().track((Activity) mContext, "Updaing record for " + tType);
    }

    public void removeRecord(int id) {
        String query = " DELETE FROM " + TABLE_RECORDS + " WHERE ID = '" + id + "'";
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(query);
        new FoamEvent().track((Activity) mContext, "Removing record");
        Log.d("Removing", "ID : " + id);
    }


}
