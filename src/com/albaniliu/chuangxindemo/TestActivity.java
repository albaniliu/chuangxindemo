package com.albaniliu.chuangxindemo;

import com.albaniliu.chuangxindemo.data.RandomDataSource;
import com.albaniliu.chuangxindemo.widget.SlideShow;

import android.app.Activity;
import android.os.Bundle;

public class TestActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SlideShow slide = new SlideShow(getBaseContext());
        slide.setDataSource(new RandomDataSource());
        setContentView(slide);
    }
}
