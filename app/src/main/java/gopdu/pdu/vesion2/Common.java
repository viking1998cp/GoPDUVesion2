package gopdu.pdu.vesion2;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.google.android.gms.common.util.Strings;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Common {

    //date now
    public static String getNgayHienTai() {
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        return df.format(Calendar.getInstance().getTime());
    }

    //Show toast long
    public static void ShowToastLong(String msg) {
        if (Strings.isEmptyOrWhitespace(msg)) {
            msg = "Có lỗi xảy ra, vui lòng kểm tra kết nối internet";
        }
        try {
            Toast.makeText(GoPDUApplication.getInstance(), msg, Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Show toast short
    public static void ShowToastShort(String msg) {
        if (Strings.isEmptyOrWhitespace(msg)) {
            msg = "Có lỗi xảy ra, vui lòng kểm tra kết nối internet";
        }
        try {
            Toast.makeText(GoPDUApplication.getInstance(), msg, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Check connect
    public static boolean checkConnect(){
        boolean haveConnectWfi = false;
        boolean haveConnectMobile = false;
        ConnectivityManager cm =(ConnectivityManager) GoPDUApplication.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] networkInfos = cm.getAllNetworkInfo();
        for(NetworkInfo ni: networkInfos){
            if(ni.getTypeName().equalsIgnoreCase("WIFI")){
                if(ni.isConnected()){
                    haveConnectWfi = true;
                }
            }
            if(ni.getTypeName().equalsIgnoreCase("MOBILE")){
                if(ni.isConnected()){
                    haveConnectMobile = true;
                }
            }
        }
        return haveConnectMobile || haveConnectWfi;
    }
    public static String editPhoneNumber(String phone){
        String newphone="+84";
        if(phone.startsWith("0")){
            newphone = newphone + phone.substring(1,phone.length());
            return  newphone;
        }else if(phone.length()==9){
            newphone = newphone + phone;
            return  newphone;
        }else {
            return phone;
        }
    }
}
