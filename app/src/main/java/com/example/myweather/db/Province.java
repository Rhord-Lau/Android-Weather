package com.example.myweather.db;

import org.litepal.crud.LitePalSupport;

//在使用LitePal库进行CRUD时需要此类（省类）继承DataSupport类，但DataSupport类已被弃用
// 因此用LitePalSupport来代替来替代DataSupport类，来对表进行Create添加数据、Read读取数据、Update修改数据、Delete删除数据
public class Province extends LitePalSupport {  //省类
    int id;
    private String provinceName;
    private String provinceAdcode;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProvinceAdcode() {
        return provinceAdcode;
    }

    public void setProvinceAdcode(String provinceAdcode) {
        this.provinceAdcode = provinceAdcode;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }
}
