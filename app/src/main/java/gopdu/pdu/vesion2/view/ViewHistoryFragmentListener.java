package gopdu.pdu.vesion2.view;

import java.util.ArrayList;

import gopdu.pdu.vesion2.object.History;

public interface ViewHistoryFragmentListener {

    void getHistorySuccess(ArrayList<History> data);

    void getHistoryFaild();
    void getHistoryStatusSuccess(String string);

    void getHistoryStatusCancel(String string);

    void reciverAllDataHistory();
}
