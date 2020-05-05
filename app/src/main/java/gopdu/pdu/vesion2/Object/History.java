package gopdu.pdu.vesion2.object;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class History implements Serializable {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("customerid")
    @Expose
    private String customerid;
    @SerializedName("driverid")
    @Expose
    private String driverid;
    @SerializedName("pickup_name")
    @Expose
    private String pickupName;
    @SerializedName("pickup_lat")
    @Expose
    private double pickupLat;
    @SerializedName("pickup_logt")
    @Expose
    private double pickupLogt;
    @SerializedName("destination_name")
    @Expose
    private String destinationName;
    @SerializedName("destination_lat")
    @Expose
    private double destinationLat;
    @SerializedName("destination_logt")
    @Expose
    private double destinationLogt;
    @SerializedName("price")
    @Expose
    private int price;
    @SerializedName("rating")
    @Expose
    private int rating;
    @SerializedName("time")
    @Expose
    private String time;
    @SerializedName("status")
    @Expose
    private String status;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCustomerid() {
        return customerid;
    }

    public void setCustomerid(String customerid) {
        this.customerid = customerid;
    }

    public String getDriverid() {
        return driverid;
    }

    public void setDriverid(String driverid) {
        this.driverid = driverid;
    }

    public String getPickupName() {
        return pickupName;
    }

    public void setPickupName(String pickupName) {
        this.pickupName = pickupName;
    }

    public double getPickupLat() {
        return pickupLat;
    }

    public void setPickupLat(double pickupLat) {
        this.pickupLat = pickupLat;
    }

    public double getPickupLogt() {
        return pickupLogt;
    }

    public void setPickupLogt(double pickupLogt) {
        this.pickupLogt = pickupLogt;
    }

    public String getDestinationName() {
        return destinationName;
    }

    public void setDestinationName(String destinationName) {
        this.destinationName = destinationName;
    }

    public double getDestinationLat() {
        return destinationLat;
    }

    public void setDestinationLat(double destinationLat) {
        this.destinationLat = destinationLat;
    }

    public double getDestinationLogt() {
        return destinationLogt;
    }

    public void setDestinationLogt(double destinationLogt) {
        this.destinationLogt = destinationLogt;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
