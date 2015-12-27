package com.metalen.worker.classes;

/**
 * Created by Metalen on 6.1.2015..
 */
public class DataRecord {

    int ID;
    String TYPE;
    String DATE;
    String DATA_1;
    String DATA_2;
    String DATA_3;
    String DATA_4;
    String ACC;
    public enum Type {
        NORMA,
        HOLIDAYS,
        OVERHOURS,
        WORK_HOURS
    }
    public enum Account{
        ACC1,
        ACC2
    }
    public enum OHMode{
        PAID,
        USED,
        UNUSED
    }

    public DataRecord(){}

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

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getACC() {
        return ACC;
    }

    public void setACC(String ACC) {
        this.ACC = ACC;
    }

    public String getTYPE() {
        return TYPE;
    }

    public void setTYPE(String TYPE) {
        this.TYPE = TYPE;
    }

    public String getDATE() {
        return DATE;
    }

    public void setDATE(String DATE) {
        this.DATE = DATE;
    }

    public String getDATA_1() {
        return DATA_1;
    }

    public void setDATA_1(String DATA_1) {
        this.DATA_1 = DATA_1;
    }

    public String getDATA_2() {
        return DATA_2;
    }

    public void setDATA_2(String DATA_2) {
        this.DATA_2 = DATA_2;
    }

    public String getDATA_3() {
        return DATA_3;
    }

    public void setDATA_3(String DATA_3) {
        this.DATA_3 = DATA_3;
    }

    public String getDATA_4() {
        return DATA_4;
    }

    public void setDATA_4(String DATA_4) {
        this.DATA_4 = DATA_4;
    }
}
