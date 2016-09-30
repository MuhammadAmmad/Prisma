package com.compilesense.liuyi.prisma.fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.compilesense.liuyi.prisma.DetectionBasedTracker;
import com.compilesense.liuyi.prisma.R;
import com.compilesense.liuyi.prisma.activity.StylesActivity;
import com.compilesense.liuyi.prisma.adapter.StyleRecViewAdapter;
import com.compilesense.liuyi.prisma.javabean.ImageBean;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.opencv.android.CameraBridgeViewBase.CAMERA_ID_BACK;
import static org.opencv.android.CameraBridgeViewBase.CAMERA_ID_FRONT;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnPhotoFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class PhotoFragment extends Fragment {
    private final static String TAG = "PhotoFragment";

    private Mat mRgba;
    private Mat mGray;
    private static final Scalar FACE_RECT_COLOR     = new Scalar(0, 255, 0, 255);
    public static final int        JAVA_DETECTOR       = 0;
    public static final int        NATIVE_DETECTOR     = 1;

    private int mDetectorType = JAVA_DETECTOR;

    private float mRelativeFaceSize = 0.2f;
    private int mAbsoluteFaceSize = 0;

    //和脸部检测相关
    private File mCascadeFile;
    private CascadeClassifier mJavaDetector;
    private DetectionBasedTracker mNativeDetector;

    //openCV 提供的java摄像头预览类
    private JavaCameraView mCameraView;
    //openC native库加载回调
    private BaseLoaderCallback mLoaderCallback;

    private OnPhotoFragmentInteractionListener mListener;

    public PhotoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_photo, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnPhotoFragmentInteractionListener) {
            mListener = (OnPhotoFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnPhotoFragmentInteractionListener");
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
        initBaseLoader(getContext());
        initCameraView();
    }
    @Override
    public void onResume() {
        super.onResume();
        if (OpenCVLoader.initDebug()) {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        } else {
            Log.e(TAG,"未能成功加载openCV本地库");
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if ( mCameraView != null){
            mCameraView.disableView();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    void detectImage(Mat image){
        Log.d(TAG,"detectImage");
        int height = image.height();
        mAbsoluteFaceSize = Math.round(height * mRelativeFaceSize);
        mNativeDetector.setMinFaceSize(mAbsoluteFaceSize);

        MatOfRect faces = new MatOfRect();
        mNativeDetector.detect(mGray, faces);
        Rect[] facesArray = faces.toArray();

        Log.d(TAG,"facesArray.length:"+facesArray.length);
//        for (int i = 0; i < facesArray.length; i++)
//            Imgproc.rectangle(mRgba, facesArray[i].tl(), facesArray[i].br(), FACE_RECT_COLOR, 3);

    }

    void initCameraView(){
        mCameraView = (JavaCameraView) getView().findViewById(R.id.jc_photo);
        mCameraView.setCameraIndex(CAMERA_ID_BACK);
        mCameraView.setVisibility(CameraBridgeViewBase.VISIBLE);
        mCameraView.setCvCameraViewListener(new CameraBridgeViewBase.CvCameraViewListener2() {
            @Override
            public void onCameraViewStarted(int width, int height) {
                mGray = new Mat();
                mRgba = new Mat();
            }

            @Override
            public void onCameraViewStopped() {
                mGray.release();
                mRgba.release();
            }

            @Override
            public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
                mRgba = inputFrame.rgba();
                mGray = inputFrame.gray();

                if (mAbsoluteFaceSize == 0) {
                    int height = mGray.rows();
                    if (Math.round(height * mRelativeFaceSize) > 0) {
                        mAbsoluteFaceSize = Math.round(height * mRelativeFaceSize);
                    }
                    mNativeDetector.setMinFaceSize(mAbsoluteFaceSize);
                }

                MatOfRect faces = new MatOfRect();

                if (mDetectorType == JAVA_DETECTOR) {
                    if (mJavaDetector != null)
                        mJavaDetector.detectMultiScale(mGray, faces, 1.1, 2, 2, // TODO: objdetect.CV_HAAR_SCALE_IMAGE
                                new Size(mAbsoluteFaceSize, mAbsoluteFaceSize), new Size());
                }
                else if (mDetectorType == NATIVE_DETECTOR) {
                    if (mNativeDetector != null)
                        mNativeDetector.detect(mGray, faces);
                }
                else {
                    Log.e(TAG, "Detection method is not selected!");
                }

                Rect[] facesArray = faces.toArray();

                Log.d(TAG,"facesArray.length:"+facesArray.length);

                for (int i = 0; i < facesArray.length; i++){
                    Imgproc.rectangle(mRgba, facesArray[i].tl(), facesArray[i].br(), FACE_RECT_COLOR, 3);
                    Log.d(TAG,"drawRec:"+ facesArray[i].toString());
                    Log.d(TAG,"mRgba:"+ mRgba.toString());
                }


                return mRgba;
            }

        });
    }

    void initBaseLoader(Context context){
        mLoaderCallback = new BaseLoaderCallback(context) {
            @Override
            public void onManagerConnected(int status) {
                switch (status) {
                    case LoaderCallbackInterface.SUCCESS:
                    {
                        Log.i(TAG, "OpenCV loaded successfully");

                        // Load native library after(!) OpenCV initialization
                        System.loadLibrary("detection_based_tracker");

                        try {
                            // load cascade file from application resources
                            InputStream is = getResources().openRawResource(R.raw.lbpcascade_frontalface);
                            File cascadeDir = getContext().getDir("cascade", Context.MODE_PRIVATE);
                            mCascadeFile = new File(cascadeDir, "lbpcascade_frontalface.xml");
                            FileOutputStream os = new FileOutputStream(mCascadeFile);

                            byte[] buffer = new byte[4096];
                            int bytesRead;
                            while ((bytesRead = is.read(buffer)) != -1) {
                                os.write(buffer, 0, bytesRead);
                            }
                            is.close();
                            os.close();

                            mJavaDetector = new CascadeClassifier(mCascadeFile.getAbsolutePath());
                            if (mJavaDetector.empty()) {
                                Log.e(TAG, "Failed to load cascade classifier");
                                mJavaDetector = null;
                            } else
                                Log.i(TAG, "Loaded cascade classifier from " + mCascadeFile.getAbsolutePath());

                            mNativeDetector = new DetectionBasedTracker(mCascadeFile.getAbsolutePath(), 0);

                            cascadeDir.delete();

                        } catch (IOException e) {
                            e.printStackTrace();
                            Log.e(TAG, "Failed to load cascade. Exception thrown: " + e);
                        }
                        mNativeDetector.start();
                        mCameraView.enableView();//使能cameraView
                    } break;
                    default:
                    {
                        super.onManagerConnected(status);
                    } break;
                }
            }
        };
    }

    void initViews(){
        ImageButton changeCameraID = (ImageButton) getView().findViewById(R.id.ibt_change_cameraID);
        changeCameraID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int index = mCameraView.getCameraIndex();
                index = index == CAMERA_ID_BACK ? CAMERA_ID_FRONT : CAMERA_ID_BACK;
                mCameraView.setCameraIndex(index);
                mCameraView.setVisibility(View.GONE);
                mCameraView.setVisibility(View.VISIBLE);
            }
        });

        final ImageView imageView = (ImageView) getView().findViewById(R.id.img_photo);

        ImageButton capture = (ImageButton) getView().findViewById(R.id.ibt_capture);
        capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Mat p  = mRgba.clone();

                if (mCameraView.getCameraIndex() == CAMERA_ID_BACK){
                    Core.flip(p.t(),p, 1);
                }else {
                    Core.flip(p.t(), p, 0);
                }

                Bitmap bitmap = Bitmap.createBitmap(p.width(),p.height(), Bitmap.Config.ARGB_8888);
                Utils.matToBitmap(p,bitmap);
                mCameraView.setVisibility(View.GONE);
                imageView.setVisibility(View.VISIBLE);
                imageView.setImageBitmap(bitmap);
                String path = com.compilesense.liuyi.prisma.util.Utils.saveImageToGallery(getContext(),bitmap);
                ImageBean imageBean = new ImageBean();
                imageBean.path = path;
                StylesActivity.startStylesActivity(getActivity(),imageBean);
                bitmap.recycle();
                p.release();
                mCameraView.setVisibility(View.VISIBLE);
                imageView.setVisibility(View.GONE);
            }
        });
    }

    public interface OnPhotoFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
