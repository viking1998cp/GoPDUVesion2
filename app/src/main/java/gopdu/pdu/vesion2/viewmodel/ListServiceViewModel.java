package gopdu.pdu.vesion2.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.Map;

import gopdu.pdu.vesion2.network.ListServiceRespon;
import gopdu.pdu.vesion2.repository.ListServiceRepository;

public class ListServiceViewModel extends AndroidViewModel {
    public ListServiceViewModel(Application application){
        super(application);
    }
    public LiveData<ListServiceRespon> getService() {
        return ListServiceRepository.getInstance().getMutableLiveData();
    }
}
