package com.example.myweather.db;

import org.litepal.crud.LitePalSupport;

//在使用LitePal库进行CRUD时需要此类（市类）继承DataSupport类，但DataSupport类已被弃用
// 因此用LitePalSupport来代替来替代DataSupport类，来对表进行Create添加数据、Read读取数据、Update修改数据、Delete删除数据
public class City extends LitePalSupport { //市类
    int id;
    private String cityName;
    private String cityAdcode;
    private int provinceId;

    public int getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(int provinceId) {
        this.provinceId = provinceId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getCityAdcode() {
        return cityAdcode;
    }

    public void setCityAdcode(String cityAdcode) {
        this.cityAdcode = cityAdcode;
    }
}
