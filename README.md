# mall

## 说明

> 基于SpringBoot+MyBatis-plus的电商系统，包括前台商城系统及后台管理系统。

> 如果该项目对您有帮助，您可以点右上角 "Star" 支持一下 谢谢！

> 或者您可以 "follow" 一下，该项目将持续更新，不断完善功能。

> 项目交流人QQ群：) 133070260 725021772]

> 如有问题或者好的建议可以在 Issues 中提。


## 前言

`mallplus`项目致力于打造一个完整的电商系统，采用现阶段流行技术实现。

## 项目介绍

`mallplus`项目是一套电商系统，包括前台商城系统及后台管理系统，小程序，h5，基于SpringBoot+MyBatis实现。
前台商城系统包含首页门户、商品推荐、商品搜索、商品展示、购物车、订单流程、会员中心、客户服务、帮助中心等模块。
后台管理系统包含商品管理、订单管理、会员管理、促销管理、运营管理、内容管理、统计报表、财务管理、权限管理、代码生成设置等模块。

### 项目演示


 _下载项目根路径下的mallplus.sql 和mall-web-admin(后台管理vue项目，需要启动mllplus-admin项目)
修改mallplus-admin中application-dev.properties 的数据库和redis配置_ 

[部署文档](https://gitee.com/zscat/mallplus/wikis/pages/preview?sort_id=1786312&doc_id=326093)
> 后台管理系统

- 1. 商城总后台 http://51wangshi.com:8087/index
- 2.商户后台和用户端h5 小程序 和app（群文件下载） 
- 商户端演示 http://51wangshi.com:8090/
- uniapp h5演示 http://51wangshi.com:8082/
- app下载 https://share.weiyun.com/5KsC2YL
pc演示  [http://51wangshi.com:8088/#/](http://51wangshi.com:8088/#/)  


小程序下载 地址 https://gitee.com/catshen/mall-applet
![输入图片说明](https://images.gitee.com/uploads/images/2020/0109/102351_cfd0b0c7_134431.png "屏幕截图.png")
### 关注公众号获取最全部署教程和后台管理的vue前端，以及uniapp生成的h5 小程序和app和演示地址
[TOC]


### 组织结构

``` lua
mall
├── mallplus-mbg -- MyBatisGenerator生成的数据库操作代码
├── mallplus-admin -- 后台商城管理系统接口
├── mall-search -- 基于Elasticsearch的商品搜索系统
├── mallplus-portal -- 前台商城系统接口
└── mall-demo -- 框架搭建时的测试代码
├── 前端项目`mallplus-admin-web` 
├── 小前端项目  uniapp 
```

**开源版本功能列表 ** 

 _下载项目根路径下的mallplus.sql 和mall-web-admin(vue项目)
修改mallplus-admin中application-dev.properties 的数据库和redis配置_ 


- 2019-01-03
- 首页统计
- 
- 2019-01-06 到 2019-02-03
- 商品列表
- 商品分类
- 商品类型
- 品牌管理
- 相册表列
- 赠礼分类
- 赠礼列表
- 商品评论
- 商品规格
- 
- 2019-02-03 到 2019-03-03
- 菜单管理
- 用户管理
- 代码生成
- 区域列表
- 系统配置
- 定时任务
- 定时任务日志
- 2019-03-03 到 2019-04-03
- 发货列表
- 售后服务
- 订单列表
- 发货地址
- 订单评论
- 运费模版
- 2019-05-03 到 2019-06-03
- 拼团列表
- 红包列表
- 限时秒杀
- 优惠卷表
- 基本营销
- 赠礼营销
- 团购管理
- 抽奖管理
- 竞拍管理
- 2019-07-03 到 2019-08-03
- 会员列表
- 大学列表
- 会员等级
- 2019-09-03 到 2019-10-03
- 帮助列表
- 专题列表
- 帮助分类
- 用户举报
- 优选专区
- 专题评论
- 专题分类
- 话题列表
- 话题分类
- 话题评论
- 2019-10-03 到 2019-12-03
- 社区广告
- 社区团购
- 社区公告
- 社区报修
- 费用类型
- 物业公司
- 缴费记录
- 小区列表
- 房间列表
- 楼栋列表
- 业主列表
- 单元列表
- 2020-01-03 到 2020-02-03
- 积分券列表
- 积分赠送规则
- 积分签到规则
- 分销审核
- 分销记录
- 分销关系
- 2020-02-03 到 2020-04-03
- 前台日志
- 后台日志
- 前台接口统计
- 后台接口统计
- 支付宝测试
- 邮件测试
关注公众号
<img src="https://images.gitee.com/uploads/images/2019/0519/174631_65c2a4e8_134431.png" width="80px" height="80px" />



 **_uniapp_** 

uni-app 是一个使用 Vue.js 开发跨平台应用的前端框架，开发者编写一套代码，可编译到iOS、Android、H5、小程序等多个平台。

<img src="https://images.gitee.com/uploads/images/2019/0528/141610_0b812292_134431.jpeg"/>



## 目前h5项目已实现功能
1. 首页数据的展示
2. 分类页数据的展示
3. 购物车
4. 我的
5. 注册
6. 登录
7. 商品详情页
8. 商品搜索
##h5项目效果图


![](https://images.gitee.com/uploads/images/2019/0217/112713_5f032a4c_134431.png)

![](https://images.gitee.com/uploads/images/2019/0217/112713_f4cb24ab_134431.png)

![](https://images.gitee.com/uploads/images/2019/0217/112713_a17c828d_134431.png)

![](https://images.gitee.com/uploads/images/2019/0217/112713_a7afcc52_134431.png)

![](https://images.gitee.com/uploads/images/2019/0217/112713_2d82d3c8_134431.png)

![](https://images.gitee.com/uploads/images/2019/0217/112714_62baf63a_134431.png)

![](https://images.gitee.com/uploads/images/2019/0217/112715_c571472d_134431.png)


## 目前小程序项目已实现功能
1. 首页数据的展示
2. 分类页数据的展示
3. 购物车
4. 我的
5. 注册
6. 登录
7. 商品详情页
8. 商品搜索
9.下单
10.用户详情


## 目前pc项目已实现功能
1. 首页数据的展示
2. 分类页数据的展示
3. 购物车
4. 我的
5. 注册
6. 登录
7. 商品详情页
8. 商品搜索
9.下单
10.用户详情
### 技术选型

#### 后端技术

技术 | 说明 | 官网
----|----|----

Spring Boot | 容器+MVC框架 | [https://spring.io/projects/spring-boot](https://spring.io/projects/spring-boot)
Spring Security | 认证和授权框架 | [https://spring.io/projects/spring-security](https://spring.io/projects/spring-security)
MyBatis | ORM框架  | [http://www.mybatis.org/mybatis-3/zh/index.html](http://www.mybatis.org/mybatis-3/zh/index.html)
MyBatisGenerator | 数据层代码生成 | [http://www.mybatis.org/generator/index.html](http://www.mybatis.org/generator/index.html)
PageHelper | MyBatis物理分页插件 | [http://git.oschina.net/free/Mybatis_PageHelper](http://git.oschina.net/free/Mybatis_PageHelper)
Swagger-UI | 文档生产工具 | [https://github.com/swagger-api/swagger-ui](https://github.com/swagger-api/swagger-ui)
Hibernator-Validator | 验证框架 | [http://hibernate.org/validator/](http://hibernate.org/validator/)
Elasticsearch | 搜索引擎 | [https://github.com/elastic/elasticsearch](https://github.com/elastic/elasticsearch)
RabbitMq | 消息队列 | [https://www.rabbitmq.com/](https://www.rabbitmq.com/)
Redis | 分布式缓存 | [https://redis.io/](https://redis.io/)
MongoDb | NoSql数据库 | [https://www.mongodb.com/](https://www.mongodb.com/)
Docker | 应用容器引擎 | [https://www.docker.com/](https://www.docker.com/)
Druid | 数据库连接池 | [https://github.com/alibaba/druid](https://github.com/alibaba/druid)
OSS | 对象存储 | [https://github.com/aliyun/aliyun-oss-java-sdk](https://github.com/aliyun/aliyun-oss-java-sdk)
JWT | JWT登录支持 | [https://github.com/jwtk/jjwt](https://github.com/jwtk/jjwt)
LogStash | 日志收集 | [https://github.com/logstash/logstash-logback-encoder](https://github.com/logstash/logstash-logback-encoder)
Lombok | 简化对象封装工具 | [https://github.com/rzwitserloot/lombok](https://github.com/rzwitserloot/lombok)

#### 前端技术

技术 | 说明 | 官网
----|----|----
Vue | 前端框架 | [https://vuejs.org/](https://vuejs.org/)
Vue-router | 路由框架 | [https://router.vuejs.org/](https://router.vuejs.org/)
Vuex | 全局状态管理框架 | [https://vuex.vuejs.org/](https://vuex.vuejs.org/)
Element | 前端UI框架 | [https://element.eleme.io/](https://element.eleme.io/)
Axios | 前端HTTP框架 | [https://github.com/axios/axios](https://github.com/axios/axios)
v-charts | 基于Echarts的图表框架 | [https://v-charts.js.org/](https://v-charts.js.org/)
Js-cookie | cookie管理工具 | [https://github.com/js-cookie/js-cookie](https://github.com/js-cookie/js-cookie)
nprogress | 进度条控件 | [https://github.com/rstacruz/nprogress](https://github.com/rstacruz/nprogress)

#### 架构图

##### 系统架构图

![系统架构图](document/resource/mall_system_arch.png)

##### 业务架构图

![系统架构图](document/resource/mall_business_arch.png)

#### 模块介绍

##### 后台管理系统 `mallplus-admin`

- 商品管理：[功能结构图-商品.jpg](document/resource/mind_product.jpg)
- 订单管理：[功能结构图-订单.jpg](document/resource/mind_order.jpg)
- 促销管理：[功能结构图-促销.jpg](document/resource/mind_sale.jpg)
- 内容管理：[功能结构图-内容.jpg](document/resource/mind_content.jpg)
- 用户管理：[功能结构图-用户.jpg](document/resource/mind_member.jpg)

##### 前台商城系统 `mallplus-portal`

[功能结构图-前台.jpg](document/resource/mind_portal.jpg)

#### 开发进度

![项目开发进度图](document/resource/mall_dev_flow.png)

## 环境搭建

### 开发工具

工具 | 说明 | 官网
----|----|----
IDEA | 开发IDE | https://www.jetbrains.com/idea/download
RedisDesktop | redis客户端连接工具 | https://redisdesktop.com/download
Robomongo | mongo客户端连接工具 | https://robomongo.org/download
SwitchHosts| 本地host管理 | https://oldj.github.io/SwitchHosts/
X-shell | Linux远程连接工具 | http://www.netsarang.com/download/software.html
Navicat | 数据库连接工具 | http://www.formysql.com/xiazai.html
PowerDesigner | 数据库设计工具 | http://powerdesigner.de/
Axure | 原型设计工具 | https://www.axure.com/
MindMaster | 思维导图设计工具 | http://www.edrawsoft.cn/mindmaster
ScreenToGif | gif录制工具 | https://www.screentogif.com/
ProcessOn | 流程图绘制工具 | https://www.processon.com/
PicPick | 屏幕取色工具 | https://picpick.app/zh/

### 开发环境

工具 | 版本号 | 下载
----|----|----
JDK | 1.8 | https://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html
Mysql | 5.7 | https://www.mysql.com/
Redis | 3.2 | https://redis.io/download
Elasticsearch | 2.4.6 | https://www.elastic.co/downloads
MongoDb | 3.2 | https://www.mongodb.com/download-center
RabbitMq | 5.25 | http://www.rabbitmq.com/download.html
nginx | 1.10 | http://nginx.org/en/download.html

### 搭建步骤

> 本地环境搭建

- 本地安装开发环境中的所有工具并启动，具体参考
- 安装最新的数据库mallplus.sql，解压 前端vue mallplsu-admin-web.zip
- 克隆源代码到本地，使用IDEA或Eclipse打开，并完成编译;
- 在mysql中新建mall数据库，导入document/sql下的mall.sql文件；
- 启动mallplus-admin项目：直接运行com.zscat.mallplus.MallAdminApplication的main方法即可，
  接口文档地址：http://localhost:8080/swagger-ui.html;
- 启动mall-search项目：直接运行com.zscat.mallplus.search.MallSearchApplication的main方法即可，
  接口文档地址：http://localhost:8081/swagger-ui.html;
- 启动mallplus-portal项目：直接运行com.zscat.mallplus.portal.MallPortalApplication的main方法即可，
  接口文档地址：http://localhost:8085/swagger-ui.html;



## 项目相关文档



## 参考资料

- [Spring实战（第4版）](https://book.douban.com/subject/26767354/)
- [Spring Boot实战](https://book.douban.com/subject/26857423/)
- [Spring Cloud微服务实战](https://book.douban.com/subject/27025912/)
- [Spring Cloud与Docker微服务架构实战](https://book.douban.com/subject/27028228/)
- [Spring Data实战](https://book.douban.com/subject/25975186/)
- [MyBatis从入门到精通](https://book.douban.com/subject/27074809/)
- [深入浅出MySQL](https://book.douban.com/subject/25817684/)
- [循序渐进Linux（第2版）](https://book.douban.com/subject/26758194/)
- [Elasticsearch 技术解析与实战](https://book.douban.com/subject/26967826/)
- [MongoDB实战(第二版)](https://book.douban.com/subject/27061123/)
- [Kubernetes权威指南](https://book.douban.com/subject/26902153/)
- [mall商城](https://github.com/shenzhuan/mallplus)
- [mybatis-plus](https://gitee.com/baomidou/mybatis-plus)


## 许可证

[MIT](https://github.com/zscatzheng/mall/blob/master/LICENSE)

Copyright (c) 2018-2019 zscatzheng


 **- 版权声明** 
- 本项目由北京zscat科技有限公司开发，禁止未经授权用于商业用途。个人学习可免费使用。如需商业授权，请加微信，获取域名授权。
- 本项目由北京zscat科技有限公司开发，禁止未经授权用于商业用途。个人学习可免费使用。如需商业授权，请加微信，获取域名授权。

### 我的微信号

![输入图片说明](https://images.gitee.com/uploads/images/2020/0109/175349_ce8614db_134431.jpeg "流逝.jpeg")

 
