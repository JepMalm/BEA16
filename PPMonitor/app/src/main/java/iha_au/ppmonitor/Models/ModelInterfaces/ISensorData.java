package iha_au.ppmonitor.Models.ModelInterfaces;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

import iha_au.ppmonitor.Models.PressureSensor;

/**
 * Created by jeppemalmberg on 16/11/2016.
 */

public interface ISensorData {
    ArrayList<ISensor> getPressureSensors();
    void setPressureSensors(ArrayList<ISensor> Sensors);
    ISensor getSensor(int index);
    void addSensor(ISensor pressureSensor);
}
