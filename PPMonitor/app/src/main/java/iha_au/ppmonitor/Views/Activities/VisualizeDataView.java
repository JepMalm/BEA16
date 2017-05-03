package iha_au.ppmonitor.Views.Activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import iha_au.ppmonitor.Presenters.Interfaces.IVisualizePresenter;
import iha_au.ppmonitor.Presenters.VisualizeDataPresenter;
import iha_au.ppmonitor.Views.Activities.ActivityInterfaces.IVisualizeView;
import iha_au.ppmonitor.Views.Fragments.LineChartFragmentView;
import iha_au.ppmonitor.R;

/**
 * VisualizeDataView står for alt det visuelle og alt det som brugeren kan interagere med.
 */

public class VisualizeDataView extends AppCompatActivity implements
        LineChartFragmentView.OnFragmentInteractionListener, IVisualizeView{

    Button stopStartBtn;
    ViewPager viewPager;
    IVisualizePresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualize_data);
        stopStartBtn = (Button)findViewById(R.id.bleBtn);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        presenter = new VisualizeDataPresenter(this);
        presenter.setUpViewPager(viewPager,getSupportFragmentManager());
        setTitle("Plantar Pressure Monitor");
    }
    @Override
    protected void onDestroy() {
        presenter.unbindMyService();
        super.onDestroy();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return presenter.inflateMenu(menu,this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(presenter.menuItemSeletected(item));
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        // da der ikke interageres med fragments direkte er denne metode ikke blevet implementeret.
    }

    public void onStartStopClick(View view) {
        Log.v("DEBUG","noticed click");
        presenter.onStartStopClick();
    }

    @Override
    public void changeButtonText(String text) {
        stopStartBtn.setText(text);
    }
    @Override
    public Context getViewContext() {
        // returnere en instance af sig selv og dets context.
        return this;
    }
}

