package com.compilesense.liuyi.prisma.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.compilesense.liuyi.prisma.R;
import com.compilesense.liuyi.prisma.javabean.StyleBean;
import com.compilesense.liuyi.prisma.util.Utils;
import com.compilesense.liuyi.prisma.view.CheckLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shenjingyuan002 on 16/9/28.
 */

public class StyleRecViewAdapter extends RecyclerView.Adapter<StyleRecViewAdapter.StyleRecViewHolder> {
    private final static String TAG = "StyleRecViewAdapter";
    private List<StyleBean> mStyleBeanList = new ArrayList<>();
    private Context mContext;
    private OnItemClickListener mListener;
    private int currentCheckPosition = -1;

    public StyleRecViewAdapter(Context context,OnItemClickListener listener){
        mContext = context;
        mListener = listener;
    }

    public void setStyleBeanList(List<StyleBean> styleBeanList) {
        this.mStyleBeanList = styleBeanList;

    }

    public int getCurrentCheckPosition() {
        return currentCheckPosition;
    }

    public void setCurrentCheckPosition(int currentCheckPosition) {
        if (currentCheckPosition >= mStyleBeanList.size() || currentCheckPosition < 0){
            return;
        }

        if (currentCheckPosition == this.currentCheckPosition){
            return;
        }

        if (this.currentCheckPosition != -1){
            mStyleBeanList.get(this.currentCheckPosition).isCheck = false;
        }

        this.currentCheckPosition = currentCheckPosition;
        mStyleBeanList.get(currentCheckPosition).isCheck = true;

        notifyDataSetChanged();
    }

    @Override
    public StyleRecViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_style_rc, parent, false);
        return new StyleRecViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final StyleRecViewHolder holder, int position) {
        StyleBean styleBean = mStyleBeanList.get(position);
        holder.imageView.setImageResource(styleBean.imageRes);
        holder.textView.setText(styleBean.title);
        holder.checkLayout.setStatus(styleBean.isCheck);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onClick(holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return mStyleBeanList.size();
    }

    class StyleRecViewHolder extends RecyclerView.ViewHolder{
        View itemView;
        ImageView imageView;
        TextView textView;
        CheckLayout checkLayout;
        StyleRecViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            imageView = (ImageView) itemView.findViewById(R.id.img_item_style_rc);
            textView = (TextView) itemView.findViewById(R.id.tx_item_style_rc);
            checkLayout = (CheckLayout) itemView.findViewById(R.id.cl_item_style_rc);
        }
    }

    public interface OnItemClickListener{
        void onClick(int position);
    }
}
