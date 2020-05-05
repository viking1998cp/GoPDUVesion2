package gopdu.pdu.vesion2.service;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import gopdu.pdu.vesion2.network.HistoryDetailRespon;
import gopdu.pdu.vesion2.network.HistoryResponse;
import gopdu.pdu.vesion2.network.ListServiceRespon;
import gopdu.pdu.vesion2.object.ServerResponse;
import gopdu.pdu.vesion2.object.Service;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

public interface DataService {

    @GET("checkExitsAccount.php")
    Call<ServerResponse> checkExits(@QueryMap Map<String,String> params);

    @GET("registerCustomer.php")
    Call<ServerResponse> registerAccount (@QueryMap Map<String,String> params);

    @GET("getService.php")
    Call<ListServiceRespon> getservice ();

    @GET("checkRating.php")
    Call<HistoryDetailRespon> checkRating(@QueryMap Map<String,String> params);

    @GET("insertRatingFromCustomer.php")
    Call<ServerResponse> insertRating (@QueryMap Map<String,String> params);

    @GET("getHistory.php")
    Call<HistoryResponse> getHistory (@QueryMap Map<String,String> params);

    @GET("getHistoryDetail.php")
    Call<HistoryDetailRespon> getHistoryDetail (@QueryMap Map<String,String> params);

}
