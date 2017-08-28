package com.example.youhe.youhecheguanjiaplus.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

import com.example.youhe.youhecheguanjiaplus.app.AppContext;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Set;


public class SharedPreferencesUtils {
    public static final String APPInfo = "APPInfo";
    public static final String LGISP = "LGISP";//只是为了方便，为了防止重复，建议用新的或者检验存值的KEY
    private static Context mContext=AppContext.getContext();

    public static void saveBean(Context context, Object entity) {
        saveBean(context, entity, entity.getClass().getName());
    }

    public static void saveBean(Context context, Object entity, String tb_name) {
        SharedPreferences preferences = context.getSharedPreferences(tb_name, Context.MODE_PRIVATE);
        Editor editor = preferences.edit();
        try {
            parsClass(editor, entity, "");
            editor.commit();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }

    }

    public static <E> void saveList(Context context, Collection<E> collection, String tb_name) {

        SharedPreferences preferences = context.getSharedPreferences(tb_name, Context.MODE_PRIVATE);
        Editor editor = preferences.edit();
        try {
            int i = 0;
            for (E e : collection) {
                parsClass(editor, e, (i++) + "");

            }
            editor.putInt("len", collection.size());
            editor.commit();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    public static <T> Collection<T> getList(Context context, Class<T> t, String tb_name) throws IllegalAccessException, IllegalArgumentException, InstantiationException {
        SharedPreferences preferences = context.getSharedPreferences(tb_name, Context.MODE_PRIVATE);
        int len = preferences.getInt("len", 0);
        Collection<T> collection = new LinkedList<T>();
        if (len < 0)
            return collection;

        for (int i = 0; i < len; i++) {

            collection.add(getBean(context, t, tb_name, i + ""));
        }
        return collection;
    }

    public static <T> T getBean(Context context, Class<T> t) throws IllegalAccessException, IllegalArgumentException, InstantiationException {
        return getBean(context, t, t.getClass().getSimpleName(), "");
    }

    public static <T> T getBean(Context context, Class<T> t, String tb_name) throws IllegalAccessException, IllegalArgumentException, InstantiationException {
        return getBean(context, t, tb_name, "");
    }

    public static <T> T getBean(Context context, Class<T> c, String tb_name, String flag) throws IllegalAccessException, IllegalArgumentException, InstantiationException {
        SharedPreferences preferences = context.getSharedPreferences(tb_name, Context.MODE_PRIVATE);

        Field[] fields = c.getDeclaredFields();
        T t = c.newInstance();
        for (Field field : fields) {
            field.setAccessible(true);
            if (field.getType().equals(int.class) || field.getType().equals(Integer.class)) {
                field.setInt(t, preferences.getInt(field.getName() + flag, 0));
            } else if (field.getType().equals(boolean.class) || field.getType().equals(Boolean.class)) {
                field.setBoolean(t, preferences.getBoolean(field.getName() + flag, false));
            } else if (field.getType().equals(char.class) || field.getType().equals(Character.class)) {
                field.setChar(t, preferences.getString(field.getName() + flag, " ").charAt(0));
            } else if (field.getType().equals(float.class) || field.getType().equals(Float.class)) {
                field.setFloat(t, preferences.getFloat(field.getName() + flag, 0f));
            } else if (field.getType().equals(long.class) || field.getType().equals(Long.class)) {
                field.setLong(t, preferences.getLong(field.getName() + flag, 0));
            } else if (field.getType().equals(String.class)) {
                field.set(t, preferences.getString(field.getName() + flag, ""));
            } else if (field.getType().equals(Set.class)) {
                field.set(t, preferences.getStringSet(field.getName() + flag, new LinkedHashSet<String>()));
            } else {
                //  throw new IllegalAccessError("无法识别的类型");
            }
        }
        return t;

    }

    public static void parsClass(Editor editor, Object object, String flag) throws IllegalAccessException, IllegalArgumentException {
        Field[] fields = object.getClass().getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true);

            if (field.getType().equals(int.class) || field.getType().equals(Integer.class)) {
                editor.putInt(field.getName() + flag, field.getInt(object));
            } else if (field.getType().equals(boolean.class) || field.getType().equals(Boolean.class)) {
                editor.putBoolean(field.getName() + flag, field.getBoolean(object));
            } else if (field.getType().equals(char.class) || field.getType().equals(Character.class)) {
                editor.putString(field.getName() + flag, new String(new char[]{field.getChar(object)}));
            } else if (field.getType().equals(float.class) || field.getType().equals(Float.class)) {
                editor.putFloat(field.getName() + flag, field.getFloat(object));
            } else if (field.getType().equals(long.class) || field.getType().equals(Long.class)) {
                editor.putLong(field.getName() + flag, field.getLong(object));
            } else if (field.getType().equals(String.class)) {
                editor.putString(field.getName() + flag, (String) field.get(object));
            } else {
                //  throw new IllegalAccessError("无法识别的类型" + field.getType() + field.getName());
            }
        }
    }

    /**
     * 获取Preference设置
     */
    @Deprecated
    public static SharedPreferences getSharedPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    /**
     * 获取Preference设置
     */
    public static SharedPreferences getSharedPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(mContext);
    }

    /**
     * 获取Preference设置
     */
    public static SharedPreferences getSharedPreferences(String sharedFile) {
        return mContext.getSharedPreferences(sharedFile, Context.MODE_PRIVATE);
    }

    /**
     * 获取Preference设置
     *
     * @param context
     * @param sharedFile
     * @return
     */
    public static SharedPreferences getSharedPreferences(Context context, String sharedFile) {
        return context.getSharedPreferences(sharedFile, Context.MODE_PRIVATE);
    }

    /**
     * 获取Preference设置
     */
    public static SharedPreferences getSharedPreferencesLGI() {
        return mContext.getSharedPreferences(LGISP, Context.MODE_PRIVATE);
    }

    /**
     * 使用这个方法获取时请确保KEY的唯一性
     */
    public static void setSharedPreferencesLGI(String configKey, Object value) {
        Editor editor;
        if (StringUtils.isEmpty(LGISP))
            editor = getSharedPreferences(mContext).edit();
        else
            editor = getSharedPreferences(mContext, LGISP).edit();
        if (value instanceof Boolean) {
            editor.putBoolean(configKey, (Boolean) value);
        } else if (value instanceof Integer) {
            editor.putInt(configKey, (Integer) value);
        } else if (value instanceof String) {
            editor.putString(configKey, (String) value);
        } else if (value instanceof Float) {
            editor.putFloat(configKey, (Float) value);
        } else if (value instanceof Long) {
            editor.putLong(configKey, (Long) value);
        } else if (value instanceof Set<?>) {
            editor.putStringSet(configKey, (Set<String>) value);
        }
        editor.commit();
    }

    /**
     * 设置Preference值
     *
     * @param context
     * @param configKey
     * @param value
     */
    public static void setSharedPreferences(Context context, String configKey, Object value) {
        setSharedPreferences(context, null, configKey, value);
    }


    public static void setSharedPreferences(Context context, String sharedFile, String configKey, Object value) {
        Editor editor;
        if (StringUtils.isEmpty(sharedFile))
            editor = getSharedPreferences(context).edit();
        else
            editor = getSharedPreferences(context, sharedFile).edit();
        if (value instanceof Boolean) {
            editor.putBoolean(configKey, (Boolean) value);
        } else if (value instanceof Integer) {
            editor.putInt(configKey, (Integer) value);
        } else if (value instanceof String) {
            editor.putString(configKey, (String) value);
        } else if (value instanceof Float) {
            editor.putFloat(configKey, (Float) value);
        } else if (value instanceof Long) {
            editor.putLong(configKey, (Long) value);
        } else if (value instanceof Set<?>) {
            editor.putStringSet(configKey, (Set<String>) value);
        }
        editor.commit();
    }


}
