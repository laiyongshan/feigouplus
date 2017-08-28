package com.example.youhe.youhecheguanjiaplus.bean;


import com.example.youhe.youhecheguanjiaplus.https.URLs;

/**
 * Created by Administrator on 2017/2/23 0023.
 */

public class Get122Url {
    public static String url122;

    public static String get122url(String prifix){
        switch (prifix){
            case "bj122":
                url122="北京";
                break;
            case "gd122":
                url122="广东";
                break;
            case "xj122":
                url122="新疆";
                break;
            case "xz122":
                url122="西藏";
                break;
            case "qh122":
                url122="青海";
                break;
            case "gs122":
                url122="甘肃";
                break;
            case "nm122":
                url122="内蒙古";
                break;
            case "nx122":
                url122="宁夏";
                break;
            case "sc122":
                url122="四川";
                break;
            case "yn122":
                url122="云南";
                break;
            case "sn122":
                url122="陕西";
                break;
            case "cq122":
                url122="重庆";
                break;
            case "gz122":
                url122="贵州";
                break;
            case "gx122":
                url122="广西";
                break;
            case "sx122":
                url122="山西";
                break;
            case "ha122":
                url122="河南";
                break;
            case "hb122":
                url122="湖北";
                break;
            case "hn122":
                url122="湖南";
                break;
            case "hi122":
                url122="海南";
                break;
            case "he122":
                url122="河北";
                break;
            case "tj122":
                url122="天津";
                break;
            case "sd122":
                url122="山东";
                break;
            case "ah122":
                url122="安徽";
                break;
            case "jx122":
                url122="江西";
                break;
            case "sh122":
                url122="上海";
                break;
            case "fj122":
                url122="福建";
                break;
            case "hl122":
                url122="黑龙江";
                break;
            case "jl122":
                url122="吉林";
                break;
            case "ln122":
                url122="辽宁";
                break;

            case "苏":
                url122="";
                break;

            case "浙":
                url122="";
                break;

            default:
                url122="";
                break;

        }

        return URLs.HTTPS+url122+URLs.REGISTER_122_URL;
    }
}
