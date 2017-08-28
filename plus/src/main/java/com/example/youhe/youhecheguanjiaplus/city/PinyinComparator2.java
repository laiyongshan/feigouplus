package com.example.youhe.youhecheguanjiaplus.city;

import com.example.youhe.youhecheguanjiaplus.bean.CarModel;

import java.util.Comparator;

/**
 * Created by Administrator on 2017/4/6 0006.
 */

public class PinyinComparator2 implements Comparator<CarModel> {
    @Override
    public int compare(CarModel o1, CarModel o2) {
        if (o1.getPinyi().equals("@")
                || o2.getPinyi().equals("#")) {
            return -1;
        } else if (o1.getPinyi().equals("#")
                || o2.getPinyi().equals("@")) {
            return 1;
        } else {
            return o1.getPinyi().compareTo(o2.getPinyi());
        }
    }
}
