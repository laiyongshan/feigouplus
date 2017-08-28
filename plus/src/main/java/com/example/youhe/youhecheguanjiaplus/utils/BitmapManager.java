package com.example.youhe.youhecheguanjiaplus.utils;


import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 异步线程加载图片工具类
 * 使用说明：
 * BitmapManager bmpManager;
 * bmpManager = new BitmapManager(BitmapFactory.decodeResource(context.getResources(), R.drawable.loading));
 * bmpManager.loadBitmap(imageURL, imageView);
 */
public class BitmapManager {

    private static HashMap<String, SoftReference<Bitmap>> cache;
    private static ExecutorService pool;
    private static Map<ImageView, String> imageViews;
    private Bitmap defaultBmp;

    private static Context mContext;

    static {
        cache = new HashMap<String, SoftReference<Bitmap>>();
        pool = Executors.newFixedThreadPool(5);  //固定线程池
        imageViews = Collections.synchronizedMap(new WeakHashMap<ImageView, String>());

    }

    public BitmapManager() {

    }

    public BitmapManager(Bitmap def,Context context) {
        this.defaultBmp = def;
        this.mContext=context;
    }

    /**
     * 设置默认图片
     *
     * @param bmp
     */
    public void setDefaultBmp(Bitmap bmp) {
        defaultBmp = bmp;
    }

    /**
     * 加载图片
     *
     * @param url
     * @param imageView
     */
    public void loadBitmap(String url, ImageView imageView) {
        WindowManager wm = (WindowManager) (mContext.getSystemService(Context.WINDOW_SERVICE));
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        int mScreenWidth = dm.widthPixels;
        int mScreenHeigh = dm.heightPixels;
        loadBitmap(url, imageView, this.defaultBmp, mScreenWidth,mScreenHeigh/2);
    }

    /**
     * 加载图片-可设置加载失败后显示的默认图片
     *
     * @param url
     * @param imageView
     * @param defaultBmp
     */
    public void loadBitmap(String url, ImageView imageView, Bitmap defaultBmp) {
        loadBitmap(url, imageView, defaultBmp, 400, 440);
    }

    /**
     * 加载图片-可指定显示图片的高宽
     *
     * @param url
     * @param imageView
     * @param width
     * @param height
     */
    public void loadBitmap(String url, ImageView imageView, Bitmap defaultBmp, int width, int height) {
        imageViews.put(imageView, url);
        Bitmap bitmap = getBitmapFromCache(url);

        if (bitmap != null) {
            //显示缓存图片
            imageView.setImageBitmap(bitmap);
        } else {
            //加载SD卡中的图片缓存
            String filename = FileUtils.getFileName(url);
            String filepath = imageView.getContext().getFilesDir() + File.separator + filename;
            File file = new File(filepath);
            if (file.exists()) {
                //显示SD卡中的图片缓存
                Bitmap bmp = ImageUtils.getBitmap(imageView.getContext(), filename);
//                imageView.setImageBitmap(bmp);
                if(bmp!=null) {
                    imageView.setImageBitmap(bmp);
                    imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                }
            } else {
//                线程加载网络图片
                if(defaultBmp!=null) {
                    imageView.setImageBitmap(defaultBmp);
                }
                queueJob(url, imageView, width, height);
            }
        }
    }


    /**
     * 加载图片-可指定显示图片的高宽
     *
     * @param url
     * @param imageView
     * @param width
     * @param height
     */
    public void loadLogoBitmap(String url, ImageView imageView, Bitmap defaultBmp, int width, int height) {
        imageViews.put(imageView, url);
        Bitmap bitmap = getBitmapFromCache(url);

        if (bitmap != null) {
            //显示缓存图片
            imageView.setImageBitmap(bitmap);
        } else {
            //加载SD卡中的图片缓存
            String filename = FileUtils.getFileName(url);
            String filepath = imageView.getContext().getFilesDir() + File.separator + filename;
            File file = new File(filepath);
            if (file.exists()) {
                //显示SD卡中的图片缓存
                Bitmap bmp = ImageUtils.getBitmap(imageView.getContext(), filename);
//                imageView.setImageBitmap(bmp);
                if(bmp!=null) {
                    imageView.setImageBitmap(bmp);
                    imageView.setBackgroundColor(Color.argb(0,0,0,0));
//                    imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                }
            } else {
//                线程加载网络图片
                if(defaultBmp!=null) {
                    imageView.setImageBitmap(defaultBmp);
                }
                queueJob(url, imageView, width, height);
            }
        }
    }


    /**
     * 从缓存中获取图片
     *
     * @param url
     */
    public Bitmap getBitmapFromCache(String url) {
        Bitmap bitmap = null;
        if (cache.containsKey(url)) {
            bitmap = cache.get(url).get();
        }
        return bitmap;
    }

    /**
     * 从网络中加载图片
     *
     * @param url
     * @param imageView
     * @param width
     * @param height
     */
    public void queueJob(final String url, final ImageView imageView, final int width, final int height) {
        /* Create handler in UI thread. */
        final Handler handler = new Handler() {
            public void handleMessage(Message msg) {
                String tag = imageViews.get(imageView);
                if (tag != null && tag.equals(url)) {

                    if (msg.obj != null) {
//                        imageView.setImageBitmap((Bitmap) msg.obj);
                        imageView.setImageBitmap(BitmapScale.toRoundCorner((Bitmap) msg.obj,5));
                        try {
                            //向SD卡中写入图片缓存
                            ImageUtils.saveImage(imageView.getContext(), FileUtils.getFileName(url), (Bitmap) msg.obj);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        };

        pool.execute(new Runnable() {
            public void run() {
                Message message = Message.obtain();
                message.obj = downloadBitmap(url, width, height);
                handler.sendMessage(message);
            }
        });
    }

    /**
     * 下载图片-可指定显示图片的高宽
     *
     * @param url
     * @param width
     * @param height
     */
    private Bitmap downloadBitmap(String url, int width, int height) {
        Bitmap bitmap = null;
        try {
            //http加载图片
            bitmap = HttpUtil.getNetBitmap(url);
            if (width > 0 && height > 0) {
                //指定显示图片的高宽
                if (bitmap != null)
                    bitmap = Bitmap.createScaledBitmap(bitmap, width*5 / 9, height*4/ 9, true);
            }
            //放入缓存
            cache.put(url, new SoftReference<Bitmap>(bitmap));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }


    /*
     * 图片模糊化
     * */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static Bitmap blurBitmap(Bitmap bitmap, float radius, Context context) {
        Bitmap outBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);

        RenderScript rs = RenderScript.create(context);

        ScriptIntrinsicBlur blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));

        Allocation allIn = Allocation.createFromBitmap(rs, bitmap);
        Allocation allOut = Allocation.createFromBitmap(rs, outBitmap);

        blurScript.setRadius(radius);

        blurScript.setInput(allIn);
        blurScript.forEach(allOut);

        allOut.copyTo(outBitmap);

        bitmap.recycle();

        rs.destroy();

        return outBitmap;
    }
}