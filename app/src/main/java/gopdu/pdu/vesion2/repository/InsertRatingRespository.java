package gopdu.pdu.vesion2.repository;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import java.util.Map;

import gopdu.pdu.vesion2.Common;
import gopdu.pdu.vesion2.GoPDUApplication;
import gopdu.pdu.vesion2.R;
import gopdu.pdu.vesion2.object.ServerResponse;
import gopdu.pdu.vesion2.service.APIService;
import gopdu.pdu.vesion2.service.DataService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InsertRatingRespository {
    private static InsertRatingRespository instance;

    public static InsertRatingRespository getInstance() {
        if (instance == null)
            instance = new InsertRatingRespository();
        return instance;
    }

    private MutableLiveData<ServerResponse> mutableLiveData;

    public MutableLiveData<ServerResponse> getMutableLiveData(Map<String, String> params) {
        mutableLiveData = new MutableLiveData<>();
        try {
            DataService dataService = APIService.getService();
            dataService.insertRating(params).enqueue(new Callback<ServerResponse>() {
                @Override
                public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                    try {
                        ServerResponse serverResponse = response.body();
                        if (serverResponse.getSuccess()) {
                            mutableLiveData.setValue(serverResponse);
                        } else {
                            mutableLiveData.setValue(null);
                            Common.ShowToastLong(serverResponse.getMessenger());
                        }

                    } catch (Exception e) {
                        mutableLiveData.setValue(null);
                        e.printStackTrace();
                    }

                }

                @Override
                public void onFailure(Call<ServerResponse> call, Throwable t) {

                    try {
                        Log.d("BBB", "onFailure: "+t.getMessage());
                        mutableLiveData.setValue(null);
                        Common.ShowToastShort(GoPDUApplication.getInstance().getString(R.string.toast_message_not_network_server));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            mutableLiveData.setValue(null);

        }
        return mutableLiveData;
    }
}
