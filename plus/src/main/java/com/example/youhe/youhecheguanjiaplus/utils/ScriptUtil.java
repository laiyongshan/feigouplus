package com.example.youhe.youhecheguanjiaplus.utils;


import android.app.Activity;
import android.util.Log;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

/**
 * Created by Administrator on 2017/6/12.
 * JS与Java交互
 */

public class ScriptUtil {


    /*java调用js函数
    * @param js js代码
	 * @param functionName js方法名称
	 * @param functionParams js方法参数
	 * @return
    * */
    public static Object runScript(Activity activity, String js, String functionName, Object[] functionParams){
        Context rhino = Context.enter();
        rhino.setOptimizationLevel(-1);
        try {
            Scriptable scope = rhino.initStandardObjects();

            ScriptableObject.putProperty(scope, "javaContext", Context.javaToJS(activity, scope));
            ScriptableObject.putProperty(scope, "javaLoader", Context.javaToJS(activity.getClass().getClassLoader(), scope));

            rhino.evaluateString(scope, js, activity.getLocalClassName(), 1, null);

            Function function = (Function) scope.get(functionName, scope);

            Object result = function.call(rhino, scope, scope, functionParams);
            if (result instanceof String) {
                return  result;
            } else if (result instanceof NativeJavaObject) {
                return  ((NativeJavaObject) result).getDefaultValue(String.class);
            } else if (result instanceof NativeObject) {
                return  ((NativeObject) result).getDefaultValue(String.class);
            }
            return result;//(String) function.call(rhino, scope, scope, functionParams);
        }finally {
            Context.exit();
        }
    }

}
