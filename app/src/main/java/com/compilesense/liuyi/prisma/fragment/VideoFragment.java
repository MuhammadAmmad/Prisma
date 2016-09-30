package com.compilesense.liuyi.prisma.fragment;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.compilesense.liuyi.prisma.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class VideoFragment extends Fragment {


    public VideoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_video, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
    }

    void init(){
        Button openWebSite = (Button) getView().findViewById(R.id.bt_open_web);
        openWebSite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                Uri content_url = Uri.parse("http://www.glasssix.com");
                intent.setData(content_url);
                startActivity(intent);
            }
        });

        Button sendEmail = (Button) getView().findViewById(R.id.bt_send_email);
        sendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent data=new Intent(Intent.ACTION_SENDTO);
                data.setData(Uri.parse("mailto:seven@glasssix.com"));
                data.putExtra(Intent.EXTRA_SUBJECT, "prisma建议");
                data.putExtra(Intent.EXTRA_TEXT,"");
                startActivity(data);
            }
        });
    }

}
