package com.compilesense.liuyi.prisma.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.SurfaceView;

/**
 * 以前用 openCV 自带的 JavaCameraView 来进行预览,这在竖屏下有问题,
 * 摄像头传感器的数据默认是横屏状态下的,而 Camera Aip 不能改变 PreviewCallback#onPreviewFrame
 * 中的数据,只能对得到的数据进行矩阵转换,这样显然图像的大小发生了变化,又会回过头来对以前的设置产生
 * 影响,所以需要自己实现一个camera的预览View。
 * Created by shenjingyuan002 on 16/9/23.
 */

public class CameraView extends SurfaceView {
    public CameraView(Context context) {
        super(context);
        init();
    }

    public CameraView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CameraView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    void init(){

    }

}
