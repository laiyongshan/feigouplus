/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: D:\\LklPospSDKTest\\src\\com\\dc\\pos\\aidl\\AidlDcService.aidl
 */
package gen.com.dc.pos.aidl;
public interface AidlDcService extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements AidlDcService
{
private static final String DESCRIPTOR = "com.dc.pos.aidl.AidlDcService";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.dc.pos.aidl.AidlDcService interface,
 * generating a proxy if needed.
 */
public static AidlDcService asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof AidlDcService))) {
return ((AidlDcService)iin);
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
case TRANSACTION_getDcSystem:
{
data.enforceInterface(DESCRIPTOR);
android.os.IBinder _result = this.getDcSystem();
reply.writeNoException();
reply.writeStrongBinder(_result);
return true;
}
case TRANSACTION_gerDcPrint:
{
data.enforceInterface(DESCRIPTOR);
android.os.IBinder _result = this.gerDcPrint();
reply.writeNoException();
reply.writeStrongBinder(_result);
return true;
}
case TRANSACTION_gerDcTools:
{
data.enforceInterface(DESCRIPTOR);
android.os.IBinder _result = this.gerDcTools();
reply.writeNoException();
reply.writeStrongBinder(_result);
return true;
}
case TRANSACTION_gerDcLed:
{
data.enforceInterface(DESCRIPTOR);
android.os.IBinder _result = this.gerDcLed();
reply.writeNoException();
reply.writeStrongBinder(_result);
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements AidlDcService
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
/** 获取串口测试操作实例  */
@Override public android.os.IBinder getDcSystem() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
android.os.IBinder _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getDcSystem, _data, _reply, 0);
_reply.readException();
_result = _reply.readStrongBinder();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/** 获取打印接口*/
@Override public android.os.IBinder gerDcPrint() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
android.os.IBinder _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_gerDcPrint, _data, _reply, 0);
_reply.readException();
_result = _reply.readStrongBinder();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/** 获取工具接口*/
@Override public android.os.IBinder gerDcTools() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
android.os.IBinder _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_gerDcTools, _data, _reply, 0);
_reply.readException();
_result = _reply.readStrongBinder();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/** 获取LED接口*/
@Override public android.os.IBinder gerDcLed() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
android.os.IBinder _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_gerDcLed, _data, _reply, 0);
_reply.readException();
_result = _reply.readStrongBinder();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
}
static final int TRANSACTION_getDcSystem = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_gerDcPrint = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_gerDcTools = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_gerDcLed = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
}
/** 获取串口测试操作实例  */
public android.os.IBinder getDcSystem() throws android.os.RemoteException;
/** 获取打印接口*/
public android.os.IBinder gerDcPrint() throws android.os.RemoteException;
/** 获取工具接口*/
public android.os.IBinder gerDcTools() throws android.os.RemoteException;
/** 获取LED接口*/
public android.os.IBinder gerDcLed() throws android.os.RemoteException;
}
