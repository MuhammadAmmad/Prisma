package com.compilesense.liuyi.prisma.adapter;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.compilesense.liuyi.prisma.R;
import com.compilesense.liuyi.prisma.javabean.ImageBean;
import com.compilesense.liuyi.prisma.util.NativeImageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shenjingyuan002 on 16/9/28.
 */

public class RecentlyAddedRecViewAdapter extends RecyclerView.Adapter<RecentlyAddedRecViewAdapter.RARecViewHolder>{
    private final static String TAG = "RecentAddRecViewAdapter";
    public List<ImageBean> mImageBeanList = new ArrayList<>();
    private OnItemClickListener mListener;

    public RecentlyAddedRecViewAdapter(OnItemClickListener listener){
        mListener = listener;
    }

    public void addImages(List<ImageBean> been){
        mImageBeanList.addAll(been);
        notifyDataSetChanged();
    }

    @Override
    public RARecViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recent_add_rc,parent,false);
        return new RARecViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RARecViewHolder holder, int position) {
        holder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"setOnClickListener");
                Log.d(TAG,"mListener==null:"+(mListener==null));
                mListener.onClick(holder.getAdapterPosition());
            }
        });

        holder.textView.setText(mImageBeanList.get(position).name);

        NativeImageLoader.getInstance()
                .loadNativeImage(mImageBeanList.get(position).path,
                        new NativeImageLoader.NativeImageCallBack() {
                            @Override
                            public void onImageLoader(Bitmap bitmap, String path) {
                                holder.imageView.setImageBitmap(bitmap);
                            }
                        });

    }

    @Override
    public int getItemCount() {
        return mImageBeanList.size();
    }

    class RARecViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        TextView textView;
        View itemView;
        RARecViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.img_item_recent_add_rc);
            textView = (TextView) itemView.findViewById(R.id.tx_item_recent_add_rc);
            this.itemView = itemView;
        }
    }

    public interface OnItemClickListener{
        void onClick(int position);
    }
}
