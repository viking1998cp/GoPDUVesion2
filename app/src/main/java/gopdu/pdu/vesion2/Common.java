package gopdu.pdu.vesion2;

import android.animation.TypeEvaluator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.res.Resources;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Log;
import android.util.Property;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewAnimator;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.util.Strings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.maps.android.SphericalUtil;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Pattern;

public class Common {
    public  static LatLngBounds  LAT_LNG_BOUNDS = new LatLngBounds(
                new LatLng(-40, -168), new LatLng(71, 136));
    //bien luu Activity hien tai
    public static Application Old_Activity = new Application();

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
            Toast toast = Toast.makeText(GoPDUApplication.getInstance(), msg, Toast.LENGTH_LONG);
            centerText(toast.getView());
            toast.show();
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
            Toast toast = Toast.makeText(GoPDUApplication.getInstance(), msg, Toast.LENGTH_SHORT);
            centerText(toast.getView());
            toast.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void centerText(View view) {
        if( view instanceof TextView){
            ((TextView) view).setGravity(Gravity.CENTER);
        }else if( view instanceof ViewGroup){
            ViewGroup group = (ViewGroup) view;
            int n = group.getChildCount();
            for( int i = 0; i<n; i++ ){
                centerText(group.getChildAt(i));
            }
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

    //Format to phone number
    public static String formatPhoneNumber(String phone){
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

    public static boolean checkEmail(final String email){
        Pattern pattern = Pattern.compile(GoPDUApplication.getInstance().getString(R.string.regexEmail));
        if(pattern.matcher(email).matches()){
            return true;
        }else {
            return false;
        }
    }

    public static float getDistance(LatLng destinationLng, LatLng pickupLng){
        return (float) SphericalUtil.computeDistanceBetween(destinationLng, pickupLng);
    }

    public  static LatLng getLocationFromAddress(String strAddress) {

        Geocoder coder = new Geocoder(GoPDUApplication.getInstance());
        List<Address> address;
        LatLng p1 = null;

        try {
            // May throw an IOException
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }

            Address location = address.get(0);
            p1 = new LatLng(location.getLatitude(), location.getLongitude() );

        } catch (IOException ex) {

            ex.printStackTrace();
        }

        return p1;
    }

    public static void hideSoftInput(Activity activity) {
        try {
            if (activity == null || activity.isFinishing()) return;
            Window window = activity.getWindow();
            if (window == null) return;
            View view = window.getCurrentFocus();
            //give decorView a chance
            if (view == null) view = window.getDecorView();
            if (view == null) return;

            InputMethodManager imm = (InputMethodManager) activity.getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm == null || !imm.isActive()) return;
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    //animation visible view
    public static Animation animationVisible() {
        return  AnimationUtils.loadAnimation(GoPDUApplication.getInstance(), R.anim.slide_up);
    }

    //
    public static String formatVNĐ(int price){
        DecimalFormat formatter = new DecimalFormat(GoPDUApplication.getInstance().getString(R.string.patternVNĐ));
        return formatter.format(price);

    }


}
