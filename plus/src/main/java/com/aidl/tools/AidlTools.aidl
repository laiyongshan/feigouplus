package com.dc.pos.aidl.tools;

import com.dc.pos.aidl.tools.AidlToolsListener;

//检测工具
interface AidlTools{
	/** 设备测试 */
	void DCDeviceTest(int type,AidlToolsListener listener);
	
	/**测试查询现在的触点是什么状况**/
	void DCDeviceTestTamerOn(AidlToolsListener listener);
	/**打开安全监测TAMPER**/
	void DCDeviceTurnOnTamper(byte status,AidlToolsListener listener);
	/**查询安全TAMPER状态**/
	void DCDeviceQueryTamper(AidlToolsListener listener);
	/***TAMPER启用被触发，重置设备****/
	void restDeviceTamper();
}
