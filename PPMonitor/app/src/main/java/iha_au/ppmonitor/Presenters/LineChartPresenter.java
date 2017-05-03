package iha_au.ppmonitor.Presenters;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;

import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BaseDataSet;
import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.Random;

import iha_au.ppmonitor.Models.ModelInterfaces.ISensorData;
import iha_au.ppmonitor.Models.PressureSensorData;
import iha_au.ppmonitor.Presenters.Interfaces.IChartPresenter;
import iha_au.ppmonitor.R;
import iha_au.ppmonitor.Services.BleService;
import iha_au.ppmonitor.Views.CostumMarkerView;
import iha_au.ppmonitor.Views.Fragments.Interfaces.IChartFragmentView;

/**
 * Created by jeppemalmberg on 16/11/2016.
 *
 * LineChartPresenter har til ansvar at opdatere linje grafen i LineChartFragmentView.
 */

public class LineChartPresenter implements IChartPresenter {
    IChartFragmentView view;
    private Boolean setUpDone = false;
    LocalBroadcastManager localBroadcastManager;
    LineChart lChart;

    public LineChartPresenter(IChartFragmentView view, LineChart c) {
        this.view = view;
        lChart = c;
        localBroadcastManager = LocalBroadcastManager.getInstance(view.getViewContext());
        localBroadcastManager.registerReceiver(clearReciever,new IntentFilter("clear"));
        localBroadcastManager.registerReceiver(dataReciever, new IntentFilter(BleService.VALUE_RESULT));
    }


    @Override
    public ChartData upDateGui(ISensorData data, ChartData dataSets) {
        // det nuværende data i grafen ligges i lineData.
        LineData lineData = (LineData)dataSets;
        // hvis grafen er instantieret
        if(setUpDone) {
            int lineDataSetCount = lineData.getDataSetCount();
            int dataSize = data.getPressureSensors().size();
            // dataSize = 5 ( fem sensore).
                for (int i = 0; i < dataSize; i++) {
                    // finder det givne dataset på index i.
                    LineDataSet set = (LineDataSet) lineData.getDataSetByIndex(i);
                    // Da vi skal finde den senste værdi, der er tilføjet sensoren. findes antallet af værdier-1, da index starter fra 0.
                    int index = data.getSensor(i).getValues().size();
                    // finder den givne værdi for den seneste værdie.
                    int yValue = data.getSensor(i).getValue(index-1);
                    // opretter en ny entry med x-værdi på antallet af tidligere entries, og med den fundne y-værdi
                    Entry e = new Entry(set.getEntryCount(),yValue);
                    // den nye entry bliver lagt i linje grafen data.
                    lineData.addEntry(e, i);
                }
                // for at opdatere grafen, kaldes notifyView(), i LineCharFragmenView.
                view.notifyView();
                return lineData;
        }
        view.notifyView();
        return lineData;
    }

    /**
     * setUpIsDone(...) bliver kaldt af LineChartFragmentView, når den har instantieret linje grafen.
     */
    @Override
    public boolean setUpIsDone(boolean b) {
        setUpDone = true;
        return b;
    }


    BroadcastReceiver dataReciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Modtager og håndtere broadcast fra BleService, med intentfilteret "Value".
            PressureSensorData data = (PressureSensorData) intent.getSerializableExtra("sensors");
            // kalder upDateGui(...) med det modtagne PressureSensor objekt modtager fra BleService.
            upDateGui(data, view.getChartData());
        }
    };
    private BroadcastReceiver clearReciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Modtager og håndtere broadcast fra VisualizeDataPresenter, om at grafens data skal slettes.
            try {
                // sletter data i grafen.
                //lChart.clear();
                lChart.getLineData().getDataSetByIndex(0).clear();
                lChart.getLineData().getDataSetByIndex(1).clear();
                lChart.getLineData().getDataSetByIndex(2).clear();
                lChart.getLineData().getDataSetByIndex(3).clear();
                lChart.getLineData().getDataSetByIndex(4).clear();
                view.notifyView();
                Log.v("DEBUG","Slettet data i lChart");
                // genopretter linje grafen.
                //view.setUpChart();
            }catch (Exception e){
                Log.v("DEBUG", e.toString());
            }
        }
    };
}
