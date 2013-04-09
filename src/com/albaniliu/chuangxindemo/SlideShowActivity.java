
package com.albaniliu.chuangxindemo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.albaniliu.chuangxindemo.data.RandomDataSource;

public class SlideShowActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Slideshow slideshow = new Slideshow(this);
        slideshow.setDataSource(new RandomDataSource());
        slideshow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                return;
            }
        });
        setContentView(slideshow);
    }
}
