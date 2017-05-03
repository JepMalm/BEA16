package iha_au.ppmonitor.Views.Activities.ActivityInterfaces;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.os.IBinder;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;

/**
 * Created by jeppemalmberg on 16/11/2016.
 */

public interface IVisualizeView {
    void changeButtonText(String text);
    Context getViewContext();


}
