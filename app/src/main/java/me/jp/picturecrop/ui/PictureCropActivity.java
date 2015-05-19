package me.jp.picturecrop.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.io.File;
import java.io.FileOutputStream;

import me.jp.picturecrop.R;
import me.jp.picturecrop.common.Constants;
import me.jp.picturecrop.util.BitmapUtil;
import me.jp.picturecrop.view.clipimage.ClipImageLayout;


/**
 * 切图页面
 *
 * @author JiangPing
 */
public class PictureCropActivity extends AppCompatActivity {
    public static final String PIC_PATH = "picture_path";


    private boolean mIsAddWaterMark;
    private Toolbar mToolbar;
    private String mPicPathStr;
    private Bitmap mBitmap;

    private ClipImageLayout mClipImageLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_crop);
        mIsAddWaterMark = getIntent().getBooleanExtra(MainActivity.ADD_STICKER_VIEW,false);
        setUpToolbar();
        initView();
    }

    private void setUpToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        mToolbar.setNavigationIcon(android.R.drawable.arrow_up_float);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //返回 按键
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_picture_crop, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //保存图片
        if (item.getItemId() == R.id.action_save) {

            boolean flag = BitmapUtil.saveBitmap(mClipImageLayout.clip(), Constants.File.ABS_PIC_PATH);
            Log.i("TestData", "save bitmap flag:" + flag);
            finish();
            if (mIsAddWaterMark) {
                startActivity(new Intent(PictureCropActivity.this,StickerPicActivity.class));
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initView() {
        //获取图片路径
        mPicPathStr = getIntent().getStringExtra(PIC_PATH);
        //图片路径不为空
        if (!TextUtils.isEmpty(mPicPathStr)) {
            //图片超过宽度超过1080px或者 高度超过1920px，进行压缩.
            mBitmap = BitmapUtil.getSmallBitmap(mPicPathStr, 1080, 1920);
        }

        mClipImageLayout = (ClipImageLayout) findViewById(R.id.id_clipImageLayout);
        //图片放入布局中去
        mClipImageLayout.setClipImage(new BitmapDrawable(mBitmap));
    }


    private void saveCropPictureToSDCard() {
        Bitmap bitmap = mClipImageLayout.clip();
        File file = new File(Constants.File.DIR_IMAGE);
        if (!file.exists()) {
            file.mkdirs();
        }

        try {
            FileOutputStream fos = new FileOutputStream(file.getPath() + File.separator + "crop_picture.jpg");
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.close();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }


}
