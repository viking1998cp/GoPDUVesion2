package gopdu.pdu.vesion2.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.Map;

import gopdu.pdu.vesion2.network.HistoryDetailRespon;
import gopdu.pdu.vesion2.repository.GetHistoryDetailRepository;

public class GetHistoryDetailViewModel extends AndroidViewModel {
    public GetHistoryDetailViewModel(Application application){
        super(application);
    }
    public LiveData<HistoryDetailRespon> getHistoryResponseLiveData(Map<String, String> params) {
        return GetHistoryDetailRepository.getInstance().getMutableLiveData(params);
    }
}
