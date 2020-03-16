package gopdu.pdu.vesion2.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.Map;

import gopdu.pdu.vesion2.object.ServerResponse;
import gopdu.pdu.vesion2.repository.RegisterAccountRepository;

public class RegisterAccountViewModel extends AndroidViewModel {
    public RegisterAccountViewModel(Application application){
        super(application);
    }
    public LiveData<ServerResponse> CheckExitsAccount(Map<String, String> params) {
        return RegisterAccountRepository.getInstance().getMutableLiveData(params);
    }
}
