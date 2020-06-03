package data.model;

public class History {
    private String date;
    private String hour;
    private Integer countShakes;
    private String location;
    private Double latitude;
    private Double longitude;

    public History(String date, String hour, Integer countShakes, String location, Double latitude, Double longitude) {
        this.date = date;
        this.hour = hour;
        this.countShakes = countShakes;
        this.location = location;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public void setCountShakes(Integer countShakes) {
        this.countShakes = countShakes;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getDate() {
        return date;
    }

    public String getHour() {
        return hour;
    }

    public Integer getCountShakes() {
        return countShakes;
    }

    public String getLocation() {
        return location;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }


    @Override
    public String toString() {
        return "History{" +
                "date='" + date + '\'' +
                ", hour='" + hour + '\'' +
                ", countShakes=" + countShakes +
                ", location='" + location + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}
