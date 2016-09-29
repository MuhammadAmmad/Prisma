package com.compilesense.liuyi.prisma.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.support.v4.util.LruCache;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.compilesense.liuyi.prisma.R;
import com.compilesense.liuyi.prisma.javabean.ImageBean;
import com.compilesense.liuyi.prisma.util.NativeImageLoader;
import com.compilesense.liuyi.prisma.util.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO:利用多线程优化用户体验
 * Created by shenjingyuan002 on 16/9/26.
 */

public class LibRecycleViewAdapter extends RecyclerView.Adapter<LibRecycleViewAdapter.LibRcyViewHolder> {
    private final static String TAG = "LibRecycleViewAdapter";
    private LruCache<String, Bitmap> mMemoryCache;
    private Context mContext;
    private OnItemClickListener mListener;
    public List<ImageBean> imageBeanList = new ArrayList<>();

    public LibRecycleViewAdapter(Context context,OnItemClickListener listener){
        mContext = context;
        mListener = listener;
//        initLruCache();
    }

    private void initLruCache(){
        // 获取到可用内存的最大值，使用内存超出这个值会引起OutOfMemory异常。
        // LruCache通过构造函数传入缓存值，以KB为单位。
        int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        // 使用最大可用内存值的1/4作为缓存的大小。
        int cacheSize = maxMemory / 4;
        Log.d(TAG,"maxM:"+maxMemory+"kb,cacheSize:"+cacheSize+"kb");
        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                // 重写此方法来衡量每张图片的大小，默认返回图片数量。
                return bitmap.getByteCount() / 1024;
            }
        };
    }

    private void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (key == null || bitmap == null){
            return;
        }

        if (getBitmapFromMemCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    private Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
    }

    private void loadBitmap(String path, ImageView imageView) {
        String imageKey = String.valueOf(path);
        Bitmap bitmap = getBitmapFromMemCache(imageKey);
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
        } else {
            imageView.setImageResource(R.drawable.yao1);
            BitmapFactory.Options options = new BitmapFactory.Options();
            int inSampleSize = Utils.calculateInSampleSize(path, imageView);
            options.inSampleSize = inSampleSize;
            bitmap = BitmapFactory.decodeFile(path, options);
            addBitmapToMemoryCache(path, bitmap);
            imageView.setImageBitmap(bitmap);
        }
    }

//    class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap> {
//        // 在后台加载图片。
//        @Override
//        protected Bitmap doInBackground(String... params) {
//            BitmapFactory.Options options = new BitmapFactory.Options();
//            options.inSampleSize = Utils.calculateInSampleSize(params[0],)
//
//            final Bitmap bitmap = BitmapFactory.decodeFile(params[0], options);
//            addBitmapToMemoryCache(String.valueOf(params[0]), bitmap);
//            return bitmap;
//        }
//    }

    public void setImageModelList(List<ImageBean> imageBeanList) {
        this.imageBeanList = imageBeanList;
        notifyDataSetChanged();
    }
    public void addImageModelList(List<ImageBean> imageBeanList){
        this.imageBeanList.addAll(imageBeanList);
        notifyDataSetChanged();
    }

    @Override
    public LibRcyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_lib_rc,parent,false);
        return new LibRcyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final LibRcyViewHolder holder, int position) {
        ImageBean imageBean = imageBeanList.get(position);
//        loadBitmap(imageBean.path,holder.imageView);
        Point point = new Point();
        point.x = holder.imageView.getMaxWidth();
        point.y = holder.imageView.getMaxHeight();
        NativeImageLoader.getInstance().loadNativeImage(imageBean.path, point,new NativeImageLoader.NativeImageCallBack() {
            @Override
            public void onImageLoader(Bitmap bitmap, String path) {
                holder.imageView.setImageBitmap(bitmap);
            }
        });
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onClick(holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return imageBeanList.size();
    }

    class LibRcyViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView ;
        LibRcyViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.img_item_lib_rc);
        }
    }

    public interface OnItemClickListener{
        void onClick(int position);
    }
}
