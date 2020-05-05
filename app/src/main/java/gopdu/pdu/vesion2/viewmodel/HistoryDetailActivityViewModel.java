package gopdu.pdu.vesion2.viewmodel;


import gopdu.pdu.vesion2.modelresponse.HistoryDetailActivityResponse;
import gopdu.pdu.vesion2.network.HistoryDetailRespon;

public class HistoryDetailActivityViewModel {
    private HistoryDetailActivityResponse callback;

    public HistoryDetailActivityViewModel(HistoryDetailActivityResponse callback) {
        this.callback = callback;
    }

    public void getDataHistoryDetail(HistoryDetailRespon historyDetailRespon) {
        if(historyDetailRespon.getSuccess() && historyDetailRespon.getData() != null){
            callback.getDataHistorySuccess(historyDetailRespon.getData());
        }else {
            callback.getDataHistoryFaild();
        }
    }
}
