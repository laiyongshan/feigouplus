/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: D:\\LklPospSDKTest\\src\\com\\dc\\pos\\aidl\\print\\AidlDcPrinterListener.aidl
 */
package gen.com.dc.pos.aidl.print;
public interface AidlDcPrinterListener extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements AidlDcPrinterListener
{
private static final String DESCRIPTOR = "com.dc.pos.aidl.print.AidlDcPrinterListener";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.dc.pos.aidl.print.AidlDcPrinterListener interface,
 * generating a proxy if needed.
 */
public static AidlDcPrinterListener asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof AidlDcPrinterListener))) {
return ((AidlDcPrinterListener)iin);
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
case TRANSACTION_onError:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
String _arg1;
_arg1 = data.readString();
this.onError(_arg0, _arg1);
reply.writeNoException();
return true;
}
case TRANSACTION_onPrintFinish:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
String _arg1;
_arg1 = data.readString();
this.onPrintFinish(_arg0, _arg1);
reply.writeNoException();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements AidlDcPrinterListener
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
/**
               打印出错
    */
@Override public void onError(int errorCode, String msg) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(errorCode);
_data.writeString(msg);
mRemote.transact(Stub.TRANSACTION_onError, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
/**
	 打印结束
	*/
@Override public void onPrintFinish(int errorCode, String msg) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(errorCode);
_data.writeString(msg);
mRemote.transact(Stub.TRANSACTION_onPrintFinish, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_onError = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_onPrintFinish = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
}
/**
               打印出错
    */
public void onError(int errorCode, String msg) throws android.os.RemoteException;
/**
	 打印结束
	*/
public void onPrintFinish(int errorCode, String msg) throws android.os.RemoteException;
}
