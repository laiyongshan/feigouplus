package com.example.youhe.youhecheguanjiaplus.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;


public class BitmapScale
{
	private static final String TAG = "BitmapScale";

	/**
	 * 通过资源id转化成Bitmap
	 * 
	 * @param context
	 * @param resId
	 * @return
	 */
	public static Bitmap ReadBitmapById(Context context, int resId)
	{
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inPreferredConfig = Config.RGB_565;
		opt.inPurgeable = true;
		opt.inInputShareable = true;

		InputStream is = context.getResources().openRawResource(resId);
		return BitmapFactory.decodeStream(is, null, opt);
	}

	/**
	 * 缩放Bitmap满屏
	 * 
	 * @param bitmap
	 * @param screenWidth
	 * @param screenHight
	 * @return
	 */
	public static Bitmap getBitmap(Bitmap bitmap, int screenWidth,
			int screenHight)
	{
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();

		Matrix matrix = new Matrix();
		float scale = (float) screenWidth / w;
		float scale2 = (float) screenHight / h;

		// scale = scale < scale2 ? scale : scale2;

		matrix.postScale(scale, scale);
		Bitmap bmp = Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
		if (bitmap != null && !bitmap.equals(bmp) && !bitmap.isRecycled())
		{
			bitmap.recycle();
			bitmap = null;
		}

		return bmp;// Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
	}

	

	/**
	 * 按最大边按一定大小缩放图片
	 * */ 
	public static Bitmap scaleImage(byte[] buffer, float size)
	{
		// 获取原图宽度
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;

		options.inPurgeable = true;
		options.inInputShareable = true;

		Bitmap bm = BitmapFactory.decodeByteArray(buffer, 0, buffer.length,
				options);

		// 计算缩放比例
		float reSize = options.outWidth / size;
		if (options.outWidth < options.outHeight)
		{
			reSize = options.outHeight / size;
		}
		// 如果是小图则放大
		if (reSize <= 1)
		{
			int newWidth = 0;
			int newHeight = 0;
			if (options.outWidth > options.outHeight)
			{
				newWidth = (int) size;
				newHeight = options.outHeight * (int) size / options.outWidth;
			} else
			{
				newHeight = (int) size;
				newWidth = options.outWidth * (int) size / options.outHeight;
			}
			bm = BitmapFactory.decodeByteArray(buffer, 0, buffer.length);
			bm = scaleImage(bm, newWidth, newHeight);
			if (bm == null)
			{
				Log.e(TAG, "convertToThumb, decode fail:" + null);
				return null;
			}

			return bm;
		}
		// 缩放
		options.inJustDecodeBounds = false;
		options.inSampleSize = (int) reSize;

		bm = BitmapFactory.decodeByteArray(buffer, 0, buffer.length, options);
		if (bm == null)
		{
			Log.e(TAG, "convertToThumb, decode fail:" + null);
			return null;
		}

		return bm;

	}

	/**
	 * 检查图片是否超过一定值，是则缩小
	 */
	public static Bitmap convertToThumb(byte[] buffer, float size)
	{
		// 获取原图宽度
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;

		options.inPurgeable = true;
		options.inInputShareable = true;

		Bitmap bm = BitmapFactory.decodeByteArray(buffer, 0, buffer.length,
				options);

		// 计算缩放比例
		float reSize = options.outWidth / size;

		if (options.outWidth > options.outHeight)
		{
			reSize = options.outHeight / size;
		}

		if (reSize <= 0)
		{
			reSize = 1;
		}

		Log.d(TAG, "convertToThumb, reSize:" + reSize);

		// 缩放
		options.inJustDecodeBounds = false;
		options.inSampleSize = (int) reSize;

		if (bm != null && !bm.isRecycled())
		{
			bm.recycle();
			bm = null;
			Log.e(TAG, "convertToThumb, recyle");
		}

		bm = BitmapFactory.decodeByteArray(buffer, 0, buffer.length, options);

		if (bm == null)
		{
			Log.e(TAG, "convertToThumb, decode fail:" + null);
			return null;
		}

		return bm;
	}

	/**
	 * Bitmap --> byte[]
	 * 
	 * @param bmp
	 * @return
	 */
	private static byte[] readBitmap(Bitmap bmp)
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bmp.compress(Bitmap.CompressFormat.JPEG, 80, baos);
		try
		{
			baos.flush();
			baos.close();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		return baos.toByteArray();
	}

	/**
	 * Bitmap --> byte[]
	 * @return
	 */
	public static byte[] readBitmapFromBuffer(byte[] buffer, float size)
	{
		return readBitmap(convertToThumb(buffer, size));
	}

	/**
	 * 以屏幕宽度为基准，显示图片
	 * @return
	 */
	public static Bitmap decodeStream(Context context, Intent data, float size)
	{
		Bitmap image = null;
		try
		{
			Uri dataUri = data.getData();
			// 获取原图宽度
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;

			options.inPurgeable = true;
			options.inInputShareable = true;
			BitmapFactory.decodeStream(context.getContentResolver()
					.openInputStream(dataUri), null, options);

			// 计算缩放比例
			float reSize = (int) (options.outWidth / size);
			if (reSize <= 0)
			{
				reSize = 1;
			}

			Log.d(TAG, "old-w:" + options.outWidth + ", llyt-w:" + size
					+ ", resize:" + reSize);
			// 缩放
			options.inJustDecodeBounds = false;
			options.inSampleSize = (int) reSize;
			image = BitmapFactory.decodeStream(context.getContentResolver()
					.openInputStream(dataUri), null, options);
		} catch (Exception e)
		{
			e.printStackTrace();
		}

		return image;
	}
	
	/**
	 * 按新的宽高缩放图片
	 * 
	 * @param bm
	 * @param newWidth
	 * @param newHeight
	 * @return
	 */
	public static Bitmap scaleImage(Bitmap bm, int newWidth, int newHeight)
	{
		if (bm == null)
		{
			return null;
		}

		int width = bm.getWidth();
		int height = bm.getHeight();
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;
		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);
		Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix,
				true);
		if (bm != null & !bm.isRecycled())
		{
			bm.recycle();
			bm = null;
		}
		return newbm;
	}

	/**
	 * fuction: 设置固定的宽度，高度随之变化，使图片不会变形
	 * 
	 * @param target
	 *            需要转化bitmap参数
	 * @param newWidth
	 *            设置新的宽度
	 * @return
	 */
	public static Bitmap fitBitmap(Bitmap target, int newWidth)
	{

		int width = target.getWidth();
		int height = target.getHeight();
		Matrix matrix = new Matrix();

		float scaleWidth = ((float) newWidth) / width;
		// float scaleHeight = ((float)newHeight) / height;
		int newHeight = (int) (scaleWidth * height);
		matrix.postScale(scaleWidth, scaleWidth);
		// Bitmap result = Bitmap.createBitmap(target,0,0,width,height,
		// matrix,true);
		Bitmap bmp = Bitmap.createBitmap(target, 0, 0, width, height, matrix,
				true);
		if (target != null && !target.equals(bmp) && !target.isRecycled())
		{
//			target.recycle();
//			target = null;
		}
		return bmp;// Bitmap.createBitmap(target, 0, 0, width, height, matrix,
					// true);
	}

	

	/**
	 * 根据指定的宽度平铺图像
	 * 
	 * @param width
	 * @param src
	 * @return
	 */
	public static Bitmap createRepeater(int width, Bitmap src)
	{
		int count = (width + src.getWidth() - 1) / src.getWidth();

		Bitmap bitmap = Bitmap.createBitmap(width, src.getHeight(),
				Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);

		for (int idx = 0; idx < count; ++idx)
		{

			canvas.drawBitmap(src, idx * src.getWidth(), 0, null);
		}

		return bitmap;
	}

	
	/**
	 * 图片的质量压缩方法
	 * 
	 * @param image
	 * @return
	 */
	public static Bitmap compressImage(Bitmap image)
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
		int options = 100;
		while (options>0&&(baos.toByteArray().length / 1024)>1024) {  //循环判断如果压缩后图片是否大于100kb,大于继续压缩
			baos.reset();//重置baos即清空baos
			image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
			options -= 10;//每次都减少10
		}
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
		if (baos != null)
		{
			try
			{
				baos.close();
			} catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
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
			image.recycle();
			image = null;
		}
		return bitmap;
	}

	

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
		if (baos.toByteArray().length / 1024 > 1024)
		{// 判断如果图片大于1M,进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出
			baos.reset();// 重置baos即清空baos
			image.compress(Bitmap.CompressFormat.JPEG, 70, baos);// 这里压缩70%，把压缩后的数据存放到baos中
			if(baos.toByteArray().length/1024>1024){
				baos.reset();// 重置baos即清空baos
				image.compress(Bitmap.CompressFormat.JPEG, 70, baos);// 这里压缩70%，把压缩后的数据存放到baos中
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
		float hh =1280f;// 这里设置高度为800f
		float ww =720f;// 这里设置宽度为480f
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
	 * 通过资源id转化成Bitmap 全屏显示
	 * 
	 * @param context
	 * @param drawableId
	 * @param screenWidth
	 * @param screenHight
	 * @return
	 */
	public static Bitmap ReadBitmapById(Context context, int drawableId,
			int screenWidth, int screenHight)
	{
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inPreferredConfig = Config.ARGB_8888;
		options.inInputShareable = true;
		options.inPurgeable = true;
		InputStream stream = context.getResources().openRawResource(drawableId);
		Bitmap bitmap = BitmapFactory.decodeStream(stream, null, options);
		return getBitmap(bitmap, screenWidth, screenHight);
	}


	/**
	 * function:图片转圆角
	 *
	 * @param bitmap
	 *            需要转的bitmap
	 * @param pixels
	 *            转圆角的弧度
	 * @return 转圆角的bitmap
	 */
	public static Bitmap toRoundCorner(Bitmap bitmap, int pixels)
	{
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);
		final float roundPx = pixels;
		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);
		if (bitmap != null && !bitmap.isRecycled())
		{
//			bitmap.recycle();
		}
		return output;
	}

	/**
	 * 旋转图片
	 *
	 * @param angle
	 * @param bitmap
	 * @return
	 */
	public static Bitmap rotaingImageView(int angle, Bitmap bitmap)
	{
		// 旋转图片 动作
		Matrix matrix = new Matrix();
		matrix.postRotate(angle);

		// 创建新的图片
		Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0,0,
				bitmap.getWidth(), bitmap.getHeight(), matrix, true);
		if (resizedBitmap != bitmap && bitmap != null && !bitmap.isRecycled())
		{
			bitmap.recycle();
			bitmap = null;
		}

		return resizedBitmap;
	}


	public static Bitmap getBitmap(String path){
		InputStream is = null;
		try {
			is = new FileInputStream(path);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = 2;
		options.inTempStorage = new byte[100 * 1024];
		options.inPreferredConfig = Config.RGB_565;
		//4.设置图片可以被回收，创建Bitmap用于存储Pixel的内存空间在系统内存不足时可以被回收
		options.inPurgeable = true;
//		options.inSampleSize = 4
		;
		//6.设置解码位图的尺寸信息
		options.inInputShareable = true;
		//7.解码位图
		Bitmap d1_bitmap =BitmapFactory.decodeStream(is,null, options);

		return d1_bitmap;
	}



	/**
	 * 将一个bitmap转化为圆形输出
	 * @param bitmap
	 * @return
	 */
	public static Bitmap toRoundBitmap(Bitmap bitmap){
		int width=bitmap.getWidth();
		int height=bitmap.getHeight();
		int r=0;
		if (width<height){
			r=width;
		}else {
			r=height;
		}
		Bitmap backgroundBitmap=Bitmap.createBitmap(width,height, Config.ARGB_8888);
		Canvas canvas=new Canvas(backgroundBitmap);
		Paint paint=new Paint();
		paint.setAntiAlias(true);
		RectF rectF=new RectF(0,0,r,r);
		canvas.drawRoundRect(rectF,r/2,r/2,paint);
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
		canvas.drawBitmap(bitmap,null,rectF,paint);
		return backgroundBitmap;
	}
	/**
	 * 保存文件
	 * @param bm
	 * @throws IOException
	 */
	public static File saveFile(Bitmap bm, String userMobile) throws IOException {
		String path = Environment.getExternalStorageDirectory().toString()+"/yeohe/head_bitmap/";
		File dirFile = new File(path);
		if(!dirFile.exists()){
			dirFile.mkdirs();
		}
//		SaveNameDao saveNameDao=new SaveNameDao(activity);
		File myIconFile= new File(path +userMobile+ "head.jpg");
//		Log.d("TAG","dddd"+myIconFile.getAbsolutePath());
		BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myIconFile));
		bm.compress(Bitmap.CompressFormat.JPEG, 80, bos);
		bos.flush();
		bos.close();
		return myIconFile;
	}


	/**
	 * 从本地获取图片
	 * @param pathString 文件路径
	 * @return 图片
	 */
	public static Bitmap getDiskBitmap(String pathString)
	{
		Bitmap bitmap = null;
		try
		{
			File file = new File(pathString);
			if(file.exists())
			{
				bitmap = BitmapFactory.decodeFile(pathString);
			}
		} catch (Exception e)
		{
			// TODO: handle exception
		}
		return bitmap;
	}


}