package com.mlins.list;


import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.mlins.utils.ResourceDownloader;
import com.spreo.spreosdk.R;

import java.util.List;

/*Helper List Adapter class to populate custom list 
 * */
public class ImageAndTextListAdapter extends ArrayAdapter<ImageAndText> {

    private Typeface tf;


    public ImageAndTextListAdapter(Activity activity,
                                   List<ImageAndText> imageAndTexts, ListView listView) {
        super(activity, 0, imageAndTexts);


    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Activity activity = (Activity) getContext();

        // Inflate the views from XML
        View rowView = convertView;
        ViewCache viewCache;
        if (rowView == null) {
            LayoutInflater inflater = activity.getLayoutInflater();
            rowView = inflater.inflate(R.layout.image_and_text_row, null);
            viewCache = new ViewCache(rowView);
            rowView.setTag(viewCache);
        } else {
            viewCache = (ViewCache) rowView.getTag();
        }
        ImageAndText imageAndText = getItem(position);

        // Load the image and set it on the ImageView
        String imageUrl = imageAndText.getImageUrl();
        ImageView imageView = viewCache.getImageView();
//        imageView.setLayoutParams(new GridView.LayoutParams(85, 85));
//        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
//        imageView.setPadding(8, 8, 8, 8);
        imageView.setTag(imageUrl);

        Bitmap cachedImage = ResourceDownloader.getInstance().getLocalBitmap(imageUrl);
        imageView.setImageBitmap(cachedImage);

        // Set the text on the TextView
        TextView textView = viewCache.getTextView();
//    tf = PropertyHolder.getInstance().getClalitFont();
//    textView.setTypeface(tf);
        textView.setText(imageAndText.getText());

//    if (imageAndText.isselected())
//    {
//    	  Resources res = rowView.getResources();
//    		Drawable drawable = res.getDrawable(R.drawable.itmbckgrnd5slctd);
//    		rowView.setBackgroundDrawable(drawable);
//    } else {
//    	Resources res = rowView.getResources();
//		Drawable drawable = res.getDrawable(R.drawable.itmbckgrnd5);
//		rowView.setBackgroundDrawable(drawable);
//    }


        return rowView;
    }
}
