package com.example.youhe.youhecheguanjiaplus.city;

import java.util.Comparator;

/**
 * 
 */
public class PinyinComparator implements Comparator<City> {

	public int compare(City o1, City o2) {
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
