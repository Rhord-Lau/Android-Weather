package com.example.myweather.gson;

public class ip {


    /**
     * status : 1
     * info : OK
     * infocode : 10000
     * province : 北京市
     * city : 北京市
     * adcode : 110000
     * rectangle : 116.0119343,39.66127144;116.7829835,40.2164962
     */

    private String status;
    private String info;
    private String infocode;
    private String province;
    private String city;
    private String adcode;
    private String rectangle;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getInfocode() {
        return infocode;
    }

    public void setInfocode(String infocode) {
        this.infocode = infocode;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAdcode() {
        return adcode;
    }

    public void setAdcode(String adcode) {
        this.adcode = adcode;
    }

    public String getRectangle() {
        return rectangle;
    }

    public void setRectangle(String rectangle) {
        this.rectangle = rectangle;
    }
}
