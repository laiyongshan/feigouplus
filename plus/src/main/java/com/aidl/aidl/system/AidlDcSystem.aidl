package com.dc.pos.aidl.system;
import com.dc.pos.aidl.system.CommTestObserver;
//系统设备
interface AidlDcSystem{
/** 设置设备参数 */
	boolean SetDeviceParams(in Bundle bundle);
	/** 设置字库 */
	boolean SetFont(in int type, in byte[] data);
	/** 验证字库 */
	boolean verifyFont(in int type, in byte[] address, in int len, out byte[] info);
	/** 恢复出厂设置 */
	boolean restoreFactorySettings();
	//结束测试
	void endCommTest();
	//开始测试
	void startCommTest(int type,CommTestObserver observer);
	//读取密钥
	String getSecretKey();
}