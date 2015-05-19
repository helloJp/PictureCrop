package me.jp.picturecrop.common;

import android.os.Environment;

/**
 * Created by JiangPing on 2015/5/14.
 */
public final class Constants {


    //外存储卡路径
    public static final String EXTERNAL_STORAGE_DIR = Environment
            .getExternalStorageDirectory().getAbsolutePath();

    public static final class File {
        //文件存储路径
        public static final String DIR_FILE = EXTERNAL_STORAGE_DIR + java.io.File.separator + "files";
        //图片存储路径
        public static final String DIR_IMAGE = EXTERNAL_STORAGE_DIR + java.io.File.separator + "images";
        //存储图片完整路径
        public static final String ABS_PIC_PATH = DIR_IMAGE + java.io.File.separator + "jp_picture.jpg";


    }
}
