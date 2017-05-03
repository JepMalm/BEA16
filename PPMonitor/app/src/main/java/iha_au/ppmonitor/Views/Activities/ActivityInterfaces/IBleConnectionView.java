package iha_au.ppmonitor.Views.Activities.ActivityInterfaces;

import android.content.Context;

/**
 * Created by jeppemalmberg on 17/11/2016.
 */

public interface IBleConnectionView {
    Context getViewContext();
    void setText(String Text);

    void setVisiblityPg(int visibility);
    void setVivibilityBtn(int visibility);
    void setTextBtn(String Text);
}
