package gopdu.pdu.vesion2.viewmodel;

import android.location.Address;
import android.location.Geocoder;
import android.util.Log;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.common.api.GoogleApiClient;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import gopdu.pdu.vesion2.Common;
import gopdu.pdu.vesion2.GoPDUApplication;
import gopdu.pdu.vesion2.R;
import gopdu.pdu.vesion2.modelresponse.CustomerMapResponse;
import gopdu.pdu.vesion2.object.Location;

public class CustomerMapFragmentViewModel {

    CustomerMapResponse callback;

    public CustomerMapFragmentViewModel(CustomerMapResponse callback) {
        this.callback = callback;
    }

    public void searchOnClick(int actionId, GoogleApiClient mGoogleApiClient) {
        if (actionId == EditorInfo.IME_ACTION_DONE
                || actionId == EditorInfo.IME_ACTION_SEARCH
                || actionId == EditorInfo.IME_ACTION_NEXT
        ) {

            if (mGoogleApiClient.isConnected()) {
                callback.searchOnClick();
            }

        }
    }

    public void getAddress(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(
                GoPDUApplication.getInstance(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            Address obj = addresses.get(0);

            callback.getAddress(obj);

            // Toast.makeText(this, "Address=>" + add,
            // Toast.LENGTH_SHORT).show();

            // TennisAppActivity.showDialog(add);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Toast.makeText(GoPDUApplication.getInstance(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void locationOnClick(boolean customPickup, int postion) {
        if(customPickup){
            callback.onPickUp(postion);
        }else {
            callback.onDestination(postion);
        }

    }


    public void getDefaultMylocation(FragmentActivity activity, boolean customPickup, Location locationDes, android.location.Location location) {
        Log.d("BBB", "getDefaultMylocation: "+activity+"/"+customPickup+"/"+location);
        if (activity != null && customPickup == false && locationDes.getNameLocation() == null) {
            callback.getDefaultMylocation(location);
        }
    }

    private long back_pressed;
    public void backOnCick(boolean customPickup, Location locationDes, float distance) {
        if (locationDes != null && customPickup == true) {
            callback.backToPickUpFromCustomPickUp();

        } else if (locationDes != null && distance != 0) {
            callback.backToPickUpFromInfomation();
        } else if (locationDes != null) {
            callback.backToDestination();
        }else {
            if (back_pressed + 2000 > System.currentTimeMillis()){
                callback.backUpFinishApp();
            }
            else{
               Common.ShowToastShort(
                        GoPDUApplication.getInstance().getString(R.string.click_again_exits));
            }
            back_pressed = System.currentTimeMillis();
        }
    }


    public void pushInfomationTravel(String key) {
        callback.pushInfomationTravel(key);
    }

}
