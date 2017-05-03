package iha_au.ppmonitor.Presenters.Interfaces;

import android.view.View;

/**
 * Created by jeppemalmberg on 17/11/2016.
 */

public interface IBleConnectionPressenter {
    void startService();
    void handleConnected(int connection);
    void setVisibilityPg(int visibility);
    void setVisibilityBtn(int visibility);
}
