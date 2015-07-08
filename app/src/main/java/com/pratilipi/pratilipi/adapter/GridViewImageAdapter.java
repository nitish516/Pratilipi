package com.pratilipi.pratilipi.adapter;

/**
 * Created by Nitish on 01-04-2015.
 */

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.pratilipi.pratilipi.DataFiles.Metadata;
import com.pratilipi.pratilipi.DetailPageActivity;
import com.pratilipi.pratilipi.R;
import com.pratilipi.pratilipi.helper.AppConstant;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.ArrayList;

public class GridViewImageAdapter extends BaseAdapter {

    private Activity _activity;
    private ArrayList<Metadata> _filePaths = new ArrayList<Metadata>();
    private int imageWidth;

    public GridViewImageAdapter(Activity activity, ArrayList<Metadata> filePaths,
                                int imageWidth) {
        this._activity = activity;
        this._filePaths = filePaths;
        this.imageWidth = imageWidth;
    }

    @Override
    public int getCount() {
       return this._filePaths.size();
//        return 12;
    }

    @Override
    public Object getItem(int position) {
        return this._filePaths.get(position);
    }

    @Override
    public long getItemId(int position) { return position; }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            imageView = new ImageView(_activity);
        } else {
            imageView = (ImageView) convertView;
        }

        String filePath = android.os.Environment.getExternalStorageDirectory()
                + File.separator + AppConstant.PHOTO_ALBUM;
        // get screen dimensions
        Bitmap image = decodeFile(android.os.Environment.getExternalStorageDirectory()
        + File.separator + AppConstant.PHOTO_ALBUM+ File.separator +_filePaths.get(position).get_pid()+".jpg", imageWidth, imageWidth+50);

        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setLayoutParams(new GridView.LayoutParams(imageWidth,
                imageWidth));
        imageView.setImageBitmap(image);

        imageView.setOnClickListener(new OnImageClickListener(position,String.valueOf(imageView.getTag())));

        return imageView;
    }

    class OnImageClickListener implements OnClickListener {

        int _postion;
        String _pId;

        // constructor
        public OnImageClickListener(int position,String pid) {
            this._postion = position;
            this._pId = pid;

        }

        @Override
        public void onClick(View v) {
            // on selecting grid view image
            // launch full screen activity
            Intent i = new Intent(_activity, DetailPageActivity.class);
            i.putExtra(DetailPageActivity.METADATA, (Serializable) _filePaths.get(_postion));
            _activity.startActivity(i);
        }

    }

    /*
     * Resizing image size
     */
    public static Bitmap decodeFile(String filePath, int WIDTH, int HIGHT) {
        try {

            File f = new File(filePath);

            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f), null, o);

            final int REQUIRED_WIDTH = WIDTH;
            final int REQUIRED_HIGHT = HIGHT;
            int scale = 1;
            while (o.outWidth / scale / 2 >= REQUIRED_WIDTH
                    && o.outHeight / scale / 2 >= REQUIRED_HIGHT)
                scale *= 2;

            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

}
