package com.example.paolosalvati.demo;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SeekBar;
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
import java.util.Collections;
import java.util.Comparator;
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
import com.spotify.sdk.android.playback.PlayerStateCallback;


public class PlayActivity extends ActionBarActivity implements  PlayerNotificationCallback, SeekBar.OnSeekBarChangeListener  {

    Context context = this;

    private String SENDER_ID = "823747579189";
    private GoogleCloudMessaging gcm;
    private NotificationHub hub;


    private ListView playlistView;
    private ListAdapter playListAdapter = null;



    // JSON Node names

    private Player mPlayer;



    private SeekBar songProgressBar;
    // Handler to update UI timer, progress bar etc,.
    private Handler mHandler = new Handler();;
    private Utilities utils;
    private TextView songCurrentDurationLabel;
    private TextView songTotalDurationLabel;
    private TextView currenTrack;
    private PlayerState playerState;

    // contacts JSONArray
    JSONArray jsonArrayALLPlaylists = null;
    JSONArray jsonArrayALLTracks = null;
    JSONArray jsonArrayINFOPlaylist = null;
    int trackPos =0;


    ArrayList<TrackObject> ArrayPlayList = new ArrayList<TrackObject>();
    //ArrayList<HashMap<String, String>> songsList = new ArrayList<HashMap<String, String>>();
    //ArrayList<HashMap<String, String>> songsListHub = new ArrayList<HashMap<String, String>>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);


        songProgressBar = (SeekBar) findViewById(R.id.songProgressBar);
        // Listeners
        songProgressBar.setOnSeekBarChangeListener(this); // Important

        songCurrentDurationLabel = (TextView) findViewById(R.id.songCurrentDurationLabel);
        songTotalDurationLabel = (TextView) findViewById(R.id.songTotalDurationLabel);
        currenTrack  = (TextView) findViewById(R.id.currentrack);










        //Connect to GCM GOOGLE NOTIFICATION MESSAGE AND AZURE HUB


        NotificationsManager.handleNotifications(this, SENDER_ID, HubHandler.class);

        gcm = GoogleCloudMessaging.getInstance(this);

        String connectionString = "Endpoint=sb://playhub.servicebus.windows.net/;SharedAccessKeyName=DefaultListenSharedAccessSignature;SharedAccessKey=/dLPV75NdWnCRKADlS1nqU0hl2MySOpqDhgJeVQmdJw=";
        hub = new NotificationHub("playhub", connectionString, this);
        registerWithNotificationHubs();




        //Save the application Context
        final Context context = this;

        //Get ACS ZUMO Access Token provided by the collaing MenuActivity
        Bundle datipassati = getIntent().getExtras();


        //final String zumoAcsToken = datipassati.getString("ZUMO_ACS_TOKEN");

        //final String zumoAcsUserId = datipassati.getString("ZUMO_ACS_USER_ID");

        //final String spotifyToken = datipassati.getString("SPOTIFY_TOKEN");
        final String songs = datipassati.getString("SONGS");


        final GlobalObjects globalObjects = ((GlobalObjects) getApplicationContext());
        final MobileServiceClient zumoClient = globalObjects.getZumoClient();
        final String spotifyToken = globalObjects.getSpotifyAccessToken();


        Log.d("SONGSxxx",songs);





        PlayListObject playlist = new PlayListObject();
        //PARSO IL JSON
        JSONObject jsonPlayListObj = null;
        try {
            jsonPlayListObj = new JSONObject(songs);

            JSONObject a=jsonPlayListObj.getJSONObject(PlayListObject.TAG_PLAYLIST);
            playlist.setProvider(a.optString(PlayListObject.TAG_PROVIDER, "defaultValue").toString());
            playlist.setProvider(a.optString(PlayListObject.TAG_PROVIDER, "defaultValue").toString());
            playlist.setPlaylistuserID(a.optString(PlayListObject.TAG_PLAYLISTUSERID, "defaultValue").toString());
            playlist.setPlaylistID(a.optString(PlayListObject.TAG_PLAYLISTID, "defaultValue").toString());
            playlist.setPlaylistName(a.optString(PlayListObject.TAG_PLAYLISTNAME, "defaultValue").toString());


            // Getting JSON Array node
            JSONArray jsonArrayPlaylist = jsonPlayListObj.getJSONArray(PlayListObject.TAG_TRACKS);

            // looping through All jsonArrayPlaylist







            for (int i = 0; i < jsonArrayPlaylist.length(); i++) {

                JSONObject c = jsonArrayPlaylist.getJSONObject(i);

                TrackObject track = new TrackObject();
                track.setTrackID(c.optString(TrackObject.TAG_TRACKID,"defaultValue").toString());
                track.setTrackName(c.optString(TrackObject.TAG_TRACKNAME, "defaultValue").toString());
                track.setDislikes(c.optInt(TrackObject.TAG_DISLIKES, 0));
                track.setLikes(c.optInt(TrackObject.TAG_LIKES, 0));
                track.setPosition(c.optInt(TrackObject.TAG_POSITION, 0));
                track.setAlbum(c.optString(TrackObject.TAG_ALBUM, "defaultValue").toString());
                track.setArtist(c.optString(TrackObject.TAG_ARTIST, "defaultValue").toString());

                ArrayPlayList.add(track);

                HashMap<String, String> song = new HashMap<String, String>();
                song.put("songTitle",track.getTrackName());
                song.put("songPath",  track.getTrackID());

                // Adding each song to SongList
               // songsList.add(song);
            }



            TextView playlistTitle = (TextView) findViewById(R.id.title);
            playlistTitle.setText(playlist.getPlaylistName());


            Collections.sort(ArrayPlayList, new Comparator<TrackObject>() {
                public int compare(TrackObject obj1, TrackObject obj2) {
                    // TODO Auto-generated method stub
                    int obj1_poistion = obj1.getPosition();
                    int obj2_poistion = obj2.getPosition();
                    ;

                    return (obj1_poistion < obj2_poistion) ? -1 : (obj1_poistion > obj2_poistion) ? 1 : 0;
                }
            });


            playListAdapter = new PlayAdapter(ArrayPlayList);
            playlistView = (ListView) findViewById(R.id.tracks);
            playlistView.setAdapter(playListAdapter);


        } catch (JSONException e) {
            e.printStackTrace();
        }





        Config playerConfig = new Config(this, spotifyToken, "d8e85984e9ac47399e41f0954563cce2");

        Spotify spotify = new Spotify();

        mPlayer = spotify.getPlayer(playerConfig, this, new Player.InitializationObserver() {
            @Override
            public void onInitialized() {

                //mPlayer.addConnectionStateCallback(PlayActivity.this);
                mPlayer.addPlayerNotificationCallback(PlayActivity.this);

/*
                mPlayer.addPlayerNotificationCallback(new PlayerNotificationCallback() {
                                @Override
                               public void onPlaybackEvent(EventType eventType, PlayerState state) {
                                         if (SpotifyHandler.this.isHost) {
                                                 if (eventType == EventType.PAUSE) {
                                                         String playlist = SpotifyHandler.this.activity.playlistName;
                                                         String songUri = state.trackUri;
                                                         String song = SpotifyHandler.this.playingTracks.getTitleFromUri(songUri);
                                                         song = song + " - "+ SpotifyHandler.this.playingTracks.getArtistFromTitle(song);
                                                         int time = state.positionInMs;
                                                         SpotifyHandler.this.activity.firebaseHandler.pushToFirebase(playlist, songUri, song, time, !SpotifyHandler.this.paused);
                                                     }
                                                 if (eventType == EventType.AUDIO_FLUSH) {
                                                         String playlist = SpotifyHandler.this.activity.playlistName;
                                                         String songUri = state.trackUri;
                                                         String song = SpotifyHandler.this.playingTracks.getTitleFromUri(songUri);
                                                         song = song + " - "+ SpotifyHandler.this.playingTracks.getArtistFromTitle(song);
                                                         int time = state.positionInMs;
                                                         ((TextView)SpotifyHandler.this.activity.findViewById(R.id.currently_playing)).setText(song);
                                                         SpotifyHandler.this.activity.firebaseHandler.pushToFirebase(playlist, songUri, song, time, !SpotifyHandler.this.paused);
                                                     } else if (eventType == EventType.PLAY) {
                                                         String playlist = SpotifyHandler.this.activity.playlistName;
                                                         String songUri = state.trackUri;
                                                         String song = SpotifyHandler.this.playingTracks.getTitleFromUri(songUri);
                                                         song = song + " - "+ SpotifyHandler.this.playingTracks.getArtistFromTitle(song);
                                                         int time = state.positionInMs;
                                                         ((TextView)SpotifyHandler.this.activity.findViewById(R.id.currently_playing)).setText(song);
                                                         SpotifyHandler.this.activity.firebaseHandler.pushToFirebase(playlist, songUri, song, time, !SpotifyHandler.this.paused);
                                                     }


                                                 // We're only allowing the user to go forward, so call this as if it means onNextSong:
                                                 if (eventType == EventType.END_OF_CONTEXT) {
                                                         MainActivity activity = SpotifyHandler.this.activity;


                                                         SpotifyHandler.this.songIndex += 1;
                                                         SpotifyHandler.this.mPlayer.play(SpotifyHandler.this.playingTracks.tracks.get(SpotifyHandler.this.songIndex).getUri());
                                                         activity.displayCurrentQueue(SpotifyHandler.this.songIndex);
                                                     }
                                             } else if (SpotifyHandler.this.isSlave) {
                                                 if (eventType == EventType.AUDIO_FLUSH) {
                                                         long current_time = new Date().getTime();
                                                         int diff = (int) (current_time-SpotifyHandler.this.timestamp);
                                                         int newPos = diff+SpotifyHandler.this.origSongPos;
                                                         if ((Math.abs(newPos-state.positionInMs))>150) {
                                                                 mPlayer.seekToPosition(newPos);
                                                                 SpotifyHandler.this.activity.setNotMuted();
                                                             }
                                                     }
                                             }
                                     }
                             });

*/

                Log.d("uuuu",   ArrayPlayList.get(trackPos).getTrackName());
                mPlayer.play("spotify:track:" + ArrayPlayList.get(trackPos).getTrackID());

                // set Progress bar values
                songProgressBar.setProgress(0);
                songProgressBar.setMax(100);
                currenTrack.setText(ArrayPlayList.get(trackPos).getTrackName());




            }


            @Override
            public void onError(Throwable throwable) {

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




        // Updating progress bar
        updateProgressBar();

        Log.d("PlayActivity", "Playback event received: " + eventType.name());
        switch (eventType) {
           case TRACK_START: Log.i("onPlaybackEventTRACK_START", ArrayPlayList.get(trackPos).getTrackName());


                break;
            case PLAY:

                long totalDuration = playerState.durationInMs;
                long currentDuration = playerState.positionInMs;
                Log.d("olga","1"+ ((Object) totalDuration).toString());
                Log.d("olga","2"+ ((Object) currentDuration).toString());
                String finalTimerString = "";
                String secondsString = "";

                // Convert total duration into time
                int hours = (int)( totalDuration / (1000*60*60));
                int minutes = (int)(totalDuration % (1000*60*60)) / (1000*60);
                int seconds = (int) ((totalDuration % (1000*60*60)) % (1000*60) / 1000);
                // Add hours if there
                if(hours > 0){
                    finalTimerString = hours + ":";
                }

                // Prepending 0 to seconds if it is one digit
                if(seconds < 10){
                    secondsString = "0" + seconds;
                }else{
                    secondsString = "" + seconds;}

                finalTimerString = finalTimerString + minutes + ":" + secondsString;
                Log.d("olga","3"+ finalTimerString);
                //Log.d("olga","3"+ ((Object) utils.milliSecondsToTimer(totalDuration)).toString());
                //Log.d("olga","3"+ ((Object) utils.milliSecondsToTimer(currentDuration)).toString());

                // Displaying Total Duration time
                songTotalDurationLabel.setText(finalTimerString);
                // Displaying time completed playing
               // songCurrentDurationLabel.setText(""+utils.milliSecondsToTimer(currentDuration));
                break;
            case TRACK_END: Log.i("onPlaybackEventTRACK_END", ArrayPlayList.get(trackPos).getTrackName());
                trackPos =trackPos+1;
                mPlayer.play("spotify:track:" + ArrayPlayList.get(trackPos).getTrackID());
                currenTrack.setText( ArrayPlayList.get(trackPos).getTrackName());
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

            // Extract data included in the Intent
            String message = intent.getStringExtra("message");

            //Recupero il Bottone di Logout tramite l'ID
            //TextView txtMessage =(TextView) findViewById(R.id.message);
            //txtMessage.setText(message);


            if (message != null) {
                try {

                    /*
                    PlayListObject playlist = new PlayListObject();
                    //PARSO IL JSON
                    JSONObject jsonPlayListObj = new JSONObject(message);
                    playlist.setProvider(jsonPlayListObj.optString(PlayListObject.TAG_PROVIDER, "defaultValue").toString());
                    playlist.setProvider(jsonPlayListObj.optString(PlayListObject.TAG_PROVIDER, "defaultValue").toString());
                    playlist.setPlaylistuserID(jsonPlayListObj.optString(PlayListObject.TAG_PLAYLISTUSERID, "defaultValue").toString());
                    playlist.setPlaylistID(jsonPlayListObj.optString(PlayListObject.TAG_PLAYLISTID, "defaultValue").toString());
                    playlist.setPlaylistName(jsonPlayListObj.optString(PlayListObject.TAG_PLAYLISTNAME, "defaultValue").toString());

*/

                    PlayListObject playlist = new PlayListObject();

                    JSONObject jsonPlayListObj = new JSONObject(message);

                    JSONObject a=jsonPlayListObj.getJSONObject(PlayListObject.TAG_PLAYLIST);
                    playlist.setProvider(a.optString(PlayListObject.TAG_PROVIDER, "defaultValue").toString());
                    playlist.setProvider(a.optString(PlayListObject.TAG_PROVIDER, "defaultValue").toString());
                    playlist.setPlaylistuserID(a.optString(PlayListObject.TAG_PLAYLISTUSERID, "defaultValue").toString());
                    playlist.setPlaylistID(a.optString(PlayListObject.TAG_PLAYLISTID, "defaultValue").toString());
                    playlist.setPlaylistName(a.optString(PlayListObject.TAG_PLAYLISTNAME, "defaultValue").toString());

                    // Getting JSON Array node
                    JSONArray jsonArrayPlaylist = jsonPlayListObj.getJSONArray(PlayListObject.TAG_TRACKS);
                    ArrayList<TrackObject> ArrayPlayList = new ArrayList<TrackObject>();
                    // looping through All jsonArrayPlaylist

                    for (int i = 0; i < jsonArrayPlaylist.length(); i++) {

                        JSONObject c = jsonArrayPlaylist.getJSONObject(i);

                        TrackObject track = new TrackObject();
                        track.setTrackID(c.optString(TrackObject.TAG_TRACKID,"defaultValue").toString());
                        track.setTrackName(c.optString(TrackObject.TAG_TRACKNAME, "defaultValue").toString());
                        track.setDislikes(c.optInt(TrackObject.TAG_DISLIKES, 0));
                        track.setLikes(c.optInt(TrackObject.TAG_LIKES, 0));
                        track.setPosition(c.optInt(TrackObject.TAG_POSITION, 0));
                        track.setAlbum(c.optString(TrackObject.TAG_ALBUM, "defaultValue").toString());
                        track.setArtist(c.optString(TrackObject.TAG_ARTIST, "defaultValue").toString());
                        ArrayPlayList.add(track);

                        HashMap<String, String> song = new HashMap<String, String>();
                        song.put("songTitle",track.getTrackName());
                        song.put("songPath",  track.getTrackID());

                        // Adding each song to SongList
                        //songsListHub.add(song);

                    }

                    //songsList = songsListHub;

                    TextView playlistTitle = (TextView) findViewById(R.id.title);
                    playlistTitle.setText(playlist.getPlaylistName());


                    Collections.sort(ArrayPlayList, new Comparator<TrackObject>() {
                        public int compare(TrackObject obj1, TrackObject obj2) {
                            // TODO Auto-generated method stub
                            int obj1_poistion = obj1.getPosition();
                            int obj2_poistion = obj2.getPosition();
                            ;

                            return (obj1_poistion < obj2_poistion) ? -1 : (obj1_poistion > obj2_poistion) ? 1 : 0;
                        }
                    });

                    playListAdapter = new PlayAdapter(ArrayPlayList);
                    playlistView = (ListView) findViewById(R.id.tracks);
                    playlistView.setAdapter(playListAdapter);


                } catch (JSONException e) {
                    e.printStackTrace();

                }


            }
        }


    };


    /**
     * Update timer on seekbar
     * */
    public void updateProgressBar() {
        mHandler.postDelayed(mUpdateTimeTask, 100);
    }

    /**
     * Background Runnable thread
     * */
    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {



            mPlayer.getPlayerState(new PlayerStateCallback() {
                                       @Override
                                       public void onPlayerState(PlayerState playerState) {

                                           long currentDuration = playerState.positionInMs;
                                           long totalDuration = playerState.durationInMs;

                                           String finalTimerString = "";
                                           String secondsString = "";

                                           // Convert total duration into time
                                           int hours = (int)( currentDuration / (1000*60*60));
                                           int minutes = (int)(currentDuration % (1000*60*60)) / (1000*60);
                                           int seconds = (int) ((currentDuration % (1000*60*60)) % (1000*60) / 1000);
                                           // Add hours if there
                                           if(hours > 0){
                                               finalTimerString = hours + ":";
                                           }

                                           // Prepending 0 to seconds if it is one digit
                                           if(seconds < 10){
                                               secondsString = "0" + seconds;
                                           }else{
                                               secondsString = "" + seconds;}

                                           finalTimerString = finalTimerString + minutes + ":" + secondsString;
                                           //Log.d("olga","3"+ finalTimerString);
                                           //Log.d("olga","3"+ ((Object) utils.milliSecondsToTimer(totalDuration)).toString());
                                           //Log.d("olga","3"+ ((Object) utils.milliSecondsToTimer(currentDuration)).toString());

                                           // Displaying Total Duration time
                                           songCurrentDurationLabel.setText(finalTimerString);



                                           //Log.d("olga","4"+ ((Object) currentDuration).toString());
                                           // Displaying Total Duration time
                                           //songTotalDurationLabel.setText(""+utils.milliSecondsToTimer(totalDuration));

                                           // Displaying time completed playing
                                           //songCurrentDurationLabel.setText(""+utils.milliSecondsToTimer(currentDuration));

                                           // Updating progress bar
                                           //int progress = (int)(utils.getProgressPercentage(currentDuration, totalDuration));

                                           Double percentage = (double) 0;

                                           long currentSeconds = (int) (currentDuration / 1000);
                                           long totalSeconds = (int) (totalDuration / 1000);

                                           // calculating percentage
                                           percentage =(((double)currentSeconds)/totalSeconds)*100;

                                           // return percentage
                                           int progress= percentage.intValue();

                                           //Log.d("Progress", ""+progress);
                                           songProgressBar.setProgress(progress);

                                       }
                                   }
            );




            // Running this thread after 100 milliseconds
            mHandler.postDelayed(this, 100);
        }
    };

    /**
     *
     * */
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {

    }

    /**
     * When user starts moving the progress handler
     * */
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // remove message Handler from updating progress bar
        mHandler.removeCallbacks(mUpdateTimeTask);
    }

    /**
     * When user stops moving the progress hanlder
     * */
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

       /*
        mHandler.removeCallbacks(mUpdateTimeTask);
        int totalDuration = mp.getDuration();
        int currentPosition = utils.progressToTimer(seekBar.getProgress(), totalDuration);

        // forward or backward to certain seconds
        mp.seekTo(currentPosition);

        // update timer progress again
        updateProgressBar();
        */
    }



}
