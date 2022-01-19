# Android-Weather

                                            安卓天气预报程序

 一个天气预报程序，最基本的需求是设计程序界面，允许用户输入城市ID，通过访问在线天气API，获取并显示该城市的天气预报数据（至少要显示省、市、日期、数据更新时间、温度、湿度、PM2.5等数据）。对于非法的城市ID（位数不足9位或不存在对应的城市，可以从返回值中判断），应该给出适当的提示。使用适当的数据持久化技术（SQLite或SharedPreferences）缓存用户的查询结果（至少缓存最后三次的查询结果），若用户查询的城市在缓存中，则不再访问在线API，直接从缓存中将结果取出显示。

在上述设计的基础上，天气预报功能仍可以扩展，天气预报的需求仍有：建立省、市两级行政单位的浏览界面(通过解析城市列表数据city.json实现)，允许用户浏览在线API支持的省、市列表，通过点选的方式选择要查询的城市，并访问在线API[3]获取天气数据加以展示。允许用户添加关注的城市（可以考虑在天气数据展示页面中添加“关注”按钮，持久化到SharedPreferences或SQLite数据库），应用程序启动时，读取关注的城市列表，在界面中显著位置向用户展示，用户可以点击关注的城市来查看天气数据。

根据需求分析可知，本软件需要对市、省、县和关注的城市进行查询，建立相关的浏览界面，通过解析城市列表数据json来进行实现，因此需要json类和市、省、县、关注的城市的相关数据库。
进入主活动界面后，可以选择关注当前城市、搜索城市、逐步选择其他城市、显示当前城市天气状况。
设计九个相关前端界面，分别提供主活动界面、关注城市活动界面、天气活动界面、地区选择界面、预测界面、预测项目界面、当前界面、标题界面和天气界面。

以下为效果演示：


![image](https://user-images.githubusercontent.com/73420535/150102361-eec2dbd5-0abc-4f56-b360-9f9ef5fb5a62.png)
![image](https://user-images.githubusercontent.com/73420535/150102368-95c08670-f8dc-4cc0-a077-cd64f6430f02.png)
![image](https://user-images.githubusercontent.com/73420535/150102378-9e7be68c-37e2-4191-9143-1515b721cb4e.png)
![image](https://user-images.githubusercontent.com/73420535/150102387-2213147e-ed26-405e-8d65-47b3f5225aa1.png)

![image](https://user-images.githubusercontent.com/73420535/150102403-d305dd4d-11de-4a2e-93ec-51008ef15381.png)
![image](https://user-images.githubusercontent.com/73420535/150102425-04e9fe13-916f-478d-a2e8-3f397d500981.png)
![image](https://user-images.githubusercontent.com/73420535/150102441-bc0179d7-645b-404b-9df6-65e7f79ed385.png)
