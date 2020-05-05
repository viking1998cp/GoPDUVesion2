package gopdu.pdu.vesion2.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.Map;

import gopdu.pdu.vesion2.object.ServerResponse;
import gopdu.pdu.vesion2.repository.InsertRatingRespository;

public class InsertRatingViewModel extends AndroidViewModel {
    public InsertRatingViewModel(Application application){
        super(application);
    }
    public LiveData<ServerResponse> checkRatting(Map<String, String> params) {
        return InsertRatingRespository.getInstance().getMutableLiveData(params);
    }
}
