package com.metalen.worker.fragments;

import com.metalen.worker.classes.DataRecord;

/**
 * Created by Metalen on 20.2.2015..
 */
public class IntervencijeFragment extends WorkHoursFragment {

    private String DataType = DataRecord.Type.INTERVENCIJE.toString();

    public IntervencijeFragment() {
        setDataType(DataType);
    }

}
