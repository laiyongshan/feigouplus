package com.baidu.ocr.ui.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 * 文件与流处理工具类
 * Created by Administrator on 2017/2/23 0023.
 */

public class BDFileUtils {

    //SD卡根目录
    public final static String EXTERNALSTORAGEDIRECTORY = Environment.getExternalStorageDirectory()
            .getAbsolutePath() + File.separator;
    //默认文件夹路径绝对路径
//    public final static  String APP_FILE_NAME= Application.getResources().getString(R.string.app_name);
    public final static  String APP_FILE_NAME= "chujun";
    public final static String DEFAULTFILEDIRECTORY = EXTERNALSTORAGEDIRECTORY + APP_FILE_NAME;

    /**
     * 检测SD卡是否存在
     */
    public static boolean checkSDcard() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    public static String getDefaultImageName(String appName, String fileName){
        String folderName = getUnEmptyOne(getOutSdRootFolder(appName), getInsideSdRootFolder(appName));
        return folderName + File.separator + fileName;
    }

    public static String getAppImageName(Context context, String appName, String fileName){
        String folderName = getUnEmptyOne(getOutSideAppFolder(context, appName),
                getInsideAppFolder(context, appName));
        return folderName + File.separator + fileName;
    }

    //外部挂载的sd卡
    public static String getOutSdRootFolder(String appName) {
        File sdCard = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        if (sdCard == null) {
            return null;
        }
        String directory = sdCard.getAbsolutePath();
        if (TextUtils.isEmpty(directory)) {
            return null;
        }
        directory = directory + File.separator +appName;
        File file = new File(directory);
        if(!file.exists() && !file.mkdir()) {
            return null;
        }
        return directory;
    }
    public static void writeFile(Context context, String fileName, String content) {
        if (content == null) content = "";

        try {
            FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            fos.write(content.getBytes());

            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static String readFile(Context context, String fileName) {
        try {
            FileInputStream in = context.openFileInput(fileName);
            return readInStream(in);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
    private static String readInStream(FileInputStream inStream) {
        try {
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[512];
            int length = -1;
            while ((length = inStream.read(buffer)) != -1) {
                outStream.write(buffer, 0, length);
            }

            outStream.close();
            inStream.close();
            return outStream.toString();
        } catch (IOException e) {
            Log.i("FileTest", e.getMessage());
        }
        return null;
    }
    public static boolean sdCardAvaiable() {
        return Environment.MEDIA_MOUNTED.equalsIgnoreCase(Environment.getExternalStorageState());
    }
    //手机里面放置的sd卡根目录
    public static String getInsideSdRootFolder(String appName) {
        File sdCard = Environment.getExternalStorageDirectory();
        if (sdCard == null || !sdCard.exists()) {
            return null;
        }
        String directory = sdCard.getAbsolutePath();
        if (TextUtils.isEmpty(directory)) {
            return null;
        }
        directory = directory + File.separator +appName;
        File file = new File(directory);
        if(!file.exists() && !file.mkdir()) {
            return null;
        }
        return directory;
    }
    //内置的sd卡的App图片目录
    public static String getInsideAppFolder(Context context, String appName) {
        File appDir = context.getFilesDir();
        if (appDir == null || !appDir.exists()) {
            return null;
        }
        String directory = appDir.getAbsolutePath() + File.separator +appName;
        File file = new File(directory);
        if(!file.exists() && !file.mkdir()) {
            return null;
        }
        return directory;
    }

    //外置的sd卡的App文件的存储目录
    public static String getOutSideAppFolder(Context context, String appName) {
        File appDir = context.getExternalFilesDir(Environment.DIRECTORY_DCIM);
        if (appDir == null || !appDir.exists()) {
            return null;
        }
        String directory = appDir.getAbsolutePath() + File.separator +appName;
        File file = new File(directory);
        if(!file.exists() && !file.mkdir()) {
            return null;
        }
        return directory;
    }

    public static String getUnEmptyOne(String... strings) {
        String result = null;
        for (String str : strings) {
            if (!TextUtils.isEmpty(str)) {
                result = str;
                break;
            }
        }
        return result;
    }


    /**
     * 将文件保存到本地
     */
    public static void saveFileCache(byte[] fileData, String folderPath, String fileName) {
        File folder = new File(folderPath);
        folder.mkdirs();
        File file = new File(folderPath, fileName);
        ByteArrayInputStream is = new ByteArrayInputStream(fileData);
        OutputStream os = null;
        if (!file.exists()) {
            try {
                file.createNewFile();
                os = new FileOutputStream(file);
                byte[] buffer = new byte[1024];
                int len = 0;
                while (-1 != (len = is.read(buffer))) {
                    os.write(buffer, 0, len);
                }
                os.flush();
            } catch (Exception e) {
                throw new RuntimeException(BDFileUtils.class.getClass().getName(), e);
            } finally {
                closeIO(is, os);
            }
        }
    }

    /**
     * 从指定文件夹获取文件
     *
     * @return 如果文件不存在则创建, 如果如果无法创建文件或文件名为空则返回null
     */
    public static File getSaveFile(String folderPath, String fileNmae) {
        File file = new File(getSavePath(folderPath) + File.separator + fileNmae);
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    /**
     * 获取SD卡下指定文件夹的绝对路径
     *
     * @return 返回SD卡下的指定文件夹的绝对路径
     */
    public static String getSavePath(String folderName) {
        return getSaveFolder(folderName).getAbsolutePath();
    }

    /**
     * 获取文件夹对象
     *
     * @return 返回SD卡下的指定文件夹对象，若文件夹不存在则创建
     */
    public static File getSaveFolder(String folderName) {
        String a="";
        if (sdCardAvaiable()){
            a=Environment.getExternalStorageDirectory().getAbsolutePath();
        }else {
            a=Environment.getRootDirectory().getAbsolutePath();
        }
        File file = new File(a + File.separator + folderName + File.separator);
        file.mkdirs();
        return file;
    }

    /**
     * 输入流转byte[]<br>
     * <p>
     * <b>注意</b> 你必须手动关闭参数inStream
     */
    public static final byte[] input2byte(InputStream inStream) {
        if (inStream == null) {
            return null;
        }
        byte[] in2b = null;
        BufferedInputStream in = new BufferedInputStream(inStream);
        ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
        int rc = 0;
        try {
            while ((rc = in.read()) > 0) {
                swapStream.write(rc);
            }
            in2b = swapStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeIO(inStream, in, swapStream);
        }
        return in2b;
    }

    /**
     * 把uri转为File对象
     */
//    public static File uri2File(Activity aty, Uri uri) {
//        return new File(uri2Path(aty, uri));
////    }

//    public static String uri2Path(Activity aty, Uri uri) {
//        String path = null;
//        Cursor cursor = null;
//        String[] proj = {MediaStore.Images.Media.DATA};
//        try {
//            if (SystemTool.getSDKVersion() < 11) {
//                try {
//                    cursor = aty.managedQuery(uri, proj, null, null, null);
//                } catch (Exception e) {
//                    cursor = aty.getContentResolver().query(uri, proj, null, null, null);
//                }
//            } else {
//                try {
//                    CursorLoader loader = new CursorLoader(aty, uri, proj, null, null, null);
//                    cursor = loader.loadInBackground();
//                } catch (Exception e) {
//                    cursor = aty.getContentResolver().query(uri, proj, null, null, null);
//                }
//            }
//        } catch (Exception e) {
//            // TODO: handle exception
//        }
//
//
//        if (cursor != null && cursor.moveToFirst()) {
//            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
//            path = cursor.getString(column_index);
//            cursor.close();
//        }
//        return path;
//
//    }


    /**
     * 复制文件
     *
     * @param from
     * @param to
     */
    public static void copyFile(File from, File to) {
        if (null == from || !from.exists()) {
            return;
        }
        if (null == to) {
            return;
        }
        FileInputStream is = null;
        FileOutputStream os = null;
        try {
            is = new FileInputStream(from);
            if (!to.exists()) {
                to.createNewFile();
            }
            os = new FileOutputStream(to);
            copyFileFast(is, os);
        } catch (Exception e) {
            throw new RuntimeException(BDFileUtils.class.getClass().getName(), e);
        } finally {
            closeIO(is, os);
        }
    }

    /**
     * 快速复制文件（采用nio操作）
     *
     * @param is 数据来源
     * @param os 数据目标
     * @throws IOException
     */
    public static void copyFileFast(FileInputStream is, FileOutputStream os) throws IOException {
        FileChannel in = is.getChannel();
        FileChannel out = os.getChannel();
        in.transferTo(0, in.size(), out);
    }

    /**
     * 关闭流
     *
     * @param closeables
     */
    public static void closeIO(Closeable... closeables) {
        if (null == closeables || closeables.length <= 0) {
            return;
        }
        for (Closeable cb : closeables) {
            try {
                if (null == cb) {
                    continue;
                }
                cb.close();
            } catch (IOException e) {
                throw new RuntimeException(BDFileUtils.class.getClass().getName(), e);
            }
        }
    }

    /**
     * 图片写入文件
     *
     * @param bitmap   图片
     * @param filePath 文件路径
     * @return 是否写入成功
     */
    public static boolean bitmapToFile(Bitmap bitmap, String filePath) {
        boolean isSuccess = false;
        if (bitmap == null) {
            return isSuccess;
        }
        OutputStream out = null;
        try {
            out = new BufferedOutputStream(new FileOutputStream(filePath), 8 * 1024);
            isSuccess = bitmap.compress(Bitmap.CompressFormat.JPEG, 30, out);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            closeIO(out);
        }
        return isSuccess;
    }

    /**
     * 从文件中读取文本
     *
     * @param filePath
     * @return
     */
    public static String readFile(String filePath) {
        InputStream is = null;
        try {
            is = new FileInputStream(filePath);
        } catch (Exception e) {
            throw new RuntimeException(BDFileUtils.class.getName() + "readFile---->" + filePath + " not found");
        }
        return inputStream2String(is);
    }

    /**
     * 从assets中读取文本
     *
     * @param name
     * @return
     */
    public static String readFileFromAssets(Context context, String name) {
        InputStream is = null;
        try {
            is = context.getResources().getAssets().open(name);
        } catch (Exception e) {
            throw new RuntimeException(BDFileUtils.class.getName() + ".readFileFromAssets---->" + name + " not found");
        }
        return inputStream2String(is);
    }


    /**
     * 输入流转字符串
     *
     * @param is
     * @return 一个流中的字符串
     */
    public static String inputStream2String(InputStream is) {
        if (null == is) {
            return null;
        }
        StringBuilder resultSb = null;
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            resultSb = new StringBuilder();
            String len;
            while (null != (len = br.readLine())) {
                resultSb.append(len);
            }
        } catch (Exception ex) {
        } finally {
            closeIO(is);
        }
        return null == resultSb ? null : resultSb.toString();
    }

    /**
     * 获取文件扩展名
     *
     * @param fileName
     * @return
     */
//    public static String getFileFormat(String fileName) {
//        if (StringUtils.isEmpty(fileName))
//            return "";
//
//        int point = fileName.lastIndexOf('.');
//        return fileName.substring(point + 1);
//    }

    // /**
    // * 纠正图片角度（有些相机拍照后相片会被系统旋转）
    // *
    // * @param path
    // * 图片路径
    // */
    // public static void correctPictureAngle(String path) {
    // int angle = BitmapOperateUtil.readPictureDegree(path);
    // if (angle != 0) {
    // Bitmap image = BitmapHelper.rotate(angle,
    // BitmapCreate.bitmapFromFile(path, 1000, 1000));
    // bitmapToFile(image, path);
    // }
    // }

    /**
     * @param src  源文件绝对路径
     * @param dest 输出文件绝对路径
     * @throws IOException
     * @example zip("/data/data/com.comc/databases", "/data/data/com.comc/databases.zip");
     */
    public static void zip(String src, String dest) throws IOException {
        //提供了一个数据项压缩成一个ZIP归档输出流
        ZipOutputStream out = null;
        try {

            File outFile = new File(dest);//源文件或者目录
            File fileOrDirectory = new File(src);//压缩文件路径
            out = new ZipOutputStream(new FileOutputStream(outFile));
            //如果此文件是一个文件，否则为false。
            if (fileOrDirectory.isFile()) {
                zipFileOrDirectory(out, fileOrDirectory, "");
            } else {
                //返回一个文件或空阵列。
                File[] entries = fileOrDirectory.listFiles();
                for (int i = 0; i < entries.length; i++) {
                    // 递归压缩，更新curPaths
                    zipFileOrDirectory(out, entries[i], "");
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            //关闭输出流
            if (out != null) {
                try {
                    out.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    /**
     * @param out
     * @param fileOrDirectory
     * @param curPath
     * @throws IOException
     */
    private static void zipFileOrDirectory(ZipOutputStream out,
                                           File fileOrDirectory, String curPath) throws IOException {
        //从文件中读取字节的输入流
        FileInputStream in = null;
        try {
            //如果此文件是一个目录，否则返回false。
            if (!fileOrDirectory.isDirectory()) {
                // 压缩文件
                byte[] buffer = new byte[4096];
                int bytes_read;
                in = new FileInputStream(fileOrDirectory);
                //实例代表一个条目内的ZIP归档
                ZipEntry entry = new ZipEntry(curPath
                        + fileOrDirectory.getName());
                //条目的信息写入底层流
                out.putNextEntry(entry);
                while ((bytes_read = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytes_read);
                }
                out.closeEntry();
            } else {
                // 压缩目录
                File[] entries = fileOrDirectory.listFiles();
                for (int i = 0; i < entries.length; i++) {
                    // 递归压缩，更新curPaths
                    zipFileOrDirectory(out, entries[i], curPath
                            + fileOrDirectory.getName() + "/");
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            // throw ex;
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    /**
     * @param zipFileName
     * @param outputDirectory
     * @throws IOException
     */
    @SuppressWarnings("unchecked")
    public static void unzip(String zipFileName, String outputDirectory)
            throws IOException {
        ZipFile zipFile = null;
        try {
            zipFile = new ZipFile(zipFileName);
            Enumeration e = zipFile.entries();
            ZipEntry zipEntry = null;
            File dest = new File(outputDirectory);
            dest.mkdirs();
            while (e.hasMoreElements()) {
                zipEntry = (ZipEntry) e.nextElement();
                String entryName = zipEntry.getName();
                InputStream in = null;
                FileOutputStream out = null;
                try {
                    if (zipEntry.isDirectory()) {
                        String name = zipEntry.getName();
                        name = name.substring(0, name.length() - 1);

                        File f = new File(outputDirectory + File.separator
                                + name);
                        f.mkdirs();
                    } else {
                        int index = entryName.lastIndexOf("\\");
                        if (index != -1) {
                            File df = new File(outputDirectory + File.separator
                                    + entryName.substring(0, index));
                            df.mkdirs();
                        }
                        index = entryName.lastIndexOf("/");
                        if (index != -1) {
                            File df = new File(outputDirectory + File.separator
                                    + entryName.substring(0, index));
                            df.mkdirs();
                        }
                        File f = new File(outputDirectory + File.separator
                                + zipEntry.getName());
                        // f.createNewFile();
                        in = zipFile.getInputStream(zipEntry);
                        out = new FileOutputStream(f);

                        int c;
                        byte[] by = new byte[1024];

                        while ((c = in.read(by)) != -1) {
                            out.write(by, 0, c);
                        }
                        out.flush();
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                    throw new IOException("解压失败：" + ex.toString());
                } finally {
                    if (in != null) {
                        try {
                            in.close();
                        } catch (IOException ex) {
                        }
                    }
                    if (out != null) {
                        try {
                            out.close();
                        } catch (IOException ex) {
                        }
                    }
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            throw new IOException("解压失败：" + ex.toString());
        } finally {
            if (zipFile != null) {
                try {
                    zipFile.close();
                } catch (IOException ex) {
                }
            }
        }
    }

    /**
     * @param context
     * @param path             文件或文件夹路径
     * @param isDelelteDirtory 如果path是文件夹路径，是否需要删除文件夹
     * @return 是否删除成功
     * @说明 删除文件或者整个文件夹
     */
    public static boolean deleteFile(Context context, String path, boolean isDelelteDirtory) {
        File file = new File(toAbsolutePath(path));
        boolean isDelete = false;
        if (file.isFile()) {
            isDelete = file.delete();
//            if (isDelete) {
//                Toast.makeText(context, "删除成功", Toast.LENGTH_LONG).show();
//            }
            return isDelete;
        } else if (file.isDirectory()) {
            File f[] = file.listFiles();
            for (int i = 0; i < f.length; i++) {
                f[i].delete();
            }
            File g[] = file.listFiles();
            isDelete = g.length == 0;
        }
        return isDelete;
    }


    /**
     * @param path 默认文件夹相对路径
     * @return 返回文件绝对路径
     */
    public static String toAbsolutePath(String path) {
        if (!path.startsWith("/")) {
            path = File.separator + path;
            return DEFAULTFILEDIRECTORY
                    + path;
        }
        return path;

    }


//    public static File getObjectSaveFile(Class c) {
//        return getObjectSaveFile(c, "default");
//
//    }

//    public static File getObjectSaveFile(Class c, String name) {
//        String path = FileUtils.getSavePath("37duC");
//        if (name == null)
//            name = "default";
//        return new File(path + "/" + MD5Util.MD5(c.getSimpleName() + UserManager.getUserId() + name));
//
//    }

    public static Bitmap getMagnifyBitmap(Bitmap bm, int newWidth) {//按比例放大图片
        //Log.i("tag1", "图片");
        //放大图片会容易OOM，改用SetViewSize类直接拉伸控件
        try {
            int width = bm.getWidth();
            int height = bm.getHeight();
            float scaleWidth = (float) newWidth / (float) width;
            Matrix matrix = new Matrix();
            matrix.postScale(scaleWidth, scaleWidth);
            bm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            bm.recycle();
//            LogUtils.Log_e("getMagnifyBitmap:", "放大图片OOM");
        }
        return bm;
    }


    /**
     * 文件转base64字符串
     * @return
     */
//    public static String fileToBase64(Bitmap bit) {
//
//        try {
//
////        Bitmap bit= bitmap;
//        ByteArrayOutputStream bos=new ByteArrayOutputStream();
//        bit.compress(Bitmap.CompressFormat.JPEG, 40, bos);//参数100表示不压缩
//        byte[] bytes=bos.toByteArray();
//
//        return Base64Encoder.encode(bytes);// 返回Base64编码过的字节数组字符串
//        }catch (Exception e){
//            return "";
//        }
//    }

    /**
     * 图片按比例大小压缩方法(根据Bitmap图片压缩)
     *
     * @param image
     * @return
     */
    public static String getImage(Bitmap image)
    {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        if (baos.toByteArray().length / 1024 > 400)
        {// 判断如果图片大于1M,进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出
            baos.reset();// 重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, 50, baos);// 这里压缩70%，把压缩后的数据存放到baos中
            if(baos.toByteArray().length/1024>400){
                baos.reset();// 重置baos即清空baos
                image.compress(Bitmap.CompressFormat.JPEG, 50, baos);// 这里压缩70%，把压缩后的数据存放到baos中
            }
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        // 开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        // 现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
        float hh =1080f;// 这里设置高度为800f
        float ww =680f;// 这里设置宽度为480f
        // 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;// be=1表示不缩放
        if (w > h && w > ww)
        {// 如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh)
        {// 如果高度高的话根据宽度固定大小缩放
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;// 设置缩放比例
        String strBitmap = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);//把图片转成BASE64
//		// 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
//		isBm = new ByteArrayInputStream(baos.toByteArray());
//		bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
        if (isBm != null)
        {
            try
            {
                isBm.close();
            } catch (IOException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        if (image != null && !image.isRecycled())
        {
//			image.recycle();
//			image = null;
//			bitmap=compressImage(bitmap);
        }
//		return compressImage(bitmap);// 压缩好比例大小后再进行质量压缩
        return strBitmap;
    }


    /**
     * 图片按比例大小压缩方法(根据Bitmap图片压缩)
     *
     * @param image
     * @return
     */
    public static String getImage2(Bitmap image)
    {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        if (baos.toByteArray().length / 1024 > 800)
        {// 判断如果图片大于1M,进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出
            baos.reset();// 重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 这里压缩70%，把压缩后的数据存放到baos中
            if(baos.toByteArray().length/1024>800){
                baos.reset();// 重置baos即清空baos
                image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 这里压缩70%，把压缩后的数据存放到baos中
            }
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        // 开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        // 现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
        float hh =1960f;// 这里设置高度为800f
        float ww =1080;// 这里设置宽度为480f
        // 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;// be=1表示不缩放
        if (w > h && w > ww)
        {// 如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh)
        {// 如果高度高的话根据宽度固定大小缩放
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;// 设置缩放比例
        String strBitmap = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);//把图片转成BASE64
        if (isBm != null)
        {
            try
            {
                isBm.close();
            } catch (IOException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        if (image != null && !image.isRecycled())
        {
        }
        return strBitmap;
    }


}
