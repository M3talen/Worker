package com.metalen.worker.fragments;

import com.metalen.worker.classes.DataRecord;

/**
 * Created by M3talen on 31.12.2015..
 */
public class SickLeaveFragment extends HolidaysFragment{

    private String DataType = DataRecord.Type.SICKLEAVE.toString();

    public SickLeaveFragment()
    {
        setDataType(DataType);
    }
}
