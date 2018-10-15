package com.example.a11059.mlearning.application;

/**
 * Created by Eugene on 2018/8/10.
 */
import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

public class BaseSampleActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        configureActionBar();
    }

    protected void configureActionBar() {
//        int color = getResources().getColor(R.color.pdfViewPager_ab_color);
////        int color = ContextCompat.getColor(this,R.color.pdfViewPager_ab_color);
//        ActionBar ab = getSupportActionBar();
//        ab.setBackgroundDrawable(new ColorDrawable(color));
    }
}
