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
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponse;

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
import java.util.HashMap;
import java.util.List;


public class LoadPlayListsActivity extends Activity {

    private  String SERVICE_URI;// = "http://jukeserver.cloudapp.net/JukeSvc.svc/";

    // contacts JSONArray
    JSONArray jsonArrayALLPlaylists = null;
    JSONArray jsonArrayALLTracks = null;

    JSONObject jsonObjectToSpotify;
    JSONArray jsonArrayToSpotify;


    private ListAdapter playListAdapter = null;

    private ArrayList<HashMap<String, String>> songsList = new ArrayList<HashMap<String, String>>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_play_lists);

        SERVICE_URI = getApplication().getString(R.string.azure_wcf_service_uri);

        final GlobalObjects globalObjects = ((GlobalObjects) getApplicationContext());
        final MobileServiceClient zumoClient = globalObjects.getZumoClient();
        final String spotifyToken = globalObjects.getSpotifyAccessToken();

        //Save the application Context
        final Context context = this;

        //Get ACS ZUMO Access Token provided by the collaing MenuActivity
        //Bundle datipassati = getIntent().getExtras();
        //final String zumoAcsToken = datipassati.getString("ZUMO_ACS_TOKEN");
        //final String zumoAcsUserId = datipassati.getString("ZUMO_ACS_USER_ID");
        //final String spotifyToken = datipassati.getString("SPOTIFY_TOKEN");

        //Insance of final classes Spotify User & Spotify PlayList Objects
        final SpotifyUserObject spotifyUserObject = new SpotifyUserObject();
        final ArrayList<SpotifyPlayListObject> ArrayALLPlayList = new ArrayList<SpotifyPlayListObject>();
        final ArrayList<SpotifyTracksObject> ArrayALLTracks = new ArrayList<SpotifyTracksObject>();

        //Recover BSSID (MAC ADDRES of Device) and tore yhem to User Properties
        WifiManager mainWifiObj;
        mainWifiObj = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        final String wifiBSSID =mainWifiObj.getConnectionInfo().getBSSID();
        final String wifiMAC   =mainWifiObj.getConnectionInfo().getMacAddress();
        final String wifiSSID  =mainWifiObj.getConnectionInfo().getSSID();

        final UserObject userObject = new UserObject();
        userObject.setAuthProvider("Facebook");
        userObject.setMac(wifiMAC);
        userObject.setSsid(wifiSSID);
        userObject.setOs("ANdroid");
        userObject.setUsernameID("paolo.salvati@hotmail.it");



        //Debug WIFI Infos
        TextView wifiBSSIDTextView =(TextView) findViewById(R.id.wifiBSSID);
        TextView wifiMACTextView =(TextView) findViewById(R.id.wifiMAC);
        TextView wifiSSIDTextView =(TextView) findViewById(R.id.wifiSSID);
        wifiBSSIDTextView.setText(wifiBSSID);
        wifiMACTextView.setText(wifiMAC);
        wifiSSIDTextView.setText(wifiSSID);

        //Insance of a  JsonObjent that ll hold all infos for building the header' parameters in the HTTP Call
        final JsonObject input = new JsonObject();
        //Add to the JsonObjent holding headers parameter for the HTTP Call the Spotify Access Token
        input.addProperty("Token", spotifyToken);



        //1.A]Invoke ZUMO CUsom API to get users'infos
        zumoClient.invokeApi("api_pjk_spotify_get_user", input,
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
                                spotifyUserObject.setCountry(jsonUserObj.getString(SpotifyUserObject.TAG_COUNTRY));
                                spotifyUserObject.setDisplayname(jsonUserObj.getString(SpotifyUserObject.TAG_DISPLAY_NAME));
                                spotifyUserObject.setDisplayname(jsonUserObj.getString(SpotifyUserObject.TAG_DISPLAY_NAME));
                                spotifyUserObject.setEmail(jsonUserObj.getString(SpotifyUserObject.TAG_EMAIL));
                                spotifyUserObject.setId(jsonUserObj.getString(SpotifyUserObject.TAG_ID));

                                        //2.A]Invoke ZUMO CUsom API to get playlist'infos
                                        input.addProperty("User", spotifyUserObject.getId());

                                        zumoClient.invokeApi(
                                                "api_pjk_spotify_get_playlists"
                                                , input
                                                , HttpPost.METHOD_NAME
                                                , null
                                                , new ApiJsonOperationCallback() {
                                                    @Override
                                                    public void onCompleted(JsonElement jsonData, Exception error, ServiceFilterResponse response) {
                                                        String jsonApiPlayListrResponse = response.getContent().toString();
                                                        Log.i("Paolo:LoadPlayListsActivity:PlayList", response.getContent().toString());// getAsJsonObject().getAsString());
                                                        if (jsonApiPlayListrResponse != null) {
                                                            try {
                                                                JSONObject jsonPlayListObj = new JSONObject(jsonApiPlayListrResponse);
                                                                // Getting JSON Array node
                                                                jsonArrayALLPlaylists = jsonPlayListObj.getJSONArray(SpotifyPlayListObject.TAG_ITEMS);
                                                                for (int i = 0; i < jsonArrayALLPlaylists.length(); i++) {
                                                                    JSONObject c = jsonArrayALLPlaylists.getJSONObject(i);
                                                                    SpotifyPlayListObject spotifyPlaylistObject = new SpotifyPlayListObject();
                                                                    spotifyPlaylistObject.setID(c.getString(SpotifyPlayListObject.TAG_ID));
                                                                    spotifyPlaylistObject.setName(c.getString(SpotifyPlayListObject.TAG_NAME));
                                                                    ArrayALLPlayList.add(spotifyPlaylistObject);
                                                                }

                                                                playListAdapter = new PlayListAdapetr(ArrayALLPlayList);
                                                                ListView allPlayLists = (ListView) findViewById(R.id.allPlayList);
                                                                allPlayLists.setAdapter(playListAdapter);
                                                                //SETTO l'setOnItemClickListener per invocare il WCF passando la playlist selezionata
                                                                allPlayLists.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                                    @Override
                                                                    public void onItemClick(AdapterView<?> adattatore, final View componente, int pos, long id) {


                                                                        final SpotifyPlayListObject playlistItem = (SpotifyPlayListObject) adattatore.getItemAtPosition(pos);

                                                                        //Costruisco ogetto Playlist
                                                                        final PlayListObject playListObject = new PlayListObject();
                                                                        playListObject.setProvider("Spotify");
                                                                        playListObject.setVersion("1.0");
                                                                        playListObject.setPlaylistID(playlistItem.getID());
                                                                        playListObject.setPlaylistuserID(spotifyUserObject.getId());
                                                                        playListObject.setPlaylistName(playlistItem.getName());
                                                                        playListObject.setProvider("Spotify");
                                                                        playListObject.setVersion("1.0");


                                                                        //INIZIO RECUPERO LE TRACKS

                                                                        input.addProperty("User", spotifyUserObject.getId());
                                                                        input.addProperty("Playlist", playlistItem.getID());
                                                                        input.addProperty("Token", spotifyToken);
                                                                        //2.A]Invoke ZUMO CUsom API to get playlist'infos
                                                                        zumoClient.invokeApi(
                                                                                "api_pjk_spotify_get_tracks"
                                                                                , input
                                                                                , HttpPost.METHOD_NAME
                                                                                , null
                                                                                , new ApiJsonOperationCallback() {
                                                                                    @Override
                                                                                    public void onCompleted(JsonElement jsonData, Exception error, ServiceFilterResponse response) {
                                                                                        String jsonApiTracksResponse = response.getContent().toString();
                                                                                        Log.i("Paolo:LoadPlayListsActivity:Track", response.getContent().toString());// getAsJsonObject().getAsString());
                                                                                        if (jsonApiTracksResponse != null) {
                                                                                            List<TrackObject> trackInfos = new ArrayList<TrackObject>();
                                                                                            try {
                                                                                                JSONObject jsonTracksObj = new JSONObject(jsonApiTracksResponse);
                                                                                                // Getting JSON Array node
                                                                                                jsonArrayALLTracks = jsonTracksObj.getJSONArray(SpotifyTracksObject.TAG_ITEMS);
                                                                                                for (int i = 0; i < jsonArrayALLTracks.length(); i++) {
                                                                                                    JSONObject c = jsonArrayALLTracks.getJSONObject(i);
                                                                                                    JSONObject d = c.getJSONObject(SpotifyTracksObject.TAG_TRACKS);
                                                                                                    SpotifyTracksObject spotifyTrackObject = new SpotifyTracksObject();
                                                                                                    spotifyTrackObject.setID(d.getString(SpotifyTracksObject.TAG_ID));
                                                                                                    spotifyTrackObject.setName(d.getString(SpotifyTracksObject.TAG_NAME));
                                                                                                    Log.d("Webinvoke",d.getString(SpotifyTracksObject.TAG_NAME));

                                                                                                    TrackObject track = new TrackObject();
                                                                                                    //track.setPosition(((Object) i).toString());
                                                                                                    track.setPosition(i);
                                                                                                    track.setLikes(5);
                                                                                                    track.setDislikes(2);
                                                                                                    track.setTrackID(spotifyTrackObject.getID());
                                                                                                    track.setTrackName(spotifyTrackObject.getName());
                                                                                                    track.setArtist("a");
                                                                                                    track.setAlbum("a");
                                                                                                    trackInfos.add(track);

                                                                                                }


                                                                                                // Creating root JSONObject
                                                                                                JSONObject json = new JSONObject();
                                                                                                JSONObject json_user = new JSONObject();
                                                                                                JSONObject json_playlist = new JSONObject();
                                                                                                // Put in it a String field

                                                                                                json_user.put(UserObject.TAG_MAC, userObject.getMac());
                                                                                                json_user.put(UserObject.TAG_BSSID, userObject.getSsid());
                                                                                                json_user.put(UserObject.TAG_AUTHPROVIDER,userObject.getAuthProvider());

                                                                                                json_user.put(UserObject.TAG_USERNAMEID, userObject.getUsernameID());
                                                                                                json_user.put(UserObject.TAG_OS, userObject.getOs());

                                                                                                json.put(UserObject.TAG_USER, json_user);

                                                                                                json_playlist.put(PlayListObject.TAG_PROVIDER, playListObject.getProvider());
                                                                                                json_playlist.put(PlayListObject.TAG_VERSION, playListObject.getVersion());
                                                                                                json_playlist.put(PlayListObject.TAG_PLAYLISTID, playListObject.getPlaylistID());
                                                                                                json_playlist.put(PlayListObject.TAG_PLAYLISTNAME, playListObject.getPlaylistName());
                                                                                                json_playlist.put(PlayListObject.TAG_PLAYLISTUSERID, playListObject.getPlaylistuserID());
                                                                                                Log.d("WebInvoke user",PlayListObject.TAG_PLAYLISTUSERID+ playListObject.getPlaylistuserID());
                                                                                                json.put(PlayListObject.TAG_PLAYLIST, json_playlist);

                                                                                                // Creating a JSONArray
                                                                                                JSONArray arr_to_wcf = new JSONArray();
                                                                                                //String arr_to_spotify ="{\"uris\": [";
                                                                                                String arr_to_spotify ="";
                                                                                                int pos = 0;
                                                                                                //Creating the element to populate the array
                                                                                                for (TrackObject track : trackInfos) {

                                                                                                    Log.i("PaoloB:", "5.1");
                                                                                                    JSONObject element = new JSONObject();

                                                                                                    String trackName = track.getTrackName();

                                                                                                    String[] invalid_characters = {"&",".", "#", "$", "!", "?", "'", ","};

                                                                                                    for(String str : invalid_characters){
                                                                                                        trackName = trackName.replace(str, "");
                                                                                                    }




                                                                                                    element.put(TrackObject.TAG_TRACKNAME, trackName);
                                                                                                    element.put(TrackObject.TAG_TRACKID, track.getTrackID());
                                                                                                    element.put(TrackObject.TAG_POSITION, pos);
                                                                                                    element.put(TrackObject.TAG_ALBUM, track.getAlbum());
                                                                                                    element.put(TrackObject.TAG_ARTIST, track.getArtist());

                                                                                                    // Put it in the array
                                                                                                    arr_to_wcf.put(element);
                                                                                                    //{"uris": ["spotify:track:4iV5W9uYEdYUVa79Axb7Rh", "spotify:track:1301WleyT98MSxVHPZCA6M"]}

                                                                                                    //Uri to spotify
                                                                                                    if(pos==0)

                                                                                                        arr_to_spotify=arr_to_spotify+"spotify:track:"+track.getTrackID();
                                                                                                    else
                                                                                                        arr_to_spotify=arr_to_spotify+",spotify:track:"+track.getTrackID();


                                                                                                    pos = pos + 1;
                                                                                                }


                                                                                                // Put the array and other fileds in the root JSONObject
                                                                                                json.put(PlayListObject.TAG_TRACKS, arr_to_wcf);


                                                                                               // arr_to_spotify=arr_to_spotify+"]}";
                                                                                                arr_to_spotify=arr_to_spotify+",spotify:track:3wRJ8kRFf3czgTR5QePcKP";
                                                                                                Log.d("arr_to_spotify",arr_to_spotify);
                                                                                                // Get the JSON String
                                                                                                final String s = json.toString();
                                                                                                                                                                                              // Get formatted and indented JSON String
                                                                                                String s2 = json.toString(4);


                                                                                                if (android.os.Build.VERSION.SDK_INT > 9) {
                                                                                                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                                                                                                    StrictMode.setThreadPolicy(policy);
                                                                                                }
                                                                                                ;

                                                                                                // POST request to WCF
                                                                                                HttpPost request = new HttpPost(SERVICE_URI + "LoadPlaylist");
                                                                                                request.setHeader("Accept", "application/json");
                                                                                                request.setHeader("Content-type", "application/json");


                                                                                                String s_json = "";
                                                                                                StringEntity entity = null;
                                                                                                try {
                                                                                                    s_json = s.toString();

                                                                                                   /*
                                                                                                    s_json = s_json.replaceAll("'", "");
                                                                                                    s_json = s_json.replaceAll("\'", "");
                                                                                                    s_json = s_json.replaceAll("/?", "");
                                                                                                    s_json = s_json.replaceAll("/&", "");
                                                                                                     */


                                                                                                    Log.d("WebInvoke: json", s_json);

                                                                                                    entity = new StringEntity(s_json, "UTF-8");
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
                                                                                                    Log.d("WebInvoke", "KO : " + e.toString());
                                                                                                }
                                                                                                Log.d("WebInvoke", "OK : " + httpResponse.getStatusLine().toString());





                                                                                                //INIZIO RECUPERO LE TRACKS
/*
                                                                                                input.addProperty("User", spotifyUserObject.getId());
                                                                                                input.addProperty("Playlist", playlistItem.getID());
                                                                                                input.addProperty("Token", spotifyToken);
                                                                                                */
                                                                                                input.addProperty("Tracks",arr_to_spotify);
                                                                                                //2.A]Invoke ZUMO CUsom API to get playlist'infos
                                                                                                zumoClient.invokeApi(
                                                                                                        "api_pjk_spotify_update_playlist"
                                                                                                        , input
                                                                                                        , HttpPost.METHOD_NAME
                                                                                                        , null
                                                                                                        , new ApiJsonOperationCallback() {
                                                                                                            @Override
                                                                                                            public void onCompleted(JsonElement jsonData, Exception error, ServiceFilterResponse response) {
                                                                                                                String jsonApiTracksResponse = response.getContent().toString();
                                                                                                                Log.i("Paolo:api_pjk_spotify_update_playlist:Track", response.getContent().toString());// getAsJsonObject().getAsString());

                                                                                                            }
                                                                                                  });





























































                                                                                                Intent loadPlayActivityIntent = new Intent(getApplicationContext(), PlayActivity.class);


                                                                                                loadPlayActivityIntent.putExtra("SONGS", s_json);
                                                                                                startActivity(loadPlayActivityIntent);


                                                                                            } catch (JSONException e) {
                                                                                                e.printStackTrace();
                                                                                                Log.i("BBBBB", e.toString());
                                                                                            }
                                                                                        } else {
                                                                                            Log.e("Paolo:LoadPlayListsActivity:User", "Error Calling ZUMO API api_pjk_spotify_get_playlists");
                                                                                        }
                                                                                    }
                                                                                }
                                                                        );


                                                                        //FINE RECUPERO LE TRACKS


                                                                    }
                                                                });
                                                            } catch (JSONException e) {
                                                                e.printStackTrace();
                                                            }
                                                        } else {
                                                            Log.e("Paolo:LoadPlayListsActivity:User", "Error Calling ZUMO API api_pjk_spotify_get_playlists");
                                                        }
                                                    }
                                                }
                                        );

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
