package com.example.paolosalvati.demo.dataClasses;

import android.util.Log;

import com.example.paolosalvati.demo.spotifyDataClasses.SpotifyAllTracksObject;
import com.example.paolosalvati.demo.spotifyDataClasses.SpotifyTracksObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Paolo on 14/03/2015.
 */
public class TracksArrayObject {

    ArrayList<TrackObject> tracksList;

    public TracksArrayObject(SpotifyAllTracksObject spotifyAllTracksObject) {
        ArrayList<TrackObject> appoList = new ArrayList<TrackObject>();
        int i = 0;
        //Creating the element to populate the array
        for (SpotifyTracksObject spotifyTrack : spotifyAllTracksObject.getArrayALLTracks()) {


            TrackObject trackObject = new TrackObject();
            //track.setPosition(((Object) i).toString());
            trackObject.setPosition(i);
            trackObject.setLikes(0);
            trackObject.setDislikes(0);
            trackObject.setTrackID(spotifyTrack.getID());
            trackObject.setTrackName(spotifyTrack.getName());
            trackObject.setArtist("a");
            trackObject.setAlbum("a");
            appoList.add(trackObject);


            i = i + 1;
        }
        this.tracksList=appoList;

    }


    public TracksArrayObject(JSONArray jsonAllTracksObject) throws JSONException {
        ArrayList<TrackObject> appoList = new ArrayList<TrackObject>();



        // looping through All jsonArrayPlaylist


        for (int i = 0; i < jsonAllTracksObject.length(); i++) {

            JSONObject c = jsonAllTracksObject.getJSONObject(i);

            TrackObject track = new TrackObject();
            track.setId(c.optInt(TrackObject.TAG_ID,0));
            track.setTrackID(c.optString(TrackObject.TAG_TRACKID,"defaultValue").toString());
            track.setTrackName(c.optString(TrackObject.TAG_TRACKNAME, "defaultValue").toString());
            track.setDislikes(c.optInt(TrackObject.TAG_DISLIKES, 0));
            track.setLikes(c.optInt(TrackObject.TAG_LIKES, 0));
            track.setPosition(c.optInt(TrackObject.TAG_POSITION, 0));
            track.setAlbum(c.optString(TrackObject.TAG_ALBUM, "defaultValue").toString());
            track.setArtist(c.optString(TrackObject.TAG_ARTIST, "defaultValue").toString());
            track.setRank(c.optString(TrackObject.TAG_RANK, "-").toString());
            track.setActive(c.optString(TrackObject.TAG_ACTIVE, "true").toString());
            track.setPlaying("N");
            appoList.add(track);

        }


        Collections.sort(appoList, new Comparator<TrackObject>() {
            public int compare(TrackObject obj1, TrackObject obj2) {
                // TODO Auto-generated method stub
                int obj1_poistion = obj1.getPosition();
                int obj2_poistion = obj2.getPosition();
                ;

                return (obj1_poistion < obj2_poistion) ? -1 : (obj1_poistion > obj2_poistion) ? 1 : 0;
            }
        });


        this.tracksList=appoList;

        if(this.tracksList.get(0).getActive()=="true"){
            this.tracksList.get(0).setPlaying("Y");
        }
        else {
            String song_before_active=this.tracksList.get(0).getActive();
            for (int i = 1; i < this.tracksList.size(); i++) {
                if(this.tracksList.get(i).getActive()=="true" && song_before_active=="false" ){
                    this.tracksList.get(i).setPlaying("Y");
                    Log.d("Flag Active",this.tracksList.get(i).getTrackName());
                    return;
                }
                else {
                song_before_active=this.tracksList.get(i).getActive();
                }
            }
        }

    }


    public ArrayList<TrackObject> getTracksList() {
        return tracksList;
    }
}
