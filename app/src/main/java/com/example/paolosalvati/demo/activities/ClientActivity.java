package com.example.paolosalvati.demo.activities;

import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.paolosalvati.demo.utilities.GlobalObjects;
import com.example.paolosalvati.demo.handlers.HubHandler;
import com.example.paolosalvati.demo.adapters.PlayClientAdapter;
import com.example.paolosalvati.demo.dataClasses.PlayListObject;
import com.example.paolosalvati.demo.R;
import com.example.paolosalvati.demo.dataClasses.TrackObject;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.microsoft.windowsazure.messaging.NotificationHub;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.notifications.NotificationsManager;

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


public class ClientActivity extends ListActivity {


    private final static String SERVICE_URI = "http://jukeserver.cloudapp.net/JukeSvc.svc/";
    Context context = this;

    private String SENDER_ID = "823747579189";
    private GoogleCloudMessaging gcm;
    private NotificationHub hub;


    //private ListView playlistView;
    private ListAdapter playListAdapter = null;


 // contacts JSONArray
    JSONArray jsonArrayALLPlaylists = null;
    JSONArray jsonArrayALLTracks = null;
    JSONArray jsonArrayINFOPlaylist = null;
    //int trackPos =0;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_client);

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
        final String songs = datipassati.getString("SONGS");
        Log.d("SONGSxxx",songs);


        final GlobalObjects globalObjects = ((GlobalObjects) getApplicationContext());
        final MobileServiceClient zumoClient = globalObjects.getZumoClient();


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
            ArrayList<TrackObject> ArrayPlayList = new ArrayList<TrackObject>();
            // looping through All jsonArrayPlaylist


            for (int i = 0; i < jsonArrayPlaylist.length(); i++) {

                JSONObject c = jsonArrayPlaylist.getJSONObject(i);

                TrackObject track = new TrackObject();
                track.setId(c.optInt(TrackObject.TAG_ID,0));
                track.setTrackID(c.optString(TrackObject.TAG_TRACKID,"defaultValue").toString());
                track.setTrackName(c.optString(TrackObject.TAG_TRACKNAME, "defaultValue").toString());
                track.setDislikes(c.optInt(TrackObject.TAG_DISLIKES, 0));
                track.setLikes(c.optInt(TrackObject.TAG_LIKES, 0));
                track.setPosition(c.optInt(TrackObject.TAG_POSITION, 0));
                track.setAlbum(c.optString(TrackObject.TAG_ALBUM, "defaultValue").toString());
                track.setArtist(c.optString(TrackObject.TAG_ARTIST, "defaultValue").toString());

                ArrayPlayList.add(track);

            }



            Collections.sort(ArrayPlayList, new Comparator<TrackObject>(){
                public int compare(TrackObject obj1, TrackObject obj2)
                {
                    // TODO Auto-generated method stub
                    int obj1_poistion=obj1.getPosition();
                    int obj2_poistion=obj2.getPosition();;

                    return (obj1_poistion < obj2_poistion) ? -1: (obj1_poistion >obj2_poistion) ? 1:0 ;
                }
            });


            playListAdapter = new PlayClientAdapter(ArrayPlayList);
            //playlistView = (ListView) findViewById(R.id.clientracks);
            //playlistView.setAdapter(playListAdapter);
            getListView().setAdapter(playListAdapter);
/*
            //SETTO l'setOnItemClickListener per invocare il WCF passando la playlist selezionata
            playlistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adattatore, final View componente, int pos, long id) {


                    final TrackObject track = (TrackObject) adattatore.getItemAtPosition(pos);

                    ImageButton btn_like =(ImageButton) componente.findViewById(R.id.track_btn_like);
                    btn_like.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            int position = getListView().getPositionForView((RelativeLayout)v.getParent());
                            TrackObject ta = (TrackObject)playListAdapter.getItem(position);
                            Log.d("Ti piace: ",ta.getTrackName());
                        }
                    });
                }
            });

*/











        } catch (JSONException e) {
            e.printStackTrace();
        }

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
            Log.d("Muffxxxxa",message);

            if (message != null) {
                try {

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
                        track.setId(c.optInt(TrackObject.TAG_ID,0));
                        track.setTrackID(c.optString(TrackObject.TAG_TRACKID,"defaultValue").toString());
                        track.setTrackName(c.optString(TrackObject.TAG_TRACKNAME, "defaultValue").toString());
                        track.setDislikes(c.optInt(TrackObject.TAG_DISLIKES, 0));
                        track.setLikes(c.optInt(TrackObject.TAG_LIKES, 0));
                        track.setRank(c.optString(TrackObject.TAG_RANK, "defaultValue"));
                        track.setPosition(c.optInt(TrackObject.TAG_POSITION, 0));
                        track.setAlbum(c.optString(TrackObject.TAG_ALBUM, "defaultValue").toString());
                        track.setArtist(c.optString(TrackObject.TAG_ARTIST, "defaultValue").toString());
                        ArrayPlayList.add(track);


                    }


                    Collections.sort(ArrayPlayList, new Comparator<TrackObject>(){
                        public int compare(TrackObject obj1, TrackObject obj2)
                        {
                            // TODO Auto-generated method stub
                            int obj1_poistion=obj1.getPosition();
                            int obj2_poistion=obj2.getPosition();;

                            return (obj1_poistion < obj2_poistion) ? -1: (obj1_poistion >obj2_poistion) ? 1:0 ;
                        }
                    });



                    playListAdapter = new PlayClientAdapter(ArrayPlayList);
                    //playlistView = (ListView) findViewById(R.id.clientracks);
                    //playlistView.setAdapter(playListAdapter);
                    getListView().setAdapter(playListAdapter);

                } catch (JSONException e) {
                    e.printStackTrace();

                }


            }
        }


    };

    public void LikeClickHandler(View v) throws JSONException {

        //reset all the listView items background colours
        //before we set the clicked one..
/*
        ListView lvItems = getListView();
        for (int i=0; i < lvItems.getChildCount(); i++)
        {
            lvItems.getChildAt(i).setBackgroundColor(Color.BLUE);
        }

*/
        //get the row the clicked button is in
        RelativeLayout vwParentRow = (RelativeLayout)v.getParent();

        TextView child = (TextView)vwParentRow.getChildAt(0);
        ImageButton btn = (ImageButton)vwParentRow.getChildAt(9);
        btn.setClickable(false);
       // Button btnChild = (Button)vwParentRow.getChildAt(1);
       // btnChild.setText(child.getText());
       // btnChild.setText("I've been clicked!");
       Log.d("muffaaaa",child.getText().toString());




        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        ;

        // POST request to WCF
        HttpPost request = new HttpPost(SERVICE_URI + "VotePlus");
        request.setHeader("Accept", "application/json");
        request.setHeader("Content-type", "application/json");


        JSONObject json = new JSONObject();

        // Put in it a String field
        int myNum = 0;

        try {
            myNum = Integer.parseInt(child.getText().toString());
            Log.d("muffa: int", ((Object) myNum).toString());
        } catch(NumberFormatException nfe) {
            Log.d("muffa: int", nfe.getMessage().toString());
        }
        json.put("id",myNum) ;
        json.put("usernameid", "paolo.salvati@hotmail.it");

        final String s = json.toString();
        StringEntity entity = null;
        try {
           // s_json = s.toString();
            Log.d("muffa: json", s);
            entity = new StringEntity(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        request.setEntity(entity);

        // Send request to WCF service
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpResponse httpResponse = null;

        try {
            httpResponse = httpClient.execute(request);
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("muffa WebInvokelike", "KO : " + e.toString());
        }
        Log.d("muffa WebInvokelike", "OK : " + httpResponse.getStatusLine().toString());







        // int c = Color.CYAN;

       // vwParentRow.setBackgroundColor(c);
        //vwParentRow.refreshDrawableState();
    }


    public void DislikeClickHandler(View v) throws JSONException {

        //reset all the listView items background colours
        //before we set the clicked one..
/*
        ListView lvItems = getListView();
        for (int i=0; i < lvItems.getChildCount(); i++)
        {
            lvItems.getChildAt(i).setBackgroundColor(Color.BLUE);
        }

*/
        //get the row the clicked button is in
        RelativeLayout vwParentRow = (RelativeLayout)v.getParent();

        TextView child = (TextView)vwParentRow.getChildAt(0);
        ImageButton btn = (ImageButton)vwParentRow.getChildAt(10);
        btn.setClickable(false);
        // Button btnChild = (Button)vwParentRow.getChildAt(1);
        // btnChild.setText(child.getText());
        // btnChild.setText("I've been clicked!");
        Log.d("muffaaaa",child.getText().toString());




        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        ;

        // POST request to WCF
        HttpPost request = new HttpPost(SERVICE_URI + "VoteMinus");
        request.setHeader("Accept", "application/json");
        request.setHeader("Content-type", "application/json");


        JSONObject json = new JSONObject();

        // Put in it a String field
        int myNum = 0;

        try {
            myNum = Integer.parseInt(child.getText().toString());
            Log.d("muffa: int", ((Object) myNum).toString());
        } catch(NumberFormatException nfe) {
            Log.d("muffa: int", nfe.getMessage().toString());
        }
        json.put("id",myNum) ;
        json.put("usernameid", "paolo.salvati@hotmail.it");

        final String s = json.toString();
        StringEntity entity = null;
        try {
            // s_json = s.toString();
            Log.d("muffa: json", s);
            entity = new StringEntity(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        request.setEntity(entity);

        // Send request to WCF service
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpResponse httpResponse = null;

        try {
            httpResponse = httpClient.execute(request);
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("muffa WebInvokelike", "KO : " + e.toString());
        }
        Log.d("muffa WebInvokelike", "OK : " + httpResponse.getStatusLine().toString());







        // int c = Color.CYAN;

        // vwParentRow.setBackgroundColor(c);
        //vwParentRow.refreshDrawableState();
    }



}
