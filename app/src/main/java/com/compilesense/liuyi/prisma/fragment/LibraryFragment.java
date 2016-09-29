package com.compilesense.liuyi.prisma.fragment;


import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.compilesense.liuyi.prisma.R;
import com.compilesense.liuyi.prisma.adapter.LibRecycleViewAdapter;
import com.compilesense.liuyi.prisma.javabean.ImageBean;
import com.facebook.drawee.view.SimpleDraweeView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class LibraryFragment extends Fragment {
    private static final String TAG = "LibraryFragment";
    //The number of columns or rows in the grid;
    private static final int ROW_COUNT = 4;

    private List<ImageBean> list = new ArrayList<ImageBean>();
    private LibRecycleViewAdapter mAdapter;
    private View mFragmentView;
    private View mProgressBar;
    private SimpleDraweeView mImageView;
    private static final String[] STORE_IMAGES = {
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media._ID
    };
    private List<String> mImagesPath = new ArrayList<>();
    private final static int IMAGE_PATH_HAD_LOAD = 12;
    private boolean isLoading = false;
    public ImageBean currentImageBean;
    private OnLibFragmentInteractionListener mListener;

    public LibraryFragment() {
        // Required empty public constructor
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case IMAGE_PATH_HAD_LOAD:
                    addImages(20,0);
                    if (currentImageBean == null){
                        setCurrentImageBean(mAdapter.imageBeanList.get(0));
                    }else {
                        setCurrentImageBean(currentImageBean);
                    }
                    mProgressBar.setVisibility(View.GONE);
                    break;
            }
        }
    };

    public void setCurrentImageBean(ImageBean imageBean){
        currentImageBean = imageBean;
        mImageView.setImageURI(currentImageBean.getUriString());
        mListener.onLibFragmentInteraction(imageBean);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnLibFragmentInteractionListener){
            mListener = (OnLibFragmentInteractionListener) context;
        }else {
            throw new RuntimeException(context.toString()
                    + " must implement OnLibFragmentInteractionListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG,"onCreateView");
        mFragmentView = inflater.inflate(R.layout.fragment_library, container, false);
        return mFragmentView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG,"onActivityCreated");
        initView(mFragmentView);
        initLoader();
    }

    void initView(View view){
        mImageView = (SimpleDraweeView) view.findViewById(R.id.img_lib);
        mProgressBar = view.findViewById(R.id.progress_bar);
        mAdapter = new LibRecycleViewAdapter(getActivity(), new LibRecycleViewAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                ImageBean imageBean = mAdapter.imageBeanList.get(position);
                setCurrentImageBean(imageBean);
            }
        });
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.rv_lib);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),ROW_COUNT));
        recyclerView.setHasFixedSize(true);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                GridLayoutManager layoutManager = (GridLayoutManager)recyclerView.getLayoutManager();
                int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
                if (lastVisibleItemPosition > mAdapter.getItemCount() - 5){
                    if (isLoading){
                        return;
                    }
                    addImages(20, mAdapter.getItemCount());
                    Log.d(TAG,"load more:offset:" + mAdapter.getItemCount());
                }
            }
        });
        recyclerView.setAdapter(mAdapter);
    }

    void initLoader(){
        getLoaderManager().initLoader(0, null, new LoaderManager.LoaderCallbacks<Cursor>(){

            @Override
            public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                int loaderLimit = 100, loaderOffset = 0;
                return new CursorLoader(
                        getActivity(),
                        MediaStore.Images.Media.getContentUri("external"),
                        STORE_IMAGES,
                        null,
                        null,
                        MediaStore.Images.ImageColumns.DATE_ADDED + " DESC LIMIT "+loaderLimit+" OFFSET "+loaderOffset);
            }

            @Override
            public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

                if (data.moveToFirst()) {
                    final int dataColumn = data.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    do {
                        final String dataString = data.getString(dataColumn);
                        mImagesPath.add(dataString);
                    } while (data.moveToNext());
                }
                handler.sendEmptyMessage(IMAGE_PATH_HAD_LOAD);
            }
            @Override
            public void onLoaderReset(Loader<Cursor> loader) {

            }
        });
    }

    void addImages(int limit, int offset){
        isLoading = true;
        List<ImageBean> imageBeen = new ArrayList<>();
        for (int i = offset; i < offset + limit; i++){
            ImageBean imageBean = new ImageBean();
            imageBean.path = mImagesPath.get(i);
            imageBeen.add(imageBean);
        }
        mAdapter.addImageModelList(imageBeen);
        isLoading = false;
    }

    public interface OnLibFragmentInteractionListener {
        void onLibFragmentInteraction(ImageBean imageBean);
    }
}
