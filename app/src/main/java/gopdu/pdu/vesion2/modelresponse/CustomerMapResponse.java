package gopdu.pdu.vesion2.modelresponse;

import android.location.Address;
import android.location.Location;

public interface CustomerMapResponse {
    void searchOnClick();


    void getAddress(Address obj);


    void onPickUp(int postion);

    void onDestination(int postion);



    void getDefaultMylocation(Location location);

    void backUpFinishApp();


    void backToPickUpFromInfomation();

    void backToPickUpFromCustomPickUp();

    void backToDestination();

    void pushInfomationTravel(String key);
}
