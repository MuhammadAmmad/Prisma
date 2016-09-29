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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.compilesense.liuyi.prisma.R;
import com.compilesense.liuyi.prisma.adapter.RecentlyAddedRecViewAdapter;
import com.compilesense.liuyi.prisma.javabean.ImageBean;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnRAFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class RecentlyAddedFragment extends Fragment {
    private final static String TAG = "RecentlyAddedFragment";
    private OnRAFragmentInteractionListener mListener;
    private RecentlyAddedRecViewAdapter mAdapter;
    private static final String[] STORE_IMAGES = {
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.TITLE
    };
    private List<String> mDataList = new ArrayList<>();
    private List<String> mNameList = new ArrayList<>();
    private final static int IMAGE_PATH_HAD_LOAD = 112;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case IMAGE_PATH_HAD_LOAD:
                    addImages(20,0);
                    break;
            }
        }
    };

    public RecentlyAddedFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnRAFragmentInteractionListener) {
            mListener = (OnRAFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnRAFragmentInteractionListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_recently_added, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void init(){
        initLoader();
        initRecycleView();
    }

    private void initRecycleView(){
        mAdapter = new RecentlyAddedRecViewAdapter(new RecentlyAddedRecViewAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                if (mListener != null){
                    mListener.onRAFragmentInteraction(mAdapter.mImageBeanList.get(position));
                }
            }
        });
        RecyclerView recyclerView = (RecyclerView) getView().findViewById(R.id.rc_recentlyAdded);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
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
                    final int nameColumn = data.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME);
                    do {
                        String dataString = data.getString(dataColumn);
                        mDataList.add(dataString);
                        String nameString = data.getString(nameColumn);
                        mNameList.add(nameString);
                    } while (data.moveToNext());
                }
                mHandler.sendEmptyMessage(IMAGE_PATH_HAD_LOAD);
            }
            @Override
            public void onLoaderReset(Loader<Cursor> loader) {

            }
        });
    }

    void addImages(int limit, int offset){
//        isLoading = true;
        List<ImageBean> imageBeen = new ArrayList<>();
        for (int i = offset; i < offset + limit; i++){
            ImageBean imageBean = new ImageBean();
            imageBean.path = mDataList.get(i);
            imageBean.name = mNameList.get(i);
            imageBeen.add(imageBean);
        }
        mAdapter.addImages(imageBeen);
//        isLoading = false;
    }

    public interface OnRAFragmentInteractionListener {
        void onRAFragmentInteraction(ImageBean imageBean);
    }
}
