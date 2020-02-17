package gopdu.pdu.vesion2.Service;

import org.json.JSONArray;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;

public interface DataService {

    @GET("checkExitsCustomer.php")
    Call<String> checkExits(@QueryMap Map<String,String> params);

    @GET("registerCustomer.php")
    Call<String> registerAccount (@QueryMap Map<String,String> params);
}
