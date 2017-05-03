package iha_au.ppmonitor.Presenters;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import iha_au.ppmonitor.Presenters.Interfaces.IBleConnectionPressenter;
import iha_au.ppmonitor.Services.BleService;
import iha_au.ppmonitor.Views.Activities.ActivityInterfaces.IBleConnectionView;
import iha_au.ppmonitor.Views.Activities.VisualizeDataView;

/**
 * Created by jeppemalmberg on 17/11/2016.
 *
 * BleConnecionPresenter har til ansvar at håndtere hvilke aktioner der skal tages når brugeren interagere med
 * BleConnectionView.
 * samtidig skal den kommunikere med BleService.
 */

public class BleConnectionPressenter implements IBleConnectionPressenter {
    IBleConnectionView view;
    boolean isbound;
    BleService service;
    private boolean serviceIsStopped;

    public BleConnectionPressenter(IBleConnectionView view) {
        this.view = view;
        service = new BleService();
        bindToService();
        // opretter en LocalBroadCastManager.
       LocalBroadcastManager connectionBroadCastManager = LocalBroadcastManager.getInstance(view.getViewContext());
        // registrerer connectionReciever og BleEnable som broadcastRecievers i connectionBroadCastManger.
        connectionBroadCastManager.registerReceiver(connectionReciever,new IntentFilter(BleService.SCAN_RESULT));
        connectionBroadCastManager.registerReceiver(BleEnable,new IntentFilter(BleService.BLE_ENABLED));
    }
    private BroadcastReceiver connectionReciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Modtager og håndterer broadcast fra bleservice med intentfilter = connected.
            Log.v("DEBUG","recieved connection");
            int connection = intent.getIntExtra("connected",-1);
            handleConnected(connection);
        }
    };
    private  BroadcastReceiver BleEnable = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Modtager og håndtere broadcast fra BleService med intentfilter "enable".
            String msg = intent.getStringExtra("message");
            // viser besked til brugren at bluetooth ikke er slået til.
            Toast.makeText(view.getViewContext(), msg, Toast.LENGTH_SHORT).show();
        }
    };
    @Override
    public void startService(){
        // stater BleService
        Intent bleService = new Intent(view.getViewContext(), BleService.class);
        view.getViewContext().startService(bleService);
        serviceIsStopped = false;
        // hvis bluetooth er slået.
        if(service.checkBlueToothEnabled()) {
            Log.v("DEBUG", "starting bleservice");
            setVisibilityPg(View.VISIBLE);
            setVisibilityBtn(View.GONE);
        }else{
            // Hvis bluetooth ikke er slået til ændres texten i "connect"-knappen til refresh.
            view.setTextBtn("Refresh");
            // hvis BleService ikke er stoppet i forvejen stoppes den.
            if(!serviceIsStopped) {
                service.stopSelf();
                serviceIsStopped = true;
            }
        }
    }

    @Override
    public void handleConnected(int connection) {
        // hvis forbindelse ikke nul ( ikke forbundet) eller -1 (default værdi for ikke forbundet)
        if(connection!=0 || connection!=-1 ){
            view.setText("Connected");
            view.setVisiblityPg(View.GONE);
            // start/gå til VisualizeDataView.
            Intent visualizeIntent = new Intent(view.getViewContext(),VisualizeDataView.class);
            view.getViewContext().startActivity(visualizeIntent);
        }
    }
    @Override
    public void setVisibilityPg(int visibility) {
        // fortæller view at progressbar skal være enten synlig eller ikke synlig.
        view.setVisiblityPg(visibility);
    }

    @Override
    public void setVisibilityBtn(int visibility) {
        // fortæller view at "connect"-knappen skal være enten synlig eller ikke synlig.
        view.setVivibilityBtn(visibility);
    }


    public void bindToService(){
        // bind til BleService, således vi kan kalde funktioner direkte igennem BleService.
        Intent i = new Intent(view.getViewContext(), BleService.class);
        view.getViewContext().bindService(i,bleConnection, view.getViewContext().BIND_AUTO_CREATE);
        isbound = true;

    }

    /**
     * Når der bindes til en Service skal dette via en ServiceConnection objekt. Dette er en framework klasse, hvor
     * de nedenstående metoder er blevet overskrevet, således de ønskede funktioner sker, ved oprettelse af forbindelse.
     */
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

    private void handleServiceConnected(IBinder iBinder) {
        // henter BleService og sætter dette som den lokale varibel service.
        BleService.BleBinder bleBinder = (BleService.BleBinder) iBinder;
        service = bleBinder.getBleService();
        isbound = true;
    }
    public void handleServiceDisconnected() {
        isbound = false;
    }
}
