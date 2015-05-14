package me.jp.picturecrop.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.io.File;

import me.jp.picturecrop.R;
import me.jp.picturecrop.common.Constants;
import me.jp.picturecrop.util.BitmapUtil;
import me.jp.picturecrop.util.ObtainTypeUtil;
import me.jp.picturecrop.util.StorageUtil;


public class MainActivity extends AppCompatActivity {

    private ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("PictureCrop");
        toolbar.setTitleTextColor(Color.WHITE);
        mImageView = (ImageView) findViewById(R.id.imageView);
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        //设置裁剪完成的图片
        Bitmap bitmap = BitmapUtil.getSmallBitmap(Constants.File.DIR_IMAGE + File.separator + "crop_picture.jpg", 1080, 1920);
        mImageView.setImageBitmap(bitmap);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.choose_picture:
                ObtainTypeUtil.choosePicture(this, CHOOSE_PICTURE_REQCODE);
                break;
            case R.id.take_photo:
                ObtainTypeUtil.takePhoto(this, TAKE_PIC_URI, TAKE_PICTURE_REQCODE);
                break;
        }
    }

    // 指定拍照完成保存路径
    private final String PICTURE_ABSOLUTE_PATH = Constants.File.DIR_IMAGE + File.separator + "picturnname.jpg";
    private Uri TAKE_PIC_URI = Uri.fromFile(new File(PICTURE_ABSOLUTE_PATH));

    private static final int CHOOSE_PICTURE_REQCODE = 0;// 相册选择照片
    private static final int TAKE_PICTURE_REQCODE = 1;// 拍照

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            // 相册选择图片完成回调
            case CHOOSE_PICTURE_REQCODE:
                if (data != null) {
                    Uri originalUri = data.getData();
                    if (originalUri != null) {// 选择图片完成跳转切图页面
                        //将uri转成完整的路径
                        String picPathStr = StorageUtil.getStringPathFromUri(this, originalUri);
                        Log.i("tag", "picPathStr:" + picPathStr);
                        Intent intent = new Intent(this, PictureCropActivity.class);
                        intent.putExtra(PictureCropActivity.PIC_PATH, picPathStr);
                        startActivity(intent);
                    }
                }
                break;
            // 拍照返回
            case TAKE_PICTURE_REQCODE:
                //拍照取消
                if (resultCode == Activity.RESULT_CANCELED) {
                    return;
                }

                Intent intent = new Intent(this, PictureCropActivity.class);
                intent.putExtra(PictureCropActivity.PIC_PATH,
                        PICTURE_ABSOLUTE_PATH);
                startActivity(intent);
                break;
        }
    }


}
