package com.dc.pos.aidl.print;
import com.dc.pos.aidl.print.AidlDcPrinterListener;
import com.dc.pos.aidl.print.DcPrintItemObj;
interface AidlDcPrinter{
    /** 获取打印机状态 */
    int getPrinterState();
    
    /** 打印文本数据 */
    void printText(in List<DcPrintItemObj> data,AidlDcPrinterListener listener);
    
    /** 打印位图 */
    void printBmp(int leftoffset,int width,int height, in Bitmap picture,AidlDcPrinterListener listener);

    /** 打印条码  */
    
    
    /**
	 * 
	 * 打印条形码
	 * 
	 * @param width
	 *              2≤n≤5，默认值为n = 3，可用-1指定默认值
					用 n 来指定条码的横向模块宽度。
					条码UPC-A、JAN13 (EAN13)设置宽度为4或5时，将自动变为3。
					条码JAN8 (EAN8)设置宽度为5时，将自动变为4。
					超出范围时,本次设置无效,与上一次设置相同.
	 * 
	 * @param height
	 *              该命令用来设置打印条码的高度为(n×0.125毫米)。
					0≤n≤255，默认值为n=162，可用-1指定默认值
	 * 
	 * @param leftoffset
	 *              UPCA、UPCE、EAN8、EAN13默认左边距为 9 * 单元宽度，最小左边距为9 * 单元宽度
					ITF、CODEBAR、CODE39、CODE93、CODE128默认左边距为5点
					设置左边距时，对所有9种条码均有效，即9种条码共用一个左边距
					打印UPCA、UPCE、EAN8、EAN13四种条码时，若设置的左边距小于最小左边距，按最小左边距打印；若设置的左边距使得条码不能完全打印，则按条码可以完全打印条件下的最大左边距打印。
					打印ITF、CODEBAR、CODE39、CODE93、CODE128五种条码时，设置左边距后，最大可打印的字符数根据实际情况发生变化。
					条码提示与条码符号具有相同的左边距（在此左边距的基础上，提示进行居中）
					
	 * @param barcodetype 
	 *              可传本接口定义的常量值
     *              public final static int BARCODE_TYPE_UPCA = 65;

					public final static int BARCODE_TYPE_UPCE = 66;
					
					public final static int BARCODE_TYPE_JAN13 = 67;
				
					public final static int BARCODE_TYPE_JAN8 = 68;
				
					public final static int BARCODE_TYPE_CODE39 = 69;
				
					public final static int BARCODE_TYPE_ITF = 70;
				
					public final static int BARCODE_TYPE_CODEBAR = 71;
				
					public final static int BARCODE_TYPE_CODE93 = 72;
					
					public final static int BARCODE_TYPE_CODE128 = 73;
	 * 
	 * @param barcode
	 *            条码内容
	 */
    void printBarCode(int width,int height,int leftoffset,int barcodetype,in String barcode,AidlDcPrinterListener listener);
	//打印灰度设置
	void setPrinterGray(int gray);	
	//打印全黑
	void printMode(int type, int printHeight, int repeat, AidlDcPrinterListener listener);
	//获取打印机芯温度AD值
	void getPrinterTemp(AidlDcPrinterListener listener);
}