package iha_au.ppmonitor.Views.Activities;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import iha_au.ppmonitor.Presenters.BleConnectionPressenter;
import iha_au.ppmonitor.Presenters.Interfaces.IBleConnectionPressenter;
import iha_au.ppmonitor.R;
import iha_au.ppmonitor.Views.Activities.ActivityInterfaces.IBleConnectionView;

/**
 * BleConnectionView står for alt det visuelle, alt det som brugeren kan se og interagere med.
 */

public class BleConnectionView extends AppCompatActivity implements IBleConnectionView {
    TextView bleTxt;
    public ProgressBar pgBar;
    public Button startBtn;

    IBleConnectionPressenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ble_connection);
        setTitle("");
        bleTxt = (TextView)findViewById(R.id.ble_txt);
        startBtn = (Button)findViewById(R.id.startConnection);
        pgBar = (ProgressBar) findViewById(R.id.progressBar);
        pgBar.setVisibility(View.INVISIBLE);
        bleTxt.setText("Start connection to sensor?");
        startBtn.setText("Connect");

        presenter = new BleConnectionPressenter(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    @Override public Context getViewContext() {
        return this;
    }

    @Override
    public void setText(String Text) {
        bleTxt.setText(Text);
    }


    @Override
    public void setVisiblityPg(int visibility) {
        pgBar.setVisibility(visibility);
    }

    @Override
    public void setVivibilityBtn(int visibility) {
        startBtn.setVisibility(visibility);
    }

    @Override
    public void setTextBtn(String Text) {
        startBtn.setText(Text);
    }

    public void onStartClick(View view){
        presenter.startService();
    }
}
