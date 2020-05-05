package gopdu.pdu.vesion2.modelresponse;

import java.util.ArrayList;

import gopdu.pdu.vesion2.object.History;

public interface HistoryFragmentResponse {
    void getHistorySuccess(ArrayList<History> data);

    void getHistoryFaild();

    void getHistoryStateSuccess(String string);

    void getHistoryStateCancel(String string);

    void reciverAllDataHistory();
}
