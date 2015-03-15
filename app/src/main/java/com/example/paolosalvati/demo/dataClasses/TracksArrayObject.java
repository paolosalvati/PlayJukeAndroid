package com.example.paolosalvati.demo.dataClasses;

import com.example.paolosalvati.demo.spotifyDataClasses.SpotifyTracksObject;
import com.example.paolosalvati.demo.spotifyDataClasses.SpotifyAllTracksObject;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by Paolo on 14/03/2015.
 */
public class TracksArrayObject {

    List<TrackObject> tracksList;

    public TracksArrayObject(SpotifyAllTracksObject spotifyAllTracksObject) {
        List<TrackObject> appoList = new ArrayList<TrackObject>();
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

    public List<TrackObject> getTracksList() {
        return tracksList;
    }
}
