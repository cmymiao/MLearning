package com.example.a11059.mlearning.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.a11059.mlearning.R;
import com.example.a11059.mlearning.adapter.TeacherMainPagerAdapter;
import com.example.a11059.mlearning.utils.UtilUI;
import com.qmuiteam.qmui.util.QMUIResHelper;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.qmuiteam.qmui.widget.QMUITabSegment;
import com.qmuiteam.qmui.widget.QMUIViewPager;

public class TeacherMainActivity extends AppCompatActivity {

    private QMUITabSegment mTabSegment;

    private QMUITabSegment.Tab check;

    private boolean isQuit = false;

    public static void actionStart(Context context){
        Intent intent = new Intent(context, TeacherMainActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        QMUIStatusBarHelper.translucent(this);
        setContentView(R.layout.activity_teacher_main);
        initTabs();
        initPagers();
    }

    private void initTabs(){
        mTabSegment = (QMUITabSegment) findViewById(R.id.main_tabs);
        int normalColor = QMUIResHelper.getAttrColor(TeacherMainActivity.this, R.attr.qmui_config_color_gray_6);
        int selectColor = QMUIResHelper.getAttrColor(TeacherMainActivity.this, R.attr.qmui_config_color_blue);
        mTabSegment.setDefaultNormalColor(normalColor);
        mTabSegment.setDefaultSelectedColor(selectColor);
        mTabSegment.setHasIndicator(true);
        mTabSegment.setIndicatorPosition(true);
        mTabSegment.setIndicatorWidthAdjustContent(false);
        mTabSegment.setDefaultTabIconPosition(QMUITabSegment.ICON_POSITION_TOP);
        QMUITabSegment.Tab statistic = new QMUITabSegment.Tab(
                ContextCompat.getDrawable(TeacherMainActivity.this, R.drawable.ic_tabbar_statis),
                null,
                "统计中心",true
        );
        QMUITabSegment.Tab home = new QMUITabSegment.Tab(
                ContextCompat.getDrawable(TeacherMainActivity.this, R.drawable.ic_tabbar_home),
                null,
                "首页", true
        );
        QMUITabSegment.Tab teachermine = new QMUITabSegment.Tab(
                ContextCompat.getDrawable(TeacherMainActivity.this, R.drawable.ic_tabbar_mine),
                null,
                "个人中心", true
        );
        mTabSegment.addTab(statistic);
        mTabSegment.addTab(home);
        mTabSegment.addTab(teachermine);

        mTabSegment.hideSignCountView(2);
    }


    private void initPagers(){
        QMUIViewPager viewPager = (QMUIViewPager) findViewById(R.id.main_viewpager);
        viewPager.setOffscreenPageLimit(2);
        FragmentManager fragmentManager = getSupportFragmentManager();
        TeacherMainPagerAdapter pagerAdapter = new TeacherMainPagerAdapter(fragmentManager);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setCurrentItem(1);
        mTabSegment.setupWithViewPager(viewPager, false);
    }

    @Override
    public void onBackPressed() {
        if (!isQuit) {
            UtilUI.shortToast("再按一次退出程序");
            isQuit = true;
            //两秒之后isQuit置为false
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {
                        isQuit = false;
                    }
                }
            }).start();
        } else {
            //super.onBackPressed();
            Intent home = new Intent(Intent.ACTION_MAIN);
            home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            home.addCategory(Intent.CATEGORY_HOME);
            startActivity(home);
        }
    }
}
