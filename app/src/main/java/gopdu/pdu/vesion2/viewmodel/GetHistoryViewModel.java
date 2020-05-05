package gopdu.pdu.vesion2.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.Map;

import gopdu.pdu.vesion2.network.HistoryResponse;
import gopdu.pdu.vesion2.repository.GetHistoryRepository;


public class GetHistoryViewModel extends AndroidViewModel {
    public GetHistoryViewModel(Application application){
        super(application);
    }
    public LiveData<HistoryResponse> getHistoryResponseLiveData(Map<String, String> params) {
        return GetHistoryRepository.getInstance().getMutableLiveData(params);
    }
}
