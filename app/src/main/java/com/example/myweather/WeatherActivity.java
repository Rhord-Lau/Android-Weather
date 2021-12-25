package com.example.myweather;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myweather.db.City;
import com.example.myweather.db.Province;
import com.example.myweather.db.Star;
import com.example.myweather.gson.NowWeather;
import com.example.myweather.gson.Weather;
import com.example.myweather.gson.ip;
import com.example.myweather.service.AutoUpdateService;
import com.example.myweather.util.HttpUtil;
import com.example.myweather.util.Utility;

import org.jetbrains.annotations.NotNull;
import org.litepal.LitePal;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {

    String TAG = "WeatherActivity";
    public SwipeRefreshLayout swipeRefresh;
    public DrawerLayout drawerLayout;
    public Button navButton;
    public Button search_button;
    public SearchView search_input;
    public Button star_button;
    public Button list_button;

    private ScrollView weatherLayout;
    private TextView titleCity;
    private TextView titleUpdateTime;
    private TextView degreeText;
    private TextView weatherInfoText;
    private LinearLayout forecastLayout;
    private TextView humidityText;
    private TextView winddirectionText;
    private TextView windpowerText;


    private String weatherId;   //把weatheId定义在外部，方便后面刷新更新

    //关注列表
    private List<Star> starList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

    }


    //因为我给weatheractivity设置的启动模式是sigletask，所以从star列表返回后会从onStart开始执行
    @Override
    protected void onStart() {
        super.onStart();

        //初始化各控件
        weatherLayout = (ScrollView) findViewById(R.id.weather_layout);
        titleCity = (TextView) findViewById(R.id.title_city);
        titleUpdateTime = (TextView) findViewById(R.id.title_update_time);
        degreeText = (TextView) findViewById(R.id.degree_text);
        weatherInfoText = (TextView) findViewById(R.id.weather_info_text);
        forecastLayout = (LinearLayout) findViewById(R.id.forecast_layout);
        humidityText = (TextView) findViewById(R.id.humidity_text);
        winddirectionText = (TextView) findViewById(R.id.winddirection_text);
        windpowerText = (TextView) findViewById(R.id.windpower_text);

        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);

        //初始化搜索按钮和搜索框
        search_button = (Button) findViewById(R.id.sea_button);
        search_input = (SearchView) findViewById(R.id.search_input);

        //实现按下搜索按钮弹出搜索框
        search_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //设置搜索框可见
                search_input.setVisibility(View.VISIBLE);

                //设置搜索框自动弹出
                search_input.setIconifiedByDefault(true);
                search_input.setFocusable(true);
                search_input.setIconified(false);
                search_input.requestFocusFromTouch();
            }
        });

        //设置关闭搜索框时搜索框不可见（待解决：优化到点击搜索框外搜索框关闭）
        search_input.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                search_input.setVisibility(View.GONE);
                return false;
            }
        });

        //搜索框搜索事件
        search_input.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //待解决：解决搜索id不存在时候的问题

                //更新数据
                requestWeather(query);
                requestNowWeather(query);

                //设置搜索框不可见
                search_input.setVisibility(View.GONE);

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        //实现关注功能
        //从sharePreference取出Weatherid
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        weatherId = prefs.getString("weather_id", null);

        Log.d(TAG, "onCreate:  star " + weatherId);
        star_button = (Button) findViewById(R.id.star_button);
        starList = LitePal.where("adcode = ?", String.valueOf(weatherId)).find(Star.class);
        //初始化
        //查询不到，当前adcode未关注，设置star为白色
        if (starList.size() == 0) {
            Log.d(TAG, "onCreate: " + "null");
            star_button.setBackgroundResource(R.drawable.ic_star);
        } else {
            Log.d(TAG, "onCreate: " + "star");
            //查询到了，当前adcode关注了，设置star为红色
            star_button.setBackgroundResource(R.drawable.ic_star_red);

        }
        //点击逻辑
        star_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //从数据库里查询是否存在这个adcode的记录
                Log.d(TAG, "Star onClick: " + weatherId);
                starList = LitePal.where("adcode = ?", String.valueOf(weatherId)).find(Star.class);
                //查询不到，当前adcode未关注，点击设置star为红色
                if (starList.size() == 0) {
                    star_button.setBackgroundResource(R.drawable.ic_star_red);

                    //点击后，把adcode存入到数据库中
                    Star star = new Star();
                    star.setAdcode(weatherId);
                    star.setName(titleCity.getText().toString());
                    star.save();

                } else {
                    //查询到了，当前adcode关注了，点击设置star为白色
                    star_button.setBackgroundResource(R.drawable.ic_star);

                    //点击后，删除数据库的这条adcode记录
                    LitePal.deleteAll(Star.class, "adcode = " + weatherId);
                }
            }
        });

        //点击弹出关注城市列表
        list_button = (Button) findViewById(R.id.list_button);
        list_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WeatherActivity.this, StarActivity.class);
                startActivity(intent);
            }
        });


        //从sharePreference取出Weather和NowWeather
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = prefs.getString("weather", null);
        String nowWeatherString = prefs.getString("nowWeather", null);

        Log.d(TAG, "onCreate: xxxxxxxxxxxxxxxxxxxxxxxxxx");
        if (weatherString != null && nowWeatherString != null) {
            //有缓存时直接解析天气数据
            Weather weather = Utility.handleWeatherResponse(weatherString);
            NowWeather nowWeather = Utility.handleNowWeatherResponse(nowWeatherString);

            weatherId = weather.getForecasts().get(0).getAdcode();
            Log.d(TAG, "onCreate: " + weatherId);
            //展示数据
            showWeatherInfo(weather);
            showNowWeatherInfo(nowWeather);

        } else {
            //无缓存时去服务器查询天气
            weatherId = getIntent().getStringExtra("weather_id");
            weatherLayout.setVisibility(View.INVISIBLE);
            Log.d(TAG, "onStart:  intent " + weatherId);

            //如果不是intent传过来，从shanrepref中获取weatherId
            if (weatherId == null) {
                //从sharePreference取出Weather和NowWeather
                weatherId = prefs.getString("weather_id", null);
                if (weatherId == null){
                    //如果这是sharepre中为空，说明是程序刚启动
                    //访问IP，将adcode存到sharepref中
                    weatherId = requestIp();
                }else {
                    Log.d(TAG, "onStart: intent null " + weatherId);
                    requestWeather(weatherId);
                    requestNowWeather(weatherId);
                }


            } else {
                //如果intent传过来不为空，说明是通过切换城市访问的，这时去服务器获取并展示数据
                requestWeather(weatherId);
                requestNowWeather(weatherId);
            }
        }


        //下拉刷新，根据weatheId重新去服务器请求加载到界面
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //从sharepref中读取weatherId，为了解决刷新的问题
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this);
                weatherId = prefs.getString("weather_id", null);

                Log.d(TAG, "onRefresh: " + weatherId);
                requestWeather(weatherId);
                requestNowWeather(weatherId);
            }
        });

        //滑动显示菜单的实现
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navButton = (Button) findViewById(R.id.nav_button);
        navButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: -----------xxxxxxxxxxxxxxxx----------------");
    }

    //根据请求获取位置信息
    public String requestIp(){
        String ipUrl = "https://restapi.amap.com/v3/ip?key=4767f5222ab9baac825697fdd37b2bc8";
        HttpUtil.sendOkHttpRequest(ipUrl, new Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                final String responseText = response.body().string();
                final ip ip = Utility.handleIpResponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (ip != null && "1".equals(ip.getStatus())){
                            Log.d(TAG, "run: " + ip.getAdcode());
                            //将查询到的adcode存入缓存
                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                            editor.putString("weather_id",ip.getAdcode());   //将weatherid更新
                            editor.apply();

                            //
                            requestWeather(ip.getAdcode());
                            requestNowWeather(ip.getAdcode());
                        }else {
                            Toast.makeText(WeatherActivity.this,"获取位置信息失败",Toast.LENGTH_SHORT).show();
                        }
                        //默认不刷新
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this,"获取位置信息失败",Toast.LENGTH_SHORT).show();
                        //默认不刷新
                        swipeRefresh.setRefreshing(false);
                    }
                });

            }
        });
        return ipUrl;
    }

    //根据天气id请求城市weather信息
    public void requestWeather(final String weatherId){
        Log.d(TAG, "requestWeather: " + weatherId);
        String weatherUrl = "https://restapi.amap.com/v3/weather/weatherInfo?key=4767f5222ab9baac825697fdd37b2bc8&city=" + weatherId + "&extensions=all&output=JSON";
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                final String responseText = response.body().string();
                final Weather weather = Utility.handleWeatherResponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (weather != null && "1".equals(weather.getCount())){
                            Log.d(TAG, "run: ------------------------------------------");
//                            Log.d(TAG, "run: " + responseText);
                            //将查询到的weather存入缓存
                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                            editor.putString("weather",responseText);
                            editor.putString("weather_id",weatherId);   //将weatherid更新
                            editor.apply();
                            //展示数据
                            showWeatherInfo(weather);
                        }else {
                            Toast.makeText(WeatherActivity.this,"输入的编码有误",Toast.LENGTH_SHORT).show();
                        }
                        //默认不刷新
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this,"获取未来天气信息失败",Toast.LENGTH_SHORT).show();
                        //默认不刷新
                        swipeRefresh.setRefreshing(false);
                    }
                });

            }
        });
    }

    //根据天气id请求城市nowWeather信息
    public void requestNowWeather(final String weatherId){
        String weatherUrl = "https://restapi.amap.com/v3/weather/weatherInfo?key=4767f5222ab9baac825697fdd37b2bc8&city=" + weatherId + "&output=JSON";
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                final String responseText = response.body().string();
                final NowWeather nowWeather = Utility.handleNowWeatherResponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (nowWeather != null && "1".equals(nowWeather.getCount())){
//                            Log.d(TAG, "run: " + responseText);
                            //将查询到的weather存入缓存
                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                            editor.putString("nowWeather",responseText);
                            editor.putString("weather_id",weatherId);   //将weatherid更新
                            editor.apply();
                            //展示数据
                            showNowWeatherInfo(nowWeather);
                        }else {
                            Toast.makeText(WeatherActivity.this,"输入的编码有误",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this,"获取今天天气信息失败",Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
    }

    //处理并展示weather实体类中的数据
    private void showWeatherInfo(Weather weather){
        forecastLayout.removeAllViews();
        for (Weather.ForecastsBean.CastsBean castsBean : weather.getForecasts().get(0).getCasts()){
            View view = LayoutInflater.from(this).inflate(R.layout.forecast_item,forecastLayout,false);
            TextView dateText = (TextView) view.findViewById(R.id.date_text);
            TextView infoText = (TextView) view.findViewById(R.id.info_text);
            TextView maxText = (TextView) view.findViewById(R.id.max_text);
            TextView minText = (TextView) view.findViewById(R.id.min_text);
//            Log.d(TAG, "showWeatherInfo: " + castsBean.getDate());
            dateText.setText(castsBean.getDate());
            infoText.setText(castsBean.getDayweather() + "～" + castsBean.getNightweather());
            maxText.setText(castsBean.getDaytemp() + "°C");
            minText.setText(castsBean.getNighttemp() + "°C");
            forecastLayout.addView(view);
        }

        weatherLayout.setVisibility(View.VISIBLE);

        //在autoservice中我们写了每隔8h自动更新缓存中的数据
        //在这里激活
        Intent intent = new Intent(this, AutoUpdateService.class);
        startService(intent);
    }

    //处理并展示nowWeather实体类中的数据
    private void showNowWeatherInfo(NowWeather nowWeather){
        String cityName = nowWeather.getLives().get(0).getCity();
        String updateTime = nowWeather.getLives().get(0).getReporttime();
        String degree = nowWeather.getLives().get(0).getTemperature() + "°C";
        String weatherInfo = nowWeather.getLives().get(0).getWeather();
        String humidity = nowWeather.getLives().get(0).getHumidity();
        String winddirection = nowWeather.getLives().get(0).getWinddirection();
        String windpower = nowWeather.getLives().get(0).getWindpower();

        titleCity.setText(cityName);
        titleUpdateTime.setText(updateTime);
        degreeText.setText(degree);
        weatherInfoText.setText(weatherInfo);
        humidityText.setText(humidity);
        winddirectionText.setText(winddirection);
        windpowerText.setText(windpower);

        weatherLayout.setVisibility(View.VISIBLE);

        //在autoservice中我们写了每隔8h自动更新缓存中的数据
        //在这里激活
        Intent intent = new Intent(this, AutoUpdateService.class);
        startService(intent);

    }
}