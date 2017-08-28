/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: D:\\LklPospSDKTest\\src\\com\\dc\\pos\\aidl\\tools\\AidlTools.aidl
 */
package gen.com.dc.pos.aidl.tools;
//检测工具

public interface AidlTools extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements AidlTools
{
private static final String DESCRIPTOR = "com.dc.pos.aidl.tools.AidlTools";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.dc.pos.aidl.tools.AidlTools interface,
 * generating a proxy if needed.
 */
public static AidlTools asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof AidlTools))) {
return ((AidlTools)iin);
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
case TRANSACTION_DCDeviceTest:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
    gen.com.dc.pos.aidl.tools.AidlToolsListener _arg1;
_arg1 = gen.com.dc.pos.aidl.tools.AidlToolsListener.Stub.asInterface(data.readStrongBinder());
this.DCDeviceTest(_arg0, _arg1);
reply.writeNoException();
return true;
}
case TRANSACTION_DCDeviceTestTamerOn:
{
data.enforceInterface(DESCRIPTOR);
    gen.com.dc.pos.aidl.tools.AidlToolsListener _arg0;
_arg0 = gen.com.dc.pos.aidl.tools.AidlToolsListener.Stub.asInterface(data.readStrongBinder());
this.DCDeviceTestTamerOn(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_DCDeviceTurnOnTamper:
{
data.enforceInterface(DESCRIPTOR);
byte _arg0;
_arg0 = data.readByte();
    gen.com.dc.pos.aidl.tools.AidlToolsListener _arg1;
_arg1 = gen.com.dc.pos.aidl.tools.AidlToolsListener.Stub.asInterface(data.readStrongBinder());
this.DCDeviceTurnOnTamper(_arg0, _arg1);
reply.writeNoException();
return true;
}
case TRANSACTION_DCDeviceQueryTamper:
{
data.enforceInterface(DESCRIPTOR);
    gen.com.dc.pos.aidl.tools.AidlToolsListener _arg0;
_arg0 = gen.com.dc.pos.aidl.tools.AidlToolsListener.Stub.asInterface(data.readStrongBinder());
this.DCDeviceQueryTamper(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_restDeviceTamper:
{
data.enforceInterface(DESCRIPTOR);
this.restDeviceTamper();
reply.writeNoException();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements AidlTools
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
/** 设备测试 */
@Override public void DCDeviceTest(int type, gen.com.dc.pos.aidl.tools.AidlToolsListener listener) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(type);
_data.writeStrongBinder((((listener!=null))?(listener.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_DCDeviceTest, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
/**测试查询现在的触点是什么状况*/
@Override public void DCDeviceTestTamerOn(gen.com.dc.pos.aidl.tools.AidlToolsListener listener) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((listener!=null))?(listener.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_DCDeviceTestTamerOn, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
/**打开安全监测TAMPER*/
@Override public void DCDeviceTurnOnTamper(byte status, gen.com.dc.pos.aidl.tools.AidlToolsListener listener) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeByte(status);
_data.writeStrongBinder((((listener!=null))?(listener.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_DCDeviceTurnOnTamper, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
/**查询安全TAMPER状态*/
@Override public void DCDeviceQueryTamper(gen.com.dc.pos.aidl.tools.AidlToolsListener listener) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((listener!=null))?(listener.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_DCDeviceQueryTamper, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
/***TAMPER启用被触发，重置设备*/
@Override public void restDeviceTamper() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_restDeviceTamper, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_DCDeviceTest = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_DCDeviceTestTamerOn = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_DCDeviceTurnOnTamper = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_DCDeviceQueryTamper = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
static final int TRANSACTION_restDeviceTamper = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
}
/** 设备测试 */
public void DCDeviceTest(int type, gen.com.dc.pos.aidl.tools.AidlToolsListener listener) throws android.os.RemoteException;
/**测试查询现在的触点是什么状况*/
public void DCDeviceTestTamerOn(gen.com.dc.pos.aidl.tools.AidlToolsListener listener) throws android.os.RemoteException;
/**打开安全监测TAMPER*/
public void DCDeviceTurnOnTamper(byte status, gen.com.dc.pos.aidl.tools.AidlToolsListener listener) throws android.os.RemoteException;
/**查询安全TAMPER状态*/
public void DCDeviceQueryTamper(gen.com.dc.pos.aidl.tools.AidlToolsListener listener) throws android.os.RemoteException;
/***TAMPER启用被触发，重置设备*/
public void restDeviceTamper() throws android.os.RemoteException;
}
