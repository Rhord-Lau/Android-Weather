package com.example.myweather;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;

import com.example.myweather.R;
import com.example.myweather.db.City;
import com.example.myweather.db.County;
import com.example.myweather.db.Province;
import com.example.myweather.db.Star;
import com.example.myweather.gson.Weather;
import com.example.myweather.util.HttpUtil;
import com.example.myweather.util.Utility;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.litepal.LitePal;
import org.litepal.crud.LitePalSupport;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


public class Choose_AreaFragment extends Fragment {  //选择地区
    public static final int LEVEL_PRONVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTY= 2;
    private static final String TAG = "com.example.myweather.Choose_AreaFragment";

    private ProgressDialog progressDialog;
    private TextView titleText;
    private Button backButton;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private List<String> dataList = new ArrayList<>();

    //省列表
    private List<Province> provinceList;

    //市列表
    private List<City> cityList;

    //县列表
    private List<County> countyList;

    //选中的省份
    private Province selectedProvince;

    //选中的城市
    private City selectedCity;

    //当前选中的级别
    private int currentLevel;

    @Override
    //创建该fragment对应的视图，其中需要创建自己的视图并返回给调用者
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.choose_area,container,false);
        titleText = (TextView) view.findViewById(R.id.title_text);
        backButton = (Button) view.findViewById(R.id.back_button);
        listView = (ListView) view.findViewById(R.id.list_view);
        adapter = new ArrayAdapter<>(getContext(),android.R.layout.simple_list_item_1,dataList);
        listView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {  //在activity完成onCreate()后被调用，来启动对应的活动
        super.onActivityCreated(savedInstanceState);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (currentLevel == LEVEL_PRONVINCE){
                    selectedProvince = provinceList.get(position);
                    queryCities();
                }else if (currentLevel == LEVEL_CITY){
                    selectedCity = cityList.get(position);
                    queryCounties();
                }else if (currentLevel == LEVEL_COUNTY){
                    String weatherId = countyList.get(position).getCountyAdcode();

                    //把weather_id存到shareprfence中，为了解决后面下来刷新的weather_id不同的问题
                    SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getContext()).edit();
                    editor.putString("weather_id",weatherId);
                    editor.apply();

                    //判断碎片在哪个活动中，执行不同的处理逻辑
                    if (getActivity() instanceof MainActivity){
                        //跳转到天气界面activity
                        Intent intent = new Intent(getActivity(),WeatherActivity.class);
                        //把点击county的adcode传到显示天气界面
                        intent.putExtra("weather_id",weatherId);
                        startActivity(intent);
                        getActivity().finish();
                    }else if (getActivity() instanceof WeatherActivity){
                        WeatherActivity activity = (WeatherActivity) getActivity();
                        activity.drawerLayout.closeDrawers();
                        activity.swipeRefresh.setRefreshing(true);
                        activity.requestWeather(weatherId);
                        activity.requestNowWeather(weatherId);

                        Log.d(TAG, "onItemClick: " + weatherId);

                        //切换城市后需要初始化star的状态，因为再选择切换区域后后不会再执行oncreat等生命周期，需要在这里刷新，上面requestWeather同理
                        Button star_button = activity.findViewById(R.id.star_button);
                        List<Star> starList = LitePal.where("adcode = ?", String.valueOf(weatherId)).find(Star.class);
                        //查询不到，当前adcode未关注，设置star为白色
                        if (starList.size() == 0) {
                            Log.d(TAG, "onCreate: " + "null");
                            star_button.setBackgroundResource(R.drawable.ic_star);
                        } else {
                            Log.d(TAG, "onCreate: " + "star");
                            //查询到了，当前adcode关注了，设置star为红色
                            star_button.setBackgroundResource(R.drawable.ic_star_red);

                        }

                    }

                }
            }
        });


        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentLevel == LEVEL_COUNTY){
                    queryCities();
                }else if (currentLevel == LEVEL_CITY){
                    queryProvinces();
                }
            }
        });

        queryProvinces();

    }


    //查询全国的所有省，优先从数据库查询，如果没有查询到再去服务器上查询
    private void queryProvinces(){
        titleText.setText("中国");
        backButton.setVisibility(View.GONE);
        //DataSupport被弃用了，用LitePal代替
        provinceList = LitePal.findAll(Province.class);
        if (provinceList.size() > 0){
            dataList.clear();
            for (Province province:provinceList){
//                Log.d(TAG, "queryProvinces: " + province.getProvinceName());
                dataList.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_PRONVINCE;
        }else {
            String address = "https://restapi.amap.com/v3/config/district?key=4767f5222ab9baac825697fdd37b2bc8";
//            Log.d(TAG, "queryProvinces: " + address);
            queryFromServer(address,"province");
        }
    }

    //查询选中的省内所有市，优先从数据库查询，如果没有查询到再去服务器上查询
    private void queryCities() {
        titleText.setText(selectedProvince.getProvinceName());
        backButton.setVisibility(View.VISIBLE);
        //DataSupport被弃用了，用LitePal代替
        cityList = LitePal.where("provinceid = ?",String.valueOf(selectedProvince.getId())).find(City.class);
        if (cityList.size() > 0){
            dataList.clear();
            for (City city : cityList){
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_CITY;
        }else {
            String provinceAdcode = selectedProvince.getProvinceAdcode();
            String address = "https://restapi.amap.com/v3/config/district?key=4767f5222ab9baac825697fdd37b2bc8&keywords=" + provinceAdcode;
//            Log.d(TAG, "queryCities: " + address);
            queryFromServer(address,"city");
        }

    }

    //查询选中市内的所有县，优先从数据库查询，如果没有查询到再去服务器上查询
    private void queryCounties(){
        titleText.setText(selectedCity.getCityName());
        backButton.setVisibility(View.VISIBLE);
        //DataSupport被弃用了，用LitePal代替
        countyList = LitePal.where("cityid = ?",String.valueOf(selectedCity.getId())).find(County.class);
        if (countyList.size() > 0){
            dataList.clear();
            for (County county : countyList){
                dataList.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_COUNTY;
        }else {
            String cityAdcode = selectedCity.getCityAdcode();
            String address = "https://restapi.amap.com/v3/config/district?key=4767f5222ab9baac825697fdd37b2bc8&keywords=" + cityAdcode;
//            Log.d(TAG, "queryCounties: " + address);
            queryFromServer(address,"county");
        }

    }

    //根据传入的地址和类型从服务器上查询省市县数据
    private void queryFromServer(String address,final String type){
        showProgressDialog();
        HttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseText = response.body().string();
                boolean result = false;
                if ("province".equals(type)){
                    result = Utility.handleProvincesResponse(responseText);
                }else if ("city".equals(type)){
                    result = Utility.handleCitiesResponse(responseText,selectedProvince.getId());
                }else if ("county".equals(type)){
                    result = Utility.handleCountiesResponse(responseText,selectedCity.getId());
                }

                if (result){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if ("province".equals(type)){
                                queryProvinces();
                            }else if ("city".equals(type)){
                                queryCities();
                            }else if ("county".equals(type)){
                                queryCounties();
                            }
                        }
                    });
                }
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                //通过runOnUiThread()方法回到主线程逻辑
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(getContext(),"加载失败",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }

    //显示进度对话框
    private void showProgressDialog(){
        if (progressDialog == null){
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("正在加载");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    //关闭进度对话框
    private void closeProgressDialog(){
        if (progressDialog != null){
            progressDialog.dismiss();
        }
    }
}






