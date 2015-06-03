package com.example.paolosalvati.demo.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.paolosalvati.demo.R;
import com.example.paolosalvati.demo.spotifyDataClasses.SpotifyPlaylistObject;

import java.io.InputStream;
import java.net.URL;
import java.util.List;

/**
 * Created by Paolo on 28/01/2015.
 */
public class PlaylistHostAdapetr extends BaseAdapter {

    List<SpotifyPlaylistObject> playlist=null;
    private Context context;
    public PlaylistHostAdapetr(List<SpotifyPlaylistObject> playlist){

        this.playlist=playlist;
        this.context=context;
    }

    @Override
    public int getCount() {
        return playlist.size();
    }

    @Override
    public Object getItem(int position) {
        return playlist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return playlist.get(position).getID().hashCode();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater=(LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if(convertView==null){
            convertView=inflater.inflate(R.layout.play_list_layout,null);
        }
        TextView nomePlaylist=(TextView)convertView.findViewById(R.id.layout_playlist_name);
        nomePlaylist.setText(playlist.get(position).getName());


        //Typeface font = Typeface.createFromAsset(context.getAssets(), "MyUnderwood.ttf");
        // Setting the name of the case
       // nomePlaylist.setTypeface(font);


        ImageView imgView =(ImageView)convertView.findViewById(R.id.layout_playlist_imageView);
        //Drawable drawable = LoadImageFromWebOperations("https://mosaic.scdn.co/640/39232c45137108113aded29eec90360f8ff6ff37fbc273de5254592f58a12c3120b8c0320c7d15247e9d54bc244c9e41718b88275f9b49506236d61627cd96280d5f5432cd7d5da6503e7cb848995f59");
        Drawable drawable = LoadImageFromWebOperations(playlist.get(position).getImageUri());

        imgView.setImageDrawable(drawable);


        return convertView;
    }


    public Drawable LoadImageFromWebOperations(String url)
    {
        try
        {
            InputStream is = (InputStream) new URL(url).getContent();
            Drawable d = Drawable.createFromStream(is, "src name");
            return d;
        }catch (Exception e) {
            System.out.println("Exc="+e);
            return null;
        }
    }


}
