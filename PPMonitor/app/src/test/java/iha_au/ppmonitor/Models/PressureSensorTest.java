package iha_au.ppmonitor.Models;

import android.hardware.Sensor;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

import iha_au.ppmonitor.Models.ModelInterfaces.ISensor;

import static org.junit.Assert.*;

/**
 * Created by jeppemalmberg on 21/11/2016.
 */
public class PressureSensorTest {
    public ISensor uSensor;
    ArrayList<Integer> testArray;
    String testName;

    @Before
    public void setUp() throws Exception {
        testName = "sensor1";
        testArray =new ArrayList<Integer>(Arrays.asList(1,2,3,4,5));
        uSensor = new PressureSensor(testName,testArray);
    }

    @Test
    public void getName() throws Exception {
        assertEquals(uSensor.getName(),testName);
    }

    @Test
    public void setName() throws Exception {
        uSensor.setName("test");
        assertEquals(uSensor.getName(),"test");
    }

    @Test
    public void getValues() throws Exception {
        assertEquals(uSensor.getValues(),testArray);
    }

    @Test
    public void setValues() throws Exception {
        ArrayList<Integer> t = new ArrayList<Integer>(Arrays.asList(10,11,12,13,14,15));
        uSensor.setValues(t);
        assertEquals(t,uSensor.getValues());
    }

    @Test
    public void addValue() throws Exception {
        int testValue = 24;
        uSensor.addValue(24);
        assertTrue(uSensor.getValues().contains(testValue));
    }

    @Test
    public void getValue() throws Exception {
        assertEquals(3,uSensor.getValue(2));
    }

    @Test
    public void getMean() throws Exception {
        assertEquals(3,uSensor.getMean());
    }

    @Test
    public void getMax() throws Exception {
        assertEquals(5,uSensor.getMax());
    }

    @Test
    public void getMin() throws Exception {
        assertEquals(1,uSensor.getMin());
    }

}