package com.dc.pos.aidl.system;
//APP安装监听器
interface CommTestObserver{
	//成功
	void onTestFinished();
	//错误
	void onTestError(int errorCode);
}