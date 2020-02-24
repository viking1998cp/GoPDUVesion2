package gopdu.pdu.vesion2.service;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Map;

import gopdu.pdu.vesion2.object.Service;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

public interface DataService {

    @GET("checkExitsCustomer.php")
    Call<String> checkExits(@QueryMap Map<String,String> params);

    @GET("registerCustomer.php")
    Call<String> registerAccount (@QueryMap Map<String,String> params);

    @GET("getservice.php")
    Call<ArrayList<Service>> getservice ();

}
