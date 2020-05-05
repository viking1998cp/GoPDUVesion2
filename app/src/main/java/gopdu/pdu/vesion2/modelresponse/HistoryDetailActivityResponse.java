package gopdu.pdu.vesion2.modelresponse;


import gopdu.pdu.vesion2.object.HistoryDetail;

public interface HistoryDetailActivityResponse {
    void getDataHistorySuccess(HistoryDetail data);

    void getDataHistoryFaild();
}
