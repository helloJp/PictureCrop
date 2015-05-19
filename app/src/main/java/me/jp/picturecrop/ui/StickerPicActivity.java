package me.jp.picturecrop.ui;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import me.jp.picturecrop.R;
import me.jp.picturecrop.adapter.GalleryAdapter;
import me.jp.picturecrop.common.Constants;
import me.jp.picturecrop.util.BitmapUtil;
import me.jp.picturecrop.view.StickerView;

/**
 * 贴图页面
 * Created by JiangPing on 2015/5/19.
 */
public class StickerPicActivity extends Activity {
    RecyclerView mRecyclerView;//贴图画廊
    GalleryAdapter mGalleryAdapter;//画廊适配器

    ImageView mIvPicture;//操作图片
    StickerView mStickerView;//贴图

    private int[] mImgResIds = new int[]{R.mipmap.ic_sticker01, R.mipmap.ic_sticker02, R.mipmap.ic_sticker03, R.mipmap.ic_sticker04, R.mipmap.ic_sticker05, R.mipmap.ic_sticker02, R.mipmap.ic_sticker03, R.mipmap.ic_sticker04};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sticker_pic);
        initView();
        initGallery();
    }

    private void initGallery() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayout.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mGalleryAdapter = new GalleryAdapter(this, mImgResIds);
        mRecyclerView.setAdapter(mGalleryAdapter);
        mGalleryAdapter.setOnItemClickListener(new GalleryAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int data) {
                pickSticker(data);
            }
        });
    }


    private void initView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mIvPicture = (ImageView) findViewById(R.id.iv_picture);
        //获取本地裁后的图片
        Bitmap bitmap = BitmapUtil.getSmallBitmap(Constants.File.ABS_PIC_PATH, 1080, 1920);
        if (bitmap != null)
            mIvPicture.setImageBitmap(bitmap);
    }

    //选择贴图添加到布局中
    private void pickSticker(int resId) {
        mStickerView = new StickerView(this);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        params.addRule(RelativeLayout.ALIGN_BOTTOM, R.id.iv_picture);
        params.addRule(RelativeLayout.ALIGN_TOP, R.id.iv_picture);
        ViewGroup parentView = (ViewGroup) mIvPicture.getParent();
        if (parentView.getChildCount() > 1) {
            parentView.removeViewAt(1);
        }
        ((ViewGroup) mIvPicture.getParent()).addView(mStickerView);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), resId);
        mStickerView.setWaterMark(bitmap);
    }

    //保存，摆放贴图后的图片
    public void onSave(View view) {
        //背景Bitmap
        mIvPicture.setDrawingCacheEnabled(true);
        mIvPicture.buildDrawingCache();
        Bitmap bmBg = mIvPicture.getDrawingCache();
        bmBg = Bitmap.createBitmap(bmBg, 0, 0, bmBg.getWidth(), bmBg.getHeight());//创建背景大小
        mIvPicture.destroyDrawingCache();
        Canvas canvas = new Canvas(bmBg);
        canvas.drawBitmap(bmBg, 0, 0, null);//在 0，0坐标开始画入背景Bitmap

        //贴纸bitmap
        if (mStickerView != null) {
            Bitmap bmSticker = mStickerView.getBitmap();
            canvas.drawBitmap(bmSticker, 0, 0, null);
        }
        canvas.save(Canvas.ALL_SAVE_FLAG);//保存
        canvas.restore();
        BitmapUtil.saveBitmap(bmBg, Constants.File.ABS_PIC_PATH);
        finish();
    }
}
