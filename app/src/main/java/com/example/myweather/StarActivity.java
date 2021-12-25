package com.example.myweather;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.myweather.db.City;
import com.example.myweather.db.County;
import com.example.myweather.db.Star;
import com.example.myweather.gson.Weather;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

public class StarActivity extends AppCompatActivity {

    String TAG = "StarActivity";

    private ListView star_list;

    //展示列表
    private List<String> dataList = new ArrayList<>();

    //关注列表
    private List<Star> starList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_star);
        star_list = (ListView)findViewById(R.id.star_list);


        queryStarList();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,dataList);
        star_list.setAdapter(adapter);
        star_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //待解决：不知道为什么把weatheractivity的启动模式改为singtask后startactivity传值到weatheractivity没用？？
                Intent intent = new Intent(StarActivity.this,WeatherActivity.class);
                String weatherId = starList.get(position).getAdcode();
                intent.putExtra("weather_id",weatherId);

                //把weatherId存到sharepref中
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(StarActivity.this).edit();
                editor.putString("weather_id",weatherId);
                //清空Sharepref中weather和nowWeather，让weatheractivity从服务器获取该城市的天气信息
                editor.putString("weather",null);
                editor.putString("nowWeather",null);
                editor.apply();

                Log.d(TAG, "onItemClick: " + weatherId);
                startActivity(intent);

                finish();
            }
        });

    }

    //从数据库查询star列表
    private void queryStarList(){
        starList = LitePal.findAll(Star.class);
        for (Star star:starList){
            dataList.add(star.getName());
        }
    }


}