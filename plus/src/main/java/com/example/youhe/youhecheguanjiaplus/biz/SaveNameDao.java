package com.example.youhe.youhecheguanjiaplus.biz;

import android.app.Activity;
import android.content.Context;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * Created by Administrator on 2016/9/10 0010.
 * 保存editTextde文本
 */
public class SaveNameDao {

    private Context activity;

    public SaveNameDao(Activity activity) {

        this.activity = activity;

    }
    public SaveNameDao(Context activity) {

        this.activity = activity;

    }

    /**
     * 将字符串写入到文本文件中
     * str
     */
    public void writeTxtToFile(String str, String filename) {
        FileOutputStream out;
        try {
            out = activity.openFileOutput(filename, Activity.MODE_PRIVATE);
            out.write(str.getBytes());
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * |
     * 读取本地文本
     */
    public String readText(String flieName) {
        String text = "";

        try {
            FileInputStream is = activity.openFileInput(flieName);
            byte[] b = new byte[1024];
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            while (is.read(b) != -1) {
                baos.write(b, 0, b.length);
            }

            baos.close();
            is.close();
            text = baos.toString();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return text;
    }

}
