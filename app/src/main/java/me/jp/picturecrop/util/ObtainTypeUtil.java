package me.jp.picturecrop.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 获取图片类型工具类 1、相册选择图片 2、相机照相得到
 * 
 * @author JiangPing
 * 
 */
public class ObtainTypeUtil {

	/**
	 * 选择图片
	 */
	public static void choosePicture(Activity activity, int requestCode) {
		Intent openAlbumIntent = new Intent(Intent.ACTION_GET_CONTENT);
		openAlbumIntent.setType("image/*");
		activity.startActivityForResult(openAlbumIntent, requestCode);
	}

	/**
	 * 照相
	 * 
	 * @param activity
	 */
	public static void takePhoto(Activity activity, Uri pictureUri,
			int requestCode) {
		Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

		// 指定照片保存路径（SD卡），pictureUri为一个临时文件，每次拍照后这个图片都会被替换
		openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, pictureUri);
		activity.startActivityForResult(openCameraIntent, requestCode);
	}

	// 使用系统当前日期加以调整作为照片的名称
	@SuppressLint("SimpleDateFormat")
	public static String getPhotoFileName() {
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"'IMG'_yyyyMMdd_HHmmss");
		return dateFormat.format(date) + ".jpg";
	}

}
