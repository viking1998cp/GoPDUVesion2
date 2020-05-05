package gopdu.pdu.vesion2.presenter;

import java.util.ArrayList;


import gopdu.pdu.vesion2.modelresponse.HistoryFragmentResponse;
import gopdu.pdu.vesion2.network.HistoryResponse;
import gopdu.pdu.vesion2.object.History;
import gopdu.pdu.vesion2.view.ViewHistoryFragmentListener;
import gopdu.pdu.vesion2.viewmodel.HistoryFragmentViewModel;

public class PresenterHistoryFragment implements HistoryFragmentResponse {
    private HistoryFragmentViewModel historyFragmentViewModel;
    private ViewHistoryFragmentListener callback;

    public PresenterHistoryFragment(ViewHistoryFragmentListener callback) {
        historyFragmentViewModel = new HistoryFragmentViewModel(this);
        this.callback = callback;
    }

    public void reciverGetHistory(HistoryResponse historyResponse) {
        historyFragmentViewModel.getHistory(historyResponse);
    }

    public void reciverStateHistoryChange(int position) {
        historyFragmentViewModel.stateHistoryChange(position);
    }

    @Override
    public void getHistorySuccess(ArrayList<History> data) {
        callback.getHistorySuccess(data);
    }

    @Override
    public void getHistoryFaild() {
        callback.getHistoryFaild();
    }

    @Override
    public void getHistoryStateSuccess(String string) {
        callback.getHistoryStatusSuccess(string);
    }

    @Override
    public void getHistoryStateCancel(String string) {
        callback.getHistoryStatusCancel(string);
    }

    @Override
    public void reciverAllDataHistory() {
        callback.reciverAllDataHistory();
    }


}
