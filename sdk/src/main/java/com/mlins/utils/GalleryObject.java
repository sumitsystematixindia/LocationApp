package com.mlins.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;

public class GalleryObject {
    public static String HEAD_TYPE = "head";
    public static String GALLERY_TYPE = "gallery";

    private String name = "";
    private String poi_id = "";
    private String uri = "";
    private String md5 = "";
    private String type = "";
    private Bitmap bitmap = null;

    public GalleryObject(String name, String poi_id, String uri, String md5, String type) {
        this.name = name;
        this.poi_id = poi_id;
        this.uri = uri;
        this.md5 = md5;
        this.type = type;

    }


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


    public String getPoiId() {
        return poi_id;
    }


    public void setPoiId(String poi_id) {
        this.poi_id = poi_id;
    }


    public String getUri() {
        return uri;
    }


    public void setUri(String uri) {
        this.uri = uri;
    }


    public String getMd5() {
        return md5;
    }


    public void setMd5(String md5) {
        this.md5 = md5;
    }


    public String getType() {
        return type;
    }


    public void setType(String type) {
        this.type = type;
    }

    public void recycleBitmap() {
        try {
            if (this.bitmap != null) {
                bitmap.recycle();
                bitmap = null;
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(byte[] bytes) {
        Bitmap bmp = null;

        recycleBitmap();

        if (bytes != null) {
            Options opts = new Options();
            opts.inPreferredConfig = Bitmap.Config.ARGB_4444; // or RGB_565
            // (without
            // transparency).
            opts.inDither = false;                     //Disable Dithering mode
            opts.inPurgeable = true;                   //Tell to gc that whether it needs free memory, the Bitmap can be cleared
            opts.inInputShareable = true;              //Which kind of reference will be used to recover the Bitmap data after being clear, when it will be used in the future
            opts.inTempStorage = new byte[64 * 1024];

            bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, opts);
        }
        this.bitmap = bmp;

    }


}
