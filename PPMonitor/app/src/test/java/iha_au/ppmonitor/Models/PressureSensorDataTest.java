package iha_au.ppmonitor.Models;

import android.hardware.Sensor;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.configuration.injection.MockInjection;
import static org.mockito.Mockito.*;


import java.util.ArrayList;
import java.util.Arrays;

import iha_au.ppmonitor.Models.ModelInterfaces.ISensor;
import iha_au.ppmonitor.Models.ModelInterfaces.ISensorData;

import static org.junit.Assert.*;

/**
 * Created by jeppemalmberg on 21/11/2016.
 */
public class PressureSensorDataTest {
    ISensor mockedSensor;
    ISensorData ut;
    ArrayList testArray;

    @Before
    public void setUp() throws Exception {
        mockedSensor = mock(ISensor.class);
        testArray = mock(ArrayList.class);
        testArray.add(mockedSensor);
        ut = new PressureSensorData(testArray);
    }
    @Test
    public void getPressureSensors() throws Exception {
        assertEquals(ut.getPressureSensors(),testArray);

    }

    @Test
    public void setPressureSensors() throws Exception {
        ArrayList testArray2 = mock(ArrayList.class);
        testArray2.add(mockedSensor);
        testArray2.add(mockedSensor);
        ut.setPressureSensors(testArray2);
        assertEquals(testArray2,ut.getPressureSensors());

    }

    @Test
    public void getSensor() throws Exception {
        assertEquals(testArray.get(0),ut.getSensor(0));
    }

    @Test
    public void addSensor() throws Exception {
        ISensor mockSensor2 = mock(ISensor.class);
        ut.addSensor(mockSensor2);
        assertEquals(testArray.get(0),mockSensor2);
    }

}