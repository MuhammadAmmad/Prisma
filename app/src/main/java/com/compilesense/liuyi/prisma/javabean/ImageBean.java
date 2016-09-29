package com.compilesense.liuyi.prisma.javabean;

import android.net.Uri;

/**
 * Created by shenjingyuan002 on 16/9/26.
 */
public class ImageBean {
    public String path;
    public String name;

    public String getUriString(){
        return "file://" + path;
    }
}
