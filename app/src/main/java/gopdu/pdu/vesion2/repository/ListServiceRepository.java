package gopdu.pdu.vesion2.repository;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import java.util.List;

import gopdu.pdu.vesion2.Common;
import gopdu.pdu.vesion2.GoPDUApplication;
import gopdu.pdu.vesion2.R;
import gopdu.pdu.vesion2.network.ListServiceRespon;
import gopdu.pdu.vesion2.object.Service;
import gopdu.pdu.vesion2.service.APIService;
import gopdu.pdu.vesion2.service.DataService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListServiceRepository {
    private static ListServiceRepository instance;
    public static ListServiceRepository getInstance(){
        if (instance==null){
            instance=new ListServiceRepository();
        }
        return instance;
    }
    private MutableLiveData<ListServiceRespon> mutableLiveData ;
    public MutableLiveData<ListServiceRespon> getMutableLiveData(){
        mutableLiveData = new MutableLiveData<>();
        try {

            DataService dataService = APIService.getService();

            dataService.getservice().enqueue(new Callback<ListServiceRespon>() {
                @Override
                public void onResponse(Call<ListServiceRespon> call, Response<ListServiceRespon> response) {
                    try {

                        ListServiceRespon services = (ListServiceRespon) response.body();
                        if(services.getData()!=null){
                            mutableLiveData.setValue(services);
                        }else {
                            mutableLiveData.setValue(null);
                        }
                        mutableLiveData.setValue(null);
                        Common.ShowToastLong(services.getMessenger());

                    } catch (Exception e) {
                        mutableLiveData.setValue(null);
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<ListServiceRespon> call, Throwable t) {
                    try {
                        mutableLiveData.setValue(null);
                        Common.ShowToastShort(GoPDUApplication.getInstance().getString(R.string.toast_message_not_network_server));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Log.d("BBB", "onFailure: "+t.getMessage());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            mutableLiveData.setValue(null);



        }
        return mutableLiveData;
    }
}
