package com.compilesense.liuyi.prisma.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;

import com.compilesense.liuyi.prisma.R;
import com.compilesense.liuyi.prisma.fragment.LibraryFragment;
import com.compilesense.liuyi.prisma.fragment.RecentlyAddedFragment;
import com.compilesense.liuyi.prisma.javabean.ImageBean;

public class MainActivity extends AppCompatActivity implements
        RecentlyAddedFragment.OnRAFragmentInteractionListener,
        LibraryFragment.OnLibFragmentInteractionListener {
    private final static String TAG = "MainActivity";
    private final static int HEADER_MORE = 0;
    private final static int HEADER_LESS = 2;
    private int headerStatus = HEADER_MORE;
    LibraryFragment mLibraryFragment;
    RecentlyAddedFragment mRecentlyAddedFragment;
    View mBottomNav;

    Button cancel;
    Button recentlyAdd;
    Button next;
    ImageView recentlyAddImage;

    ImageBean currentImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG,"onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initFragment();
        initView();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.main_container, mLibraryFragment)
                .addToBackStack("lib")
                .commit();
    }

    void initFragment(){
        mLibraryFragment = new LibraryFragment();
        mRecentlyAddedFragment = new RecentlyAddedFragment();
    }

    void initView(){
        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.rg_bottom_nav);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                switch (checkedId){
                    case R.id.rb_library:

                        break;

                    case R.id.rb_photo:

                        break;

                    case R.id.rb_video:

                        break;
                }
            }
        });
        initBottomNav();
        initHeader();
    }

    private void initBottomNav(){
        mBottomNav = findViewById(R.id.fl_bottom_nav);
    }


    //TODO:添加recentAdd的转场动画
    private void initHeader(){
        cancel = (Button) findViewById(R.id.bt_cancel);
        recentlyAdd = (Button) findViewById(R.id.bt_recently_add);
        next = (Button) findViewById(R.id.bt_next);
        recentlyAddImage = (ImageView) findViewById(R.id.img_recently_add);
        recentlyAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeHeaderStatus();
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StylesActivity.startStylesActivity(MainActivity.this,currentImage);
            }
        });

    }

    private void changeHeaderStatus(){
        if (headerStatus == HEADER_MORE){
            headerStatus = HEADER_LESS;
            cancel.setVisibility(View.INVISIBLE);
            next.setVisibility(View.INVISIBLE);
            recentlyAddImage.setImageResource(R.drawable.ic_expand_less_black_24dp);
            mBottomNav.setVisibility(View.GONE);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_container,mRecentlyAddedFragment)
                    .addToBackStack("recentAdd")
                    .commit();

        }else if (headerStatus == HEADER_LESS){
            headerStatus = HEADER_MORE;
            cancel.setVisibility(View.VISIBLE);
            next.setVisibility(View.VISIBLE);
            recentlyAddImage.setImageResource(R.drawable.ic_expand_more_black_24dp);
            mBottomNav.setVisibility(View.VISIBLE);
            getSupportFragmentManager().popBackStack();

        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK){
            Log.d("onKeyDown","finish");
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 在更多的时候我喜欢用类的组合的方式实现接口,但是 fragment 比较特殊,传递给他的参数需要使用 Bundle,
     * 接口无法通过这个传递。
     * @param imageBean
     */
    @Override
    public void onRAFragmentInteraction(ImageBean imageBean) {
        currentImage = imageBean;
        changeHeaderStatus();
        mLibraryFragment.setCurrentImageBean(imageBean);
    }

    @Override
    public void onLibFragmentInteraction(ImageBean imageBean) {
        currentImage = imageBean;
    }
}
