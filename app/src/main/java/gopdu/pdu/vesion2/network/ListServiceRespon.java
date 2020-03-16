package gopdu.pdu.vesion2.network;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import gopdu.pdu.vesion2.object.ServerResponse;
import gopdu.pdu.vesion2.object.Service;

public class ListServiceRespon extends ServerResponse {
    @SerializedName("data")
    @Expose
    private ArrayList<Service> data;

    public ArrayList<Service> getData() {
        return data;
    }

    public void setData(ArrayList<Service> data) {
        this.data = data;
    }
}
