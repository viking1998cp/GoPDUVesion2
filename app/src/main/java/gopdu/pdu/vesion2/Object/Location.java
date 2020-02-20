package gopdu.pdu.vesion2.object;

import java.io.Serializable;

public class Location implements Serializable {

    private double lat;
    private double logt;
    private String nameLocation;
    private String nameDetailLocation;
    private float distance;
    private String id;

    public Location() {
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLogt() {
        return logt;
    }

    public void setLogt(Double logt) {
        this.logt = logt;
    }

    public String getNameLocation() {
        return nameLocation;
    }

    public void setNameLocation(String nameLocation) {
        this.nameLocation = nameLocation;
    }

    public String getNameDetailLocation() {
        return nameDetailLocation;
    }

    public void setNameDetailLocation(String nameDetailLocation) {
        this.nameDetailLocation = nameDetailLocation;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }
}
