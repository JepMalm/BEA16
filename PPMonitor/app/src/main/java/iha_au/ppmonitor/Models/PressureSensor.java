package iha_au.ppmonitor.Models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

import iha_au.ppmonitor.Models.ModelInterfaces.ISensor;

/**
 * Created by jeppemalmberg on 07/10/2016.
 *
 * PressureSensor klassens ansvar er at immitere den konkrete sensore, der sidder i hw.
 */

public class PressureSensor implements Serializable, ISensor {
    // class representing a pressuresensor or pressurepoint in the sensor-unit.
    public ArrayList<Integer> values;
    public String name;


    public PressureSensor(String n, ArrayList<Integer> v) {
        name = n;
        values = v;
    }

    public ArrayList<Integer> getValues() {return values;}
    public void setValues(ArrayList<Integer> values) {this.values = values;}
    public void addValue(int value){
        values.add(value);
    }
    public int getValue(int index){
        return values.get(index);
    }

    /**
     * getMean udregner den gennemsnitlige værdi i sensorens arrayliste af data.
     * Dette er ikke brugt nogle steder i koden, men viser hvorledes visse udregninger kan udføres på
     * de data som sensorerne indeholder.
     * @return
     */
    @Override
    public int getMean() {
        int sum= 0;
        for(int i = 0;i<values.size();i++){
            // plusser værdi til den nuværende værdi i sum.
            sum+=values.get(i);
        }
        // returnere den samlede sum af værdier divideret med antallet af værdier.
        return sum/values.size();
    }

    @Override
    public int getMax() {
        return Collections.max(values);
    }

    @Override
    public int getMin() {
        return Collections.min(values);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }
}
