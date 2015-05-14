package me.jp.picturecrop.util;

import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

import java.io.File;

import me.jp.picturecrop.common.Constants;

/**
 * 存储文件工具类
 *
 * @author JiangPing
 */
public class StorageUtil {

    private StorageUtil() {
    }

    public static Context mContext;

    public static void init(Context context) {
        mContext = context;
    }

    /**
     * 判断外存储是否可写
     *
     * @return
     */
    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    /**
     * 获取当前app文件存储路径
     *
     * @return
     */
    public static File getFileDir() {
        File fileDir = null;
        if (isExternalStorageWritable()) {
            fileDir = new File(Constants.File.DIR_FILE);
            if (!fileDir.exists()) {
                fileDir.mkdirs();
            }
        } else {
            throw new RuntimeException("外部存储不可写");
        }
        return fileDir;
    }

    /**
     * 获取当前app图片文件存储路径
     *
     * @return
     */
    public static File getImageDir() {
        File imageDir = null;
        if (isExternalStorageWritable()) {
            imageDir = new File(Constants.File.DIR_IMAGE);
            if (!imageDir.exists()) {
                imageDir.mkdirs();
            }
        } else {
            throw new RuntimeException("外部存储不可写");
        }
        return imageDir;
    }


    /**
     * 获取可用sd空间大小
     *
     * @return
     */
    @SuppressWarnings("deprecation")
    public static long getAvailableSize() {
        StatFs statfs = new StatFs(Environment.getExternalStorageDirectory().getAbsolutePath());
        long blocks = statfs.getAvailableBlocks();
        long size = statfs.getBlockSize();
        return blocks * size;
    }

    /**
     * sd卡空间是否够用
     *
     * @param contentLength 文件大小
     * @return
     */
    public static boolean isSaveable(int contentLength) {
        long avaliable = getAvailableSize();
        return avaliable > contentLength ? true : false;
    }

    /**
     * 存储完文件后，sd存储空间是否处于快用完状态
     *
     * @param contentLength 文件大小
     * @return
     */
    public static boolean isWeakSaveable(int contentLength) {
        return getAvailableSize() - 30 * 1024 * 1024 < contentLength ? true : false;
    }

    /**
     * Get a file path from a Uri. This will get the the path for Storage Access Framework Documents, as well as the _data field for the MediaStore and other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri     The Uri to query.
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static String getStringPathFromUri(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        final String[] projection = {MediaStore.MediaColumns.DATA};
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public static boolean deleteDirectory(String filePath) {
        if (null == filePath) {
            return false;
        }
        File file = new File(filePath);
        if (file == null || !file.exists()) {
            return false;
        }
        if (file.isDirectory()) {
            File[] list = file.listFiles();
            for (int i = 0; i < list.length; i++) {
                if (list[i].isDirectory()) {
                    deleteDirectory(list[i].getAbsolutePath());
                } else {
                    list[i].delete();
                }
            }
        }
        file.delete();
        return true;
    }

    public static boolean createDirectory(String filePath) {
        if (null == filePath) {
            return false;
        }
        File file = new File(filePath);
        if (file.exists()) {
            return true;
        }
        file.mkdirs();
        return true;
    }

    public static boolean deleteFile(String filepath) {
        File file = new File(filepath);
        if (file.exists()) {
            return file.delete();
        }
        return false;
    }

    public static boolean isExist(String filePath) {
        File file = new File(filePath);
        if (file == null || !file.exists()) {
            return false;
        }
        return true;
    }
}