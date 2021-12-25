package com.example.myweather.db;

import org.litepal.crud.LitePalSupport;

//在使用LitePal库进行CRUD时需要此类（县类）继承DataSupport类，但DataSupport类已被弃用
// 因此用LitePalSupport来代替来替代DataSupport类，来对表进行Create添加数据、Read读取数据、Update修改数据、Delete删除数据
public class County extends LitePalSupport {  //县类
    int id;
    private String countyName;
    private String countyAdcode;
    private int cityId;

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCountyName() {
        return countyName;
    }

    public void setCountyName(String countyName) {
        this.countyName = countyName;
    }

    public String getCountyAdcode() {
        return countyAdcode;
    }

    public void setCountyAdcode(String countyAdcode) {
        this.countyAdcode = countyAdcode;
    }
}
