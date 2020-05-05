package gopdu.pdu.vesion2.view;

import android.location.Address;
import android.location.Location;

import com.google.firebase.database.DataSnapshot;

import gopdu.pdu.vesion2.object.HistoryDetail;

public interface ViewCustomerMapListener {
    void searchOnClick();
    void getAddress(Address obj);

    void onPickUp(int postion);

    void onDestination(int postion);

    void getDefaultMylocation(Location location);
    void backUpFinishApp();


    void backToPickUpFromInfomation();

    void backToPickUpFromCustomPickUp();

    void backToDestination();

    void pushInfomationTravel(String idDriver);

    void startRide();

    void endRide();

    void showRatingView(HistoryDetail data);

    void insertRatingSuccess(String messenger);

    void insertRatingFaild(String messenger);

    void resumTrip(DataSnapshot dataSnapshot);
}
