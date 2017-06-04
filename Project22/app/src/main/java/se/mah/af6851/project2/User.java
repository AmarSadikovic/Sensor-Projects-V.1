package se.mah.af6851.project2;

/**
 * Created by Amar on 2017-05-23.
 */

public class User {
    private String name;
    private String longitude;
    private String latitude;


    public User(String name, String latitude, String longitude){
        this.name = name;
        this.longitude = longitude;
        this.latitude = latitude;
    }
    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }





}
