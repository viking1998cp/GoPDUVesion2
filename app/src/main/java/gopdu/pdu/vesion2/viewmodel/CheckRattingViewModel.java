package gopdu.pdu.vesion2.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.Map;

import gopdu.pdu.vesion2.network.HistoryDetailRespon;
import gopdu.pdu.vesion2.repository.CheckRattingRepository;

public class CheckRattingViewModel extends AndroidViewModel {
    public CheckRattingViewModel(Application application){
        super(application);
    }
    public LiveData<HistoryDetailRespon> checkRatting(Map<String, String> params) {
        return CheckRattingRepository.getInstance().getMutableLiveData(params);
    }
}
