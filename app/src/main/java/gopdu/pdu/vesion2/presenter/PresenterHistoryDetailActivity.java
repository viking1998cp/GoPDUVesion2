package gopdu.pdu.vesion2.presenter;


import gopdu.pdu.vesion2.modelresponse.HistoryDetailActivityResponse;
import gopdu.pdu.vesion2.network.HistoryDetailRespon;
import gopdu.pdu.vesion2.object.HistoryDetail;
import gopdu.pdu.vesion2.view.ViewHistoryDetailActivityListener;
import gopdu.pdu.vesion2.viewmodel.HistoryDetailActivityViewModel;

public class PresenterHistoryDetailActivity implements HistoryDetailActivityResponse {
    private HistoryDetailActivityViewModel historyDetailActivityModel;
    private ViewHistoryDetailActivityListener callback;

    public PresenterHistoryDetailActivity(ViewHistoryDetailActivityListener callback) {
        historyDetailActivityModel = new HistoryDetailActivityViewModel(this);
        this.callback = callback;
    }

    public void reciverDataHistoryDetail(HistoryDetailRespon historyDetailRespon) {
        historyDetailActivityModel.getDataHistoryDetail(historyDetailRespon);
    }

    @Override
    public void getDataHistorySuccess(HistoryDetail data) {
        callback.getDataHistorySuccess(data);
    }

    @Override
    public void getDataHistoryFaild() {
        callback.getDataHistoryFaild();
    }
}
