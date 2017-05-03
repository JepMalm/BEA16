package iha_au.ppmonitor.Models.ModelInterfaces;

import java.util.ArrayList;

/**
 * Created by jeppemalmberg on 16/11/2016.
 */

public interface ISensor {
    ArrayList<Integer> getValues();
    void setValues(ArrayList<Integer> values);
    void addValue(int value);
    int getValue(int index);
    int getMean();
    int getMax();
    int getMin();
    String getName();
    void setName(String name);
}
