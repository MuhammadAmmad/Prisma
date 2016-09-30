package com.compilesense.liuyi.prisma.util;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.util.Log;

/**
 * Created by shenjingyuan002 on 16/9/30.
 */

@SuppressWarnings("deprecation")
public class CameraPara {
    private static final String tag = "CameraPara";
    private CameraSizeComparator sizeComparator = new CameraSizeComparator();
    private static CameraPara myCamPara = null;
    private CameraPara(){
    }
    public static CameraPara getInstance(){
        if(myCamPara == null){
            myCamPara = new CameraPara();
            return myCamPara;
        }
        else{
            return myCamPara;
        }
    }
    public Camera.Size getPreviewSize(List<Camera.Size> list, int th){
        Collections.sort(list, sizeComparator);
        int i = 0;
        for(Camera.Size s:list){
            if((s.width > th) && equalRate(s, 1.33f)){
                Log.i(tag, "最终设置预览尺寸:w = " + s.width + "h = " + s.height);
                break;
            }
            i++;
        }
        return list.get(i);
    }
    public Size getPictureSize(List<Camera.Size> list, int th){
        Collections.sort(list, sizeComparator);
        int i = 0;
        for(Size s:list){
            if((s.width > th) && equalRate(s, 1.33f)){
                Log.i(tag, "最终设置图片尺寸:w = " + s.width + "h = " + s.height);
                break;
            }
            i++;
        }
        return list.get(i);
    }
    public boolean equalRate(Size s, float rate){
        float r = (float)(s.width)/(float)(s.height);
        if(Math.abs(r - rate) <= 0.2)
        {
            return true;
        }
        else{
            return false;
        }
    }
    public  class CameraSizeComparator implements Comparator<Camera.Size>{
        //按升序排列
        public int compare(Size lhs, Size rhs) {

            if(lhs.width == rhs.width){
                return 0;
            }
            else if(lhs.width > rhs.width){
                return 1;
            }
            else{
                return -1;
            }
        }
    }
}
