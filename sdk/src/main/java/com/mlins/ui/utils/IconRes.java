package com.mlins.ui.utils;


import com.mlins.utils.PropertyHolder;
import com.mlins.utils.ServerConnection;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.List;


public class IconRes {

    public static String MD5 = null;
    private int autoIndex = -1;
    private String name = null;
    private String imageUrl = null;


    public IconRes() {
        super();
    }


    public static List<IconRes> parseJson(String content) {

        List<IconRes> list = new ArrayList<IconRes>();

        try {

            JSONTokener jsonTokener = new JSONTokener(content);

            JSONObject gObj = (JSONObject) jsonTokener.nextValue();


            MD5 = gObj.getString("md5");

            JSONArray iconsJarr = gObj.getJSONArray("icons");

            for (int i = 0; i < iconsJarr.length(); i++) {


                JSONObject icRes = iconsJarr.getJSONObject(i);
                IconRes icon = new IconRes();

                try {
                    int autoIndex = icRes.getInt("index");
                    icon.setAutoIndex(autoIndex);
                } catch (Exception e) {
                    System.out.println("IconRes: error key index");
                    e.printStackTrace();
                }


                try {
                    String id = icRes.getString("name");
                    icon.setName(id);
                } catch (Exception e) {
                    System.out.println("IconRes: error key name");
                    e.printStackTrace();
                }


                try {
                    String imgUri = icRes.getString("img_uri");
                    icon.setImageUrl(imgUri);
                } catch (Exception e) {
                    System.out.println("IconRes: error key img_uri");
                    e.printStackTrace();
                }

                list.add(icon);

            }

        } catch (Throwable e) {
            e.printStackTrace();
        }

        return list;


    }


    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }


    public int getAutoIndex() {
        return autoIndex;
    }

    public void setAutoIndex(int autoIndex) {
        this.autoIndex = autoIndex;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public byte[] downloadImg() {

        byte[] imgBlob = new byte[0];

        try {

            String server = PropertyHolder.getInstance().getServerName();
            String projectid = PropertyHolder.getInstance().getProjectId();
            String url = server + "res/" + projectid + "/" + getImageUrl();
            imgBlob = ServerConnection.getInstance().getResourceBytes(url);

            if (imgBlob == null) {
                imgBlob = new byte[0];
            }


        } catch (Throwable t) {
            t.printStackTrace();
        }

        return imgBlob;

    }


}
