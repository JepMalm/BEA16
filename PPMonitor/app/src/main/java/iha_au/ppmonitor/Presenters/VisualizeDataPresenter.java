package iha_au.ppmonitor.Presenters;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import iha_au.ppmonitor.Presenters.Interfaces.IVisualizePresenter;
import iha_au.ppmonitor.R;
import iha_au.ppmonitor.Services.BleService;
import iha_au.ppmonitor.Views.Activities.ActivityInterfaces.IVisualizeView;
import iha_au.ppmonitor.Views.Activities.BleConnectionView;
import iha_au.ppmonitor.Views.Adapters.ChartPagerAdapter;

/**
 * Created by jeppemalmberg on 16/11/2016.
 */

public class VisualizeDataPresenter implements IVisualizePresenter {
    IVisualizeView view;
    boolean isbound = false;
    BleService service;
    boolean isStartet = false;
    Context context;

    public VisualizeDataPresenter(IVisualizeView view) {
        this.view = view;
        context = view.getViewContext();
        bindMyService();
    }


    @Override
    public void handleServiceConnected(IBinder binder) {
        // sætter den modtagne
        BleService.BleBinder bleBinder = (BleService.BleBinder) binder;
        service = bleBinder.getBleService();
        isbound = true;
    }

    @Override
    public void handleServiceDisconnected() {
        isbound = false;
    }

    @Override
    public void bindMyService() {
        Intent i = new Intent(view.getViewContext(), BleService.class);
        context.bindService(i,bleConnection, context.BIND_AUTO_CREATE);
        isbound = true;
    }
    @Override
    public void unbindMyService() {
        service.disconnectFromDevice();
        view.getViewContext().unbindService(bleConnection);
        view.getViewContext().stopService(new Intent(context, BleService.class));
    }
    @Override
    public void onStartStopClick()  {
        // hvis Bleservice er sat til at modtage data.
        if(isStartet) {
            service.stopDataRecording();
            isStartet = false;
            view.changeButtonText("start recording");
        }else if(!isStartet){
            service.startDataRecording();
            isStartet=true;
            view.changeButtonText( "stop recording");
        }
    }
    @Override
    public void setUpViewPager(ViewPager vp, FragmentManager fm) {
        PagerAdapter pa = new ChartPagerAdapter(fm,context);
        vp.setAdapter(pa);
    }
    @Override
    public boolean inflateMenu(Menu menu, AppCompatActivity activity) {
        // MenuInflater er en framework klasse, som "inflater" menuen med designet fra menu_overview.xml.
        MenuInflater menuInflater = activity.getMenuInflater();
        menuInflater.inflate(R.menu.menu_overview, menu);

        return true;
    }
    @Override
    public MenuItem menuItemSeletected(MenuItem item) {
        // det item som brugren trykkede på.
        int id = item.getItemId();
        switch (id){
            case R.id.action_clear:
                // hvis brugeren har trykket på clear i menuen, broadcastes det til LineChartPresenter at den skal slette grafens data.
                LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(context);
                Intent j = new Intent("clear");
                localBroadcastManager.sendBroadcast(j);
                break;
            case R.id.action_disconnect:
                // hvis der trykkes på disconnect, kaldes lukkes forbindelsen til GATT serveren igennem BleService.
                service.disconnectFromDevice();
                // og BleConnectionView vises.
                Intent i = new Intent(context, BleConnectionView.class);
                context.startActivity(i);
                break;
        }
        return item;
    }
    private ServiceConnection bleConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            handleServiceConnected(iBinder);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            handleServiceDisconnected();
        }
    };
}

