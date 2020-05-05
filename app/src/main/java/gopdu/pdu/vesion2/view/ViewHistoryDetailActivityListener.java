package gopdu.pdu.vesion2.view;

import gopdu.pdu.vesion2.object.HistoryDetail;

public interface ViewHistoryDetailActivityListener {
    void getDataHistorySuccess(HistoryDetail data);

    void getDataHistoryFaild();
}
