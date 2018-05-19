package com.yoavgibri.myincome;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.yoavgibri.myincome.Fragments.StatsFragment;

import java.util.Calendar;

/**
 * Created by Yoav on 16/04/17.
 */

public class PagerAdapterStats extends FragmentStatePagerAdapter {

    private final boolean isHebrewLocale;

    public PagerAdapterStats(FragmentManager fm, boolean isHebrewLocale) {  //int amountOfMonthsWithSessions
        super(fm);
        this.isHebrewLocale = isHebrewLocale;
//        this.amountOfMonthsWithSessions = amountOfMonthsWithSessions;
    }

    @Override
    public Fragment getItem(int position) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, (isHebrewLocale ? (-position) : (position - 12) ));
        return StatsFragment.newInstance(cal);
    }

    @Override
    public int getCount() {
        return 13;
// should be
//        return 1 + amountOfMonthsWithSessions;
    }
}
