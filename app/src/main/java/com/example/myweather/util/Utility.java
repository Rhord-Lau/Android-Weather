package com.example.myweather.util;

import android.text.TextUtils;

import com.example.myweather.db.City;
import com.example.myweather.db.County;
import com.example.myweather.db.Province;
import com.example.myweather.gson.NowWeather;
import com.example.myweather.gson.Weather;
import com.example.myweather.gson.ip;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Utility {
    //解析和处理服务器放回的省级数据
    public static boolean handleProvincesResponse(String response){
        if (!TextUtils.isEmpty(response)){
            try {
                JSONObject result = new JSONObject(response);
                JSONArray allProvinces = result.getJSONArray("districts").getJSONObject(0).getJSONArray("districts");

                for (int i = 0;i < allProvinces.length();i++){
                    JSONObject provinceObject = allProvinces.getJSONObject(i);
                    Province province = new Province();
                    province.setProvinceName(provinceObject.getString("name"));
                    province.setProvinceAdcode(provinceObject.getString("adcode"));
                    province.save();
                }
                return true;
            }catch (JSONException e){
                e.printStackTrace();
            }
        }

        return false;
    }

    //解析和处理服务器放回的市级数据
    public static boolean handleCitiesResponse(String response,int provinceId){
        if (!TextUtils.isEmpty(response)){
            try {
                JSONObject result = new JSONObject(response);
                JSONArray allCities = result.getJSONArray("districts").getJSONObject(0).getJSONArray("districts");

                for (int i = 0;i < allCities.length();i++){
                    JSONObject cityObject = allCities.getJSONObject(i);
                    City city = new City();
                    city.setCityName(cityObject.getString("name"));
                    city.setCityAdcode(cityObject.getString("adcode"));
                    city.setProvinceId(provinceId);
                    city.save();
                }
                return true;
            }catch (JSONException e){
                e.printStackTrace();
            }
        }

        return false;
    }

    //解析和处理服务器放回的县级数据
    public static boolean handleCountiesResponse(String response,int cityId){
        if (!TextUtils.isEmpty(response)){
            try {
                JSONObject result = new JSONObject(response);
                JSONArray allCounties = result.getJSONArray("districts").getJSONObject(0).getJSONArray("districts");

                for (int i = 0;i < allCounties.length();i++){
                    JSONObject countyObject = allCounties.getJSONObject(i);
                    County county = new County();
                    county.setCountyName(countyObject.getString("name"));
                    county.setCountyAdcode(countyObject.getString("adcode"));
                    county.setCityId(cityId);
                    county.save();

                }
                return true;
            }catch (JSONException e){
                e.printStackTrace();
            }
        }

        return false;
    }

    //将返回的JSON数据解析成Weather（预报）实体类
    public static Weather handleWeatherResponse(String response){

        return new Gson().fromJson(response,Weather.class);
    }

    //将返回的JSON数据解析成Weather（现在）实体类
    public static NowWeather handleNowWeatherResponse(String response){

        return new Gson().fromJson(response, NowWeather.class);
    }

    //将返回的JSON数据解析成ip（位置）实体类
    public static ip handleIpResponse(String response){

        return new Gson().fromJson(response, ip.class);
    }
























}
