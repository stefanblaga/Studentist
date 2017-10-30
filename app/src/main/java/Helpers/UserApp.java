package Helpers;

/**
 * Created by mario on 16/04/2017.
 */

public class UserApp {

    public String name;
    public String uid;
    public String birthDate;
    public String role;
    public String telephoneNumber;
    public String appVersion;
    public String deviceToken;
    public String city;

    public UserApp()
    {
    }

    public UserApp(String name, String uid, String birthDate, String role, String telephoneNumber, String city) {
        this.name = name;
        this.uid = uid;
        this.birthDate = birthDate;
        this.role = role;
        this.telephoneNumber = telephoneNumber;
        this.city = city;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getTelephoneNumber() {
        return telephoneNumber;
    }

    public void setTelephoneNumber(String telephoneNumber) {
        this.telephoneNumber = telephoneNumber;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }
}
