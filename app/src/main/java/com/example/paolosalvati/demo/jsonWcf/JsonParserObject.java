package com.example.paolosalvati.demo.jsonWcf;

import android.net.wifi.ScanResult;
import android.util.Log;

import com.example.paolosalvati.demo.adapters.PlayClientAdapter;
import com.example.paolosalvati.demo.dataClasses.PlayListObject;
import com.example.paolosalvati.demo.dataClasses.TrackObject;
import com.example.paolosalvati.demo.dataClasses.TracksArrayObject;
import com.example.paolosalvati.demo.dataClasses.UserObject;
import com.example.paolosalvati.demo.dataClasses.WifiObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by Paolo on 15/03/2015.
 */
public class JsonParserObject {

    WifiObject wifiObject;
    UserObject userObject;
    PlayListObject playListObject;
    TracksArrayObject tracksArrayObject;

    public JsonParserObject() {
    }

    public JsonParserObject(UserObject userObject, PlayListObject playListObject, TracksArrayObject tracksArrayObject) {
            this.userObject = userObject;
            this.playListObject = playListObject;
            this.tracksArrayObject = tracksArrayObject;
    }

    public JsonParserObject(WifiObject wifiObject,UserObject userObject) {
        this.userObject = userObject;
        this.wifiObject = wifiObject;
    }

    public UserObject getUserObject() {
        return userObject;
    }

    public void setUserObject(UserObject userObject) {
        this.userObject = userObject;
    }

    public PlayListObject getPlayListObject() {
        return playListObject;
    }

    public void setPlayListObject(PlayListObject playListObject) {
        this.playListObject = playListObject;
    }

    public TracksArrayObject getTracksArrayObject() {
        return tracksArrayObject;
    }

    public void setTracksArrayObject(TracksArrayObject tracksArrayObject) {
        this.tracksArrayObject = tracksArrayObject;
    }

    public String jsonLoadPlaylist() throws JSONException {

        // Creating root JSONObject
        JSONObject json = new JSONObject();
        JSONObject json_user = new JSONObject();
        JSONObject json_playlist = new JSONObject();

        // Put in it a String field

        json_user.put(UserObject.TAG_MAC, this.userObject.getMac());
        json_user.put(UserObject.TAG_BSSID, this.userObject.getSsid());
        json_user.put(UserObject.TAG_AUTHPROVIDER, this.userObject.getAuthProvider());

        json_user.put(UserObject.TAG_USERNAMEID, this.userObject.getUsernameID());
        json_user.put(UserObject.TAG_OS, this.userObject.getOs());

        json.put(UserObject.TAG_USER, json_user);

        json_playlist.put(PlayListObject.TAG_PROVIDER, this.playListObject.getProvider());
        json_playlist.put(PlayListObject.TAG_VERSION, this.playListObject.getVersion());
        json_playlist.put(PlayListObject.TAG_PLAYLISTID, this.playListObject.getPlaylistID());
        json_playlist.put(PlayListObject.TAG_PLAYLISTNAME, this.playListObject.getPlaylistName());
        json_playlist.put(PlayListObject.TAG_PLAYLISTUSERID, this.playListObject.getPlaylistuserID());
        json.put(PlayListObject.TAG_PLAYLIST, json_playlist);

        // Creating a JSONArray
        JSONArray arr_to_wcf = new JSONArray();
        int pos = 0;
        //Creating the element to populate the array
        for (TrackObject track : this.tracksArrayObject.getTracksList()) {


            JSONObject element = new JSONObject();

            String trackName = track.getTrackName();

            String[] invalid_characters = {"&", ".", "#", "$", "!", "?", "'", ","};

            for (String str : invalid_characters) {
                trackName = trackName.replace(str, "");
            }
            element.put(TrackObject.TAG_TRACKNAME, trackName);
            element.put(TrackObject.TAG_TRACKID, track.getTrackID());
            element.put(TrackObject.TAG_POSITION, pos);
            element.put(TrackObject.TAG_ALBUM, track.getAlbum());
            element.put(TrackObject.TAG_ARTIST, track.getArtist());

            // Put it in the array
            arr_to_wcf.put(element);


        }
        json.put(PlayListObject.TAG_TRACKS, arr_to_wcf);
        // Get the JSON String
        String stringJson = json.toString();
        // Get formatted and indented JSON String
        //String s2 = json.toString(4);

        return stringJson;
    }





    public String jsonClientRegistration() throws JSONException {

        // Creating root JSONObject
        JSONObject json = new JSONObject();
        JSONObject json_user = new JSONObject();


        // Creating a JSONArray
        JSONArray wifiMacListArray = new JSONArray();

            json_user.put(UserObject.TAG_USERNAMEID, userObject.getUsernameID());
            json_user.put(UserObject.TAG_OS, userObject.getOs());
            //Togliere
            json_user.put(PlayListObject.TAG_VERSION,"1.0");
            json.put(UserObject.TAG_CLIENT, json_user);


            //JSONArray JsonwifiMacListArray = new JSONArray();
            int i=0;
            if (wifiObject.getWifiScanList() != null) {
                for (      ScanResult scan : wifiObject.getWifiScanList()) {
                    String a = scan.BSSID.toString() + " "+scan.SSID.toString()  ;
                    Log.d( "WIFI BSSID", a );

                        /*
                        JSONObject element = new JSONObject();
                        try {
                            element.put(UserObject.TAG_MAC,scan.BSSID.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        try {
                            element.put(UserObject.TAG_BSSID, scan.SSID.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        */

                    // Put it in the array
                    // arr.put(scan.BSSID.toString().replaceAll(":", "-"));
                    wifiMacListArray.put(scan.BSSID.toString());


                    i=i+1;

                }
            }
            json.put(UserObject.TAG_MACLIST, wifiMacListArray);

        // Get the JSON String
        String stringJson = json.toString();
        // Get formatted and indented JSON String
        //String s2 = json.toString(4);

        return stringJson;
    }




    public PlayListObject jsonClientRegistrationResponseGetPlaylist(String jsonStringTracksList) throws JSONException {

        //Istanzio la Playlist
        PlayListObject playListObject = new PlayListObject();


        //PARSO IL JSON
        JSONObject jsonPlayListObj = null;
        jsonPlayListObj = new JSONObject(jsonStringTracksList);

        JSONObject a=jsonPlayListObj.getJSONObject(PlayListObject.TAG_PLAYLIST);

        playListObject.setProvider(a.optString(PlayListObject.TAG_PROVIDER, "defaultValue").toString());
        playListObject.setProvider(a.optString(PlayListObject.TAG_PROVIDER, "defaultValue").toString());
        playListObject.setPlaylistuserID(a.optString(PlayListObject.TAG_PLAYLISTUSERID, "defaultValue").toString());
        playListObject.setPlaylistID(a.optString(PlayListObject.TAG_PLAYLISTID, "defaultValue").toString());
        playListObject.setPlaylistName(a.optString(PlayListObject.TAG_PLAYLISTNAME, "defaultValue").toString());

        return playListObject;
    }

    public TracksArrayObject jsonClientRegistrationResponseGetTracks(String jsonStringTracksList) throws JSONException {

        //PARSO IL JSON
        JSONObject jsonObject = null;
        jsonObject = new JSONObject(jsonStringTracksList);
        // Getting JSON Array node
        JSONArray jsonArray = jsonObject.getJSONArray(PlayListObject.TAG_TRACKS);

        TracksArrayObject tracksArrayObject = new TracksArrayObject(jsonArray);

        return tracksArrayObject;
    }
























}
