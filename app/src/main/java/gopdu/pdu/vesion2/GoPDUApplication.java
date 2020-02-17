package gopdu.pdu.vesion2;

import android.app.Application;

import com.google.gson.Gson;

public class GoPDUApplication extends Application {
    private static GoPDUApplication instance;
    private Gson mGSon;
    @Override
    public void onCreate() {
        super.onCreate();
        instance=this;
        mGSon=new Gson();
    }
    public static GoPDUApplication getInstance(){
        return instance;
    }
    public Gson getGSon() {
        return mGSon;
    }
}
