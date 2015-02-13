package com.example.paolosalvati.demo;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.microsoft.windowsazure.messaging.NotificationHub;
import com.microsoft.windowsazure.mobileservices.ApiJsonOperationCallback;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceUser;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponse;
import com.microsoft.windowsazure.notifications.NotificationsManager;
import com.spotify.sdk.android.Spotify;
import com.spotify.sdk.android.playback.Config;
import com.spotify.sdk.android.playback.Player;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.spotify.sdk.android.Spotify;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.authentication.SpotifyAuthentication;
import com.spotify.sdk.android.playback.Config;
import com.spotify.sdk.android.playback.ConnectionStateCallback;
import com.spotify.sdk.android.playback.Player;
import com.spotify.sdk.android.playback.PlayerNotificationCallback;
import com.spotify.sdk.android.playback.PlayerState;


public class PlayActivity extends ActionBarActivity implements  PlayerNotificationCallback {

    Context context = this;

    private String SENDER_ID = "823747579189";
    private GoogleCloudMessaging gcm;
    private NotificationHub hub;


    private final static String SERVICE_URI = "http://jukeserver.cloudapp.net/JukeServer.svc/";
    // JSON Node names

    private Player mPlayer;


    // contacts JSONArray
    JSONArray jsonArrayALLPlaylists = null;
    JSONArray jsonArrayALLTracks = null;
    JSONArray jsonArrayINFOPlaylist = null;
    int trackPos =1;
    private ListAdapter playListAdapter = null;
    ArrayList<HashMap<String, String>> songsList = new ArrayList<HashMap<String, String>>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);




        //Connect to GCM GOOGLE NOTIFICATION MESSAGE AND AZURE HUB


        NotificationsManager.handleNotifications(this, SENDER_ID, HubHandler.class);

        gcm = GoogleCloudMessaging.getInstance(this);

        String connectionString = "Endpoint=sb://playhub.servicebus.windows.net/;SharedAccessKeyName=DefaultListenSharedAccessSignature;SharedAccessKey=/dLPV75NdWnCRKADlS1nqU0hl2MySOpqDhgJeVQmdJw=";
        hub = new NotificationHub("playhub", connectionString, this);
        registerWithNotificationHubs();




        Log.i("BBBBB", "1");
        //Save the application Context
        final Context context = this;

        //Get ACS ZUMO Access Token provided by the collaing MenuActivity
        Bundle datipassati = getIntent().getExtras();
        Log.i("BBBBB", "2");

        final String zumoAcsToken = datipassati.getString("ZUMO_ACS_TOKEN");
        Log.i("BBBBB", "3");
        final String zumoAcsUserId = datipassati.getString("ZUMO_ACS_USER_ID");
        Log.i("BBBBB", "4");
        final String spotifyToken = datipassati.getString("SPOTIFY_TOKEN");
        Log.i("BBBBBtoken", spotifyToken);
        songsList = (ArrayList) datipassati.getParcelableArrayList("SONGS");
        Log.i("BBBBB", "6");
        for (int i = 0; i < songsList.size(); i++) {
            // creating new HashMap
            HashMap<String, String> song = songsList.get(i);

            // adding HashList to ArrayList
            Log.i("BBBBB", song.get("songTitle"));
            Log.i("BBBBB", song.get("songPath"));
        }

        Config playerConfig = new Config(this, spotifyToken, "d8e85984e9ac47399e41f0954563cce2");
        Log.i("BBBBB", "a");
        Spotify spotify = new Spotify();
        Log.i("BBBBB", "b");
        mPlayer = spotify.getPlayer(playerConfig, this, new Player.InitializationObserver() {
            @Override
            public void onInitialized() {
                Log.i("BBBBB", "c"+ ((Object) trackPos).toString());
                //mPlayer.addConnectionStateCallback(PlayActivity.this);
                mPlayer.addPlayerNotificationCallback(PlayActivity.this);
                Log.i("BBBBB", songsList.get(trackPos).get("songTitle").toString());

                mPlayer.play("spotify:track:" + songsList.get(trackPos).get("songPath"));
            }


            @Override
            public void onError(Throwable throwable) {
                Log.i("BBBBB", "err");
            }



/*
        playListAdapter=new PlayListAdapetr(ArrayALLPlayList);
        ListView allPlayLists = (ListView) findViewById(R.id.allPlayList);
        allPlayLists.setAdapter(playListAdapter);
*/
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_load_play_lists, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onPlaybackEvent(EventType eventType, PlayerState playerState) {
        Log.d("PlayActivity", "Playback event received: " + eventType.name());
        switch (eventType) {
           case TRACK_START: Log.i("onPlaybackEventTRACK_START", songsList.get(trackPos).get("songTitle"));
                break;
            case TRACK_END: Log.i("onPlaybackEventTRACK_END", songsList.get(trackPos).get("songTitle"));
                trackPos =trackPos+1;
                mPlayer.play("spotify:track:" + songsList.get(trackPos).get("songPath"));
                break;

            default:
                Log.i("onPlaybackEventTRACK_default",eventType.toString());
                break;
        }
    }



    @Override
    public void onPlaybackError(ErrorType errorType, String s) {

    }



    @SuppressWarnings("unchecked")
    private void registerWithNotificationHubs() {
        new AsyncTask() {
            @Override
            protected Object doInBackground(Object... params) {
                try {
                    String regid = gcm.register(SENDER_ID);
                    hub.register(regid);
                } catch (Exception e) {
                    return e;
                }
                return null;
            }
        }.execute(null, null, null);
    }




    //register your activity onResume()
    @Override
    public void onResume() {
        super.onResume();
        context.registerReceiver(mMessageReceiver, new IntentFilter("unique_name"));
    }

    //Must unregister onPause()
    @Override
    protected void onPause() {
        super.onPause();
        context.unregisterReceiver(mMessageReceiver);
    }
    //This is the handler that will manager to process the broadcast intent
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("onReceive","STARTAA");
            // Extract data included in the Intent
            String message = intent.getStringExtra("message");

            //Recupero il Bottone di Logout tramite l'ID
            //TextView txtMessage =(TextView) findViewById(R.id.message);
            //txtMessage.setText(message);

            Log.d("PAOLOA",message);
            if (message != null) {
                try {

                    PlayListObject playlist = new PlayListObject();
                    //PARSO IL JSON
                    JSONObject jsonPlayListObj = new JSONObject(message);
                    playlist.setProvider(jsonPlayListObj.optString(PlayListObject.TAG_PROVIDER, "defaultValue").toString());
                    playlist.setPoweredby(jsonPlayListObj.optString(PlayListObject.TAG_POWEREDBY, "defaultValue").toString());
                    playlist.setUsernameID(jsonPlayListObj.optString(PlayListObject.TAG_USERNAMEID, "defaultValue").toString());
                    playlist.setPlaylistuserID(jsonPlayListObj.optString(PlayListObject.TAG_PLAYLISTUSERID, "defaultValue").toString());
                    playlist.setPlaylistID(jsonPlayListObj.optString(PlayListObject.TAG_PLAYLISTID, "defaultValue").toString());
                    playlist.setPlaylistName(jsonPlayListObj.optString(PlayListObject.TAG_PLAYLISTNAME, "defaultValue").toString());


                    // Getting JSON Array node
                    JSONArray jsonArrayPlaylist = jsonPlayListObj.getJSONArray(PlayListObject.TAG_TRACKS);
                    ArrayList<TrackObject> ArrayPlayList = new ArrayList<TrackObject>();
                    // looping through All jsonArrayPlaylist
                    for (int i = 0; i < jsonArrayPlaylist.length(); i++) {

                        JSONObject c = jsonArrayPlaylist.getJSONObject(i);

                        TrackObject track = new TrackObject();
                        track.setTrackID(c.optString(TrackObject.TAG_TRACKID,"defaultValue").toString());
                        track.setTrackName(c.optString(TrackObject.TAG_TRACKNAME, "defaultValue").toString());
                        track.setTotalLike(c.optString(TrackObject.TAG_TOTALLIKE, "0").toString());
                        track.setPosition(c.optString(TrackObject.TAG_POSITION, "SAME").toString());
                        ArrayPlayList.add(track);

                    }


                    ListAdapter playListAdapter = null;
                    playListAdapter = new PlayAdapter(ArrayPlayList);

                    TextView playlistTitle = (TextView) findViewById(R.id.title);
                    playlistTitle.setText(playlist.getPlaylistName());
                    ListView allPlayLists = (ListView) findViewById(R.id.tracks);
                    allPlayLists.setAdapter(playListAdapter);


                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("PAOLOA",e.toString());
                }


            }
        }


    };

}
