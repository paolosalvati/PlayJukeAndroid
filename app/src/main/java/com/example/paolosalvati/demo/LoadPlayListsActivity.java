package com.example.paolosalvati.demo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.microsoft.windowsazure.mobileservices.ApiJsonOperationCallback;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceUser;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponse;

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


public class LoadPlayListsActivity extends Activity {

    private final static String SERVICE_URI = "http://jukeserver.cloudapp.net/JukeServer.svc/";
    // JSON Node names



    // contacts JSONArray
    JSONArray jsonArrayALLPlaylists = null;
    JSONArray jsonArrayALLTracks = null;
    JSONArray jsonArrayINFOPlaylist = null;

    private ListAdapter playListAdapter = null;

    private ArrayList<HashMap<String, String>> songsList = new ArrayList<HashMap<String, String>>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_play_lists);

        //Save the application Context
        final Context context = this;

        //Get ACS ZUMO Access Token provided by the collaing MenuActivity
        Bundle datipassati = getIntent().getExtras();
        final String zumoAcsToken = datipassati.getString("ZUMO_ACS_TOKEN");
        final String zumoAcsUserId = datipassati.getString("ZUMO_ACS_USER_ID");
        final String spotifyToken = datipassati.getString("SPOTIFY_TOKEN");

         //Insance of final classes Spotify User & Spotify PlayList Objects
        final SpotifyUserObject user = new SpotifyUserObject();
        final ArrayList<SpotifyPlayListObject> ArrayALLPlayList = new ArrayList<SpotifyPlayListObject>();
        final ArrayList<SpotifyTracksObject> ArrayALLTracks = new ArrayList<SpotifyTracksObject>();

        //Recover BSSID (MAC ADDRES of Device) and tore yhem to User Properties
        WifiManager mainWifiObj;
        mainWifiObj = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        user.setwifiBSSID(mainWifiObj.getConnectionInfo().getBSSID());
        user.setwifiMAC(mainWifiObj.getConnectionInfo().getMacAddress());
        user.setwifiSSID(mainWifiObj.getConnectionInfo().getSSID());

        //Debug WIFI Infos
        String wifiBSSID =mainWifiObj.getConnectionInfo().getBSSID();
        final String wifiMAC   =mainWifiObj.getConnectionInfo().getMacAddress();
        String wifiSSID  =mainWifiObj.getConnectionInfo().getSSID();
        TextView wifiBSSIDTextView =(TextView) findViewById(R.id.wifiBSSID);
        TextView wifiMACTextView =(TextView) findViewById(R.id.wifiMAC);
        TextView wifiSSIDTextView =(TextView) findViewById(R.id.wifiSSID);
        wifiBSSIDTextView.setText(wifiBSSID);
        wifiMACTextView.setText(wifiMAC);
        wifiSSIDTextView.setText(wifiSSID);

        //Insance of a  JsonObjent that ll hold all infos for building the header' parameters in the HTTP Call
        final JsonObject input = new JsonObject();
        //Add to the JsonObjent holding headers parameter for the HTTP Call the Spotify Access Token
        input.addProperty("Token", spotifyToken.toString());
        //Debug
        Log.i("Paolo:LoadPlayListsActivity:Token",spotifyToken.toString());
        MobileServiceClient mClient;

        //1] PROVO A CREARE CONNSESSIONE AL ZUMO PER API api_pjk_spotify_get_user
        try
        {
            // Create the Mobile Service Client instance, using the provided
            // Mobile Service URL and key
            mClient = new MobileServiceClient(
                    "https://playjuke.azure-mobile.net/",
                    "faMsCpUEYWcknZULBGywlrFxPBDqDM33",
                    this);

            MobileServiceUser zumoUser =new MobileServiceUser(zumoAcsUserId);
            zumoUser.setAuthenticationToken(zumoAcsToken);
            mClient.setCurrentUser(zumoUser);

           

            //1.A]Invoke ZUMO CUsom API to get users'infos
            mClient.invokeApi("api_pjk_spotify_get_user", input,
                HttpPost.METHOD_NAME, null,
                new ApiJsonOperationCallback()
                {

                    @Override
                    public void onCompleted(JsonElement jsonData,Exception error,ServiceFilterResponse response)
                    {
                        String jsonApiUserResponse = response.getContent().toString();
                        Log.i("Paolo:LoadPlayListsActivity:User", response.getContent().toString());
                        if (jsonApiUserResponse != null)
                        {
                            try
                            {
                                    JSONObject jsonUserObj = new JSONObject(jsonApiUserResponse);
                                    user.setCountry(jsonUserObj.getString(user.TAG_COUNTRY));
                                    Log.i("USER_COUNTRY", user.getCountry());
                                    user.setDisplayname(jsonUserObj.getString(user.TAG_DISPLAY_NAME));
                                    Log.i("USER_DISPLAYNAME", user.getDisplayName());
                                    user.setEmail(jsonUserObj.getString(user.TAG_EMAIL));
                                    Log.i("USER_EMAIL", user.getEmail());
                                    user.setId(jsonUserObj.getString(user.TAG_ID));
                                    Log.i("USER_ID", user.getId());
                                    user.setwifiMAC(wifiMAC);

                                    //2] PROVO A CREARE CONNSESSIONE AL ZUMO PER API api_pjk_spotify_get_playlists
                                    try
                                    {
                                        // Create the Mobile Service Client instance, using the provided
                                        // Mobile Service URL and key
                                        MobileServiceClient mClient2 = new MobileServiceClient(
                                                "https://playjuke.azure-mobile.net/",
                                                "faMsCpUEYWcknZULBGywlrFxPBDqDM33",
                                                getApplicationContext()
                                        );
                                        MobileServiceUser zumoUser =new MobileServiceUser(zumoAcsUserId);
                                        zumoUser.setAuthenticationToken(zumoAcsToken);
                                        mClient2.setCurrentUser(zumoUser);
                                        input.addProperty("User", user.getId());
                                        //2.A]Invoke ZUMO CUsom API to get playlist'infos
                                        mClient2.invokeApi(
                                                "api_pjk_spotify_get_playlists"
                                                ,input
                                                ,HttpPost.METHOD_NAME
                                                ,null
                                                ,new ApiJsonOperationCallback(){
                                                        @Override
                                                        public void onCompleted(JsonElement jsonData, Exception error,ServiceFilterResponse response)
                                                        {
                                                                String jsonApiPlayListrResponse = response.getContent().toString();
                                                                Log.i("Paolo:LoadPlayListsActivity:PlayList", response.getContent().toString());// getAsJsonObject().getAsString());
                                                                if (jsonApiPlayListrResponse != null)
                                                                {
                                                                        try
                                                                        {
                                                                            JSONObject jsonPlayListObj = new JSONObject(jsonApiPlayListrResponse);
                                                                            // Getting JSON Array node
                                                                            jsonArrayALLPlaylists = jsonPlayListObj.getJSONArray(SpotifyPlayListObject.TAG_ITEMS);
                                                                            for (int i = 0; i < jsonArrayALLPlaylists.length(); i++)
                                                                            {
                                                                                JSONObject c = jsonArrayALLPlaylists.getJSONObject(i);
                                                                                SpotifyPlayListObject playlistitem = new SpotifyPlayListObject();
                                                                                playlistitem.setID(c.getString(SpotifyPlayListObject.TAG_ID));
                                                                                Log.i("Paolo:LoadPlayListsActivity:PlayList:ID", playlistitem.getID());
                                                                                playlistitem.setName(c.getString(SpotifyPlayListObject.TAG_NAME));
                                                                                Log.i("Paolo:LoadPlayListsActivity:PlayList:NAME", playlistitem.getName());
                                                                                playlistitem.setUser(user);
                                                                                ArrayALLPlayList.add(playlistitem);
                                                                            }

                                                                            playListAdapter=new PlayListAdapetr(ArrayALLPlayList);
                                                                            ListView allPlayLists = (ListView) findViewById(R.id.allPlayList);
                                                                            allPlayLists.setAdapter(playListAdapter);
                                                                            //SETTO l'setOnItemClickListener per invocare il WCF passando la playlist selezionata
                                                                            allPlayLists.setOnItemClickListener(new  AdapterView.OnItemClickListener()
                                                                            {
                                                                                @Override
                                                                                public void onItemClick(AdapterView<?> adattatore, final View componente, int pos, long id)
                                                                                {
                                                                                        final SpotifyPlayListObject playlistItem =(SpotifyPlayListObject) adattatore.getItemAtPosition(pos);
                                                                                        Log.d("PAOLO","Ho cliccato su"+playlistItem.getName());
                                                                                        Log.d("PAOLO","il MAC è: "+playlistItem.getUser().getwifiMAC());
                                                                                        Log.d("PAOLO","l'utente è "+playlistItem.getUser().getDisplayName());


                                                                                        //INIZIO RECUPERO LE TRACKS



                                                                                    try
                                                                                    {
                                                                                        // Create the Mobile Service Client instance, using the provided
                                                                                        // Mobile Service URL and key
                                                                                        final MobileServiceClient mClient3 = new MobileServiceClient(
                                                                                                "https://playjuke.azure-mobile.net/",
                                                                                                "faMsCpUEYWcknZULBGywlrFxPBDqDM33",
                                                                                                getApplicationContext()
                                                                                        );
                                                                                        MobileServiceUser zumoUser =new MobileServiceUser(zumoAcsUserId);
                                                                                        zumoUser.setAuthenticationToken(zumoAcsToken);
                                                                                        mClient3.setCurrentUser(zumoUser);
                                                                                        input.addProperty("User", user.getId());
                                                                                        input.addProperty("Playlist", playlistItem.getID());
                                                                                        input.addProperty("Token", spotifyToken.toString());
                                                                                        //2.A]Invoke ZUMO CUsom API to get playlist'infos
                                                                                        mClient3.invokeApi(
                                                                                                "api_pjk_spotify_get_tracks"
                                                                                                ,input
                                                                                                ,HttpPost.METHOD_NAME
                                                                                                ,null
                                                                                                ,new ApiJsonOperationCallback(){
                                                                                                    @Override
                                                                                                    public void onCompleted(JsonElement jsonData, Exception error,ServiceFilterResponse response)
                                                                                                    {
                                                                                                        String jsonApiTracksResponse = response.getContent().toString();
                                                                                                        Log.i("Paolo:LoadPlayListsActivity:Track", response.getContent().toString());// getAsJsonObject().getAsString());
                                                                                                        if (jsonApiTracksResponse != null)
                                                                                                        {
                                                                                                            try
                                                                                                            {
                                                                                                                JSONObject jsonTracksObj = new JSONObject(jsonApiTracksResponse);
                                                                                                                // Getting JSON Array node
                                                                                                                jsonArrayALLTracks = jsonTracksObj.getJSONArray(SpotifyTracksObject.TAG_ITEMS);
                                                                                                                for (int i = 0; i < jsonArrayALLTracks.length(); i++)
                                                                                                                {
                                                                                                                    JSONObject c = jsonArrayALLTracks.getJSONObject(i);
                                                                                                                    JSONObject d = c.getJSONObject(SpotifyTracksObject.TAG_TRACKS);
                                                                                                                    SpotifyTracksObject tracktitem = new SpotifyTracksObject();
                                                                                                                    tracktitem.setID(d.getString(SpotifyTracksObject.TAG_ID));
                                                                                                                    tracktitem.setName(d.getString(SpotifyTracksObject.TAG_NAME));
                                                                                                                    Log.i("PaoloA:LoadPlayListsActivity:Track:ID", tracktitem.getID());
                                                                                                                    Log.i("PaoloA:LoadPlayListsActivity:Track:NAME", tracktitem.getName());
                                                                                                                    ArrayALLTracks.add(tracktitem);

                                                                                                                    HashMap<String, String> song = new HashMap<String, String>();
                                                                                                                    song.put("songTitle",tracktitem.getName());
                                                                                                                    song.put("songPath",  tracktitem.getID());
                                                                                                                    Log.i("PaoloA:LoadPlayListsActivity:Track","1");
                                                                                                                    // Adding each song to SongList
                                                                                                                    songsList.add(song);
                                                                                                                    Log.i("PaoloA:LoadPlayListsActivity:Track","2");

                                                                                                                }
                                                                                                                Log.i("PaoloA:LoadPlayListsActivity:Track","3");
                                                                                                                Intent loadPlayActivityIntent = new Intent(getApplicationContext(), PlayActivity.class);
                                                                                                                Log.i("PaoloA:LoadPlayListsActivity:Track","4");
                                                                                                                loadPlayActivityIntent.putExtra("ZUMO_ACS_USER_ID",mClient3.getCurrentUser().getUserId());
                                                                                                                Log.i("PaoloA:LoadPlayListsActivity:Track","5");
                                                                                                                loadPlayActivityIntent.putExtra("ZUMO_ACS_TOKEN",mClient3.getCurrentUser().getAuthenticationToken());
                                                                                                                Log.i("PaoloA:LoadPlayListsActivity:Track","6");

                                                                                                                loadPlayActivityIntent.putExtra("SPOTIFY_TOKEN",spotifyToken.toString());
                                                                                                                Log.i("PaoloA:LoadPlayListsActivity:Track","6");


                                                                                                                loadPlayActivityIntent.putExtra("SONGS",songsList);
                                                                                                                startActivity(loadPlayActivityIntent);
                                                                                                                //loadPlayActivityIntent.putExtra("ZUMO_ACS_TOKEN", user.getId());
                                                                                                                Log.i("PaoloA:LoadPlayListsActivity:Track","7");

                                                                                                            } catch (JSONException e)
                                                                                                            {
                                                                                                                e.printStackTrace();
                                                                                                                Log.i("BBBBB",e.toString());
                                                                                                            }
                                                                                                        } else
                                                                                                        {
                                                                                                            Log.e("Paolo:LoadPlayListsActivity:User", "Error Calling ZUMO API api_pjk_spotify_get_playlists");
                                                                                                        }
                                                                                                    }
                                                                                                }
                                                                                        );
                                                                                        //catch per punto 2]
                                                                                    } catch (MalformedURLException e)
                                                                                    {
                                                                                        Log.e("onCrmeate",e.getMessage().toString());
                                                                                    }

































































































                                                                                    //FINE RECUPERO LE TRACKS










                                                                                        if (android.os.Build.VERSION.SDK_INT > 9)
                                                                                        {
                                                                                            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                                                                                            StrictMode.setThreadPolicy(policy);
                                                                                        };
                                                                                        // POST request to WCF
                                                                                        HttpPost request = new HttpPost(SERVICE_URI + "CreatePub");
                                                                                        request.setHeader("Accept", "application/json");
                                                                                        request.setHeader("Content-type", "application/json");
                                                                                        // Build JSON string
                                                                                        JSONStringer PubInfo = null;
                                                                                        try
                                                                                        {
                                                                                                 //
                                                                                                 //   PubInfo = new JSONStringer().object()

                                                                                                 //           .key("PubInfo")
                                                                                                 //           .object()
                                                                                                 //           .key("PlaylistId").value("paolo")
                                                                                                 //           .key("UserId").value("aaa")
                                                                                                 //           .key("Mac").value("aaa")
                                                                                                  //          .endObject()
                                                                                                  //          .endObject();

                                                                                                    PubInfo = new JSONStringer().object()
                                                                                                           .key("PlaylistId").value(playlistItem.getID())
                                                                                                           .key("UserId").value(playlistItem.getUser().getDisplayName())
                                                                                                           .key("Mac").value(playlistItem.getUser().getwifiMAC())
                                                                                                            .endObject();


                                                                                        } catch (JSONException e)
                                                                                        {
                                                                                            e.printStackTrace();
                                                                                        }
                                                                                        StringEntity entity = null;
                                                                                        try
                                                                                        {
                                                                                            entity = new StringEntity(PubInfo.toString());
                                                                                        } catch (UnsupportedEncodingException e)
                                                                                        {
                                                                                            e.printStackTrace();
                                                                                        }
                                                                                        request.setEntity(entity);
                                                                                        Log.d("WebInvoke", "req : " +PubInfo.toString() );
                                                                                        // Send request to WCF service
                                                                                        DefaultHttpClient httpClient = new DefaultHttpClient();
                                                                                        HttpResponse httpResponse = null;
                                                                                        try
                                                                                        {
                                                                                            httpResponse = httpClient.execute(request);
                                                                                        } catch (IOException e)
                                                                                        {
                                                                                            e.printStackTrace();
                                                                                            Log.d("WebInvoke", "Saving : " + e.toString());
                                                                                        }
                                                                                        Log.d("WebInvoke", "Saving : " + httpResponse.getStatusLine().toString());
                                                                                }
                                                                            });
                                                                        } catch (JSONException e)
                                                                        {
                                                                        e.printStackTrace();
                                                                        }
                                                                } else
                                                                {
                                                                    Log.e("Paolo:LoadPlayListsActivity:User", "Error Calling ZUMO API api_pjk_spotify_get_playlists");
                                                                }
                                                        }
                                                }
                                        );
                                    //catch per punto 2]
                                    } catch (MalformedURLException e)
                                    {
                                        Log.e("onCreate",e.getMessage().toString());
                                    }
                            } catch (JSONException e)
                            {
                                e.printStackTrace();
                            }
                        } else
                        {
                            Log.e("Paolo:LoadPlayListsActivity:User", "Error Calling ZUMO API api_pjk_spotify_get_user");
                        }
                     }
                });
        //catch per punto 1]
        } catch (MalformedURLException e)
        {
            Log.e("onCreate",e.getMessage().toString());
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


}
