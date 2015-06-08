package com.example.paolosalvati.demo.dataClasses;

/**
 * Created by paolo.salvati on 22/01/2015.
 */
public class PlayListObject {


    // TAG for JSON parsing
    public static final String TAG_PLAYLIST = "playlist";
    public static final String TAG_PROVIDER = "provider";

    public static final String TAG_VERSION = "version";


    public static final String TAG_PLAYLISTUSERID = "playlistuserID";
    public static final String TAG_PLAYLISTID="playlistID";
    public static final String TAG_PLAYLISTNAME="playlistname";
    public static final String TAG_TRACKS="tracks";

    //Membri
    private String provider;
    private String version;

    private String playlistuserID;
    private String playlistID;
    private String imageUri;
    private String playlistname;
   // private List<TrackObject> tracks;


    //Getter and Setter
    public String getProvider() {
        return provider;
    }
    public void setProvider(String provider) {
        this.provider= provider;
    }



    public String getVersion() {
        return version;
    }
    public void setVersion(String version) {
        this.version = version;
    }



    public String getPlaylistuserID() {
        return playlistuserID;
    }
    public void setPlaylistuserID(String playlistuserID) {
        this.playlistuserID = playlistuserID;
    }




    public String getPlaylistID() {
        return playlistID;
    }
    public void setPlaylistID(String playlistID) {
        this.playlistID= playlistID;
    }

    public String getPlaylistName() {
        return playlistname;
    }
    public void setPlaylistName(String playlistname) {
        this.playlistname= playlistname;
    }

    public String getPlaylistImageUri() {return imageUri;}
    public void setPlaylistImageUri(String imageUri) {this.imageUri = imageUri;}

   // public List<TrackObject> getTracks() {
   //     return tracks;
    //}
    //public void setTracks(List<TrackObject> tracks) {
    //    this.tracks= tracks;
    //}

}
