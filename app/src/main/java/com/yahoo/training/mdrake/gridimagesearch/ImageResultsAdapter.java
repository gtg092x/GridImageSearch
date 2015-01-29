package com.yahoo.training.mdrake.gridimagesearch;

import android.content.Context;
import android.text.Html;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by mdrake on 1/28/15.
 */
public class ImageResultsAdapter extends ArrayAdapter<ImageResult> {

    public ImageResultsAdapter(Context context, List<ImageResult> images){
        super(context, R.layout.image_result, images);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ImageResult image = getItem(position);

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.image_result,parent,false);
        }

        ImageView preview = (ImageView)convertView.findViewById(R.id.ivImage);
        TextView previewInfo = (TextView)convertView.findViewById(R.id.tvTitle);

        preview.setImageResource(0);

        Picasso.with(getContext()).load(image.thumbUrl).into(preview);

        previewInfo.setText(Html.fromHtml(image.title));

        return convertView;
    }
}
