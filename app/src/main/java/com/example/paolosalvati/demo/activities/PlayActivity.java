package com.example.paolosalvati.demo.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.paolosalvati.demo.R;
import com.example.paolosalvati.demo.adapters.PlayAdapter;
import com.example.paolosalvati.demo.dataClasses.PlayListObject;
import com.example.paolosalvati.demo.dataClasses.TrackObject;
import com.example.paolosalvati.demo.dataClasses.TracksArrayObject;
import com.example.paolosalvati.demo.dataClasses.UserObject;
import com.example.paolosalvati.demo.handlers.HubHandler;
import com.example.paolosalvati.demo.jsonWcf.JsonParserObject;
import com.example.paolosalvati.demo.utilities.GlobalObjects;
import com.example.paolosalvati.demo.utilities.Utilities;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.microsoft.windowsazure.messaging.NotificationHub;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.notifications.NotificationsManager;
import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerNotificationCallback;
import com.spotify.sdk.android.player.PlayerState;
import com.spotify.sdk.android.player.PlayerStateCallback;
import com.spotify.sdk.android.player.Spotify;


import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;


import com.spotify.sdk.android.player.Spotify;
import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerNotificationCallback;
import com.spotify.sdk.android.player.PlayerState;

public class PlayActivity extends ActionBarActivity implements PlayerNotificationCallback, SeekBar.OnSeekBarChangeListener  {
    private UserObject userObject;
    private PlayListObject playListObject;
    private TracksArrayObject tracksArrayObject;
    List<TrackObject> arrayPlayList;

    Context context = this;

    private String SENDER_ID = "823747579189";
    private GoogleCloudMessaging gcm;
    private NotificationHub hub;


    private ListView playlistView;
    private ListAdapter playListAdapter = null;



    // JSON Node names

    private Player mPlayer;

private Spotify spotify;

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

    //WCF
    private String SERVICE_URI;

    //ArrayList<HashMap<String, String>> songsList = new ArrayList<HashMap<String, String>>();
    //ArrayList<HashMap<String, String>> songsListHub = new ArrayList<HashMap<String, String>>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_play);

            //Save the application Context
            final Context context = this;

            //Get Connection to ZUMO client and Spotify Acceess Token
            final GlobalObjects globalObjects = ((GlobalObjects) getApplicationContext());
            final MobileServiceClient zumoClient = globalObjects.getZumoClient();
            final String spotifyToken = globalObjects.getSpotifyAccessToken();

            //Connect to GCM GOOGLE NOTIFICATION MESSAGE AND AZURE HUB
            NotificationsManager.handleNotifications(this, SENDER_ID, HubHandler.class);
            gcm = GoogleCloudMessaging.getInstance(this);
            String connectionString = "Endpoint=sb://playhub.servicebus.windows.net/;SharedAccessKeyName=DefaultListenSharedAccessSignature;SharedAccessKey=/dLPV75NdWnCRKADlS1nqU0hl2MySOpqDhgJeVQmdJw=";
            hub = new NotificationHub("playhub", connectionString, this);
            registerWithNotificationHubs();

            //Get Track List to be played
            Bundle datipassati = getIntent().getExtras();
            final String songs = datipassati.getString("SONGS");
            Log.d("SONGSrrr",songs);

        Log.d("SONGSrrr","1");
            SERVICE_URI = getApplication().getString(R.string.azure_wcf_service_uri);

            //Set SeekBar for track's play progress
            songProgressBar = (SeekBar) findViewById(R.id.songProgressBar);
            // Listeners

            songProgressBar.setOnSeekBarChangeListener(this); // Important
            songCurrentDurationLabel = (TextView) findViewById(R.id.songCurrentDurationLabel);
            songTotalDurationLabel = (TextView) findViewById(R.id.songTotalDurationLabel);
            currenTrack  = (TextView) findViewById(R.id.currentrack);

            try{
            JsonParserObject jsonParserObject = new JsonParserObject();
                Log.d("SONGSrrr","2");
            userObject=    jsonParserObject.jsonClientRegistrationResponseGetUser(songs);
                Log.d("SONGSrrr","3");
            playListObject = jsonParserObject.jsonClientRegistrationResponseGetPlaylist(songs);
                Log.d("SONGSrrr","4");
            tracksArrayObject =  jsonParserObject.jsonClientRegistrationResponseGetTracks(songs);
                Log.d("SONGSrrr","5");

            arrayPlayList= tracksArrayObject.getTracksList();

            //Show Playlist Info
            TextView playlistTitle = (TextView) findViewById(R.id.title);
            playlistTitle.setText(playListObject.getPlaylistName());

           //Show Track List
            playListAdapter = new PlayAdapter(arrayPlayList);
            playlistView = (ListView) findViewById(R.id.tracks);
            playlistView.setAdapter(playListAdapter);


            } catch (JSONException e) {
                e.printStackTrace();
            }

        Log.d("SONGSrrr","fff");
            //Costruisco il Player
        try {
        Config playerConfig = new Config(this, spotifyToken, "d8e85984e9ac47399e41f0954563cce2",Config.DeviceType.SMARTPHONE );


            Log.d("SONGSrrr","ggg");
            mPlayer = Spotify.getPlayer(playerConfig, this, new Player.InitializationObserver()  {



                @Override
                public void onInitialized(Player mPlayer) {
                    Log.d("SONGSrrr", "7");
                    mPlayer.addPlayerNotificationCallback(PlayActivity.this);
                    Log.d("Play: ", arrayPlayList.get(trackPos).getTrackName());
                    mPlayer.play("spotify:track:" + arrayPlayList.get(trackPos).getTrackID());

                    // set Progress bar values
                    songProgressBar.setProgress(0);
                    songProgressBar.setMax(100);
                    currenTrack.setText(arrayPlayList.get(trackPos).getTrackName());

                }


                @Override
                public void onError(Throwable throwable) {
                    Log.d("SONGSrrr", "7" + throwable.getMessage().toString());
                }

            });

        } catch (Exception e) {

            Log.d("SONGSrrr", "7"+ e.getMessage().toString());
        };


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
            case TRACK_START: Log.i("onPlaybackEventTRACK_START", arrayPlayList.get(trackPos).getTrackName());



                // Creating root JSONObject
                JSONObject json = new JSONObject();


                try {
                   if(trackPos==0) {
                       json.put("mac", userObject.getMac());
                       json.put("trackid", 0);
                       json.put("playlistid",  0 );
                   }
                    else{
                       json.put("mac", userObject.getMac());
                       json.put("trackid",1011);// arrayPlayList.get(trackPos).getId());
                       json.put("plistid",  1158 );
                   }

                } catch (JSONException e) {
                    e.printStackTrace();
                };


                String stringJson = json.toString();
                // Get formatted and indented JSON String
                //String s2 = json.toString(4);




                if (android.os.Build.VERSION.SDK_INT > 9) {
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                    StrictMode.setThreadPolicy(policy);
                }
                ;



                // POST request to WCF
                HttpPost request = new HttpPost(SERVICE_URI + "DeactivateTrack");
                request.setHeader("Accept", "application/json");
                request.setHeader("Content-type", "application/json");
                StringEntity entity = null;
                try {
                    Log.d("WebInvoke DeactivateTrack: json", stringJson);
                    entity = new StringEntity(stringJson, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                request.setEntity(entity);

                // Send request to WCF service
                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpResponse httpResponse = null;


                try {
                    httpResponse = httpClient.execute(request);
                    Log.d("WebInvoke DeactivateTrack", "OK");
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d("WebInvoke DeactivateTrack", "KO : " + e.toString());
                };



            break;
            case PLAY:
                long totalDuration = playerState.durationInMs;
                long currentDuration = playerState.positionInMs;

                // Displaying Total Duration time
                String finalTimerString=Utilities.milliSecondsToTimer(totalDuration);
                songTotalDurationLabel.setText(finalTimerString);
            break;
            case TRACK_END: Log.i("onPlaybackEventTRACK_END", arrayPlayList.get(trackPos).getTrackName());
                trackPos =trackPos+1;
                mPlayer.play("spotify:track:" + arrayPlayList.get(trackPos).getTrackID());
                currenTrack.setText( arrayPlayList.get(trackPos).getTrackName());
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
            String songs = intent.getStringExtra("message");
            Log.d("dddsssssdddd",songs);
            if (songs != null) {
                try {

                    JsonParserObject jsonParserObject = new JsonParserObject();
                    playListObject = jsonParserObject.jsonClientRegistrationResponseGetPlaylist(songs);
                    tracksArrayObject =  jsonParserObject.jsonClientRegistrationResponseGetTracks(songs);

                    arrayPlayList= tracksArrayObject.getTracksList();

                    /*DA SPOSTARE NEL PLAYER....imizio
                    *
                    */

                    //Show Playlist Info
                    TextView playlistTitle = (TextView) findViewById(R.id.title);
                    playlistTitle.setText(playListObject.getPlaylistName());

                    //Show Track List
                    playListAdapter = new PlayAdapter(arrayPlayList);
                    playlistView = (ListView) findViewById(R.id.tracks);
                    playlistView.setAdapter(playListAdapter);


                    /*DA SPOSTARE NEL PLAYER....fine
                    *
                    */
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
                                           // Displaying Total Duration time
                                           String finalTimerString=Utilities.milliSecondsToTimer(currentDuration);
                                           songCurrentDurationLabel.setText(finalTimerString);

                                           /*
                                           Double percentage = (double) 0;

                                           long currentSeconds = (int) (currentDuration / 1000);
                                           long totalSeconds = (int) (totalDuration / 1000);

                                           // calculating percentage
                                           percentage =(((double)currentSeconds)/totalSeconds)*100;

                                           // return percentage
                                           int progress= percentage.intValue();
                                           */
                                            int progress=Utilities.getProgressPercentage(currentDuration,totalDuration);
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

    @Override
    protected void onDestroy() {
        Spotify.destroyPlayer(this);
        super.onDestroy();
    }

}
