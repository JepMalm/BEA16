package iha_au.ppmonitor.Models;

import java.io.Serializable;
import java.util.ArrayList;

import iha_au.ppmonitor.Models.ModelInterfaces.ISensor;
import iha_au.ppmonitor.Models.ModelInterfaces.ISensorData;

/**
 * Created by jeppemalmberg on 07/10/2016.
 */

public class PressureSensorData implements Serializable, ISensorData {
    private ArrayList<ISensor> pressureSensors;


    public PressureSensorData() {
        pressureSensors = new ArrayList<>();
    }

    @Override
    public ArrayList<ISensor> getPressureSensors() {
        return pressureSensors;
    }

    @Override
    public void setPressureSensors(ArrayList<ISensor> Sensors) {
        pressureSensors = Sensors;
    }

    @Override
    public ISensor getSensor(int index) {
        return pressureSensors.get(index);
    }

    @Override
    public void addSensor(ISensor pressureSensor) {
        pressureSensors.add(pressureSensor);
    }
}
