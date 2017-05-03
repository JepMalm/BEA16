package iha_au.ppmonitor.Views.Fragments.Interfaces;


import android.content.Context;
import android.os.Bundle;
import android.telecom.Connection;
import android.view.View;

import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.data.BaseDataSet;
import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;

import java.util.ArrayList;

import iha_au.ppmonitor.Views.Fragments.LineChartFragmentView;

public interface IChartFragmentView {
    void setUpChart();
    Context getViewContext();
    ChartData getChartData();
    void notifyView();

}
