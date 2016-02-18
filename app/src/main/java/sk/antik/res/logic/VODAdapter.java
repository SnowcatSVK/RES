package sk.antik.res.logic;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import sk.antik.res.R;

/**
 * Created by Admin on 20.01.2016.
 */
public class VODAdapter extends ArrayAdapter<VOD> {

    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
    private DisplayImageOptions options;

    public VODAdapter(Context context, ArrayList<VOD> vods) {
        super(context, R.layout.item_vod, vods);
        options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .resetViewBeforeLoading(true)
                .displayer(new SimpleBitmapDisplayer())
                .build();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final VOD vod = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_vod, parent, false);
        }
        TextView name = (TextView) convertView.findViewById(R.id.name_textView);
        ImageView image = (ImageView) convertView.findViewById(R.id.vod_imageView);

        name.setText(vod.getName());
        /*if (vod.getSource() == null) {
            image.setImageResource(R.drawable.vod1);
        } else {
            image.setImageResource(R.drawable.vod3);
        }*/
        ImageLoader.getInstance().displayImage(vod.getImgSource(), image, options, animateFirstListener);

        return convertView;
    }

    private static class AnimateFirstDisplayListener extends SimpleImageLoadingListener {

        static final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());

        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            if (loadedImage != null) {
                ImageView imageView = (ImageView) view;
                boolean firstDisplay = !displayedImages.contains(imageUri);
                if (firstDisplay) {
                    FadeInBitmapDisplayer.animate(imageView, 0);
                    displayedImages.add(imageUri);
                }
            }
        }
    }
}
