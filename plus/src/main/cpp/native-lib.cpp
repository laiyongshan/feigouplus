#include <jni.h>
#include <string>
#include "Md5.h"
#include "cstring"
#include <sstream>
#include <stdlib.h>


extern "C"{
__attribute((visibility("default")))
JNIEXPORT jint JNI_OnLoad(JavaVM *vm, void *reserved){//这是JNI_OnLoad的声明，必须按照这样的方式声明
    return JNI_VERSION_1_4;//这里很重要，必须返回版本，否则加载会失败。
}
__attribute((visibility("default")))
JNIEXPORT void JNICALL JNI_OnUnload(JavaVM *vm, void *reserved){

}
}

extern "C"
jstring
Java_com_example_youhe_youhecheguanjiaplus_utils_ParamSign_stringFromJNI(
        JNIEnv* env,
        jobject /* this */) {
    //MD5 md5;

    std::string appkey = md5("HDvxBxoID2CiJNc3psEoVyAc");
    std::string key=appkey.substr(8,16);
    return env->NewStringUTF("hello");
}

/**
 * 用户密码 签名加密
 */
extern "C"
jstring
Java_com_example_youhe_youhecheguanjiaplus_utils_ParamSign_getUserPasswordFromJNI(
        JNIEnv* env,
        jobject obj,
        jstring jstr) {
//    std::string appkey = md5("12345678912345678912345678912312");
    std::string appkey = md5("rldstwvhral33brqz9ypetwe");
    std::string key=appkey.substr(8,16);

    return env->NewStringUTF(key.c_str());
}

/**
 * url 签名加密
 */
extern "C"
jstring
Java_com_example_youhe_youhecheguanjiaplus_utils_ParamSign_getFromJNI(
        JNIEnv* env,
        jobject obj,
        jstring jstr) {

//    std::string appkey = md5("12345678912345678912345678912312");
    std::string appkey = md5("rldstwvhral33brqz9ypetwe");
    std::string key=appkey.substr(8,16);

    jboolean isCopy;    // 返回JNI_TRUE表示原字符串的拷贝，返回JNI_FALSE表示返回原字符串的指针
    std::string  c_str = env->GetStringUTFChars(jstr, &isCopy);

    std::string a=md5(key+c_str);

    return env->NewStringUTF(a.c_str());
}



/**
 * DES加密算法
 */

extern "C"
jstring
Java_com_example_youhe_youhecheguanjiaplus_utils_ParamSign_encryptDES(JNIEnv *env, jobject instance, jstring msg_) {

    return env->NewStringUTF("");
}

std::string* byteToHexStr(unsigned char byte_arr[], int arr_len)
{
    std::string* hexstr=new std::string();
    for (int i=0;i<arr_len;i++)
    {
        char hex1;
        char hex2;
        /*借助C++支持的unsigned和int的强制转换，把unsigned char赋值给int的值，那么系统就会自动完成强制转换*/
        int value=byte_arr[i];
        int S=value/16;
        int Y=value % 16;
        //将C++中unsigned char和int的强制转换得到的商转成字母
        if (S>=0&&S<=9)
            hex1=(char)(48+S);
        else
            hex1=(char)(55+S);
        //将C++中unsigned char和int的强制转换得到的余数转成字母
        if (Y>=0&&Y<=9)
            hex2=(char)(48+Y);
        else
            hex2=(char)(55+Y);
        //最后一步的代码实现，将所得到的两个字母连接成字符串达到目的
        *hexstr=*hexstr+hex1+hex2;
    }
    return hexstr;

}

/**
 *
 * DES解密算法
 */
extern "C"
jstring
Java_com_example_youhe_youhecheguanjiaplus_utils_ParamSign_decryptDES(JNIEnv *env, jobject instance, jstring msg_) {
    const char *msg = env->GetStringUTFChars(msg_, 0);


}