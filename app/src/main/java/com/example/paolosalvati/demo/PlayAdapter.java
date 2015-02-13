package com.example.paolosalvati.demo;

import android.widget.BaseAdapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

        import java.util.List;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Paolo on 13/02/2015.
 */
public class PlayAdapter extends BaseAdapter {




    List<TrackObject> playlist=null;

    public PlayAdapter(List<TrackObject> playlist){

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
        return playlist.get(position).getTrackID().hashCode();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater=(LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if(convertView==null){
            convertView=inflater.inflate(R.layout.play_layout,null);
        }
        TextView nomeTrack=(TextView)convertView.findViewById(R.id.layout_track_name);
        nomeTrack.setText("NOME: "+playlist.get(position).getTrackName());
        TextView idTrack=(TextView)convertView.findViewById(R.id.layout_track_id);
        idTrack.setText("ID: "+playlist.get(position).getTrackID());
        TextView totaLikeTrack=(TextView)convertView.findViewById(R.id.layout_track_totallike);
        totaLikeTrack.setText("LIKE: "+playlist.get(position).getTotalLike());
        TextView positionTrack=(TextView)convertView.findViewById(R.id.layout_track_position);
        positionTrack.setText("POSITION: "+playlist.get(position).getPosition());
        return convertView;
    }
}
