package gopdu.pdu.vesion2.network;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import gopdu.pdu.vesion2.object.History;
import gopdu.pdu.vesion2.object.ServerResponse;

public class HistoryResponse extends ServerResponse {
    @SerializedName("data")
    @Expose
    private ArrayList<History> data;

    public ArrayList<History> getData() {
        return data;
    }

    public void setData(ArrayList<History> data) {
        this.data = data;
    }
}
