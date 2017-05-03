package iha_au.ppmonitor.Presenters.Interfaces;

import android.os.Bundle;

import com.github.mikephil.charting.data.ChartData;

import java.util.ArrayList;

import iha_au.ppmonitor.Models.ModelInterfaces.ISensorData;

/**
 * Created by jeppemalmberg on 16/11/2016.
 */

public interface IChartPresenter {
    ChartData upDateGui(ISensorData data, ChartData dataSets);
    boolean setUpIsDone(boolean b);
}
