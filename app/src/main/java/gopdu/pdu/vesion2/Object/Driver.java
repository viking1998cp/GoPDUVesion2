package gopdu.pdu.vesion2.object;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;


public class Driver implements Serializable {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("birthdate")
    @Expose
    private String birthdate;
    @SerializedName("numberphone")
    @Expose
    private String numberphone;
    @SerializedName("gender")
    @Expose
    private String gender;
    @SerializedName("licenseplates")
    @Expose
    private String licenseplates;
    @SerializedName("imv_driverface")
    @Expose
    private String imvDriverface;
    @SerializedName("imv_identitycard_front")
    @Expose
    private String imvIdentitycardFront;
    @SerializedName("imv_identitycard_backside")
    @Expose
    private String imvIdentitycardBackside;
    @SerializedName("imv_licensedriver_front")
    @Expose
    private String imvLicensedriverFront;
    @SerializedName("imv_licensedriver_backside")
    @Expose
    private String imvLicensedriverBackside;
    @SerializedName("imv_motorcyclepapers_front")
    @Expose
    private String imvMotorcyclepapersFront;
    @SerializedName("imv_motorcyclepapers_backside")
    @Expose
    private String imvMotorcyclepapersBackside;
    @SerializedName("pay")
    @Expose
    private String pay;
    @SerializedName("type_account")
    @Expose
    private String typeAccount;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public String getNumberphone() {
        return numberphone;
    }

    public void setNumberphone(String numberphone) {
        this.numberphone = numberphone;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getLicenseplates() {
        return licenseplates;
    }

    public void setLicenseplates(String licenseplates) {
        this.licenseplates = licenseplates;
    }

    public String getImvDriverface() {
        return imvDriverface;
    }

    public void setImvDriverface(String imvDriverface) {
        this.imvDriverface = imvDriverface;
    }

    public String getImvIdentitycardFront() {
        return imvIdentitycardFront;
    }

    public void setImvIdentitycardFront(String imvIdentitycardFront) {
        this.imvIdentitycardFront = imvIdentitycardFront;
    }

    public String getImvIdentitycardBackside() {
        return imvIdentitycardBackside;
    }

    public void setImvIdentitycardBackside(String imvIdentitycardBackside) {
        this.imvIdentitycardBackside = imvIdentitycardBackside;
    }

    public String getImvLicensedriverFront() {
        return imvLicensedriverFront;
    }

    public void setImvLicensedriverFront(String imvLicensedriverFront) {
        this.imvLicensedriverFront = imvLicensedriverFront;
    }

    public String getImvLicensedriverBackside() {
        return imvLicensedriverBackside;
    }

    public void setImvLicensedriverBackside(String imvLicensedriverBackside) {
        this.imvLicensedriverBackside = imvLicensedriverBackside;
    }

    public String getImvMotorcyclepapersFront() {
        return imvMotorcyclepapersFront;
    }

    public void setImvMotorcyclepapersFront(String imvMotorcyclepapersFront) {
        this.imvMotorcyclepapersFront = imvMotorcyclepapersFront;
    }

    public String getImvMotorcyclepapersBackside() {
        return imvMotorcyclepapersBackside;
    }

    public void setImvMotorcyclepapersBackside(String imvMotorcyclepapersBackside) {
        this.imvMotorcyclepapersBackside = imvMotorcyclepapersBackside;
    }

    public String getPay() {
        return pay;
    }

    public void setPay(String pay) {
        this.pay = pay;
    }

    public String getTypeAccount() {
        return typeAccount;
    }

    public void setTypeAccount(String typeAccount) {
        this.typeAccount = typeAccount;
    }

}
