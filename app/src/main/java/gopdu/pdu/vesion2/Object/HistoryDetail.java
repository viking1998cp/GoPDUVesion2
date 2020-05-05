package gopdu.pdu.vesion2.object;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class HistoryDetail implements Serializable {

    @SerializedName("history")
    @Expose
    private History history;
    @SerializedName("driver")
    @Expose
    private Driver driver;
    @SerializedName("customer")
    @Expose
    private Customer customer;

    public History getHistory() {
        return history;
    }

    public void setHistory(History history) {
        this.history = history;
    }

    public Driver getDriver() {
        return driver;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
}
