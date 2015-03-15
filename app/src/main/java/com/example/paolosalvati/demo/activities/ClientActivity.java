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

import com.example.paolosalvati.demo.R;
import com.example.paolosalvati.demo.adapters.PlayClientAdapter;
import com.example.paolosalvati.demo.dataClasses.PlayListObject;
import com.example.paolosalvati.demo.dataClasses.TrackObject;
import com.example.paolosalvati.demo.dataClasses.TracksArrayObject;
import com.example.paolosalvati.demo.handlers.HubHandler;
import com.example.paolosalvati.demo.jsonWcf.JsonParserObject;
import com.example.paolosalvati.demo.utilities.GlobalObjects;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.microsoft.windowsazure.messaging.NotificationHub;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.notifications.NotificationsManager;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;


public class ClientActivity extends ListActivity {

    private PlayListObject playListObject;
    private TracksArrayObject tracksArrayObject;

    private final static String SERVICE_URI = "http://jukeserver.cloudapp.net/JukeSvc.svc/";
    Context context = this;

    private String SENDER_ID = "823747579189";
    private GoogleCloudMessaging gcm;
    private NotificationHub hub;


    //private ListView playlistView;
    private ListAdapter playListAdapter = null;


    // contacts JSONArray
    //JSONArray jsonArrayALLPlaylists = null;
    //JSONArray jsonArrayALLTracks = null;
    //JSONArray jsonArrayINFOPlaylist = null;
    //int trackPos =0;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_client);

        //Save the application Context
        final Context context = this;

        //Get Connection to ZUMO client
        final GlobalObjects globalObjects = ((GlobalObjects) getApplicationContext());
        final MobileServiceClient zumoClient = globalObjects.getZumoClient();

        //Get ACS ZUMO Access Token provided by the collaing MenuActivity
        Bundle datipassati = getIntent().getExtras();
        final String songs = datipassati.getString("SONGS");
        Log.d("SONGSxxx",songs);

        //Connect to GCM GOOGLE NOTIFICATION MESSAGE AND AZURE HUB
        NotificationsManager.handleNotifications(this, SENDER_ID, HubHandler.class);
        gcm = GoogleCloudMessaging.getInstance(this);
        String connectionString = "Endpoint=sb://playhub.servicebus.windows.net/;SharedAccessKeyName=DefaultListenSharedAccessSignature;SharedAccessKey=/dLPV75NdWnCRKADlS1nqU0hl2MySOpqDhgJeVQmdJw=";
        hub = new NotificationHub("playhub", connectionString, this);
        registerWithNotificationHubs();


        try {

            JsonParserObject jsonParserObject = new JsonParserObject();
            playListObject = jsonParserObject.jsonClientRegistrationResponseGetPlaylist(songs);
            tracksArrayObject =  jsonParserObject.jsonClientRegistrationResponseGetTracks(songs);
            List<TrackObject> arrayPlayList;
            arrayPlayList= tracksArrayObject.getTracksList();


            //Show Track List
            playListAdapter = new PlayClientAdapter(arrayPlayList);
            getListView().setAdapter(playListAdapter);

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



                        JsonParserObject jsonParserObject = new JsonParserObject();
                        playListObject = jsonParserObject.jsonClientRegistrationResponseGetPlaylist(message);
                        tracksArrayObject =  jsonParserObject.jsonClientRegistrationResponseGetTracks(message);
                        List<TrackObject> arrayPlayList;
                        arrayPlayList= tracksArrayObject.getTracksList();


                        //Show Track List
                        playListAdapter = new PlayClientAdapter(arrayPlayList);
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
