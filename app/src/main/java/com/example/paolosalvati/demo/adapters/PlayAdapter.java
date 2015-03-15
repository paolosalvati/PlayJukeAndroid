package com.example.paolosalvati.demo.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.paolosalvati.demo.R;
import com.example.paolosalvati.demo.dataClasses.TrackObject;

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

        TextView totaLikesTrack=(TextView)convertView.findViewById(R.id.layout_track_likes);
        totaLikesTrack.setText("LIKES: "+((Object) playlist.get(position).getLikes()).toString());

        TextView totaDislikesTrack=(TextView)convertView.findViewById(R.id.layout_track_dislikes);
        totaDislikesTrack.setText("DISLIKES: "+((Object) playlist.get(position).getDislikes()).toString());

        TextView positionTrack=(TextView)convertView.findViewById(R.id.layout_track_position);
        positionTrack.setText("POSITION: "+ ((Object) playlist.get(position).getPosition()).toString());

        TextView albumTrack=(TextView)convertView.findViewById(R.id.layout_track_album);
        albumTrack.setText("ALBUM: "+playlist.get(position).getAlbum());

        TextView artistTrack=(TextView)convertView.findViewById(R.id.layout_track_artist);
        artistTrack.setText("ARTIST: "+playlist.get(position).getArtist());

        return convertView;
    }
}
