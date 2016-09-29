package com.compilesense.liuyi.prisma.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.compilesense.liuyi.prisma.R;
import com.compilesense.liuyi.prisma.adapter.StyleRecViewAdapter;
import com.compilesense.liuyi.prisma.javabean.ImageBean;
import com.compilesense.liuyi.prisma.javabean.StyleBean;
import com.compilesense.liuyi.prisma.util.Utils;
import com.daimajia.numberprogressbar.NumberProgressBar;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * progressbar https://github.com/daimajia/NumberProgressBar.
 */
public class StylesActivity extends AppCompatActivity {
    private static final String TAG = "StylesActivity";
    private static final int UPDATA_PROGRESS = 1;
    private static final int FINISH_PROGRESS = 2;
    private StyleRecViewAdapter mAdapter;
    private String imagePath;
    private NumberProgressBar mProgressBar;
    private View mProgressLayout;
    private int mProgressStatus = 0;
    private Button mShare, mSave;
    private FrameLayout mBottomContainer;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case UPDATA_PROGRESS:
                    mProgressBar.setProgress(mProgressStatus);
                    break;

                case FINISH_PROGRESS:
                    mProgressStatus = 0;
                    mProgressLayout.setVisibility(View.GONE);
                    mProgressBar.setProgress(0);
                    mShare.setVisibility(View.VISIBLE);
                    changeBottom();
                    break;
            }
        }
    };
    public static void startStylesActivity(Context context, ImageBean imageBean){
        Intent intent = new Intent(context, StylesActivity.class);
        intent.putExtra("imagePath",imageBean.path);
        context.startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_styles);
        imagePath = getIntent().getStringExtra("imagePath");
        initView();
    }

    void initView(){
        mProgressLayout = findViewById(R.id.ll_progress);
        mProgressBar = (NumberProgressBar) findViewById(R.id.pb_style);

        ImageView imageView = (ImageView) findViewById(R.id.img_style);
        Uri uri = Uri.parse("file://" + imagePath);
        imageView.setImageURI(uri);

        initHeader();
        initBottom();
        initRecycleView();
    }
    private void initHeader(){
        ImageButton imageButton = (ImageButton) findViewById(R.id.bt_cancel);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mShare = (Button) findViewById(R.id.bt_share);
        mShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareSingleImage();
            }
        });
    }

    private void initBottom(){
        mBottomContainer = (FrameLayout) findViewById(R.id.fl_bottom_container);
    }

    private void changeBottom(){
        mBottomContainer.removeAllViews();
        mSave = (Button) LayoutInflater.from(this).inflate(R.layout.button_style_bottom, mBottomContainer, false);
        mBottomContainer.addView(mSave);
        if (mSave!=null){
            mSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utils.saveImageToGallery(StylesActivity.this, BitmapFactory.decodeFile(imagePath));
                }
            });
        }
    }

    //分享单张图片
    public void shareSingleImage() {
        String imagePath = this.imagePath;
        //由文件得到uri
        Uri imageUri = Uri.fromFile(new File(imagePath));
        Log.d("share", "uri:" + imageUri);  //输出：file:///storage/emulated/0/test.jpg

        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
        shareIntent.setType("image/*");
        startActivity(Intent.createChooser(shareIntent, "分享到"));
    }


    private void initRecycleView(){
        mAdapter = new StyleRecViewAdapter(this, new StyleRecViewAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {

                mAdapter.setCurrentCheckPosition(position);
                mProgressLayout.setVisibility(View.VISIBLE);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final long duration = 10000;
                        long startTime = System.currentTimeMillis();
                        long cTime;
                        do {
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            cTime = System.currentTimeMillis();
                            Log.d("updata","mProgressStatus:" + mProgressStatus);
                            mProgressStatus += 10;
                            mHandler.sendEmptyMessage(UPDATA_PROGRESS);
                        }while (cTime < startTime + duration);
                        mHandler.sendEmptyMessage(FINISH_PROGRESS);
                    }
                }).start();
            }
        });

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rc_style);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager( new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        testRC();
        recyclerView.setAdapter(mAdapter);
    }
    void testRC(){
        List<StyleBean> styleBeen = new ArrayList<>();
        StyleBean styleBean = new StyleBean();
        styleBean.imageRes = R.drawable.jugg;
        styleBean.title = "jugg";
        styleBeen.add(styleBean);
        styleBean = new StyleBean();
        styleBean.imageRes = R.drawable.so;
        styleBean.title = "目录";
        styleBeen.add(styleBean);
        styleBean = new StyleBean();
        styleBean.imageRes = R.drawable.wlh;
        styleBean.title = "王力宏";
        styleBeen.add(styleBean);
        styleBean = new StyleBean();
        styleBean.imageRes = R.drawable.yao1;
        styleBean.title = "yao";
        styleBeen.add(styleBean);

        mAdapter.setStyleBeanList(styleBeen);
    }
}
