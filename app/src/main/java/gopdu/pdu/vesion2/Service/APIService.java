package gopdu.pdu.vesion2.Service;

public class APIService {
    private static String Base_Url = "https://gopdu.000webhostapp.com/serverGoPDU/";
    public static DataService getService(){
        return APIRetrofitClient .getClient(Base_Url).create(DataService.class);
    }

}
