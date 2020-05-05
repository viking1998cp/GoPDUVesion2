package gopdu.pdu.vesion2.repository;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import java.util.Map;

import gopdu.pdu.vesion2.Common;
import gopdu.pdu.vesion2.GoPDUApplication;
import gopdu.pdu.vesion2.R;
import gopdu.pdu.vesion2.network.HistoryDetailRespon;
import gopdu.pdu.vesion2.service.APIService;
import gopdu.pdu.vesion2.service.DataService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CheckRattingRepository {

    private static CheckRattingRepository instance;

    public static CheckRattingRepository getInstance() {
        if (instance == null)
            instance = new CheckRattingRepository();
        return instance;
    }

    private MutableLiveData<HistoryDetailRespon> mutableLiveData;

    public MutableLiveData<HistoryDetailRespon> getMutableLiveData(Map<String, String> params) {
        mutableLiveData = new MutableLiveData<>();
        try {
            DataService dataService = APIService.getService();
            dataService.checkRating(params).enqueue(new Callback<HistoryDetailRespon>() {
                @Override
                public void onResponse(Call<HistoryDetailRespon> call, Response<HistoryDetailRespon> response) {
                    try {
                        HistoryDetailRespon historyDetailRespon = response.body();
                        if (historyDetailRespon.getData() != null) {
                            mutableLiveData.setValue(historyDetailRespon);
                        } else {
                            mutableLiveData.setValue(null);
                            Common.ShowToastShort(GoPDUApplication.getInstance().getString(R.string.toast_message_not_network_server));
                        }

                    } catch (Exception e) {
                        Log.d("BBB", "onResponse: "+e.getMessage());
                        mutableLiveData.setValue(null);
                        e.printStackTrace();
                    }

                }

                @Override
                public void onFailure(Call<HistoryDetailRespon> call, Throwable t) {

                    try {
                        Log.d("BBB", "onResponse: "+t.getMessage());
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
