package gopdu.pdu.vesion2.presenter;

import android.location.Address;

import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.common.api.GoogleApiClient;

import gopdu.pdu.vesion2.modelresponse.CustomerMapResponse;
import gopdu.pdu.vesion2.object.Location;
import gopdu.pdu.vesion2.view.ViewCustomerMapListener;
import gopdu.pdu.vesion2.viewmodel.CustomerMapFragmentViewModel;

public class PresenterCustomerMapFragment implements CustomerMapResponse {

    private CustomerMapFragmentViewModel customerMapViewModel;

    public PresenterCustomerMapFragment(ViewCustomerMapListener callback) {
        customerMapViewModel = new CustomerMapFragmentViewModel(this);
        this.callback = callback;
    }

    private ViewCustomerMapListener callback;

    public void reciverSearchOnClick(int actionId, GoogleApiClient mGoogleApiClient) {
        customerMapViewModel.searchOnClick(actionId, mGoogleApiClient);
    }

    @Override
    public void searchOnClick() {
        callback.searchOnClick();
    }

    @Override
    public void getAddress(Address names) {
        callback.getAddress(names);
    }

    @Override
    public void onPickUp(int postion) {
        callback.onPickUp(postion);
    }

    @Override
    public void onDestination(int postion) {
        callback.onDestination(postion);
    }

    @Override
    public void getDefaultMylocation(android.location.Location location) {
        callback.getDefaultMylocation(location);
    }

    @Override
    public void backUpFinishApp() {
        callback.backUpFinishApp();
    }

    @Override
    public void backToPickUpFromInfomation() {
        callback.backToPickUpFromInfomation();
    }

    @Override
    public void backToPickUpFromCustomPickUp() {
        callback.backToPickUpFromCustomPickUp();
    }

    @Override
    public void backToDestination() {
        callback.backToDestination();
    }


    public void reciverGetAddress(double latitude, double longitude) {
        customerMapViewModel.getAddress(latitude, longitude);
    }

    public void reciverLocationOnclick(boolean customPickup, int postion) {
        customerMapViewModel.locationOnClick(customPickup, postion);
    }




    public void reciverGetDefaultMyloction(FragmentActivity activity, boolean customPickup, Location locationDes, android.location.Location location) {
        customerMapViewModel.getDefaultMylocation(activity, customPickup, locationDes, location);
    }


    public void reciverBackOnClick(boolean customPickup, Location locationDes, float distance) {
        customerMapViewModel.backOnCick(customPickup, locationDes, distance);
    }

    public void pushInfomationTravel(String key) {
        customerMapViewModel.pushInfomationTravel(key);
    }

    @Override
    public void startRide() {
        callback.startRide();
    }

    @Override
    public void endRide() {
        callback.endRide();
    }

    public void reciverEndRide(boolean requestBoolena) {
        customerMapViewModel.endRide(requestBoolena);
    }

}
