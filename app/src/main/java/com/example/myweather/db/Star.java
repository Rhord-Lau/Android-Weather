package com.example.myweather.db;

import org.litepal.crud.LitePalSupport;

//在使用LitePal库进行CRUD时需要此类（关注类）继承DataSupport类，但DataSupport类已被弃用
// 因此用LitePalSupport来代替来替代DataSupport类，来对表进行Create添加数据、Read读取数据、Update修改数据、Delete删除数据
public class Star extends LitePalSupport { //关注类
    int id;
    private String Name;
    private String Adcode;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getAdcode() {
        return Adcode;
    }

    public void setAdcode(String adcode) {
        Adcode = adcode;
    }
}
