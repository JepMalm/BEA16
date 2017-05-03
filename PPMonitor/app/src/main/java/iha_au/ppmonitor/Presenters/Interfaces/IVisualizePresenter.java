package iha_au.ppmonitor.Presenters.Interfaces;

import android.os.IBinder;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

/**
 * Created by jeppemalmberg on 16/11/2016.
 */

public interface IVisualizePresenter {
    void handleServiceConnected(IBinder binder);
    void handleServiceDisconnected();
    void bindMyService();
    void unbindMyService();
    void onStartStopClick();
    void setUpViewPager(ViewPager vp, FragmentManager fm);
    boolean inflateMenu(Menu menu, AppCompatActivity activity);
    MenuItem menuItemSeletected(MenuItem item);
}
