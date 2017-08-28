package com.dc.pos.aidl.print;
interface AidlDcPrinterListener{
    /**
               打印出错
    */
	void onError(int errorCode, String msg);
	
	/**
	 打印结束
	*/
	void onPrintFinish(int errorCode, String msg);
}