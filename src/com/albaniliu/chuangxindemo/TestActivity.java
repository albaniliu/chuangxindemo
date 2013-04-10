package com.albaniliu.chuangxindemo;

import com.albaniliu.chuangxindemo.util.Utils;
import com.albaniliu.chuangxindemo.widget.BaseImageView;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;

public class TestActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);
        BaseImageView base = (BaseImageView) findViewById(R.id.test_image);
        Bitmap bitmap = Utils.createBitmapByFilePath("/sdcard/liangdemo/umei_cc_0123zwt.jpg", 1024);
        base.setImageBitmap(bitmap);
    }
}
