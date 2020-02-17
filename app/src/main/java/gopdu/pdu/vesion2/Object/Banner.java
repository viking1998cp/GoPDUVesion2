package gopdu.pdu.vesion2.Object;

public class Banner {

    private int id;
    private String urlImv;

    public Banner() {
    }

    public Banner(int id, String urlImv) {
        this.id = id;
        this.urlImv = urlImv;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrlImv() {
        return urlImv;
    }

    public void setUrlImv(String urlImv) {
        this.urlImv = urlImv;
    }
}
