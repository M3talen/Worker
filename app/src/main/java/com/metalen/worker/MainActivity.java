package com.metalen.worker;

import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.WindowManager;
import com.metalen.worker.fragments.*;
import com.percolate.foam.FoamEvent;
import it.neokree.materialnavigationdrawer.MaterialNavigationDrawer;
import it.neokree.materialnavigationdrawer.elements.MaterialAccount;
import it.neokree.materialnavigationdrawer.elements.MaterialSection;
import it.neokree.materialnavigationdrawer.elements.listeners.MaterialAccountListener;

import java.io.File;


public class MainActivity extends MaterialNavigationDrawer implements MaterialAccountListener {

    private static final int RC_SIGN_IN = 0;
    private static final int PROFILE_PIC_SIZE = 1000;
    public boolean mSignInClicked;
    MaterialAccount account, account2;
    MaterialSection sHome, sNorm, sHolidays, sWorkHours, sOvertime, sSalary, sSettings, sCalendar, sIntervencije, sSickLeave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getProfileInformation();
    }

    @Override
    public void onClick(MaterialSection section) {
        super.onClick(section);
        new FoamEvent().track(this, "Opening " + section.getTitle());
    }

    @Override
    public void init(Bundle savedInstanceState) {

        account = new MaterialAccount(this.getResources(), getString(R.string.text_acc_edit_1), getString(R.string.text_acc_edit_2), R.drawable.ic_launcher, R.drawable.bg2);
        account2 = new MaterialAccount(this.getResources(), "", "", R.drawable.ic_launcher, R.drawable.bg2);
        this.addAccount(account);
        this.addAccount(account2);
        this.setAccountListener(this);

        sHome = this.newSection(getString(R.string.home), this.getResources().getDrawable(R.drawable.ic_action_home), new HomeFragment());
        sCalendar = this.newSection(getString(R.string.calendar_title), this.getResources().getDrawable(R.drawable.ic_action_calendar_month), new FragmentCalendar());
        sIntervencije = this.newSection(getString(R.string.interventions_title), this.getResources().getDrawable(R.drawable.ic_alarm_black_24dp), new IntervencijeFragment());
        sNorm = this.newSection(getString(R.string.norm_title), this.getResources().getDrawable(R.drawable.ic_action_calendar_month), new NormFragment());
        sHolidays = this.newSection(getString(R.string.holidays_title), this.getResources().getDrawable(R.drawable.ic_action_calendar_day), new HolidaysFragment());
        sWorkHours = this.newSection(getString(R.string.workhours_title), this.getResources().getDrawable(R.drawable.ic_av_timer_black_24dp), new WorkHoursFragment());
        sOvertime = this.newSection(getString(R.string.overtimehours_title), this.getResources().getDrawable(R.drawable.ic_timelapse_black_24dp), new OverhoursFragment());
        sSalary = this.newSection(getString(R.string.salary_title), this.getResources().getDrawable(R.drawable.ic_local_atm_black_24dp), new SalaryFragment());
        sSettings = this.newSection(getString(R.string.settings_title), this.getResources().getDrawable(R.drawable.ic_settings_black_24dp), new FragmentSettings());
        sSickLeave = this.newSection(getString(R.string.text_sickleave_title), this.getResources().getDrawable(R.drawable.ic_ambulance_black_24dp), new SickLeaveFragment());

        this.addSection(sHome);
       // this.addSection(sCalendar);
        this.addDivisor();
        this.addSection(sNorm);
        this.addSection(sWorkHours);
        this.addSection(sOvertime);
        this.addSection(sIntervencije);
        this.addDivisor();
        this.addSection(sHolidays);
        this.addSection(sSickLeave);
        this.addDivisor();
        this.addSection(sSalary);

        this.addBottomSection(sSettings);
        this.enableToolbarElevation(); //Experimental
        this.allowArrowAnimation();
        this.disableLearningPattern();
        this.setBackPattern(MaterialNavigationDrawer.BACKPATTERN_BACK_TO_FIRST);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        //Test code
       /* View C = findViewById(R.id.C);
        ViewGroup parent = (ViewGroup) C.getParent();
        int index = parent.indexOfChild(C);
        parent.removeView(C);
        C = getLayoutInflater().inflate(optionId, parent, false);
        parent.addView(C, index);*/

    }

    @Override
    public void onAccountOpening(MaterialAccount materialAccount) {

    }

    @Override
    public void onChangeAccount(MaterialAccount materialAccount) {
        SharedPreferences.Editor editor = getSharedPreferences("Worker", MODE_PRIVATE).edit();

        if (materialAccount == account)
            editor.putInt("ACC", 1);
        else
            editor.putInt("ACC", 2);
        editor.commit();

        this.onStart();
        Fragment selected = (Fragment) this.getCurrentSection().getTargetFragment();
        final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.detach(selected);
        ft.attach(selected);
        ft.commit();
    }

    public void forceUpdateFragment() {
        this.onStart();
        Fragment selected = (Fragment) this.getCurrentSection().getTargetFragment();
        final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.detach(selected);
        ft.attach(selected);
        ft.commit();
    }
    //Google+

    protected void onStart() {
        super.onStart();
    }

    protected void onStop() {
        super.onStop();
    }

    /**
     * Fetching user's information name, email, profile pic
     */
    public void getProfileInformation() {
        try {
            SharedPreferences prefs2 = getSharedPreferences("Worker", MODE_PRIVATE);
            account.setTitle(prefs2.getString("User0", getString(R.string.text_acc_edit_1)));
            account.setSubTitle(prefs2.getString("Job0", getString(R.string.text_acc_edit_2)));
            account2.setTitle(prefs2.getString("User1", " "));
            account2.setSubTitle(prefs2.getString("Job1", " "));

            notifyAccountDataChanged();
            if(prefs2.getString("Icon0", " ").equals("1"))
                new LoadImage(true, 0).execute("user");
            if(prefs2.getString("Cover0", " ").equals("1"))
                new LoadImage(false, 0).execute("cover");

            if(prefs2.getString("Icon1", " ").equals("1"))
                new LoadImage(true, 1).execute("user2");
            if(prefs2.getString("Cover1", " ").equals("1"))
                new LoadImage(false, 1).execute("cover2");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public MaterialAccount getUser(int id) {
        if (id == 0) return account;
        return account2;
    }

    public void forceNotifyAccountChange() {
        this.notifyAccountDataChanged();
    }

    public void forceNotifyAccountChangeWithChange(String dFileName) {
        SharedPreferences.Editor editor = getSharedPreferences("Worker", MODE_PRIVATE).edit();
        if (dFileName.equals("user"))
            editor.putString("Icon0", "1");
        if (dFileName.equals("user2"))
            editor.putString("Icon1", "1");

        if (dFileName.equals("cover"))
            editor.putString("Cover0", "1");
        if (dFileName.equals("cover2"))
            editor.putString("Cover1", "1");
        editor.commit();
        Log.d("FORCE", dFileName);
    }

    /**
     * Background Async task to load pictures
     */
    private class LoadImage extends AsyncTask<String, String, String> {
        boolean user;
        int id;

        public LoadImage(boolean user, int userid) {
            this.user = user;
            this.id = userid;
        }

        protected String doInBackground(String... urls) {

            final String fileName = urls[0];

            final File direct = new File(Environment.getExternalStorageDirectory()
                    + "/Worker");
            try {
                if (!direct.exists()) {
                    direct.mkdirs();
                }

                final File mFile = new File(direct + "/" + fileName + ".jpg");

                if (mFile.exists())
                    return direct.toString() + "/" + fileName + ".jpg";
                Log.d("Image", "Getting image");
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return direct.toString() + "/" + fileName + ".jpg";
        }

        protected void onPostExecute(String result) {
            if (user) {
                if (id == 0)
                    account.setPhoto(BitmapFactory.decodeFile(result));
                if (id == 1)
                    account2.setPhoto(BitmapFactory.decodeFile(result));
            } else {
                if (id == 0)
                    account.setBackground(BitmapFactory.decodeFile(result));
                if (id == 1)
                    account2.setBackground(BitmapFactory.decodeFile(result));
            }
            notifyAccountDataChanged();
        }
    }


}
