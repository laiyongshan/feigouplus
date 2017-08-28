package com.example.youhe.youhecheguanjiaplus.logic;

/*
 * 网络请求任务类型
 * */

public class TaskType {

    public static final int NETWORK_ERROE = 0; //网络请求错误

    public static final int TS_YHCGJ_HOMG_PAGE_INIT = 1;//首页界面的初始化

    public static final int TS_REFLUSH_HOME_PAGE = 2;//刷新首页数据

    public static final int TS_USER_LOGIN = 3;//用户登录

    public static final int TS_USER_EXIT = 4;//用户退出

    public static final int TS_QUERYINDEX = 8;//违章查询任务

    public static final int TS_REFLUSH_VIALATION = 9;//刷新首页车辆信息中的违章信息的界面

    public static final int TS_QUERY_ORDER_RULES = 10;//获取车行易端的查询并下单的最低限条件

    public static final int TS_CAR_OPEN_CITYS=11;//获取本人本车开放城市

    public static final int TS_OTHER_ORDER_PROVICE=12;//获取其他订单省份

    public static final int TS_INIT_HOMEPAGE=13;//初始化首页显示数据

    public static final int TS_TO_XINGE_TOKEN=14;//提交信鸽Token

    public static final int TS_TO_MAINFRAGMENT=15;//跳转到主fragment

    public static final int TS_TO_ORDERFRAGMENT=16;//跳转到订单fragment

    public static final int TS_GET_START_IMG=17;//获取启动页广告图片


    public static final int TS_GET_BANNER=18;//获取首页banner图片

    public static final int TS_REAL_NAME=19;//隐藏实名认证按钮

    public static final int TS_GET_YU_E=20;//获取余额

    public static final int TS_GET_NOTICE_INFO=21;//获取首页公告栏

    public static final int TS_GET_OPEN_PROVINCE=22;//获取快速查询开放省份

    public static final int TS_GET_PLUS_DISTRIBUTION_INVICODE=23;//获取分销用户的邀请码

}
