package com.example.paolosalvati.demo;

import java.util.List;


import com.google.android.gms.internal.id;

import java.util.List;

/**
 * Created by paolo.salvati on 22/01/2015.
 */
public class PlayListObject {


    // TAG for JSON parsing
    public static final String TAG_PROVIDER = "provider";
    public static final String TAG_POWEREDBY = "so";
    public static final String TAG_VERSION = "version";
    public static final String TAG_MAC = "mac";
    public static final String TAG_USERNAMEID = "usernameID";
    public static final String TAG_PLAYLISTUSERID = "playlistuserID";
    public static final String TAG_PLAYLISTID="playListId";
    public static final String TAG_PLAYLISTNAME="playlistname";
    public static final String TAG_TRACKS="tracks";

    //Membri
    private String provider;
    private String poweredby;
    private String version;
    private String mac;
    private String usernameID;
    private String playlistuserID;
    private String playlistID;
    private String playlistname;
    private List<TrackObject> playlist;


    //Getter and Setter
    public String getProvider() {
        return provider;
    }
    public void setProvider(String provider) {
        this.provider= provider;
    }

    public String getPoweredby() {
        return poweredby;
    }
    public void setPoweredby(String poweredby) {
        this.poweredby = poweredby;
    }

    public String getVersion() {
        return version;
    }
    public void setVersion(String version) {
        this.version = version;
    }

    public String getMac() {
        return mac;
    }
    public void setMac(String mac) {
        this.mac = mac;
    }


    public String getPlaylistuserID() {
        return playlistuserID;
    }
    public void setPlaylistuserID(String playlistuserID) {
        this.playlistuserID = playlistuserID;
    }

    public String getUsernameID() {
        return playlistID;
    }
    public void setUsernameID(String playlistID) {
        this.playlistID = playlistID;
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

    public List<TrackObject> getPlaylist() {
        return playlist;
    }
    public void setPlaylist(List<TrackObject> playlist) {
        this.playlist= playlist;
    }

}
