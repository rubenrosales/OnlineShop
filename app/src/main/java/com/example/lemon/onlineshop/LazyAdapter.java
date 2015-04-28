package com.example.lemon.onlineshop;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class LazyAdapter extends BaseAdapter {

    private Activity activity;

    private static LayoutInflater inflater=null;
    public ImageLoader imageLoader;

    private String[] artistArray;
    private String[] songArray;
    private String[] artworkArray;
    private String[] priceArray;

    public LazyAdapter(Activity a, String[] artwork,String[] artist, String[] song,  String [] price ) {

        activity = a;
        artworkArray = artwork;
        artistArray = artist;
        songArray = song;
        priceArray = price;

        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageLoader=new ImageLoader(activity.getApplicationContext());
    }

    public int getCount() {
        return artworkArray.length;
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        View vi=convertView;

            if (convertView == null)
                vi = inflater.inflate(R.layout.item, null);

            TextView text = (TextView) vi.findViewById(R.id.text);
            ImageView image = (ImageView) vi.findViewById(R.id.image);
            text.setText(artistArray[position]+ " - " + songArray[position]+ "  "+ priceArray[position]);
            imageLoader.DisplayImage(artworkArray[position], image);


        return vi;
    }
}