package com.ymg.beststatusandquotes.Adapter;


import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ymg.beststatusandquotes.Model.Category;
import com.ymg.beststatusandquotes.R;
import com.ymg.beststatusandquotes.Utils.RoundImage;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;


public class CategoriesListAdapter extends ArrayAdapter<Category> {
    Context context;
    int layoutResourceId;
    private int lastPosition = -1;
    private RoundImage roundedImage;
    ArrayList<Category> data = new ArrayList<Category>();

    public CategoriesListAdapter(Context context, int layoutResourceId,
                                 ArrayList<Category> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View row = convertView;

        ImageHolder holder = null;
        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new ImageHolder();
            holder.txtName = (TextView) row.findViewById(R.id.CatName);
            holder.imgCat = (ImageView) row.findViewById(R.id.imgCat);
            holder.txtCounter = (TextView) row.findViewById(R.id.txtCounter);
            holder.relativeLayout = row.findViewById(R.id.llBackground);

            row.setTag(holder);
        } else {
            holder = (ImageHolder) row.getTag();
        }

        Category picture = data.get(position);
        holder.txtName.setText(picture.getName());
        holder.txtCounter.setText(picture.getCount());

        Random rnd = new Random();
        int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));

        String[] mColors = {
                "#1D5E95",
                "#13D6EB",
                "#9055F7",
                "#D60B77",
                "#7ACB9F",
                "#158BB2",
                "#D75A6C",
                "#F27DBD",
                "#F07146",
                "#7EAB1D",
                "#4F9A9F",
                "#E707A5",
                "#4E0698",
                "#2E5AE1",
                "#25685D",
                "#5C5DBB",
                "#FE6106",
                "#920363",
                "#ECB10B",
                "#0587D5",
                "#C02642",
                "#B50DF9",
                "#E7A1D5",
                "#673AB7"
        };

        holder.relativeLayout.setBackgroundColor(Color.parseColor(mColors[position % 24]));

        //AssetManager assetManager = context.getAssets();
        boolean isExist = false;
        AssetManager assetManager = context.getAssets();
        InputStream imageStream = null;
        try {
            imageStream = assetManager.open("categories/"+picture.getFileName()+".png");

            isExist =true;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        if (isExist != false){
            Bitmap theImage = BitmapFactory.decodeStream(imageStream);
            roundedImage = new RoundImage(theImage);
            holder.imgCat.setImageDrawable(roundedImage);
        }
        else {
            Bitmap bm = BitmapFactory.decodeResource(context.getResources(), R.mipmap.notification);
            roundedImage = new RoundImage(bm);
            holder.imgCat.setImageDrawable(roundedImage);
        }

        return row;
    }

    static class ImageHolder {
        TextView txtCounter;
        ImageView imgCat;
        TextView txtName;
        RelativeLayout relativeLayout;

    }
}
