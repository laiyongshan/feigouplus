package com.dc.pos.aidl;

interface AidlDcService{

	/** 获取串口测试操作实例  */
	IBinder getDcSystem();

	/** 获取打印接口*/
	IBinder gerDcPrint();
	
	/** 获取工具接口*/
	IBinder gerDcTools();
	
	/** 获取LED接口*/
	IBinder gerDcLed();
}