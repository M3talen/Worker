package com.metalen.worker.classes;

import com.google.firebase.database.Exclude;

/**
 * Created by Metalen on 6.1.2015..
 */
public class DataRecord {

    int ID;
    public String TYPE;
    public String DATE;
    public String DATA_1;
    public String DATA_2;
    public String DATA_3;
    public String DATA_4;
    public String ACC;

    public enum Type {
        NORMA,
        HOLIDAYS,
        OVERHOURS,
        WORK_HOURS,
        INTERVENCIJE,
        SICKLEAVE;
    }

    public enum Account {
        ACC1,
        ACC2
    }

    public enum OHMode {
        PAID,
        USED,
        UNUSED
    }

    public DataRecord() {

    }


    public DataRecord(int ID, String ACC, String TYPE, String DATE, String DATA_1, String DATA_2, String DATA_3, String DATA_4) {
        this.ID = ID;
        this.ACC = ACC;
        this.TYPE = TYPE;
        this.DATE = DATE;
        this.DATA_1 = DATA_1;
        this.DATA_2 = DATA_2;
        this.DATA_3 = DATA_3;
        this.DATA_4 = DATA_4;
    }

    @Exclude
    public int getID() {
        return ID;
    }

    @Exclude
    public void setID(int ID) {
        this.ID = ID;
    }

    @Exclude
    public String getACC() {
        return ACC;
    }

    @Exclude
    public void setACC(String ACC) {
        this.ACC = ACC;
    }

    @Exclude
    public String getTYPE() {
        return TYPE;
    }

    @Exclude
    public void setTYPE(String TYPE) {
        this.TYPE = TYPE;
    }

    @Exclude
    public String getDATE() {
        return DATE;
    }

    @Exclude
    public void setDATE(String DATE) {
        this.DATE = DATE;
    }

    @Exclude
    public String getDATA_1() {
        return DATA_1;
    }

    @Exclude
    public void setDATA_1(String DATA_1) {
        this.DATA_1 = DATA_1;
    }

    @Exclude
    public String getDATA_2() {
        return DATA_2;
    }

    @Exclude
    public void setDATA_2(String DATA_2) {
        this.DATA_2 = DATA_2;
    }

    @Exclude
    public String getDATA_3() {
        return DATA_3;
    }

    @Exclude
    public void setDATA_3(String DATA_3) {
        this.DATA_3 = DATA_3;
    }

    @Exclude
    public String getDATA_4() {
        return DATA_4;
    }

    @Exclude
    public void setDATA_4(String DATA_4) {
        this.DATA_4 = DATA_4;
    }
}
