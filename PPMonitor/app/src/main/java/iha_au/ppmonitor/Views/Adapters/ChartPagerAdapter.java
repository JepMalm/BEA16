package iha_au.ppmonitor.Views.Adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import iha_au.ppmonitor.Views.Fragments.LineChartFragmentView;

/**
 * Created by jeppemalmberg on 25/10/2016.
 */

public class ChartPagerAdapter extends FragmentPagerAdapter {

    private static final String TAG = "DEBUG";
    private final Context context;

    public ChartPagerAdapter(FragmentManager fragmentManager, Context context){
        super(fragmentManager);
        this.context = context;
    }
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                Log.v(TAG, "Create linechart fragment");
                return new LineChartFragmentView().newInstance();
            case 1:
                return new LineChartFragmentView().newInstance();
            default:
                return null;
        }
    }
    //KILDE: http://stackoverflow.com/questions/20006736/cant-use-method-getstringint-resid-in-fragmentpageradapter
    @Override
    public CharSequence getPageTitle(int position){
        switch (position){
            case 0:
                return "LineChart1";
            case 1:
                return "LineChart2";
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }
}
