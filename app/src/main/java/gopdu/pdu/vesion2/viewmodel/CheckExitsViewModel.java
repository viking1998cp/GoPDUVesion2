package gopdu.pdu.vesion2.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.Map;

import gopdu.pdu.vesion2.object.ServerResponse;
import gopdu.pdu.vesion2.repository.CheckExitsAccountRepository;

public class CheckExitsViewModel extends AndroidViewModel {
    public CheckExitsViewModel(Application application){
        super(application);
    }
    public LiveData<ServerResponse> CheckExitsAccount(Map<String, String> params) {
        return CheckExitsAccountRepository.getInstance().getMutableLiveData(params);
    }
}
