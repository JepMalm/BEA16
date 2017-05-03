package iha_au.ppmonitor.Presenters;

import android.content.Context;

import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

import iha_au.ppmonitor.Models.ModelInterfaces.ISensor;
import iha_au.ppmonitor.Models.ModelInterfaces.ISensorData;
import iha_au.ppmonitor.Models.PressureSensor;
import iha_au.ppmonitor.Models.PressureSensorData;
import iha_au.ppmonitor.Views.Activities.ActivityInterfaces.IVisualizeView;
import iha_au.ppmonitor.Views.Fragments.Interfaces.IChartFragmentView;
import iha_au.ppmonitor.Views.Fragments.LineChartFragmentView;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockingDetails;
import static org.mockito.Mockito.when;

/**
 * Created by jeppemalmberg on 14/12/2016.
 */
public class LineChartPresenterTest {
    IChartFragmentView mView;
    LineChart mLineChart;
    ISensorData mSensorData;
    LineChartPresenter uut;
    @Before
    public void setUp() throws Exception {
        mView =  mock(LineChartFragmentView.class);
        mLineChart = mock(LineChart.class);
        mSensorData = mock(PressureSensorData.class);
        Context mContext = getInstrumentation().getTargetContext();
        when(mView.getViewContext()).thenReturn(mContext);
        uut = new LineChartPresenter(mView,mLineChart);
    }

    @Test
    public void upDateGui() throws Exception {
        uut.setUpIsDone(true);
        LineData lData = new LineData();
        LineDataSet lds = new LineDataSet(null,"mockSensor");
        lData.addDataSet(lds);
        lData.addDataSet(lds);
        lData.addDataSet(lds);
        lData.addDataSet(lds);
        lData.addDataSet(lds);


        ISensor mSensor = new PressureSensor("sensor",new ArrayList<>(Arrays.asList(10)));
        PressureSensorData pds = new PressureSensorData();
        pds.setPressureSensors(new ArrayList<ISensor>(Arrays.asList(
                new PressureSensor("sensor",new ArrayList<>(Arrays.asList(10))),
                new PressureSensor("sensor",new ArrayList<>(Arrays.asList(10))),
                new PressureSensor("sensor",new ArrayList<>(Arrays.asList(10))),
                new PressureSensor("sensor",new ArrayList<>(Arrays.asList(10))),
                new PressureSensor("sensor",new ArrayList<>(Arrays.asList(10))))));

        int uutCount = uut.upDateGui(pds,lData).getEntryCount();
        int dataCount = lData.getDataSetCount();
        Assert.assertNotEquals(uutCount,dataCount);
    }

    @Test
    public void setUpIsDone() throws Exception {
        Assert.assertTrue(uut.setUpIsDone(true));
    }

}