package iha_au.ppmonitor.Views.Fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import iha_au.ppmonitor.Presenters.Interfaces.IChartPresenter;
import iha_au.ppmonitor.Presenters.LineChartPresenter;
import iha_au.ppmonitor.R;
import iha_au.ppmonitor.Views.CostumMarkerView;
import iha_au.ppmonitor.Views.Fragments.Interfaces.IChartFragmentView;

import static java.lang.Float.valueOf;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LineChartFragmentView.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link LineChartFragmentView#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LineChartFragmentView extends Fragment implements IChartFragmentView {
    private static final String TAG = "Debug";
    IChartPresenter presenter;
    private LineChart lChart;
    private LineData lineData;

    private LineChartFragmentView.OnFragmentInteractionListener mListener;


    public static LineChartFragmentView newInstance() {
        LineChartFragmentView fragment = new LineChartFragmentView();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Log.v(TAG, "Container = " + container.toString());
        View view = inflater.inflate(R.layout.fragment_line_chart, container, false);
        lChart = (LineChart)view.findViewById(R.id.lineChart);
        presenter = new LineChartPresenter(this, lChart);
        setUpChart();
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void setUpChart() {
        lineData = new LineData();
        int[] myColors = new int[]{Color.BLACK, Color.BLUE, Color.GREEN, Color.RED, Color.rgb(255,177,61)};
        // nedenståen for-lykke, opretter de 5 sensor Dataset. giver forskellige farve og navn.
        // alle instantieres som tomme.
        for(int i = 0; i<5;i++){
            LineDataSet lds = new LineDataSet(null,"sensor "+(i));

            //lds.setMode(LineDataSet.Mode.LINEAR);
            lds.setMode(LineDataSet.Mode.LINEAR);
            lds.setDrawValues(false);
            lds.setFillAlpha(100);
            lds.setDrawCircles(false);
            lds.setLineWidth(1.8f);
            lds.setCircleRadius(4f);
            lds.setCircleColor(Color.WHITE);
            lds.setCubicIntensity(0.2f);
            lds.setHighLightColor(Color.BLACK);
            switch (i){
                case 0:
                    lds.setColor(myColors[i]);
                    lds.setFillColor(myColors[i]);
                    break;
                case 1:
                    lds.setColor(myColors[i]);
                    lds.setFillColor(myColors[i]);
                    break;
                case 2:
                    lds.setColor(myColors[i]);
                    lds.setFillColor(myColors[i]);
                    break;
                case 3:
                    lds.setColor(myColors[i]);
                    lds.setFillColor(myColors[i]);
                    break;
                case 4:
                    lds.setColor(myColors[i]);
                    lds.setFillColor(myColors[i]);
                    break;
            }
            lineData.addDataSet(lds);
        }
        lChart.setDescription("");
        Legend l = lChart.getLegend();
        l.setCustom(myColors, new String[]{"Storetå", "Højre fodballe", "Venstre fodballe", "Buen" , "Hæl"});
        // gør grafen scrollable.
        lChart.setScrollContainer(true);
        // fjern baggrund gitter.
        lChart.getAxisLeft().setDrawGridLines(false);
        // sæt den maksimale antal værdier der vises på x-aksen
        lChart.getXAxis().setAxisMaxValue(300);

        // Marker view identificere  det specifikke koordinat som brugeren har trykket på. Her vises den givne y værdier.
        lChart.setDrawMarkerViews(true);
        CostumMarkerView marker = new CostumMarkerView(lChart.getContext(),R.layout.markerview);
        lChart.setMarkerView(marker);


        XAxis x = lChart.getXAxis();
        // sætte x-aksen til at være i bunde af grafen
        x.setPosition(XAxis.XAxisPosition.BOTTOM);
        // fjerne gitter baggrund.
        x.setDrawGridLines(false);
        // ??
        x.setAvoidFirstLastClipping(true);

        //  højre y akse fjernes.
        YAxis yr = lChart.getAxisRight();
        yr.setDrawGridLines(false);
        yr.setEnabled(false);

        // venstre y-akse
        YAxis yl = lChart.getAxisLeft();
        // sættes uden for grafen
        yl.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        // maximum y værdi 5000
        yl.setAxisMaxValue(5000f);
        // minimum y værdi 0
        yl.setAxisMinValue(0f);

        // indsætter de 5 datasets i grafen.
        lChart.setData(lineData);
        presenter.setUpIsDone(true);
        Log.v("DEBUG","Linechart has been created");
    }

    @Override
    public Context getViewContext() {
        return getContext();
    }


    @Override
    public ChartData getChartData() {
        return lineData;
    }

    @Override
    public void notifyView() {
        lChart.notifyDataSetChanged();

        // flytter scope til den sidste x-værdi.
        lChart.moveViewToX(lineData.getEntryCount());
        // nulstiller maximum x-værdi og sætter den til 600.
        lChart.getXAxis().resetAxisMaxValue();
        lChart.setVisibleXRangeMaximum(300);

    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}

