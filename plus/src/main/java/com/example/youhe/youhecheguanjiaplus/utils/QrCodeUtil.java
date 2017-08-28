package com.example.youhe.youhecheguanjiaplus.utils;

import android.graphics.Bitmap;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

/**
 * Created by Administrator on 2017/5/19 0019.
 */

public class QrCodeUtil {

//    /**
//     * 生成二维码Bitmap
//     *
//     * @param content   内容
//     * @param widthPix  图片宽度
//     * @param heightPix 图片高度
//     * @param logoBm    二维码中心的Logo图标（可以为null）
//     * @param filePath  用于存储二维码图片的文件路径
//     * @return 生成二维码及保存文件是否成功
//     */
//    public static boolean createQRImage(String content, int widthPix, int heightPix, Bitmap logoBm, String filePath) {
//        Bitmap bitmap = null;
//        FileOutputStream stream = null;
//        try {
//            if (content == null || "".equals(content)) {
//                return false;
//            }
//
//            //配置参数
//            Hashtable hints = new Hashtable();
//            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
//            //容错级别
//            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
//            //设置空白边距的宽度
////            hints.put(EncodeHintType.MARGIN, 2); //default is 4
//
//            // 图像数据转换，使用了矩阵转换
//            BitMatrix bitMatrix = new QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, widthPix, heightPix, hints);
//            int[] pixels = new int[widthPix * heightPix];
//            // 下面这里按照二维码的算法，逐个生成二维码的图片，
//            // 两个for循环是图片横列扫描的结果
//            for (int y = 0; y < heightPix; y++) {
//                for (int x = 0; x < widthPix; x++) {
//                    if (bitMatrix.get(x, y)) {
//                        pixels[y * widthPix + x] = 0xff000000;
//                    } else {
//                        pixels[y * widthPix + x] = 0xffffffff;
//                    }
//                }
//            }
//
//            // 生成二维码图片的格式，使用ARGB_8888
//            bitmap =  Bitmap.createBitmap(widthPix, heightPix, Bitmap.Config.ARGB_8888);
//            bitmap.setPixels(pixels, 0, widthPix, 0, 0, widthPix, heightPix);
//
//            if (logoBm != null) {
////                bitmap = addLogo(bitmap, logoBm);
//            }
//            stream = new FileOutputStream(filePath);
//            boolean compressed = bitmap.compress(Bitmap.CompressFormat.JPEG, 100,stream);
//            //必须使用compress方法将bitmap保存到文件中再进行读取。直接返回的bitmap是没有任何压缩的，内存消耗巨大！
//
//            return bitmap != null && compressed;
//        } catch (WriterException | IOException e) {
//            e.printStackTrace();
//            bitmap.recycle();
//            bitmap = null;
//            try {
//                stream.close();
//            } catch (IOException e1) {
//                e1.printStackTrace();
//            }
//        }
//        finally {
//            if(bitmap!=null)
//            {
//                bitmap.recycle();
//            }
//            try {
//                stream.close();
//            } catch (IOException e1) {
//                e1.printStackTrace();
//            }
//        }
//
//        return false;
//    }

    public static  Bitmap generateBitmap(String content,int width, int height) {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        Map<EncodeHintType, String> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        try {
            BitMatrix encode = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, width, height, hints);
            int[] pixels = new int[width * height];
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    if (encode.get(j, i)) {
                        pixels[i * width + j] = 0x00000000;
                    } else {
                        pixels[i * width + j] = 0xffffffff;
                    }
                }
            }
            return Bitmap.createBitmap(pixels, 0, width, width, height, Bitmap.Config.RGB_565);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return null;
    }
}
