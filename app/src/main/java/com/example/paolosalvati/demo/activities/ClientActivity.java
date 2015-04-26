package com.example.paolosalvati.demo.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.paolosalvati.demo.R;
import com.example.paolosalvati.demo.adapters.CustomListAdapter;
import com.example.paolosalvati.demo.dataClasses.PlayListObject;
import com.example.paolosalvati.demo.dataClasses.TrackObject;
import com.example.paolosalvati.demo.dataClasses.TracksArrayObject;
import com.example.paolosalvati.demo.handlers.HubHandler;
import com.example.paolosalvati.demo.jsonWcf.JsonParserObject;
import com.example.paolosalvati.demo.sqlLite.DbAdapter;
import com.example.paolosalvati.demo.utilities.GlobalObjects;
import com.example.paolosalvati.demo.utilities.SwipeDetector;
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


//SWIPE

import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

import com.example.paolosalvati.demo.sqlLite.*;

public class ClientActivity extends Activity {

    private DbAdapter dbHelper;
    private Cursor cursor;

    private ListView listview;


    private PlayListObject playListObject;
    private TracksArrayObject tracksArrayObject;

    private final static String SERVICE_URI = "http://jukeserver.cloudapp.net/JukeSvc.svc/";
    Context context = this;

    private String SENDER_ID = "823747579189";
    private GoogleCloudMessaging gcm;
    private NotificationHub hub;



    private CustomListAdapter playListAdapter = null;
    //private ArrayList manageUserLike = new ArrayList();
    //private ArrayList manageUserUnlike = new ArrayList();



    // Messages for Handler
    public static final int MSG_UPDATE_ADAPTER 		= 0;
    public static final int MSG_CHANGE_ITEM 		= 1;
    public static final int MSG_ANIMATION_REMOVE 	= 2;

    // Messages for the context menu
    public static final int MSG_REMOVE_ITEM 		= 10;
    public static final int MSG_RENAME_ITEM 		= 11;


    private Handler handler = new Handler() {
        public void handleMessage(Message msg,int position) {
            Log.d("Swipe",msg.toString());
            switch (msg.what)
            {

                /*case MSG_UPDATE_ADAPTER: // ListView updating
                    playListAdapter. notifyDataSetChanged();
                    setCountPurchaseProduct();
                    break;
                case MSG_CHANGE_ITEM: // Do / not do case
                    ToDoItem item = list.get(msg.arg1);
                    item.setCheck(!item.isCheck());
                    Utils.sorting(list, 0);
                    saveList();
                    adapter.notifyDataSetChanged();
                    setCountPurchaseProduct();
                    break;
                    */
                case MSG_ANIMATION_REMOVE: // Start animation removing
                    View view = (View)msg.obj;
                    view.startAnimation(getDeleteAnimation(0, (msg.arg2 == 0) ? -view.getWidth() : 2 * view.getWidth(), msg.arg1));
                    playListAdapter.notifyDataSetChanged();

                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_client);


        setContentView(R.layout.main);



        listview = (ListView)findViewById(R.id.listview);


        //Save the application Context
        final Context context = this;

        //Get Connection to ZUMO client
        final GlobalObjects globalObjects = ((GlobalObjects) getApplicationContext());
        final MobileServiceClient zumoClient = globalObjects.getZumoClient();


        dbHelper = new DbAdapter(this);






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
            //playListAdapter = new PlayClientAdapter(arrayPlayList);
            playListAdapter = new CustomListAdapter(arrayPlayList,this);
            listview.setAdapter(playListAdapter);

            Log.d("Swipe","aaa");
            final SwipeDetector swipeDetector = new SwipeDetector();
            listview.setOnTouchListener(swipeDetector);
            Log.d("Swipe","bbbb");
            listview.setOnItemClickListener(new OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position,
                                        long id) {
                    Log.d("Swipe","onItemClick");
                    Message msg = new Message();



                    msg.arg1 = position - 1;// If was detected swipe we delete an item
                    if (swipeDetector.swipeDetected()){
                        if (swipeDetector.getAction() == SwipeDetector.Action.LR ||
                                swipeDetector.getAction() == SwipeDetector.Action.LR)
                        {
                            ListView vwParentRow = (ListView)view.getParent();
                            LinearLayout ll = (LinearLayout)vwParentRow.getChildAt(position);
                            //TextView child = (TextView)ll.getChildAt(0);
                            //child.setText("left");
                            //ll.setBackgroundColor(Color.RED);
                            ImageView child = (ImageView)ll.getChildAt(3);
                            child.setImageResource (R.drawable.unlikeicon);
                            ll.setClickable(false);
                            Log.d("muffa","like");
                            TextView tw = (TextView)ll.getChildAt(0);
                            Integer idTrack = Integer.parseInt( tw.getText().toString());
                            // manageUserLike.add(tw.getText().toString());
                            dbHelper.open();
                            long _id = dbHelper.createPreference("spotify", idTrack,0,1);
                            cursor = dbHelper.fetchPreferencesByFilter(idTrack);

                            while ( cursor.moveToNext() ) {
                                Log.d(DatabaseStrings.FIELD_LIKE, "muffaffffff idTrack = " + idTrack.toString());
                                Integer iLike = cursor.getInt(cursor.getColumnIndex(DatabaseStrings.FIELD_LIKE));
                                Log.d(DatabaseStrings.FIELD_LIKE, "muffaffffff ilike = " + iLike.toString());
                                Integer iUnLike = cursor.getInt(cursor.getColumnIndex(DatabaseStrings.FIELD_UN_LIKE));
                                Log.d(DatabaseStrings.FIELD_LIKE, "muffaffffff iUnlike = " + iUnLike.toString());
                            }
                            cursor.close();

                            dbHelper.close();


                            Log.d("muffa","like ok");
                            msg.what = MSG_ANIMATION_REMOVE;
                            msg.arg2 = swipeDetector.getAction() == SwipeDetector.Action.LR ? 1 : 0;
                            msg.obj = view;




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
                                TextView track = (TextView)ll.getChildAt(0);
                                myNum = Integer.parseInt(track.getText().toString());
                                Log.d("muffa: int", ((Object) myNum).toString());
                            } catch(NumberFormatException nfe) {
                                Log.d("muffa: int", nfe.getMessage().toString());
                            }
                            try {
                                json.put("id",myNum) ;
                                json.put("usernameid", "paolo.salvati@hotmail.it");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


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







                        }
                        if (swipeDetector.getAction() == SwipeDetector.Action.RL ||
                                swipeDetector.getAction() == SwipeDetector.Action.RL)
                        {
                            ListView vwParentRow = (ListView)view.getParent();
                            LinearLayout ll = (LinearLayout)vwParentRow.getChildAt(position);
                            //ll.setBackgroundColor(Color.GREEN);
                            ImageView child = (ImageView)ll.getChildAt(3);
                            child.setImageResource(R.drawable.rockandrollicon);
                            //child.setText("right");
                            ll.setClickable(false);
                            Log.d("muffa","unlike");
                            TextView tw = (TextView)ll.getChildAt(0);
                            //manageUserLike.add(tw.getText().toString());
                            Integer idTrack = Integer.parseInt( tw.getText().toString());
                            dbHelper.open();
                            long _id = dbHelper.createPreference("spotify", idTrack,1,0);
                            cursor = dbHelper.fetchPreferencesByFilter(idTrack);

                            while ( cursor.moveToNext() ) {
                                Log.d(DatabaseStrings.FIELD_LIKE, "muffaffffff idTrack = " + idTrack.toString());
                                Integer iLike = cursor.getInt(cursor.getColumnIndex(DatabaseStrings.FIELD_LIKE));
                                Log.d(DatabaseStrings.FIELD_LIKE, "muffaffffff ilike = " + iLike.toString());
                                Integer iUnLike = cursor.getInt(cursor.getColumnIndex(DatabaseStrings.FIELD_UN_LIKE));
                                Log.d(DatabaseStrings.FIELD_LIKE, "muffaffffff iUnlike = " + iUnLike.toString());
                            }
                            cursor.close();

                            dbHelper.close();


                            Log.d("muffa","unlike ok");
                            msg.what = MSG_ANIMATION_REMOVE;
                            msg.arg2 = swipeDetector.getAction() == SwipeDetector.Action.RL ? 1 : 0;
                            msg.obj = view;


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
                                TextView track = (TextView)ll.getChildAt(0);
                                myNum = Integer.parseInt(track.getText().toString());
                                Log.d("muffa: int", ((Object) myNum).toString());
                            } catch(NumberFormatException nfe) {
                                Log.d("muffa: int", nfe.getMessage().toString());
                            }
                            try {
                                json.put("id",myNum) ;
                                json.put("usernameid", "paolo.salvati@hotmail.it");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


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





                        }
                    }

                    // Otherwise, select an item
                    else

                        msg.what = MSG_CHANGE_ITEM;

                    handler.sendMessage(msg);
                }
            });



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

                        //Retrive info user likes or unilikes tracks
                    dbHelper.open();
                    for (int i=0;i<tracksArrayObject.getTracksList().size();i++){

                        Integer idTrack = tracksArrayObject.getTracksList().get(i).getId();
                        cursor = dbHelper.fetchPreferencesByFilter(idTrack);

                        Integer iLike=0;
                        Integer iUnLike=0;

                        while ( cursor.moveToNext() ) {


                            iLike = cursor.getInt(cursor.getColumnIndex(DatabaseStrings.FIELD_LIKE));

                            Log.d(DatabaseStrings.FIELD_LIKE, "muffaffffff ilike = " + iLike.toString());

                            iUnLike = cursor.getInt(cursor.getColumnIndex(DatabaseStrings.FIELD_UN_LIKE));

                            Log.d(DatabaseStrings.FIELD_LIKE, "muffaffffff iUnlike = " + iUnLike.toString());


                            if(iLike==1) {
                                tracksArrayObject.getTracksList().get(i).setILike("Y");
                                tracksArrayObject.getTracksList().get(i).setIUnLike("N");
                            }
                            else if(iUnLike==1) {
                                tracksArrayObject.getTracksList().get(i).setILike("N");
                                tracksArrayObject.getTracksList().get(i).setIUnLike("Y");
                            }


                        }
                        cursor.close();





                    }
                    dbHelper.close();

                    //Show Track List
                    playListAdapter = new CustomListAdapter(arrayPlayList,context);
                    listview.setAdapter(playListAdapter);
                    for (int i=0;i<arrayPlayList.size();i++){
                    Log.d("muffa",((Object)(arrayPlayList.get(i).getPosition())).toString());
                    Log.d("muffa",arrayPlayList.get(i).getTrackName());
                    }
                    playListAdapter.notifyDataSetChanged();







/*
                        int tot = playListAdapter.getCount();
                        for (int i=0;i<tot;i++){
                            Log.d("muffa","uuuu1");
                            ListView lv = (ListView) findViewById(R.id.listview);
                            Log.d("muffa","uuuu2");
                            TextView tw = (TextView)lv.getChildAt(0);
                            Log.d("muffa","uuuu3");


                            if(manageUserLike.contains(tw.getText().toString())) {


                                Log.d("muffa","fffff");
                                lv.getChildAt(i).setClickable(false);
                                ImageView iv = (ImageView)lv.getChildAt(3).findViewById(R.id.listitem_like_icon);
                                iv.setImageResource(R.drawable.rockandrollicon);

                            }
                            else if(manageUserUnlike.contains(tw.getText().toString())) {
                           // else if(1==0) {

                                lv.getChildAt(i).setClickable(false);
                                Log.d("muffa","gggg");
                                ImageView iv = (ImageView)lv.getChildAt(3).findViewById(R.id.listitem_like_icon);
                                iv.setImageResource(R.drawable.unlikeicon);
                            }

                        }

*/


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
        Log.d("muffaaaa","XXXX");

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

/*
    public void DislikeClickHandler(View v) throws JSONException {

        //reset all the listView items background colours
        //before we set the clicked one..

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

*/
    /**
     * Starting animation remove
     */
    private Animation getDeleteAnimation(float fromX, float toX, int position)
    {
        Log.d("Swipe","getDeleteAnimation");
        Animation animation = new TranslateAnimation(fromX, toX, 0, 0);
        animation.setStartOffset(100);
        animation.setDuration(800);
        animation.setAnimationListener(new DeleteAnimationListenter(position));
        animation.setInterpolator(AnimationUtils.loadInterpolator(this, android.R.anim.accelerate_decelerate_interpolator));
        return animation;
    }

    /**
     * Listenter used to remove an item after the animation has finished remove
     */
    public class DeleteAnimationListenter implements Animation.AnimationListener
    {

        private int position;
        public DeleteAnimationListenter(int position)
        {
            this.position = position;
            Log.d("Swipe","1");
        }
        @Override
        public void onAnimationEnd(Animation arg0) {
            //removeItem(position);


            Log.d("Swipe","2");
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

            Log.d("Swipe","3");
        }

        @Override
        public void onAnimationStart(Animation animation) {
            Log.d("Swipe","4");
        }
    }

}
