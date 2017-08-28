/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: D:\\LklPospSDKTest\\src\\com\\dc\\pos\\aidl\\system\\AidlDcSystem.aidl
 */
package gen.com.dc.pos.aidl.system;
//系统设备

public interface AidlDcSystem extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements AidlDcSystem
{
private static final String DESCRIPTOR = "com.dc.pos.aidl.system.AidlDcSystem";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.dc.pos.aidl.system.AidlDcSystem interface,
 * generating a proxy if needed.
 */
public static AidlDcSystem asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof AidlDcSystem))) {
return ((AidlDcSystem)iin);
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
case TRANSACTION_SetDeviceParams:
{
data.enforceInterface(DESCRIPTOR);
android.os.Bundle _arg0;
if ((0!=data.readInt())) {
_arg0 = android.os.Bundle.CREATOR.createFromParcel(data);
}
else {
_arg0 = null;
}
boolean _result = this.SetDeviceParams(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_SetFont:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
byte[] _arg1;
_arg1 = data.createByteArray();
boolean _result = this.SetFont(_arg0, _arg1);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_verifyFont:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
byte[] _arg1;
_arg1 = data.createByteArray();
int _arg2;
_arg2 = data.readInt();
byte[] _arg3;
int _arg3_length = data.readInt();
if ((_arg3_length<0)) {
_arg3 = null;
}
else {
_arg3 = new byte[_arg3_length];
}
boolean _result = this.verifyFont(_arg0, _arg1, _arg2, _arg3);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
reply.writeByteArray(_arg3);
return true;
}
case TRANSACTION_restoreFactorySettings:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.restoreFactorySettings();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_endCommTest:
{
data.enforceInterface(DESCRIPTOR);
this.endCommTest();
reply.writeNoException();
return true;
}
case TRANSACTION_startCommTest:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
    gen.com.dc.pos.aidl.system.CommTestObserver _arg1;
_arg1 = gen.com.dc.pos.aidl.system.CommTestObserver.Stub.asInterface(data.readStrongBinder());
this.startCommTest(_arg0, _arg1);
reply.writeNoException();
return true;
}
case TRANSACTION_getSecretKey:
{
data.enforceInterface(DESCRIPTOR);
String _result = this.getSecretKey();
reply.writeNoException();
reply.writeString(_result);
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements AidlDcSystem
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
/** 设置设备参数 */
@Override public boolean SetDeviceParams(android.os.Bundle bundle) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
if ((bundle!=null)) {
_data.writeInt(1);
bundle.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
mRemote.transact(Stub.TRANSACTION_SetDeviceParams, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/** 设置字库 */
@Override public boolean SetFont(int type, byte[] data) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(type);
_data.writeByteArray(data);
mRemote.transact(Stub.TRANSACTION_SetFont, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/** 验证字库 */
@Override public boolean verifyFont(int type, byte[] address, int len, byte[] info) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(type);
_data.writeByteArray(address);
_data.writeInt(len);
if ((info==null)) {
_data.writeInt(-1);
}
else {
_data.writeInt(info.length);
}
mRemote.transact(Stub.TRANSACTION_verifyFont, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
_reply.readByteArray(info);
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/** 恢复出厂设置 */
@Override public boolean restoreFactorySettings() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_restoreFactorySettings, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
//结束测试

@Override public void endCommTest() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_endCommTest, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
//开始测试

@Override public void startCommTest(int type, gen.com.dc.pos.aidl.system.CommTestObserver observer) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(type);
_data.writeStrongBinder((((observer!=null))?(observer.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_startCommTest, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
//读取密钥

@Override public String getSecretKey() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
String _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getSecretKey, _data, _reply, 0);
_reply.readException();
_result = _reply.readString();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
}
static final int TRANSACTION_SetDeviceParams = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_SetFont = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_verifyFont = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_restoreFactorySettings = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
static final int TRANSACTION_endCommTest = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
static final int TRANSACTION_startCommTest = (android.os.IBinder.FIRST_CALL_TRANSACTION + 5);
static final int TRANSACTION_getSecretKey = (android.os.IBinder.FIRST_CALL_TRANSACTION + 6);
}
/** 设置设备参数 */
public boolean SetDeviceParams(android.os.Bundle bundle) throws android.os.RemoteException;
/** 设置字库 */
public boolean SetFont(int type, byte[] data) throws android.os.RemoteException;
/** 验证字库 */
public boolean verifyFont(int type, byte[] address, int len, byte[] info) throws android.os.RemoteException;
/** 恢复出厂设置 */
public boolean restoreFactorySettings() throws android.os.RemoteException;
//结束测试

public void endCommTest() throws android.os.RemoteException;
//开始测试

public void startCommTest(int type, gen.com.dc.pos.aidl.system.CommTestObserver observer) throws android.os.RemoteException;
//读取密钥

public String getSecretKey() throws android.os.RemoteException;
}
