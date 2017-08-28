/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: D:\\LklPospSDKTest\\src\\com\\dc\\pos\\aidl\\print\\AidlDcPrinter.aidl
 */
package gen.com.dc.pos.aidl.print;
public interface AidlDcPrinter extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements AidlDcPrinter
{
private static final String DESCRIPTOR = "com.dc.pos.aidl.print.AidlDcPrinter";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.dc.pos.aidl.print.AidlDcPrinter interface,
 * generating a proxy if needed.
 */
public static AidlDcPrinter asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof AidlDcPrinter))) {
return ((AidlDcPrinter)iin);
}
return new Proxy(obj);
}
@Override public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
case TRANSACTION_getPrinterState:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.getPrinterState();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_printText:
{
data.enforceInterface(DESCRIPTOR);
java.util.List<com.aidl.print.DcPrintItemObj> _arg0;
_arg0 = data.createTypedArrayList(com.aidl.print.DcPrintItemObj.CREATOR);
	gen.com.dc.pos.aidl.print.AidlDcPrinterListener _arg1;
_arg1 = gen.com.dc.pos.aidl.print.AidlDcPrinterListener.Stub.asInterface(data.readStrongBinder());
this.printText(_arg0, _arg1);
reply.writeNoException();
return true;
}
case TRANSACTION_printBmp:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _arg1;
_arg1 = data.readInt();
int _arg2;
_arg2 = data.readInt();
android.graphics.Bitmap _arg3;
if ((0!=data.readInt())) {
_arg3 = android.graphics.Bitmap.CREATOR.createFromParcel(data);
}
else {
_arg3 = null;
}
	gen.com.dc.pos.aidl.print.AidlDcPrinterListener _arg4;
_arg4 = gen.com.dc.pos.aidl.print.AidlDcPrinterListener.Stub.asInterface(data.readStrongBinder());
this.printBmp(_arg0, _arg1, _arg2, _arg3, _arg4);
reply.writeNoException();
return true;
}
case TRANSACTION_printBarCode:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _arg1;
_arg1 = data.readInt();
int _arg2;
_arg2 = data.readInt();
int _arg3;
_arg3 = data.readInt();
String _arg4;
_arg4 = data.readString();
	gen.com.dc.pos.aidl.print.AidlDcPrinterListener _arg5;
_arg5 = gen.com.dc.pos.aidl.print.AidlDcPrinterListener.Stub.asInterface(data.readStrongBinder());
this.printBarCode(_arg0, _arg1, _arg2, _arg3, _arg4, _arg5);
reply.writeNoException();
return true;
}
case TRANSACTION_setPrinterGray:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.setPrinterGray(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_printMode:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _arg1;
_arg1 = data.readInt();
int _arg2;
_arg2 = data.readInt();
	gen.com.dc.pos.aidl.print.AidlDcPrinterListener _arg3;
_arg3 = gen.com.dc.pos.aidl.print.AidlDcPrinterListener.Stub.asInterface(data.readStrongBinder());
this.printMode(_arg0, _arg1, _arg2, _arg3);
reply.writeNoException();
return true;
}
case TRANSACTION_getPrinterTemp:
{
data.enforceInterface(DESCRIPTOR);
	gen.com.dc.pos.aidl.print.AidlDcPrinterListener _arg0;
_arg0 = gen.com.dc.pos.aidl.print.AidlDcPrinterListener.Stub.asInterface(data.readStrongBinder());
this.getPrinterTemp(_arg0);
reply.writeNoException();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements AidlDcPrinter
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
@Override public android.os.IBinder asBinder()
{
return mRemote;
}
public String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
/** 获取打印机状态 */
@Override public int getPrinterState() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getPrinterState, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/** 打印文本数据 */
@Override public void printText(java.util.List<com.aidl.print.DcPrintItemObj> data, gen.com.dc.pos.aidl.print.AidlDcPrinterListener listener) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeTypedList(data);
_data.writeStrongBinder((((listener!=null))?(listener.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_printText, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
/** 打印位图 */
@Override public void printBmp(int leftoffset, int width, int height, android.graphics.Bitmap picture, gen.com.dc.pos.aidl.print.AidlDcPrinterListener listener) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(leftoffset);
_data.writeInt(width);
_data.writeInt(height);
if ((picture!=null)) {
_data.writeInt(1);
picture.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
_data.writeStrongBinder((((listener!=null))?(listener.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_printBmp, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
/** 打印条码  *//**
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
@Override public void printBarCode(int width, int height, int leftoffset, int barcodetype, String barcode, gen.com.dc.pos.aidl.print.AidlDcPrinterListener listener) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(width);
_data.writeInt(height);
_data.writeInt(leftoffset);
_data.writeInt(barcodetype);
_data.writeString(barcode);
_data.writeStrongBinder((((listener!=null))?(listener.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_printBarCode, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
//打印灰度设置

@Override public void setPrinterGray(int gray) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(gray);
mRemote.transact(Stub.TRANSACTION_setPrinterGray, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
//打印全黑

@Override public void printMode(int type, int printHeight, int repeat, gen.com.dc.pos.aidl.print.AidlDcPrinterListener listener) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(type);
_data.writeInt(printHeight);
_data.writeInt(repeat);
_data.writeStrongBinder((((listener!=null))?(listener.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_printMode, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
//获取打印机芯温度AD值

@Override public void getPrinterTemp(gen.com.dc.pos.aidl.print.AidlDcPrinterListener listener) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((listener!=null))?(listener.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_getPrinterTemp, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_getPrinterState = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_printText = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_printBmp = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_printBarCode = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
static final int TRANSACTION_setPrinterGray = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
static final int TRANSACTION_printMode = (android.os.IBinder.FIRST_CALL_TRANSACTION + 5);
static final int TRANSACTION_getPrinterTemp = (android.os.IBinder.FIRST_CALL_TRANSACTION + 6);
}
/** 获取打印机状态 */
public int getPrinterState() throws android.os.RemoteException;
/** 打印文本数据 */
public void printText(java.util.List<com.aidl.print.DcPrintItemObj> data, gen.com.dc.pos.aidl.print.AidlDcPrinterListener listener) throws android.os.RemoteException;
/** 打印位图 */
public void printBmp(int leftoffset, int width, int height, android.graphics.Bitmap picture, gen.com.dc.pos.aidl.print.AidlDcPrinterListener listener) throws android.os.RemoteException;
/** 打印条码  *//**
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
public void printBarCode(int width, int height, int leftoffset, int barcodetype, String barcode, gen.com.dc.pos.aidl.print.AidlDcPrinterListener listener) throws android.os.RemoteException;
//打印灰度设置

public void setPrinterGray(int gray) throws android.os.RemoteException;
//打印全黑

public void printMode(int type, int printHeight, int repeat, gen.com.dc.pos.aidl.print.AidlDcPrinterListener listener) throws android.os.RemoteException;
//获取打印机芯温度AD值

public void getPrinterTemp(gen.com.dc.pos.aidl.print.AidlDcPrinterListener listener) throws android.os.RemoteException;
}
