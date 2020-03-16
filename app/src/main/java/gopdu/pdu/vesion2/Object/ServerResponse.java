package gopdu.pdu.vesion2.object;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ServerResponse {
    @SerializedName("success")
    @Expose
    private boolean success;
    @SerializedName("messenger")
    @Expose
    private String messenger;

    public boolean getSuccess() {
        return success;
    }

    public String getMessenger() {
        return messenger;
    }
}
