package iha_au.ppmonitor.Services;

/**
 * Created by jeppemalmberg on 20/11/2016.
 */

public interface IDataService {
    boolean checkBlueToothEnabled();
    void startScanForDevice();
    void stopScanForDevice();
    void startDataRecording();
    void stopDataRecording();
    void disconnectFromDevice();

}
