package com.compilesense.liuyi.prisma.view;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.os.Build;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.compilesense.liuyi.prisma.R;

/**
 * Created by shenjingyuan002 on 16/9/29.
 */

public class CheckLayout extends FrameLayout {
    private static final String TAG = "CheckLayout";
    private static final PorterDuffXfermode DST_IN = new PorterDuffXfermode(PorterDuff.Mode.DST_IN);
    private Paint mAlphaPaint;
    private Paint mMaskPaint;
    private Bitmap mRenderMaskBitmap;
    private Bitmap mRenderUnmaskBitmap;
    private Bitmap mMaskBitmap;
    private boolean isCheck = false;


    public CheckLayout(Context context) {
        super(context);
    }

    public CheckLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CheckLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CheckLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }


    public boolean getStatus(){
        return isCheck;
    }

    public void setStatus(boolean isCheck){
        Log.d(TAG,"setStatus:" + isCheck);

        this.isCheck = isCheck;
        invalidate();
        if (!this.isCheck){
            recycleBitmaps();
        }
    }

    private void init(){
        mAlphaPaint = new Paint();
        mAlphaPaint.setAlpha(120);
        mMaskPaint = new Paint();
        mMaskPaint.setAntiAlias(true);
        mMaskPaint.setDither(true);
        mMaskPaint.setFilterBitmap(true);
        mMaskPaint.setXfermode(DST_IN);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        init();
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        if (isCheck){
            dispatchDrawUsingBitmap(canvas);
        }else {
            super.dispatchDraw(canvas);
        }

    }

    private void dispatchDrawUsingBitmap(Canvas canvas){
        Bitmap unmaskBitmap = getRenderUnmaskBitmap();
        Bitmap maskBitmap = getRenderMaskBitmap();

        drawUnmaskBitmap(new Canvas(unmaskBitmap));
        canvas.drawBitmap(unmaskBitmap, 0, 0, mAlphaPaint);
        drawMaskBitmap(new Canvas(maskBitmap));
        canvas.drawBitmap(maskBitmap, 0, 0, null);
    }

    private Bitmap getRenderMaskBitmap(){
        if (mRenderMaskBitmap == null){
            mRenderMaskBitmap = createBitmap(getWidth(),getHeight());
        }
        return mRenderMaskBitmap;
    }

    private Bitmap getRenderUnmaskBitmap(){
        if (mRenderUnmaskBitmap == null){
            mRenderUnmaskBitmap = createBitmap(getWidth(),getHeight());
        }
        return mRenderUnmaskBitmap;
    }

    private void drawUnmaskBitmap(Canvas ubCanvas){
        ubCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
        super.dispatchDraw(ubCanvas);
    }

    private void drawMaskBitmap(Canvas mbCanvas){
        Bitmap mask = getMaskBitmap();
        if (mask == null){
            Log.e(TAG,"maskBitmap == null");
            return;
        }
        mbCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
        super.dispatchDraw(mbCanvas);
        Rect src = new Rect(0, 0, mask.getWidth(), mask.getHeight());
        Rect dst = new Rect(0, 0, getWidth(), getHeight());
        mbCanvas.drawBitmap(mask, src, dst, mMaskPaint);
    }

    private Bitmap getMaskBitmap(){
        if (mMaskBitmap != null && !mMaskBitmap.isRecycled()){
            return mMaskBitmap;
        }
        mMaskBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_check);
        return mMaskBitmap;
    }

    private Bitmap createBitmap(int width, int height){
        return Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
    }

    private void recycleBitmaps(){
        if (mMaskBitmap != null){
            mMaskBitmap.recycle();
            mMaskBitmap = null;
        }

        if (mRenderMaskBitmap != null){
            mRenderMaskBitmap.recycle();
            mRenderMaskBitmap = null;
        }

        if (mRenderUnmaskBitmap != null){
            mRenderUnmaskBitmap.recycle();
            mRenderUnmaskBitmap = null;
        }
    }
}
