package gopdu.pdu.vesion2.network;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import gopdu.pdu.vesion2.object.HistoryDetail;
import gopdu.pdu.vesion2.object.ServerResponse;

public class HistoryDetailRespon extends ServerResponse {

    @SerializedName("data")
    @Expose
    private HistoryDetail data;

    public HistoryDetail getData() {
        return data;
    }

    public void setData(HistoryDetail data) {
        this.data = data;
    }
}
