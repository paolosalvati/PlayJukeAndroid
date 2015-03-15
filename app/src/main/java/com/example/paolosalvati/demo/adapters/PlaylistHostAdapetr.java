package com.example.paolosalvati.demo.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.paolosalvati.demo.R;
import com.example.paolosalvati.demo.spotifyDataClasses.SpotifyPlaylistObject;

import java.util.List;

/**
 * Created by Paolo on 28/01/2015.
 */
public class PlaylistHostAdapetr extends BaseAdapter {

    List<SpotifyPlaylistObject> playlist=null;

    public PlaylistHostAdapetr(List<SpotifyPlaylistObject> playlist){

        this.playlist=playlist;

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
        return convertView;
    }
}
