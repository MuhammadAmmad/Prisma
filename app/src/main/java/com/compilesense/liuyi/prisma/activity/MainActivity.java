package com.compilesense.liuyi.prisma.activity;

import android.net.Uri;
import android.support.v4.app.FragmentManager;
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
import com.compilesense.liuyi.prisma.fragment.PhotoFragment;
import com.compilesense.liuyi.prisma.fragment.RecentlyAddedFragment;
import com.compilesense.liuyi.prisma.fragment.VideoFragment;
import com.compilesense.liuyi.prisma.javabean.ImageBean;

public class MainActivity extends AppCompatActivity implements
        RecentlyAddedFragment.OnRAFragmentInteractionListener,
        LibraryFragment.OnLibFragmentInteractionListener,
        PhotoFragment.OnPhotoFragmentInteractionListener{
    private final static String TAG = "MainActivity";
    private final static int HEADER_MORE = 0;
    private final static int HEADER_LESS = 2;
    private final static int HEADER_PHOTO = 4;
    private final static int HEADER_VIDEO = 6;
    private int mHeaderStatus = HEADER_MORE;

    private LibraryFragment mLibraryFragment;
    private RecentlyAddedFragment mRecentlyAddedFragment;
    private PhotoFragment mPhotoFragment;
    private FragmentManager mFragmentManager;
    private VideoFragment mVideoFragment;
    private MyFragment mCurrentFragment;

    private enum MyFragment{
        lib,recentAdd,photo,video
    }

    private View mBottomNav;
    private Button cancel;
    private Button recentlyAdd;
    private Button next;
    private ImageView recentlyAddImage;

    private ImageBean currentImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG,"onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initFragment();
        initView();

        mFragmentManager.beginTransaction()
                .add(R.id.main_container, mLibraryFragment)
                .addToBackStack("lib")
                .commit();
        mCurrentFragment = MyFragment.lib;
    }

    void initFragment(){
        mLibraryFragment = new LibraryFragment();
        mRecentlyAddedFragment = new RecentlyAddedFragment();
        mPhotoFragment = new PhotoFragment();
        mVideoFragment = new VideoFragment();
        mFragmentManager = getSupportFragmentManager();
    }

    void initView(){
        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.rg_bottom_nav);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                MyFragment dstFragment = null;
                switch (checkedId){
                    case R.id.rb_library:
                        dstFragment = MyFragment.lib;
                        break;

                    case R.id.rb_photo:
                        dstFragment = MyFragment.photo;

                        break;

                    case R.id.rb_video:
                        dstFragment = MyFragment.video;
                        break;
                }

                changeCurrentFragment(dstFragment);
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
                MyFragment dst = null;
                switch (mCurrentFragment){
                    case lib:
                        dst = MyFragment.recentAdd;
                        break;

                    case recentAdd:
                        dst = MyFragment.lib;
                        break;

                    case photo:
                        dst = null;
                        break;
                }

                changeCurrentFragment(dst);
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StylesActivity.startStylesActivity(MainActivity.this,currentImage);
            }
        });

    }

    private void changeCurrentFragment(MyFragment dstFragment){
        if (dstFragment == null){
            return;
        }
        Log.d(TAG,"mCurrentFragment:" + mCurrentFragment.toString());
        Log.d(TAG,"dstFragment:" + dstFragment.toString());

        switch (dstFragment){
            case lib:
                if (mCurrentFragment != MyFragment.lib){

                    changeHeaderStatus(HEADER_MORE);

                    if (mCurrentFragment == MyFragment.recentAdd){
                        mBottomNav.setVisibility(View.VISIBLE);
                        mFragmentManager.popBackStack();
                        mCurrentFragment = MyFragment.lib;
                        return;
                    }

                    mCurrentFragment = MyFragment.lib;
                    mFragmentManager.beginTransaction()
                            .replace(R.id.main_container,mLibraryFragment)
                            .commit();
                }
                break;

            case photo:
                if (mCurrentFragment != MyFragment.photo){

                    changeHeaderStatus(HEADER_PHOTO);

                    mCurrentFragment = MyFragment.photo;
                    mFragmentManager.beginTransaction()
                            .replace(R.id.main_container,mPhotoFragment)
                            .commit();
                }

                break;

            case recentAdd:
                if (mCurrentFragment != MyFragment.recentAdd){

                    changeHeaderStatus(HEADER_LESS);
                    mBottomNav.setVisibility(View.GONE);

                    mCurrentFragment = MyFragment.recentAdd;
                    mFragmentManager.beginTransaction()
                            .replace(R.id.main_container, mRecentlyAddedFragment)
                            .addToBackStack("recentAdd")
                            .commit();
                }
                break;

            case video:
                if (mCurrentFragment != MyFragment.video){

                    changeHeaderStatus(HEADER_VIDEO);
                    mCurrentFragment = MyFragment.video;
                    mFragmentManager.beginTransaction()
                            .replace(R.id.main_container, mVideoFragment)
                            .commit();
                }
        }

    }

    private void changeHeaderStatus(int headerStatus){
        switch (headerStatus){
            case HEADER_MORE:
                if (mHeaderStatus == HEADER_MORE){
                    return;
                }
                if (mHeaderStatus == HEADER_PHOTO){
                    recentlyAddImage.setVisibility(View.VISIBLE);
                    recentlyAdd.setText(R.string.library);
                }


                mHeaderStatus = HEADER_MORE;
                cancel.setVisibility(View.VISIBLE);
                next.setVisibility(View.VISIBLE);
                recentlyAddImage.setImageResource(R.drawable.ic_expand_more_black_24dp);
                break;

            case HEADER_LESS:
                if (mHeaderStatus == HEADER_LESS){
                    return;
                }

                mHeaderStatus = HEADER_LESS;
                cancel.setVisibility(View.INVISIBLE);
                next.setVisibility(View.INVISIBLE);
                recentlyAddImage.setImageResource(R.drawable.ic_expand_less_black_24dp);
                break;

            case HEADER_PHOTO:
                if (mHeaderStatus == HEADER_PHOTO){
                    return;
                }
                mHeaderStatus = HEADER_PHOTO;
                cancel.setVisibility(View.INVISIBLE);
                next.setVisibility(View.INVISIBLE);
                recentlyAddImage.setVisibility(View.INVISIBLE);
                recentlyAdd.setText(R.string.photo);
                break;

            case HEADER_VIDEO:
                if (mHeaderStatus == HEADER_VIDEO){
                    return;
                }

                if (mHeaderStatus == HEADER_PHOTO){
                    recentlyAddImage.setVisibility(View.VISIBLE);
                    recentlyAdd.setText(R.string.library);
                }
                cancel.setVisibility(View.VISIBLE);
                mHeaderStatus = HEADER_VIDEO;
                next.setVisibility(View.INVISIBLE);
                break;
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
        changeCurrentFragment(MyFragment.lib);
        mLibraryFragment.setCurrentImageBean(imageBean);
    }

    @Override
    public void onLibFragmentInteraction(ImageBean imageBean) {
        currentImage = imageBean;
    }
    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
