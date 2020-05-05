package gopdu.pdu.vesion2.repository;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import java.util.Map;

import gopdu.pdu.vesion2.Common;
import gopdu.pdu.vesion2.GoPDUApplication;
import gopdu.pdu.vesion2.R;
import gopdu.pdu.vesion2.network.HistoryResponse;
import gopdu.pdu.vesion2.service.APIService;
import gopdu.pdu.vesion2.service.DataService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GetHistoryRepository {

    private static GetHistoryRepository instance;

    public static GetHistoryRepository getInstance() {
        if (instance == null)
            instance = new GetHistoryRepository();
        return instance;
    }

    private MutableLiveData<HistoryResponse> mutableLiveData;

    public MutableLiveData<HistoryResponse> getMutableLiveData(Map<String, String> params) {
        mutableLiveData = new MutableLiveData<>();
        try {
            DataService dataService = APIService.getService();
            dataService.getHistory(params).enqueue(new Callback<HistoryResponse>() {
                @Override
                public void onResponse(Call<HistoryResponse> call, Response<HistoryResponse> response) {
                    try {
                        HistoryResponse historyResponse = response.body();
                        if (historyResponse != null) {
                            mutableLiveData.setValue(historyResponse);
                        } else {
                            mutableLiveData.setValue(null);
                            Common.ShowToastShort(historyResponse.getMessenger());
                        }


                    } catch (Exception e) {
                        mutableLiveData.setValue(null);
                        Log.d("BBB", "onResponse: "+e.toString());
                        e.printStackTrace();

                    }

                }

                @Override
                public void onFailure(Call<HistoryResponse> call, Throwable t) {
                    Log.d("BBB", "onResponse: "+t.toString());
                    try {
                        mutableLiveData.setValue(null);
                        Common.ShowToastShort(GoPDUApplication.getInstance().getString(R.string.checkConnect));
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
