package com.example.paolosalvati.demo;



/**
 * Created by Paolo on 04/02/2015.
 */
public class TrackObject {
    public static final String TAG_TRACKNAME="trackname";
    public static final String TAG_TRACKID="trackID";
    public static final String TAG_LIKES="likes";
    public static final String TAG_DISLIKES="dislikes";
    public static final String TAG_POSITION="position";
    public static final String TAG_ALBUM="album";
    public static final String TAG_ARTIST="artist";

    private String trackname;
    private String trackID;
    private int likes;
    private int dislikes;
    private int position;
    private String album;
    private String artist;

    public String getTrackName() {
        return trackname;
    }
    public void setTrackName(String trackname) {   this.trackname = trackname;  }

    public String getTrackID() {
        return trackID;
    }
    public void setTrackID(String trackID) {   this.trackID = trackID;  }

    public int getLikes() {
        return likes;
    }
    public void setLikes(int likes) {   this.likes = likes;  }

    public int getDislikes() {
        return dislikes;
    }
    public void setDislikes(int dislikes) {   this.dislikes = dislikes;  }

    public int getPosition() {
        return position;
    }
    public void setPosition(int position) {   this.position = position;  }



    public String getAlbum() {
        return album;
    }
    public void setAlbum(String album) {   this.album = album;  }


    public String getArtist() {
        return artist;
    }
    public void setArtist(String artist) {   this.artist = artist;  }
}
